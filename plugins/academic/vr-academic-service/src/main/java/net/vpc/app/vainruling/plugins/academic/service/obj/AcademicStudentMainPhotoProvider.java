package net.vpc.app.vainruling.plugins.academic.service.obj;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.obj.MainPhotoProvider;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.common.util.Convert;

/**
 * Created by vpc on 4/15/17.
 */
public class AcademicStudentMainPhotoProvider implements MainPhotoProvider {
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
