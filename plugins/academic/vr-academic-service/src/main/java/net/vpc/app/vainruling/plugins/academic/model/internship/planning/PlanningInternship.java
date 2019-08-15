package net.vpc.app.vainruling.plugins.academic.model.internship.planning;

import net.vpc.common.strings.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 5/26/16.
 */
public class PlanningInternship implements Comparable<PlanningInternship> {
    private int id;
    private String code;
    private String name;
    private String student;
    private String session;
    private String disciplines;
    private List<String> supervisors;

    public PlanningInternship() {
    }

    public PlanningInternship(int id, String code, String name, String student, String disciplines, String session, String... supervisors) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.student = student;
        this.disciplines = disciplines;
        this.session = session;
        this.supervisors = new ArrayList<>();
        if (supervisors != null) {
            for (String supervisor : supervisors) {
                if (!StringUtils.isBlank(supervisor)) {
                    this.supervisors.add(supervisor);
                }
            }
        }
    }


    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(String disciplines) {
        this.disciplines = disciplines;
    }

    public List<String> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<String> supervisors) {
        this.supervisors = supervisors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningInternship)) return false;

        PlanningInternship that = (PlanningInternship) o;

        if (id != that.id) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (student != null ? !student.equals(that.student) : that.student != null) return false;
        if (disciplines != null ? !disciplines.equals(that.disciplines) : that.disciplines != null) return false;
        return !(supervisors != null ? !supervisors.equals(that.supervisors) : that.supervisors != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (student != null ? student.hashCode() : 0);
        result = 31 * result + (disciplines != null ? disciplines.hashCode() : 0);
        result = 31 * result + (supervisors != null ? supervisors.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(PlanningInternship o) {
        int c = Integer.compare(getId(),o.getId());
        if (c != 0) {
            return 0;
        }
        return c;
//        c = getCode().compareTo(o.getCode());
//        if (c != 0) {
//            return 0;
//        }
//        c = getName().compareTo(o.getName());
//        if (c != 0) {
//            return 0;
//        }
//        return c;
    }

    public PlanningInternship copy() {
        return new PlanningInternship(
                id, code, name, student, disciplines, session, supervisors.toArray(new String[supervisors.size()])
        );
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return code;
    }
}
