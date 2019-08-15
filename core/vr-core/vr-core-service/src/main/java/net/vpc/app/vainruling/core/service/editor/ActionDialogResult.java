package net.vpc.app.vainruling.core.service.editor;

public class ActionDialogResult {
    public static final ActionDialogResult VOID=new ActionDialogResult(ActionDialogResultPostProcess.VOID);
    private String message;
    private ActionDialogResultPostProcess type;

    public ActionDialogResult(String message, ActionDialogResultPostProcess type) {
        this.message = message;
        this.type = type;
    }

    public ActionDialogResult(ActionDialogResultPostProcess type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public ActionDialogResultPostProcess getType() {
        return type;
    }
}
