/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web;

/**
 * resolves Controller (in @VrController class) info according to the actual command. This interface is
 * useful to get controller title or xhtml url customized according to the
 * command parameter
 *
 * @author taha.bensalah@gmail.com
 */
public interface VrControllerInfoResolver {

    VrControllerInfo resolveVrControllerInfo(String cmd);
}
