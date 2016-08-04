/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.devtoolbox.service;

import org.springframework.stereotype.Service;

/**
 * @author taha.bensalah@gmail.com
 */
@Service
public class DevSrv {

    public void doUpgrade() {
//        resetContacts();
//        doUpgradeUsers();
//        doUpgradeTeachers();
//        doUpgradeStuents();
    }

//    private void resetContacts() {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        UConnection cnx = pu.getPersistenceStore().getConnection();
//        cnx.executeNonQuery("update VR.ACADEMIC_TEACHER set CONTACT_ID=null", null, null);
//        cnx.executeNonQuery("update VR.ACADEMIC_STUDENT set CONTACT_ID=null", null, null);
////        cnx.executeNonQuery("update VR.ACADEMIC_FORMER_STUDENT set CONTACT_ID=null", null, null);
//        cnx.executeNonQuery("update VR.APP_USER set CONTACT_ID=null", null, null);
//        cnx.executeNonQuery("delete from VR.APP_CONTACT", null, null);
//    }
//
//    private Integer findContactForTable(String tableName, String colName, int id) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        CorePlugin c = VrApp.getBean(CorePlugin.class);
//        UConnection cnx = pu.getPersistenceStore().getConnection();
//        String query = "Select ID,FULL_NAME FROM VR.APP_CONTACT Where FULL_NAME=(Select " + colName + " from VR." + tableName + " where ID=" + id + ")";
//        QueryResult q = cnx.executeQuery(query, null, null, false);
//        while (q.hasNext()) {
//            int t = (Integer) q.read(0);
//            String fn = (String) q.read(1);
//            System.out.println(tableName+"["+id+"] = "+fn);
//            return t;
//        }
//        return null;
//    }
//
//    public void doUpgradeUsers() {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        UConnection cnx = pu.getPersistenceStore().getConnection();
//        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        for (AppUser u : core.findUsers()) {
//            if (u.getContact() == null) {
//                Integer contactID = findContactForTable("APP_USER", "FULL_NAME", u.getId());
//                if (contactID == null) {
//                    AppContact cc = new AppContact();
//                    pu.persist(cc);
//                    u.setContact(cc);
//                    pu.merge(u);
//                }else{
//                    u.setContact(core.findContact(contactID));
//                    pu.merge(u);
//                }
//            }
//        }
//         for (String[] fromTo : new String[][]{
//            {"APP_USER","FULL_NAME", "FULL_NAME"}
//            ,{"APP_USER","FIRST_NAME", "FIRST_NAME"}
//            ,{"APP_USER","LAST_NAME", "LAST_NAME"}
//            ,{"APP_USER","EMAIL", "EMAIL"}
//            ,{"APP_USER","CIVITITY_ID", "CIVILITY_ID"}
//            ,{"APP_USER","GENDER_ID", "GENDER_ID"}
//            ,{"APP_USER","NIN", "NIN"}
//            ,{"APP_USER","POSITION_TITLE1", "POSITION_TITLE1"}
//            ,{"APP_USER","POSITION_TITLE2", "POSITION_TITLE2"}
//            ,{"APP_USER","POSITION_TITLE3", "POSITION_TITLE3"}
//            ,{"APP_USER","COMPANY_ID", "COMPANY_ID"}
//
//        
//        }) {
//            String FROM_TAB = fromTo[0];
//            String FROM_COL = fromTo[1];
//            String TO = fromTo[2];
//            int c = cnx.executeNonQuery("Update VR.APP_CONTACT set " + TO + "=(Select A." + FROM_COL + " from VR."+FROM_TAB+" A WHERE A.CONTACT_ID=VR.APP_CONTACT.ID) where " + TO + " IS NULL", null, null);
//            System.out.println(FROM_TAB+"."+FROM_COL + " => APP_CONTACT." + TO + " : " + c);
//        }
//    }
//    public void doUpgradeTeachers() {
//         PersistenceUnit pu = UPA.getPersistenceUnit();
//        UConnection cnx = pu.getPersistenceStore().getConnection();
//        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        AcademicPlugin ac = VrApp.getBean(AcademicPlugin.class);
//         for (AcademicTeacher u : ac.findTeachers()) {
//            if (u.getContact() == null) {
//                Integer contactID = findContactForTable("ACADEMIC_TEACHER", "NAME", u.getId());
//                if (contactID == null) {
//                    AppContact cc = new AppContact();
//                    pu.persist(cc);
//                    u.setContact(cc);
//                    pu.merge(u);
//                } else {
//                    u.setContact(core.findContact(contactID));
//                    pu.merge(u);
//                }
//            }
//        }
//          for (String[] fromTo : new String[][]{
//           
//            {"ACADEMIC_TEACHER","NAME", "FULL_NAME"}
//            ,{"ACADEMIC_TEACHER","FIRST_NAME", "FIRST_NAME"}
//            ,{"ACADEMIC_TEACHER","LAST_NAME", "LAST_NAME"}
//            ,{"ACADEMIC_TEACHER","NAME2", "FULL_NAME2"}
//            ,{"ACADEMIC_TEACHER","FIRST_NAME2", "FIRST_NAME2"}
//            ,{"ACADEMIC_TEACHER","LAST_NAME2", "LAST_NAME2"}
//            ,{"ACADEMIC_TEACHER","EMAIL", "EMAIL"}
//            ,{"ACADEMIC_TEACHER","CIVITITY_ID", "CIVILITY_ID"}
//            ,{"ACADEMIC_TEACHER","GENDER_ID", "GENDER_ID"}
//            ,{"ACADEMIC_TEACHER","NIN", "NIN"}
////            ,{"ACADEMIC_TEACHER","TITLE1", "POSITION_TITLE1"}
////            ,{"ACADEMIC_TEACHER","TITLE2", "POSITION_TITLE2"}
////            ,{"ACADEMIC_TEACHER","TITLE3", "POSITION_TITLE3"}
//            ,{"ACADEMIC_TEACHER","PHONE1", "PHONE1"}
//            ,{"ACADEMIC_TEACHER","PHONE2", "PHONE2"}
//            ,{"ACADEMIC_TEACHER","PHONE3", "PHONE3"}
////            ,{"ACADEMIC_TEACHER","COMPANY_ID", "COMPANY_ID"}
//            ,{"ACADEMIC_TEACHER","NIN", "NIN"}
//        }) {
//            String FROM_TAB = fromTo[0];
//            String FROM_COL = fromTo[1];
//            String TO = fromTo[2];
//            int c = cnx.executeNonQuery("Update VR.APP_CONTACT set " + TO + "=(Select A." + FROM_COL + " from VR."+FROM_TAB+" A WHERE A.CONTACT_ID=VR.APP_CONTACT.ID) where " + TO + " IS NULL", null, null);
//            System.out.println(FROM_TAB+"."+FROM_COL + " => APP_CONTACT." + TO + " : " + c);
//        }
//    }
//    public void doUpgradeStuents() {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        UConnection cnx = pu.getPersistenceStore().getConnection();
//        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        AcademicPlugin ac = VrApp.getBean(AcademicPlugin.class);
//        
//       
//        for (AcademicStudent u : ac.findStudents()) {
//            if (u.getContact() == null) {
//                Integer contactID = findContactForTable("ACADEMIC_STUDENT", "NAME", u.getId());
//                if (contactID == null) {
//                    AppContact cc = new AppContact();
//                    pu.persist(cc);
//                    u.setContact(cc);
//                    pu.merge(u);
//                } else {
//                    u.setContact(core.findContact(contactID));
//                    pu.merge(u);
//                }
//            }
//        }
//
//        for (String[] fromTo : new String[][]{
//           
//            {"ACADEMIC_STUDENT","NAME", "FULL_NAME"}
//            ,{"ACADEMIC_STUDENT","FIRST_NAME", "FIRST_NAME"}
//            ,{"ACADEMIC_STUDENT","LAST_NAME", "LAST_NAME"}
//            ,{"ACADEMIC_STUDENT","NAME2", "FULL_NAME2"}
//            ,{"ACADEMIC_STUDENT","FIRST_NAME2", "FIRST_NAME2"}
//            ,{"ACADEMIC_STUDENT","LAST_NAME2", "LAST_NAME2"}
//            ,{"ACADEMIC_STUDENT","EMAIL", "EMAIL"}
//            ,{"ACADEMIC_STUDENT","CIVITITY_ID", "CIVILITY_ID"}
//            ,{"ACADEMIC_STUDENT","GENDER_ID", "GENDER_ID"}
//            ,{"ACADEMIC_STUDENT","NIN", "NIN"}
//        }) {
//            String FROM_TAB = fromTo[0];
//            String FROM_COL = fromTo[1];
//            String TO = fromTo[2];
//            int c = cnx.executeNonQuery("Update VR.APP_CONTACT set " + TO + "=(Select A." + FROM_COL + " from VR."+FROM_TAB+" A WHERE A.CONTACT_ID=VR.APP_CONTACT.ID) where " + TO + " IS NULL", null, null);
//            System.out.println(FROM_TAB+"."+FROM_COL + " => APP_CONTACT." + TO + " : " + c);
//        }
//    }
}
