package net.vpc.app.vainruling.core.service.editor;

public class ActionParam {
    private String name;
    private ParamType type;
    private Object initialValue;

    public ActionParam(String name, ParamType type,Object initialValue) {
        this.name = name;
        this.type = type;
        this.initialValue = initialValue;
    }

    public Object getInitialValue() {
        return initialValue;
    }

    public String getName() {
        return name;
    }

    public ParamType getType() {
        return type;
    }
}
