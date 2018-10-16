/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.helper;

import java.util.Map;
import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.plugins.academic.service.helper.parsers.AppCivilityParser;
import net.vpc.app.vainruling.plugins.academic.service.helper.parsers.AppGenderParser;

/**
 *
 * @author vpc
 */
public class ImportStudentContext {
    
    AppGenderParser genders = new AppGenderParser();
    AppCivilityParser civilities = new AppCivilityParser();
    Map<String, AppProfile> profiles;
    AppCompany mainCompany;
    AppPeriod mainPeriod;
    private boolean simulate;

    public Map<String, AppProfile> getProfiles() {
        return profiles;
    }

    public ImportStudentContext setProfiles(Map<String, AppProfile> profiles) {
        this.profiles = profiles;
        return this;
    }

    public AppCompany getMainCompany() {
        return mainCompany;
    }

    public ImportStudentContext setMainCompany(AppCompany mainCompany) {
        this.mainCompany = mainCompany;
        return this;
    }

    public AppPeriod getMainPeriod() {
        return mainPeriod;
    }

    public ImportStudentContext setMainPeriod(AppPeriod mainPeriod) {
        this.mainPeriod = mainPeriod;
        return this;
    }

    public boolean isSimulate() {
        return simulate;
    }

    public void setSimulate(boolean simulate) {
        this.simulate = simulate;
    }
    
}
