package net.thevpc.app.vainruling.core.service.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vpc on 6/18/16.
 */
public class AppVersion {
    private String id = "vr";
    private String shortName = "VR";
    private String longName = "Vain Ruling";
    private String version = "1.0.54";
    private String buildDate = "2016-06-17";
    private String buildNumber = "54";
    private String author = "Taha BEN SALAH (c)";
    private String authorUrl = "http://tahabensalah.net";
    private String authorAffiliation = "ENISo";
    private String defaultPublicTheme = "default";
    private String defaultPrivateTheme = "default";
    private Map<String,String> config = new HashMap<>();

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getDefaultPublicTheme() {
        return defaultPublicTheme;
    }

    public void setDefaultPublicTheme(String defaultPublicTheme) {
        this.defaultPublicTheme = defaultPublicTheme;
    }

    public String getDefaultPrivateTheme() {
        return defaultPrivateTheme;
    }

    public void setDefaultPrivateTheme(String defaultPrivateTheme) {
        this.defaultPrivateTheme = defaultPrivateTheme;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getAuthorAffiliation() {
        return authorAffiliation;
    }

    public void setAuthorAffiliation(String authorAffiliation) {
        this.authorAffiliation = authorAffiliation;
    }
    
}
