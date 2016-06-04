package net.vpc.app.vainruling.plugins.academic.internship.service.model.planning;

import net.vpc.common.strings.StringUtils;

import java.util.*;

/**
 * Created by vpc on 5/19/16.
 */
public class PlanningActivity implements Comparable<PlanningActivity>,Cloneable{
    private PlanningInternship internship;
    private PlanningSpaceTime spaceTime;
    private String examiner;
    private String chair;
    private boolean fixedExaminer;
    private boolean fixedChair;
    private boolean fixedTime;
    private boolean fixedSpace;


    public PlanningActivity(PlanningInternship internship) {
        this.internship = internship;
    }

    public PlanningActivity() {
    }

    public PlanningActivity copy(){
        try {
            return (PlanningActivity) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public PlanningInternship getInternship() {
        return internship;
    }

    public PlanningActivity setInternship(PlanningInternship internship) {
        this.internship = internship;
        return this;
    }

    public PlanningSpaceTime getSpaceTime() {
        return spaceTime;
    }

    public PlanningRoom getRoom() {
        return getSpaceTime().getRoom();
    }


    public PlanningTime getTime() {
        PlanningSpaceTime t = getSpaceTime();
        return t==null?null:t.getTime();
    }

    public PlanningActivity setSpaceTime(PlanningSpaceTime spaceTime) {
        this.spaceTime = spaceTime;
        return this;
    }

    public String getExaminer() {
        return examiner;
    }

    public boolean isExaminer(String examiner) {
        return (Objects.equals(getExaminer(),examiner));
    }

    public boolean isChair(String chair) {
        return (Objects.equals(getChair(), chair));
    }

    public boolean isSupervisor(String supervisor) {
        if(getInternship().getSupervisors()!=null){
            for (String s : getInternship().getSupervisors()) {
                if((Objects.equals(s, supervisor))){
                    return true;
                }
            }
        }
        return false;
    }

    public PlanningActivity setExaminer(String examiner) {
        this.examiner = examiner;
        return this;
    }

    public String getChair() {
        return chair;
    }

    public PlanningActivity setChair(String chair) {
        this.chair = chair;
        return this;
    }

    private int compare(PlanningSpaceTime o1, PlanningSpaceTime o2) {
        if(o1==null && o2==null){
            return 0;
        }
        if(o1==null){
            return -1;
        }
        if(o2==null){
            return 1;
        }
        return o1.compareTo(o2);
    }
//    private int compare(PlanningTime o1, PlanningTime o2) {
//        if(o1==null && o2==null){
//            return 0;
//        }
//        if(o1==null){
//            return -1;
//        }
//        if(o2==null){
//            return 1;
//        }
//        return o1.compareTo(o2);
//    }
//    private int compare(PlanningRoom o1, PlanningRoom o2) {
//        if(o1==null && o2==null){
//            return 0;
//        }
//        if(o1==null){
//            return -1;
//        }
//        if(o2==null){
//            return 1;
//        }
//        return o1.compareTo(o2);
//    }

    @Override
    public int compareTo(PlanningActivity o2) {

        int c = compare(this.getSpaceTime(), o2.getSpaceTime());
        if (c == 0) {
            c = this.getInternship().compareTo(o2.getInternship());
        }
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningActivity)) return false;

        PlanningActivity that = (PlanningActivity) o;

        if (fixedExaminer != that.fixedExaminer) return false;
        if (fixedChair != that.fixedChair) return false;
        if (fixedTime != that.fixedTime) return false;
        if (fixedSpace != that.fixedSpace) return false;
        if (internship != null ? !internship.equals(that.internship) : that.internship != null) return false;
        if (spaceTime != null ? !spaceTime.equals(that.spaceTime) : that.spaceTime != null) return false;
        if (examiner != null ? !examiner.equals(that.examiner) : that.examiner != null) return false;
        return !(chair != null ? !chair.equals(that.chair) : that.chair != null);

    }

    @Override
    public int hashCode() {
        int result = internship != null ? internship.hashCode() : 0;
        result = 31 * result + (spaceTime != null ? spaceTime.hashCode() : 0);
        result = 31 * result + (examiner != null ? examiner.hashCode() : 0);
        result = 31 * result + (chair != null ? chair.hashCode() : 0);
        result = 31 * result + (fixedExaminer ? 1 : 0);
        result = 31 * result + (fixedChair ? 1 : 0);
        result = 31 * result + (fixedTime ? 1 : 0);
        result = 31 * result + (fixedSpace ? 1 : 0);
        return result;
    }

    public List<String> getAllTeachers(){
        TreeSet<String> persons = new TreeSet<>();
        if (!StringUtils.isEmpty(getChair())) {
            persons.add(getChair());
        }
        if (!StringUtils.isEmpty(getExaminer())) {
            persons.add(getExaminer());
        }
        if (getInternship().getSupervisors() != null) {
            for (String sp : getInternship().getSupervisors()) {
                if (!StringUtils.isEmpty(sp)) {
                    persons.add(sp);
                }
            }
        }
        return new ArrayList<>(persons);
    }

    public boolean isFixedExaminer() {
        return fixedExaminer;
    }

    public void setFixedExaminer(boolean fixedExaminer) {
        this.fixedExaminer = fixedExaminer;
    }

    public boolean isFixedChair() {
        return fixedChair;
    }

    public void setFixedChair(boolean fixedChair) {
        this.fixedChair = fixedChair;
    }

    public boolean isFixedTime() {
        return fixedTime;
    }

    public void setFixedTime(boolean fixedTime) {
        this.fixedTime = fixedTime;
    }

    public boolean isFixedSpace() {
        return fixedSpace;
    }

    public void setFixedSpace(boolean fixedSpace) {
        this.fixedSpace = fixedSpace;
    }
}
