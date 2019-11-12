/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.editor;

import java.util.Map;
import net.vpc.app.vainruling.VrEditorSearch;
import net.vpc.app.vainruling.core.service.util.VrUtils;

/**
 * added to fix problem with 
 * "Property not found on type” when using interface default methods in JSP EL"
 * @author vpc
 */
public abstract class VrEditorSearchBase implements VrEditorSearch {

    @Override
    public String getId() {
        return VrUtils.getBeanName(this);
    }

    @Override
    public String getTitle() {
        return getName();
    }

    @Override
    public String createHelperString(String name, String entityName) {
        return "Tapez ici les mots clés de recherche.";
    }

    @Override
    public String createPreProcessingExpression(String entityName, Map<String, Object> parameters, String paramPrefix) {
        return null;
    }

}
