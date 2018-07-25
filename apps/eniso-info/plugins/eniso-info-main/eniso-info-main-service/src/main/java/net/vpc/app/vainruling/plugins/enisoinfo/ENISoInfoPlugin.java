package net.vpc.app.vainruling.plugins.enisoinfo;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.content.ArticlesDisposition;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.List;
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;
import net.vpc.app.vainruling.plugins.academic.service.examples.model.Ambulance;

@VrPlugin
public class ENISoInfoPlugin {
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
        updateVersion();
    }

    private void updateVersion() {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        AcademicPlugin academicPlugin = AcademicPlugin.get();
//        ApblPlugin apblPlugin = ApblPlugin.get();
//        for (AppPeriod periods : core.findPeriods()) {
//            ListValueMap<String, AcademicCoursePlan> map = new ListValueMap<>();
//            for (AcademicCoursePlan coursePlan : academicPlugin.findCoursePlans(periods.getId())) {
//                String cls = coursePlan.getCourseLevel().getAcademicClass().getName();
//                String sem = coursePlan.getCourseLevel().getSemester().getCode();
//                String name = coursePlan.getName();
//                String period = coursePlan.getPeriod().getName();
//                String id = cls + "-" + sem + "-" + period + "-" + name;
//                map.put(id, coursePlan);
//            }
//            for (String id : map.keySet()) {
//                List<AcademicCoursePlan> list = map.get(id);
//                for (int i = 1; i < list.size(); i++) {
//                    AcademicCoursePlan ref = list.get(0);
//                    AcademicCoursePlan curr = list.get(i);
//                    for (AcademicCourseAssignment assignment : academicPlugin.findAcademicCourseAssignmentListByCoursePlanId(curr.getId())) {
//                        assignment.setCoursePlan(ref);
//                        pu.merge(assignment);
//                    }
//                    for (ApblProgramSession session : apblPlugin.findProgramSessionsByCoursePlan(curr.getId())) {
//                        session.setCourse(ref);
//                        pu.merge(session);
//                    }
//                    pu.remove(curr);
//                }
//            }
//        }

    }
    
    public void kanbelOAllAmbulances(){
        PersistenceUnit pu = UPA.getPersistenceUnit();
        //pu.clear(Ambulance.class,null);
        List<Ambulance> toutes=pu.findAll(Ambulance.class);
        for (Ambulance a : toutes) {
            //delete
            pu.remove(a);
        }
    }
    
    public List<Ambulance> jeebToutesLesAmbulanceParMarque(String marque){
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findByField(Ambulance.class, "marque", marque);
    }
    
    public List<Ambulance> jeebToutesLesAmbulanceDontLeChauffeurEst(String nomDuChauffeur){
        PersistenceUnit pu = UPA.getPersistenceUnit();
        //UPQL(UPA) == JPQL(JPA) == HQL (Hibernate)
        return pu.createQuery("Select a from Ambulance a where a.chauffeur.nom=:v")
                .setParameter("v", nomDuChauffeur)
                .getResultList();
    }
    public List<Ambulance> jeebToutesLesAmbulanceParMarque2(String marque){
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu
                //pas du SQL
                .createQuery("Select a from Ambulance a where a.marque=:uneVariable")
                .setParameter("uneVariable", marque)
                .getResultList()
                ;
    }
    
    public void echriAmbulance(){
        Ambulance a=new Ambulance();
        a.setImmatriculation("Test 1");
        PersistenceUnit dao = UPA.getPersistenceUnit();
        //insert !!
        dao.persist(a);
        
        a.setMarque("Marc Lavoine");
        //mettre à jour
        dao.merge(a);
        //dao.update(a);
    }

}
