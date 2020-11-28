/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.core.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.upa.config.Entity;
import net.thevpc.upa.config.Field;
import net.thevpc.upa.config.Id;
import net.thevpc.upa.config.Main;
import net.thevpc.upa.config.Path;
import net.thevpc.upa.config.Properties;
import net.thevpc.upa.config.Property;
import net.thevpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity
@Path("Repository/Equipment")
public class EquipmentIssueSeverity {
    @Id @Sequence
    private int id;
    @Main
    private String name;
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    @Field(max = "400")
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
