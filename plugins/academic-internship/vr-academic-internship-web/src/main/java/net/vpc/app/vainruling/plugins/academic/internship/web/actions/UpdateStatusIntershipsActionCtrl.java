/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.web.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.plugins.academic.internship.service.AcademicInternshipPlugin;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipStatus;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipType;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.ext.AcademicInternshipExtList;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
@ManagedBean
@Scope("session")
public class UpdateStatusIntershipsActionCtrl {

    private static final Logger log = Logger.getLogger(UpdateStatusIntershipsActionCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    @Autowired
    AcademicInternshipPlugin pi;
    @Autowired
    AcademicPlugin ap;
    private Model model = new Model();

    public void openDialog() {
        resetModel();
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/internship/updatestatusintershipsDialog", options, null);

    }

    public void reloadInternshipBoards() {
        List<SelectItem> all = new ArrayList<>();
        AcademicTeacher tt = ap.getCurrentTeacher();
        List<AcademicInternshipBoard> internshipBoards = pi.findEnabledInternshipBoardsByDepartment(tt.getDepartment().getId());
        for (AcademicInternshipBoard t : internshipBoards) {
            String n = t.getName();
            all.add(new SelectItem(String.valueOf(t.getId()), n));
        }
        getModel().setBoards(all);
        reloadInternshipStatuses();
    }

    public void reloadInternshipStatuses() {
        List<SelectItem> all = new ArrayList<>();
        if (!StringUtils.isEmpty(getModel().getSelectedBoard())) {
            int boardId = Integer.parseInt(getModel().getSelectedBoard());
            AcademicInternshipBoard board = pi.findInternshipBoard(boardId);

            for (AcademicInternshipStatus t : pi.findInternshipStatusesByType(board.getInternshipType().getId())) {
                all.add(new SelectItem(String.valueOf(t.getId()), t.getName()));
            }
        }
        getModel().setStatuses(all);
    }

    public void onUpdateBoard() {
        reloadInternshipStatuses();
        getModel().setSelectedStatusFrom(null);
        getModel().setSelectedStatusTo(null);
    }

    public void resetModel() {
        getModel().setDisabled(false);
        getModel().setMessage("");
        reloadInternshipBoards();
        getModel().setSelectedBoard(null);
        getModel().setSelectedStatusFrom(null);
        getModel().setSelectedStatusTo(null);
    }

    public void apply() {
        if (!StringUtils.isEmpty(getModel().getSelectedBoard())
                && !StringUtils.isEmpty(getModel().getSelectedStatusFrom())
                && !StringUtils.isEmpty(getModel().getSelectedStatusTo())) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            int boardId = Integer.parseInt(getModel().getSelectedBoard());
            int from = Integer.parseInt(getModel().getSelectedStatusFrom());
            int to = Integer.parseInt(getModel().getSelectedStatusTo());
            AcademicInternshipStatus toObj = pi.findInternshipStatus(to);
            List<AcademicInternship> all = pi.findInternships(-1, boardId, -1, -1, true);
            for (AcademicInternship ii : all) {
                AcademicInternshipStatus s = ii.getInternshipStatus();
                if (s != null && s.getId() == from) {
                    ii.setInternshipStatus(toObj);
                    pu.merge(ii);
                }
            }
        }
        //
        //VrApp.getBean(AcademicInternshipPlugin.class).generateInternships(getModel().getInternship(), getModel().getProfile());
    }

    public void onChangeType() {
        reloadInternshipStatuses();
        getModel().setSelectedStatusFrom(null);
        getModel().setSelectedStatusTo(null);
    }

    public void fireEventExtraDialogClosed() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public static class Model {

        private boolean disabled;
        private String message;
        private AcademicInternship internship;
        private String selectedBoard;
        private String selectedStatusFrom;
        private String selectedStatusTo;
        private List<SelectItem> boards = new ArrayList<SelectItem>();
        private List<SelectItem> statuses = new ArrayList<SelectItem>();

        public AcademicInternship getInternship() {
            return internship;
        }

        public void setInternship(AcademicInternship internship) {
            this.internship = internship;
        }

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

        public List<SelectItem> getBoards() {
            return boards;
        }

        public void setBoards(List<SelectItem> boards) {
            this.boards = boards;
        }

        public List<SelectItem> getStatuses() {
            return statuses;
        }

        public void setStatuses(List<SelectItem> statuses) {
            this.statuses = statuses;
        }

        public String getSelectedBoard() {
            return selectedBoard;
        }

        public void setSelectedBoard(String selectedBoard) {
            this.selectedBoard = selectedBoard;
        }

        public String getSelectedStatusFrom() {
            return selectedStatusFrom;
        }

        public void setSelectedStatusFrom(String selectedStatusFrom) {
            this.selectedStatusFrom = selectedStatusFrom;
        }

        public String getSelectedStatusTo() {
            return selectedStatusTo;
        }

        public void setSelectedStatusTo(String selectedStatusTo) {
            this.selectedStatusTo = selectedStatusTo;
        }

    }

    public Model getModel() {
        return model;
    }

}
