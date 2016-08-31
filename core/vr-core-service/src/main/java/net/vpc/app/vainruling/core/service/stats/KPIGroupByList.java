package net.vpc.app.vainruling.core.service.stats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 8/29/16.
 */
public class KPIGroupByList<V> implements KPIGroupBy<V> {
    private List<KPIGroupBy<V>> children;

    public KPIGroupByList(List<KPIGroupBy<V>> children) {
        this.children = children;
    }

    @Override
    public List<KPIGroup> createGroups(V assignment) {
        List<List<KPIGroup>> agroups = new ArrayList<>();
        for (KPIGroupBy<V> factory : children) {
            List<KPIGroup> KPIGroups = factory.createGroups(assignment);
            agroups.add(KPIGroups);
        }
        List<List<KPIGroup>> lists = cartesianProduct(agroups);
        List<KPIGroup> ok = new ArrayList<>(lists.size());
        for (List<KPIGroup> list : lists) {
            ok.add(new KPIGroupList(list));
        }
        return ok;
    }

    public List<KPIGroupBy<V>> getGroups() {
        return children;
    }


    public static <T> List<List<T>> cartesianProduct(List<List<T>> sets) {
        if (sets.size() < 2)
            throw new IllegalArgumentException(
                    "Can't have a product of fewer than two sets (got " +
                            sets.size() + ")");

        return _cartesianProduct(0, sets);
    }

    private static <T> List<List<T>> _cartesianProduct(int index, List<List<T>> sets) {
        List<List<T>> ret = new ArrayList<List<T>>();
        if (index == sets.size()) {
            ret.add(new ArrayList<T>());
        } else {
            for (T obj : sets.get(index)) {
                for (List<T> set : _cartesianProduct(index + 1, sets)) {
                    set.add(obj);
                    ret.add(set);
                }
            }
        }
        return ret;
    }
}
