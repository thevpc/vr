/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.service;

import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentActionType;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentBrandLine;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusType;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentType;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentTypeGroup;
import net.vpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentProperty;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentBrand;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusLog;
import net.vpc.app.vainruling.core.service.*;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.PlatformUtils;
import net.vpc.common.util.Utils;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import java.sql.Timestamp;
import java.util.*;

import net.vpc.app.vainruling.VrPlugin;
import net.vpc.app.vainruling.plugins.equipments.aquisition.service.EquipmentAcquisitionService;
import net.vpc.app.vainruling.plugins.equipments.aquisition.model.EquipmentAquisitionLog;
import net.vpc.app.vainruling.plugins.equipments.borrow.service.EquipmentBorrowService;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowLog;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentReturnBorrowedLog;
import net.vpc.app.vainruling.VrInstall;
import net.vpc.app.vainruling.VrInstallDemo;
import net.vpc.app.vainruling.VrStart;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin()
@DependsOn("corePlugin")
public class EquipmentPlugin {

    @Autowired
    CorePlugin core;
    private EquipmentBorrowService borrowService = new EquipmentBorrowService();

    public static EquipmentPlugin get() {
        return VrApp.getBean(EquipmentPlugin.class);
    }

    @VrStart
    private void start() {
        //EquipmentPluginHelper
        for (AppDepartment d : core.findDepartments()) {
            EquipmentPluginHelper.ensureCreatedEquipmentDepartmentUpdateRight(d);
        }
        EquipmentBorrowService borrowSrv = VrApp.getBean(EquipmentBorrowService.class);
        borrowSrv.startService();

    }

    public List<Equipment> findNonMigratableEquipmentStatuses() {
        List<Equipment> errors = new ArrayList<>();
        migrateEquipmentStatuses(false, errors);
        return errors;
    }

    public void migrateEquipmentStatuses(boolean applyMigration, List<Equipment> errors) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Set<Integer> equipmentsWithLog = pu.createQuery("Select distinct(a.equipmentId) from EquipmentStatusLog a").getResultSet();
        for (Integer eqId : equipmentsWithLog) {
            if (eqId != null) {
                if (!migrateEquipmentStatuses(0, applyMigration)) {
                    Equipment eq = pu.findById(Equipment.class, eqId);
                    if (eq != null) {
                        errors.add(eq);
                    }
                }
            }
        }
    }

    public boolean migrateEquipmentStatuses(int eqId, boolean applyMigration) {
        EquipmentBorrowService borrowSrv = VrApp.getBean(EquipmentBorrowService.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        EquipmentActionType acquisition = EquipmentPlugin.get().findOrCreateEquipmentActionType("Acquisition", "Acquisition");
        EquipmentActionType borrow = EquipmentPlugin.get().findOrCreateEquipmentActionType("Borrow", "Borrow");
        EquipmentActionType returnBorrowed = EquipmentPlugin.get().findOrCreateEquipmentActionType("ReturnBorrowed", "Return Borrowed");
        EquipmentAcquisitionService acquisitionSrv = VrApp.getBean(EquipmentAcquisitionService.class);
        boolean migratable = true;
        List<PotsponeAction> todos = new ArrayList<>();
        double borrowedQuantity = 0;
        List<EquipmentBorrowLog> lastEquipmentBorrowLogs = new ArrayList<>();
        List<EquipmentStatusLog> equipmentStatusLogList = pu.createQuery("Select a from EquipmentStatusLog a where a.equipmentId=:equipmentId order by a.startDate").setParameter("equipmentId", eqId).<EquipmentStatusLog>getResultList();
        boolean exitHere = false;
        for (EquipmentStatusLog elog : equipmentStatusLogList) {
            if (exitHere) {
                break;
            }
            EquipmentStatusType elogType = elog.getType();
            if (elogType == null) {
                //why?
            } else {
                switch (elogType) {
                    case ACQUISITION: {
                        EquipmentAquisitionLog alog = acquisitionSrv.findAquisitionLog(elog.getId());
                        if (alog == null) {
                            alog = new EquipmentAquisitionLog();
                            alog.setAcquisition(elog.getAcquisition());
                            alog.setOpDate(elog.getStartDate());
                            alog.setQuantity(elog.getQuantity());
                            alog.setStatusLog(elog);
                            todos.add(new PotsponeActionPersist(pu, alog));
                        }
                        if (elog.getInQty() == 0 && elog.getOutQty() == 0 && elog.getModQty() == 0) {
                            if (elog.getQuantity() > 0) {
                                elog.setInQty(elog.getQuantity());
                                elog.setOutQty(0);
                                elog.setModQty(0);
                                elog.setQuantity(0);
                            } else if (elog.getQuantity() < 0) {
                                elog.setOutQty(-elog.getQuantity());
                                elog.setInQty(0);
                                elog.setModQty(0);
                                elog.setQuantity(0);
                            } else {
                                elog.setInQty(0);
                                elog.setOutQty(0);
                                elog.setModQty(1);
                                elog.setQuantity(0);
                            }
                            if (applyMigration) {
                                pu.merge(elog);
                            } else {
                                todos.add(new PotsponeActionMerge(pu, elog));
                            }
                        }
                        elog.setType(EquipmentStatusType.AVAILABLE);
                        elog.setAcquisition(null);
                        elog.setAction(acquisition);
                        todos.add(new PotsponeActionMerge(pu, elog));
                        break;
                    }
                    case LOST:
                    case UNUSABLE:
                    case TEMPORARILY_UNAVAILABLE:
                    case USABLE_WITH_CARE:
                    case BROKEN: {
                        //do nothing
                        if (elog.getInQty() == 0 && elog.getOutQty() == 0 && elog.getModQty() == 0) {
                            double q = Math.abs(elog.getQuantity());
                            elog.setOutQty(q);
                            elog.setInQty(0);
                            elog.setModQty(0);
                            elog.setQuantity(0);
                            todos.add(new PotsponeActionMerge(pu, elog));
                        }
                        break;
                    }

                    case COMPLAINT:
                    case INTERVENTION_ON_COMPLAINT:
                    case PLANNED_INTERVENTION: {
//do nothing
                        if (elog.getInQty() == 0 && elog.getOutQty() == 0 && elog.getModQty() == 0) {
                            double q = elog.getQuantity();
                            if (q == 0) {
                                q = 1;
                            }
                            elog.setInQty(0);
                            elog.setOutQty(0);
                            elog.setModQty(q);
                            elog.setQuantity(0);
                            todos.add(new PotsponeActionMerge(pu, elog));
                        }
                        break;
                    }

                    case BORROWED: {
                        EquipmentBorrowLog blog = borrowSrv.findBorrowLogByStatusLogId(elog.getId());
//                                EquipmentReturnBorrowedLog rlog = borrowSrv.findReturnBorrowedLog(elog.getId());
                        if (blog == null) {
                            blog = new EquipmentBorrowLog();
                            blog.setQuantity(Math.abs(elog.getQuantity()));
                            blog.setStatusLog(elog);
                            blog.setStartDate(elog.getStartDate());
                            blog.setEndDate(elog.getStartDate());
                            blog.setBorrower(elog.getResponsible());
                            todos.add(new PotsponeActionPersist(pu, blog));
                        }
                        lastEquipmentBorrowLogs.add(blog);
                        elog.setType(EquipmentStatusType.AVAILABLE);
                        elog.setAcquisition(null);
                        elog.setAction(borrow);
                        if (elog.getInQty() == 0 && elog.getOutQty() == 0 && elog.getModQty() == 0) {
                            if (elog.getQuantity() > 0) {
                                elog.setInQty(elog.getQuantity());
                                elog.setOutQty(0);
                                elog.setModQty(0);
                                elog.setQuantity(0);
                            } else if (elog.getQuantity() < 0) {
                                elog.setOutQty(-elog.getQuantity());
                                elog.setInQty(0);
                                elog.setModQty(0);
                                elog.setQuantity(0);
                            } else {
                                elog.setInQty(0);
                                elog.setOutQty(0);
                                elog.setModQty(1);
                                elog.setQuantity(0);
                            }
                        }
                        borrowedQuantity += elog.getOutQty();
                        todos.add(new PotsponeActionMerge(pu, elog));
                        break;
                    }

                    case AVAILABLE:
                    case BORROWABLE: {
                        if (elog.getQuantity() > 0) {
                            EquipmentReturnBorrowedLog rlog = borrowSrv.findReturnBorrowedLogByStatusLogId(elog.getId());
                            if (rlog == null) {
                                EquipmentBorrowLog borrowLog = null;
                                for (int i = lastEquipmentBorrowLogs.size() - 1; i >= 0; i--) {
                                    EquipmentBorrowLog b = lastEquipmentBorrowLogs.get(i);
                                    if (!b.isArchive()) {
                                        AppUser borrower = b.getBorrower();
                                        if (elog.getResponsible() == null && borrower != null && Math.abs(b.getQuantity()) == Math.abs(elog.getQuantity())) {
                                            elog.setResponsible(borrower);
                                            borrowLog = b;
                                            break;
                                        }
                                        if (elog.getResponsible() != null && borrower != null && borrower.getId() == elog.getResponsible().getId()) {
                                            borrowLog = b;
                                            break;
                                        }
                                    }
                                }
                                if (borrowLog == null) {
                                    migratable = false;
                                    break;
                                }
                                if (elog.getInQty() == 0 && elog.getOutQty() == 0 && elog.getModQty() == 0) {
                                    if (elog.getQuantity() > 0) {
                                        elog.setInQty(elog.getQuantity());
                                        elog.setOutQty(0);
                                        elog.setModQty(0);
                                        elog.setQuantity(0);
                                    } else if (elog.getQuantity() < 0) {
                                        elog.setOutQty(-elog.getQuantity());
                                        elog.setInQty(0);
                                        elog.setModQty(0);
                                        elog.setQuantity(0);
                                    } else {
                                        elog.setInQty(0);
                                        elog.setOutQty(0);
                                        elog.setModQty(1);
                                        elog.setQuantity(0);
                                    }
                                }
                                rlog = new EquipmentReturnBorrowedLog();
                                rlog.setBorrowLog(borrowLog);
                                rlog.setQuantity(elog.getInQty());
                                rlog.setStatusLog(elog);
                                rlog.setReturnDate(elog.getStartDate());
                                todos.add(new PotsponeActionPersist(pu, rlog));
                                elog.setType(EquipmentStatusType.AVAILABLE);
                                elog.setAcquisition(null);
                                elog.setEndDate(null);
                                elog.setAction(returnBorrowed);
                                borrowedQuantity -= Math.abs(elog.getInQty());
                                if (borrowedQuantity <= 0) {
                                    borrowLog.setArchive(true);
                                }
                                todos.add(new PotsponeActionMerge(pu, elog));
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (!migratable) {
            return false;
        } else {
            if (applyMigration) {
                for (PotsponeAction todo : todos) {
                    todo.run();
                }
            }
            return true;
        }

    }

    private static interface PotsponeAction {

        void run();
    }

    private static class PotsponeActionMerge implements PotsponeAction {

        PersistenceUnit pu;
        Object o;

        public PotsponeActionMerge(PersistenceUnit pu, Object o) {
            this.pu = pu;
            this.o = o;
        }

        @Override
        public void run() {
            pu.merge(o);
        }
    }

    private static class PotsponeActionPersist implements PotsponeAction {

        PersistenceUnit pu;
        Object o;

        public PotsponeActionPersist(PersistenceUnit pu, Object o) {
            this.pu = pu;
            this.o = o;
        }

        @Override
        public void run() {
            pu.persist(o);
        }
    }

    public void migrate() {
        ArrayList<Equipment> errors = new ArrayList<>();
        migrateEquipmentStatuses(false, errors);
        if (errors.isEmpty()) {
            migrateEquipmentStatuses(true, errors);
        }
    }

    public Equipment findEquipment(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (Equipment) pu.findById(Equipment.class, id);
    }

    public List<AppUser> findUsers() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a.responsible from EquipmentStatusLog a order by a.name")
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
    public Equipment copyEquipment(Equipment eq) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Equipment eq2 = new Equipment();
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
//        if(!StringUtils.isBlank(eq.getSerial())) {
//            eq2.setSerial(eq.getSerial() + "_" + i);
//        }
        eq2.setStatusType(eq.getStatusType());
        eq2.setStockSerial(eq.getStockSerial());
//        if(!StringUtils.isBlank(eq.getStockSerial())) {
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

    public int splitEquipmentQuantities(int equipmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(Equipment.class);
        Equipment eq = entity.findById(equipmentId);
        if (PlatformUtils.isInteger(eq.getQuantity())) {
            int qte = (int) eq.getQuantity();
            if (qte > 1) {
                for (int i = 2; i < qte + 1; i++) {
                    Equipment eq2 = copyEquipment(eq);
                    eq2.setName(eq.getName() + " " + i);
                    if (!StringUtils.isBlank(eq.getSerial())) {
                        eq2.setSerial(eq.getSerial() + "_" + i);
                    }
                    if (!StringUtils.isBlank(eq.getStockSerial())) {
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

    @VrInstall
    private void installService() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);

        AppUserType technicianType = core.findOrCreate(new AppUserType("Technician", "Technician"));

        AppProfile technicianProfile=core.findOrCreateCustomProfile("Technician", "UserType");

        AppProfile headOfDepartment=core.findOrCreateCustomProfile(CorePlugin.PROFILE_HEAD_OF_DEPARTMENT, "UserType");

//        core.profileAddRight(headOfDepartment.getId(),AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_COURSE_LOAD);
        ProfileRightBuilder prb = new ProfileRightBuilder();
        for (net.vpc.upa.Entity ee : UPA.getPersistenceUnit().getPackage("Equipment").getEntities(true)) {
            for (String right : CorePluginSecurity.getEntityRights(ee, true, true, true, false, false)) {
                prb.addProfileRight(headOfDepartment.getId(), right);
            }
            for (String right : CorePluginSecurity.getEntityRights(ee, true, true, false, false, false)) {
                prb.addProfileRight(technicianProfile.getId(), right);
            }
        }
        prb.execute();

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
//        core.addUserProfile(tech1.getId(), "Technician");
//        core.addUserProfile(tech2.getId(), "Technician");
    }

    @VrInstallDemo
    private void installDemoService() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        EquipmentPluginInitData initData = new EquipmentPluginInitData();
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

    public EquipmentStatusLog findEquipmentLatestLog(int equipmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select Top 1 o from EquipmentStatusLog o where o.equipmentId=:id order by o.startDate desc, o.id asc")
                .setParameter("id", equipmentId)
                .getFirstResultOrNull();

    }

    public EquipmentActionType findOrCreateEquipmentActionType(String code, String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<EquipmentActionType> oldItems = pu.findAllByField(EquipmentActionType.class, "code", code);
        if (!oldItems.isEmpty()) {
            return oldItems.get(0);
        }
        EquipmentActionType newItem = new EquipmentActionType();
        newItem.setCode(code);
        newItem.setName(name);
        pu.persist(newItem);
        return newItem;
    }

}
