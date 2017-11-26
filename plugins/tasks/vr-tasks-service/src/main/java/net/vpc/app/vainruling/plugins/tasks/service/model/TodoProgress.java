/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.service.model;

import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "date desc")
@Path("Todo")
@Properties(
        {
                @Property(name = "ui.auto-filter.todo", value = "{expr='this.todo',order=1}"),
                @Property(name = "ui.auto-filter.responsible", value = "{expr='this.todo.responsible',order=2}"),
                @Property(name = "ui.auto-filter.initiator", value = "{expr='this.todo.initiator',order=3}"),
                @Property(name = "ui.auto-filter.list", value = "{expr='this.todo.list',order=4}"),
                @Property(name = "ui.auto-filter.status", value = "{expr='this.status',order=5}"),
                @Property(name = "ui.auto-filter.priority", value = "{expr='this.todo.priority',order=6}")
        }
)
public class TodoProgress {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Summary
    private int progress;
    private Timestamp date;
    @Summary
    private String message;
    @Summary
    private TodoStatus status;
    @Summary
    private Todo todo;
    private double reEstimation;
    private double consumption;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    public double getReEstimation() {
        return reEstimation;
    }

    public void setReEstimation(double reEstimation) {
        this.reEstimation = reEstimation;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

}
