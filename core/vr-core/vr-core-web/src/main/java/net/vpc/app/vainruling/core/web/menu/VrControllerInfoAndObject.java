package net.vpc.app.vainruling.core.web.menu;

import net.vpc.app.vainruling.core.web.VrControllerInfo;

public class VrControllerInfoAndObject {
    private VrControllerInfo info;
    private Object instance;

    public VrControllerInfoAndObject(VrControllerInfo info, Object instance) {
        this.info=info;
        this.instance=instance;
    }

    public VrControllerInfo getInfo() {
        return info;
    }

//    public void setInfo(VrControllerInfo info) {
//        this.info = info;
//    }

    public Object getInstance() {
        return instance;
    }

//    public void setInstance(Object instance) {
//        this.instance = instance;
//    }
}
