package net.thevpc.app.vainruling.plugins.academic.perfeval.service.dto;

import java.util.HashMap;

import net.thevpc.app.vainruling.core.service.util.ValueCountSet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class StatCountSet extends ValueCountSet {

    private EvalScore score = null;
    private Set<Integer> students = new HashSet<>();

    public StatCountSet() {
    }

    public Set<Integer> getStudents() {
        return students;
    }

    public int getStudentsSize() {
        return students.size();
    }

    public void rebuild() {
        int questions = 0;
        double maxScore = 0;
        double all = 0;
        TreeSet<String> keys = new TreeSet<String>((Set) this.keySet());
        Map<String, Integer> mappedWeights = new HashMap<>();
        int maxWeight = 0;
        for (String key : keys) {
            maxWeight++;
            mappedWeights.put(key, maxWeight);
        }
        if (maxWeight == 0) {
            this.score = new EvalScore(0, students.size(), questions);
        } else {
            for (String val : keys) {
                String vv = String.valueOf(val);
                int count = this.getCount(val);
                double weight = ((Number)mappedWeights.get(vv)).doubleValue();
                questions += count;
                all += (count * weight);
                maxScore += (count * maxWeight);
            }
            all = all / maxScore;
            this.score = new EvalScore((int) (all * 100.0), students.size(), questions);
        }
    }

    public EvalScore getScore() {
        if (score == null) {
            rebuild();
        }
        return score;
    }
}
