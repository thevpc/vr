package net.vpc.app.vainruling.plugins.academic.pbl.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 9/19/16.
 */
@Entity
@Path("Education/Projects/Apbl")
public class ApblSessionStatus {
    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    @Field(max = "1024")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;
    private boolean allowAddTeam;
    private boolean allowRemoveTeam;
    private boolean allowAddMember;
    private boolean allowRemoveMember;
    private boolean allowAddProject;
    private boolean allowRemoveProject;
    private boolean allowAddCoach;
    private boolean allowRemoveCoach;
    private boolean closed;
    private boolean active;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAllowAddTeam() {
        return allowAddTeam;
    }

    public void setAllowAddTeam(boolean allowAddTeam) {
        this.allowAddTeam = allowAddTeam;
    }

    public boolean isAllowRemoveTeam() {
        return allowRemoveTeam;
    }

    public void setAllowRemoveTeam(boolean allowRemoveTeam) {
        this.allowRemoveTeam = allowRemoveTeam;
    }

    public boolean isAllowAddMember() {
        return allowAddMember;
    }

    public void setAllowAddMember(boolean allowAddMember) {
        this.allowAddMember = allowAddMember;
    }

    public boolean isAllowRemoveMember() {
        return allowRemoveMember;
    }

    public void setAllowRemoveMember(boolean allowRemoveMember) {
        this.allowRemoveMember = allowRemoveMember;
    }

    public boolean isAllowAddProject() {
        return allowAddProject;
    }

    public void setAllowAddProject(boolean allowAddProject) {
        this.allowAddProject = allowAddProject;
    }

    public boolean isAllowRemoveProject() {
        return allowRemoveProject;
    }

    public void setAllowRemoveProject(boolean allowRemoveProject) {
        this.allowRemoveProject = allowRemoveProject;
    }

    public boolean isAllowAddCoach() {
        return allowAddCoach;
    }

    public void setAllowAddCoach(boolean allowAddCoach) {
        this.allowAddCoach = allowAddCoach;
    }

    public boolean isAllowRemoveCoach() {
        return allowRemoveCoach;
    }

    public void setAllowRemoveCoach(boolean allowRemoveCoach) {
        this.allowRemoveCoach = allowRemoveCoach;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
