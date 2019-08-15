package net.vpc.app.vainruling.core.service.menu;

import net.vpc.app.vainruling.core.service.pages.VrPageInfo;

public class VrPageInfoAndObject {
    private VrPageInfo info;
    private Object instance;

    public VrPageInfoAndObject(VrPageInfo info, Object instance) {
        this.info=info;
        this.instance=instance;
    }

    public VrPageInfo getInfo() {
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
