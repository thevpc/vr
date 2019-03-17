package net.vpc.app.vr.core.toolbox;

public class ProjectProperty {
    private String key;
    private String title;
    private String value;
    private String defaultValue;
    private StringValidator validator;
    private ProjectConfig config;

    public ProjectProperty(String key, String title, String value, String defaultValue, StringValidator validator, ProjectConfig config) {
        this.key = key;
        this.title = title;
        this.value = value;
        this.defaultValue = defaultValue;
        this.validator = validator;
        this.config = config;
    }

    public ProjectProperty setTitle(String title) {
        this.title = title;
        return this;
    }

    public ProjectProperty setValidator(StringValidator validator) {
        this.validator = validator;
        return this;
    }

    public ProjectProperty setValue(String value) {
        this.value = value;
        for (ProjectConfigListener listener : config.listeners) {
            listener.onSetProperty(key, value);
        }
        return this;
    }

    public ProjectProperty setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public StringValidator getValidator() {
        return validator;
    }

    public String get() {
        return get(defaultValue,validator);
    }

    public String get(String defaultValue,StringValidator validator) {
        String x = getValue();
        if (x != null) {
            return x;
        }
        String o = config.getConsole().ask(getKey(), getTitle(), validator, defaultValue);
        if (o != null) {
            setValue(o);
        }
        return o;
    }
    public boolean getBoolean(boolean defaultValue) {
        String s = get(String.valueOf(defaultValue), ValidatorFactory.BOOLEAN);
        return "true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s);
    }
}
