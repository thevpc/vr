package net.thevpc.app.vainruling.plugins.academic.model.internship.planning;

/**
 * Created by vpc on 5/20/16.
 */
class TeacherTime {
    private String teacher;
    private PlanningTime time;

    public TeacherTime(String teacher, PlanningTime time) {
        this.teacher = teacher;
        this.time = time;
    }

    public String getTeacher() {
        return teacher;
    }

    public PlanningTime getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeacherTime)) return false;

        TeacherTime that = (TeacherTime) o;

        if (teacher != null ? !teacher.equals(that.teacher) : that.teacher != null) return false;
        return !(time != null ? !time.equals(that.time) : that.time != null);

    }

    @Override
    public int hashCode() {
        int result = teacher != null ? teacher.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
