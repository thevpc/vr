/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.internship;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.MirroredPath;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.config.*;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.ext.AcademicInternshipExt;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.ext.AcademicInternshipExtList;
import net.vpc.app.vainruling.plugins.academic.web.dialog.DisciplineDialogCtrl;
import net.vpc.common.io.PathInfo;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.springframework.beans.factory.annotation.Autowired;

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
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Mes Comités de Stage",
        menu = "/Education/Projects/Internships",
        securityKey = AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_INTERNSHIP_BOARDS,
        url = "modules/academic/internship/my-internship-boards"
)
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
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
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
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
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
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            AcademicInternshipBoard tt = p.findInternshipBoard(Integer.parseInt(ii));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public void onAddNewMessage() {
        if (getModel().getInternship() != null && !StringUtils.isEmpty(getModel().getNewMessage())) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
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
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
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
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            AcademicInternshipVariant tt = p.findInternshipVariant(Integer.parseInt(id));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AcademicInternshipDuration getSelectedInternshipDuration(String id) {
        if (id != null && id.length() > 0) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
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
                FacesUtils.addInfoMessage("Enregistrement réussi");
            } else {
                FacesUtils.addWarnMessage("Rien à enregistrer");
            }
        } catch (Exception e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    public void onClose() {
        getModel().setMode(AccessMode.READ);
        onRefresh();
    }

    @OnPageLoad
    public void onPageLoad() {
        getModel().setMode(AccessMode.READ);
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
        getModel().setMode(AccessMode.UPDATE);
        onRefresh();
    }

    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByTeacherAndBoard(int teacherId, int boardId) {
        AcademicPlugin pi = VrApp.getBean(AcademicPlugin.class);
        return pi.findEnabledInternshipBoardsByTeacher(teacherId);
    }

    public AcademicInternshipExtList findActualInternshipsByTeacherAndBoard(int teacherId, int boardId, int internshipTypeId) {
        AcademicPlugin pi = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getCurrentTeacher();
        return pi.findInternshipsByTeacherExt(-1, (t != null && t.getUser().getDepartment() != null) ? t.getUser().getDepartment().getId() : -1, teacherId, internshipTypeId, boardId,
                true);
    }

    public void onRefreshListMode() {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        AcademicPlugin pi = VrApp.getBean(AcademicPlugin.class);
        getModel().setInternshipInfos(new ArrayList<AcademicInternshipInfo>());
        getModel().setInternshipItems(new ArrayList<SelectItem>());
        getModel().setInternships(new ArrayList<AcademicInternship>());
        getModel().setPeriods(new ArrayList<SelectItem>());
        getModel().setBoards(new ArrayList<SelectItem>());
        getModel().setTeachers(new ArrayList<SelectItem>());
        getModel().setTypeVariants(new ArrayList<SelectItem>());
        getModel().setDurations(new ArrayList<SelectItem>());
        getModel().setCompanies(new ArrayList<SelectItem>());
        getModel().setInternshipStatuses(new ArrayList<SelectItem>());
        getModel().setInternshipTypes(new ArrayList<SelectItem>());
        getModel().getPeriods().clear();

        AcademicTeacher currentTeacher = getCurrentTeacher();
        AcademicInternshipExtList internships = new AcademicInternshipExtList();
        List<AcademicInternshipBoard> internshipBoards = new ArrayList<>();
        for (AppPeriod period : core.findNavigatablePeriods()) {
            getModel().getPeriods().add(FacesUtils.createSelectItem(String.valueOf(period.getId()), period.getName()));
        }

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
            getModel().getBoards().add(FacesUtils.createSelectItem(String.valueOf(t.getId()), n));
        }

        AcademicPlugin pp = VrApp.getBean(AcademicPlugin.class);
        List<AcademicInternshipInfo> internshipInfosToAddTo = getModel().getInternshipInfos();
        List<SelectItem> internshipItemsToAddTo = getModel().getInternshipItems();
        for (AcademicInternshipExt t : internships.getInternshipExts()) {
            String n = null;
            AcademicStudent s = t.getInternship().getStudent();
            String sname = pp.getValidName(s);
            n = (t.getInternship().getBoard() == null ? "?" : t.getInternship().getBoard().getName()) + "-" + t.getInternship().getCode() + "-" + sname + "-" + t.getInternship().getName();
            internshipItemsToAddTo.add(FacesUtils.createSelectItem(String.valueOf(t.getInternship().getId()), n));
            internshipInfosToAddTo.add(new AcademicInternshipInfo(t, getCurrentTeacher()));
//            System.out.println(t);
        }
//        int min=5;
//        int max=9;
//        min=min;
//        for (int i = max; i >=min; i--) {
//            if(i>=0 && i<internshipItemsToAddTo.size()) {
//                internshipItemsToAddTo.remove(i);
//            }
//            if(i>=0 && i<internshipInfosToAddTo.size()) {
//                internshipInfosToAddTo.remove(i);
//            }
//        }

        getModel().setInternships(internships.getInternships());
        getModel().setInternshipExtList(internships);

        getModel().setBoardManager(false);

        for (AppCompany t : c.findCompanies()) {
            getModel().getCompanies().add(FacesUtils.createSelectItem(String.valueOf(t.getId()), t.getName()));
        }

        for (AcademicInternshipType t : pi.findInternshipTypes()) {
            getModel().getInternshipTypes().add(FacesUtils.createSelectItem(String.valueOf(t.getId()), t.getName()));
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
                AcademicTeacher s1 = ii.getInternship().getSupervisor();
                AcademicTeacher s2 = ii.getInternship().getSecondSupervisor();
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
                    String s0 = s1.resolveFullName();
                    Number y = localIntershipSupersorsMap.get(s1.getId());
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 1;
                    }
                    data.put(s0, y);
                    localIntershipSupersorsMap.put(s1.getId(), y);
                } else if (s2 != null && s1 == null) {
                    String s0 = s2.resolveFullName();
                    Number y = localIntershipSupersorsMap.get(s2.getId());
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 1;
                    }
                    data.put(s0, y);
                    localIntershipSupersorsMap.put(s2.getId(), y);
                } else {
                    String s0 = s1.resolveFullName();
                    Number y = localIntershipSupersorsMap.get(s1.getId());
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 0.5;
                    }
                    data.put(s0, y);
                    localIntershipSupersorsMap.put(s1.getId(), y);

                    s0 = s2.resolveFullName();
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
                AppConfig appConfig = VrApp.getBean(CorePlugin.class).getCurrentConfig();
                filterPeriodId = (appConfig == null || appConfig.getMainPeriod() == null) ? -1 : appConfig.getMainPeriod().getId();
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
                getModel().getTeachers().add(FacesUtils.createSelectItem(String.valueOf(t.getId()), n + countSuffix));
            }

            data = VrUtils.reverseSortCount(data);
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
        AcademicPlugin pi = VrApp.getBean(AcademicPlugin.class);
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
                getModel().getTypeVariants().add(FacesUtils.createSelectItem(String.valueOf(t.getId()), n));
            }
            for (AcademicInternshipDuration t : pi.findInternshipDurationsByType(internshipType.getId())) {
                String n = t.getName();
                getModel().getDurations().add(FacesUtils.createSelectItem(String.valueOf(t.getId()), n));
            }
            for (AcademicInternshipStatus t : pi.findInternshipStatusesByType(internshipType.getId())) {
                String n = t.getName();
                getModel().getInternshipStatuses().add(FacesUtils.createSelectItem(String.valueOf(t.getId()), n));
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


//    protected AcademicInternshipInfo wrap(AcademicInternshipExt internship) {
//        AcademicInternshipInfo i = new AcademicInternshipInfo();
//        i.setInternship(internship.getInternship());
//        i.setInternshipExt(internship);
//        rewrap(i);
//        return i;
//    }
//
//    protected AcademicInternshipInfo rewrap(AcademicInternshipInfo i) {
//        i.rewrap(getCurrentTeacher());
//        return i;
//    }

    public void onRequestUpload(String report) {
        getModel().setRequestUploadType(report);
        getModel().setUploading(true);
    }

    public StreamedContent download(final String report) {
        return UPA.getPersistenceUnit().invokePrivileged(new Action<StreamedContent>() {
            @Override
            public StreamedContent run() {
                VFile f = null;
                if ("report1".equals(report)) {
                    f = core.getRootFileSystem().get(getModel().getInternship().getSpecFilePath());
                } else if ("report2".equals(report)) {
                    f = core.getRootFileSystem().get(getModel().getInternship().getMidTermReportFilePath());
                } else if ("report3".equals(report)) {
                    f = core.getRootFileSystem().get(getModel().getInternship().getReportFilePath());
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
                    String login = core.getCurrentUserLogin();
                    MirroredPath temp=CorePlugin.get().createTempUploadFolder();


                    File f = new File(temp.getNativePath(), event.getFile().getFileName());
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
                        temp.getPath().get(event.getFile().getFileName()).copyTo(ff);
                        internship.setSpecFilePath(ff.getBaseFile("vrfs").getPath());
                    } else if ("report2".equals(report)) {
                        String validName = internship.getBoard().getInternshipType().getName() + "-" + internship.getCode() + "-" + login + "-mid." + extensionPart;
                        VFile ff = userHome.get(validName);
                        temp.getPath().get(event.getFile().getFileName()).copyTo(ff);
                        internship.setMidTermReportFilePath(ff.getBaseFile("vrfs").getPath());

                    } else if ("report3".equals(report)) {
                        String validName = internship.getBoard().getInternshipType().getName() + "-" + internship.getCode() + "-" + login + "-final." + extensionPart;
                        VFile ff = userHome.get(validName);
                        temp.getPath().get(event.getFile().getFileName()).copyTo(ff);
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
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        p.removeBoardMessage(messageId);
        getModel().setBoardMessages(p.findInternshipMessagesByInternship(getModel().getInternship().getId()));
    }

    public class Model {

        private AccessMode mode = AccessMode.READ;
        private String requestUploadType;
        private boolean uploading;
        private boolean boardManager;
        private String internshipId;
        private String boardId;
        private String periodId;
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
        private List<SelectItem> periods = new ArrayList<SelectItem>();
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
        private boolean filterInternshipTypeVisible = true;

        public String getPeriodId() {
            return periodId;
        }

        public void setPeriodId(String periodId) {
            this.periodId = periodId;
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

        public List<SelectItem> getPeriods() {
            return periods;
        }

        public void setPeriods(List<SelectItem> periods) {
            this.periods = periods;
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

        public List<AcademicInternship> getInternships2() {
            List<AcademicInternship> all = new ArrayList<>();
            for (AcademicInternshipInfo academicInternshipInfo : getInternshipInfos()) {
                all.add(academicInternshipInfo.getInternship());
            }
            return all;
        }

        public List<AcademicInternshipInfo> getInternshipInfos() {
            return internshipInfos;
        }

        public void setInternshipInfos(List<AcademicInternshipInfo> internshipInfos) {
            this.internshipInfos = internshipInfos;
        }

        public AccessMode getMode() {
            return mode;
        }

        public void setMode(AccessMode mode) {
            this.mode = mode;
        }

        public boolean isList() {
            return getMode() == AccessMode.READ;
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
