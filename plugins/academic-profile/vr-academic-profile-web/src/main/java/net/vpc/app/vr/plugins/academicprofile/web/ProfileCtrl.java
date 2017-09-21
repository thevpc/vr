/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.plugins.academicprofile.web;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUserType;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.UCtrlData;
import net.vpc.app.vainruling.core.web.UCtrlProvider;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;

//@VrController(menu = "/Config", title = "Choix du profil")
public class ProfileCtrl implements UCtrlProvider {

    @Override
    public UCtrlData getUCtrl(String cmd) {
        AppUserType t = UserSession.get().getUser().getType();
        UCtrlData uCtrlData = null;
        if (t.getName().equals("Student")) {
            VrApp.getBean(StudentProfileSettingsCtrl.class);
            uCtrlData = new UCtrlData(
                    //le label du menu à gauche 
                    "Mon Profil", "je suis etudiant",
                    //l'url à afficher
                    "modules/academic/profile/student-profile-settings",
                    //l'icone dans le menu arbo (gauche) 
                    "fa-dashboard", "Custom.MyProfile",
                    //le breadcrumb affiché à droite en haut
                    new BreadcrumbItem("Mon Profil", "je suis Student", "fa-dashboard", "", "")
            );
        }
        else if(t.getName().equals("Teacher")){
            VrApp.getBean(TeacherProfileSettingsCtrl.class);
            uCtrlData = new UCtrlData(
                    //le label du menu à gauche 
                    "Mon Profil", "je suis enseignant",
                    //l'url à afficher
                    "modules/academic/profile/my-profile",
                    //l'icone dans le menu arbo (gauche) 
                    "fa-dashboard", "Custom.MyProfile",
                    //le breadcrumb affiché à droite en haut
                    new BreadcrumbItem("Mon Profil", "je suis Student", "fa-dashboard", "", "")
            );
        }
        return uCtrlData;
    }

}
