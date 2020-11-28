/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.core.model;

import java.util.Date;

import net.thevpc.app.vainruling.core.service.model.AppArea;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.upa.config.Entity;
import net.thevpc.upa.config.Field;
import net.thevpc.upa.config.Id;
import net.thevpc.upa.config.Main;
import net.thevpc.upa.config.Path;
import net.thevpc.upa.config.Properties;
import net.thevpc.upa.config.Property;
import net.thevpc.upa.config.Sequence;
import net.thevpc.upa.config.Summary;

/**
 *
 * @author vpc
 */
@Entity
@Path("Equipment")
public class EquipmentIssueLogList {

    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    @Summary
    private Date date;
    @Summary
    private AppUser operator;
    @Summary
    private AppArea location;
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    @Field(max = "400")
    private String observations;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AppUser getOperator() {
        return operator;
    }

    public void setOperator(AppUser operator) {
        this.operator = operator;
    }

    public AppArea getLocation() {
        return location;
    }

    public void setLocation(AppArea location) {
        this.location = location;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
