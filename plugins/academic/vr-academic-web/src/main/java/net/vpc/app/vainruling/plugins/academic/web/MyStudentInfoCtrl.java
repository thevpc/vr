/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.web.jsf.ctrl.DocumentUploadListener;
import net.vpc.app.vainruling.core.web.jsf.ctrl.DocumentsCtrl;
import net.vpc.app.vainruling.core.web.jsf.ctrl.dialog.DocumentsUploadDialogCtrl;
import net.vpc.app.vainruling.core.web.jsf.ctrl.FileUploadEventHandler;
import net.vpc.app.vainruling.core.service.util.ValidatorProgressHelper;
import net.vpc.app.vainruling.core.web.jsf.VrJsf;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.model.imp.AcademicStudentImport;
import net.vpc.common.io.PathInfo;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileFilter;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.primefaces.event.FileUploadEvent;

import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.VrActionEnabler;
import net.vpc.app.vainruling.VrOnPageLoad;

/**
 * @author taha.bensalah@gmail.com
 */
//@VrController(
//        breadcrumb = {
//                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
////        css = "fa-table",
////        title = "Inscription Etudiant",
//        url = "modules/academic/my-student-info",
//        menu = "/Desktop"
//)
public class MyStudentInfoCtrl implements DocumentUploadListener, VrActionEnabler {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public void onImport() {
        try {
            UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    try {
                        PersistenceUnit pu = UPA.getPersistenceUnit();
                        if (getModel().isInsertMode()) {
                            AcademicPlugin.get().importStudent(CorePlugin.get().getCurrentPeriod().getId(), new AcademicStudentImport(getModel().getStudent()));
                        } else {
                            pu.merge(getModel().getStudent());
                            pu.merge(getModel().getStudent().getUser());
                        }
                        //p.importStudent(-1, getModel().getStudent());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            FacesUtils.addInfoMessage("Enregistrement reussi");
            onRefresh();
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Enregistrement echoue");
            e.printStackTrace();
        }
    }

    @VrOnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();

    }

    public void onRefresh() {
        AcademicStudent c = AcademicPlugin.get().getCurrentStudent();
        if (c == null) {
            c = new AcademicStudent();
            c.setUser(new AppUser());
        }
        getModel().setStudent(c);
        ValidatorProgressHelper h = new ValidatorProgressHelper();
        h.checkNotDefault(c.getUser().getFirstName(), "Missing FirstName");
        h.checkNotDefault(c.getUser().getLastName(), "Missing LastName");
        h.checkNotDefault(c.getUser().getEmail(), "Missing Email");
        h.checkNotDefault(c.getUser().getPhone1(), "Missing Phone1");
        h.checkNotDefault(c.getUser().getCivility(), "Missing Civility");
        h.checkNotDefault(c.getUser().getGender(), "Missing Gender");
        h.checkNotDefault(c.getUser().getDepartment(), "Missing Department");
        h.checkNotDefault(c.getBaccalaureateClass(), "Missing BaccalaureateClass");
        h.checkNotDefault(c.getBaccalaureateScore(), "Missing BaccalaureateScore");
        h.checkNotDefault(c.getPreClassType(), "Missing PreClassType");
        h.checkNotDefault(c.getPreClass(), "Missing PreClass");
        h.checkNotDefault(c.getPreClassRankByProgram(), "Missing PreClassRankByProgram");
        h.checkNotDefault(c.getPreClassScore(), "Missing PreClassScore");
        h.check(c.getPreClassRank() > 0 || c.getPreClassRank2() > 0, "Missing ");
        h.check(c.getPreClassChoice1() != null || !StringUtils.isBlank(c.getPreClassChoice1Other()));
        h.check(c.getPreClassChoice2() != null || !StringUtils.isBlank(c.getPreClassChoice2Other()));
        h.check(c.getPreClassChoice3() != null || !StringUtils.isBlank(c.getPreClassChoice3Other()));
        getModel().setCompletion(h.getCompletionPercent());
        updateLists();
    }

    public void onFiltersChanged() {
        //onRefresh();
    }

    public void updateLists() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        getModel().setPeriodItems(VrJsf.toSelectItemList(core.findValidPeriods()));
        getModel().setCivilityItems(VrJsf.toSelectItemList(core.findCivilities()));
        getModel().setGenderItems(VrJsf.toSelectItemList(core.findGenders()));
        getModel().setBacItems(VrJsf.toSelectItemList(p.findAcademicBacs()));
        getModel().setPrepItems(VrJsf.toSelectItemList(p.findAcademicPreClasses()));
        getModel().setDepartmentItems(VrJsf.toSelectItemList(core.findDepartments()));
        getModel().setClassItems(VrJsf.toSelectItemList(p.findAcademicClasses()));

    }

    public void onRequestUploadPhoto() {
        DocumentsUploadDialogCtrl docs = VrApp.getBean(DocumentsUploadDialogCtrl.class);
        docs.openCustomDialog(new DocumentsUploadDialogCtrl.Config()
                .setSizeLimit(1024 * 1024)
                .setExtensions("png,jpg,jpeg"),
                this);
    }

    @Override
    public void onUpload(FileUploadEvent event) {
        try {
            AcademicStudent c = AcademicPlugin.get().getCurrentStudent();
            String e = PathInfo.create(event.getFile().getFileName()).getExtensionPart();
            if (e.equals("jpeg")) {
                e = "jpg";
            }
            if (e.equals("png") || e.equals("jpg")) {
                VFile filePath = CorePlugin.get().getUserFolder(c.getUser().getLogin()).get("/Config/photo." + e);
                if (filePath != null) {
                    filePath.getParentFile().mkdirs();
                    VFile[] old = filePath.getParentFile().listFiles(new VFileFilter() {
                        @Override
                        public boolean accept(VFile pathname) {
                            if (pathname.getName().equalsIgnoreCase("photo.png")
                                    || pathname.getName().equalsIgnoreCase("photo.jpg")
                                    || pathname.getName().equalsIgnoreCase("photo.jpeg")) {
                                return true;
                            }
                            return false;
                        }
                    });
                    for (VFile f : old) {
                        f.delete();
                    }
                    UPA.getContext().invokePrivileged(new VoidAction() {
                        @Override
                        public void run() {
                            try {
                                CorePlugin.get().uploadFile(filePath, new FileUploadEventHandler(event));
                                FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully uploaded.");
                            } catch (IOException e1) {
                                Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, e1);
                                FacesUtils.addErrorMessage(e1, event.getFile().getFileName() + " uploading failed.");
                            }
                        }
                    });
                }
            } else {
                FacesUtils.addErrorMessage(event.getFile().getFileName() + " has invalid extension");
            }
        } catch (Exception ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex, event.getFile().getFileName() + " uploading failed.");
            return;
        }

        FacesUtils.addErrorMessage(event.getFile().getFileName() + " uploading failed.");
    }

    @Override
    public void checkEnabled(net.vpc.app.vainruling.VrActionInfo data) {
        if (AcademicPlugin.get().getCurrentStudent() == null) {
            throw new SecurityException("Expected student");
        }
    }

    public class Model {

        AcademicStudent student = new AcademicStudent();
        List<SelectItem> genderItems = new ArrayList<>();
        List<SelectItem> civilityItems = new ArrayList<>();
        List<SelectItem> periodItems = new ArrayList<>();
        List<SelectItem> departmentItems = new ArrayList<>();
        List<SelectItem> bacItems = new ArrayList<>();
        List<SelectItem> prepItems = new ArrayList<>();
        List<SelectItem> classItems = new ArrayList<>();
        private boolean insertMode = false;
        private double completion = 40;

        public double getCompletion() {
            return completion;
        }

        public void setCompletion(double completion) {
            this.completion = completion;
        }

        public boolean isInsertMode() {
            return insertMode;
        }

        public void setInsertMode(boolean insertMode) {
            this.insertMode = insertMode;
        }

        public AcademicStudent getStudent() {
            return student;
        }

        public void setStudent(AcademicStudent student) {
            this.student = student;
        }

        public List<SelectItem> getGenderItems() {
            return genderItems;
        }

        public void setGenderItems(List<SelectItem> genderItems) {
            this.genderItems = genderItems;
        }

        public List<SelectItem> getCivilityItems() {
            return civilityItems;
        }

        public void setCivilityItems(List<SelectItem> civilityItems) {
            this.civilityItems = civilityItems;
        }

        public List<SelectItem> getPeriodItems() {
            return periodItems;
        }

        public void setPeriodItems(List<SelectItem> periodItems) {
            this.periodItems = periodItems;
        }

        public List<SelectItem> getDepartmentItems() {
            return departmentItems;
        }

        public void setDepartmentItems(List<SelectItem> departmentItems) {
            this.departmentItems = departmentItems;
        }

        public List<SelectItem> getBacItems() {
            return bacItems;
        }

        public void setBacItems(List<SelectItem> bacItems) {
            this.bacItems = bacItems;
        }

        public List<SelectItem> getPrepItems() {
            return prepItems;
        }

        public void setPrepItems(List<SelectItem> prepItems) {
            this.prepItems = prepItems;
        }

        public List<SelectItem> getClassItems() {
            return classItems;
        }

        public void setClassItems(List<SelectItem> classItems) {
            this.classItems = classItems;
        }

    }
}
