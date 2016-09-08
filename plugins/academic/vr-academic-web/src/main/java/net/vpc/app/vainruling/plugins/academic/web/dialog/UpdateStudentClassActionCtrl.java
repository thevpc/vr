/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl
public class UpdateStudentClassActionCtrl {

    public static final Logger log = Logger.getLogger(UpdateStudentClassActionCtrl.class.getName());
    @Autowired
    AcademicPlugin pi;
    @Autowired
    AcademicPlugin ap;
    @Autowired
    private CorePlugin core;
    private Model model = new Model();

    public void openDialog(List<String> itemIds) {
        resetModel();
        getModel().setSelectionIdList(itemIds == null ? new ArrayList<String>() : itemIds);
        getModel().setUserSelectedOnly(!getModel().getSelectionIdList().isEmpty());
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/dialog/update-student-class-dialog", options, null);

    }

    public void reloadStudentClasses() {
        List<SelectItem> items = new ArrayList<>();
        AcademicTeacher tt = ap.getCurrentTeacher();

        List<AcademicClass> internshipBoards = pi.findAcademicClasses();
        for (AcademicClass t : internshipBoards) {
            String n = t.getName();
            items.add(new SelectItem(String.valueOf(t.getId()), n));
        }
        getModel().setClasses(items);

    }


    public void resetModel() {
        getModel().setDisabled(false);
        getModel().setMessage("");
        reloadStudentClasses();
        getModel().setSelectedClassFrom(null);
        getModel().setSelectedClassTo(null);
        getModel().setUserSelectedOnly(false);
    }

    public void apply() {
        if (    //xor between board and group
                (getModel().isUserSelectedOnly() || !StringUtils.isEmpty(getModel().getSelectedClassFrom()))
                        && !StringUtils.isEmpty(getModel().getSelectedClassTo())) {
            getModel().setMessage("");
            int toId = Convert.toInteger(getModel().getSelectedClassTo(), IntegerParserConfig.LENIENT_F);
            boolean userSelectedOnly = getModel().isUserSelectedOnly();
            if (userSelectedOnly && toId >= 0) {
                AcademicClass cls = ap.findAcademicClass(toId);
                if (cls != null) {
                    try {
                        for (String studentIdStr : getModel().getSelectionIdList()) {
                            int studentId = Convert.toInteger(studentIdStr, IntegerParserConfig.LENIENT_F);
                            ap.updateStudentClass(studentId, getModel().getClassNumber(), cls.getId());
                        }
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Error", ex);
                        getModel().setMessage(ex.getMessage());
                    }
                }
            } else {
                int fromId = Convert.toInteger(getModel().getSelectedClassFrom(), IntegerParserConfig.LENIENT_F);
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
        if(StringUtils.isEmpty(getModel().getMessage())) {
            fireEventExtraDialogClosed();
        }
        //
        //VrApp.getBean(AcademicPlugin.class).generateInternships(getModel().getInternship(), getModel().getProfile());
    }

    public void fireEventExtraDialogClosed() {
        RequestContext.getCurrentInstance().closeDialog(null);
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
        private boolean userSelectedOnly;
        private List<SelectItem> classes = new ArrayList<SelectItem>();

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
    }

}
