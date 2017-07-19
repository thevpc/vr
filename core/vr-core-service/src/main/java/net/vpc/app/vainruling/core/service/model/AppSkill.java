package net.vpc.app.vainruling.core.service.model;

import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 * Created by vpc on 7/19/17.
 */
@Entity()
@Path("Admin/Config")
public class AppSkill {
    @Id
    @Sequence
    private int id;
    private String name;
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
}
