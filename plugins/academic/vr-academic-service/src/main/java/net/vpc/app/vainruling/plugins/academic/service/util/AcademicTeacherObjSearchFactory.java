package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.AbstractEntityObjSearchFactory;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import java.util.List;

/**
 * Created by vpc on 6/25/17.
 */
public class AcademicTeacherObjSearchFactory extends AbstractEntityObjSearchFactory {
    @Override
    protected List filterContactsByProfileFilter0(List objects, String profileSearchText) {
        return VrApp.getBean(AcademicPlugin.class).filterTeachers(objects,profileSearchText);
    }
}
