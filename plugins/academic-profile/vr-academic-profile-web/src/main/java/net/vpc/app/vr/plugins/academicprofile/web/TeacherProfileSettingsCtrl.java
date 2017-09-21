/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.plugins.academicprofile.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vr.plugins.academicprofile.service.AcademicProfilePlugin;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCV;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCVItem;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author olfa
 */
//@VrController(
//        breadcrumb = {
//            @UPathItem(title = "Paramètres", css = "fa-dashboard", ctrl = "")},
//        //        css = "fa-table",
//        title = "Mon profil enseignant",
//        menu = "/Config",
//        url = "modules/academic/profile/teacher-profile-settings"
//)
public class TeacherProfileSettingsCtrl {

    @Autowired
    private AcademicProfilePlugin app;
    @Autowired
    private CorePlugin cp;

    private static final Logger log = Logger.getLogger(TeacherProfileSettingsCtrl.class.getName());

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    private void onPageReload() {
        Vr vr = Vr.get();
        getModel().setContact(vr.getUserSession().getUser().getContact());
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        getModel().setTeacher(ap.findTeacherByUser(vr.getUserSession().getUser().getId()));
        getModel().setTeacherCV(app.findOrCreateAcademicTeacherCV(getModel().getTeacher().getId()));
        getModel().setExperienceSectionList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), 1));
        
        List<SelectItem> list = null;

        list = new ArrayList<>();
        for (AppCompany x : cp.findCompanies()) {
            list.add(new SelectItem(x.getId(), x.getName()));
        }
        getModel().setCompanyItems(list);

    }

    @OnPageLoad
    public void onRefresh(String cmd) {

    }
    
    public void addNewCourseItem() {
        
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().courseItem);
                    FacesUtils.addInfoMessage(null, "Opération d'ajout réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }
    
    public void addNewEducationItem() {
        
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().educationItem);
                    FacesUtils.addInfoMessage(null, "Opération d'ajout réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }

    public void addNewExpeienceItem() {
        
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().experienceItem);
                    FacesUtils.addInfoMessage(null, "Opération d'ajout réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }
    
     public void addNewProjectItem() {
        
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().projectItem);
                    FacesUtils.addInfoMessage(null, "Opération d'ajout réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }
    
    public void addNewResearchItem() {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().researchItem);
                    FacesUtils.addInfoMessage(null, "Opération d'ajout réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }
    
    public void addCvItem() {
        
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().cvItem);
                    FacesUtils.addInfoMessage(null, "Opération d'ajout réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }

    public void deleteCvItem(AcademicTeacherCVItem item) {
        app.removeTeacherCvItem(item.getId());
    }

    public void updateBasicInformationSection() {
        getModel().contact.setEmail(getModel().contact.getEmail());
        getModel().contact.setPhone1(getModel().contact.getPhone1());
        getModel().contact.setPhone2(getModel().contact.getPhone2());
        getModel().contact.setPhone3(getModel().contact.getPhone3());
        getModel().contact.setOfficeLocationNumber(getModel().contact.getOfficeLocationNumber());
        getModel().contact.setOfficePhoneNumber(getModel().contact.getOfficePhoneNumber());

        getModel().teacherCV.setWwwURL(getModel().teacherCV.getWwwURL());
        getModel().teacherCV.setSocialURL1(getModel().teacherCV.getSocialURL1());
        getModel().teacherCV.setSocialURL2(getModel().teacherCV.getSocialURL2());
        getModel().teacherCV.setSocialURL3(getModel().teacherCV.getSocialURL3());
        getModel().teacherCV.setSocialURL4(getModel().teacherCV.getSocialURL4());

        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.updateContactInformations(getModel().contact);
                    app.updateTeacherCVInformations(getModel().teacherCV);
                    FacesUtils.addInfoMessage(null, "Modifications enregistrées");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }

    public void updateAboutSection() {
        getModel().contact.setDescription(getModel().contact.getDescription());

        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.updateContactInformations(getModel().contact);
                    FacesUtils.addInfoMessage(null, "Modifications enregistrées");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }


    public void uploadPhoto() {

    }

    public static class Model {

        private AcademicTeacher teacher;
        private AppContact contact;
        private AcademicTeacherCV teacherCV;
        List<SelectItem> companyItems = new ArrayList<>();
        List<AcademicTeacherCVItem> experienceSectionList = new ArrayList<>();
        List<SelectItem> educationSectionList = new ArrayList<>();
        List<SelectItem> projectSectionList = new ArrayList<>();
        List<SelectItem> researchSectionList = new ArrayList<>();
        List<SelectItem> coursesSectionList = new ArrayList<>();
        List<SelectItem> skillSectionList = new ArrayList<>();
        private AcademicTeacherCVItem cvItem;
        private AcademicTeacherCVItem courseItem = new AcademicTeacherCVItem();
        private AcademicTeacherCVItem educationItem = new AcademicTeacherCVItem();
        private AcademicTeacherCVItem experienceItem = new AcademicTeacherCVItem();
        private AcademicTeacherCVItem projectItem = new AcademicTeacherCVItem();
        private AcademicTeacherCVItem researchItem = new AcademicTeacherCVItem();

        public Model() {
        }

        public AcademicTeacherCVItem getExperienceItem() {
            return experienceItem;
        }

        public void setExperienceItem(AcademicTeacherCVItem experienceItem) {
            this.experienceItem = experienceItem;
        }

        public AcademicTeacherCVItem getResearchItem() {
            return researchItem;
        }

        public void setResearchItem(AcademicTeacherCVItem researchItem) {
            this.researchItem = researchItem;
        }

        public AcademicTeacherCVItem getCourseItem() {
            return courseItem;
        }

        public void setCourseItem(AcademicTeacherCVItem courseItem) {
            this.courseItem = courseItem;
        }

        public AcademicTeacherCVItem getEducationItem() {
            return educationItem;
        }

        public void setEducationItem(AcademicTeacherCVItem educationItem) {
            this.educationItem = educationItem;
        }

        public AcademicTeacherCVItem getProjectItem() {
            return projectItem;
        }

        public void setProjectItem(AcademicTeacherCVItem projectItem) {
            this.projectItem = projectItem;
        }
        
        public AcademicTeacherCVItem getCvItem() {
            return cvItem;
        }

        public void setCvItem(AcademicTeacherCVItem cvItem) {
            this.cvItem = cvItem;
        }

        public AcademicTeacher getTeacher() {
            return teacher;
        }

        public void setTeacher(AcademicTeacher teacher) {
            this.teacher = teacher;
        }

        public AcademicTeacherCV getTeacherCV() {
            return teacherCV;
        }

        public void setTeacherCV(AcademicTeacherCV teacherCV) {
            this.teacherCV = teacherCV;
        }

        public AppContact getContact() {
            return contact;
        }

        public void setContact(AppContact contact) {
            this.contact = contact;
        }

        public List<SelectItem> getCompanyItems() {
            return companyItems;
        }

        public void setCompanyItems(List<SelectItem> companyItems) {
            this.companyItems = companyItems;
        }

        public List<AcademicTeacherCVItem> getExperienceSectionList() {
            return experienceSectionList;
        }

        public void setExperienceSectionList(List<AcademicTeacherCVItem> experienceSectionList) {
            this.experienceSectionList = experienceSectionList;
        }

    }
}
