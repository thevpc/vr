package net.vpc.app.vainruling.core.service.obj;

import net.vpc.upa.Entity;

/**
 * Created by vpc on 6/25/17.
 */
public interface EntityObjSearchFactory {
    ObjSearch create(String name, Entity entity, String expression);
}
