package net.vpc.app.vainruling.plugins.academic.web.photo;

import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.app.vainruling.core.web.obj.MainPhotoProvider;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
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
        return Vr.get().getUserPhoto(
                (teacher==null || teacher.getUser()==null)?-1:teacher.getUser().getId()
        );
    }
}
