package net.thevpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.thevpc.app.vainruling.VrEntityName;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.editor.ProfileBasedEntityEditorSearch;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Created by vpc on 6/25/17.
 */
@VrEntityName("AcademicTeacher")
@Component
public class AcademicTeacherByProfileEditorSearch extends ProfileBasedEntityEditorSearch {
    @Override
    protected List filterDocumentByProfileFilter(List objects, String profileSearchText) {
        return VrApp.getBean(AcademicPlugin.class).filterTeachers(objects,profileSearchText);
    }
}
