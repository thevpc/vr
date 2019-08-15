package net.vpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.editor.ForEntity;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.common.util.Convert;
import net.vpc.app.vainruling.core.service.editor.EntityEditorMainPhotoProvider;
import org.springframework.stereotype.Component;

/**
 * Created by vpc on 4/15/17.
 */
@ForEntity("AcademicTeacher")
@Component
public class AcademicTeacherMainPhotoProvider implements EntityEditorMainPhotoProvider {
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
