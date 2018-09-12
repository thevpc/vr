/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.menu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.web.VrActionInfo;
import net.vpc.app.vainruling.core.web.VrActionEnabler;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import net.vpc.common.strings.StringUtils;

/**
 * @author taha.bensalah@gmail.com
 */
public class VRMenuInfo implements VrActionInfo{

    public static final Comparator<VRMenuInfo> VR_MENU_DEF_COMPARATOR = new Comparator<VRMenuInfo>() {
        @Override
        public int compare(VRMenuInfo o1, VRMenuInfo o2) {
            int x=Integer.compare(o1.getOrder(),o2.getOrder());
            if(x!=0){
                return x;
            }
            return o1.getName().compareTo(o2.getName());
        }
    };
    private int order;
    private String name;
    private String source;
    private String path;
    private String type;
    private String command;
    private boolean selected;
    private VrActionEnabler enabler;
    private String securityKey;
    private String icon;
    private List<VRMenuInfo> children=null;
    private List<VRMenuLabel> labels=new ArrayList<>();

    public VRMenuInfo(String name, String path, String type, String command, String securityKey, VrActionEnabler enabler,String icon,int order,VRMenuLabel[] labels) {
        this.name = name;
        this.path = path;
        this.enabler = enabler;
        this.order = order;
        this.type = type;
        this.command = command;
        this.securityKey = securityKey;
        this.icon = icon;
        if(labels!=null){
            for (VRMenuLabel label : labels) {
                if(label!=null){
                    this.labels.add(label);
                }
            }
        }
        //should get labels for the current session
//        int r=(int)(Math.random()*4);
//        for (int i = 0; i < r; i++) {
//            String stype=VrApp.getBean(Vr.class).randomize("success","warn","info","severe");
//            String sval=VrApp.getBean(Vr.class).randomize("new","12","3..","(+)"
//                    ,String.valueOf((int)(Math.random()*100))
//                    ,String.valueOf((int)(Math.random()*100))
//                    ,String.valueOf((int)(Math.random()*100))
//                    ,String.valueOf((int)(Math.random()*100))
//                    ,String.valueOf((int)(Math.random()*100))
//                    );
//            labels.add(new VRMenuLabel(sval,stype));
//        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    

    @Override
    public String getMenuPath() {
        return path;
    }

    @Override
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    

    
    public int getOrder() {
        return order;
    }

    public List<VRMenuLabel> getLabels() {
        return labels;
    }

    public String getPrettyURL() {
        String context = VrWebHelper.getContext();
        if(context==null){
            context="";
        }
        if (context.length()>0 && !context.endsWith("/")) {
            context = context + "/";
        }
        String p = context + "/p/" + type;
        if (!StringUtils.isEmpty(command)) {
            try {
                p += "?a=" + URLEncoder.encode(command, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(VRMenuInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return p;
    }

    public String getIcon() {
        return icon;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public List<VRMenuInfo> getChildren() {
        return children;
    }

    public void setChildren(List<VRMenuInfo> children) {
        this.children = children;
    }

    public List<VRMenuInfo> getChildren(String type) {
        List<VRMenuInfo> all = new ArrayList<>();
        for (VRMenuInfo c : children) {
            if (c.getType().equals(type)) {
                all.add(c);
            }
        }
        return all;
    }

    public boolean isGroup() {
        return getType().equals("package");
    }

    public boolean isLeaf() {
        return !getType().equals("package");
    }

    public List<VRMenuInfo> resolveGroups() {
        List<VRMenuInfo> all = new ArrayList<>();
        for (VRMenuInfo c : children) {
            if (c.getType().equals("package")) {
                all.add(c);
            }
        }
        Collections.sort(all, VR_MENU_DEF_COMPARATOR);
        return all;
    }

    public List<VRMenuInfo> resolveLeaves() {
        List<VRMenuInfo> all = new ArrayList<>();
        for (VRMenuInfo c : children) {
            if (!c.getType().equals("package")) {
                all.add(c);
            }
        }
        Collections.sort(all, VR_MENU_DEF_COMPARATOR);
        return all;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.path);
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + Objects.hashCode(this.command);
        hash = 89 * hash + Objects.hashCode(this.securityKey);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VRMenuInfo other = (VRMenuInfo) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.command, other.command)) {
            return false;
        }
        return Objects.equals(this.securityKey, other.securityKey);
    }

    @Override
    public String toString() {
        return "VRMenuDef{" + "name=" + name + ", path=" + path + ", type=" + type + ", command=" + command + ", securityKey=" + securityKey + ", icon=" + icon + '}';
    }

    public VrActionEnabler getEnabler() {
        return enabler;
    }

    public void setEnabler(VrActionEnabler enabler) {
        this.enabler = enabler;
    }
}
