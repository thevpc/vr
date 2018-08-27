package net.vpc.app.vainruling.plugins.enisoinfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.content.ArticlesDisposition;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;
import net.vpc.app.vainruling.core.service.util.Arg;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Document;
import net.vpc.upa.EntityUsage;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.RemoveOptions;
import net.vpc.upa.UPA;
import net.vpc.upa.persistence.UConnection;
import net.vpc.upa.types.I18NString;

@VrPlugin
public class ENISoInfoPlugin {

    protected static Logger log = Logger.getLogger(ENISoInfoPlugin.class.getName());

    private CorePlugin core;

    @Install
    private void onInstall() {
        if (core == null) {
            core = CorePlugin.get();
        }
        core.setAppProperty("System.App.Title", null, "Eniso.info");
        core.setAppProperty("System.App.Description", null, "ENISo Computer Science Department Web Site");
        core.setAppProperty("System.App.Keywords", null, "eniso");
        core.setAppProperty("System.App.Title.Major.Main", null, "Eniso");
        core.setAppProperty("System.App.Title.Major.Secondary", null, "info");
        core.setAppProperty("System.App.Title.Minor.Main", null, "Eniso");
        core.setAppProperty("System.App.Title.Minor.Secondary", null, "info");
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
        if (core == null) {
            core = CorePlugin.get();
        }
        updateVersionNative();
        updateVersion();
    }

    private void updateVersionNative() {
        String sql = "";
//                = "update ACADEMIC_TEACHER set CONTACT_ID=null;\n"
//                + "update ACADEMIC_STUDENT set CONTACT_ID=null;\n"
//                + "alter table ACADEMIC_TEACHER drop foreign key ACADEMIC_TEACHER_CONTACT;\n"
//                + "alter table ACADEMIC_STUDENT drop foreign key ACADEMIC_STUDENT_CONTACT;\n"
//                + "";

        PersistenceUnit pu = UPA.getPersistenceUnit();
        UConnection cnx = null;
        try {
            cnx = pu.getPersistenceStore().createConnection();
            for (String line : sql.split(";")) {
                line = line.trim();
                if (line.length() > 0) {
                    cnx.beginTransaction();
                    boolean ok = false;
                    try {
                        cnx.executeNonQuery(line, null, null);
                        ok = true;
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Error in : " + line, ex);
                        cnx.rollbackTransaction();
                    }
                    if (ok) {
                        cnx.commitTransaction();
                    }
                }
            }
        } finally {
            if (cnx != null) {
                cnx.close();
            }
        }

    }

    private void updateVersion() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
    }
}
