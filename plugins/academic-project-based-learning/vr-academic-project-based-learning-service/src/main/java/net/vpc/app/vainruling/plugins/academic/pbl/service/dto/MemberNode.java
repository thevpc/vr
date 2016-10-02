package net.vpc.app.vainruling.plugins.academic.pbl.service.dto;

import net.vpc.app.vainruling.plugins.academic.pbl.service.model.ApblTeamMember;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

/**
 * Created by vpc on 9/25/16.
 */
public class MemberNode {
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
