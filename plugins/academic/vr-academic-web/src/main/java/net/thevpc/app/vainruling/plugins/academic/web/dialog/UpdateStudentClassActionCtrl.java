/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.web.dialog;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.core.web.jsf.DialogBuilder;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicFormerStudent;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudentStage;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.util.Convert;
import net.thevpc.common.util.IntegerParserConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage
public class UpdateStudentClassActionCtrl {

    public static final Logger log = Logger.getLogger(UpdateStudentClassActionCtrl.class.getName());
    @Autowired
    AcademicPlugin ap;
    @Autowired
    private CorePlugin core;
    private Model model = new Model();

    public void openDialog(List<String> itemIds) {
        resetModel();
        getModel().setSelectionIdList(itemIds == null ? new ArrayList<String>() : itemIds);
        getModel().setUserSelectedOnly(!getModel().getSelectionIdList().isEmpty());
        new DialogBuilder("/modules/academic/dialog/update-student-class-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();

    }

    public void reloadData() {
        List<SelectItem> items = new ArrayList<>();

        List<AcademicClass> internshipBoards = ap.findAcademicClasses();
        for (AcademicClass t : internshipBoards) {
            String n = t.getName();
            items.add(FacesUtils.createSelectItem(String.valueOf(t.getId()), n));
        }
        getModel().setClasses(items);

        items = new ArrayList<>();
        List<AppPeriod> periods = core.findNavigatablePeriods();
        for (AppPeriod t : periods) {
            String n = t.getName();
            items.add(FacesUtils.createSelectItem(String.valueOf(t.getId()), n));
        }
        getModel().setPeriods(items);

    }


    public void resetModel() {
        getModel().setDisabled(false);
        getModel().setMessage("");
        reloadData();
        getModel().setSelectedClassFrom(null);
        getModel().setSelectedClassTo(null);
        getModel().setSelectedPeriod(null);
        getModel().setUserSelectedOnly(false);
    }

    public void onGraduated() {
        if (    //xor between board and group
                (getModel().isUserSelectedOnly() || !StringUtils.isBlank(getModel().getSelectedClassFrom()))
                        && !StringUtils.isBlank(getModel().getSelectedPeriod())
                ) {
            getModel().setMessage("");
            int periodId = Convert.toInt(getModel().getSelectedPeriod(), IntegerParserConfig.LENIENT_F);
            boolean userSelectedOnly = getModel().isUserSelectedOnly();
            if (userSelectedOnly && periodId >= 0) {
                AppPeriod period = core.findPeriod(periodId);
                if (period != null) {
                    try {
                        for (String studentIdStr : getModel().getSelectionIdList()) {
                            int studentId = Convert.toInt(studentIdStr, IntegerParserConfig.LENIENT_F);
                            AcademicStudent st = ap.findStudent(studentId);
                            st.setLastSubscription(period);
                            st.setStage(AcademicStudentStage.GRADUATED);
                            core.save(null,st);
                        }
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Error", ex);
                        getModel().setMessage(ex.getMessage());
                    }
                }
            } else {
                int fromId = Convert.toInt(getModel().getSelectedClassFrom(), IntegerParserConfig.LENIENT_F);
                try {
                    ap.updateStudentClassByClass(getModel().getClassNumber(), fromId, periodId);
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    getModel().setMessage(ex.getMessage());
                }
            }
        }else{
            getModel().setMessage("Merci de faire votre selection");
        }
        if(StringUtils.isBlank(getModel().getMessage())) {
            fireEventExtraDialogClosed();
        }
    }

    public void onEliminated() {
        if (    //xor between board and group
                (getModel().isUserSelectedOnly() || !StringUtils.isBlank(getModel().getSelectedClassFrom()))
                        && !StringUtils.isBlank(getModel().getSelectedPeriod())
                ) {
            getModel().setMessage("");
            int periodId = Convert.toInt(getModel().getSelectedPeriod(), IntegerParserConfig.LENIENT_F);
            boolean userSelectedOnly = getModel().isUserSelectedOnly();
            if (userSelectedOnly && periodId >= 0) {
                AppPeriod period = core.findPeriod(periodId);
                if (period != null) {
                    try {
                        for (String studentIdStr : getModel().getSelectionIdList()) {
                            int studentId = Convert.toInt(studentIdStr, IntegerParserConfig.LENIENT_F);
                            AcademicStudent st = ap.findStudent(studentId);
                            st.setLastSubscription(period);
                            st.setStage(AcademicStudentStage.ELIMINATED);
                            core.save(null,st);
                            AcademicFormerStudent formerStudent = ap.findFormerStudent(studentId);
                            formerStudent.setEliminationReason(getModel().getEliminationReason());
                            core.save(null,formerStudent);
                        }
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Error", ex);
                        getModel().setMessage(ex.getMessage());
                    }
                }
            } else {
                int fromId = Convert.toInt(getModel().getSelectedClassFrom(), IntegerParserConfig.LENIENT_F);
                try {
                    ap.updateStudentClassByClass(getModel().getClassNumber(), fromId, periodId);
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    getModel().setMessage(ex.getMessage());
                }
            }
        }else{
            getModel().setMessage("Merci de faire votre selection");
        }
        if(StringUtils.isBlank(getModel().getMessage())) {
            fireEventExtraDialogClosed();
        }
    }

    public void onFailure() {
        if (    //xor between board and group
                (getModel().isUserSelectedOnly() || !StringUtils.isBlank(getModel().getSelectedClassFrom()))
                        && !StringUtils.isBlank(getModel().getSelectedPeriod())
                ) {
            getModel().setMessage("");
            int periodId = Convert.toInt(getModel().getSelectedPeriod(), IntegerParserConfig.LENIENT_F);
            boolean userSelectedOnly = getModel().isUserSelectedOnly();
            if (userSelectedOnly && periodId >= 0) {
                AppPeriod period = core.findPeriod(periodId);
                if (period != null) {
                    try {
                        for (String studentIdStr : getModel().getSelectionIdList()) {
                            int studentId = Convert.toInt(studentIdStr, IntegerParserConfig.LENIENT_F);
                            AcademicStudent st = ap.findStudent(studentId);
                            st.setFailureCount(st.getFailureCount()+1);
                            core.save(null,st);
                        }
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Error", ex);
                        getModel().setMessage(ex.getMessage());
                    }
                }
            } else {
                int fromId = Convert.toInt(getModel().getSelectedClassFrom(), IntegerParserConfig.LENIENT_F);
                try {
                    for (AcademicStudent st : ap.findStudentsByClass(fromId, getModel().getClassNumber())) {
                        st.setFailureCount(st.getFailureCount()+1);
                        core.save(null,st);
                    }
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    getModel().setMessage(ex.getMessage());
                }
            }
        }else{
            getModel().setMessage("Merci de faire votre selection");
        }
        if(StringUtils.isBlank(getModel().getMessage())) {
            fireEventExtraDialogClosed();
        }
    }

    public void onFilterChanged() {

    }
    public void onChangeClass() {
        if (    //xor between board and group
                (getModel().isUserSelectedOnly() || !StringUtils.isBlank(getModel().getSelectedClassFrom()))
                        && !StringUtils.isBlank(getModel().getSelectedClassTo())
                        && !StringUtils.isBlank(getModel().getSelectedPeriod())
                ) {
            getModel().setMessage("");
            int periodId = Convert.toInt(getModel().getSelectedPeriod(), IntegerParserConfig.LENIENT_F);
            int toId = Convert.toInt(getModel().getSelectedClassTo(), IntegerParserConfig.LENIENT_F);
            boolean userSelectedOnly = getModel().isUserSelectedOnly();
            if (userSelectedOnly && toId >= 0 && periodId>=0) {
                AppPeriod period = core.findPeriod(periodId);
                AcademicClass cls = ap.findAcademicClass(toId);
                if (cls != null && period!=null) {
                    try {
                        for (String studentIdStr : getModel().getSelectionIdList()) {
                            int studentId = Convert.toInt(studentIdStr, IntegerParserConfig.LENIENT_F);
                            ap.updateStudentClass(studentId, getModel().getClassNumber(), cls.getId());
                            AcademicStudent st = ap.findStudent(studentId);
                            st.setLastSubscription(period);
                            core.save(null,st);
                        }
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Error", ex);
                        getModel().setMessage(ex.getMessage());
                    }
                }
            } else {
                int fromId = Convert.toInt(getModel().getSelectedClassFrom(), IntegerParserConfig.LENIENT_F);
                try {
                    ap.updateStudentClassByClass(getModel().getClassNumber(), fromId, toId);
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    getModel().setMessage(ex.getMessage());
                }
            }
        }else{
            getModel().setMessage("Merci de faire votre selection");
        }
        if(StringUtils.isBlank(getModel().getMessage())) {
            fireEventExtraDialogClosed();
        }
        //
        //VrApp.getBean(AcademicPlugin.class).generateInternships(getModel().getInternship(), getModel().getProfile());
    }

    public void fireEventExtraDialogClosed() {
        DialogBuilder.closeCurrent();
    }

    public Model getModel() {
        return model;
    }

    public static class Model {

        private boolean disabled;
        private String message;
        private int classNumber = 1;
        private List<String> selectionIdList;
        private String selectedClassFrom;
        private String selectedClassTo;
        private String selectedPeriod;
        private String eliminationReason;
        private boolean userSelectedOnly;
        private List<SelectItem> classes = new ArrayList<SelectItem>();
        private List<SelectItem> periods = new ArrayList<SelectItem>();

        public boolean isDisabled() {
            return disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getSelectionIdList() {
            return selectionIdList;
        }

        public Model setSelectionIdList(List<String> selectionIdList) {
            this.selectionIdList = selectionIdList;
            return this;
        }

        public String getSelectedClassFrom() {
            return selectedClassFrom;
        }

        public void setSelectedClassFrom(String selectedClassFrom) {
            this.selectedClassFrom = selectedClassFrom;
        }

        public String getSelectedClassTo() {
            return selectedClassTo;
        }

        public void setSelectedClassTo(String selectedClassTo) {
            this.selectedClassTo = selectedClassTo;
        }

        public boolean isUserSelectedOnly() {
            return userSelectedOnly;
        }

        public void setUserSelectedOnly(boolean userSelectedOnly) {
            this.userSelectedOnly = userSelectedOnly;
        }

        public List<SelectItem> getClasses() {
            return classes;
        }

        public void setClasses(List<SelectItem> classes) {
            this.classes = classes;
        }

        public int getClassNumber() {
            return classNumber;
        }

        public void setClassNumber(int classNumber) {
            this.classNumber = classNumber;
        }

        public String getSelectedPeriod() {
            return selectedPeriod;
        }

        public void setSelectedPeriod(String selectedPeriod) {
            this.selectedPeriod = selectedPeriod;
        }

        public String getEliminationReason() {
            return eliminationReason;
        }

        public void setEliminationReason(String eliminationReason) {
            this.eliminationReason = eliminationReason;
        }

        public List<SelectItem> getPeriods() {
            return periods;
        }

        public void setPeriods(List<SelectItem> periods) {
            this.periods = periods;
        }

    }

}
