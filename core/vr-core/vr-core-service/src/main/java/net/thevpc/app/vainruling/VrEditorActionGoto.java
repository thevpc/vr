package net.thevpc.app.vainruling;

import java.util.List;

public interface VrEditorActionGoto extends VrEditorActionProcessor {
    String[] getCommand(String actionId, List<String> itemIds) ;
}
