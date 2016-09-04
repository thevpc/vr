package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by vpc on 9/4/16.
 */
public class AssignmentChuck {
    private Map<Integer, TeacherAssignmentChunck> chuncks = new HashMap<>();

    public Map<Integer, TeacherAssignmentChunck> getChuncks() {
        return chuncks;
    }

    public String toStringByTeacher(int teacherId){
        StringBuilder sb = new StringBuilder();

        for (TeacherAssignmentChunck i : chuncks.values()) {
            if(i.getTeacherId()!=teacherId) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(i.toString());
            }
        }
        return sb.toString();
    }
}