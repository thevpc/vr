/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.internship.actions;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.jsf.DialogBuilder;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.config.AcademicInternshipStatus;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.config.AcademicInternshipType;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternshipGroup;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController
public class UpdateStatusInternshipsActionCtrl {

    private static final Logger log = Logger.getLogger(UpdateStatusInternshipsActionCtrl.class.getName());
    @Autowired
    AcademicPlugin pi;
    @Autowired
    AcademicPlugin ap;
    @Autowired
    private CorePlugin core;
    private Model model = new Model();

    public void openDialog(List<String> itemIds) {
        getModel().setSelectionIdList(itemIds == null ? new ArrayList<String>() : itemIds);
        getModel().setUserSelectedOnly(getModel().getSelectionIdList().size() > 0);
        onUserFilterChanged();
        new DialogBuilder("/modules/academic/internship/update-status-interships-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();

    }

    public void reloadInternshipBoards() {
        List<SelectItem> internshipBoardsItems = new ArrayList<>();
        AcademicTeacher tt = ap.getCurrentTeacher();

        List<AcademicInternshipBoard> internshipBoards = pi.findEnabledInternshipBoardsByDepartment(-1, tt.getDepartment().getId(), true);
        for (AcademicInternshipBoard t : internshipBoards) {
            String n = t.getName();
            internshipBoardsItems.add(FacesUtils.createSelectItem(String.valueOf(t.getId()), n));
        }
        getModel().setBoards(internshipBoardsItems);

        reloadInternshipStatuses();
    }

    public void reloadInternshipGroups() {
        List<SelectItem> internshipGroupsItems = new ArrayList<>();
        AcademicTeacher tt = ap.getCurrentTeacher();
        List<AcademicInternshipGroup> internshipGroups = pi.findEnabledInternshipGroupsByDepartment(tt.getDepartment().getId());
        for (AcademicInternshipGroup t : internshipGroups) {
            String n = t.getName();
            internshipGroupsItems.add(FacesUtils.createSelectItem(String.valueOf(t.getId()), n));
        }
        getModel().setGroups(internshipGroupsItems);
    }

    public void reloadInternshipTypes() {
        List<SelectItem> list = new ArrayList<>();
        List<AcademicInternshipType> internshipGroups = pi.findInternshipTypes();
        for (AcademicInternshipType t : internshipGroups) {
            String n = t.getName();
            list.add(FacesUtils.createSelectItem(String.valueOf(t.getId()), n));
        }
        getModel().setInternshipTypes(list);
    }

    public void reloadInternshipStatuses() {
        List<SelectItem> all = new ArrayList<>();
        if (getModel().isUserSelectedOnly()) {
            if (!StringUtils.isEmpty(getModel().getSelectedInternshipType())) {
                int typeId = Integer.parseInt(getModel().getSelectedInternshipType());
                for (AcademicInternshipStatus t : pi.findInternshipStatusesByType(typeId)) {
                    all.add(FacesUtils.createSelectItem(String.valueOf(t.getId()), t.getName()));
                }
            }
        } else {
            if (!StringUtils.isEmpty(getModel().getSelectedBoard())) {
                int boardId = Integer.parseInt(getModel().getSelectedBoard());
                AcademicInternshipBoard board = pi.findInternshipBoard(boardId);

                for (AcademicInternshipStatus t : pi.findInternshipStatusesByType(board.getInternshipType().getId())) {
                    all.add(FacesUtils.createSelectItem(String.valueOf(t.getId()), t.getName()));
                }
            }
        }
        getModel().setStatuses(all);
    }

    public void onUserFilterChanged() {
        getModel().setDisabled(false);
        getModel().setMessage("");
        reloadInternshipBoards();
        reloadInternshipGroups();
        reloadInternshipTypes();
        reloadInternshipStatuses();
        getModel().setSelectedBoard(null);
        getModel().setSelectedStatusFrom(null);
        getModel().setSelectedStatusTo(null);
    }

    public void onUpdateBoard() {
        reloadInternshipStatuses();
        getModel().setSelectedStatusFrom(null);
        getModel().setSelectedStatusTo(null);
    }

    public void resetModel() {
        getModel().setUserSelectedOnly(false);
        onUserFilterChanged();
    }

    public void apply() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (getModel().isUserSelectedOnly()) {
            if (!StringUtils.isEmpty(getModel().getSelectedStatusTo())) {
                int to = Integer.parseInt(getModel().getSelectedStatusTo());
                AcademicInternshipStatus toObj = pi.findInternshipStatus(to);
                for (String s : getModel().getSelectionIdList()) {
                    AcademicInternship ii=pi.findInternship(Convert.toInt(s));
                    ii.setInternshipStatus(toObj);
                    pu.merge(ii);
                }
                fireEventExtraDialogClosed();
            }
        } else if (    //xor between board and group
                !StringUtils.isEmpty(getModel().getSelectedStatusFrom())
                        && !StringUtils.isEmpty(getModel().getSelectedStatusTo())) {
            int boardId = Convert.toInt(getModel().getSelectedBoard(), IntegerParserConfig.LENIENT_F);
            int groupId = Convert.toInt(getModel().getSelectedGroup(), IntegerParserConfig.LENIENT_F);
            int from = StringUtils.isEmpty(getModel().getSelectedStatusFrom()) ? -1 : Integer.parseInt(getModel().getSelectedStatusFrom());
            int to = Integer.parseInt(getModel().getSelectedStatusTo());
            AcademicInternshipStatus toObj = pi.findInternshipStatus(to);

            List<AcademicInternship> all = pi.findInternships(-1, groupId, boardId, -1, -1, true);
            for (AcademicInternship ii : all) {
                AcademicInternshipStatus s = ii.getInternshipStatus();
                if (s != null && s.getId() == from) {
                    ii.setInternshipStatus(toObj);
                    pu.merge(ii);
                }
            }
            fireEventExtraDialogClosed();
        }
        //
        //VrApp.getBean(AcademicPlugin.class).generateInternships(getModel().getInternship(), getModel().getProfile());
    }

    public void onChangeType() {
        reloadInternshipStatuses();
        getModel().setSelectedStatusFrom(null);
        getModel().setSelectedStatusTo(null);
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
        private List<String> selectionIdList;
        private AcademicInternship internship;
        private String selectedInternshipType;
        private String selectedBoard;
        private String selectedGroup;
        private String selectedStatusFrom;
        private String selectedStatusTo;
        private boolean userSelectedOnly;
        private List<SelectItem> boards = new ArrayList<SelectItem>();
        private List<SelectItem> groups = new ArrayList<SelectItem>();
        private List<SelectItem> statuses = new ArrayList<SelectItem>();
        private List<SelectItem> internshipTypes = new ArrayList<SelectItem>();

        public String getSelectedInternshipType() {
            return selectedInternshipType;
        }

        public void setSelectedInternshipType(String selectedInternshipType) {
            this.selectedInternshipType = selectedInternshipType;
        }

        public List<SelectItem> getInternshipTypes() {
            return internshipTypes;
        }

        public void setInternshipTypes(List<SelectItem> internshipTypes) {
            this.internshipTypes = internshipTypes;
        }

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

        public boolean isUserSelectedOnly() {
            return userSelectedOnly;
        }

        public void setUserSelectedOnly(boolean userSelectedOnly) {
            this.userSelectedOnly = userSelectedOnly;
        }

        public List<String> getSelectionIdList() {
            return selectionIdList;
        }

        public void setSelectionIdList(List<String> selectionIdList) {
            this.selectionIdList = selectionIdList;
        }

        public String getSelectedGroup() {
            return selectedGroup;
        }

        public void setSelectedGroup(String selectedGroup) {
            this.selectedGroup = selectedGroup;
        }

        public List<SelectItem> getGroups() {
            return groups;
        }

        public void setGroups(List<SelectItem> groups) {
            this.groups = groups;
        }
    }

}
