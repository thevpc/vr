package net.vpc.app.vainruling.core.service.stats;

import java.util.List;

/**
 * Created by vpc on 8/29/16.
 */
public class KPIGroupList implements KPIGroup {
    private List<KPIGroup> groups;
    private String name;

    public KPIGroupList(List<KPIGroup> groups) {
        this.groups = groups;
        StringBuilder sb=new StringBuilder();
        for (KPIGroup group : groups) {
            if(sb.length()>0){
                sb.append(" ");
            }
            sb.append(group.getName());
        }
        name=sb.toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KPIGroupList that = (KPIGroupList) o;

        return groups != null ? groups.equals(that.groups) : that.groups == null;

    }

    @Override
    public boolean equals(KPIGroup other) {
        return equals((Object)other);
    }

    @Override
    public int hashCode() {
        int h=0;
        if(groups != null){
            for (KPIGroup e : groups) {
                h = 31*h+ (e==null ? 0 : e.hashCode());
            }
        }
        return h;
    }

    @Override
    public int compareTo(KPIGroup o) {
        if(o instanceof KPIGroupList){
            List<KPIGroup> ogroups = ((KPIGroupList) o).groups;
            int x=groups.size()-ogroups.size();
            if(x!=0){
                return x;
            }
            for (int i = 0; i < groups.size(); i++) {
                 x=groups.get(i).compareTo(ogroups.get(i));
                if(x!=0){
                    return x;
                }
            }
            return 0;
        }else{
            return -1;
        }
    }
}
