/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling;

/**
 * @author taha.bensalah@gmail.com
 */
public interface VrEditorActionProcessor {

    default boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return true;
    }
}
