package net.thevpc.app.vainruling.core.service.model;

import net.thevpc.upa.config.*;

/**
 * Created by vpc on 7/19/17.
 */
@Entity()
@Path("/Repository/General")
public class AppSkill {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    @Summary
    private AppSkill parent;

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

    public AppSkill getParent() {
        return parent;
    }

    public void setParent(AppSkill parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppSkill appSkill = (AppSkill) o;

        return id == appSkill.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
