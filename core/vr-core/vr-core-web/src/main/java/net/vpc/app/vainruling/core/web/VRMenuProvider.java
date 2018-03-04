/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web;

import java.util.List;
import net.vpc.app.vainruling.core.web.menu.VRMenuInfo;

/**
 * companion interface to cerate custom menus in @VrController classes
 * @author taha.bensalah@gmail.com
 */
public interface VRMenuProvider {
    List<VRMenuInfo> createCustomMenus();
}
