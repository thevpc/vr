package net.vpc.app.vainruling.plugins.academic.internship.service.model.planning;

/**
 * Created by vpc on 5/19/16.
 */
public class PlanningSpaceTime implements Comparable<PlanningSpaceTime> {
    private PlanningRoom room;
    private PlanningTime time;

    public PlanningSpaceTime(PlanningRoom room, PlanningTime time) {
        this.room = room;
        this.time = time;
    }

    public PlanningRoom getRoom() {
        return room;
    }

    public PlanningTime getTime() {
        return time;
    }


    @Override
    public String toString() {
        return time + "-" + room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningSpaceTime)) return false;

        PlanningSpaceTime that = (PlanningSpaceTime) o;

        if (!room.equals(that.room)) return false;
        return time.equals(that.time);

    }

    @Override
    public int hashCode() {
        int result = room.hashCode();
        result = 31 * result + time.hashCode();
        return result;
    }

    @Override
    public int compareTo(PlanningSpaceTime o) {
        int compare = getTime().compareTo(o.getTime());
        if (compare == 0) {
            compare = getRoom().compareTo(o.getRoom());
        }
        return compare;
    }
}
