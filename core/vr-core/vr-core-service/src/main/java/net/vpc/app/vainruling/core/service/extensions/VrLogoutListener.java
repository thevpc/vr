/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.extensions;

import net.vpc.app.vainruling.core.service.security.UserToken;

/**
 *
 * @author vpc
 */
public interface VrLogoutListener {

    void onLogout(UserToken a);
}
