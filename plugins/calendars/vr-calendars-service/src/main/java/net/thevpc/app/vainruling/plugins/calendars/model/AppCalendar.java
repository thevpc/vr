/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.calendars.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.model.AppUserType;
import net.thevpc.upa.config.Entity;
import net.thevpc.upa.config.Id;
import net.thevpc.upa.config.Main;
import net.thevpc.upa.config.Path;
import net.thevpc.upa.config.Property;
import net.thevpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "this.name")
@Path("/Repository/General")
public class AppCalendar {

    @Sequence
    @Id
    private int id;
    @Main
    private String code;
    @Main
    private String name;

    private AppUser owner;
    private AppUser readUserFilter;
    private AppUserType readUserTypeFilter;
    private String readProfileFilter;
    private String writeProfileFilter;
    private boolean publicCalendar;
    private transient boolean dynamic;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public AppUser getReadUserFilter() {
        return readUserFilter;
    }

    public void setReadUserFilter(AppUser readUserFilter) {
        this.readUserFilter = readUserFilter;
    }

    public AppUserType getReadUserTypeFilter() {
        return readUserTypeFilter;
    }

    public void setReadUserTypeFilter(AppUserType readUserTypeFilter) {
        this.readUserTypeFilter = readUserTypeFilter;
    }

    public String getReadProfileFilter() {
        return readProfileFilter;
    }

    public void setReadProfileFilter(String readProfileFilter) {
        this.readProfileFilter = readProfileFilter;
    }

    public String getWriteProfileFilter() {
        return writeProfileFilter;
    }

    public void setWriteProfileFilter(String writeProfileFilter) {
        this.writeProfileFilter = writeProfileFilter;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + this.id;
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
        final AppCalendar other = (AppCalendar) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.valueOf(getName());
    }

    public boolean isPublicCalendar() {
        return publicCalendar;
    }

    public void setPublicCalendar(boolean publicCalendar) {
        this.publicCalendar = publicCalendar;
    }

}
