package com.mycompany.main.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.content.ArticlesDisposition;
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.Start;
import org.springframework.beans.factory.annotation.Autowired;

@VrPlugin
public class MyProjectPlugin {
    @Autowired
    private CorePlugin core;

    @Install
    private void onInstall() {
        core.setAppProperty("System.App.Title", null, "my-project");
        core.setAppProperty("System.App.Description", null, "my-project");
        core.setAppProperty("System.App.Keywords", null, "my-project");
        core.setAppProperty("System.App.Title.Major.Main", null, "my-project");
        core.setAppProperty("System.App.Title.Major.Secondary", null, "app");
        core.setAppProperty("System.App.Title.Minor.Main", null, "my-project");
        core.setAppProperty("System.App.Title.Minor.Secondary", null, "app");
        core.setAppProperty("System.App.Copyrights.Date", null, "2015-2017");
        core.setAppProperty("System.App.Copyrights.Author.Name", null, "Taha Ben Salah");
        core.setAppProperty("System.App.Copyrights.Author.URL", null, "http://tahabensalah.net");
        core.setAppProperty("System.App.Copyrights.Author.Affiliation", null, "ENISo");

        for (String[] n : new String[][]{{"II", "Informatique Industrielle"}, {"EI", "Electronique Indstrielle"}, {"MA", "Mecanique Avancee"}, {"ADM", "Administration"}}) {
            core.findOrCreateAppDepartment(n[0], n[0], n[1]);
        }
        ArticlesDisposition education = core.findOrCreateDisposition("Services", "Education", "Education");
        //force to Education
        education.setDescription("Education");
        education.setActionName("Education");
        core.save("ArticlesDisposition", education);

    }

    @Start
    private void onStart() {
    }

}