/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.tasks.service;

import net.thevpc.app.vainruling.VrPlugin;
import net.thevpc.app.vainruling.VrStart;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.ProfileRightBuilder;
import net.thevpc.app.vainruling.core.service.TraceService;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.core.service.*;
import net.thevpc.app.vainruling.core.service.model.AppProfile;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.upa.Entity;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.RemoveOptions;
import net.thevpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.thevpc.app.vainruling.core.service.util.I18n;
import net.thevpc.app.vainruling.plugins.tasks.service.model.Todo;
import net.thevpc.app.vainruling.plugins.tasks.service.model.TodoCategory;
import net.thevpc.app.vainruling.plugins.tasks.service.model.TodoList;
import net.thevpc.app.vainruling.plugins.tasks.service.model.TodoProgress;
import net.thevpc.app.vainruling.plugins.tasks.service.model.TodoStatus;
import net.thevpc.app.vainruling.plugins.tasks.service.model.TodoStatusGroup;
import net.thevpc.app.vainruling.plugins.tasks.service.model.TodoStatusType;
import net.thevpc.common.strings.StringUtils;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin
public class TaskPlugin {

    private static Logger log = java.util.logging.Logger.getLogger(TaskPlugin.class.getName());

    @Autowired
    CorePlugin core;
    @Autowired
    TraceService trace;

    @VrStart
    private void startService() {
        ProfileRightBuilder b = new ProfileRightBuilder();
        for (TodoList findTodoListsByResp : findTodoLists()) {
            b.addName(TaskPluginSecurity.PREFIX_RIGHT_CUSTOM_TODO + findTodoListsByResp.getName(), "TODO " + findTodoListsByResp.getName());
        }
        b.execute();
        TodoStatusGroup g0 = findDefaultStatusGroup();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (TodoList t : findTodoLists()) {
            if (t.getStatusGroup() == null) {
                t.setStatusGroup(g0);
                pu.merge(t);
            }
        }
        CorePlugin core = CorePlugin.get();
        AppProfile to = core.findOrCreateProfile("TodoOwner");
        ProfileRightBuilder prb = new ProfileRightBuilder();
        prb.add(to.getId(),
                "Todo.Load", "Todo.Persist", "Todo.Update",
                "TodoProgress.Load", "TodoProgress.Persist", "TodoProgress.Update",
                "TodoCategory.Load", "TodoCategory.Persist", "TodoCategory.Update"
        );
        prb.execute();
    }

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

    public boolean isAllowedList(TodoList list, boolean promoteAdmin) {
        if (list == null) {
            return false;
        }
        CorePlugin core = CorePlugin.get();
        if (core.getCurrentUserId() == null) {
            return false;
        }
        if (list.getRespUser() == null) {
            log.log(Level.SEVERE, "Todo List : {0} has no responsible. Will be ignored.", list.getId());
            return false;
        }
        if (core.getCurrentUserId() == list.getRespUser().getId()) {
            return true;
        }
        if (promoteAdmin) {
            if (core.isCurrentSessionAdminOrUser(core.getCurrentUserId())) {
                return true;
            }
        }
        if (!StringUtils.isBlank(list.getCollaborators())) {
            if (core.isUserMatchesProfileFilter(core.getCurrentUserId(), list.getCollaborators())) {
                return true;
            }
        }
        return false;
    }

    public List<TodoList> findTodoListsByResp(Integer user, boolean promoteAdmin) {
        if (user == null) {
            user = core.getCurrentUserId();
        }
        if (user == null) {
            return Collections.emptyList();
        }
        ArrayList<TodoList> list = new ArrayList<>();
        for (TodoList i : findTodoLists()) {
            if (isAllowedList(i, promoteAdmin)) {
                list.add(i);
            }
        }
        return list;
    }

    public List<TodoList> findTodoListsByInitiator(Integer user) {
        HashSet<Integer> fnd = null;
        if (user == null) {
            user = core.getCurrentUserId();
        } else {
            //error
            user = core.getCurrentUserId();
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

    public List<TodoStatusGroup> findTodoStatusGroups() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findAll(TodoStatusGroup.class);
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
            if (list.getStatusGroup() == null) {
                list.setStatusGroup(findDefaultStatusGroup());
            }
            pu.persist(list);
            TodoCategory c = new TodoCategory();
            c.setName("divers");
            c.setShortName("D");
            c.setList(list);
            saveTodoCategory(c);
        } else {
            pu.merge(list);
        }
    }

    public TodoStatusGroup findDefaultStatusGroup() {
        List<TodoStatusGroup> gs = new ArrayList<>(findTodoStatusGroups());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (gs.isEmpty()) {
            TodoStatusGroup g = new TodoStatusGroup();
            g.setName("Default");
            pu.persist(g);
            gs.add(g);
            for (TodoStatusType value : TodoStatusType.values()) {
                TodoStatus s = new TodoStatus();
                s.setName(I18n.get().getEnum(value));
                s.setType(value);
                s.setStatusGroup(g);
                pu.persist(s);
            }
            return g;
        }
        return gs.get(0);
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
//            t.setDeletedBy(session.getUser().getUserLogin());
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
            if (t.getStatus() == null) {
                t.setStatus(findStartStatus(t.getList().getStatusGroup().getId()));
            }
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
    public void addTodoCategory(int listId, String name, String shortName) {
        TodoList l = findTodoList(listId);
        if (l == null) {
            return;
        }
        if (StringUtils.isBlank(shortName)) {
            shortName = name;
        }
        for (TodoCategory o : findTodoCategories(listId)) {
            if (Objects.equals(o.getName(), name)) {
                return;
            }
            if (Objects.equals(o.getShortName(), shortName)) {
                return;
            }
        }
        TodoCategory c = new TodoCategory();
        c.setList(l);
        c.setName(name);
        c.setShortName(shortName);
        UPA.getPersistenceUnit().persist(c);
    }

    public List<AppUser> findCollaborators(int listId) {
        TodoList l = findTodoList(listId);
        if (l == null) {
            return null;
        }
        return findCollaborators(l);
    }

    public List<AppUser> findCollaborators(TodoList list) {
        String p = list.getCollaborators();
        if (!StringUtils.isBlank(p)) {
            List<AppUser> t = new ArrayList<>(CorePlugin.get().findUsersByProfileFilter(p, null, null));
            AppUser u = list.getRespUser();
            if (u != null) {
                boolean found = false;
                for (AppUser a : t) {
                    if (a.getId() == u.getId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    t.add(u);
                }
            }
            return t;
        } else {
            List<AppUser> t = new ArrayList<>();
            AppUser u = list.getRespUser();
            if (u != null) {
                t.add(u);
            }
            return t;
        }
    }

    public TodoCategory addTodoCategory(int listId, String name) {
        TodoList l = findTodoList(listId);
        if (l == null) {
            return null;
        }
        String name0 = VrUtils.normalizeName(name);
        for (TodoCategory o : findTodoCategories(listId)) {
            if (VrUtils.normalizeName(o.getName()).equals(name0)) {
                return o;
            }
            if (VrUtils.normalizeName(o.getShortName()).equals(name0)) {
                return o;
            }
        }
        TodoCategory c = new TodoCategory();
        c.setList(l);
        c.setName(name);
        c.setShortName(name);
        UPA.getPersistenceUnit().persist(c);
        return c;
    }

    public List<TodoCategory> findTodoCategories(int listId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a from TodoCategory a where a.listId=:u")
                .setParameter("u", listId)
                .getResultList();

    }

    public List<TodoStatus> findTodoStatuses(int groupId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a from TodoStatus a where a.statusGroupId=:u")
                .setParameter("u", groupId)
                .getResultList();

    }

    public List<Todo> findTodosByResponsible(Integer listId, Integer responsible, TodoStatusType[] statuses) {

        PersistenceUnit pu = UPA.getPersistenceUnit();
        HashMap<String, Object> params = new HashMap<String, Object>();
        StringBuilder q = new StringBuilder("Select a from Todo a where a.deleted=:d and a.archived=:r");
        params.put("d", false);
        params.put("r", false);
        if (statuses != null && statuses.length > 0) {
            q.append(" and ");
            q.append(" ( ");
            for (int i = 0; i < statuses.length; i++) {
                TodoStatusType st = statuses[i];
                if (i > 0) {
                    q.append(" or ");
                }
                q.append(" a.status.type = :s" + i);
                params.put("s" + i, st);
            }
            q.append(" ) ");
        }
        //a.listId=:li and a.status.type = :s and 
        if (listId != null) {
            q.append(" and a.listId=:li");
            params.put("li", listId);
        }
        if (responsible != null) {
            q.append(" and a.responsibleId=:i");
            params.put("i", responsible);
        }
        return pu.createQuery(q.toString()).setParameters(params).getResultList();
    }

    public List<Todo> findTodosByInitiator(Integer listId, Integer initiator, TodoStatusType status) {
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
        if (initiator != null) {
            q.append(" and a.initiatorId=:i");
            params.put("i", initiator);
        }
        return pu.createQuery(q.toString()).setParameters(params).getResultList();
    }

    public TodoStatus findStartStatus(int groupId) {
        List<TodoStatus> all = findTodoStatuses(groupId);
        for (TodoStatus n : all) {
            if (n.getType() == TodoStatusType.UNASSIGNED) {
                return n;
            }
        }
        return null;
    }

    //should implement state machine for statuses
    public TodoStatus findNextStatus(TodoStatus from, TodoStatusType to) {
        TodoStatusGroup g = from.getStatusGroup();
        if (g == null) {
            return null;
        }
        List<TodoStatus> all = findTodoStatuses(g.getId());
        for (TodoStatus n : all) {
            if (n.getType() == to) {
                if (hasTransition(from, n)) {
                    return n;
                }
            }
        }
        return null;
    }

    public boolean isTodoCollaborator(TodoList list, Integer user) {
        if (list == null) {
            return false;
        }
        CorePlugin c = CorePlugin.get();
        if (user == null) {
            user = c.getCurrentUserId();
        }
        if (user == null) {
            return false;
        }
        boolean visible = c.isCurrentSessionAdmin();
        if (!visible) {
            AppUser r = list.getRespUser();
            if (r != null && r.getId() == c.getCurrentUserIdFF()) {
                visible = true;
            }
        }
        if (!visible) {
            String r = list.getCollaborators();
            if (!StringUtils.isBlank(r)) {
                visible = c.isUserMatchesProfileFilter(c.getCurrentUserIdFF(), r);
            }
        }
        return visible;
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

}
