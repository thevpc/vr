package net.vpc.app.vainruling.core.service.stats;

/**
 * Created by vpc on 8/29/16.
 */
public interface KPIResultRow {
    public KPIGroup getGroup();

    public KPIValue[] getValues();

    public KPIValue getValue(int index);

    public KPIValue getValue(String name);
}
