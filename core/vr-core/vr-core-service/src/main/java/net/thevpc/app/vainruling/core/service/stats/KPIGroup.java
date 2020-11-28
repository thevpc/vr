package net.thevpc.app.vainruling.core.service.stats;

/**
 * Created by vpc on 8/29/16.
 */
public interface KPIGroup extends Comparable<KPIGroup> {
    public String getName();

    public boolean equals(KPIGroup other);

    public int hashCode();
}
