/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.helper;

import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.helper.parsers.AppCivilityParser;
import net.vpc.app.vainruling.plugins.academic.service.helper.parsers.AppGenderParser;

/**
 *
 * @author vpc
 */
public class ImportTeacherContext {
    
    AppPeriod mainPeriod;
    AppCompany mainCompany;
    AppGenderParser genders = new AppGenderParser();
    AppCivilityParser civilities = new AppCivilityParser();

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
    
}
