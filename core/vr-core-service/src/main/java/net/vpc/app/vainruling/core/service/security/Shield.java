/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

//import net.acs.dal.dto.PermissionDTO;
//import net.acs.service.core.Constants;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author dev01
 */
@Service
@Scope("session")
public class Shield implements Serializable {

    private Map<String, String> rightsMap = new HashMap<String, String>();

    public boolean allow(String rights) {
//        rightArray = rights.split("(,| )+");
////        SSOManagerFactory.getCurrent().
//        return true;
        String permissionDTO = null;
        if (rightsMap.containsKey(rights)) {
            permissionDTO = rightsMap.get(rights);
        }
        return (permissionDTO != null);
    }

    public boolean allowUpdate(String rights) {

        String permissionDTO = null;
        if (rightsMap.containsKey(rights)) {
            permissionDTO = rightsMap.get(rights);
        }
        return (permissionDTO != null);
    }

    public boolean allowURL(String url) {
//        if (url.contains(Constants.HOME_URL) || url.endsWith("/acs-web/")) {
//            return true;
//        }
//        for (Entry<String, PermissionDTO> permission : rightsMap.entrySet()) {
//            if (permission.getValue().getPages() != null) {
//                String[] pages = permission.getValue().getPages().split(",");
//                for (String page : pages) {
//                    if (url.contains(page)) {
//                        return true;
//                    }
//                }
//            }
//        }
        return false;
    }

    public boolean deny(String rights) {
        return true;
    }

    public Map<String, String> getRightsMap() {
        return rightsMap;
    }

    public void setRightsMap(Map<String, String> rightsMap) {
        this.rightsMap = rightsMap;
    }

}
