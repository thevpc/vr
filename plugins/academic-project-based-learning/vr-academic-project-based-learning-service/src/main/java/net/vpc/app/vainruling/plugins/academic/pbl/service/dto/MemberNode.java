package net.vpc.app.vainruling.plugins.academic.pbl.service.dto;

import net.vpc.app.vainruling.plugins.academic.pbl.model.ApblTeamMember;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;

/**
 * Created by vpc on 9/25/16.
 */
public class MemberNode extends ApblNode{
    private ApblTeamMember member;

    public MemberNode() {
    }

    public MemberNode(ApblTeamMember member) {
        this.member = member;
    }

    public ApblTeamMember getMember() {
        return member;
    }

    public void setMember(ApblTeamMember member) {
        this.member = member;
    }
}
