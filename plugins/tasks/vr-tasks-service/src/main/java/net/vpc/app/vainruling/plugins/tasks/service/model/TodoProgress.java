/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.service.model;

import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author vpc
 */
@Entity(listOrder = "date desc")
@Path("Todo")
public class TodoProgress {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private int progress;
    private Timestamp date;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private String message;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private TodoStatus status;
    @Field(modifiers = UserFieldModifier.SUMMARY)
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
