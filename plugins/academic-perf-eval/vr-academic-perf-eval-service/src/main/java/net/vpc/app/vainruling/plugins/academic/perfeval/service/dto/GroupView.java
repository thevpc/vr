package net.vpc.app.vainruling.plugins.academic.perfeval.service.dto;
import java.util.List;

/**
 * Created by vpc on 5/15/16.
 */
public class GroupView {

    private String title;
    private List<QuestionView> questions;
    private StatCountSet values = new StatCountSet();
    private Object chart;

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
