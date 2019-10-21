/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling;

import java.util.List;

/**
 * companion interface to cerate custom menus in @VrController classes
 * @author taha.bensalah@gmail.com
 */
public interface VrMenuProvider {
    List<VrMenuInfo> createCustomMenus();
}
