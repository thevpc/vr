package net.thevpc.app.vainruling.plugins.academic.perfeval.service.dto;

import net.thevpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedback;

import java.util.ArrayList;
import java.util.List;

public class FeedbackForm {
    private List<FRow> rows=new ArrayList<>();
    private AcademicFeedback feedback;

    public AcademicFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(AcademicFeedback feedback) {
        this.feedback = feedback;
    }

    public List<FRow> getRows() {
        return rows;
    }

    public void setRows(List<FRow> rows) {
        this.rows = rows;
    }
}
