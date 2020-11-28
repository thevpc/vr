/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.calendars.service.dto;

import java.util.Objects;

/**
 *
 * @author vpc
 */
public class CalendarActivity {

    private String room;
    private String students;
    private String teacher;
    private String actor;
    private String activity;
    private String subject;

    private int subjectIndex;
    private int studentsIndex;

    public CalendarActivity() {
    }

    public CalendarActivity(CalendarActivity o) {
        if (o != null) {
            this.room = o.room;
            this.students = o.students;
            this.teacher = o.teacher;
            this.actor = o.actor;
            this.activity = o.activity;
            this.subject = o.subject;
            this.subjectIndex = o.subjectIndex;
            this.studentsIndex = o.studentsIndex;
        }
    }

    public int getSubjectIndex() {
        return subjectIndex;
    }

    public void setSubjectIndex(int subjectIndex) {
        this.subjectIndex = subjectIndex;
    }

    public int getStudentsIndex() {
        return studentsIndex;
    }

    public void setStudentsIndex(int studentsIndex) {
        this.studentsIndex = studentsIndex;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getStudents() {
        return students;
    }

    public void setStudents(String students) {
        this.students = students;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "CalendarActivity{ room=" + room + ", students=" + students + ", activity=" + activity + ", subject=" + subject + '}';
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.room);
        hash = 53 * hash + Objects.hashCode(this.students);
        hash = 53 * hash + Objects.hashCode(this.teacher);
        hash = 53 * hash + Objects.hashCode(this.actor);
        hash = 53 * hash + Objects.hashCode(this.activity);
        hash = 53 * hash + Objects.hashCode(this.subject);
        hash = 53 * hash + this.subjectIndex;
        hash = 53 * hash + this.studentsIndex;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CalendarActivity other = (CalendarActivity) obj;
        if (this.subjectIndex != other.subjectIndex) {
            return false;
        }
        if (this.studentsIndex != other.studentsIndex) {
            return false;
        }
        if (!Objects.equals(this.room, other.room)) {
            return false;
        }
        if (!Objects.equals(this.students, other.students)) {
            return false;
        }
        if (!Objects.equals(this.teacher, other.teacher)) {
            return false;
        }
        if (!Objects.equals(this.actor, other.actor)) {
            return false;
        }
        if (!Objects.equals(this.activity, other.activity)) {
            return false;
        }
        if (!Objects.equals(this.subject, other.subject)) {
            return false;
        }
        return true;
    }
    

}
