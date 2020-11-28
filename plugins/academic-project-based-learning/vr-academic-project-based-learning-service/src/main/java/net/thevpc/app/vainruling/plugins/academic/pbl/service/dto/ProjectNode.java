package net.thevpc.app.vainruling.plugins.academic.pbl.service.dto;

import net.thevpc.app.vainruling.plugins.academic.pbl.model.ApblProject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 9/25/16.
 */
public class ProjectNode extends ApblNode{
    private ApblProject project;
    private List<TeamNode> teams = new ArrayList<>();

    public ApblProject getProject() {
        return project;
    }

    public void setProject(ApblProject project) {
        this.project = project;
    }

    public List<TeamNode> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamNode> teams) {
        this.teams = teams;
    }
}
