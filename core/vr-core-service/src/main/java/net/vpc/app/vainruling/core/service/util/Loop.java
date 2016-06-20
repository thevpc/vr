package net.vpc.app.vainruling.core.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vpc on 5/25/16.
 */
public class Loop {
    private List<LoopDef> sub = new ArrayList<>();
    private int[] val;
    private boolean needReset = true;

    public static void main(String[] args) {
        Loop loop = new Loop().over(0, 3).over(0, 2);
        int[] r = null;
        while ((r = loop.next()) != null) {
            System.out.println(Arrays.toString(r));
        }
    }

    public Loop over(int min, int max) {
        sub.add(new LoopDef(min, max));
        return this;
    }

    public void reset() {
        val = new int[sub.size()];
        for (int i = 0; i < val.length; i++) {
            val[i] = sub.get(i).min;
        }
        needReset = false;
    }

    public int[] next() {
        if (needReset) {
            reset();
        }
        if (val == null) {
            return null;
        }
        int[] v = new int[val.length];
        System.arraycopy(val, 0, v, 0, val.length);

        int x = 0;
        boolean ok = false;
        while (x < val.length) {
            val[x]++;
            if (val[x] >= sub.get(x).max) {
                val[x] = sub.get(x).min;
                x++;
            } else {
                ok = true;
                break;
            }
        }
        if (!ok) {
            val = null;
        }
        return v;
    }


    public static class LoopDef {
        int min;
        int max;

        public LoopDef(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }
}
