/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.imp;

/**
 * @author taha.bensalah@gmail.com
 */
public class AcademicTeacherImport {

    private String firstName;
    private String lastName;
    private String firstName2;
    private String lastName2;
    private String discipline;
    private String nin;
    private String phone;
    private String degreeName;
    private Integer degreeId;
    private String officialDisciplineName;
    private Integer officialDisciplineId;
    private String situationName;
    private Integer situationId;
    private String situationName2;
    private String genderName;
    private Integer genderId;
    private String civilityName;
    private Integer civilityId;
    private String departmentName;
    private Integer departmentId;
    private String startPeriodName;
    private Integer startPeriodId;
    private String email;
    private int[] weekLoads = new int[]{0, 0};

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName2() {
        return firstName2;
    }

    public void setFirstName2(String firstName2) {
        this.firstName2 = firstName2;
    }

    public String getLastName2() {
        return lastName2;
    }

    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public Integer getDegreeId() {
        return degreeId;
    }

    public void setDegreeId(Integer degreeId) {
        this.degreeId = degreeId;
    }

    public String getSituationName() {
        return situationName;
    }

    public void setSituationName(String situationName) {
        this.situationName = situationName;
    }

    public Integer getSituationId() {
        return situationId;
    }

    public void setSituationId(Integer situationId) {
        this.situationId = situationId;
    }

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }

    public String getCivilityName() {
        return civilityName;
    }

    public void setCivilityName(String civilityName) {
        this.civilityName = civilityName;
    }

    public Integer getCivilityId() {
        return civilityId;
    }

    public void setCivilityId(Integer civilityId) {
        this.civilityId = civilityId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int[] getWeekLoads() {
        return weekLoads;
    }

    public void setWeekLoads(int[] weekLoads) {
        this.weekLoads = weekLoads;
    }

    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public String getSituationName2() {
        return situationName2;
    }

    public void setSituationName2(String situationName2) {
        this.situationName2 = situationName2;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getStartPeriodName() {
        return startPeriodName;
    }

    public void setStartPeriodName(String startPeriodName) {
        this.startPeriodName = startPeriodName;
    }

    public Integer getStartPeriodId() {
        return startPeriodId;
    }

    public void setStartPeriodId(Integer startPeriodId) {
        this.startPeriodId = startPeriodId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOfficialDisciplineName() {
        return officialDisciplineName;
    }

    public void setOfficialDisciplineName(String officialDisciplineName) {
        this.officialDisciplineName = officialDisciplineName;
    }

    public Integer getOfficialDisciplineId() {
        return officialDisciplineId;
    }

    public void setOfficialDisciplineId(Integer officialDisciplineId) {
        this.officialDisciplineId = officialDisciplineId;
    }
}
