/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vr.plugins.academicprofile.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicFormerStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudentStage;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vr.plugins.academicprofile.service.AcademicProfilePlugin;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCV;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.filters.ObjectFilter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")}
//        , css = "fa-table"
//        , title = "Address Book"
)
public class AcademicAddressBookCtrl {
    @Autowired
    AcademicPlugin ap;
    @Autowired
    AcademicProfilePlugin apr;

    private Model model = new Model();

    public String getValidString(String en, String fr, String ar) {
        return VrUtils.getValidString(getValidLocaleCode(), en, fr, ar);
    }

    public String getValidLocaleCode() {
        String loc = getModel().getLocale();
        if (loc == null) {
            loc = "en";
        }
        if (loc.contains("ar")) {
            return "ar";
        }
        if (loc.contains("fr")) {
            return "fr";
        }
        return "en";
    }

    @OnPageLoad
    public String onPageLoad() {
        onUpdatePermanentList();
        return "ignore-me";
    }

    public void onUpdatePermanentList() {
        List<Contact> teachers = loadList(new ObjectFilter<Object>() {
            @Override
            public boolean accept(Object obj) {
                if (obj instanceof AcademicTeacher) {
                    AcademicTeacher t = (AcademicTeacher) obj;
                    if (t.getSituation() == null) {
                        return false;
                    }
                    if (t.getDepartment() == null) {
                        return false;
                    }
                    return "II".equals(t.getDepartment().getCode()) && "Permanent".equals(t.getSituation().getName());
                }
                return false;
            }
        }, "teachers");
        Collections.shuffle(teachers);
        getModel().setPermanentList(teachers);
    }

    public void onSearch() {
        String query = getModel().getQuery();
        if (query == null) {
            query = "";
        }
        query = query.trim();
        final String fquery = query;
        String qt = getModel().getQueryType();
        getModel().setLastQuery(getModel().getQuery());
        getModel().setLastQueryType(getModel().getQueryType());
        getModel().setList(loadList(new ObjectFilter<Object>() {
            @Override
            public boolean accept(Object obj) {
                if (fquery.length() == 0) {
                    return true;
                }
                if (obj instanceof AcademicTeacher) {
                    AcademicTeacher t = (AcademicTeacher) obj;
                    return t.getContact().getFullName().toLowerCase().contains(fquery.toLowerCase());
                }
                if (obj instanceof AcademicStudent) {
                    AcademicStudent t = (AcademicStudent) obj;
                    return t.getContact().getFullName().toLowerCase().contains(fquery.toLowerCase());
                }
                if (obj instanceof AcademicFormerStudent) {
                    AcademicFormerStudent t = (AcademicFormerStudent) obj;
                    return t.getStudent().getContact().getFullName().toLowerCase().contains(fquery.toLowerCase());
                }
                return false;
            }
        }, qt));
    }

    public List<Contact> loadList(ObjectFilter<Object> query, String qt) {
        List<Contact> cc = new ArrayList<>();
        if (!StringUtils.isEmpty(qt)) {
            if (true) //q.length() > 0
            {
                if (qt.equals("teachers")) {
                    CorePlugin core = VrApp.getBean(CorePlugin.class);
                    AppPeriod mainPeriod = core.getCurrentPeriod();

                    for (AcademicTeacher t : ap.findEnabledTeachers(mainPeriod.getId())) {
                        if (query.accept(t)) {
                            Contact ct = new Contact();
                            ct.setName(t.getContact().getFullName());
                            if (t.getDepartment() != null) {
                                ct.getTitles().add("Dept. " + getValidString(t.getDepartment().getName(), t.getDepartment().getName2(), t.getDepartment().getName3()));
                            }
                            ct.getTitles().add((t.getDegree() == null ? "?" : getValidString(t.getDegree().getName(), t.getDegree().getName2(), t.getDegree().getName3())) + ", "
                                    + (t.getSituation() == null ? "?" : getValidString(t.getSituation().getName(), t.getSituation().getName2(), t.getSituation().getName3())));

                            String disc = ap.formatDisciplinesForLocale(t.getDiscipline(), getValidLocaleCode());

                            if (!StringUtils.isEmpty(disc)) {
                                ct.getTitles().add(disc);
                            }

                            AcademicTeacherCV cv = apr.findOrCreateAcademicTeacherCV(t.getId());
                            String t1 = getValidString(cv.getTitle1(), cv.getTitle2(), cv.getTitle3());
                            if (!StringUtils.isEmpty(t1)) {
                                ct.getTitles().add(t1);
                            }
                            if (!StringUtils.isEmpty(t.getContact().getEmail())) {
                                ct.getTitles().add(t.getContact().getEmail());
                            }
                            ct.setUrlCommand("teacherCurriculum");
                            ct.setUrlArgs("{teacherId:'" + t.getId() + "'}");
                            ct.setPhoto(findTeacherPhoto(t.getId()));
                            cc.add(ct);
                        }
                    }
                } else if (qt.endsWith("students")) {
                    for (AcademicStudent t : VrApp.getBean(AcademicPlugin.class).findStudents()) {
                        if (query.accept(t)) {
                            Contact ct = new Contact();
                            ct.setName(t.getContact().getFullName());
                            if (t.getFirstSubscription() != null) {
                                ct.getTitles().add(getValidString(
                                        "sub. " + t.getFirstSubscription().getName(),
                                        "inscr. " + t.getFirstSubscription().getName(),
                                        null
                                ));
                            }
                            if (t.getLastClass1() != null) {
                                ct.getTitles().add(
                                        getValidString(t.getLastClass1().getName(), t.getLastClass1().getName2(), null)
                                );
                            }
                            if (t.getLastClass2() != null) {
                                ct.getTitles().add(
                                        getValidString(t.getLastClass2().getName(),
                                                t.getLastClass2().getName2(),
                                                null
                                        ));
                            }
                            if (t.getLastClass3() != null) {
                                ct.getTitles().add(
                                        getValidString(
                                                t.getLastClass3().getName(), t.getLastClass3().getName2(), null
                                        ));
                            }
                            if (!StringUtils.isEmpty(t.getContact().getEmail())) {
                                ct.getTitles().add(t.getContact().getEmail());
                            }
                            ct.setUrlCommand("");
                            cc.add(ct);
                        }
                    }
                } else if (qt.endsWith("graduated")) {
                    for (AcademicFormerStudent t : VrApp.getBean(AcademicPlugin.class).findGraduatedStudents()) {
                        if (query.accept(t)) {
                            Contact ct = new Contact();
                            ct.setName(t.getStudent().getContact().getFullName());
                            if (t.getStudent().getStage() == AcademicStudentStage.GRADUATED) {
                                ct.getTitles().add("Graduated on " + t.getGraduationDate());
                            } else {
                                ct.getTitles().add("Eliminated");
                            }
                            if (t.getStudent().getFirstSubscription() != null) {
                                ct.getTitles().add("sub. " + t.getStudent().getContact().getFullName());
                            }
//                            if (t.getLastClass1() != null) {
//                                ct.getTitles().add(t.getLastClass1().getName());
//                            }
//                            if (t.getLastClass2() != null) {
//                                ct.getTitles().add(t.getLastClass2().getName());
//                            }
//                            if (t.getLastClass3() != null) {
//                                ct.getTitles().add(t.getLastClass3().getName());
//                            }
                            if (t.getLastJobPosition() != null) {
                                ct.getTitles().add(t.getLastJobPosition());
                            }
                            if (t.getLastJobCompany() != null) {
                                ct.getTitles().add("@ " + t.getLastJobCompany().getName());
                            }
                            if (!StringUtils.isEmpty(t.getStudent().getContact().getEmail())) {
                                ct.getTitles().add(t.getStudent().getContact().getEmail());
                            }
                            ct.setUrlCommand("");
                            cc.add(ct);
                        }
                    }
                }
            }
        }
        return cc;
    }

    public String findTeacherPhoto(int id) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = ap.findTeacher(id);
        return Vr.get().getUserPhoto(t.getUser()==null?-1:t.getUser().getId());
    }

    public Model getModel() {
        return model;
    }

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

    public class Model {

        private String locale;
        private String lastQuery;
        private String lastQueryType;
        private String query;
        private String queryType = "teachers";
        private List<Contact> list = new ArrayList<>();
        private List<Contact> permanentList = new ArrayList<>();

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

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public List<Contact> getList() {
//            synchronized (this) {
//                if (list == null || lastQuery == null || lastQueryType == null
//                        || !Objects.equals(lastQuery, query)
//                        || !Objects.equals(lastQueryType, queryType)) {
//                    onSearch();
//                    //rebuild
//                }
//            }
            return list;
        }

        public void setList(List<Contact> list) {
            this.list = list;
        }

        public List<Contact> getPermanentList() {
            return permanentList;
        }

        public void setPermanentList(List<Contact> permanentList) {
            this.permanentList = permanentList;
        }

        public String getLastQueryType() {
            return lastQueryType;
        }

        public void setLastQueryType(String lastQueryType) {
            this.lastQueryType = lastQueryType;
        }

    }

}
