package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.util.I18n;

/**
 * Created by vpc on 9/4/16.
 */
public class TeacherAssignmentChunck {

    int teacherId;
    String teacherName;
    boolean assigned;
    boolean acceptedWish;
    boolean acceptedProposal;
    boolean wish;
    boolean proposal;

    public TeacherAssignmentChunck() {
        
    }
    public TeacherAssignmentChunck(int teacherId, String teacherName) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
    }

    @Override
    public String toString() {
        return teacherName + (assigned ? "(*)" : "");
    }

    public boolean isAssigned() {
        return assigned;
    }

    public TeacherAssignmentChunck setAssigned(boolean assigned) {
        this.assigned = assigned;
        return this;
    }

//    public boolean isIntended() {
//        return intended;
//    }
//
//    public TeacherAssignmentChunck setIntended(boolean intended) {
//        this.intended = intended;
//        return this;
//    }
    public boolean isWish() {
        return wish;
    }

    public void setWish(boolean wish) {
        this.wish = wish;
    }

    public boolean isProposal() {
        return proposal;
    }

    public void setProposal(boolean proposal) {
        this.proposal = proposal;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public boolean isAcceptedWish() {
        return acceptedWish;
    }

    public void setAcceptedWish(boolean acceptedWish) {
        this.acceptedWish = acceptedWish;
    }

    public boolean isAcceptedProposal() {
        return acceptedProposal;
    }

    public void setAcceptedProposal(boolean acceptedProposal) {
        this.acceptedProposal = acceptedProposal;
    }

    public String getStatusLocalizedString() {
        return I18n.get().get("TeacherAssignmentStatus[" + getStatusString() + "]");
    }

    public String getStatusString() {
        if (assigned) {
            if (isAcceptedProposal()) {
                return "AssignedAcceptedProposal";
            } else if (isAcceptedWish()) {
                return "AssignedAcceptedWish";
            } else if (isProposal() && isWish()) {
                return "AssignedProposalAndWhish";
            } else if (isProposal()) {
                return "AssignedProposal";
            } else if (isWish()) {
                return "AssignedWhish";
            } else {
                return "AssignedNone";
            }
        }
        if (isAcceptedProposal()) {
            return "AcceptedProposal";
        } else if (isAcceptedWish()) {
            return "AcceptedWish";
        } else if (isProposal() && isWish()) {
            return "ProposalAndWhish";
        } else if (isProposal()) {
            return "Proposal";
        } else if (isWish()) {
            return "Whish";
        } else {
            return "Unknown";
        }
    }

}
