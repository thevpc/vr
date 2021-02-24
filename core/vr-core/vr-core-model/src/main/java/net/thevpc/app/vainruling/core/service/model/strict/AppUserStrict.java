package net.thevpc.app.vainruling.core.service.model.strict;

import net.thevpc.app.vainruling.core.service.model.AppUser;

public class AppUserStrict {

    private int id;
    private String login;
    private String fullName;
    private String fullTitle;
    private String genderCode;
    private String iconPath;

    public AppUserStrict() {
    }

    public AppUserStrict(AppUser u, String icon) {
        this(u);
        setIconPath(icon);
    }

    public AppUserStrict(AppUser u) {
        if (u != null) {
            id = u.getId();
            login = u.getLogin();
            fullName = u.getFullName();
            fullTitle = u.getFullTitle();
            if (u.getGender() != null) {
                genderCode = u.getGender().getCode();
            }
        }
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }
}
