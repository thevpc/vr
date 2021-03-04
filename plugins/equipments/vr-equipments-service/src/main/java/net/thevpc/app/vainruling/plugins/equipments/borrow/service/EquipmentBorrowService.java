/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.borrow.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.CorePluginSecurity;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.core.service.ProfileRightBuilder;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.model.AppProfile;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.model.AppUserType;
import net.thevpc.app.vainruling.core.service.model.strict.AppUserStrict;
import net.thevpc.app.vainruling.core.service.util.TextSearchFilter;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowLog;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequest;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequestStatus;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowVisaStatus;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowWorkflow;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentReturnBorrowedLog;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentBorrowOperatorType;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentBorrowRequestFilter;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentForResponsibleInfo;
import net.thevpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
import net.thevpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.thevpc.app.vainruling.plugins.equipments.core.model.EquipmentActionType;
import net.thevpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusLog;
import net.thevpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusType;
import net.thevpc.app.vainruling.plugins.equipments.core.model.EquipmentType;
import net.thevpc.app.vainruling.plugins.equipments.core.service.EquipmentPluginSecurity;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.time.MutableDate;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class EquipmentBorrowService {

    @Autowired
    private CorePlugin core;

    public void startService() {
        AppProfile sop = core.findOrCreateProfile("BorrowSuperOperator");
        AppProfile op = core.findOrCreateProfile("BorrowOperator");
        AppProfile v = core.findOrCreateProfile("BorrowVisa");
        AppProfile br = core.findOrCreateProfile("Borrower");
        AppProfile t = core.findOrCreateProfile("Teacher");
        AppProfile s = core.findOrCreateProfile("Student");
        AppProfile tp = core.findOrCreateProfile("TechnicianPlus");
        ProfileRightBuilder b = new ProfileRightBuilder();
        b.addNames(EquipmentPluginSecurity.RIGHTS_CORE);

        b.addProfileRight(sop.getId(), EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_BORROW_VISA);
        b.addProfileRight(v.getId(), EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_BORROW_VISA);
        b.addProfileRight(op.getId(), EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_BORROW_VISA);

        b.addProfileRight(sop.getId(), EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_RETURN_BORROWED);
        b.addProfileRight(v.getId(), EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_RETURN_BORROWED);
        b.addProfileRight(op.getId(), EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_RETURN_BORROWED);

        b.addProfileRight(sop.getId(), EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_BORROWABLE);
        b.addProfileRight(op.getId(), EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_BORROWABLE);
        b.addProfileRight(br.getId(), EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_BORROWABLE);

        b.addProfileRight(br.getId(), EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_BORROWED);
        b.execute();
        core.addProfileParents("Teacher", "Borrower", "BorrowVisa");
        core.addProfileParents("Student", "Borrower");
        core.addProfileParents("HeadOfDepartment", "BorrowSuperOperator");
        core.addProfileParents("TechnicianPlus", "BorrowOperator");

        ProfileRightBuilder pb = new ProfileRightBuilder();
        pb.add(br.getId(), "EquipmentActionType.Load", "Equipment.Load", "EquipmentBorrowRequest.Load", "EquipmentBorrowRequest.Persist", "EquipmentBorrowRequest.Update");
        pb.add(v.getId(), "EquipmentActionType.Load", "Equipment.Load", "EquipmentBorrowRequest.Load", "EquipmentBorrowRequest.Persist", "EquipmentBorrowRequest.Update");
        pb.add(sop.getId(), "EquipmentActionType.Load", "Equipment.Load", "EquipmentBorrowRequest.Load", "EquipmentBorrowRequest.Persist", "EquipmentBorrowRequest.Update", "Equipment.Update");
        pb.add(op.getId(), "EquipmentActionType.Load", "Equipment.Load", "EquipmentBorrowRequest.Load", "EquipmentBorrowRequest.Persist", "EquipmentBorrowRequest.Update", "Equipment.Update");
        pb.execute();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Map<String, EquipmentBorrowWorkflow> r = new HashMap<>();
        for (EquipmentBorrowWorkflow w : pu.<EquipmentBorrowWorkflow>findAll(EquipmentBorrowWorkflow.class)) {
            r.put(w.getName(), w);
        }
        if (r.containsKey("NoVisa")) {
            EquipmentBorrowWorkflow tt = new EquipmentBorrowWorkflow();
            tt.setName("NoVisa");
            tt.setRequireUser(false);
            tt.setRequireOperator(true);
            tt.setRequireSuperOperator(false);
            tt.setImpl("superThenOpEquipmentBorrowWorkflowExtension");
            pu.persist(tt);
        }
        if (r.containsKey("UserVisa")) {
            EquipmentBorrowWorkflow tt = new EquipmentBorrowWorkflow();
            tt.setName("UserVisa");
            tt.setRequireUser(true);
            tt.setRequireOperator(true);
            tt.setRequireSuperOperator(false);
            tt.setImpl("superThenOpEquipmentBorrowWorkflowExtension");
            pu.persist(tt);
        }
        if (r.containsKey("UserAndSuperVisa")) {
            EquipmentBorrowWorkflow tt = new EquipmentBorrowWorkflow();
            tt.setName("UserAndSuperVisa");
            tt.setRequireUser(true);
            tt.setRequireOperator(true);
            tt.setRequireSuperOperator(true);
            tt.setImpl("superOperatorEquipmentBorrowWorkflowExtension");
            pu.persist(tt);
        }
        if (r.containsKey("SuperVisa")) {
            EquipmentBorrowWorkflow tt = new EquipmentBorrowWorkflow();
            tt.setName("SuperVisa");
            tt.setRequireUser(false);
            tt.setRequireOperator(true);
            tt.setRequireSuperOperator(true);
            tt.setImpl("superOperatorEquipmentBorrowWorkflowExtension");
            pu.persist(tt);
        }
    }

    public EquipmentBorrowLog findBorrowLogByStatusLogId(int statusLogId) {
        _requireBorrowRole();
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            return UPA.getPersistenceUnit().createQuery("Select a from EquipmentBorrowLog a where a.statusLogId=:statusLogId")
                    .setParameter("statusLogId", statusLogId).getSingleResultOrNull();
        });
    }

    public EquipmentReturnBorrowedLog findReturnBorrowedLogByStatusLogId(int statusLogId) {
        _requireBorrowRole();
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            return UPA.getPersistenceUnit().createQuery("Select a from EquipmentReturnBorrowedLog a where a.statusLogId=:statusLogId")
                    .setParameter("statusLogId", statusLogId).getSingleResultOrNull();
        });
    }

    public List<EquipmentReturnBorrowedLog> findReturnBorrowedLogByBorrowLogId(int statusLogId) {
        _requireBorrowRole();
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            return UPA.getPersistenceUnit().createQuery("Select a from EquipmentReturnBorrowedLog a where a.borrowLogId=:borrowLogId")
                    .setParameter("borrowLogId", statusLogId).getResultList();
        });
    }

//    public List<EquipmentBorrowLog> findBorrowedEquipments(Integer actorId, Integer resposibleId) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        StringBuilder sb = new StringBuilder();
//        sb.append("Select a from EquipmentBorrowLog a where a.statusType=:statusType");
//        if (actorId != null && actorId >= 0) {
//            sb.append(" and a.actorId=:actorId");
//        }
//        if (resposibleId != null && resposibleId >= 0) {
//            sb.append(" and a.responsibleId=:responsibleId");
//        }
//        sb.append(" order by a.logEndDate asc, a.logStartDate asc, a.name asc");
//        List<Equipment> eq = pu.createQuery(sb.toString())
//                .setParameter("statusType", EquipmentStatusType.BORROWED)
//                .setParameter("actorId", actorId, actorId != null && actorId >= 0)
//                .setParameter("responsibleId", resposibleId, resposibleId != null && resposibleId >= 0)
//                .getResultList();
//        List<EquipmentStatusLog> ret = new ArrayList<>();
//        EquipmentPlugin eqp = EquipmentPlugin.get();
//        for (Equipment e : eq) {
//            EquipmentStatusLog logged = eqp.findEquipmentLatestLog(e.getId());
//            if (logged != null) {
//                if (logged.getType() != EquipmentStatusType.BORROWED) {
//                    //why ??
//                } else {
//                    ret.add(logged);
//                }
//            }
//        }
//        return ret;
//    }
    public List<Equipment> findBorrowableEquipments(Integer userId, Integer equipmentTypeId, Integer departmentId) {
        _requireBorrowRole();
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            StringBuilder sb = new StringBuilder();
            sb.append("Select a from Equipment a where a.statusType=:statusType");
            sb.append(" and a.borrowable=true");
            sb.append(" and a.actualQuantity>0");
            if (equipmentTypeId != null && equipmentTypeId >= 0) {
                sb.append(" and a.typeId=:typeId");
            }
            if (departmentId != null && departmentId >= 0) {
                sb.append(" and a.departmentId=:departmentId");
            }
            sb.append(" order by a.name asc");
            return pu.createQuery(sb.toString())
                    .setParameter("statusType", EquipmentStatusType.AVAILABLE)
                    .setParameter("typeId", equipmentTypeId, equipmentTypeId != null && equipmentTypeId >= 0)
                    .setParameter("departmentId", departmentId, departmentId != null && departmentId >= 0)
                    .getResultList();
        });
    }

    public List<Equipment> findEquipments() {
        _requireBorrowRole();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a from Equipment a order by a.name")
                .getResultList();

    }

    public List<Equipment> findEquipmentsByType(int typeId) {
        _requireBorrowRole();
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            return pu.createQuery("Select a from Equipment a where a.typeId=:typeId order by a.name")
                    .setParameter("typeId", typeId)
                    .getResultList();
        });
    }

    public boolean isBorrowed(int equipmentId) {
        return findOpenBorrowLogs(equipmentId, null).size() > 0;
    }

    public double findEquipmentBorrowLogRemainingQuantity(int equipmentBorrowLogId) {
        _requireBorrowRole();
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            EquipmentBorrowLog e = pu.findById(EquipmentBorrowLog.class, equipmentBorrowLogId);
            if (e == null) {
                throw new NoSuchElementException();
            }
            Double d = pu.createQuery(
                    "Select sum(a.quantity) from EquipmentReturnBorrowedLog a where a.borrowLogId=:borrowLogId")
                    .setParameter("borrowLogId", equipmentBorrowLogId)
                    .getDouble();
            double q = e.getQuantity();
            if (d == null) {
                return q;
            }
            return q - d;
        });
    }

    public List<EquipmentBorrowLog> findOpenBorrowLogs(Integer equipmentId, Integer borrowerId) {
        _requireBorrowRole();
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            return pu.createQuery(
                    "Select a from EquipmentBorrowLog a where a.archive=false"
                    + (VrUtils.isValidId(borrowerId) ? " and a.borrowerId=:borrowerId" : "")
                    + (VrUtils.isValidId(equipmentId) ? " and a.statusLog.equipmentId=:equipmentId" : "")
                    + " order by a.startDate asc"
            )
                    .setParameter("borrowerId", borrowerId, VrUtils.isValidId(borrowerId))
                    .setParameter("equipmentId", equipmentId, VrUtils.isValidId(equipmentId))
                    .getResultList();
        });
    }

    public List<EquipmentBorrowRequest> findOpenBorrowRequests(Integer equipmentId, Integer borrowerId) {
        _requireBorrowRole();
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            return pu.createQuery(
                    "Select a from EquipmentBorrowRequest a where a.archive=false"
                    + " and a.finalStatus != :delivered"
                    + (VrUtils.isValidId(borrowerId) ? " and a.borrowerUserId=:borrowerId" : "")
                    + (VrUtils.isValidId(equipmentId) ? " and a.equipmentId=:equipmentId" : "")
                    + " order by a.creationDate asc"
            )
                    .setParameter("borrowerId", borrowerId, VrUtils.isValidId(borrowerId))
                    .setParameter("equipmentId", equipmentId, VrUtils.isValidId(equipmentId))
                    .setParameter("delivered", EquipmentBorrowRequestStatus.BORROWED)
                    .getResultList();
        });
    }

    private AppUser _resolveActor(Integer actorId) {
        AppUser actorInstance = null;
        if (actorId == null || actorId <= 0) {
            actorInstance = CorePlugin.get().getCurrentUser();
        } else {
            actorInstance = CorePlugin.get().findUser(actorId);
        }
        if (actorInstance == null) {
            throw new IllegalArgumentException("Actor not found");
        }
        return actorInstance;
    }

    public EquipmentBorrowLog addBorrow(int equipmentId, Integer requestId, int borrowerId, Integer actorId, double qty, Timestamp startDate, Timestamp endDate, String shortDesc, String longDesc) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        EquipmentBorrowRequest request = null;
        if (requestId != null) {
            _requireBorrowRole();
            request = UPA.getPersistenceUnit().invokePrivileged(() -> pu.findById(EquipmentBorrowRequest.class, requestId));
        }
        return _addBorrow(equipmentId, request, borrowerId, actorId, qty, startDate, endDate, shortDesc, longDesc);
    }

    private EquipmentBorrowLog _addBorrow(int equipmentId, EquipmentBorrowRequest request, int borrowerId, Integer actorId, double qty, Timestamp startDate, Timestamp endDate, String shortDesc, String longDesc) {
        _requireBorrowRole();
        if (actorId == null || actorId <= 0) {
            actorId = CorePlugin.get().getCurrentUserIdFF();
        }
        if (request.getBorrow() != null) {
            return request.getBorrow();
        }
        Integer actorId0 = actorId;
        return UPA.getPersistenceUnit().invokePrivileged(() -> {

            if (request.getFinalStatus() == EquipmentBorrowRequestStatus.BORROWED || request.getFinalStatus() == null) {
                if (request.getBorrow() == null) {
                    request.setFinalStatus(EquipmentBorrowRequestStatus.ACCEPTED);
                    request.setFinalStatusDate(new Date());
                } else {
                    return request.getBorrow();
                }
            }
            if (request.getFinalStatus() != EquipmentBorrowRequestStatus.ACCEPTED) {
                return null;
            }
            PersistenceUnit pu = UPA.getPersistenceUnit();
            AppUser borrower = pu.findById(AppUser.class, borrowerId);
            if (borrower == null) {
                throw new IllegalArgumentException("Missing borrower");
            }
            Equipment e = pu.findById(Equipment.class, equipmentId);
            if (e == null || !e.isBorrowable()) {
                throw new IllegalArgumentException("Not Borrowable");
            }
            if (qty <= 0) {
                throw new IllegalArgumentException("Invalid quantity");
            }
            if (request.getBorrow() == null) {
                EquipmentBorrowLog borr = new EquipmentBorrowLog();
                Timestamp startDate0 = startDate;
                Timestamp endDate0 = endDate;
                if (startDate0 == null) {
                    startDate0 = new Timestamp(System.currentTimeMillis());
                }
                if (endDate0 == null) {
                    endDate0 = startDate0;
                }
                borr.setStartDate(startDate0);
                borr.setEndDate(endDate0);
                borr.setQuantity(qty);
                borr.setBorrower(borrower);

                EquipmentStatusLog statusLog = new EquipmentStatusLog();
                statusLog.setEquipment(e);
                statusLog.setAction(getBorrowAction());
                statusLog.setActor(_resolveActor(actorId0));
                statusLog.setName(shortDesc);
                statusLog.setDescription(longDesc);
                statusLog.setStartDate(borr.getStartDate());
                statusLog.setEndDate(borr.getEndDate());
                statusLog.setOutQty(borr.getQuantity());
                statusLog.setResponsible(borr.getBorrower());
                statusLog.setType(EquipmentStatusType.AVAILABLE);

                pu.persist(statusLog);

                borr.setStatusLog(statusLog);
                pu.persist(borr);
                request.setBorrow(borr);
                request.setFinalStatus(EquipmentBorrowRequestStatus.BORROWED);
                request.setFinalStatusDate(new Date());
                pu.merge(request);
                pu.updateFormulas(Equipment.class, e.getId());
                return borr;
            } else {
                if (request.getFinalStatus() != EquipmentBorrowRequestStatus.BORROWED) {
                    request.setFinalStatus(EquipmentBorrowRequestStatus.BORROWED);
                    request.setFinalStatusDate(new Date());
                    pu.merge(request);
                    pu.updateFormulas(Equipment.class, e.getId());
                }
            }
            return request.getBorrow();
        });
    }

    public boolean returnBorrowed(int equipmentId, Integer borrowerId, Integer actorId, double qty, Timestamp returnDate, String shortDesc, String longDesc) {
        if (qty <= 0 || Double.isNaN(qty) || Double.isInfinite(qty)) {
            return true;
        }
        double qty0 = qty;
        if (returnDate == null) {
            returnDate = new Timestamp(System.currentTimeMillis());
        }
        Timestamp returnDate0 = returnDate;
        Integer actorId0 = actorId;
        if (actorId == null || actorId <= 0) {
            actorId = CorePlugin.get().getCurrentUserIdFF();
        }

        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            double qty2 = qty0;
            PersistenceUnit pu = UPA.getPersistenceUnit();
            for (EquipmentBorrowLog borrowLog : findOpenBorrowLogs(equipmentId, borrowerId)) {
                if (qty2 <= 0) {
                    return true;
                }
                double q = findEquipmentBorrowLogRemainingQuantity(borrowLog.getId());
                if (q <= 0) {
                    if (!borrowLog.isArchive()) {
                        borrowLog.setArchive(true);
                        pu.merge(borrowLog);
                    }
                    EquipmentBorrowRequest r = findBorrowRequestByBorrowLog(borrowLog.getId());
                    if (r != null && !r.isArchive()) {
                        r.setArchive(true);
                    }
                } else {
                    if (qty2 >= q) {
                        qty2 -= q;
                    }
                    EquipmentReturnBorrowedLog rblog = new EquipmentReturnBorrowedLog();
                    rblog.setBorrowLog(borrowLog);
                    rblog.setQuantity(q);
                    rblog.setReturnDate(returnDate0);

                    EquipmentStatusLog slog = new EquipmentStatusLog();
                    slog.setActor(_resolveActor(actorId0));
                    slog.setAction(getReturnBorrowedAction());
                    slog.setEquipment(borrowLog.getStatusLog().getEquipment());
                    slog.setDescription(longDesc);
                    slog.setName(shortDesc);
                    slog.setInQty(q);
                    slog.setResponsible(borrowLog.getBorrower());
                    slog.setStartDate(returnDate0);
                    slog.setEndDate(returnDate0);
                    slog.setType(EquipmentStatusType.AVAILABLE);
                    slog.setAcquisition(null);
                    pu.persist(slog);

                    rblog.setStatusLog(slog);
                    pu.persist(rblog);
                    double q2 = findEquipmentBorrowLogRemainingQuantity(borrowLog.getId());
                    if (q2 <= 0) {
                        if (!borrowLog.isArchive()) {
                            borrowLog.setArchive(true);
                            pu.merge(borrowLog);
                        }
                        EquipmentBorrowRequest r = findBorrowRequestByBorrowLog(borrowLog.getId());
                        if (r != null && (!r.isArchive() || r.getFinalStatus() != EquipmentBorrowRequestStatus.RETURNED)) {
                            r.setArchive(true);
                            r.setFinalStatus(EquipmentBorrowRequestStatus.RETURNED);
                            r.setFinalStatusDate(new Date());
                            pu.merge(r);
                        }
                    }
                }
                pu.updateFormulas(EquipmentBorrowLog.class, borrowLog.getId());
            }
            pu.updateFormulas(Equipment.class, equipmentId);
            return qty2 != 0;
        });
    }

    public EquipmentActionType getReturnBorrowedAction() {
        return EquipmentPlugin.get().findOrCreateEquipmentActionType("ReturnBorrowed", "Return Borrowed");
    }

    public EquipmentActionType getBorrowAction() {
        return EquipmentPlugin.get().findOrCreateEquipmentActionType("Borrow", "Borrow");
    }

//    public EquipmentBorrowRequestLog findEquipmentBorrowRequestLog(int logId, int requestId) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select a from EquipmentBorrowRequestLog a where a.requestId=requestId and a.statusLogId=:statusLogId")
//                .setParameter("statusLogId", logId)
//                .setParameter("requestId", requestId)
//                .getFirstResultOrNull();
//    }
//
//    public void addEquipmentBorrowRequestLog(int logId, int requestId) {
//        if (logId >= 0 && requestId >= 0) {
//            PersistenceUnit pu = UPA.getPersistenceUnit();
//            EquipmentBorrowRequestLog found = findEquipmentBorrowRequestLog(logId, requestId);
//            if (found == null) {
//                EquipmentBorrowRequest r = pu.findById(EquipmentBorrowRequest.class, requestId);
//                EquipmentBorrowRequest l = pu.findById(EquipmentStatusLog.class, logId);
//                if (r != null && l != null) {
//
//                }
//            }
//        }
//    }
//
//    public void removeEquipmentBorrowRequestLog(int logId, int requestId) {
//        EquipmentBorrowRequestLog found = findEquipmentBorrowRequestLog(logId, requestId);
//        if (found != null) {
//            UPA.getPersistenceUnit().remove(EquipmentBorrowRequestLog.class, found);
//        }
//    }
    public EquipmentBorrowRequest findBorrowRequestByBorrowLog(int borrowLogId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a from EquipmentBorrowRequest a where a.borrowId=:borrowLogId")
                .setParameter("borrowLogId", borrowLogId)
                //                .setParameter("requestId", requestId)
                .getFirstResultOrNull();
    }

    public List<EquipmentStatusLog> findEquipmentStatusLogByBorrowRequest(int requestId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a.statusLog from EquipmentBorrowRequestLog a where a.requestId=requestId")
                .setParameter("requestId", requestId)
                //                .setParameter("requestId", requestId)
                .getResultList();
    }

    private void _requireBorrowRole() {
        CorePluginSecurity.requireAnyOfProfiles("Borrower", "BorrowVisa", "BorrowOperator", "BorrowSuperOperator");
    }

    public List<AppUserStrict> findBorrowVisaUsers() {
        _requireBorrowRole();
        return UPA.getPersistenceUnit().invokePrivileged(() -> CorePlugin.get().findUsersByProfile("BorrowVisa").stream().map(
                x->new AppUserStrict(x,CorePlugin.get().getUserIcon(x.getId()))
        ).collect(Collectors.toList()));
    }

    public List<AppUserStrict> findBorrowUsers() {
        _requireBorrowRole();
        return UPA.getPersistenceUnit().invokePrivileged(() -> CorePlugin.get().findUsersByProfile("Borrower").stream().map(
                x->new AppUserStrict(x,CorePlugin.get().getUserIcon(x.getId()))
        ).collect(Collectors.toList()));
    }

    public boolean isBorrowVisaUser(Integer userId) {
        _requireBorrowRole();
        Set<String> profiles = UPA.getPersistenceUnit().invokePrivileged(() -> CorePlugin.get().findProfilesByUser(userId).stream().map(x -> x.getCode()).collect(Collectors.toSet()));
        return profiles.contains("BorrowVisa");
    }

    public boolean isBorrowOperatorUser(Integer userId) {
        _requireBorrowRole();
        Set<String> profiles = UPA.getPersistenceUnit().invokePrivileged(() -> CorePlugin.get().findProfilesByUser(userId).stream().map(x -> x.getCode()).collect(Collectors.toSet()));
        return profiles.contains("BorrowOperator");
    }

    public boolean isBorrowSuperOperatorUser(Integer userId) {
        _requireBorrowRole();
        Set<String> profiles = UPA.getPersistenceUnit().invokePrivileged(() -> CorePlugin.get().findProfilesByUser(userId).stream().map(x -> x.getCode()).collect(Collectors.toSet()));
        return profiles.contains("BorrowSuperOperator");
    }

    public List<AppUser> findBorrowerUsers(Integer equipmentDepartmentId, boolean includeBorrowUser, boolean includeVisaUser) {
        _requireBorrowRole();
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            final CorePlugin core = CorePlugin.get();
            List<Integer> allUsers = new ArrayList<>();

            List<Integer> userIds = null;
            if (includeBorrowUser) {
                userIds = UPA.getPersistenceUnit().createQuery("Select distinct(a.borrowerUserId) from EquipmentBorrowRequest a where a.archive=false"
                        + ((equipmentDepartmentId != null && equipmentDepartmentId >= 0) ? " and a.equipment.departmentId=:departmentId" : "")
                )
                        .setParameter("departmentId", equipmentDepartmentId, equipmentDepartmentId != null && equipmentDepartmentId >= 0)
                        .<Integer>getResultList();
                allUsers.addAll(userIds);
            }

            if (includeVisaUser) {
                userIds = UPA.getPersistenceUnit().createQuery("Select distinct(a.visaUserId) from EquipmentBorrowRequest a where a.archive=false"
                        + ((equipmentDepartmentId != null && equipmentDepartmentId >= 0) ? " and a.equipment.departmentId=:departmentId" : "")
                )
                        .setParameter("departmentId", equipmentDepartmentId, equipmentDepartmentId != null && equipmentDepartmentId >= 0)
                        .<Integer>getResultList();
                allUsers.addAll(userIds);

                userIds = UPA.getPersistenceUnit().createQuery("Select distinct(a.operatorUserId) from EquipmentBorrowRequest a where a.archive=false"
                        + ((equipmentDepartmentId != null && equipmentDepartmentId >= 0) ? " and a.equipment.departmentId=:departmentId" : "")
                )
                        .setParameter("departmentId", equipmentDepartmentId, equipmentDepartmentId != null && equipmentDepartmentId >= 0)
                        .<Integer>getResultList();
                allUsers.addAll(userIds);

                userIds = UPA.getPersistenceUnit().createQuery("Select distinct(a.superOperatorUserId) from EquipmentBorrowRequest a where a.archive=false"
                        + ((equipmentDepartmentId != null && equipmentDepartmentId >= 0) ? " and a.equipment.departmentId=:departmentId" : "")
                )
                        .setParameter("departmentId", equipmentDepartmentId, equipmentDepartmentId != null && equipmentDepartmentId >= 0)
                        .<Integer>getResultList();
                allUsers.addAll(userIds);

                List<Integer> deptIds = UPA.getPersistenceUnit().createQuery("Select distinct(a.statusLog.equipment.departmentId) from EquipmentBorrowRequest a where a.archive=false"
                        + ((equipmentDepartmentId != null && equipmentDepartmentId >= 0) ? " and a.equipment.departmentId=:departmentId" : "")
                )
                        .setParameter("departmentId", equipmentDepartmentId, equipmentDepartmentId != null && equipmentDepartmentId >= 0)
                        .<Integer>getResultList();
                deptIds.remove(null);
                for (Integer deptId : deptIds) {
                    AppUser h = core.findHeadOfDepartment(deptId);
                    if (h != null) {
                        allUsers.add(h.getId());
                    }
                    AppUserType tech = core.findUserType("Technician");
                    if (tech != null) {
                        for (AppUser appUser : core.findUsersByProfileFilter(null, tech.getId(), deptId)) {
                            allUsers.add(appUser.getId());
                        }
                    }
                }
            }

            allUsers.remove(null);
            return allUsers.stream().map(x -> core.findUser(x)).filter(x -> x != null).collect(Collectors.toList());
        });
    }

    public List<EquipmentForResponsibleInfo> findBorrowedEquipmentsInfo(Integer borrowerId) {
        return findOpenBorrowLogs(null, borrowerId).stream()
                .map(x -> new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.OPERATOR, x))
                .collect(Collectors.toList());
    }

    public List<EquipmentForResponsibleInfo> findBorrowEquipmentsVisasForResponsibleInfo(Integer userId) {
        if (userId == null || userId < 0) {
            return Collections.emptyList();
        }
        AppUser uu = core.findUser(userId);
        if (uu == null) {
            return Collections.emptyList();
        }
        Map<Integer, EquipmentForResponsibleInfo> map = new HashMap<>();
        Set<String> profiles = CorePlugin.get().findProfilesByUser(userId).stream().map(x -> x.getCode()).collect(Collectors.toSet());
        if (profiles.contains("BorrowVisa")) {
            for (EquipmentBorrowRequest e : findRequestsByBorrower(new EquipmentBorrowRequestFilter()
                    .setVisaUserId(userId)
                    //                    .setVisaStatus(EquipmentBorrowRequestStatus.PENDING)
                    .setFinalStatus(EquipmentBorrowRequestStatus.PENDING, EquipmentBorrowRequestStatus.ACCEPTED)
                    .setArchive(false)
                    .setCancelled(false)
            )) {
                EquipmentForResponsibleInfo old = map.get(e.getId());
                if (old == null) {
                    map.put(e.getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.USER, e));
                }
            }
        }
        if (profiles.contains("BorrowOperator")) {
            for (EquipmentBorrowRequest e : findRequestsByBorrower(new EquipmentBorrowRequestFilter()
                    //                    .setOperatorStatus(EquipmentBorrowRequestStatus.PENDING)
                    .setFinalStatus(EquipmentBorrowRequestStatus.PENDING, EquipmentBorrowRequestStatus.ACCEPTED)
                    .setDepartmentId(uu.getDepartment().getId())
                    .setArchive(false)
                    .setCancelled(false)
            )) {
                EquipmentForResponsibleInfo old = map.get(e.getId());
                if (old == null) {
                    map.put(e.getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.OPERATOR, e));
                }
            }
        }
        if (profiles.contains("BorrowSuperOperator")) {
            for (EquipmentBorrowRequest e : findRequestsByBorrower(new EquipmentBorrowRequestFilter()
                    //                    .setSuperOperatorStatus(EquipmentBorrowRequestStatus.PENDING)
                    .setFinalStatus(EquipmentBorrowRequestStatus.PENDING, EquipmentBorrowRequestStatus.ACCEPTED)
                    .setDepartmentId(uu.getDepartment().getId())
                    .setArchive(false)
                    .setCancelled(false)
            )) {
                EquipmentForResponsibleInfo old = map.get(e.getId());
                if (old == null) {
                    map.put(e.getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.SUPER_OPERATOR, e));
                }
            }
        }
        ArrayList<EquipmentForResponsibleInfo> list = new ArrayList<>(map.values());
        list.sort(null);
        return list;
    }

    public List<EquipmentForResponsibleInfo> findBorrowableEquipmentsForResponsibleInfo(Integer userId, Integer equipmentTypeId, Integer departmentId, String searchText) {
        if (userId == null || userId < 0) {
            return Collections.emptyList();
        }
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            Map<Integer, EquipmentForResponsibleInfo> map = new HashMap<>();
            for (Equipment e : findBorrowableEquipments(userId, equipmentTypeId, departmentId)) {
                map.put(e.getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.BORROWER, e));
            }
//            for (EquipmentBorrowLog item : findOpenBorrowLogs(null, userId)) {
//                map.remove(item.getStatusLog().getEquipment().getId());
//            }
//            for (EquipmentBorrowRequest item : findOpenBorrowRequests(null, userId)) {
//                map.remove(item.getEquipment().getId());
//            }
            List<EquipmentForResponsibleInfo> list = new ArrayList<>(map.values());
            if (!StringUtils.isBlank(searchText)) {
                list = (List) TextSearchFilter.forJson(searchText).filterList(list);

            }
            list.sort(null);
            return list;
        });
    }

    public List<EquipmentForResponsibleInfo> findBorrowedEquipmentsForResponsibleInfo(Integer userId, Integer equipmentTypeId, Integer departmentId) {
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            Map<Integer, EquipmentForResponsibleInfo> logMap = new HashMap<>();
            Map<Integer, EquipmentForResponsibleInfo> reqMap = new HashMap<>();
            for (EquipmentBorrowLog e : findOpenBorrowLogs(null, userId)) {
                logMap.put(e.getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.BORROWER, e));
            }
            for (EquipmentBorrowRequest e : findRequestsByBorrower(
                    new EquipmentBorrowRequestFilter().setBorrowerUserId(userId)
                            .setEquipmentTypeId(equipmentTypeId).setDepartmentId(departmentId)
                            .setArchive(false))) {
                EquipmentBorrowLog b = e.getBorrow();
                if (b != null && logMap.containsKey(b.getId())) {
                    logMap.remove(b.getId());
                }
                reqMap.put(e.getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.BORROWER, e));
            }
            ArrayList<EquipmentForResponsibleInfo> list = new ArrayList<>(logMap.values());
            list.addAll(reqMap.values());
            list.sort(null);
            return list;
        });
    }

    public List<EquipmentBorrowRequest> findRequestsByBorrower(EquipmentBorrowRequestFilter filter) {
        return UPA.getPersistenceUnit().invokePrivileged(() -> {

            StringBuilder sb = new StringBuilder();
            Map<String, Object> extra = new HashMap<>();
            int pIndex = 0;

            sb.append("Select a from EquipmentBorrowRequest a where 1=1");

            if (VrUtils.isValidId(filter.getBorrowerUserId())) {
                sb.append(" and a.borrowerUserId=:borrowerUserId");
            }
            if (VrUtils.isValidId(filter.getVisaUserId())) {
                sb.append(" and a.visaUserId=:visaUserId");
            }
            if (VrUtils.isValidId(filter.getOperatorUserId())) {
                sb.append(" and a.operatorUserId=:operatorUserId");
            }
            if (VrUtils.isValidId(filter.getSuperOperatorUserId())) {
                sb.append(" and a.superOperatorUserId=:superOperatorUserId");
            }
            if (filter.getVisaStatus() != null && filter.getVisaStatus().length > 0) {
                sb.append(" and a.visaUserStatus in (");
                boolean first = true;
                for (EquipmentBorrowRequestStatus s : filter.getVisaStatus()) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(",");
                    }
                    pIndex++;
                    String v = "visaUserStatus" + pIndex;
                    sb.append(":").append(v);
                    extra.put(v, s);
                }
                sb.append(" )");
            }
            if (filter.getOperatorStatus() != null && filter.getOperatorStatus().length > 0) {
                sb.append(" and a.operatorUserStatus in (");
                boolean first = true;
                for (EquipmentBorrowRequestStatus s : filter.getOperatorStatus()) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(",");
                    }
                    pIndex++;
                    String v = "operatorUserStatus" + pIndex;
                    sb.append(":").append(v);
                    extra.put(v, s);
                }
                sb.append(" )");
            }
            if (filter.getSuperOperatorStatus() != null && filter.getSuperOperatorStatus().length > 0) {
                sb.append(" and a.superOperatorUserStatus in (");
                boolean first = true;
                for (EquipmentBorrowRequestStatus s : filter.getSuperOperatorStatus()) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(",");
                    }
                    pIndex++;
                    String v = "superOperatorUserStatus" + pIndex;
                    sb.append(":").append(v);
                    extra.put(v, s);
                }
                sb.append(" )");
            }
            if (filter.getFinalStatus() != null && filter.getFinalStatus().length > 0) {
                sb.append(" and a.finalStatus in (");
                boolean first = true;
                for (EquipmentBorrowRequestStatus s : filter.getFinalStatus()) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(",");
                    }
                    pIndex++;
                    String v = "finalStatus" + pIndex;
                    sb.append(":").append(v);
                    extra.put(v, s);
                }
                sb.append(" )");
            }
            if (filter.getArchive() != null) {
                sb.append(" and a.archive=:archive");
            }
            if (filter.getCancelled() != null) {
                sb.append(" and a.cancelled=:cancelled");
            }
            if (VrUtils.isValidId(filter.getEquipmentTypeId())) {
                sb.append(" and a.equipment.typeId=:typeId");
            }
            if (VrUtils.isValidId(filter.getDepartmentId())) {
                sb.append(" and a.equipment.departmentId=:departmentId");
            }
            return UPA.getPersistenceUnit().createQuery(sb.toString())
                    .setParameter("borrowerUserId", filter.getBorrowerUserId(), VrUtils.isValidId(filter.getBorrowerUserId()))
                    .setParameter("visaUserId", filter.getVisaUserId(), VrUtils.isValidId(filter.getVisaUserId()))
                    .setParameter("operatorUserId", filter.getOperatorUserId(), VrUtils.isValidId(filter.getOperatorUserId()))
                    .setParameter("superOperatorUserId", filter.getSuperOperatorUserId(), VrUtils.isValidId(filter.getSuperOperatorUserId()))
                    .setParameter("archive", filter.getArchive(), filter.getArchive() != null)
                    .setParameter("cancelled", filter.getCancelled(), filter.getCancelled() != null)
                    .setParameter("typeId", filter.getEquipmentTypeId(), VrUtils.isValidId(filter.getEquipmentTypeId()))
                    .setParameter("departmentId", filter.getDepartmentId(), VrUtils.isValidId(filter.getDepartmentId()))
                    .setParameters(extra)
                    .getResultList();
        });
    }

    public boolean isEquipmentRequestAcceptVisaUser(int equipmentId, int userId) {
        return true;
    }

    public boolean isEquipmentRequestAcceptSuperOperatorUser(int equipmentId, int userId) {
        return true;
    }

//    private boolean _updateRequestFinalStatus(EquipmentBorrowRequest req, AppUser u, Date dte, boolean apply) {
//        boolean requireSuper = false;
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        if (requireSuper) {
//            if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED) {
//                req.setFinalStatus(EquipmentBorrowRequestStatus.ACCEPTED);
//                req.setFinalStatusDate(dte);
//                pu.merge(req);
//                if (apply && req.getBorrow() == null) {
//                    _addBorrow(req.getEquipment().getId(), req, req.getBorrowerUser().getId(), u.getId(), req.getQuantity(), new Timestamp(req.getFromDate().getTime()), new Timestamp(req.getToDate().getTime()), null, req.getDescription());
//                }
//                return true;
//            } else if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.REJECTED) {
//                req.setFinalStatus(EquipmentBorrowRequestStatus.REJECTED);
//                req.setFinalStatusDate(dte);
//                pu.merge(req);
//                return true;
//            }
//        } else {
//            if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED
//                    || (req.getSuperOperatorUserStatus() != EquipmentBorrowVisaStatus.REJECTED
//                    && req.getOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED)) {
//                req.setFinalStatus(EquipmentBorrowRequestStatus.ACCEPTED);
//                req.setFinalStatusDate(dte);
//                pu.merge(req);
//                if (apply && req.getBorrow() == null) {
//                    _addBorrow(req.getEquipment().getId(), req, req.getBorrowerUser().getId(), u.getId(), req.getQuantity(), new Timestamp(req.getFromDate().getTime()), new Timestamp(req.getToDate().getTime()), null, req.getDescription());
//                }
//                return true;
//            } else if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.REJECTED
//                    || (req.getSuperOperatorUserStatus() != EquipmentBorrowVisaStatus.ACCEPTED
//                    && req.getOperatorUserStatus() == EquipmentBorrowVisaStatus.REJECTED)) {
//                req.setFinalStatus(EquipmentBorrowRequestStatus.REJECTED);
//                req.setFinalStatusDate(dte);
//                pu.merge(req);
//                return true;
//            }
//        }
//        return false;
//    }
    public EquipmentBorrowWorkflow resolveBorrowWorkflow(Equipment eq) {
        EquipmentBorrowWorkflowExtension s = null;
        EquipmentBorrowWorkflow w = eq.getBorrowWorkflow();
        if (w != null) {
            return w;
        }
        EquipmentType t = eq.getType();
        if (t != null && t.getBorrowWorkflow() != null) {
            w = eq.getBorrowWorkflow();
            if (w != null) {
                return w;
            }
        }
        EquipmentBorrowWorkflow e = new EquipmentBorrowWorkflow();
        e.setId(-1);
        e.setName("Default");
        e.setRequireUser(false);
        e.setRequireOperator(true);
        e.setRequireSuperOperator(false);
        return e;
    }

    public EquipmentBorrowWorkflowExtension resolveBorrowWorkflowExtension(Equipment eq) {
        EquipmentBorrowWorkflowExtension s = null;
        EquipmentBorrowWorkflow w = eq.getBorrowWorkflow();
        if (w != null && !StringUtils.isBlank(w.getImpl())) {
            try {
                s = (EquipmentBorrowWorkflowExtension) VrApp.getBean(w.getImpl().trim());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (s != null) {
                return s;
            }
        }
        EquipmentType t = eq.getType();
        if (t != null && t.getBorrowWorkflow() != null) {
            w = eq.getBorrowWorkflow();
            if (w != null && !StringUtils.isBlank(w.getImpl())) {
                try {
                    s = (EquipmentBorrowWorkflowExtension) VrApp.getBean(w.getImpl().trim());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (s != null) {
                    return s;
                }
            }
        }
        return VrApp.getBean(SuperThenOpEquipmentBorrowWorkflowExtension.class);
    }

    private boolean _updateRequestToBorrow(EquipmentBorrowRequest req, AppUser u) {
        if (req.getFromDate() == null || req.getToDate() == null) {
            return false;
        }
        Equipment eq = req.getEquipment();
        EquipmentBorrowWorkflowExtension ext = resolveBorrowWorkflowExtension(eq);
        EquipmentBorrowVisaStatus s = ext.computeStatus(req);
        if (s == EquipmentBorrowVisaStatus.ACCEPTED) {
            _addBorrow(req.getEquipment().getId(), req, req.getBorrowerUser().getId(), u.getId(), req.getQuantity(), new Timestamp(req.getFromDate().getTime()), new Timestamp(req.getToDate().getTime()), null, req.getDescription());
            return true;
        } else if (s == EquipmentBorrowVisaStatus.REJECTED) {
            EquipmentBorrowLog b = req.getBorrow();
            if (b != null) {
                List<EquipmentReturnBorrowedLog> allReturns = findReturnBorrowedLogByBorrowLogId(b.getId());
                if (!allReturns.isEmpty()) {
                    throw new IllegalArgumentException("Cannot Reject already accepted Request");
                }
                _removeBorrow(b);
                return true;
            }
        }
        return false;
    }

    private void _removeBorrow(EquipmentBorrowLog b) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        EquipmentStatusLog l = b.getStatusLog();
        Equipment e = null;
        if (l != null) {
            e = l.getEquipment();
            pu.remove(l);
        }
        pu.remove(b);
        if (e != null) {
            pu.updateFormulas(Equipment.class, e.getId());
        }
    }

    private boolean _updateRequest(EquipmentBorrowRequest req, EquipmentBorrowOperatorType operatorType, AppUser u, Date dte, EquipmentBorrowVisaStatus acceptedStatus) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (req.getFinalStatus() == EquipmentBorrowRequestStatus.BORROWED || req.getFinalStatus() == EquipmentBorrowRequestStatus.RETURNED) {
            return false;
        }
        boolean updated = false;
        switch (operatorType) {
            case BORROWER: {
                break;
            }
            case USER: {
                if (acceptedStatus != req.getVisaUserStatus()) {
                    req.setVisaUser(u);
                    req.setVisaUserStatus(acceptedStatus);
                    req.setVisaUserStatusDate(dte);
                    updated = true;
                }
                break;
            }
            case OPERATOR: {
                if (acceptedStatus != req.getOperatorUserStatus()) {
                    req.setOperatorUser(u);
                    req.setOperatorUserStatus(acceptedStatus);
                    req.setOperatorUserStatusDate(dte);
                    updated = true;
                }
                break;
            }
            case SUPER_OPERATOR: {
                if (acceptedStatus != req.getSuperOperatorUserStatus()) {
                    req.setSuperOperatorUser(u);
                    req.setSuperOperatorUserStatus(acceptedStatus);
                    req.setSuperOperatorUserStatusDate(dte);
                    updated = true;
                }
                break;
            }
        }
        boolean requireSuper = false;
        if (requireSuper) {
            if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED) {
                req.setFinalStatus(EquipmentBorrowRequestStatus.ACCEPTED);
                req.setFinalStatusDate(dte);
                updated = true;
            } else if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.REJECTED) {
                req.setFinalStatus(EquipmentBorrowRequestStatus.REJECTED);
                req.setFinalStatusDate(dte);
                updated = true;
            }
        } else {
            if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED
                    || (req.getSuperOperatorUserStatus() != EquipmentBorrowVisaStatus.REJECTED
                    && req.getOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED)) {
                req.setFinalStatus(EquipmentBorrowRequestStatus.ACCEPTED);
                req.setFinalStatusDate(dte);
                updated = true;
            } else if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.REJECTED
                    || (req.getSuperOperatorUserStatus() != EquipmentBorrowVisaStatus.ACCEPTED
                    && req.getOperatorUserStatus() == EquipmentBorrowVisaStatus.REJECTED)) {
                req.setFinalStatus(EquipmentBorrowRequestStatus.REJECTED);
                req.setFinalStatusDate(dte);
                updated = true;
            }
        }
        if (updated) {
            UPA.getPersistenceUnit().invokePrivileged(() -> {
                pu.merge(req);
                pu.updateFormulas(Equipment.class, req.getEquipment().getId());
            });
        }
        return updated;
    }

    public void archiveRequest(int requestId, Integer userId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (!VrUtils.isValidId(userId)) {
            userId = CorePlugin.get().getCurrentUserId();
        }
        if (userId == null) {
            return;
        }
        EquipmentBorrowRequest req = pu.findById(EquipmentBorrowRequest.class, requestId);
        if (!req.isArchive()) {
            req.setArchive(true);
            pu.merge(req);
        }
    }

    public void cancelRequest(int requestId, Integer userId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (!VrUtils.isValidId(userId)) {
            userId = CorePlugin.get().getCurrentUserId();
        }
        if (userId == null) {
            return;
        }
        EquipmentBorrowRequest req = pu.findById(EquipmentBorrowRequest.class, requestId);
        if (!req.isCancelled()) {
            req.setCancelled(true);
            pu.merge(req);
        }
    }

    public void applyVisa(int requestId, EquipmentBorrowOperatorType operatorType, Integer userId, boolean accept, boolean deliverOrDeliverBack) {
        Date dte = new Date();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (!VrUtils.isValidId(userId)) {
            userId = CorePlugin.get().getCurrentUserId();
        }
        if (userId == null) {
            return;
        }
        EquipmentBorrowVisaStatus acceptedStatus = accept ? EquipmentBorrowVisaStatus.ACCEPTED : EquipmentBorrowVisaStatus.REJECTED;
        AppUser u = pu.findById(AppUser.class, userId);
        EquipmentBorrowRequest req = pu.findById(EquipmentBorrowRequest.class, requestId);
        if (req != null) {
            _updateRequest(req, operatorType, u, dte, acceptedStatus);
            if (deliverOrDeliverBack) {
                _updateRequestToBorrow(req, u);
            }
        }
    }

    public void deliverOrDeliverBackEquipment(int requestId, Integer userId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (!VrUtils.isValidId(userId)) {
            userId = CorePlugin.get().getCurrentUserId();
        }
        if (userId == null) {
            return;
        }
        AppUser u = pu.findById(AppUser.class, userId);
        EquipmentBorrowRequest req = pu.findById(EquipmentBorrowRequest.class, requestId);
        if (req != null) {
            _updateRequestToBorrow(req, u);
        }
    }

    public void archiveEquipmentRequest(int requestId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        EquipmentBorrowRequest a = pu.findById(EquipmentBorrowRequest.class, requestId);
        if (a != null) {
            a.setArchive(true);
            pu.merge(a);
        }
    }

    public List<EquipmentBorrowRequest> findEquipmentRequestsByVisaUser(int userId, boolean requestHistory) {
        return UPA.getPersistenceUnit().createQueryBuilder(EquipmentBorrowRequest.class)
                .setEntityAlias("u")
                .byField("visaUserId", userId)
                .byField("visaArchive", false, !requestHistory)
                .getResultList();
    }

    public List<EquipmentBorrowRequest> findEquipmentRequestsByOperatorUser(int userId, boolean requestHistory) {
        return UPA.getPersistenceUnit().createQueryBuilder(EquipmentBorrowRequest.class)
                .setEntityAlias("u")
                .byField("operatorUserId", userId)
                .byField("operatorArchive", false, !requestHistory)
                .getResultList();
    }

    public List<EquipmentBorrowRequest> findEquipmentRequestsBySuperOperatorUser(int userId, boolean requestHistory) {
        return UPA.getPersistenceUnit().createQueryBuilder(EquipmentBorrowRequest.class)
                .setEntityAlias("u")
                .byField("superOperatorUserId", userId)
                .byField("superOperatorArchive", false, !requestHistory)
                .getResultList();
    }

    public EquipmentBorrowRequest addEquipmentBorrowRequest(Integer userId, int eqId, Date from, Date to, Integer visaUserId, double qty, boolean acceptOldDates) {
        AppUser bu = userId == null ? null : UPA.getPersistenceUnit().invokePrivileged(() -> core.findUser(userId));
        AppUser vu = visaUserId == null ? null : UPA.getPersistenceUnit().invokePrivileged(() -> core.findUser(visaUserId));
        Equipment eq = EquipmentPlugin.get().findEquipment(eqId);
        if (bu == null) {
            bu = core.getCurrentUser();
        }

        EquipmentBorrowRequest req = new EquipmentBorrowRequest();
        req.setBorrowerUser(bu);
        req.setEquipment(eq);
        req.setFromDate(from);
        req.setToDate(to);
        req.setVisaUser(vu);
        req.setQuantity(qty);
        addEquipmentBorrowRequest(req, acceptOldDates);
        return req;
    }

    public void addEquipmentBorrowRequest(EquipmentBorrowRequest req, boolean acceptOldDates) {
        AppUser u = req.getBorrowerUser();
        if (u == null) {
            u = core.getCurrentUser();
            req.setBorrowerUser(u);
        }
        Equipment eq = req.getEquipment();
        double qte = req.getQuantity();
        Date fromDte = req.getFromDate();
        Date toDte = req.getFromDate();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (u == null) {
            throw new IllegalArgumentException("Missing Borrower User");
        }
        if (eq == null) {
            throw new IllegalArgumentException("Missing Equipment");
        }
        if (qte <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        if (fromDte == null || toDte == null || fromDte.compareTo(toDte) > 0) {
            throw new IllegalArgumentException("Invalid date range");
        }
        if (!acceptOldDates) {
            if (fromDte.compareTo(new MutableDate().clearTime().getDateTime()) < 0) {
                throw new IllegalArgumentException("Invalid date range");
            }
        }
        if (eq.getDepartment() == null) {
            throw new IllegalArgumentException("Invalid equipment, missing department");
        }
        EquipmentBorrowWorkflow p = resolveBorrowWorkflow(eq);
        if (p.isRequireUser() && req.getVisaUser() == null) {
            throw new IllegalArgumentException("Missing Visa User");
        }
        int exitingCount = findOpenBorrowRequests(eq.getId(), u.getId()).size();
//        pu.createQuery("Select a from EquipmentBorrowRequest a where a.equipmentId=:eid "
//                + " and a.archive=false "
//                + " and a.borrowerUserId=:uid"
//                + " and a.finalStatus!=:accepted  "
//                + " and a.finalStatus!=:rejected"
//        ).setParameter("uid", u.getId())
//                .setParameter("eid", eq.getId())
//                .setParameter("accepted", EquipmentBorrowRequestStatus.ACCEPTED)
//                .setParameter("rejected", EquipmentBorrowRequestStatus.REJECTED)
//                .getResultList().size();
        if (exitingCount > 0) {
            throw new IllegalArgumentException("Equipment already requested");
        }
        Date dte = new Date();
        req.setBorrowerDate(dte);

        req.setVisaUserStatusDate(dte);
        if (req.getVisaUser() == null) {
            req.setVisaUserStatus(EquipmentBorrowVisaStatus.ACCEPTED);
            req.setOperatorUserStatus(EquipmentBorrowVisaStatus.PENDING);
        } else {
            req.setVisaUserStatus(EquipmentBorrowVisaStatus.PENDING);
            req.setOperatorUserStatus(EquipmentBorrowVisaStatus.PENDING);
        }

        List<AppUser> f = core.findUsersByProfileFilter("Technician+BorrowOperator", core.findUserType("Technician").getId(), eq.getDepartment().getId());
        req.setOperatorUser(VrUtils.radomItem(f));
        req.setOperatorUserStatusDate(dte);

        f = core.findUsersByProfileFilter("BorrowSuperOperator", core.findUserType("Teacher").getId(), eq.getDepartment().getId());
        req.setSuperOperatorUser(VrUtils.radomItem(f));
        req.setSuperOperatorUserStatus(EquipmentBorrowVisaStatus.PENDING);
        req.setSuperOperatorUserStatusDate(dte);

        req.setFinalStatus(EquipmentBorrowRequestStatus.PENDING);
        req.setFinalStatusDate(dte);
        pu.persist(req);
        pu.updateFormulas(Equipment.class, req.getEquipment().getId());
    }

}
