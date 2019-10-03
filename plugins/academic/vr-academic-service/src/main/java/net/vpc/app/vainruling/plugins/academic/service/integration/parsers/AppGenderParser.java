/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.integration.parsers;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppGender;
import net.vpc.app.vainruling.core.service.util.VrUtils;

/**
 *
 * @author vpc
 */
public class AppGenderParser {

    private Map<String, AppGender> gendersByCode;
    private Map<String, AppGender> gendersByName;
    private Map<Integer, AppGender> gendersById;

    private void prepare() {
        if (gendersById == null) {
            gendersByCode = new HashMap<>();
            gendersByName = new HashMap<>();
            gendersById = new HashMap<>();
            for (AppGender g : CorePlugin.get().findGenders()) {
                gendersByCode.put(g.getCode(), g);
                gendersByName.put(VrUtils.normalizeName(g.getName()), g);
                    for (String v : VrUtils.parseNormalizedOtherNames(g.getOtherNames())) {
                        gendersByName.put(v, g);
                    }
            }
        }
    }

    public AppGender resolveGender(Integer genderId, String genderName, boolean required) {
        prepare();
        AppGender gender = null;
        if (genderId != null) {
            gender = gendersById.get(genderId);
            if (gender == null && required) {
                throw new NoSuchElementException("Gender Not Found " + genderId);
            }
            return gender;
        } else if (genderName != null) {
            gender = gendersByCode.get(genderName);
            if (gender == null) {
                gender = gendersByName.get(VrUtils.normalizeName(genderName));
            }
            if (gender == null && required) {
                throw new NoSuchElementException("Gender Not Found " + genderName);
            }
            return gender;
        }
        if (required) {
            throw new NoSuchElementException("Gender Not Found");
        }
        return null;
    }
}
