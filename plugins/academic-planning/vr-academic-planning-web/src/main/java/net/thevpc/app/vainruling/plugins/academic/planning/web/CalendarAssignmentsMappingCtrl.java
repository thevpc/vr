/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.planning.web;

import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import net.thevpc.app.vainruling.plugins.academic.planning.service.AcademicPlanningPlugin;
import net.thevpc.app.vainruling.plugins.academic.planning.service.AcademicPlanningPluginSecurity;
import net.thevpc.app.vainruling.plugins.academic.planning.service.model.CalendarAssignment;
import net.thevpc.app.vainruling.plugins.academic.planning.service.model.CalendarAssignmentsMapping;
import net.thevpc.app.vainruling.VrPage;
import net.thevpc.app.vainruling.core.service.VrLabel;
import net.thevpc.upa.UPA;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author vpc
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        //        css = "fa-table",
        //        title = "Emploi par Groupe",
        url = "modules/academic/load/calendar-assignments",
        menu = "/Education/Load",
        securityKey = AcademicPlanningPluginSecurity.RIGHT_CUSTOM_CALENDAR_ASSIGNMENTS
)
@Controller
public class CalendarAssignmentsMappingCtrl {

    @Autowired
    private AcademicPlanningPlugin plannings;
    private Model model = new Model();

    @VrOnPageLoad
    private void init() {
        getModel().setPeriods(Vr.get().entitySelectItems("AppPeriod", true, false));
        getModel().setSemesters(Vr.get().entitySelectItems("AcademicSemester", true, false));
        getModel().setClasses(Vr.get().entitySelectItems("AcademicClass", true, false));
    }

    public boolean isSimpleLabel(VrLabel lab) {
        return !isAssignButton(lab) && StringUtils.isBlank(lab.getActionType());
    }

    public boolean isLinkToEditor(VrLabel lab) {
        return !isAssignButton(lab) && !StringUtils.isBlank(lab.getActionType());
    }

    public void assignCurrent(CalendarAssignment ass) {
        if (ass.getAssignment() != null && ass.getTeacher().getTeacher() != null) {
            ass.getAssignment().setTeacher(ass.getTeacher().getTeacher());
            UPA.getPersistenceUnit().merge(ass.getAssignment());
            refresh();
        }
    }

    public boolean isAssignButton(VrLabel lab) {
        return "Assignable".equals(lab.getType());
    }

    public boolean isMissingResult() {
        Integer p = getModel().getSelectedPeriod();
        Integer s = getModel().getSelectedSemester();
        Integer c = getModel().getSelectedClass();
        if (p != null && s != null) {
            return getModel().getMapping().getElements().isEmpty();
        } else {
            return false;
        }
    }

    public void refresh() {
        Integer p = getModel().getSelectedPeriod();
        Integer s = getModel().getSelectedSemester();
        Integer c = getModel().getSelectedClass();
        if (p != null && s != null) {
            getModel().setMapping(plannings.loadAllAssignments(p, s, c));
        } else {
            getModel().setMapping(new CalendarAssignmentsMapping());
        }
    }

    public Model getModel() {
        return model;
    }

    public static class Model {

        private CalendarAssignmentsMapping mapping = new CalendarAssignmentsMapping();
        private List<SelectItem> periods = new ArrayList<SelectItem>();
        private List<SelectItem> semesters = new ArrayList<SelectItem>();
        private List<SelectItem> classes = new ArrayList<SelectItem>();

        private Integer selectedPeriod;
        private Integer selectedSemester;
        private Integer selectedClass;

        public CalendarAssignmentsMapping getMapping() {
            return mapping;
        }

        public void setMapping(CalendarAssignmentsMapping mapping) {
            this.mapping = mapping;
        }

        public List<SelectItem> getPeriods() {
            return periods;
        }

        public void setPeriods(List<SelectItem> periods) {
            this.periods = periods;
        }

        public List<SelectItem> getSemesters() {
            return semesters;
        }

        public void setSemesters(List<SelectItem> semesters) {
            this.semesters = semesters;
        }

        public List<SelectItem> getClasses() {
            return classes;
        }

        public void setClasses(List<SelectItem> classes) {
            this.classes = classes;
        }

        public Integer getSelectedPeriod() {
            return selectedPeriod;
        }

        public void setSelectedPeriod(Integer selectedPeriod) {
            this.selectedPeriod = selectedPeriod;
        }

        public Integer getSelectedSemester() {
            return selectedSemester;
        }

        public void setSelectedSemester(Integer selectedSemester) {
            this.selectedSemester = selectedSemester;
        }

        public Integer getSelectedClass() {
            return selectedClass;
        }

        public void setSelectedClass(Integer selectedClass) {
            this.selectedClass = selectedClass;
        }

    }
}
