package net.vpc.app.vainruling.plugins.academic.perfeval.service.dto;

import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;

public class Studentinfo {
    private AcademicStudent student;
    private int maxValidation;
    private int validated;
    private long maxAnswers;
    private long answers;

    public AcademicStudent getStudent() {
        return student;
    }

    public void setStudent(AcademicStudent student) {
        this.student = student;
    }

    public int getMaxValidation() {
        return maxValidation;
    }

    public void setMaxValidation(int maxValidation) {
        this.maxValidation = maxValidation;
    }

    public double getValidatedPercent() {
        if (maxValidation == 0) {
            return 0;
        }
        return 100.0 * validated / (double) maxValidation;
    }

    public double getAnswersPercent() {
        if (maxAnswers == 0) {
            return 0;
        }
        return 100.0 * answers / (double) maxAnswers;
    }

    public int getValidated() {
        return validated;
    }

    public void setValidated(int validated) {
        this.validated = validated;
    }

    public long getMaxAnswers() {
        return maxAnswers;
    }

    public void setMaxAnswers(long maxAnswers) {
        this.maxAnswers = maxAnswers;
    }

    public long getAnswers() {
        return answers;
    }

    public void setAnswers(long answers) {
        this.answers = answers;
    }
}
