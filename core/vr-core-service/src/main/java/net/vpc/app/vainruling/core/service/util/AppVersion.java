package net.vpc.app.vainruling.core.service.util;

/**
 * Created by vpc on 6/18/16.
 */
public class AppVersion {
    private String shortName = "VR";
    private String longName = "Vain Ruling";
    private String version = "1.0.54";
    private String buildDate = "2016-06-17";
    private String buildNumber = "54";
    private String author = "Taha BEN SALAH (c)";

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
}
