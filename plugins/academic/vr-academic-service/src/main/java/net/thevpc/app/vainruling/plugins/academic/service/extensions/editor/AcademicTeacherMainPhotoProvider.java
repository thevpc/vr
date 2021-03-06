package net.thevpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.thevpc.app.vainruling.VrEditorMainPhotoProvider;
import net.thevpc.app.vainruling.VrEntityName;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.common.util.Convert;
import org.springframework.stereotype.Component;

/**
 * Created by vpc on 4/15/17.
 */
@VrEntityName("AcademicTeacher")
@Component
public class AcademicTeacherMainPhotoProvider implements VrEditorMainPhotoProvider {
    @Override
    public String getMainPhotoPath(Object id, Object valueOrNull) {
        if(id==null){
            return null;
        }
        AcademicTeacher teacher = AcademicPlugin.get().findTeacher(Convert.toInt(id));
        return CorePlugin.get().getUserPhoto(
                (teacher == null || teacher.getUser() == null) ? -1 : teacher.getUser().getId()
        );
    }

    @Override
    public String getMainIconPath(Object id, Object valueOrNull) {
        if(id==null){
            return null;
        }
        AcademicTeacher teacher = AcademicPlugin.get().findTeacher(Convert.toInt(id));
        return CorePlugin.get().getUserIcon(
                (teacher == null || teacher.getUser() == null) ? -1 : teacher.getUser().getId()
        );
    }
}
