package net.vpc.app.vainruling.plugins.academic.perfeval.service.dto;

import java.util.ArrayList;
import java.util.List;

public class StatData {
    private int countFeedbacks;
    private int countQuestions;
    private int countValidResponses;
    private double countResponseCompletion;
    private List<Studentinfo> studentinfos = new ArrayList<Studentinfo>();
    private List<GroupView> groupedQuestionsList = new ArrayList<>();
    private StatCountSet globalValues = new StatCountSet();
    private Object globalChart;

    public int getCountFeedbacks() {
        return countFeedbacks;
    }

    public void setCountFeedbacks(int countFeedbacks) {
        this.countFeedbacks = countFeedbacks;
    }

    public int getCountQuestions() {
        return countQuestions;
    }

    public void setCountQuestions(int countQuestions) {
        this.countQuestions = countQuestions;
    }

    public int getCountValidResponses() {
        return countValidResponses;
    }

    public void setCountValidResponses(int countValidResponses) {
        this.countValidResponses = countValidResponses;
    }

    public double getCountResponseCompletion() {
        return countResponseCompletion;
    }

    public void setCountResponseCompletion(double countResponseCompletion) {
        this.countResponseCompletion = countResponseCompletion;
    }

    public List<Studentinfo> getStudentinfos() {
        return studentinfos;
    }

    public void setStudentinfos(List<Studentinfo> studentinfos) {
        this.studentinfos = studentinfos;
    }

    public List<GroupView> getGroupedQuestionsList() {
        return groupedQuestionsList;
    }

    public void setGroupedQuestionsList(List<GroupView> groupedQuestionsList) {
        this.groupedQuestionsList = groupedQuestionsList;
    }

    public StatCountSet getGlobalValues() {
        return globalValues;
    }

    public void setGlobalValues(StatCountSet globalValues) {
        this.globalValues = globalValues;
    }

    public Object getGlobalChart() {
        return globalChart;
    }

    public void setGlobalChart(Object globalChart) {
        this.globalChart = globalChart;
    }
}
