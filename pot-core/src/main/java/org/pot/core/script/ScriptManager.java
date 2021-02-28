package org.pot.core.script;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.core.util.FileUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class ScriptManager implements BeanFactoryAware {

  private static ScheduledExecutorService scheduledExecutorService = Executors
      .newScheduledThreadPool(2);
  private Map<String, ScriptEntry> allScriptFileMap = new ConcurrentHashMap<>();
  private Map<String, ScriptEntry> allScriptMap = new ConcurrentHashMap<>();
  private BeanFactory beanFactory;
  private ReadWriteLock lock = new ReentrantReadWriteLock();
  private Lock readLock = lock.readLock();
  private Lock writeLock = lock.writeLock();
  // 脚本源文件夹
  private String sourceDir;
  // 输出文件夹
  private String outDir;
  // 附加的jar包地址
  private String jarsDir = "";

  public ScriptManager() {
    String dir = System.getProperty("user.dir");
    String path =
            dir + File.separator + "src" + File.separator + "main"
            + File.separator
            + "groovy"
            + File.separator;
    String outPath =
        dir + File.separator + "target" + File.separator + "scriptsbin" + File.separator;
    String jarsDir = dir + File.separator + "target" + File.separator;
    setSource(path, outPath, jarsDir);
  }

  @Bean(name = "groovyClassLoader")
  public GroovyClassLoader groovyClassLoader() {
    return new GroovyClassLoader();
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @PostConstruct
  public void init() {
    scheduledExecutorService.scheduleAtFixedRate(() -> {
      try {
        List<File> sourceFileList = new ArrayList<>();
        FileUtil.getFiles(this.sourceDir, sourceFileList, ".groovy", null);
        sourceFileList.forEach(file -> {
          ScriptEntry scriptEntry = getScriptEntry(file.getPath());
          if (scriptEntry == null || scriptEntry.getLastModifiyTime() != file
              .lastModified()) {
            addScript(file);
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, 5000L, 5000L, TimeUnit.MILLISECONDS);
  }

  public Object removeScript(ScriptEntry scriptEntry) {
    try {
      Object inst = null;
      writeLock.lock();
      log.info("delete script:{}", scriptEntry.getName());
      allScriptMap.remove(scriptEntry.getPath());
      allScriptFileMap.remove(scriptEntry.getFilePath());
      return inst;
    } finally {
      writeLock.unlock();
    }
  }

  private Object addScript(File file) {
    try {
      writeLock.lock();
      ScriptEntry scriptEntry = parseScript(file);
      //注册 class
      Class<?> clazz = scriptEntry.getScriptClass();
      DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
      BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
      factory
          .registerBeanDefinition(scriptEntry.getName(), beanDefinitionBuilder.getBeanDefinition());
//      ServerContext.getApplicationContext().register(clazz);
      log.info("bean2:{}", factory.getBean(scriptEntry.getName()));
      allScriptFileMap.put(file.getPath(), scriptEntry);
      allScriptMap.put(scriptEntry.getPath(), scriptEntry);
//      Object inst = getScriptInstance(clazz);
      Object inst = factory.getBean(scriptEntry.getName());
      log.info("compileGroovy:{}", inst);
      return inst;
    } finally {
      writeLock.unlock();
    }
  }

  public final void setSource(String source, String out, String jarsDir) {
    if (StringUtils.isEmpty(source)) {
      log.error("指定 输入 输出 目录为空");
      return;
    }
    this.sourceDir = source;
    this.outDir = out;
    this.jarsDir = jarsDir;
    log.info("指定 输入{} 输出{} jarsDir{}", source, out, jarsDir);
  }

  public ScriptEntry getScriptEntry(String filePath) {
    try {
//            readLock.lock();
      return allScriptFileMap.get(filePath);
    } finally {
//            readLock.unlock();
    }
  }

  private ScriptEntry parseScript(File file) {
    try {
      GroovyCodeSource source = new GroovyCodeSource(file, "UTF-8");
      Class<?> clazz = groovyClassLoader().parseClass(source, false);
      Script script = clazz.getAnnotation(Script.class);
      if (script == null) {
        log.error("parse script error. {} must be @Script", clazz.getName());
        return null;
      }
      return new ScriptEntry(
          org.springframework.util.StringUtils.isEmpty(script.name()) ? clazz.getSimpleName()
              : script.name(),
          file.getPath(), file.lastModified(), clazz, source.getScriptText(), script.path());
    } catch (IOException e) {
      e.printStackTrace();
      log.error("load script ：" + file.getName());
    }
    return null;
  }

  public String loadGroovy(Consumer<String> condition) {
    this.compileGroovy();
    return "";
  }

  public void init(Consumer<String> condition) {
    loadGroovy(condition);
  }

  private void compileGroovy(List<File> sourceFileList) {
    if (CollectionUtils.isEmpty(sourceFileList)) {
      return;
    }
    sourceFileList.forEach(file -> {
      addScript(file);
    });
  }

  private void compileGroovy() {
    List<File> sourceFileList = new ArrayList<>();
    FileUtil.getFiles(this.sourceDir, sourceFileList, ".groovy", null);
    compileGroovy(sourceFileList);
  }

  private Object getScriptInstance(Class<?> clazz) {
    try {
      readLock.lock();
      Object instance = beanFactory.getBean(clazz);
      return instance;
    } catch (Exception e) {
      log.error("get " + clazz.getName() + " instance error.", e);
    } finally {
      readLock.unlock();
    }
    return null;
  }

  public Object getScriptByName(String scriptName) {
    try {
      readLock.lock();
      if (scriptName.contains("?")) {
        scriptName = scriptName.substring(0, scriptName.indexOf("?"));
      }
      ScriptEntry scriptEntry = allScriptMap.get(scriptName);
      if (scriptEntry == null) {
        return null;
      }
      return getScriptInstance(scriptEntry.getScriptClass());
    } finally {
      readLock.unlock();
    }
  }
}
