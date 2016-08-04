package net.vpc.app.vainruling.plugins.academic.internship.service.model.planning;

import net.vpc.common.strings.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by vpc on 5/20/16.
 */
public class FitnessValue {
    String name;
    boolean valid;
    double value;
    List<FitnessValue> children;

    public FitnessValue(String name, boolean valid, double value, List<FitnessValue> children) {
        this.name = name;
        this.valid = valid;
        this.value = value;
        this.children = children != null ? children : Collections.EMPTY_LIST;
        if (value < 0 || Double.isNaN(value)) {
            throw new IllegalArgumentException();
        }
    }

    public static FitnessValue create(String name, List<FitnessValue> others) {
        return create(name, others.toArray(new FitnessValue[others.size()]));
    }

    public static FitnessValue zero(String name) {
        return create(name, false, 0);
    }

    public static FitnessValue invalid(String name, double value) {
        return new FitnessValue(name, false, value, null);
    }

    public static FitnessValue create(String name, boolean valid, double value) {
        return new FitnessValue(name, valid, value, null);
    }

    public static FitnessValue valid(String name, double value) {
        return new FitnessValue(name, true, value, null);
    }

    public static FitnessValue create(String name, FitnessValue... others) {
        boolean allValid = true;
        double sum = 0;
        for (FitnessValue other : others) {
            allValid = allValid && other.valid;
            sum += other.value;
        }
        if (!allValid) {
            sum = sum / 2;
        }
        return new FitnessValue(name, allValid, sum, Arrays.asList(others));
    }

    public FitnessValue mul(double coeff) {
        return new FitnessValue(name, valid, value * coeff, new ArrayList<>(children));
    }

//        public FitnessValue add(FitnessValue o){
//            boolean allValid = o.valid && this.valid;
//            if(allValid) {
//                return new FitnessValue("", allValid, value + o.value, Arrays.asList(this, o));
//            }else{
//                return new FitnessValue("", allValid, (value + o.value)/1000, Arrays.asList(this, o));
//            }
//        }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(name)) {
            sb.append(name);
        }
        sb.append("(");
        sb.append(value);
        if (!children.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(children);
        }
        sb.append(")");
        if (!valid) {
            sb.insert(0, "invalid-");
        }
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public FitnessValue setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isValid() {
        return valid;
    }

    public double getValue() {
        return value;
    }

    public List<FitnessValue> getChildren() {
        return children;
    }
}
