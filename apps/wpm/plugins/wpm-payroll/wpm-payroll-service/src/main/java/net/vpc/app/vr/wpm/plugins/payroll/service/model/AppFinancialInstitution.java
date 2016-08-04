package net.vpc.app.vr.wpm.plugins.payroll.service.model;

import net.vpc.upa.config.*;

/**
 * Created by vpc on 6/13/16.
 */
@Entity
@Path("/Contact")
public class AppFinancialInstitution {
    @Id
    @Sequence
    private int id;
    @Main
    @Unique
    private String name;
    private AppFinancialInstitutionType institutionType;

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

    public AppFinancialInstitutionType getInstitutionType() {
        return institutionType;
    }

    public void setInstitutionType(AppFinancialInstitutionType institutionType) {
        this.institutionType = institutionType;
    }
}
