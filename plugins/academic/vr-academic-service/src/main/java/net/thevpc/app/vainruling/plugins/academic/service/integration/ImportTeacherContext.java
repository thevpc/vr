/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.integration;

import net.thevpc.app.vainruling.plugins.academic.service.integration.parsers.AppCivilityParser;
import net.thevpc.app.vainruling.plugins.academic.service.integration.parsers.AppGenderParser;
import net.thevpc.app.vainruling.core.service.model.AppCompany;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;

/**
 *
 * @author vpc
 */
public class ImportTeacherContext {
    
    private AppPeriod mainPeriod;
    private AppCompany mainCompany;
    private AppGenderParser genders = new AppGenderParser();
    private AppCivilityParser civilities = new AppCivilityParser();

    public AppPeriod getMainPeriod() {
        return mainPeriod;
    }

    public ImportTeacherContext setMainPeriod(AppPeriod mainPeriod) {
        this.mainPeriod = mainPeriod;
        return this;
    }

    public AppCompany getMainCompany() {
        return mainCompany;
    }

    public ImportTeacherContext setMainCompany(AppCompany mainCompany) {
        this.mainCompany = mainCompany;
        return this;
    }

    public AppGenderParser getGenders() {
        return genders;
    }

    public ImportTeacherContext setGenders(AppGenderParser genders) {
        this.genders = genders;
        return this;
    }

    public AppCivilityParser getCivilities() {
        return civilities;
    }

    public ImportTeacherContext setCivilities(AppCivilityParser civilities) {
        this.civilities = civilities;
        return this;
    }
}
