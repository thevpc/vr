package net.vpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.editor.ProfileBasedEntityObjSearchFactory;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import java.util.List;
import net.vpc.app.vainruling.core.service.editor.ForEntity;
import org.springframework.stereotype.Component;

/**
 * Created by vpc on 6/25/17.
 */
@ForEntity("AcademicTeacher")
@Component
public class AcademicTeacherObjSearchFactory extends ProfileBasedEntityObjSearchFactory {
    @Override
    protected List filterDocumentByProfileFilter(List objects, String profileSearchText) {
        return VrApp.getBean(AcademicPlugin.class).filterTeachers(objects,profileSearchText);
    }
}
