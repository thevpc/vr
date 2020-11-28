/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.thevpc.app.vainruling.core.service.editor.CustomTarget;

/**
 * @author taha.bensalah@gmail.com
 */
public interface CustomPropertyViewFactory extends PropertyViewFactory {
    /**
     * @return
     */
    CustomTarget getTarget();
}
