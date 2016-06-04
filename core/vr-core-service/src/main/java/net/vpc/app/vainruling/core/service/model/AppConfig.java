/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "mainPeriod.name desc")
@Path("Admin/Config")
public class AppConfig {

    @Id
    private int id=1;
    @Field(modifiers = {UserFieldModifier.MAIN})
    private AppPeriod mainPeriod;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AppCompany mainCompany;

    public AppConfig() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppPeriod getMainPeriod() {
        return mainPeriod;
    }

    public void setMainPeriod(AppPeriod mainPeriod) {
        this.mainPeriod = mainPeriod;
    }

    public AppCompany getMainCompany() {
        return mainCompany;
    }

    public void setMainCompany(AppCompany mainCompany) {
        this.mainCompany = mainCompany;
    }

}
