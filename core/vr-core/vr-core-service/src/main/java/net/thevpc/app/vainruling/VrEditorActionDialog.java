package net.thevpc.app.vainruling;

import java.util.List;

public interface VrEditorActionDialog extends VrEditorActionProcessor {
    void openDialog(String actionId, List<String> itemIds) ;
}
