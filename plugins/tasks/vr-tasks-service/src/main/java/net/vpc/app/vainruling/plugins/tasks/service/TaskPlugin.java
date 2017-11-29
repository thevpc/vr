/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.service;

import net.vpc.app.vainruling.core.service.*;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.plugins.AppPlugin;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.InstallDemo;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.plugins.tasks.service.model.*;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.common.util.Utils;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.RemoveOptions;
import net.vpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

/**
 * @author taha.bensalah@gmail.com
 */
@AppPlugin
public class TaskPlugin {

    @Autowired
    CorePlugin core;
    @Autowired
    TraceService trace;

    public List<AppProfile> findTodoListsProfiles(int todoListId) {
        return UPA.getPersistenceUnit().createQuery("select a.profile from TodoListProfile a where a.listId=:li")
                .setParameter("li", todoListId)
                .getResultList();
    }

    public List<TodoList> findProfileTodoLists(int profileId) {
        return UPA.getPersistenceUnit()
                .createQuery("select a.list from TodoListProfile a where a.profileId=:pi")
                .setParameter("pi", profileId)
                .getResultList();
    }

    public List<TodoList> findTodoListsByResp(Integer user) {
        UserSession session = core.getCurrentSession();
        List<AppProfile> up = null;
        if (!session.allowed("TASK_VIEW_ALL")) {
            if (user == null) {
                user = core.getCurrentUserId();
            } else {
                //error
                //user = session.getUser().getId();
            }
        }

        if (user == null) {
            return findTodoLists();
        }
        ArrayList<TodoList> list = new ArrayList<>();
        for (TodoList i : findTodoLists()) {
            AppUser u = i.getRespUser();
            boolean ok = false;
            if (u != null) {
                if (u.getId() == user) {
                    ok = true;
                }
            } else if (i.getRespProfile() != null) {
                if (up == null) {
                    up = core.findProfilesByUser(user);
                }
                for (AppProfile up0 : up) {
                    if (up0.getId() == i.getRespProfile().getId()) {
                        ok = true;
                        break;
                    }
                }
            } else {
                ok = true;
            }
            if (ok) {
                list.add(i);
            }
        }
        return list;
    }

    public List<TodoList> findTodoListsByInitiator(Integer user) {
        UserSession session = core.getCurrentSession();
        HashSet<Integer> fnd = null;
        if (!session.allowed("TASK_VIEW_ALL")) {
            if (user == null) {
                user = core.getCurrentUserId();
            } else {
                //error
                user = core.getCurrentUserId();
            }
        }

        if (user == null) {
            return findTodoLists();
        }
        ArrayList<TodoList> list = new ArrayList<>();
        for (TodoList i : findTodoLists()) {
            AppUser u = i.getRespUser();
            List<AppProfile> acceptedProfiles = findTodoListsProfiles(i.getId());
            boolean ok = false;
            if (acceptedProfiles.isEmpty()) {
                ok = true;
                //all accepted
            } else {
                HashSet<Integer> acc = new HashSet<>();
                for (AppProfile p : acceptedProfiles) {
                    acc.add(p.getId());
                }
                if (fnd == null) {
                    fnd = new HashSet<>();
                    List<AppProfile> up = core.findProfilesByUser(user);
                    for (AppProfile p : up) {
                        fnd.add(p.getId());
                    }
                }
                acc.retainAll(fnd);
                ok = !acc.isEmpty();
            }
            if (ok) {
                list.add(i);
            }
        }
        return list;
    }

    public List<TodoList> findTodoLists() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findAll(TodoList.class);
    }

    public TodoList findTodoList(String name) {
        //sys-labo-action
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (TodoList) pu.findByField(TodoList.class, "name", name);
    }

    public TodoList findTodoList(int id) {
        //sys-labo-action
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (TodoList) pu.findById(TodoList.class, id);
    }

    public void saveTodoList(TodoList list) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (list.getId() == 0) {
            pu.persist(list);
            for (TodoStatusType value : TodoStatusType.values()) {
                TodoStatus s = new TodoStatus();
                s.setName(value.name());
                s.setType(TodoStatusType.DONE);
                s.setList(list);
                saveTodoStatus(s);
            }
            TodoCategory c = new TodoCategory();
            c.setName("divers");
            c.setShortName("-");
            c.setList(list);
            saveTodoCategory(c);
        } else {
            pu.merge(list);
        }
    }

    public void saveCategory(TodoCategory t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (t.getId() == 0) {
            pu.persist(t);
        } else {
            pu.merge(t);
        }
    }

    public void eraseTodo(int todoId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.remove(Todo.class, RemoveOptions.forId(todoId));
    }

    public void removeTodo(int todoId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(Todo.class);
        Todo t = pu.findById(Todo.class, todoId);
        if (t != null) {
            if (!t.isDeleted()) {
                t.setDeleted(true);
                t.setDeletedBy(core.getCurrentUserLogin());
                t.setDeletedOn(new Timestamp(System.currentTimeMillis()));
                pu.merge(t);
                trace.softremoved(Todo.class.getSimpleName(), pu.findById(Todo.class, todoId), e.getParent().getPath(), Level.FINE);
            }
        }
    }

    public void removeTodoList(int todoListId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        TodoList t = pu.findById(TodoList.class, todoListId);
        if (t != null) {
            pu.remove(TodoList.class, RemoveOptions.forId(t.getId()));
//            UserSession session = core.getCurrentSession();
//            t.setDeleted(true);
//            t.setDeletedBy(session.getUser().getLogin());
//            t.setDeletedOn(new Timestamp(System.currentTimeMillis()));
//            pu.merge(t);
        }
    }

    public void removeTodoCategory(int todoCategoryId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        TodoCategory t = pu.findById(TodoCategory.class, todoCategoryId);
        if (t != null) {
            pu.remove(TodoCategory.class, RemoveOptions.forId(todoCategoryId));
        }
    }

    public void removeTodoStatus(int todoStatusId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        TodoStatus t = pu.findById(TodoStatus.class, todoStatusId);
        if (t != null) {
            pu.remove(TodoStatus.class, RemoveOptions.forId(todoStatusId));
        }
    }

    public void archiveTodo(int todoId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Todo t = pu.findById(Todo.class, todoId);
        if (t != null) {
            UserSession session = core.getCurrentSession();
            t.setArchived(true);
            pu.merge(t);
        }
    }

    public Todo findTodo(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (Todo) pu.findById(Todo.class, id);
    }

    public void saveTodoCategory(TodoCategory t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (t.getId() == 0) {
            pu.persist(t);
        } else {
            Object old = pu.findById(t.getClass(), t.getId());
            pu.merge(t);
        }
    }

    public void saveTodoStatus(TodoStatus t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (t.getId() == 0) {
            pu.persist(t);
        } else {
            Object old = pu.findById(t.getClass(), t.getId());
            pu.merge(t);
        }
    }

    public void saveTodo(Todo t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (t.getId() == 0) {
            t.setInitiator(core.getCurrentUser());
            t.setCreationTime(new Timestamp(System.currentTimeMillis()));
            t.setStatus(findStartStatus(t.getList().getId()));
            pu.persist(t);

            TodoProgress p = new TodoProgress();
            p.setTodo(t);
            p.setConsumption(t.getConsumption());
            p.setDate(new Timestamp(System.currentTimeMillis()));
            p.setMessage(t.getMessage());
            p.setProgress(t.getProgress());
            p.setReEstimation(0);
            p.setStatus(t.getStatus());
            pu.persist(p);
        } else {
            Object old = pu.findById(t.getClass(), t.getId());
            pu.merge(t);
            TodoProgress p = new TodoProgress();
            p.setTodo(t);
            p.setConsumption(t.getConsumption());
            p.setDate(new Timestamp(System.currentTimeMillis()));
            p.setMessage(t.getMessage());
            p.setProgress(t.getProgress());
            p.setReEstimation(0);
            p.setStatus(t.getStatus());
            pu.persist(p);
        }
    }

    //    public void saveProgress(TodoProgress todo) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        UserSession session = core.getCurrentSession();
//        pu.persist(todo);
//    }
    public List<TodoCategory> findTodoCategories(int listId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a from TodoCategory a where a.listId=:u")
                .setParameter("u", listId)
                .getResultList();

    }

    public List<TodoStatus> findTodoStatuses(int listId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a from TodoStatus a where a.listId=:u")
                .setParameter("u", listId)
                .getResultList();

    }

    public List<Todo> findTodosByResponsible(Integer listId, Integer responsible, TodoStatusType[] statuses) {

        UserSession session = core.getCurrentSession();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        HashMap<String, Object> params = new HashMap<String, Object>();
        StringBuilder q = new StringBuilder("Select a from Todo a where a.deleted=:d and a.archived=:r");
        params.put("d", false);
        params.put("r", false);
        if (statuses != null && statuses.length>0) {
            q.append(" and ");
            q.append(" ( ");
            for (int i = 0; i < statuses.length; i++) {
                TodoStatusType st = statuses[i];
                if(i>0){
                    q.append(" or ");
                }
                q.append(" a.status.type = :s"+i);
                params.put("s"+i, st);
            }
            q.append(" ) ");
        }
        //a.listId=:li and a.status.type = :s and 
        if (listId != null) {
            q.append(" and a.listId=:li");
            params.put("li", listId);
        }
        if (!session.allowed("TASK_VIEW_ALL")) {
            q.append(" and a.responsibleId=:i");
            params.put("i", Convert.toInt(core.getCurrentUser(), IntegerParserConfig.LENIENT_F));
        } else {
            if (responsible != null) {
                q.append(" and a.responsibleId=:i");
                params.put("i", responsible);
            }
        }
        return pu.createQuery(q.toString()).setParameters(params).getResultList();
    }

    public List<Todo> findTodosByInitiator(Integer listId, Integer initiator, TodoStatusType status) {
        UserSession session = core.getCurrentSession();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        HashMap<String, Object> params = new HashMap<String, Object>();
        StringBuilder q = new StringBuilder("Select a from Todo a where a.deleted=:d and a.archived=:r");
        params.put("d", false);
        params.put("r", false);
        if (status != null) {
            q.append(" and a.status.type = :s");
            params.put("s", status);
        }
        //a.listId=:li and a.status.type = :s and 
        if (listId != null) {
            q.append(" and a.listId=:li");
            params.put("li", listId);
        }
        if (!session.allowed("TASK_VIEW_ALL")) {
            q.append(" and a.initiatorId=:i");
            params.put("i", Convert.toInt(core.getCurrentUserId(),IntegerParserConfig.LENIENT_F));
        } else {
            if (initiator != null) {
                q.append(" and a.initiatorId=:i");
                params.put("i", initiator);
            }
        }
        return pu.createQuery(q.toString()).setParameters(params).getResultList();
    }

    public TodoStatus findStartStatus(int listId) {
        List<TodoStatus> all = findTodoStatuses(listId);
        for (TodoStatus n : all) {
            if (n.getType() == TodoStatusType.UNASSIGNED) {
                return n;
            }
        }
        return null;
    }

    //should implement state machine for statuses
    public TodoStatus findNextStatus(TodoStatus from, TodoStatusType to) {
        List<TodoStatus> all = findTodoStatuses(from.getList().getId());
        for (TodoStatus n : all) {
            if (n.getType() == to) {
                if (hasTransition(from, n)) {
                    return n;
                }
            }
        }
        return null;
    }

    public boolean hasTransition(TodoStatus from, TodoStatus to) {
        TodoStatusType f = from.getType();
        TodoStatusType t = from.getType();
        if (f == t) {
            return false;
        }
        switch (f) {
            case UNASSIGNED: {
                switch (t) {
                    case ASSIGNED: {
                        return true;
                    }
                }
                return false;
            }
            case ASSIGNED: {
                switch (t) {
                    case UNASSIGNED: {
                        return true;
                    }
                    case TO_VERIFY: {
                        return true;
                    }
                    case DONE: {
                        return true;
                    }
                }
                return false;
            }
            case DONE: {
                switch (t) {
                    case ASSIGNED: {
                        return true;
                    }
                }
                return false;
            }
            case TO_VERIFY: {
                switch (t) {
                    case DONE: {
                        return true;
                    }
                    case ASSIGNED: {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    @Start
    private void startService() {
        for (TodoList findTodoListsByResp : findTodoLists()) {
            VrApp.getBean(CorePlugin.class).createRight("Custom.Todo." + findTodoListsByResp.getName(), "TODO " + findTodoListsByResp.getName());
        }
    }

    @Install
    private void installService() {
        InitData d = new InitData();
        d.laboActions = new TodoList();
        d.laboActions.setName(TodoList.LABO_ACTION);
        d.laboActions.setSystemList(true);
        d.laboActions.setRespProfile(core.findProfileByName("Technician"));
        d.laboActions = core.findOrCreate(d.laboActions);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        {
            TodoCategory s = new TodoCategory();
            s.setName("Divers");
            s.setShortName("DIV");
            s.setList(d.laboActions);
            if (pu.createQueryBuilder(TodoCategory.class)
                    .byField("name", s.getName())
                    .byField("list", s.getList())
                    .getFirstResultOrNull() == null) {
                pu.persist(s);
            }
        }
        for (TodoStatusType value : TodoStatusType.values()) {
            TodoStatus s = new TodoStatus();
            s.setList(d.laboActions);
            s.setName(value.name());
            s.setType(value);
            if (pu.createQueryBuilder(TodoStatus.class)
                    .byField("name", s.getName())
                    .byField("list", s.getList())
                    .getFirstResultOrNull() == null) {
                pu.persist(s);
            }
        }

        d.laboTickets = new TodoList();
        d.laboTickets.setName(TodoList.LABO_TICKET);
        d.laboTickets.setSystemList(true);
        d.laboTickets.setRespProfile(core.findProfileByName("Technician"));
        d.laboTickets = core.findOrCreate(d.laboTickets);
        {
            TodoCategory s = new TodoCategory();
            s.setName("Divers");
            s.setList(d.laboTickets);
            if (pu.createQueryBuilder(TodoCategory.class)
                    .byField("name", s.getName())
                    .byField("list", s.getList())
                    .getFirstResultOrNull() == null) {
                pu.persist(s);
            }
        }
        for (TodoStatusType value : TodoStatusType.values()) {
            TodoStatus s = new TodoStatus();
            s.setList(d.laboTickets);
            s.setName(value.name());
            s.setType(value);
            if (pu.createQueryBuilder(TodoStatus.class)
                    .byField("name", s.getName())
                    .byField("list", s.getList())
                    .getFirstResultOrNull() == null) {
                pu.persist(s);
            }
        }
    }

    @InstallDemo
    private void installDemoService() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        final AppUser admin = core.findUser(CorePlugin.USER_ADMIN);
        TodoList laboActionsList = pu.findByMainField(TodoList.class, TodoList.LABO_ACTION);
        TodoList laboTicketsList = pu.findByMainField(TodoList.class, TodoList.LABO_TICKET);

        List<TodoCategory> laboActionsCategories = pu.createQueryBuilder(TodoCategory.class).byField("list", laboActionsList).getResultList();
        List<TodoStatus> laboActionsStatuses = pu.createQueryBuilder(TodoStatus.class).byField("list", laboActionsList).getResultList();
        List<TodoCategory> laboTicketsCategories = pu.createQueryBuilder(TodoCategory.class).byField("list", laboTicketsList).getResultList();
        List<TodoStatus> laboTicketsStatuses = pu.createQueryBuilder(TodoStatus.class).byField("list", laboTicketsList).getResultList();
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        for (int i = 0; i < 20; i++) {
            Todo d = new Todo();
            d.setCategory(laboActionsCategories.get(Utils.rand(0, laboActionsCategories.size())));
            d.setList(laboActionsList);
            d.setDeadline(now);
            d.setName("Action " + (i + 1));
            d.setPriority(TodoPriority.values()[Utils.rand(0, TodoPriority.values().length)]);
            d.setStartTime(now);
            d.setStatus(laboActionsStatuses.get(Utils.rand(0, laboActionsStatuses.size())));
            d.setEstimation(Utils.fibonacci(Utils.rand(1, 4)));
            d.setReEstimation(d.getEstimation());
            switch (d.getStatus().getType()) {
                case DONE: {
                    d.setProgress(100);
                    break;
                }
                case ASSIGNED: {
                    d.setProgress(Utils.rand(20, 80));
                    break;
                }
                case TO_VERIFY: {
                    d.setProgress(100);
                    break;
                }
                case UNASSIGNED: {
                    d.setProgress(0);
                    break;
                }
            }
            d.setResponsible(admin);
            d.setCreationTime(now);
            d.setInitiator(admin);
            if (pu.createQueryBuilder(Todo.class)
                    .byField("name", d.getName())
                    .byField("list", d.getList())
                    .getFirstResultOrNull() == null) {
                pu.persist(d);
            }
        }
        for (int i = 0; i < 20; i++) {
            Todo d = new Todo();
            d.setCategory(laboTicketsCategories.get(Utils.rand(0, laboTicketsCategories.size())));
            d.setList(laboTicketsList);
            d.setDeadline(now);
            d.setName("Ticket " + (i + 1));
            d.setPriority(TodoPriority.values()[Utils.rand(0, TodoPriority.values().length)]);
            d.setStartTime(now);
            d.setStatus(laboTicketsStatuses.get(Utils.rand(0, laboTicketsStatuses.size())));
            d.setEstimation(Utils.fibonacci(Utils.rand(1, 4)));
            d.setReEstimation(d.getEstimation());
            switch (d.getStatus().getType()) {
                case DONE: {
                    d.setProgress(100);
                    break;
                }
                case ASSIGNED: {
                    d.setProgress(Utils.rand(20, 80));
                    break;
                }
                case TO_VERIFY: {
                    d.setProgress(100);
                    break;
                }
                case UNASSIGNED: {
                    d.setProgress(0);
                    break;
                }
            }
            d.setResponsible(admin);
            if (pu.createQueryBuilder(Todo.class)
                    .byField("name", d.getName())
                    .byField("list", d.getList())
                    .getFirstResultOrNull() == null) {
                pu.persist(d);
            }
        }
    }

    private static class InitData {

        TodoList laboActions;
        TodoList laboTickets;
//        List<TodoCategory> laboActionsCategories;
//        List<TodoStatus> laboActionsStatuses;
//        List<TodoCategory> laboTicketsCategories;
//        List<TodoStatus> laboTicketsStatuses;
//        long now;
//        AppUserProfile adminProfile;
//        AppUserProfile techProfile;
//        AppUserProfile inventoryProfile;
//        AppUserProfile teacherProfile;
//        AppUserProfile studentProfile;
//        AppUser admin;
//        AppUser tech1;
//        AppUser tech2;
//        List<AppCivility> civilities;
//        List<AppGender> genders;
//        List<AppDepartment> departments;
//        AppAreaType areaType_etablissement = new AppAreaType("etablissement");
//        AppAreaType areaType_bloc = new AppAreaType("bloc");
//        AppAreaType areaType_salle = new AppAreaType("salle");
//        AppAreaType areaType_armoire = new AppAreaType("armoire");
//        AppAreaType areaType_rangement = new AppAreaType("rangement");
    }
}
