/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

/**
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Equipment")
public class EquipmentProperty {

    @Id
    @Sequence
    private int id;

    @ManyToOne(type = RelationshipType.COMPOSITION)
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Equipment equipment;

    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;

    @Field(max = "400", modifiers = UserFieldModifier.SUMMARY)
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

}
