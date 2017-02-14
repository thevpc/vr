package net.vpc.app.vainruling.plugins.academic.perfeval.service.dto;

import net.vpc.app.vainruling.core.service.util.ValueCountSet;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class StatCountSet extends ValueCountSet {
    private EvalScore score=null;
    private Set<Integer> students=new HashSet<>();

    public StatCountSet() {
    }

    public Set<Integer> getStudents() {
        return students;
    }

    public int getStudentsSize() {
        return students.size();
    }

    public void rebuild(){
        int questions = 0;
        double all = 0;

        for (String val : new TreeSet<String>((Set) this.keySet())) {
            String vv = String.valueOf(val);
            int count = this.getCount(val);
            double weight = (Integer.valueOf(val));
            questions += count;
            all += (count * weight);
        }
        all = all / questions;
        this.score=new EvalScore((int) ((all - 1) / 3 * 100.0),students.size(),questions);
    }

    public EvalScore getScore() {
        if(score==null){
            rebuild();
        }
        return score;
    }
}
