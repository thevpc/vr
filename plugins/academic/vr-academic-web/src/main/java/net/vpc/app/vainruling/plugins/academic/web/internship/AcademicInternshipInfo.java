package net.vpc.app.vainruling.plugins.academic.web.internship;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.config.AcademicInternshipStatus;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternshipSupervisorIntent;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.ext.AcademicInternshipExt;
import net.vpc.common.strings.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by vpc on 6/17/17.
 */
public class AcademicInternshipInfo {

    private AcademicInternshipExt internshipExt;
    private AcademicInternship internship;
    private List<List<Integer>> flags;
    private List<AcademicTeacher> intentTeachers;
    private boolean selectable;
    private boolean demanded;
    private boolean demandedOrAssigned;
    private boolean demandedByMe;
    private boolean assigned;
    private boolean assignedToMe;
    private String supervisorInfo;

    public AcademicInternshipInfo() {
    }

    public AcademicInternshipInfo(AcademicInternshipExt internship,AcademicTeacher teacher) {
        setInternship(internship.getInternship());
        setInternshipExt(internship);
        rewrap(teacher);
    }

    public AcademicInternship getInternship() {
        return internship;
    }

    public void setInternship(AcademicInternship internship) {
        this.internship = internship;
    }

    public List<List<Integer>> getFlags() {
        return flags;
    }

    public void setFlags(List<List<Integer>> flags) {
        this.flags = flags;
    }

    public boolean isDemanded() {
        return demanded;
    }

    public void setDemanded(boolean demanded) {
        this.demanded = demanded;
    }

    public boolean isAssignedToMe() {
        return assignedToMe;
    }

    public void setAssignedToMe(boolean assignedToMe) {
        this.assignedToMe = assignedToMe;
    }

    public String getSupervisorInfo() {
        return supervisorInfo;
    }

    public void setSupervisorInfo(String supervisorInfo) {
        this.supervisorInfo = supervisorInfo;
    }

    public List<AcademicTeacher> getIntentTeachers() {
        return intentTeachers;
    }

    public void setIntentTeachers(List<AcademicTeacher> intentTeachers) {
        this.intentTeachers = intentTeachers;
    }

    public boolean isDemandedOrAssigned() {
        return demandedOrAssigned;
    }

    public void setDemandedOrAssigned(boolean demandedOrAssigned) {
        this.demandedOrAssigned = demandedOrAssigned;
    }

    public boolean isDemandedByMe() {
        return demandedByMe;
    }

    public void setDemandedByMe(boolean demandedByMe) {
        this.demandedByMe = demandedByMe;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public AcademicInternshipExt getInternshipExt() {
        return internshipExt;
    }

    public void setInternshipExt(AcademicInternshipExt internshipExt) {
        this.internshipExt = internshipExt;
    }

    protected AcademicInternshipInfo rewrap(AcademicTeacher tt) {
        AcademicInternshipInfo i=this;
        i.setFlags(new ArrayList<>());
        i.setAssigned(false);
        i.setAssignedToMe(false);
        i.setDemanded(false);
        i.setDemandedByMe(false);
        i.setDemandedOrAssigned(false);
        i.setSelectable(true);

//        AcademicTeacher tt = getCurrentTeacher();

        AcademicInternshipStatus status = i.getInternship().getInternshipStatus();

        i.setSelectable(status.isSupervisorRequestable());
        TreeSet<String> supervisorInfo = new TreeSet<>();
        if (i.getInternship().getSupervisor() != null || i.getInternship().getSecondSupervisor() != null) {
            i.setAssigned(true);
            if (tt != null && i.getInternship().getSupervisor().getId() == tt.getId()) {
                i.setAssignedToMe(true);
            }
            if (i.getInternship().getSupervisor() != null) {
                supervisorInfo.add(i.getInternship().getSupervisor().getContact().getFullName() + "*");
            }
            if (i.getInternship().getSupervisor() != null) {
                supervisorInfo.add(i.getInternship().getSupervisor().getContact().getFullName() + "*");
            }
        }
        List<AcademicInternshipSupervisorIntent> allIntents = i.getInternshipExt().getSupervisorIntents();
        if (allIntents.size() > 0) {
            i.setDemanded(true);
            for (AcademicInternshipSupervisorIntent aa : allIntents) {
                AcademicTeacher a = aa.getTeacher();
                if (tt != null && a.getId() == tt.getId()) {
                    i.setDemandedByMe(true);
                }
                String n = a.getContact().getFullName();
                if (!supervisorInfo.contains(n + "*")) {
                    supervisorInfo.add(n);
                }
            }
        }
        StringBuilder supervisorInfoStr = new StringBuilder();
        for (String s : supervisorInfo) {
            if (supervisorInfoStr.length() > 0) {
                supervisorInfoStr.append(", ");
            }
            supervisorInfoStr.append(s);
        }
        i.setSupervisorInfo(supervisorInfoStr.toString());
        List<Integer> row1 = new ArrayList<>();
        List<Integer> row2 = new ArrayList<>();
        List<Integer> row3 = new ArrayList<>();
        i.getFlags().add(row1);
        i.getFlags().add(row2);
        i.getFlags().add(row3);

        row1.add(StringUtils.isEmpty(i.getInternship().getName()) ? 2 : i.getInternship().getName().length() < 10 ? 1 : 0);
        row1.add(StringUtils.isEmpty(i.getInternship().getDescription()) ? 2 : (i.getInternship().getDescription().length() < 50) ? 1 : 0);
        row1.add(i.getInternship().getInternshipVariant() == null ? 2 : 0);
        row1.add((i.getInternship().getCompany() == null && StringUtils.isEmpty(i.getInternship().getCompanyOther())) ? 2 : (i.getInternship().getCompany() == null) ? 1 : 0);
        row1.add(
                (i.getInternship().getCompanyMentor() == null && StringUtils.isEmpty(i.getInternship().getCompanyMentorOther())) ? 2 : 0
        );
        row1.add(2 - ((!StringUtils.isEmpty(i.getInternship().getCompanyMentorOtherEmail())) ? 1 : 0)
                - ((!StringUtils.isEmpty(i.getInternship().getCompanyMentorOtherPhone())) ? 1 : 0));

        row2.add(2 - (StringUtils.isEmpty(i.getInternship().getMainDiscipline()) ? 0 : 1) - (StringUtils.isEmpty(i.getInternship().getTechnologies()) ? 0 : 1));
        row2.add((i.getInternship().getStartDate() == null || i.getInternship().getEndDate() == null) ? 2 : i.getInternship().getEndDate().before(i.getInternship().getStartDate()) ? 1 : 0);
        row2.add((i.getInternship().getSpecFilePath() == null) ? 2 : 0);
        row2.add((i.getInternship().getMidTermReportFilePath() == null) ? 2 : 0);
        row2.add((i.getInternship().getReportFilePath() == null) ? 2 : 0);
        row2.add(-1);

        row3.add((i.getInternship().getSupervisor() == null) ? 2 : 0);
        boolean boardUpdatesEvaluators = i.getInternship().getInternshipStatus().isBoardUpdatesEvaluators();
        row3.add(boardUpdatesEvaluators ? ((i.getInternship().getChairExaminer() == null) ? 2 : 0) : -1);
        row3.add(boardUpdatesEvaluators ? ((i.getInternship().getFirstExaminer() == null) ? 2 : 0) : -1);
        row3.add(boardUpdatesEvaluators ? ((i.getInternship().getExamDate() == null) ? 2 : 0) : -1);
        row3.add(boardUpdatesEvaluators ? ((i.getInternship().getExamLocation() == null) ? 2 : 0) : -1);
        row3.add(-1);

        return i;
    }

}
