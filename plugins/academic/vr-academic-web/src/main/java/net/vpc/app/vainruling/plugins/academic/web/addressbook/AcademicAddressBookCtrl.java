/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.addressbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicFormerStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.content.AcademicTeacherCV;
import net.vpc.app.vainruling.plugins.academic.web.AcademicCtrlUtils;
import net.vpc.upa.impl.util.Strings;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Address Book"
)
@ManagedBean
public class AcademicAddressBookCtrl {

    private Model model = new Model();

    public static class Contact {

        private String name;
        private String urlCommand;
        private String urlArgs;
        private String photo;
        private List<String> titles = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrlCommand() {
            return urlCommand;
        }

        public void setUrlCommand(String urlCommand) {
            this.urlCommand = urlCommand;
        }

        public String getUrlArgs() {
            return urlArgs;
        }

        public void setUrlArgs(String urlArgs) {
            this.urlArgs = urlArgs;
        }

        public List<String> getTitles() {
            return titles;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

    }

    public void onSearch() {
        String q = getModel().getQuery();
        if (q == null) {
            q = "";
        }
        q = q.trim();
        String qt = getModel().getQueryType();
        List<Contact> cc = new ArrayList<>();
        if (!Strings.isNullOrEmpty(qt)) {
            if (q.length() > 0) {
                if (qt.equals("teachers")) {
                    AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
                    for (AcademicTeacher t : ap.findEnabledTeachers()) {
                        if (t.getName().toLowerCase().contains(q.toLowerCase())) {
                            Contact ct = new Contact();
                            ct.setName(t.getName());
                            if (t.getDepartment() != null) {
                                ct.getTitles().add(t.getDepartment().getName());
                            }
                            ct.getTitles().add(t.getDegree().getName() + ", " + t.getSituation().getName());

                            if (!Strings.isNullOrEmpty(t.getDiscipline())) {
                                ct.getTitles().add(t.getDiscipline());
                            }

                            AcademicTeacherCV cv = ap.findOrCreateAcademicTeacherCV(t.getId());
                            if (!Strings.isNullOrEmpty(cv.getTitle1())) {
                                ct.getTitles().add(cv.getTitle1());
                            }
                            ct.setUrlCommand("teacherCurriculum");
                            ct.setUrlArgs("{teacherId:'" + t.getId() + "'}");
                            ct.setPhoto(findTeacherPhoto(t.getId()));
                            cc.add(ct);
                        }
                    }
                } else if (qt.endsWith("students")) {
                    for (AcademicStudent t : VrApp.getBean(AcademicPlugin.class).findStudents()) {
                        if (t.getName().toLowerCase().contains(q.toLowerCase())) {
                            Contact ct = new Contact();
                            ct.setName(t.getName());
                            if (t.getFirstSubscription() != null) {
                                ct.getTitles().add("sub. " + t.getFirstSubscription().getName());
                            }
                            if (t.getLastClass1() != null) {
                                ct.getTitles().add(t.getLastClass1().getName());
                            }
                            if (t.getLastClass2() != null) {
                                ct.getTitles().add(t.getLastClass2().getName());
                            }
                            if (t.getLastClass3() != null) {
                                ct.getTitles().add(t.getLastClass3().getName());
                            }
                            ct.setUrlCommand("");
                            cc.add(ct);
                        }
                    }
                } else if (qt.endsWith("graduated")) {
                    for (AcademicFormerStudent t : VrApp.getBean(AcademicPlugin.class).findGraduatedStudents()) {
                        if (t.getName().toLowerCase().contains(q.toLowerCase())) {
                            Contact ct = new Contact();
                            ct.setName(t.getName());
                            if (t.isGraduated()) {
                                ct.getTitles().add("Graduated on " + t.getGraduationDate());
                            } else {
                                ct.getTitles().add("Eliminated");
                            }
                            if (t.getFirstSubscription() != null) {
                                ct.getTitles().add("sub. " + t.getFirstSubscription().getName());
                            }
                            if (t.getLastClass1() != null) {
                                ct.getTitles().add(t.getLastClass1().getName());
                            }
                            if (t.getLastClass2() != null) {
                                ct.getTitles().add(t.getLastClass2().getName());
                            }
                            if (t.getLastClass3() != null) {
                                ct.getTitles().add(t.getLastClass3().getName());
                            }
                            if (t.getLastJobPosition() != null) {
                                ct.getTitles().add(t.getLastJobPosition());
                            }
                            if (t.getLastJobCompany() != null) {
                                ct.getTitles().add("@ " + t.getLastJobCompany().getName());
                            }
                            ct.setUrlCommand("");
                            cc.add(ct);
                        }
                    }
                }
            }
        }
        getModel().setLastQuery(getModel().getQuery());
        getModel().setLastQueryType(getModel().getQueryType());
        getModel().setList(cc);
    }

     public String findTeacherPhoto(int id) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = ap.findTeacher(id);
        String photo = t == null ? null : AcademicCtrlUtils.getTeacherAppWebPath(t.getId(), "WebSite/photo.jpg");
        return photo;
    }
     
    public class Model {

        private String lastQuery;
        private String lastQueryType;
        private String query;
        private String queryType = "teachers";
        private List<Contact> list;

        public String getLastQuery() {
            return lastQuery;
        }

        public void setLastQuery(String lastQuery) {
            this.lastQuery = lastQuery;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getQueryType() {
            return queryType;
        }

        public void setQueryType(String queryType) {
            this.queryType = queryType;
        }

        public List<Contact> getList() {
            synchronized (this) {
                if (list == null || lastQuery == null || lastQueryType == null
                        || !Objects.equals(lastQuery, query)
                        || !Objects.equals(lastQueryType, queryType)) {
                    onSearch();
                    //rebuild
                }
            }
            return list;
        }

        public void setList(List<Contact> list) {
            this.list = list;
        }

        public String getLastQueryType() {
            return lastQueryType;
        }

        public void setLastQueryType(String lastQueryType) {
            this.lastQueryType = lastQueryType;
        }

    }

    public Model getModel() {
        return model;
    }

}
