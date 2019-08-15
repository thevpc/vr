package net.vpc.app.vainruling.plugins.academic.service.tools.pfe;

import net.vpc.app.vainruling.plugins.academic.model.internship.planning.PlanningActivity;

import java.util.Objects;

public class ResourceAllocation {
    String allocationId;
    String type;
    String desc;
    PlanningActivity a;

    public ResourceAllocation(String allocationId, String type, PlanningActivity a, String desc) {
        this.allocationId = allocationId;
        this.type = type;
        this.desc = desc;
        this.a = a;
    }

    public void checkConflicts(ResourceAllocation other) {
        if (allocationId.equals(other.allocationId)) {
            throw new IllegalArgumentException("Allocation Failed. Conflict between " + desc + " and " + other.desc);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceAllocation that = (ResourceAllocation) o;
        return Objects.equals(allocationId, that.allocationId) &&
                Objects.equals(type, that.type) &&
                Objects.equals(desc, that.desc) &&
                Objects.equals(a, that.a);
    }

    @Override
    public int hashCode() {

        return Objects.hash(allocationId, type, desc, a);
    }
}
