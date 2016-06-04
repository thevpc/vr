/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.Locale;

/**
 *
 * @author dev01
 */
public class SSOPrincipal implements Principal, Serializable {

    private Object userObject;
    private String name;
    private String fullName;
    private String authority;
    private String lang;
    private String ipAddress;
    private String station;
    private boolean admin;
    private long profileId;
    private String profileName;
    private String profileType;
    private Locale locale;

    public SSOPrincipal(Object userObject, String name, String authority, String lang) {
        this.name = name;
        this.userObject = userObject;
        this.authority = authority;
        this.lang = lang;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getLang() {
        return lang;
    }

    public Object getUserObject() {
        return userObject;
    }

    public String getAuthority() {
        return authority;
    }

    @Override
    public String getName() {
        return name;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
