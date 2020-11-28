/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.model;

import net.thevpc.upa.config.*;


/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.userUrl")
@Path("Social")
public class AppUrl {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    private String userUrl;
    @Summary
    private String internalURL;


    public AppUrl() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getInternalURL() {
        return internalURL;
    }

    public void setInternalURL(String internalURL) {
        this.internalURL = internalURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppUrl appUrl = (AppUrl) o;

        return id == appUrl.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
