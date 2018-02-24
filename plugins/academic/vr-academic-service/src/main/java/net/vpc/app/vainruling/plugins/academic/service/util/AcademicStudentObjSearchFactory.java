package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.core.service.obj.AbstractEntityObjSearchFactory;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vpc on 6/25/17.
 */
public class AcademicStudentObjSearchFactory extends AbstractEntityObjSearchFactory {

    @Override
    protected List filterContactsByProfileFilter0(List objects, String profileSearchText) {
        AcademicStudentProfileFilter filter=new AcademicStudentProfileFilter(profileSearchText);
        return (List) objects.stream().filter(x -> filter.accept((AcademicStudent) x)).collect(Collectors.toList());
    }
}
