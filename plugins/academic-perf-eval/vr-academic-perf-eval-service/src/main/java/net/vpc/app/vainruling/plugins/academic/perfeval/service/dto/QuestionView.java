package net.vpc.app.vainruling.plugins.academic.perfeval.service.dto;

import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackQuestion;

public class QuestionView {

    AcademicFeedbackQuestion question;
    StatCountSet values = new StatCountSet();
    Object chart;

    public AcademicFeedbackQuestion getQuestion() {
        return question;
    }

    public void setQuestion(AcademicFeedbackQuestion question) {
        this.question = question;
    }

    public StatCountSet getValues() {
        return values;
    }

    public void setValues(StatCountSet values) {
        this.values = values;
    }

    public Object getChart() {
        return chart;
    }

    public void setChart(Object chart) {
        this.chart = chart;
    }

}
