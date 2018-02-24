/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.service.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.deadline")
@Path("Todo")
@Properties(
        {
                @Property(name = "ui.auto-filter.responsible", value = "{expr='this.responsible',order=1}"),
                @Property(name = "ui.auto-filter.initiator", value = "{expr='this.initiator',order=2}"),
                @Property(name = "ui.auto-filter.list", value = "{expr='this.list',order=3}"),
                @Property(name = "ui.auto-filter.status", value = "{expr='this.status',order=4}"),
                @Property(name = "ui.auto-filter.priority", value = "{expr='this.priority',order=5}")
        }
)
public class Todo {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;
    private Timestamp creationTime;
    private Timestamp startTime;
    @Summary
    private Timestamp deadline;
    @Summary
    private AppUser responsible;
    private AppUser initiator;
    @Summary
    private TodoCategory category;
    private TodoList list;
    @Summary
    private TodoStatus status;
    @ToString
    private TodoPriority priority;
    @Summary
    private int progress;
    private int estimation;
    private double reEstimation;
    //    @Formula(value = "Select sum(x.consumption) from TodoProgress x where x.todoId=this.id")
    private double consumption;
    private String message;

    private boolean deleted;
    private String deletedBy;
    private Timestamp deletedOn;
    private boolean archived;

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Timestamp getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Timestamp deletedOn) {
        this.deletedOn = deletedOn;
    }

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

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public TodoCategory getCategory() {
        return category;
    }

    public void setCategory(TodoCategory category) {
        this.category = category;
    }

    public AppUser getResponsible() {
        return responsible;
    }

    public void setResponsible(AppUser responsible) {
        this.responsible = responsible;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    public TodoPriority getPriority() {
        return priority;
    }

    public void setPriority(TodoPriority priority) {
        this.priority = priority;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getEstimation() {
        return estimation;
    }

    public void setEstimation(int estimation) {
        this.estimation = estimation;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public double getReEstimation() {
        return reEstimation;
    }

    public void setReEstimation(double reEstimation) {
        this.reEstimation = reEstimation;
    }

    public AppUser getInitiator() {
        return initiator;
    }

    public void setInitiator(AppUser initiator) {
        this.initiator = initiator;
    }

    public TodoList getList() {
        return list;
    }

    public void setList(TodoList list) {
        this.list = list;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

}
