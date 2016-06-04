package net.vpc.app.vainruling.plugins.academic.teachereval.web;

import java.util.List;

/**
 * Created by vpc on 5/15/16.
 */
public class GroupView {

    private String title;
    private List<QuestionView> questions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<QuestionView> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionView> questions) {
        this.questions = questions;
    }

}
