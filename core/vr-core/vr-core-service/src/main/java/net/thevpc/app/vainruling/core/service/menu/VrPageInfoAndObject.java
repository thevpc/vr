package net.thevpc.app.vainruling.core.service.menu;

import net.thevpc.app.vainruling.VrPageInfo;

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
