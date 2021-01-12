package org.pot.core.script;

/**
 * @author 孙崇
 * @date 2016/7/11
 */
public class ScriptEntry {

    private String name;
    private long lastModifiyTime;
    private Class<?> scriptClass;
    private String sourceCode;
    private String filePath;

    private String path;

    public ScriptEntry(String name, String filePath, long lastModifiyTime, Class<?> scriptClass,
        String sourceCode, String path) {
        this.name = name;
        this.filePath = filePath;
        this.lastModifiyTime = lastModifiyTime;
        this.scriptClass = scriptClass;
        this.sourceCode = sourceCode;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getLastModifiyTime() {
        return lastModifiyTime;
    }

    public Class<?> getScriptClass() {
        return scriptClass;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScriptEntry that = (ScriptEntry) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
