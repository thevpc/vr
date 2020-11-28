/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.imp;

import java.util.Date;

import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.upa.config.Id;
import net.thevpc.upa.config.Sequence;

/**
 * @author taha.bensalah@gmail.com
 */
public class AcademicStudentImport {
    @Id @Sequence
    private int id;
    private String nin;
    private String subscriptionNumber;
    private String firstName;
    private String lastName;
    private String firstName2;
    private String lastName2;
    private Integer departmentId;
    private String departmentName;
    private String birthDateString;
    private Date birthDate;
    private Integer preClassBacId;
    private String preClassBacName;
    private double preClassBacScore;
    private double preClassPrepScore;
    private Integer preClassPrepRank;
    private Integer preClassPrepRank2;
    private Integer preClassPrepRankMax;
    private Integer preClassRankByProgram;
    private Integer preClassChoice;
    private Integer preClassPrepId;
    private String preClassPrepName;
    private Integer preClassTypeId;
    private String preClassTypeName;
    private String email;
    private String email2;
    private String startPeriodName;
    private Integer startPeriodId;
    private String phone1;
    private String phone2;
    private String phone3;
    private String genderName;
    private Integer genderId;
    private String civilityName;
    private Integer civilityId;
    private String className;
    private Integer classId;
    private Integer preClassChoice1Id;
    private Integer preClassChoice2Id;
    private Integer preClassChoice3Id;

    public AcademicStudentImport() {
    }

    public AcademicStudentImport(AcademicStudent student) {
        nin = student.getSubscriptionNumber();
        AppUser user = student.getUser();
        if(user !=null) {
            firstName = user.getFirstName();
            firstName2 = user.getFirstName2();
            lastName = user.getLastName();
            lastName2 = user.getLastName2();
            email = user.getEmail();
            email2 = user.getEmail2();
            phone1 = user.getPhone1();
            phone2 = user.getPhone1();
            genderId = user.getGender()==null?null:user.getGender().getId();
            civilityId = user.getCivility()==null?null:user.getCivility().getId();
        }
        preClassChoice1Id = student.getPreClassChoice1() == null ? null : student.getPreClassChoice1().getId();
        preClassChoice2Id = student.getPreClassChoice2() == null ? null : student.getPreClassChoice2().getId();
        preClassChoice3Id = student.getPreClassChoice3() == null ? null : student.getPreClassChoice3().getId();
        preClassBacId=student.getBaccalaureateClass()==null?null:student.getBaccalaureateClass().getId();
        preClassBacScore=student.getBaccalaureateScore();
        preClassPrepRank=student.getPreClassRank();
        preClassPrepRank=student.getPreClassRank2();
        preClassPrepRankMax=student.getPreClassRankMax();
        preClassRankByProgram=student.getPreClassRankByProgram();
        preClassChoice=student.getPreClassChoice();
        preClassTypeId=student.getPreClassType()==null?null:student.getPreClassType().getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public String getSubscriptionNumber() {
        return subscriptionNumber;
    }

    public void setSubscriptionNumber(String subscriptionNumber) {
        this.subscriptionNumber = subscriptionNumber;
    }

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

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getPreClassBacId() {
        return preClassBacId;
    }

    public void setPreClassBacId(Integer preClassBacId) {
        this.preClassBacId = preClassBacId;
    }

    public String getPreClassBacName() {
        return preClassBacName;
    }

    public void setPreClassBacName(String preClassBacName) {
        this.preClassBacName = preClassBacName;
    }

    public Integer getPreClassPrepId() {
        return preClassPrepId;
    }

    public void setPreClassPrepId(Integer preClassPrepId) {
        this.preClassPrepId = preClassPrepId;
    }

    public String getPreClassPrepName() {
        return preClassPrepName;
    }

    public void setPreClassPrepName(String preClassPrepName) {
        this.preClassPrepName = preClassPrepName;
    }

    public double getPreClassBacScore() {
        return preClassBacScore;
    }

    public void setPreClassBacScore(double preClassBacScore) {
        this.preClassBacScore = preClassBacScore;
    }

    public Integer getPreClassPrepRank() {
        return preClassPrepRank;
    }

    public void setPreClassPrepRank(Integer preClassPrepRank) {
        this.preClassPrepRank = preClassPrepRank;
    }

    public Integer getPreClassPrepRankMax() {
        return preClassPrepRankMax;
    }

    public void setPreClassPrepRankMax(Integer preClassPrepRankMax) {
        this.preClassPrepRankMax = preClassPrepRankMax;
    }

    public Integer getPreClassTypeId() {
        return preClassTypeId;
    }

    public void setPreClassTypeId(Integer preClassTypeId) {
        this.preClassTypeId = preClassTypeId;
    }

    public String getPreClassTypeName() {
        return preClassTypeName;
    }

    public void setPreClassTypeName(String preClassTypeName) {
        this.preClassTypeName = preClassTypeName;
    }

    public double getPreClassPrepScore() {
        return preClassPrepScore;
    }

    public void setPreClassPrepScore(double preClassPrepScore) {
        this.preClassPrepScore = preClassPrepScore;
    }

    public int getPreClassPrepRank2() {
        return preClassPrepRank2;
    }

    public void setPreClassPrepRank2(Integer preClassPrepRank2) {
        this.preClassPrepRank2 = preClassPrepRank2;
    }

    public Integer getPreClassRankByProgram() {
        return preClassRankByProgram;
    }

    public void setPreClassRankByProgram(Integer preClassRankByProgram) {
        this.preClassRankByProgram = preClassRankByProgram;
    }

    public Integer getPreClassChoice() {
        return preClassChoice;
    }

    public void setPreClassChoice(Integer preClassChoice) {
        this.preClassChoice = preClassChoice;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public Integer getPreClassChoice1Id() {
        return preClassChoice1Id;
    }

    public void setPreClassChoice1Id(Integer preClassChoice1Id) {
        this.preClassChoice1Id = preClassChoice1Id;
    }

    public Integer getPreClassChoice2Id() {
        return preClassChoice2Id;
    }

    public void setPreClassChoice2Id(Integer preClassChoice2Id) {
        this.preClassChoice2Id = preClassChoice2Id;
    }

    public Integer getPreClassChoice3Id() {
        return preClassChoice3Id;
    }

    public void setPreClassChoice3Id(Integer preClassChoice3Id) {
        this.preClassChoice3Id = preClassChoice3Id;
    }

    public String getBirthDateString() {
        return birthDateString;
    }

    public void setBirthDateString(String birthDateString) {
        this.birthDateString = birthDateString;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    
}
