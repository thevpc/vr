package net.vpc.app.vainruling.plugins.academic.pbl.service.dto;

import net.vpc.app.vainruling.plugins.academic.pbl.service.model.ApblTeam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 9/25/16.
 */
public class TeamNode extends ApblNode{
    private ApblTeam team;
    private int unsatisfiedTeamConstraints;
    private List<MemberNode> members = new ArrayList<>();
    private List<CoachNode> coaches = new ArrayList<>();

    public ApblTeam getTeam() {
        return team;
    }

    public void setTeam(ApblTeam team) {
        this.team = team;
    }

    public List<MemberNode> getMembers() {
        return members;
    }

    public void setMembers(List<MemberNode> members) {
        this.members = members;
    }

    public List<CoachNode> getCoaches() {
        return coaches;
    }

    public void setCoaches(List<CoachNode> coaches) {
        this.coaches = coaches;
    }

    public int getUnsatisfiedTeamConstraints() {
        return unsatisfiedTeamConstraints;
    }

    public TeamNode setUnsatisfiedTeamConstraints(int unsatisfiedTeamConstraints) {
        this.unsatisfiedTeamConstraints = unsatisfiedTeamConstraints;
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(team==null?"null":team.getName());
    }
}
