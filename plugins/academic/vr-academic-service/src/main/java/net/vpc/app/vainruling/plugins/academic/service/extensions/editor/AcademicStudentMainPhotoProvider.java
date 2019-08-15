package net.vpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.editor.ForEntity;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.common.util.Convert;
import net.vpc.app.vainruling.core.service.editor.EntityEditorMainPhotoProvider;
import org.springframework.stereotype.Component;

/**
 * Created by vpc on 4/15/17.
 */

@ForEntity("AcademicStudent")
@Component
public class AcademicStudentMainPhotoProvider implements EntityEditorMainPhotoProvider {
    @Override
    public String getMainPhotoPath(Object id, Object valueOrNull) {
        if(id==null){
            return null;
        }
        AcademicStudent teacher = AcademicPlugin.get().findStudent(Convert.toInt(id));
        return CorePlugin.get().getUserPhoto(
                (teacher==null || teacher.getUser()==null)?-1:teacher.getUser().getId()
        );
    }

    @Override
    public String getMainIconPath(Object id, Object valueOrNull) {
        if(id==null){
            return null;
        }
        AcademicStudent teacher = AcademicPlugin.get().findStudent(Convert.toInt(id));
        return CorePlugin.get().getUserIcon(
                (teacher==null || teacher.getUser()==null)?-1:teacher.getUser().getId()
        );
    }
}
