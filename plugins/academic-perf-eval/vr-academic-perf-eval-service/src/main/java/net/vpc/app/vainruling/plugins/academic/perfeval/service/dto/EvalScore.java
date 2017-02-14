package net.vpc.app.vainruling.plugins.academic.perfeval.service.dto;

public class EvalScore {
    private int value;
    /**
     * nomber of students
     */
    private int population;
    /**
     * number of questions
     */
    private int coverage;

    public EvalScore(int value, int population, int coverage) {
        this.value = value;
        this.population = population;
        this.coverage = coverage;
    }

    public int getValue() {
        return value;
    }

    public int getPopulation() {
        return population;
    }

    public int getCoverage() {
        return coverage;
    }
}
