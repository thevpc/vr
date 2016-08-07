/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.app.vainruling.core.web.util.ChartUtils;
import net.vpc.app.vainruling.plugins.academic.internship.service.AcademicInternshipPlugin;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.*;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipSupervisorIntent;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.ext.AcademicInternshipExt;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.ext.AcademicInternshipExtList;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.web.dialog.DisciplineDialogCtrl;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.streams.PathInfo;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.PieChartModel;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * internships for teachers
 *
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Mes Comités de Stage",
        menu = "/Education/Internship",
        securityKey = "Custom.Education.MyInternshipBoards",
        url = "modules/academic/internship/my-internship-boards"
)
@ManagedBean
public class MyInternshipBoardsCtrl {


    private Model model = new Model();
    @Autowired
    private CorePlugin core;

    public Model getModel() {
        return model;
    }

    public boolean isStudent() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        return a.getCurrentStudent() != null;
    }

    public boolean isTeacher() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        return a.getCurrentTeacher() != null;
    }

    public AcademicTeacher getCurrentTeacher() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        return a.getCurrentTeacher();
    }

    public AcademicInternshipBoardTeacher getCurrentBoardTeacher() {
        AcademicTeacher t = getCurrentTeacher();
        if (t == null) {
            return null;
        }
        if (getModel().getInternship().getBoard() == null || getModel().getInternship().getBoard() == null) {
            return null;
        }
        AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
        List<AcademicInternshipBoardTeacher> teachers = p.findInternshipTeachersByBoard(getModel().getInternship().getBoard().getId());
        for (AcademicInternshipBoardTeacher teacher : teachers) {
            if (teacher.getTeacher() != null && teacher.getTeacher().getId() == t.getId()) {
                return teacher;
            }
        }
        return null;
    }

    public AcademicInternship getSelectedInternship() {
        String ii = getModel().getInternshipId();
        if (ii != null && ii.length() > 0) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            AcademicInternship tt = p.findInternship(Integer.parseInt(ii));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AcademicInternshipBoard getSelectedInternshipBoard() {
        String ii = getModel().getBoardId();
        if (ii != null && ii.length() > 0) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            AcademicInternshipBoard tt = p.findInternshipBoard(Integer.parseInt(ii));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public void onAddNewMessage() {
        if (getModel().getInternship() != null && !StringUtils.isEmpty(getModel().getNewMessage())) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            AcademicInternshipBoardTeacher b = getCurrentBoardTeacher();
            if (b != null) {
                AcademicInternshipBoardMessage m = new AcademicInternshipBoardMessage();
                m.setBoardTeacher(b);
                m.setInternship(getModel().getInternship());
                m.setPrivateObservations(getModel().getNewMessage());
                p.addBoardMessage(m);
                getModel().setBoardMessages(p.findInternshipMessagesByInternship(getModel().getInternship().getId()));
                getModel().setNewMessage("");
            }
        }
    }

    public AcademicTeacher getSelectedTeacher(String id) {
        if (id != null && id.length() > 0) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            AcademicTeacher tt = p.findTeacher(Integer.parseInt(id));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AcademicInternshipStatus getSelectedInternshipStatus(String id) {
        if (id != null && id.length() > 0) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            AcademicInternshipStatus tt = p.findInternshipStatus(Integer.parseInt(id));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AppCompany getSelectedCompany(String id) {
        if (id != null && id.length() > 0) {
            CorePlugin p = VrApp.getBean(CorePlugin.class);
            AppCompany tt = p.findCompany(Integer.parseInt(id));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AcademicInternshipVariant getSelectedInternshipVariant(String id) {
        if (id != null && id.length() > 0) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            AcademicInternshipVariant tt = p.findInternshipVariant(Integer.parseInt(id));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AcademicInternshipDuration getSelectedInternshipDuration(String id) {
        if (id != null && id.length() > 0) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            AcademicInternshipDuration tt = p.findInternshipDuration(Integer.parseInt(id));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public void onUpdateCompany() {
        if (getModel().getInternship() != null) {
            AppCompany s1 = getSelectedCompany(getModel().getCompanyId());
            getModel().getInternship().setCompany(s1);
        }
    }

    public void onUpdateChairExaminer() {
        if (getModel().getInternship() != null) {
            AcademicTeacher s1 = getSelectedTeacher(getModel().getChairExaminerId());
            getModel().getInternship().setChairExaminer(s1);
        }
    }

    public void onUpdateFirstExaminer() {
        if (getModel().getInternship() != null) {
            AcademicTeacher s1 = getSelectedTeacher(getModel().getFirstExaminerId());
            getModel().getInternship().setFirstExaminer(s1);
        }
    }

    public void onUpdateSecondExaminer() {
        if (getModel().getInternship() != null) {
            AcademicTeacher s1 = getSelectedTeacher(getModel().getSecondExaminerId());
            getModel().getInternship().setSecondExaminer(s1);
        }
    }

    public void onUpdateInternshipStatus() {
        if (getModel().getInternship() != null) {
            AcademicInternshipStatus s1 = getSelectedInternshipStatus(getModel().getInternshipStatusId());
            getModel().getInternship().setInternshipStatus(s1);
        }
    }

    public void onUpdateSupervisor() {
        if (getModel().getInternship() != null) {
            if (getModel().getInternship().getInternshipStatus().isBoardUpdatesSupervisors()) {
                AcademicTeacher s1 = getSelectedTeacher(getModel().getSupervisor1Id());
                getModel().getInternship().setSupervisor(s1);
                AcademicTeacher s2 = getSelectedTeacher(getModel().getSupervisor2Id());
                getModel().getInternship().setSecondSupervisor(s2);
            }
        }
    }

    public void onUpdateVariant() {
        if (getModel().getInternship() != null) {
            if (getModel().getInternship().getInternshipStatus().isBoardUpdatesDescr()) {
                AcademicInternshipVariant s1 = getSelectedInternshipVariant(getModel().getTypeVariantId());
                getModel().getInternship().setInternshipVariant(s1);
            }
        }
    }

    public void onUpdateDuration() {
        if (getModel().getInternship() != null) {
            if (getModel().getInternship().getInternshipStatus().isBoardUpdatesDescr()) {
                AcademicInternshipDuration s1 = getSelectedInternshipDuration(getModel().getDurationId());
                getModel().getInternship().setDuration(s1);
            }
        }
    }

    public void onSave() {
        try {
            if (getModel().getInternship() != null) {
                UPA.getContext().invokePrivileged(new VoidAction() {
                    @Override
                    public void run() {
                        PersistenceUnit pu = UPA.getPersistenceUnit();
                        AcademicInternship old = getModel().getInternship();
                        old.setLastTeacherUpdateTime(new Timestamp(System.currentTimeMillis()));
                        old.setLastUpdateTime(old.getLastTeacherUpdateTime());
                        pu.merge(old);
                        getModel().setInternship((AcademicInternship) pu.findById(AcademicInternship.class, old.getId()));
                    }
                });
                FacesUtils.addWarnMessage(null, "Enregistrement réussi");
            } else {
                FacesUtils.addWarnMessage(null, "Rien à enregistrer");
            }
        } catch (Exception e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    public void onClose() {
        getModel().setMode(EditCtrlMode.LIST);
        onRefresh();
    }

    @OnPageLoad
    public void onPageLoad() {
        getModel().setMode(EditCtrlMode.LIST);
        onUpdateBoard();
    }

    public void onUpdateBoard() {
        getModel().setInternshipBoard(getSelectedInternshipBoard());
        if (getModel().getInternshipBoard() == null) {
            getModel().setInternship(null);
        } else if (getModel().getInternship() != null && getModel().getInternship().getBoard() != null && getModel().getInternship().getBoard().getId() == getModel().getInternshipBoard().getId()) {
            //ok
        } else {
            getModel().setInternship(null);
        }
        onRefresh();
    }

    public void onUpdateInternship() {
        getModel().setInternship(getSelectedInternship());
        onRefresh();
    }

    public void onSelectInternship(AcademicInternship internship) {
        getModel().setInternship(internship);
        getModel().setMode(EditCtrlMode.UPDATE);
        onRefresh();
    }

    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByTeacherAndBoard(int teacherId, int boardId) {
        AcademicInternshipPlugin pi = VrApp.getBean(AcademicInternshipPlugin.class);
        return pi.findEnabledInternshipBoardsByTeacher(teacherId);
    }

    public AcademicInternshipExtList findActualInternshipsByTeacherAndBoard(int teacherId, int boardId, int internshipTypeId) {
        AcademicInternshipPlugin pi = VrApp.getBean(AcademicInternshipPlugin.class);
        AcademicTeacher t = getCurrentTeacher();
        return pi.findInternshipsByTeacherExt(teacherId, boardId,
                (t != null && t.getDepartment() != null) ? t.getDepartment().getId() : -1,
                internshipTypeId, true);
    }

    public void onRefreshListMode() {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        AcademicInternshipPlugin pi = VrApp.getBean(AcademicInternshipPlugin.class);
        getModel().setInternshipInfos(new ArrayList<AcademicInternshipInfo>());
        getModel().setInternshipItems(new ArrayList<SelectItem>());
        getModel().setInternships(new ArrayList<AcademicInternship>());
        getModel().setBoards(new ArrayList<SelectItem>());
        getModel().setTeachers(new ArrayList<SelectItem>());
        getModel().setTypeVariants(new ArrayList<SelectItem>());
        getModel().setDurations(new ArrayList<SelectItem>());
        getModel().setCompanies(new ArrayList<SelectItem>());
        getModel().setInternshipStatuses(new ArrayList<SelectItem>());
        getModel().setInternshipTypes(new ArrayList<SelectItem>());

        AcademicTeacher currentTeacher = getCurrentTeacher();
        AcademicInternshipExtList internships = new AcademicInternshipExtList();
        List<AcademicInternshipBoard> internshipBoards = new ArrayList<>();

        if (currentTeacher != null) {
            int boardId = getModel().getInternshipBoard() == null ? -1 : getModel().getInternshipBoard().getId();
            int type = -1;
            if (boardId == -1) {
                type = StringUtils.isEmpty(getModel().getFilterInternshipTypeId()) ? -1 : Integer.valueOf(getModel().getFilterInternshipTypeId());
            }
            internshipBoards = findEnabledInternshipBoardsByTeacherAndBoard(currentTeacher.getId(), boardId);
            if (boardId == -1 && type == -1) {
                internships = new AcademicInternshipExtList();
            } else {
                internships = findActualInternshipsByTeacherAndBoard(currentTeacher.getId(), boardId, type);
                internships.getMessages().sort(new Comparator<AcademicInternshipBoardMessage>() {
                    @Override
                    public int compare(AcademicInternshipBoardMessage o1, AcademicInternshipBoardMessage o2) {
                        return o2.getObsUpdateDate().compareTo(o1.getObsUpdateDate());
                    }
                });
            }
        }

        for (AcademicInternshipBoard t : internshipBoards) {
            String n = t.getName();
            getModel().getBoards().add(new SelectItem(String.valueOf(t.getId()), n));
        }

        AcademicPlugin pp = VrApp.getBean(AcademicPlugin.class);
        for (AcademicInternshipExt t : internships.getInternshipExts()) {
            String n = null;
            AcademicStudent s = t.getInternship().getStudent();
            String sname = pp.getValidName(s);
            n = (t.getInternship().getBoard() == null ? "?" : t.getInternship().getBoard().getName()) + "-" + t.getInternship().getCode() + "-" + sname + "-" + t.getInternship().getName();
            getModel().getInternshipItems().add(new SelectItem(String.valueOf(t.getInternship().getId()), n));
            getModel().getInternshipInfos().add(wrap(t));
        }

        getModel().setInternships(internships.getInternships());
        getModel().setInternshipExtList(internships);

        getModel().setBoardManager(false);

        for (AppCompany t : c.findCompanies()) {
            getModel().getCompanies().add(new SelectItem(String.valueOf(t.getId()), t.getName()));
        }

        for (AcademicInternshipType t : pi.findInternshipTypes()) {
            getModel().getInternshipTypes().add(new SelectItem(String.valueOf(t.getId()), t.getName()));
        }
        getModel().setFilterInternshipTypeVisible(StringUtils.isEmpty(getModel().getBoardId()));

        getModel().setAcademicInternshipCounts(new ArrayList<AcademicInternshipCount>());
        {
            BarChartModel d1 = new BarChartModel();
            d1.setTitle("Encadrements");
            d1.setLegendPosition("e");
            d1.setShadow(true);

            ChartSeries boys = new ChartSeries();
            boys.setLabel("Encadrements");
            d1.addSeries(boys);

            Map<String, Number> data = new LinkedHashMap<String, Number>();
            Map<Integer, Number> localIntershipSupersorsMap = new LinkedHashMap<Integer, Number>();

            for (AcademicInternshipInfo ii : getModel().getInternshipInfos()) {
                AcademicTeacher s1 = ii.internship.getSupervisor();
                AcademicTeacher s2 = ii.internship.getSecondSupervisor();
                if (s1 == null && s2 == null) {
                    //do nothing
                    String s0 = "<< Stages Sans Encadrement >>";
                    Number y = data.get(s0);
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 1;
                    }
                    data.put(s0, y);
                } else if (s1 != null && s2 == null) {
                    String s0 = s1.getContact().getFullName();
                    Number y = localIntershipSupersorsMap.get(s1.getId());
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 1;
                    }
                    data.put(s0, y);
                    localIntershipSupersorsMap.put(s1.getId(), y);
                } else if (s2 != null && s1 == null) {
                    String s0 = s2.getContact().getFullName();
                    Number y = localIntershipSupersorsMap.get(s2.getId());
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 1;
                    }
                    data.put(s0, y);
                    localIntershipSupersorsMap.put(s2.getId(), y);
                } else {
                    String s0 = s1.getContact().getFullName();
                    Number y = localIntershipSupersorsMap.get(s1.getId());
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 0.5;
                    }
                    data.put(s0, y);
                    localIntershipSupersorsMap.put(s1.getId(), y);

                    s0 = s2.getContact().getFullName();
                    y = localIntershipSupersorsMap.get(s2.getId());
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 0.5;
                    }
                    data.put(s0, y);
                    localIntershipSupersorsMap.put(s2.getId(), y);
                }
            }
            int filterPeriodId = -1;
            int filterTypeId = -1;
            if (getModel().getInternshipBoard() == null) {
                AppConfig appConfig = VrApp.getBean(CorePlugin.class).findAppConfig();
                filterPeriodId = (appConfig == null || appConfig.getMainPeriod()==null) ? -1 : appConfig.getMainPeriod().getId();
                String d = getModel().getFilterInternshipTypeId();
                filterTypeId = StringUtils.isEmpty(d) ? -1 : Integer.valueOf(d);
            } else {
                filterPeriodId = getModel().getInternshipBoard().getPeriod().getId();
                filterTypeId = getModel().getInternshipBoard().getInternshipType().getId();
            }
            Map<Integer, Number> internshipTeachersInternshipsCounts = pi.findInternshipTeachersInternshipsCounts(
                    filterPeriodId,
                    filterTypeId
            );
            for (AcademicTeacher t : p.findTeachers()) {
                String n = p.getValidName(t);
                double count = 0;
                double localCount = 0;
                if (getModel().getInternshipBoard() != null) {
                    Number cc = internshipTeachersInternshipsCounts.get(t.getId());
                    count = cc == null ? 0 : cc.doubleValue();
                }
                Number cc = localIntershipSupersorsMap.get(t.getId());
                localCount = cc == null ? 0 : cc.doubleValue();

                String countSuffix = "";
                if (count > 0) {
                    if (localCount == count) {
                        //all internships are visible here
                        if (count == ((int) count)) {
                            countSuffix += " (" + ((int) count) + ")";
                        } else {
                            countSuffix += " (" + (count) + ")";
                        }
                    } else {
                        String s2 = (count == ((int) count)) ? String.valueOf(((int) count)) : String.valueOf(count);
                        String s1 = (localCount == ((int) localCount)) ? String.valueOf(((int) localCount)) : String.valueOf(localCount);
                        countSuffix += " (" + s1 + "<" + s2 + ")";
                    }
                }
                getModel().getTeachers().add(new SelectItem(String.valueOf(t.getId()), n + countSuffix));
            }

            data = ChartUtils.reverseSortCount(data);
            boys.setData(new LinkedHashMap<Object, Number>(data));

//                getModel().setBar1(d1);
            List<AcademicInternshipCount> list = new ArrayList<>();
            for (Map.Entry<String, Number> entry : data.entrySet()) {
                AcademicInternshipCount a = new AcademicInternshipCount();
                a.setTeacherName(entry.getKey());
                a.setCount(entry.getValue().doubleValue());
                list.add(a);
            }
            getModel().setAcademicInternshipCounts(list);
        }

    }

    public void onRefreshUpdateMode() {
        getModel().setBoardTeachers(new ArrayList<AcademicInternshipBoardTeacher>());
        getModel().setBoardMessages(new ArrayList<AcademicInternshipBoardMessage>());
        AcademicInternshipPlugin pi = VrApp.getBean(AcademicInternshipPlugin.class);
        if (getModel().getInternship() != null && getModel().getInternship().getCompany() != null) {
            getModel().setCompanyId(String.valueOf(getModel().getInternship().getCompany().getId()));
        } else {
            getModel().setCompanyId(null);
        }

//        if (getModel().getInternship() != null && getModel().getInternship().getBoard() != null) {
//            getModel().setBoardId(String.valueOf(getModel().getInternship().getBoard().getId()));
//        } else {
//            getModel().setBoardId(null);
//        }
        if (getModel().getInternship() != null && getModel().getInternship().getChairExaminer() != null) {
            getModel().setChairExaminerId(String.valueOf(getModel().getInternship().getChairExaminer().getId()));
        } else {
            getModel().setChairExaminerId(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getDuration() != null) {
            getModel().setDurationId(String.valueOf(getModel().getInternship().getDuration().getId()));
        } else {
            getModel().setDurationId(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getFirstExaminer() != null) {
            getModel().setFirstExaminerId(String.valueOf(getModel().getInternship().getFirstExaminer().getId()));
        } else {
            getModel().setFirstExaminerId(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getSecondExaminer() != null) {
            getModel().setSecondExaminerId(String.valueOf(getModel().getInternship().getSecondExaminer().getId()));
        } else {
            getModel().setSecondExaminerId(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getInternshipStatus() != null) {
            getModel().setInternshipStatusId(String.valueOf(getModel().getInternship().getInternshipStatus().getId()));
        } else {
            getModel().setInternshipStatusId(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getSupervisor() != null) {
            getModel().setSupervisor1Id(String.valueOf(getModel().getInternship().getSupervisor().getId()));
        } else {
            getModel().setSupervisor1Id(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getSecondSupervisor() != null) {
            getModel().setSupervisor2Id(String.valueOf(getModel().getInternship().getSecondSupervisor().getId()));
        } else {
            getModel().setSupervisor2Id(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getInternshipVariant() != null) {
            getModel().setTypeVariantId(String.valueOf(getModel().getInternship().getInternshipVariant().getId()));
        } else {
            getModel().setTypeVariantId(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getBoard() != null) {
            AcademicInternshipBoardTeacher bt = getCurrentBoardTeacher();
            getModel().setBoardManager(bt != null && bt.isManager());
            AcademicInternshipType internshipType = getModel().getInternship().getBoard().getInternshipType();
            for (AcademicInternshipVariant t : pi.findInternshipVariantsByType(internshipType.getId())) {
                String n = t.getName();
                getModel().getTypeVariants().add(new SelectItem(String.valueOf(t.getId()), n));
            }
            for (AcademicInternshipDuration t : pi.findInternshipDurationsByType(internshipType.getId())) {
                String n = t.getName();
                getModel().getDurations().add(new SelectItem(String.valueOf(t.getId()), n));
            }
            for (AcademicInternshipStatus t : pi.findInternshipStatusesByType(internshipType.getId())) {
                String n = t.getName();
                getModel().getInternshipStatuses().add(new SelectItem(String.valueOf(t.getId()), n));
            }
            getModel().setBoardTeachers(pi.findInternshipTeachersByBoard(getModel().getInternship().getBoard().getId()));
            getModel().setBoardMessages(pi.findInternshipMessagesByInternship(getModel().getInternship().getId()));
        }
//        getModel().setSupervisor1Id((getModel().getInternship() == null || getModel().getInternship().getSupervisor() == null) ? null : String.valueOf(getModel().getInternship().getSupervisor().getId()));
//        getModel().setSupervisor2Id((getModel().getInternship() == null || getModel().getInternship().getSecondSupervisor() == null) ? null : String.valueOf(getModel().getInternship().getSecondSupervisor().getId()));
//        getModel().setTypeVariantId((getModel().getInternship() == null || getModel().getInternship().getInternshipVariant() == null) ? null : String.valueOf(getModel().getInternship().getInternshipVariant().getId()));
        getModel().setDurationId((getModel().getInternship() == null || getModel().getInternship().getDuration() == null) ? null : String.valueOf(getModel().getInternship().getDuration().getId()));
        getModel().setChairExaminerId((getModel().getInternship() == null || getModel().getInternship().getChairExaminer() == null) ? null : String.valueOf(getModel().getInternship().getChairExaminer().getId()));
        getModel().setFirstExaminerId((getModel().getInternship() == null || getModel().getInternship().getFirstExaminer() == null) ? null : String.valueOf(getModel().getInternship().getFirstExaminer().getId()));
        getModel().setSecondExaminerId((getModel().getInternship() == null || getModel().getInternship().getSecondExaminer() == null) ? null : String.valueOf(getModel().getInternship().getSecondExaminer().getId()));
        getModel().setInternshipStatusId((getModel().getInternship() == null || getModel().getInternship().getInternshipStatus() == null) ? null : String.valueOf(getModel().getInternship().getInternshipStatus().getId()));
    }

    public void onRefresh() {

        if (getModel().isList()) {
            onRefreshListMode();
        } else {
            onRefreshUpdateMode();
        }

    }

    LocationInfo resolveLocation(AppCompany c) {
        c = c == null ? null : VrApp.getBean(CorePlugin.class).findCompany(c.getId());
        LocationInfo info = new LocationInfo();
        info.company = c;
        info.governorate = info.company == null ? null : info.company.getGovernorate();
        info.region = info.governorate == null ? null : info.governorate.getRegion();
        info.country = info.region == null ? null : info.region.getCountry();
        if (info.country == null) {
            info.country = info.company == null ? null : info.company.getCountry();
        }
        info.companyName = info.company == null ? "?" : info.company.getName();
        info.governorateName = info.governorate == null ? "?" : info.governorate.getName();
        info.regionName = info.region == null ? "?" : info.region.getName();
        info.countryName = info.country == null ? "?" : info.country.getName();
        return info;
    }

    protected AcademicInternshipInfo wrap(AcademicInternshipExt internship) {
        AcademicInternshipInfo i = new AcademicInternshipInfo();
        i.internship = internship.getInternship();
        i.internshipExt = internship;
        rewrap(i);
        return i;
    }

    protected AcademicInternshipInfo rewrap(AcademicInternshipInfo i) {
        i.flags = new ArrayList<>();
        i.assigned = false;
        i.assignedToMe = false;
        i.demanded = false;
        i.demandedByMe = false;
        i.demandedOrAssigned = false;
        i.selectable = true;

        AcademicTeacher tt = getCurrentTeacher();

        AcademicInternshipStatus status = i.internship.getInternshipStatus();

        i.selectable = status.isSupervisorRequestable();
        TreeSet<String> supervisorInfo = new TreeSet<>();
        if (i.internship.getSupervisor() != null || i.internship.getSecondSupervisor() != null) {
            i.assigned = true;
            if (tt != null && i.internship.getSupervisor().getId() == tt.getId()) {
                i.assignedToMe = true;
            }
            if (i.internship.getSupervisor() != null) {
                supervisorInfo.add(i.internship.getSupervisor().getContact().getFullName() + "*");
            }
            if (i.internship.getSupervisor() != null) {
                supervisorInfo.add(i.internship.getSupervisor().getContact().getFullName() + "*");
            }
        }
        List<AcademicInternshipSupervisorIntent> allIntents = i.internshipExt.getSupervisorIntents();
        if (allIntents.size() > 0) {
            i.demanded = true;
            for (AcademicInternshipSupervisorIntent aa : allIntents) {
                AcademicTeacher a = aa.getTeacher();
                if (tt != null && a.getId() == tt.getId()) {
                    i.demandedByMe = true;
                }
                String n = a.getContact().getFullName();
                if (!supervisorInfo.contains(n + "*")) {
                    supervisorInfo.add(n);
                }
            }
        }
        StringBuilder supervisorInfoStr = new StringBuilder();
        for (String s : supervisorInfo) {
            if (supervisorInfoStr.length() > 0) {
                supervisorInfoStr.append(", ");
            }
            supervisorInfoStr.append(s);
        }
        i.supervisorInfo = supervisorInfoStr.toString();
        List<Integer> row1 = new ArrayList<>();
        List<Integer> row2 = new ArrayList<>();
        List<Integer> row3 = new ArrayList<>();
        i.flags.add(row1);
        i.flags.add(row2);
        i.flags.add(row3);

        row1.add(StringUtils.isEmpty(i.internship.getName()) ? 2 : i.internship.getName().length() < 10 ? 1 : 0);
        row1.add(StringUtils.isEmpty(i.internship.getDescription()) ? 2 : (i.internship.getDescription().length() < 50) ? 1 : 0);
        row1.add(i.internship.getInternshipVariant() == null ? 2 : 0);
        row1.add((i.internship.getCompany() == null && StringUtils.isEmpty(i.internship.getCompanyOther())) ? 2 : (i.internship.getCompany() == null) ? 1 : 0);
        row1.add(
                (i.internship.getCompanyMentor() == null && StringUtils.isEmpty(i.internship.getCompanyMentorOther())) ? 2 : 0
        );
        row1.add(2 - ((!StringUtils.isEmpty(i.internship.getCompanyMentorOtherEmail())) ? 1 : 0)
                - ((!StringUtils.isEmpty(i.internship.getCompanyMentorOtherPhone())) ? 1 : 0));

        row2.add(2 - (StringUtils.isEmpty(i.internship.getMainDiscipline()) ? 0 : 1) - (StringUtils.isEmpty(i.internship.getTechnologies()) ? 0 : 1));
        row2.add((i.internship.getStartDate() == null || i.internship.getEndDate() == null) ? 2 : i.internship.getEndDate().before(i.internship.getStartDate()) ? 1 : 0);
        row2.add((i.internship.getSpecFilePath() == null) ? 2 : 0);
        row2.add((i.internship.getMidTermReportFilePath() == null) ? 2 : 0);
        row2.add((i.internship.getReportFilePath() == null) ? 2 : 0);
        row2.add(-1);

        row3.add((i.internship.getSupervisor() == null) ? 2 : 0);
        boolean boardUpdatesEvaluators = i.internship.getInternshipStatus().isBoardUpdatesEvaluators();
        row3.add(boardUpdatesEvaluators ? ((i.internship.getChairExaminer() == null) ? 2 : 0) : -1);
        row3.add(boardUpdatesEvaluators ? ((i.internship.getFirstExaminer() == null) ? 2 : 0) : -1);
        row3.add(boardUpdatesEvaluators ? ((i.internship.getExamDate() == null) ? 2 : 0) : -1);
        row3.add(boardUpdatesEvaluators ? ((i.internship.getExamLocation() == null) ? 2 : 0) : -1);
        row3.add(-1);

        return i;
    }

    public void onRequestUpload(String report) {
        getModel().setRequestUploadType(report);
        getModel().setUploading(true);
    }

    public StreamedContent download(final String report) {
        return UPA.getContext().invokePrivileged(new Action<StreamedContent>() {
            @Override
            public StreamedContent run() {
                VFile f = null;
                if ("report1".equals(report)) {
                    f = core.getFileSystem().get(getModel().getInternship().getSpecFilePath());
                } else if ("report2".equals(report)) {
                    f = core.getFileSystem().get(getModel().getInternship().getMidTermReportFilePath());
                } else if ("report3".equals(report)) {
                    f = core.getFileSystem().get(getModel().getInternship().getReportFilePath());
                }
                if (f != null) {
                    InputStream stream = null;
                    try {
                        core.markDownloaded(f);
                        stream = f.getInputStream();
                        return new DefaultStreamedContent(stream, f.probeContentType(), f.getName());
                    } catch (IOException ex) {
                        Logger.getLogger(MyInternshipBoardsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }
        });
    }

    public void handleFileUpload(final FileUploadEvent event) {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    String report = getModel().getRequestUploadType();
                    String login = VrApp.getBean(UserSession.class).getUser().getLogin();
                    String tempPath = CorePlugin.PATH_TEMP + "/Import/" + VrHelper.date(new Date(), "yyyy-MM-dd-HH-mm")
                            + "-" + login;
                    String p = core.getNativeFileSystemPath() + tempPath;
                    new File(p).mkdirs();
                    File f = new File(p, event.getFile().getFileName());
                    event.getFile().write(f.getPath());
                    AcademicInternship internship = getModel().getInternship();
                    PathInfo uu = PathInfo.create(f);
                    String extensionPart = uu.getExtensionPart();
                    if (extensionPart == null) {
                        extensionPart = "pdf";
                    }
                    VFile userHome = core.getUserFolder(login).get("MesRapports");
                    userHome.mkdirs();
                    if ("report1".equals(report)) {
                        String validName = internship.getBoard().getInternshipType().getName() + "-" + internship.getCode() + "-" + login + "-spec." + extensionPart;
                        VFile ff = userHome.get(validName);
                        core.getFileSystem().get(tempPath + "/" + event.getFile().getFileName()).copyTo(ff);
                        internship.setSpecFilePath(ff.getBaseFile("vrfs").getPath());
                    } else if ("report2".equals(report)) {
                        String validName = internship.getBoard().getInternshipType().getName() + "-" + internship.getCode() + "-" + login + "-mid." + extensionPart;
                        VFile ff = userHome.get(validName);
                        core.getFileSystem().get(tempPath + "/" + event.getFile().getFileName()).copyTo(ff);
                        internship.setMidTermReportFilePath(ff.getBaseFile("vrfs").getPath());

                    } else if ("report3".equals(report)) {
                        String validName = internship.getBoard().getInternshipType().getName() + "-" + internship.getCode() + "-" + login + "-final." + extensionPart;
                        VFile ff = userHome.get(validName);
                        core.getFileSystem().get(tempPath + "/" + event.getFile().getFileName()).copyTo(ff);
                        internship.setReportFilePath(ff.getBaseFile("vrfs").getPath());
                    } else {
                        return;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MyInternshipBoardsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        getModel().setRequestUploadType(null);
        getModel().setUploading(false);
    }

    public void openDisciplineDialog() {
        DisciplineDialogCtrl.Config c = new DisciplineDialogCtrl.Config();
        c.setSourceId("");
        c.setUserInfo("");
        c.setTitle("Disciplines");
        c.setExpression(getModel().getInternship().getMainDiscipline());
        VrApp.getBean(DisciplineDialogCtrl.class).openDialog(c);
    }

    public void onDisciplineDialogClosed(SelectEvent event) {
        DialogResult o = (DialogResult) event.getObject();
        if (o != null) {
            getModel().getInternship().setMainDiscipline((String) o.getValue());
        }
    }

    public void removeMessage(int messageId) {
        AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
        p.removeBoardMessage(messageId);
        getModel().setBoardMessages(p.findInternshipMessagesByInternship(getModel().getInternship().getId()));
    }

    public static class LocationInfo {

        AppCompany company;
        AppGovernorate governorate;
        AppCountryRegion region;
        AppCountry country;
        String companyName;
        String governorateName;
        String regionName;
        String countryName;
    }

    public static class AcademicInternshipCount {

        private String teacherName;
        private double count;

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public double getCount() {
            return count;
        }

        public void setCount(double count) {
            this.count = count;
        }

    }

    public static class AcademicInternshipInfo {

        private AcademicInternshipExt internshipExt;
        private AcademicInternship internship;
        private List<List<Integer>> flags;
        private List<AcademicTeacher> intentTeachers;
        private boolean selectable;
        private boolean demanded;
        private boolean demandedOrAssigned;
        private boolean demandedByMe;
        private boolean assigned;
        private boolean assignedToMe;
        private String supervisorInfo;

        public AcademicInternship getInternship() {
            return internship;
        }

        public void setInternship(AcademicInternship internship) {
            this.internship = internship;
        }

        public List<List<Integer>> getFlags() {
            return flags;
        }

        public void setFlags(List<List<Integer>> flags) {
            this.flags = flags;
        }

        public boolean isDemanded() {
            return demanded;
        }

        public void setDemanded(boolean demanded) {
            this.demanded = demanded;
        }

        public boolean isAssignedToMe() {
            return assignedToMe;
        }

        public void setAssignedToMe(boolean assignedToMe) {
            this.assignedToMe = assignedToMe;
        }

        public String getSupervisorInfo() {
            return supervisorInfo;
        }

        public void setSupervisorInfo(String supervisorInfo) {
            this.supervisorInfo = supervisorInfo;
        }

        public List<AcademicTeacher> getIntentTeachers() {
            return intentTeachers;
        }

        public void setIntentTeachers(List<AcademicTeacher> intentTeachers) {
            this.intentTeachers = intentTeachers;
        }

        public boolean isDemandedOrAssigned() {
            return demandedOrAssigned;
        }

        public void setDemandedOrAssigned(boolean demandedOrAssigned) {
            this.demandedOrAssigned = demandedOrAssigned;
        }

        public boolean isDemandedByMe() {
            return demandedByMe;
        }

        public void setDemandedByMe(boolean demandedByMe) {
            this.demandedByMe = demandedByMe;
        }

        public boolean isAssigned() {
            return assigned;
        }

        public void setAssigned(boolean assigned) {
            this.assigned = assigned;
        }

        public boolean isSelectable() {
            return selectable;
        }

        public void setSelectable(boolean selectable) {
            this.selectable = selectable;
        }

        public AcademicInternshipExt getInternshipExt() {
            return internshipExt;
        }

        public void setInternshipExt(AcademicInternshipExt internshipExt) {
            this.internshipExt = internshipExt;
        }

    }

    public class Model {

        private EditCtrlMode mode = EditCtrlMode.LIST;
        private String requestUploadType;
        private boolean uploading;
        private boolean boardManager;
        private String internshipId;
        private String boardId;
        private String companyId;
        private String internshipStatusId;
        private String chairExaminerId;
        private String firstExaminerId;
        private String secondExaminerId;
        private String supervisor1Id;
        private String supervisor2Id;
        private String filterInternshipTypeId;
        private String typeVariantId;
        private String durationId;
        private String newMessage;
        private AcademicInternship internship;
        private AcademicInternshipBoard internshipBoard;
        private List<AcademicInternshipCount> academicInternshipCounts = new ArrayList<AcademicInternshipCount>();
        private List<SelectItem> boards = new ArrayList<SelectItem>();
        private List<SelectItem> internshipItems = new ArrayList<SelectItem>();
        private List<SelectItem> internshipTypes = new ArrayList<SelectItem>();
        private List<AcademicInternship> internships = new ArrayList<AcademicInternship>();
        private AcademicInternshipExtList internshipExtList = new AcademicInternshipExtList();
        private List<AcademicInternshipInfo> internshipInfos = new ArrayList<AcademicInternshipInfo>();
        private List<SelectItem> teachers = new ArrayList<SelectItem>();
        private List<SelectItem> companies = new ArrayList<SelectItem>();
        private List<SelectItem> typeVariants = new ArrayList<SelectItem>();
        private List<SelectItem> durations = new ArrayList<SelectItem>();
        private List<SelectItem> internshipStatuses = new ArrayList<SelectItem>();
        private List<AcademicInternshipBoardTeacher> boardTeachers = new ArrayList<AcademicInternshipBoardTeacher>();
        private List<AcademicInternshipBoardMessage> boardMessages = new ArrayList<AcademicInternshipBoardMessage>();
        private DonutChartModel donut1;
        private DonutChartModel donut2;
        private DonutChartModel donut3;
        private DonutChartModel donut4;
        private DonutChartModel donut5;
        private BarChartModel bar1;
        private PieChartModel pie1;
        private PieChartModel pie2;
        private boolean filterInternshipTypeVisible = true;

        public DonutChartModel getDonut1() {
            return donut1;
        }

        public void setDonut1(DonutChartModel donut1) {
            this.donut1 = donut1;
        }

        public DonutChartModel getDonut2() {
            return donut2;
        }

        public void setDonut2(DonutChartModel donut2) {
            this.donut2 = donut2;
        }

        public boolean isBoardManager() {
            return boardManager;
        }

        public void setBoardManager(boolean boardManager) {
            this.boardManager = boardManager;
        }

        public List<AcademicInternshipBoardTeacher> getBoardTeachers() {
            return boardTeachers;
        }

        public void setBoardTeachers(List<AcademicInternshipBoardTeacher> boardTeachers) {
            this.boardTeachers = boardTeachers;
        }

        public AcademicInternship getInternship() {
            return internship;
        }

        public void setInternship(AcademicInternship internship) {
            this.internship = internship;
        }

        public String getInternshipId() {
            return internshipId;
        }

        public void setInternshipId(String internshipId) {
            this.internshipId = internshipId;
        }

        public List<SelectItem> getInternshipItems() {
            return internshipItems;
        }

        public void setInternshipItems(List<SelectItem> internships) {
            this.internshipItems = internships;
        }

        public List<SelectItem> getInternshipTypes() {
            return internshipTypes;
        }

        public void setInternshipTypes(List<SelectItem> internshipTypes) {
            this.internshipTypes = internshipTypes;
        }

        public String getFilterInternshipTypeId() {
            return filterInternshipTypeId;
        }

        public void setFilterInternshipTypeId(String filterInternshipTypeId) {
            this.filterInternshipTypeId = filterInternshipTypeId;
        }

        public List<AcademicInternship> getInternships() {
            return internships;
        }

        public void setInternships(List<AcademicInternship> internships) {
            this.internships = internships;
        }

        public String getSupervisor1Id() {
            return supervisor1Id;
        }

        public void setSupervisor1Id(String supervisor1Id) {
            this.supervisor1Id = supervisor1Id;
        }

        public String getSupervisor2Id() {
            return supervisor2Id;
        }

        public void setSupervisor2Id(String supervisor2Id) {
            this.supervisor2Id = supervisor2Id;
        }

        public List<SelectItem> getTeachers() {
            return teachers;
        }

        public void setTeachers(List<SelectItem> teachers) {
            this.teachers = teachers;
        }

        public String getTypeVariantId() {
            return typeVariantId;
        }

        public void setTypeVariantId(String typeVariantId) {
            this.typeVariantId = typeVariantId;
        }

        public List<SelectItem> getTypeVariants() {
            return typeVariants;
        }

        public void setTypeVariants(List<SelectItem> typeVariants) {
            this.typeVariants = typeVariants;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public List<SelectItem> getCompanies() {
            return companies;
        }

        public void setCompanies(List<SelectItem> companies) {
            this.companies = companies;
        }

        public String getRequestUploadType() {
            return requestUploadType;
        }

        public void setRequestUploadType(String requestUploadType) {
            this.requestUploadType = requestUploadType;
        }

        public boolean isUploading() {
            return uploading;
        }

        public void setUploading(boolean uploading) {
            this.uploading = uploading;
        }

        public String getBoardId() {
            return boardId;
        }

        public void setBoardId(String boardId) {
            this.boardId = boardId;
        }

        public List<SelectItem> getBoards() {
            return boards;
        }

        public void setBoards(List<SelectItem> boards) {
            this.boards = boards;
        }

        public AcademicInternshipBoard getInternshipBoard() {
            return internshipBoard;
        }

        public void setInternshipBoard(AcademicInternshipBoard internshipBoard) {
            this.internshipBoard = internshipBoard;
        }

        public String getDurationId() {
            return durationId;
        }

        public void setDurationId(String durationId) {
            this.durationId = durationId;
        }

        public List<SelectItem> getDurations() {
            return durations;
        }

        public void setDurations(List<SelectItem> durations) {
            this.durations = durations;
        }

        public String getInternshipStatusId() {
            return internshipStatusId;
        }

        public void setInternshipStatusId(String internshipStatusId) {
            this.internshipStatusId = internshipStatusId;
        }

        public String getChairExaminerId() {
            return chairExaminerId;
        }

        public void setChairExaminerId(String chairExaminerId) {
            this.chairExaminerId = chairExaminerId;
        }

        public String getFirstExaminerId() {
            return firstExaminerId;
        }

        public void setFirstExaminerId(String firstExaminerId) {
            this.firstExaminerId = firstExaminerId;
        }

        public String getSecondExaminerId() {
            return secondExaminerId;
        }

        public void setSecondExaminerId(String secondExaminerId) {
            this.secondExaminerId = secondExaminerId;
        }

        public List<SelectItem> getInternshipStatuses() {
            return internshipStatuses;
        }

        public void setInternshipStatuses(List<SelectItem> internshipStatuses) {
            this.internshipStatuses = internshipStatuses;
        }

        public List<AcademicInternshipBoardMessage> getBoardMessages() {
            return boardMessages;
        }

        public void setBoardMessages(List<AcademicInternshipBoardMessage> boardMessages) {
            this.boardMessages = boardMessages;
        }

        public String getNewMessage() {
            return newMessage;
        }

        public void setNewMessage(String newMessage) {
            this.newMessage = newMessage;
        }

        public List<AcademicInternshipInfo> getInternshipInfos() {
            return internshipInfos;
        }

        public void setInternshipInfos(List<AcademicInternshipInfo> internshipInfos) {
            this.internshipInfos = internshipInfos;
        }

        public EditCtrlMode getMode() {
            return mode;
        }

        public void setMode(EditCtrlMode mode) {
            this.mode = mode;
        }

        public boolean isList() {
            return getMode() == EditCtrlMode.LIST;
        }

        public DonutChartModel getDonut3() {
            return donut3;
        }

        public void setDonut3(DonutChartModel donut3) {
            this.donut3 = donut3;
        }

        public DonutChartModel getDonut4() {
            return donut4;
        }

        public void setDonut4(DonutChartModel donut4) {
            this.donut4 = donut4;
        }

        public DonutChartModel getDonut5() {
            return donut5;
        }

        public void setDonut5(DonutChartModel donut5) {
            this.donut5 = donut5;
        }

        public PieChartModel getPie1() {
            return pie1;
        }

        public void setPie1(PieChartModel pie1) {
            this.pie1 = pie1;
        }

        public PieChartModel getPie2() {
            return pie2;
        }

        public void setPie2(PieChartModel pie2) {
            this.pie2 = pie2;
        }

        public BarChartModel getBar1() {
            return bar1;
        }

        public void setBar1(BarChartModel bar1) {
            this.bar1 = bar1;
        }

        public List<AcademicInternshipCount> getAcademicInternshipCounts() {
            return academicInternshipCounts;
        }

        public void setAcademicInternshipCounts(List<AcademicInternshipCount> academicInternshipCounts) {
            this.academicInternshipCounts = academicInternshipCounts;
        }

        public AcademicInternshipExtList getInternshipExtList() {
            return internshipExtList;
        }

        public void setInternshipExtList(AcademicInternshipExtList internshipExtList) {
            this.internshipExtList = internshipExtList;
        }

        public boolean isFilterInternshipTypeVisible() {
            return filterInternshipTypeVisible;
        }

        public void setFilterInternshipTypeVisible(boolean filterInternshipTypeVisible) {
            this.filterInternshipTypeVisible = filterInternshipTypeVisible;
        }

    }
}
