package net.thevpc.app.vainruling.plugins.academic.model.current;


import java.util.*;

/**
 * Created by vpc on 9/4/16.
 */
public class AssignmentChuck {

    private Map<Integer, TeacherAssignmentChunck> chuncks = new HashMap<>();

    public Map<Integer, TeacherAssignmentChunck> getChuncks() {
        return chuncks;
    }

    public TeacherAssignmentChunck getForTeacher(int teacherId) {
        for (TeacherAssignmentChunck i : chuncks.values()) {
            if (i.getTeacherId() == teacherId) {
                return i;
            }
        }
        return null;
    }

    public List<TeacherAssignmentChunck> getAllButTeacher(int teacherId) {
        List<TeacherAssignmentChunck> all = new ArrayList<>();
        for (TeacherAssignmentChunck i : chuncks.values()) {
            if (i.getTeacherId() != teacherId) {
                all.add(i);
            }
        }
        return all;
    }

    public String toStringByTeacher(int teacherId) {
        StringBuilder sb = new StringBuilder();

        for (TeacherAssignmentChunck i : chuncks.values()) {
            if (i.getTeacherId() != teacherId) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(i.toString());
            }
        }
        return sb.toString();
    }
}
