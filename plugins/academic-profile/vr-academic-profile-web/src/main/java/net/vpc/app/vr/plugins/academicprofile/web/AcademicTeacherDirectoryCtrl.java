package net.vpc.app.vr.plugins.academicprofile.web;

import java.util.ArrayList;
import java.util.List;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCV;
import org.springframework.beans.factory.annotation.Autowired;

/*@VrController(
        breadcrumb = {
            @UPathItem(title = "Address book", css = "fa-dashboard", ctrl = "")},
        //        css = "fa-table",
        title = "Teacher Directory",
        menu = "/Config",
        url = "modules/academic/addressbook/teacher-directory"
)*/
public class AcademicTeacherDirectoryCtrl {
    
    @Autowired
    AcademicPlugin ap;
    
    @OnPageLoad
    public void onPageLoad() {
        //getModel().setTeacherList(ap.findTeachers());
        getModel().setTeacherContactList(loadList());
    }
    
    private Model model = new Model();

    public Model getModel() {
        return model;
    }
    
    public List<TeacherContact> loadList() {
        List<TeacherContact> list = new ArrayList<>();
        
        return list;
    }
    
    public String findTeacherPhoto(int id) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = ap.findTeacher(id);
        return Vr.get().getUserPhoto(t.getUser()==null?-1:t.getUser().getId());
    }
    
    public static class TeacherContact {

        private AcademicTeacher academicTeacher;
        private String photo;
        private AcademicTeacherCV academicTeacherCV;

        public AcademicTeacher getAcademicTeacher() {
            return academicTeacher;
        }

        public void setAcademicTeacher(AcademicTeacher academicTeacher) {
            this.academicTeacher = academicTeacher;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public AcademicTeacherCV getAcademicTeacherCV() {
            return academicTeacherCV;
        }

        public void setAcademicTeacherCV(AcademicTeacherCV academicTeacherCV) {
            this.academicTeacherCV = academicTeacherCV;
        }       
    }
    
    public class Model {
    
        private List<TeacherContact> teacherContactList = new ArrayList<>();

        public List<TeacherContact> getTeacherContactList() {
            return teacherContactList;
        }

        public void setTeacherContactList(List<TeacherContact> teacherContactList) {
            this.teacherContactList = teacherContactList;
        }

    }
    
}
