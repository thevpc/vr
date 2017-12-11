package net.vpc.app.vainruling.plugins.academic.web.photo;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.app.vainruling.core.web.obj.MainPhotoProvider;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.util.Convert;

/**
 * Created by vpc on 4/15/17.
 */
public class AcademicTeacherMainPhotoProvider implements MainPhotoProvider {
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
