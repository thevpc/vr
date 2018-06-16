package net.vpc.app.vainruling.core.service.obj;

import net.vpc.upa.Entity;

/**
 * Created by vpc on 6/25/17.
 */
public interface EntityObjSearchFactory {
    String createHelperString(String name, Entity entity);
    ObjSearch create(String name, Entity entity, String expression);
}
