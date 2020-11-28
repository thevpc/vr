/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.config;

/**
 * @author taha.bensalah@gmail.com
 */
public class AcademicTeacherStrict {

    private int id;
    private String fullName;
    private String positionSuffix;
    private String email;
    private String phone;
    private String discipline;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPositionSuffix() {
        return positionSuffix;
    }

    public void setPositionSuffix(String positionSuffix) {
        this.positionSuffix = positionSuffix;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }
}
