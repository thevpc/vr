/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.borrow.service;

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
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowLog;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequest;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequestStatus;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowVisaStatus;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentReturnBorrowedLog;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentBorrowOperatorType;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentBorrowRequestFilter;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentForResponsibleInfo;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
import net.vpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentAcquisitionStatus;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentActionType;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusLog;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusType;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
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
        core.findOrCreateProfile("BorrowSuperOperator");
        core.findOrCreateProfile("BorrowOperator");
        core.findOrCreateProfile("BorrowVisa");
    }

    public EquipmentBorrowLog findBorrowLogByStatusLogId(int statusLogId) {
        return UPA.getPersistenceUnit().createQuery("Select a from EquipmentBorrowLog a where a.statusLogId=:statusLogId")
                .setParameter("statusLogId", statusLogId).getSingleResultOrNull();
    }

    public EquipmentReturnBorrowedLog findReturnBorrowedLogByStatusLogId(int statusLogId) {
        return UPA.getPersistenceUnit().createQuery("Select a from EquipmentReturnBorrowedLog a where a.statusLogId=:statusLogId")
                .setParameter("statusLogId", statusLogId).getSingleResultOrNull();
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

    public boolean isBorrowed(int equipmentId) {
        return findOpenBorrowLogs(equipmentId, null).size() > 0;
    }

    public double findEquipmentBorrowLogRemainingQuantity(int equipmentBorrowLogId) {
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
    }

    public List<EquipmentBorrowLog> findOpenBorrowLogs(Integer equipmentId, Integer borrowerId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery(
                "Select a from EquipmentBorrowLog a where a.archive=false"
                + (VrUtils.isValidId(borrowerId) ? " and a.borrowerId=:borrowerId" : "")
                + (VrUtils.isValidId(equipmentId) ? " and a.statusLog.equipmentId=:equipmentId" : "")
        )
                .setParameter("borrowerId", borrowerId, VrUtils.isValidId(borrowerId))
                .setParameter("equipmentId", equipmentId, VrUtils.isValidId(equipmentId))
                .getResultList();
    }

    public List<EquipmentBorrowRequest> findOpenBorrowRequests(Integer equipmentId, Integer borrowerId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery(
                "Select a from EquipmentBorrowRequest a where a.archive=false"
                        +" and a.finalStatus != :delivered"
                + (VrUtils.isValidId(borrowerId) ? " and a.borrowerUserId=:borrowerId" : "")
                + (VrUtils.isValidId(equipmentId) ? " and a.equipmentId=:equipmentId" : "")
        )
                .setParameter("borrowerId", borrowerId, VrUtils.isValidId(borrowerId))
                .setParameter("equipmentId", equipmentId, VrUtils.isValidId(equipmentId))
                .setParameter("delivered", EquipmentBorrowRequestStatus.DELIVERED)
                .getResultList();
    }

    private AppUser resolveActor(Integer actorId) {
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

    public void addBorrow(int equipmentId, Integer requestId, int borrowerId, Integer actorId, double qty, Timestamp startDate, Timestamp endDate, String shortDesc, String longDesc) {
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
        EquipmentBorrowLog borr = new EquipmentBorrowLog();
        if (startDate == null) {
            startDate = new Timestamp(System.currentTimeMillis());
        }
        if (endDate == null) {
            endDate = startDate;
        }
        borr.setStartDate(startDate);
        borr.setEndDate(endDate);
        borr.setQuantity(qty);
        borr.setBorrower(borrower);

        EquipmentStatusLog statusLog = new EquipmentStatusLog();
        statusLog.setEquipment(e);
        statusLog.setAction(getBorrowAction());
        statusLog.setActor(resolveActor(actorId));
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
        if (requestId != null) {
            EquipmentBorrowRequest request = pu.findById(EquipmentBorrowRequest.class, requestId);
            request.setBorrow(borr);
            pu.merge(request);
        }
    }

    public boolean returnBorrowed(int equipmentId, Integer borrowerId, Integer actorId, double qty, Timestamp returnDate, String shortDesc, String longDesc) {
        if (returnDate == null) {
            returnDate = new Timestamp(System.currentTimeMillis());
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (EquipmentBorrowLog borrowLog : findOpenBorrowLogs(equipmentId, borrowerId)) {
            if (qty <= 0) {
                return true;
            }
            double q = findEquipmentBorrowLogRemainingQuantity(borrowLog.getId());
            if (q <= 0) {
                borrowLog.setArchive(true);
                pu.merge(borrowLog);
                EquipmentBorrowRequest r = findBorrowRequestByBorrowLog(borrowLog.getId());
                if (r != null) {
                    r.setArchive(true);
                }
            } else {
                if (qty >= q) {
                    qty -= q;
                }
                EquipmentReturnBorrowedLog rblog = new EquipmentReturnBorrowedLog();
                rblog.setBorrowLog(borrowLog);
                rblog.setQuantity(q);
                rblog.setReturnDate(returnDate);

                EquipmentStatusLog slog = new EquipmentStatusLog();
                slog.setActor(resolveActor(actorId));
                slog.setAction(getReturnBorrowedAction());
                slog.setEquipment(borrowLog.getStatusLog().getEquipment());
                slog.setDescription(longDesc);
                slog.setName(shortDesc);
                slog.setInQty(q);
                slog.setResponsible(borrowLog.getBorrower());
                slog.setStartDate(returnDate);
                slog.setEndDate(returnDate);
                slog.setType(EquipmentStatusType.AVAILABLE);
                slog.setAcquisition(null);
                pu.persist(slog);

                rblog.setStatusLog(slog);
                pu.persist(slog);
            }
        }
        return qty != 0;
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
        return pu.createQuery("Select a from EquipmentBorrowRequest a where a.borrowLogId=:borrowLogId")
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

    public void changeEquipmentBorrowVisaStatus(int rentId, EquipmentAcquisitionStatus status) {

    }

    public void changeEquipmentBorrowFinalStatus(int rentId, Equipment status) {
        //UPA.getPersistenceUnit().
    }

    public List<AppUser> findBorrowVisaUsers() {
        return CorePlugin.get().findUsersByProfile("BorrowVisa");
    }

    public boolean isBorrowVisaUser(Integer userId) {
        Set<String> profiles = CorePlugin.get().findProfilesByUser(userId).stream().map(x -> x.getCode()).collect(Collectors.toSet());
        return profiles.contains("BorrowVisa");
    }

    public boolean isBorrowOperatorUser(Integer userId) {
        Set<String> profiles = CorePlugin.get().findProfilesByUser(userId).stream().map(x -> x.getCode()).collect(Collectors.toSet());
        return profiles.contains("BorrowOperator");
    }

    public boolean isBorrowSuperOperatorUser(Integer userId) {
        Set<String> profiles = CorePlugin.get().findProfilesByUser(userId).stream().map(x -> x.getCode()).collect(Collectors.toSet());
        return profiles.contains("BorrowSuperOperator");
    }

    public List<AppUser> findBorrowerUsers(Integer equipmentDepartmentId) {
        final CorePlugin core = CorePlugin.get();
        List<Integer> userIds = UPA.getPersistenceUnit().createQuery("Select distinct(a.responsibleId) from EquipmentBorrowLog a where a.archive=false"
                + ((equipmentDepartmentId != null && equipmentDepartmentId >= 0) ? " and a.departmentId=:departmentId" : "")
        )
                .setParameter("statusType", EquipmentStatusType.BORROWED)
                .setParameter("departmentId", equipmentDepartmentId, equipmentDepartmentId != null && equipmentDepartmentId >= 0)
                .<Integer>getResultList();
        return userIds.stream().map(x -> x == null ? null : core.findUser(x)).filter(x -> x != null).collect(Collectors.toList());
    }

    public List<EquipmentForResponsibleInfo> findBorrowedEquipmentsInfo(Integer borrowerId) {
        return findOpenBorrowLogs(null, borrowerId).stream().map(x -> new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.OPERATOR, x))
                .collect(Collectors.toList());
    }

    public List<EquipmentForResponsibleInfo> findBorrowEquipmentsVisasForResponsibleInfo(Integer userId) {
        if (userId == null || userId < 0) {
            return Collections.emptyList();
        }
        Map<Integer, EquipmentForResponsibleInfo> map = new HashMap<>();
        Set<String> profiles = CorePlugin.get().findProfilesByUser(userId).stream().map(x -> x.getCode()).collect(Collectors.toSet());
        if (profiles.contains("BorrowVisa")) {
            for (EquipmentBorrowRequest e : findRequestsByBorrower(new EquipmentBorrowRequestFilter()
                    .setVisaUserId(userId)
                    .setVisaStatus(EquipmentBorrowRequestStatus.PENDING)
                    .setArchive(false)
            )) {
                EquipmentForResponsibleInfo old = map.get(e.getEquipment().getId());
                if (old == null) {
                    map.put(e.getEquipment().getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.VISA, e));
                }
            }
        }
        if (profiles.contains("BorrowOperator")) {
            for (EquipmentBorrowRequest e : findRequestsByBorrower(new EquipmentBorrowRequestFilter()
                    .setOperatorStatus(EquipmentBorrowRequestStatus.PENDING)
                    .setDepartmentId(core.findUser(userId).getDepartment().getId())
                    .setArchive(false)
            )) {
                EquipmentForResponsibleInfo old = map.get(e.getEquipment().getId());
                if (old == null) {
                    map.put(e.getEquipment().getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.VISA, e));
                }
            }
        }
        if (profiles.contains("BorrowSuperOperator")) {
            for (EquipmentBorrowRequest e : findRequestsByBorrower(new EquipmentBorrowRequestFilter()
                    .setSuperOperatorStatus(EquipmentBorrowRequestStatus.PENDING)
                    .setDepartmentId(core.findUser(userId).getDepartment().getId())
                    .setArchive(false)
            )) {
                EquipmentForResponsibleInfo old = map.get(e.getEquipment().getId());
                if (old == null) {
                    map.put(e.getEquipment().getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.VISA, e));
                }
            }
        }
        ArrayList<EquipmentForResponsibleInfo> list = new ArrayList<>(map.values());
        list.sort(null);
        return list;
    }

    public List<EquipmentForResponsibleInfo> findBorrowableEquipmentsForResponsibleInfo(Integer userId, Integer equipmentTypeId, Integer departmentId) {
        if (userId == null || userId < 0) {
            return Collections.emptyList();
        }
        Map<Integer, EquipmentForResponsibleInfo> map = new HashMap<>();
        for (Equipment e : findBorrowableEquipments(userId, equipmentTypeId, departmentId)) {
            map.put(e.getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.BORROWER, e));
        }
        for (EquipmentBorrowLog item : findOpenBorrowLogs(null, userId)) {
            map.remove(item.getStatusLog().getEquipment().getId());
        }
        for (EquipmentBorrowRequest item : findOpenBorrowRequests(null, userId)) {
            map.remove(item.getEquipment().getId());
        }
        ArrayList<EquipmentForResponsibleInfo> list = new ArrayList<>(map.values());
        list.sort(null);
        return list;
    }

    public List<EquipmentForResponsibleInfo> findBorrowedEquipmentsForResponsibleInfo(Integer userId, Integer equipmentTypeId, Integer departmentId) {
//        if (userId < 0) {
//            return Collections.emptyList();
//        }
        Map<Integer, EquipmentForResponsibleInfo> map = new HashMap<>();
        for (EquipmentBorrowLog e : findOpenBorrowLogs(null, userId)) {
            map.put(e.getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.BORROWER, e));
        }
        for (EquipmentBorrowRequest e : findRequestsByBorrower(
                new EquipmentBorrowRequestFilter().setBorrowerUserId(userId)
                        .setEquipmentTypeId(equipmentTypeId).setDepartmentId(departmentId)
                        .setArchive(false))) {
            EquipmentForResponsibleInfo old = map.get(e.getEquipment().getId());
            if (old == null) {
                map.put(e.getEquipment().getId(), new EquipmentForResponsibleInfo(EquipmentBorrowOperatorType.BORROWER, e));
            }
        }
        ArrayList<EquipmentForResponsibleInfo> list = new ArrayList<>(map.values());
        list.sort(null);
        return list;
    }

//    public List<EquipmentBorrowRequest> findEquipmentRequestsByBorrowerUser(Integer userId, Integer visaUserId,Integer equipmentTypeId, Integer departmentId, Boolean requestHistory) {
    public List<EquipmentBorrowRequest> findRequestsByBorrower(EquipmentBorrowRequestFilter filter) {
//        EquipmentBorrowRequest prototype;

        StringBuilder sb = new StringBuilder();
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
        if (filter.getVisaStatus() != null) {
            sb.append(" and a.visaUserStatus=:visaUserStatus");
        }
        if (filter.getOperatorStatus() != null) {
            sb.append(" and a.operatorUserStatus=:operatorUserStatus");
        }
        if (filter.getSuperOperatorStatus() != null) {
            sb.append(" and a.superOperatorUserStatus=:superOperatorUserStatus");
        }
        if (filter.getArchive() != null) {
            sb.append(" and a.archive=:archive");
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
                .setParameter("visaUserStatus", filter.getVisaStatus(), filter.getVisaStatus() != null)
                .setParameter("operatorUserStatus", filter.getOperatorStatus(), filter.getOperatorStatus() != null)
                .setParameter("superOperatorUserStatus", filter.getSuperOperatorStatus(), filter.getSuperOperatorStatus() != null)
                .setParameter("archive", filter.getArchive(), filter.getArchive() != null)
                .setParameter("typeId", filter.getEquipmentTypeId(), VrUtils.isValidId(filter.getEquipmentTypeId()))
                .setParameter("departmentId", filter.getDepartmentId(), VrUtils.isValidId(filter.getDepartmentId()))
                .getResultList();
    }

    public boolean isEquipmentRequestAcceptVisaUser(int equipmentId, int userId) {
        return true;
    }

    public boolean isEquipmentRequestAcceptSuperOperatorUser(int equipmentId, int userId) {
        return true;
    }

    public void applyEquipmentRequestByVisaUser(int requestId, EquipmentBorrowOperatorType type, Integer userId, boolean accept) {
        EquipmentBorrowVisaStatus acceptedStatus = accept ? EquipmentBorrowVisaStatus.ACCEPTED : EquipmentBorrowVisaStatus.REJECTED;
        Date dte = new Date();
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
            switch (req.getFinalStatus()) {
                case REJECTED:
                case ACCEPTED: {
                    return;
                }
            }
            switch (type) {
                case BORROWER: {
                    return;
                }
                case VISA: {
                    req.setVisaUser(u);
                    req.setVisaUserStatus(acceptedStatus);
                    req.setVisaUserStatusDate(dte);
                    pu.merge(req);
                    break;
                }
                case OPERATOR: {
                    req.setOperatorUser(u);
                    req.setOperatorUserStatus(acceptedStatus);
                    req.setOperatorUserStatusDate(dte);
                    pu.merge(req);
                    break;
                }
                case SUPER_OPERATOR: {
                    req.setSuperOperatorUser(u);
                    req.setSuperOperatorUserStatus(acceptedStatus);
                    req.setSuperOperatorUserStatusDate(dte);
                    pu.merge(req);
                    break;
                }
            }

            if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED) {
                req.setFinalStatus(EquipmentBorrowRequestStatus.ACCEPTED);
                req.setFinalStatusDate(dte);
                pu.merge(req);
                addBorrow(req.getEquipment().getId(), req.getId(), req.getBorrowerUser().getId(), u.getId(), req.getQuantity(), new Timestamp(req.getFromDate().getTime()), new Timestamp(req.getToDate().getTime()), null, req.getDescription());
            } else if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.REJECTED) {
                req.setFinalStatus(EquipmentBorrowRequestStatus.REJECTED);
                req.setFinalStatusDate(dte);
                pu.merge(req);
            }
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

    public void addEquipmentBorrowRequest(EquipmentBorrowRequest req) {
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
        if (!(fromDte != null
                && toDte != null
                && fromDte.compareTo(toDte) <= 0)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        if (eq.getDepartment() == null) {
            throw new IllegalArgumentException("Invalid equipment, missing department");
        }
        int exitingCount = findOpenBorrowRequests(eq.getId(), u.getId()).size();
                pu.createQuery("Select a from EquipmentBorrowRequest a where a.equipmentId=:eid "
                + " and a.archive=false "
                + " and a.borrowerUserId=:uid"
                + " and a.finalStatus!=:accepted  "
                + " and a.finalStatus!=:rejected"
        ).setParameter("uid", u.getId())
                .setParameter("eid", eq.getId())
                .setParameter("accepted", EquipmentBorrowRequestStatus.ACCEPTED)
                .setParameter("rejected", EquipmentBorrowRequestStatus.REJECTED)
                .getResultList().size();
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
            req.setOperatorUserStatus(EquipmentBorrowVisaStatus.NEW);
        }

        List<AppUser> f = core.findUsersByProfileFilter("Technician+BorrowOperator", core.findUserType("Technician").getId(), eq.getDepartment().getId());
        req.setOperatorUser(VrUtils.radomItem(f));
        req.setOperatorUserStatusDate(dte);

        f = core.findUsersByProfileFilter("BorrowSuperOperator", core.findUserType("Teacher").getId(), eq.getDepartment().getId());
        req.setSuperOperatorUser(VrUtils.radomItem(f));
        req.setSuperOperatorUserStatus(EquipmentBorrowVisaStatus.NEW);
        req.setSuperOperatorUserStatusDate(dte);

        req.setFinalStatus(EquipmentBorrowRequestStatus.NEW);
        req.setFinalStatusDate(dte);
        pu.persist(req);
    }

}
