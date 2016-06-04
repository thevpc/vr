package net.vpc.app.vainruling.plugins.academic.teachereval.web;

import net.vpc.app.vainruling.core.service.util.ValueCountSet;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackQuestion;
import org.primefaces.model.chart.BarChartModel;

/**
 * Created by vpc on 5/15/16.
 */
public class QuestionView {

    AcademicFeedbackQuestion question;
    ValueCountSet values = new ValueCountSet();
    BarChartModel chart;

    public AcademicFeedbackQuestion getQuestion() {
        return question;
    }

    public void setQuestion(AcademicFeedbackQuestion question) {
        this.question = question;
    }

    public ValueCountSet getValues() {
        return values;
    }

    public void setValues(ValueCountSet values) {
        this.values = values;
    }

    public BarChartModel getChart() {
        return chart;
    }

    public void setChart(BarChartModel chart) {
        this.chart = chart;
    }

}
