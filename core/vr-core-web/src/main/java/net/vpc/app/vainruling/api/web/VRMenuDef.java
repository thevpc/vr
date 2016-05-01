/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author vpc
 */
public class VRMenuDef {

    private String name;
    private String path;
    private String type;
    private String command;
    private String securityKey;
    private String icon;
    private List<VRMenuDef> children;

    public VRMenuDef(String name, String path, String type, String command, String securityKey, String icon) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.command = command;
        this.securityKey = securityKey;
        this.icon = icon;
    }

    public String getPrettyURL() {
        String p = "/vr/p/" + type;
        if (!net.vpc.common.strings.StringUtils.isEmpty(command)) {
            try {
                p += "?a=" + URLEncoder.encode(command, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(VRMenuDef.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return p;
    }

    public String getIcon() {
        return icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public List<VRMenuDef> getChildren() {
        return children;
    }

    public List<VRMenuDef> getChildren(String type) {
        List<VRMenuDef> all = new ArrayList<>();
        for (VRMenuDef c : children) {
            if (c.getType().equals(type)) {
                all.add(c);
            }
        }
        return all;
    }

    public List<VRMenuDef> getSubPackages() {
        List<VRMenuDef> all = new ArrayList<>();
        for (VRMenuDef c : children) {
            if (c.getType().equals("package")) {
                all.add(c);
            }
        }
        return all;
    }

    public List<VRMenuDef> getLeafs() {
        List<VRMenuDef> all = new ArrayList<>();
        for (VRMenuDef c : children) {
            if (!c.getType().equals("package")) {
                all.add(c);
            }
        }
        return all;
    }

    public void setChildren(List<VRMenuDef> children) {
        this.children = children;
    }

    public String getName() {
        return name;
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
        final VRMenuDef other = (VRMenuDef) obj;
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
        if (!Objects.equals(this.securityKey, other.securityKey)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "VRMenuDef{" + "name=" + name + ", path=" + path + ", type=" + type + ", command=" + command + ", securityKey=" + securityKey + ", icon=" + icon + '}';
    }

}
