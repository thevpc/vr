/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.core.model;

import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowWorkflow;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Repository/Equipment")
public class EquipmentType {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    @Summary
    private EquipmentTypeGroup typeGroup;

    @Summary
    private EquipmentBorrowWorkflow borrowWorkflow;

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

    public EquipmentTypeGroup getTypeGroup() {
        return typeGroup;
    }

    public void setTypeGroup(EquipmentTypeGroup typeGroup) {
        this.typeGroup = typeGroup;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EquipmentType other = (EquipmentType) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    public EquipmentBorrowWorkflow getBorrowWorkflow() {
        return borrowWorkflow;
    }

    public void setBorrowWorkflow(EquipmentBorrowWorkflow borrowWorkflow) {
        this.borrowWorkflow = borrowWorkflow;
    }

}
