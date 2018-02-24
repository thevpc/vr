package net.vpc.app.vainruling.plugins.academic.perfeval.service.dto;

import java.util.List;

public class FRow {

    private String title;
    private List<FQuestion> questions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<FQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<FQuestion> questions) {
        this.questions = questions;
    }

}
