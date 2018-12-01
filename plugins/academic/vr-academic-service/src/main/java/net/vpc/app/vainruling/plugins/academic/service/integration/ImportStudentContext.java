/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.integration;

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
    
    private AppGenderParser genders = new AppGenderParser();
    private AppCivilityParser civilities = new AppCivilityParser();
    private Map<String, AppProfile> profiles;
    private AppCompany mainCompany;
    private AppPeriod mainPeriod;
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

    public AppGenderParser getGenders() {
        return genders;
    }

    public ImportStudentContext setGenders(AppGenderParser genders) {
        this.genders = genders;
        return this;
    }

    public AppCivilityParser getCivilities() {
        return civilities;
    }

    public ImportStudentContext setCivilities(AppCivilityParser civilities) {
        this.civilities = civilities;
        return this;
    }
}
