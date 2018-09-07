package net.vpc.app.vainruling.plugins.academic.service.tools.pfe;

import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.PlanningActivityTable;

import java.util.*;

class ResourceAllocationList {
    private Map<String, ResourceAllocation> all = new HashMap<>();
    private Map<String, TeacherInfo> teachers = new HashMap<>();
    private PlanningActivityTable activitiesTable;

    public ResourceAllocationList(PlanningActivityTable activitiesTable) {
        this.activitiesTable = activitiesTable;
    }

    public PlanningActivityTable getActivitiesTable() {
        return activitiesTable;
    }

    public void add(ResourceAllocation a) {
        if (all.containsKey(a.allocationId)) {
            throw new IllegalArgumentException("Allocation Failed. Conflict between \n\t" + a.desc + " \n\t\tand \n\t" + all.get(a.allocationId).desc);
        }
        all.put(a.allocationId, a);
    }

    public List<TeacherInfo> getTeacherInfos() {
        List<TeacherInfo> values = new ArrayList<>(teachers.values());
        Collections.sort(values, new Comparator<TeacherInfo>() {
            @Override
            public int compare(TeacherInfo o1, TeacherInfo o2) {
                return o1.getTeacher().compareTo(o2.getTeacher());
            }
        });
        return values;
    }

    public TeacherInfo getTeacherInfo(String person) {
        TeacherInfo p = teachers.get(person);
        if (p == null) {
            p = new TeacherInfo(person);
            teachers.put(person, p);
        }
        return p;
    }
}
