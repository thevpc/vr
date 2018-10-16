/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.helper.parsers;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppCivility;
import net.vpc.app.vainruling.core.service.model.AppGender;

/**
 *
 * @author vpc
 */
public class AppCivilityParser {

    private Map<String, AppCivility> civilityByName;
    private Map<Integer, AppCivility> civilityById;

    private void prepare() {
        if (civilityById == null) {
            civilityByName = new HashMap<>();
            civilityById = new HashMap<>();
            for (AppCivility g : CorePlugin.get().findCivilities()) {
                civilityByName.put(g.getName().toUpperCase(), g);
                civilityById.put(g.getId(), g);
            }
        }
    }

    public AppCivility resolveCivility(Integer civilityId, String civilityName, AppGender gender, boolean required) {
        prepare();
        AppCivility civility = null;
        if (civilityId != null) {
            civility = civilityById.get(civilityId);
            if (civility == null && required) {
                throw new NoSuchElementException("Civility Not Found " + civilityId);
            }
            return civility;
        } else if (civilityName != null) {
            civility = civilityByName.get(civilityName.toUpperCase().trim());
        }
        if (gender != null && gender.getCode().equals("F")) {
            civility = civilityByName.get("MLLE");
        } else {
            civility = civilityByName.get("M.");
        }
        if (civility == null && required) {
            throw new NoSuchElementException("Civility Not Found" + (civilityName==null?"":(" "+civilityName)));
        }
        return civility;
    }
}
