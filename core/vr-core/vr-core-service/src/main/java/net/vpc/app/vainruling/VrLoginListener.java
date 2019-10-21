/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling;

import net.vpc.app.vainruling.core.service.security.UserSessionInfo;

/**
 *
 * @author vpc
 */
public interface VrLoginListener {
    void onAuthenticate(UserSessionInfo a);
}
