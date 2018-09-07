package net.vpc.app.vainruling.core.service.obj;

import net.vpc.upa.NamedId;

/**
 * Created by vpc on 8/20/16.
 */
public class AutoFilterData implements Comparable<AutoFilterData> {

    private String filterType;
    private String formatType;
    private String entityName;
    private String name;
    private String label;
    private String expr;
    private String type;
    private String initial;
    private NamedId defaultSelectedValue;
    private int order;

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public NamedId getDefaultSelectedValue() {
        return defaultSelectedValue;
    }

    public void setDefaultSelectedValue(NamedId defaultSelectedValue) {
        this.defaultSelectedValue = defaultSelectedValue;
    }

    @Override
    public int compareTo(AutoFilterData o) {
        int x = Integer.compare(order, o.order);
        if (x != 0) {
            return x;
        }
        x = name.compareTo(o.name);
        if (x != 0) {
            return x;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AutoFilterData that = (AutoFilterData) o;

        if (order != that.order) {
            return false;
        }
        if (filterType != null ? !filterType.equals(that.filterType) : that.filterType != null) {
            return false;
        }
        if (entityName != null ? !entityName.equals(that.entityName) : that.entityName != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (label != null ? !label.equals(that.label) : that.label != null) {
            return false;
        }
        if (expr != null ? !expr.equals(that.expr) : that.expr != null) {
            return false;
        }
        return type != null ? type.equals(that.type) : that.type == null;

    }

    @Override
    public int hashCode() {
        int result = filterType != null ? filterType.hashCode() : 0;
        result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (expr != null ? expr.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + order;
        return result;
    }
}
