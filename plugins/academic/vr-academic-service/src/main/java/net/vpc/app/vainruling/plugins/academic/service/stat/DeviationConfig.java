package net.vpc.app.vainruling.plugins.academic.service.stat;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 8/19/16.
 */
public class DeviationConfig {
    private Set<DeviationGroup> groups=new HashSet<>();
    private boolean weekBased=true;
    private boolean extraBased =true;

    public Set<DeviationGroup> getGroups() {
        return groups;
    }

    public DeviationConfig setGroups(Set<DeviationGroup> groups) {
        this.groups = groups;
        return this;
    }

    public boolean isWeekBased() {
        return weekBased;
    }

    public DeviationConfig setWeekBased(boolean weekBased) {
        this.weekBased = weekBased;
        return this;
    }

    public boolean isExtraBased() {
        return extraBased;
    }

    public DeviationConfig setExtraBased(boolean extraBased) {
        this.extraBased = extraBased;
        return this;
    }
}
