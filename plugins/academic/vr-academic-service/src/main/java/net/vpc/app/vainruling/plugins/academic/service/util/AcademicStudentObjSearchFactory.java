package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.AbstractEntityObjSearchFactory;
import net.vpc.app.vainruling.core.service.obj.EntityObjSearchFactory;
import net.vpc.app.vainruling.core.service.obj.ObjSearch;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.upa.Document;
import net.vpc.upa.Entity;
import net.vpc.upa.EntityBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 6/25/17.
 */
public class AcademicStudentObjSearchFactory extends AbstractEntityObjSearchFactory {

    @Override
    protected List filterContactsByProfileFilter0(List objects, String profileSearchText) {
        return VrApp.getBean(AcademicPlugin.class).filterStudents(objects,profileSearchText);
    }
}
