/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import java.io.Serializable;

/**
 * @author taha.bensalah@gmail.com
 */
public class DialogResult implements Serializable{
    private String value;
    private String userInfo;

    public DialogResult() {
    }

    public DialogResult(String value, String userInfo) {
        this.value = value;
        this.userInfo = userInfo;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

}
