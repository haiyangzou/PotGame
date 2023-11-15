package org.pot.dal.dao;

import javassist.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@SuppressWarnings("rawtypes")
public class EntityMapperBuilder<E> {
    private static final Map<Class, Class<EntityMapper>> mapperClassMap = new ConcurrentHashMap<>();
    private final Class<E> entityClass;
    private final Class<EntityMapper> entityMapperClass;

    public EntityMapperBuilder(Class<E> entityClass) {
        this.entityClass = entityClass;
        this.entityMapperClass = mapperClassMap.computeIfAbsent(entityClass, EntityMapperBuilder::makeEntityMapperClass);
        if (entityMapperClass == null) {
            throw new IllegalArgumentException("No Table annotation found of {}" + entityClass.getName());
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<EntityMapper> makeEntityMapperClass(Class entityClass) {
        try {
            TableMetas.TableMeta tableMeta = TableMetas.of(entityClass);
            if (tableMeta == null) {
                return null;
            }
            TableMetas.ColumnMeta[] column = tableMeta.getColumn();
            String className = "Generated" + entityClass.getSimpleName() + "Mapper";
            ClassPool classPool = ClassPool.getDefault();
            Class<?> superClass = EntityMapper.class;
            CtClass superCtClass = classPool.get(superClass.getName());
            CtClass ctClass = classPool.makeClass(className, superCtClass);
            CtConstructor ctConstructor = CtNewConstructor.make("public " + className + "(Class entityClass) {\nsuper(entityClass);\n}", ctClass);
            ctClass.addConstructor(ctConstructor);
            CtMethod setParamMethod = makeSetParamMethod(classPool, ctClass, entityClass, column);
            ctClass.addMethod(setParamMethod);
            CtMethod setParamBridgeMethod = makeSetParamBridgeMethod(ctClass, entityClass);
            ctClass.addMethod(setParamBridgeMethod);
            CtMethod setPrimaryKeyParamMethod = makeSetParamMethod(classPool, ctClass, entityClass, column, true);
            ctClass.addMethod(setPrimaryKeyParamMethod);
            CtMethod setPrimaryKeyBridgeMethod = makeSetPrimaryKeyBridgeMethod(ctClass, entityClass, "setPrimaryKeyParam");
            ctClass.addMethod(setPrimaryKeyBridgeMethod);
            CtMethod setNonPrimaryKeyParamMethod = makeSetParamMethod(classPool, ctClass, entityClass, column, false);
            ctClass.addMethod(setNonPrimaryKeyParamMethod);
            CtMethod setNonPrimaryKeyParam = makeSetPrimaryKeyBridgeMethod(ctClass, entityClass, "setNonPrimaryKeyParam");
            ctClass.addMethod(setNonPrimaryKeyParam);
            CtMethod parseMethod = makeParseMethod(classPool, ctClass, entityClass, column);
            ctClass.addMethod(parseMethod);
            Class<?> entityMapperClass = ctClass.toClass();
            return (Class<EntityMapper>) entityMapperClass;
        } catch (Throwable ex) {
            log.error("failed to generate EntityMapper of{}", entityClass, ex);
        }
        return null;
    }

    private static CtMethod makeSetParamBridgeMethod(CtClass ctClass, Class entityClass) throws CannotCompileException {
        String bodyBuilder = "public void setParam(java.sql.PreparedStatement statement, Object entity, int index) throws java.sql.SQLException {\n" +
                "setParam(statement, (" + entityClass.getName() + ")entity, index);\n" +
                "}";
        return CtNewMethod.make(bodyBuilder, ctClass);
    }

    private static CtMethod makeSetParamMethod(ClassPool classPool, CtClass ctClass, Class entityClass, TableMetas.ColumnMeta[] column, boolean pk) throws CannotCompileException, NotFoundException {
        StringBuilder bodyBuilder = new StringBuilder();
        if (pk) {
            bodyBuilder.append("public void setPrimaryKeyParam(java.sql.PreparedStatement statement, int index, ").append(entityClass.getName()).append(" entity) throws java.sql.SQLException {\n");
        } else {
            bodyBuilder.append("public void setNonPrimaryKeyParam(java.sql.PreparedStatement statement, int index, ").append(entityClass.getName()).append(" entity) throws java.sql.SQLException {\n");
        }
        for (int i = 0; i < column.length; i++) {
            appendSetParamStatement(classPool, bodyBuilder, i, entityClass, column[i].getPropertyName(), pk);
        }
        bodyBuilder.append("}");
        return CtNewMethod.make(bodyBuilder.toString(), ctClass);
    }

    private static CtMethod makeSetParamMethod(ClassPool classPool, CtClass ctClass, Class entityClass, TableMetas.ColumnMeta[] column) throws CannotCompileException, NotFoundException {
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("public void setParam(java.sql.PreparedStatement statement, ").append(entityClass.getName()).append(" entity, int index) throws java.sql.SQLException {\n");
        for (int i = 0; i < column.length; i++) {
            appendSetParamStatement(classPool, bodyBuilder, i, entityClass, column[i].getPropertyName(), false);
        }
        bodyBuilder.append("}");
        return CtNewMethod.make(bodyBuilder.toString(), ctClass);
    }

    private static void appendSetParamStatement(ClassPool classPool, StringBuilder bodyBuilder, int i, Class entityClass, String propertyName, boolean nonNull) throws NotFoundException {
        Method getter = getGetter(entityClass, propertyName);
        if (getter != null) {
            Class<?> returnType = getter.getReturnType();
            bodyBuilder.append("try {\n");
            if (returnType.isPrimitive()) {
                CtPrimitiveType primitiveType = (CtPrimitiveType) classPool.get(returnType.getName());
                String wrapperClassName = primitiveType.getWrapperName();
                wrapperClassName = wrapperClassName.substring(wrapperClassName.lastIndexOf('.') + 1);
                bodyBuilder.append("typeHandler[").append(i).append("].set(statement, index++, ").append(wrapperClassName).append(".valueOf(entity.").append(getter.getName()).append("()));\n");
            } else {
                if (nonNull) {
                    bodyBuilder.append("java.util.Objects.requireNonNull(").append("entity.").append(getter.getName()).append("(), \"").append(propertyName).append(" should not be null\");\n");
                }
                bodyBuilder.append("typeHandler[").append(i).append("].set(statement, index++, entity.").append(getter.getName()).append("());\n");
            }
            bodyBuilder.append("} catch(Throwable cause) {\n logger.error(\"failed to set param ").append(entityClass.getName()).append(".").append(propertyName).append("\", cause);\n}\n");
        } else {
            log.error("no getter found of {}.{}", entityClass.getName(), propertyName);
        }
    }

    private static CtMethod makeSetPrimaryKeyBridgeMethod(CtClass ctClass, Class entityClass, String methodName) throws CannotCompileException {
        String bodyBuilder = "public void " + methodName + "(java.sql.PreparedStatement statement, int firstIndex, Object entity) throws java.sql.SQLException {\n" +
                methodName + "(statement, firstIndex, (" + entityClass.getName() + ")entity);\n" +
                "}";
        return CtNewMethod.make(bodyBuilder, ctClass);
    }

    private static CtMethod makeParseMethod(ClassPool classPool, CtClass ctClass, Class entityClass, TableMetas.ColumnMeta[] column) throws CannotCompileException, NotFoundException {
        StringBuilder bodyBuilder = new StringBuilder();
        String entityName = entityClass.getName();
        bodyBuilder.append("public Object parse(java.sql.ResultSet resultSet) throws java.sql.SQLException {\n");
        bodyBuilder.append(entityName).append(" entity = new ").append(entityName).append("();\n");
        for (int i = 0; i < column.length; i++) {
            String propertyName = column[i].getPropertyName();
            String columnName = column[i].getColumnName();
            Method setter = getSetter(entityClass, propertyName);
            if (setter != null) {
                Parameter parameter = setter.getParameters()[0];
                Class<?> paramType = parameter.getType();
                bodyBuilder.append("try {\n");
                if (paramType.isPrimitive()) {
                    CtPrimitiveType primitiveType = (CtPrimitiveType) classPool.get(paramType.getName());
                    if (primitiveType == CtClass.byteType || primitiveType == CtClass.shortType || primitiveType == CtClass.intType) {
                        bodyBuilder.append("entity.").append(setter.getName()).append("(((Number)(typeHandler[").append(i).append("].get(resultSet, \"").append(columnName).append("\"))).")
                                .append(paramType.getName()).append("Value());\n");
                    } else {
                        String wrapperClassName = primitiveType.getWrapperName();
                        wrapperClassName = wrapperClassName.substring(wrapperClassName.lastIndexOf('.') + 1);
                        bodyBuilder.append("entity.").append(setter.getName()).append("(((").append(wrapperClassName).append(")(typeHandler[").append(i).append("].get(resultSet, \"").append(columnName).append("\"))).")
                                .append(paramType.getName()).append("Value());\n");
                    }
                } else {
                    String paramTypeName = paramType.getName();
                    if (paramType.isArray()) {
                        CtClass paramCtClass = classPool.getCtClass(paramType.getName());
                        paramTypeName = paramCtClass.getName();
                    }
                    bodyBuilder.append("entity.").append(setter.getName()).append("((").append(paramTypeName).append(")(typeHandler[)").append(i).append("].get(resultSet, \")").append(columnName).append("\")));\n");
                }
                bodyBuilder.append("} catch(Throwable cause) {\n logger.error(\"failed to parse ").append(entityName).append(".").append(propertyName).append("\", cause);\n}\n");
            } else {
                log.error("no setter found of{}.{}", entityClass.getName(), propertyName);
            }
        }
        bodyBuilder.append("return entity;\n");
        bodyBuilder.append("}");
        return CtNewMethod.make(bodyBuilder.toString(), ctClass);
    }

    private static Method getGetter(Class entityClass, String propertyName) {
        String get = "get" + propertyName;
        String is = "is" + propertyName;
        Method[] method = entityClass.getMethods();
        for (Method m : method) {
            if (m.getParameters().length != 0) {
                continue;
            }
            if (get.equalsIgnoreCase(m.getName())) {
                return m;
            }
            Class<?> returnType = m.getReturnType();
            if (returnType == boolean.class || returnType == Boolean.class) {
                if (is.equalsIgnoreCase(m.getName())) {
                    return m;
                }
            }
        }
        return null;
    }

    private static Method getSetter(Class entityClass, String propertyName) {
        String setterName = "set" + propertyName;
        Method[] method = entityClass.getMethods();
        for (Method m : method) {
            if (m.getParameters().length != 1) {
                continue;
            }
            if (setterName.equalsIgnoreCase(m.getName())) {
                return m;
            }
        }
        return null;
    }

    public static <E> EntityMapperBuilder<E> builder(Class<E> entityClass) {
        return new EntityMapperBuilder<>(entityClass);
    }

    @SuppressWarnings("unchecked")
    public EntityMapper<E> build() {
        try {
            Constructor<?> constructor = entityMapperClass.getConstructor(Class.class);
            return (EntityMapper<E>) constructor.newInstance(entityClass);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
