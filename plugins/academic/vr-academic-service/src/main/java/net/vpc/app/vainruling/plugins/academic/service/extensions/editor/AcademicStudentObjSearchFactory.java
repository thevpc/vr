package net.vpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.vpc.app.vainruling.core.service.editor.AbstractEntityObjSearchFactory;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;

import java.util.List;
import java.util.stream.Collectors;
import net.vpc.app.vainruling.core.service.editor.ForEntity;
import net.vpc.app.vainruling.plugins.academic.service.util.AcademicStudentProfileFilter;
import org.springframework.stereotype.Component;

/**
 * Created by vpc on 6/25/17.
 */
@ForEntity("AcademicStudent")
@Component
public class AcademicStudentObjSearchFactory extends AbstractEntityObjSearchFactory {

    @Override
    protected List filterContactsByProfileFilter0(List objects, String profileSearchText) {
        AcademicStudentProfileFilter filter=new AcademicStudentProfileFilter(profileSearchText);
        return (List) objects.stream().filter(x -> filter.accept((AcademicStudent) x)).collect(Collectors.toList());
    }
}
