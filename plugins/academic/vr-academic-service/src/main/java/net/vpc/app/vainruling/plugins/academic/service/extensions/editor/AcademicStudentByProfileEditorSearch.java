package net.vpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.vpc.app.vainruling.core.service.editor.ProfileBasedEntityEditorSearch;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;

import java.util.List;
import java.util.stream.Collectors;
import net.vpc.app.vainruling.plugins.academic.service.util.AcademicStudentProfileFilter;
import org.springframework.stereotype.Component;
import net.vpc.app.vainruling.VrEntityName;

/**
 * Created by vpc on 6/25/17.
 */
@VrEntityName("AcademicStudent")
@Component
public class AcademicStudentByProfileEditorSearch extends ProfileBasedEntityEditorSearch {

    @Override
    protected List filterDocumentByProfileFilter(List objects, String profileSearchText) {
        AcademicStudentProfileFilter filter = new AcademicStudentProfileFilter(profileSearchText);
        return (List) objects.stream().filter(x -> filter.accept((AcademicStudent) x)).collect(Collectors.toList());
    }


}
