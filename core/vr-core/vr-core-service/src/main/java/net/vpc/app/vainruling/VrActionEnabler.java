package net.vpc.app.vainruling;

/**
 * manages visibility of menus (VrControllerInfo, VrMenuInfo)
 *
 * @author vpc
 */
public interface VrActionEnabler {

    void checkEnabled(VrActionInfo data) throws SecurityException;
}
