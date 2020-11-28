package net.thevpc.app.vainruling.plugins.academic.perfeval.service.dto;

import net.thevpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedbackQuestion;

public class QuestionView {

    AcademicFeedbackQuestion question;
    StatCountSet values = new StatCountSet();
    String chartType;
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

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

}
