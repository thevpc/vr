package net.vpc.app.vainruling.plugins.academic.planning.web;

///*
// * To change this license header, choose License Headers in Project Properties.
// *
// * and open the template in the editor.
// */
//package net.vpc.app.vainruling.plugins.academic.web.planning;
//
//import java.util.ArrayList;
//import java.util.List;
//import javax.faces.bean.ManagedBean;
//import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
//import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
//import net.vpc.app.vainruling.api.VrApp;
//import net.vpc.app.vainruling.core.service.model.AppUser;
//import net.vpc.app.vainruling.core.service.security.UserSession;
//import net.vpc.app.vainruling.core.web.OnPageLoad;
//import net.vpc.app.vainruling.core.web.UCtrl;
//import net.vpc.app.vainruling.core.web.UPathItem;
//import net.vpc.app.vainruling.plugins.academic.service.model.PlanningData;
//import net.vpc.app.vainruling.plugins.academic.service.model.PlanningDay;
//import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
//
///**
// *
// * @author taha.bensalah@gmail.com
// */
////@UCtrl(
////        breadcrumb = {
////            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
////        css = "fa-table",
////        title = "Mon Emploi du Temps (simple)",
////        url = "modules/academic/myplanning",
////        menu = "/Education"
////        ,securityKey = "Custom.Education.MyPlanning"
////)
//@ManagedBean
//@Deprecated
//public class MyPlanningCtrl extends AbstractPlanningCtrl {
//
//    public MyPlanningCtrl() {
//        model = new Model();
//    }
//
//    @OnPageLoad
//    public void onPageLoad() {
//        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
//        PlanningData plannings = null;
//        UserSession sm = VrApp.getBean(UserSession.class);
//        AppUser user = (sm == null) ? null : sm.getUser();
//        if (user != null) {
//            plannings = a.loadUserPlanning(user.getId());
//            if (plannings != null) {
//                updateModel(plannings.getDays());
//                return;
//            }
//        }
//        AcademicTeacher t = a.getCurrentTeacher();
//        if (t != null) {
//            plannings = a.loadTeacherPlanning(t.getId());
//            if (plannings != null) {
//                updateModel(plannings.getDays());
//                return;
//            }
//        }
//
//        AcademicStudent st = a.getCurrentStudent();
//        if (st != null) {
//            plannings = a.loadStudentPlanning(st.getId());
//            if (plannings != null) {
//                updateModel(plannings.getDays());
//                return;
//            }
//        }
//
//        updateModel(new ArrayList<PlanningDay>());
//    }
//
//    public void onRefresh() {
//        onPageLoad();
//    }
//
//}
