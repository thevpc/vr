/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service;

import net.vpc.app.vainruling.core.service.*;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.InstallDemo;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.plugins.equipments.service.model.*;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Utils;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import java.sql.Timestamp;
import java.util.*;
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin()
@DependsOn("corePlugin")
public class EquipmentPlugin {

    @Autowired
    CorePlugin core;

    @Start
    private void start(){
        //EquipmentPluginHelper
        for (AppDepartment d : core.findDepartments()) {
            EquipmentPluginHelper.ensureCreatedEquipmentDepartmentUpdateRight(d);
        }
    }

    public Equipment findEquipment(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (Equipment) pu.findById(Equipment.class, id);
    }

    public List<Equipment> findEquipments() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a from Equipment a order by a.name")
                .getResultList();

    }

    public List<Equipment> findEquipmentsByType(int typeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a from Equipment a where a.typeId=:typeId order by a.name")
                .setParameter("typeId", typeId)
                .getResultList();

    }

//    //TODO
//    public List<Equipment> findEquipmentsByArea(int typeId, int areaId, boolean deep) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select a from Equipment a where a.typeId=:typeId order by a.name")
//                .setParameter("typeId", typeId)
//                .setParameter("areaId", areaId)
//                .setParameter("deep", deep)
//                .getResultList();
//
//    }

    public Equipment copyEquipment(Equipment eq){
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Equipment eq2=new Equipment();
        eq2.setName(eq.getName());
        eq2.setQuantity(eq.getQuantity());
        eq2.setAcquisition(eq.getAcquisition());
        eq2.setArchived(eq.isArchived());
        eq2.setBrandLine(eq.getBrandLine());
        eq2.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        eq2.setDeleted(eq.isDeleted());
        eq2.setDeletedBy(eq.getDeletedBy());
        eq2.setDeletedOn(eq.getDeletedOn());
        eq2.setDepartment(eq.getDepartment());
        eq2.setDescription(eq.getDescription());
        eq2.setLocation(eq.getLocation());
        eq2.setRelativeTo(eq.getRelativeTo());
        eq2.setSerial(eq.getSerial());
//        if(!StringUtils.isEmpty(eq.getSerial())) {
//            eq2.setSerial(eq.getSerial() + "_" + i);
//        }
        eq2.setStatusType(eq.getStatusType());
        eq2.setStockSerial(eq.getStockSerial());
//        if(!StringUtils.isEmpty(eq.getStockSerial())) {
//            eq2.setStockSerial(eq.getStockSerial());
//        }
        eq2.setType(eq.getType());
        pu.persist(eq2);

        for (Object child : pu.createQuery("Select e from Equipment e where e.relativeToId=:id").setParameter("id", eq.getId()).getResultList()) {
            Equipment child2 = copyEquipment((Equipment) child);
            child2.setRelativeTo(eq2);
            pu.merge(child2);
        }
        return eq2;
    }

    public int splitEquipmentQuantities(int equipmentId){
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(Equipment.class);
        Equipment eq = entity.findById(equipmentId);
        if(Utils.isInteger(eq.getQuantity())){
            int qte=(int) eq.getQuantity();
            if(qte>1) {
                for (int i = 2; i < qte+1; i++) {
                    Equipment eq2=copyEquipment(eq);
                    eq2.setName(eq.getName()+" "+i);
                    if(!StringUtils.isEmpty(eq.getSerial())) {
                        eq2.setSerial(eq.getSerial() + "_" + i);
                    }
                    if(!StringUtils.isEmpty(eq.getStockSerial())) {
                        eq2.setStockSerial(eq.getStockSerial());
                    }
                    eq2.setQuantity(1);
                    pu.merge(eq2);
                }
                eq.setQuantity(1);
                pu.merge(eq);
                return qte;
            }
        }
        return 0;
    }

    @Install
    private void installService() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);

        AppUserType technicianType;
        technicianType = new AppUserType();
        technicianType.setName("Technician");
        technicianType = core.findOrCreate(technicianType);

        AppProfile technicianProfile;
        technicianProfile = new AppProfile();
        technicianProfile.setName("Technician");
        technicianProfile = core.findOrCreate(technicianProfile);

        AppProfile headOfDepartment;
        headOfDepartment = new AppProfile();
        headOfDepartment.setName(CorePlugin.PROFILE_HEAD_OF_DEPARTMENT);
        headOfDepartment = core.findOrCreate(headOfDepartment);

//        core.profileAddRight(headOfDepartment.getId(),AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_COURSE_LOAD);
        for (net.vpc.upa.Entity ee : UPA.getPersistenceUnit().getPackage("Equipment").getEntities(true)) {
            for (String right : CorePluginSecurity.getEntityRights(ee, true, true, true, false, false)) {
                core.addProfileRight(headOfDepartment.getId(),right);
            }
            for (String right : CorePluginSecurity.getEntityRights(ee, true, true, false, false, false)) {
                core.addProfileRight(technicianProfile.getId(),right);
            }
        }

//        AppContact techContact = new AppContact();
//        AppUser tech1 = new AppUser();
//        tech1.setEnabled(true);
//        techContact.setFirstName("riadh");
//        techContact.setLastName("tech");
//        techContact.setFullName("riadh");
//        tech1.setUserLogin("riadh.chikhaoui");
//        tech1.setPassword("riadh");
//        techContact.setCivility(core.findCivility("M."));
//        techContact.setEmail("riadh@vr.net");
//        techContact.setGender(core.findGender("H"));
//        tech1.setType(technicianType);
//        techContact = core.findOrCreateContact(techContact);
//        tech1.setContact(techContact);
//        tech1 = core.findOrCreate(tech1);

//        techContact = new AppContact();
//        AppUser tech2 = new AppUser();
//        tech2.setEnabled(true);
//        techContact.setFirstName("sameh");
//        techContact.setLastName("tech");
//        techContact.setFullName("techsameh");
//        tech2.setUserLogin("sameh.gassab");
//        tech2.setPassword("sameh");
//        techContact.setCivility(core.findCivility("Mme"));
//        techContact.setEmail("sameh@vr.net");
//        techContact.setGender(core.findGender("F"));
//        tech2.setType(technicianType);
//        techContact = core.findOrCreateContact(techContact);
//        tech2.setContact(techContact);
//        tech2 = core.findOrCreate(tech2);
//
//        core.userAddProfile(tech1.getId(), "Technician");
//        core.userAddProfile(tech2.getId(), "Technician");
    }

    @InstallDemo
    private void installDemoService() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        InitData initData = new InitData();
        initData.areaType_etablissement = core.findOrCreate(initData.areaType_etablissement);
        initData.areaType_bloc = core.findOrCreate(initData.areaType_bloc);
        initData.areaType_salle = core.findOrCreate(initData.areaType_salle);
        initData.areaType_armoire = core.findOrCreate(initData.areaType_armoire);
        initData.areaType_rangement = core.findOrCreate(initData.areaType_rangement);

        List<AppArea> areas = new ArrayList<>();
        List<AppArea> salles = new ArrayList<>();
        AppArea areaEniso = new AppArea("Eniso", null, initData.areaType_etablissement, null);
        areaEniso = core.findOrCreate(areaEniso);
        areas.add(areaEniso);
        AppArea areaBlocA = new AppArea("Bloc A", null, initData.areaType_bloc, areaEniso);
        areaBlocA = core.findOrCreate(areaBlocA);
        areas.add(areaBlocA);
        AppArea areaBlocB = new AppArea("Bloc B", null, initData.areaType_bloc, areaEniso);
        areaBlocB = core.findOrCreate(areaBlocB);
        areas.add(areaBlocB);

        AppArea areaBlocII = new AppArea("Bloc II", null, initData.areaType_bloc, areaEniso);
        areaBlocII = core.findOrCreate(areaBlocII);
        areas.add(areaBlocII);
        for (String s : new String[]{"II01", "II02", "II03", "II12", "II13", "II21", "II22"}) {
            AppArea salle = new AppArea(s, null, initData.areaType_salle, areaBlocII);
            salle = core.findOrCreate(salle);
            areas.add(salle);
            salles.add(salle);
        }

//        List<EquipmentBrand> brands = new ArrayList<>();
//        List<EquipmentBrandLine> brandLines = new ArrayList<>();
//        List<EquipmentType> eqTypes = new ArrayList<>();
//        List<EquipmentTypeGroup> eqTypesGroups = new ArrayList<>();
        Map<String, Object> cached = new HashMap<String, Object>();

        for (String n : new String[]{
                "Materiel Info/PC Desktop/HP/Pavillon/Pavillon 123",
                "Materiel Info/PC Portable/HP/Pavillon/Pavillon 456",
                "Materiel Info/PC Desktop/TOSHIBA/Satellite/Sat 222",
                "Materiel Info/PC Portable/TOSHIBA/Satellite/Expatria",
                "Materiel Info/Imprimante/CANON/LBP/LBP2900",
                "Materiel Info/Imprimante/CANON/LBP/LBP2900B",
                "Materiel Info/Scanner/CANON/ScanLite/ScanLite200",
                "Materiel Info/PC Portable/IBM/Thinkpad/T200",}) {
            String[] nn = n.split("/");
            String eqTypeGroupName = nn[0];
            String eqTypeName = nn[1];
            String eqBrandName = nn[2];
            String eqLineName = nn[3];
            String eqName = nn[4];

            EquipmentTypeGroup eqTypeGroup = (EquipmentTypeGroup) cached.get(eqTypeGroupName);
            if (eqTypeGroup == null) {
                eqTypeGroup = new EquipmentTypeGroup();
                eqTypeGroup.setName(eqTypeGroupName);
                eqTypeGroup = core.findOrCreate(eqTypeGroup);
                cached.put(eqTypeGroupName, eqTypeGroup);
            }

            EquipmentType eqType = (EquipmentType) cached.get(eqTypeName);
            if (eqType == null) {
                eqType = new EquipmentType();
                eqType.setTypeGroup(eqTypeGroup);
                eqType.setName(eqTypeName);
                eqType = core.findOrCreate(eqType);
                cached.put(eqTypeName, eqType);
            }

            EquipmentBrand eqBrand = (EquipmentBrand) cached.get(eqBrandName);
            if (eqBrand == null) {
                eqBrand = new EquipmentBrand();
                eqBrand.setName(eqBrandName);
                eqBrand = core.findOrCreate(eqBrand);
                cached.put(eqBrandName, eqBrand);
            }

            EquipmentBrandLine eqBrandLine = (EquipmentBrandLine) cached.get(eqLineName);
            if (eqBrandLine == null) {
                eqBrandLine = new EquipmentBrandLine();
                eqBrandLine.setBrand(eqBrand);
                eqBrandLine.setName(eqLineName);
                eqBrandLine = core.findOrCreate(eqBrandLine);
                cached.put(eqLineName, eqBrandLine);
            }

            Equipment e = new Equipment();
            e.setName(eqName);
            e.setSerial(UUID.randomUUID().toString());
            e.setType(eqType);
            e.setBrandLine(eqBrandLine);
            e.setStatusType(Utils.rand(EquipmentStatusType.class));
            e.setLocation(Utils.rand(salles));
            e = core.findOrCreate(e);
            int count = pu.createQueryBuilder(EquipmentProperty.class).byExpression("equipmentId=" + e.getId()).getResultList().size();
            if (count == 0) {
                for (String nnn : new String[]{"Color", "Width", "Height"}) {
                    EquipmentProperty x = new EquipmentProperty();
                    x.setName(nnn);
                    x.setValue("100");
                    x.setEquipment(e);
                    pu.persist(x);
                }
            }
        }
    }

    private static class InitData {

        AppAreaType areaType_etablissement = new AppAreaType("etablissement");
        AppAreaType areaType_bloc = new AppAreaType("bloc");
        AppAreaType areaType_salle = new AppAreaType("salle");
        AppAreaType areaType_armoire = new AppAreaType("armoire");
        AppAreaType areaType_rangement = new AppAreaType("rangement");
    }

}
