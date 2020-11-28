package net.thevpc.app.vainruling.plugins.academic.model.internship.planning;

/**
 * Created by vpc on 5/19/16.
 */
public class PlanningRoom implements Comparable<PlanningRoom> {
    private String name;
    private int index;

    public PlanningRoom(String name) {
        this(name,0);
    }

    public PlanningRoom(PlanningRoom other) {
        this.name=other.name;
        this.index=other.index;
    }

    public PlanningRoom(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public double distance(PlanningRoom other) {
        return Math.abs(this.getIndex() - other.getIndex());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningRoom)) return false;

        PlanningRoom that = (PlanningRoom) o;

        if (index != that.index) return false;
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + index;
        return result;
    }

    @Override
    public int compareTo(PlanningRoom o) {
        int compare = Integer.compare(getIndex(), o.getIndex());
        if (compare == 0) {
            compare = getName().compareTo(o.getName());
        }
        return compare;
    }
}
