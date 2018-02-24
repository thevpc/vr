package net.vpc.app.vainruling.core.service.stats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 8/29/16.
 */
public class KPIUtils {
    public static <V> KPIGroupBy<V> groupBy(KPIGroupBy<V>... all){
        List<KPIGroupBy<V>> ok=new ArrayList<>();
        if(all!=null){
            for (KPIGroupBy<V> f : all) {
                if(f!=null){
                    ok.add(f);
                }
            }
        }
        if(ok.size()==0){
            throw new RuntimeException("Invalid");
        }
        return new KPIGroupByList<V>(ok);
    }
}
