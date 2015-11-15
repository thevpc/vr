/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.security;

import net.vpc.app.vainruling.api.security.UserSession;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.TraceService;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.upa.DefaultUserPrincipal;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.PersistenceGroupSecurityManager;
import net.vpc.upa.UserPrincipal;
import net.vpc.upa.config.SecurityContext;
import net.vpc.upa.exceptions.UPAException;

/**
 *
 * @author vpc
 */
@SecurityContext
public class VRSecurityManager implements PersistenceGroupSecurityManager {

    private static final String INTERNAL_LOGIN = "<internal>";
    public static final Set<String> denyAdmin = new HashSet<String>();
    public static final Set<String> allowOthers = new HashSet<String>();
    public static final Set<String> denyOthers = new HashSet<String>();

    static {
        for (String e : CorePlugin.ADMIN_ENTITIES) {
            denyOthers.add(e + ".Persist");
            denyOthers.add(e + ".Update");
            denyOthers.add(e + ".Delete");
            denyOthers.add(e + ".Clone");
            denyOthers.add(e + ".Rename");
            denyOthers.add(e + ".DefaultEditor");
        }
    }

    public boolean isSystem(String s) {
        return INTERNAL_LOGIN.equals(s);
    }

    public boolean isAdmin(String login) {
        if (CorePlugin.USER_ADMIN.equals(login)) {
            return true;
        }
        UserSession s = UserSession.getCurrentSession();
        if (s == null) {
            return false;
        }
        return s.getProfileNames().contains(CorePlugin.PROFILE_ADMIN);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return "admin".equals(s)
//                || pu.createQuery("Select u from AppUserProfileBinding u where u.user.login=:login and u.profile.name=:profile")
//                .setParameter("login", s)
//                .setParameter("profile", CorePlugin.PROFILE_ADMIN)
//                .getRecordList().size() > 0;
    }

    public boolean isAllowedKey(Entity e, String key) throws UPAException {
        if (key == null) {
            return true;
        }
        return isAllowedKey(e.getAbsoluteName() + "." + key);
    }

    @Override
    public boolean isAllowedKey(String key) throws UPAException {
        if (key == null) {
            return true;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        UserPrincipal userPrincipal = pu.getUserPrincipal();
        String u = userPrincipal == null ? null : userPrincipal.getName();
        if (isSystem(u)) {
            return true;
        }
        if (isAdmin(u)) {
            if (denyAdmin.contains(key)) {
                return false;
            }
            return true;
        }
        if (allowOthers.contains(key)) {
            return true;
        }
        if (denyOthers.contains(key)) {
            return false;
        }
        UserSession s = UserSession.getCurrentSession();
        if (s == null) {
            return false;
        }
        return s.getRights().contains(key);
//        List<AppProfileRight> list = pu.createQuery("Select r from AppProfileRight r inner join AppUserProfileBinding u on u.profileId=r.profileId where u.user.login=:login")
//                .setParameter("login", u).getEntityList();
//        for (AppProfileRight x : list) {
//            if (x.getRight().getName().endsWith(key)) {
//                return true;
//            }
//        }
//        return false;
    }

    @Override
    public UserPrincipal getUserPrincipal() throws UPAException {
        UserSession sm = VrApp.getBean(UserSession.class);
        AppUser user = (sm == null) ? null : sm.getUser();
        return new DefaultUserPrincipal(user == null ? "anonymous" : user.getLogin(), user);
    }

    @Override
    public UserPrincipal loginPrivileged(String login) throws UPAException {
        if (login == null) {
            login = "";
        }
        if (login.equals("")) {
            login = INTERNAL_LOGIN;
        }
        TraceService trace = VrApp.getContext().getBean(TraceService.class);
        if (login.equals(INTERNAL_LOGIN)) {
            AppUser user = new AppUser();
            user.setLogin(login);
            return new DefaultUserPrincipal(INTERNAL_LOGIN, user);
        } else {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            UserSession sm = null;
            try {
                sm = VrApp.getBean(UserSession.class);
            } catch (Exception e) {
                //
            }
            AppUser user = (AppUser) pu.createQuery("Select u from AppUser u where u.login=:login")
                    .setParameter("login", login)
                    .getEntity();
            if (user != null) {
                trace.trace("login-privileged", "successfull", login, getClass().getSimpleName(), null, null, login, user.getId(), Level.INFO, sm == null ? null : sm.getClientIpAddress());
                return new DefaultUserPrincipal(login, user);
            } else {
                trace.trace("login-privileged", "failed", login, getClass().getSimpleName(), null, null, "anonymous", -1, Level.SEVERE, sm == null ? null : sm.getClientIpAddress());
                throw new UPAException("InvalidLogin");
            }
        }
    }

    @Override
    public UserPrincipal login(String login, String credentials) throws UPAException {
        if (login == null) {
            login = "";
        }
        if (credentials == null) {
            credentials = "";
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppUser user = (AppUser) pu.createQuery("Select u from AppUser u where u.login=:login and u.password=:password")
                .setParameter("login", login)
                .setParameter("password", credentials)
                .getEntity();
        TraceService trace = VrApp.getContext().getBean(TraceService.class);
        UserSession sm = null;
        try {
            sm = VrApp.getBean(UserSession.class);
        } catch (Exception e) {
            //
        }
        if (user != null) {
            trace.trace("login", "successfull", login, getClass().getSimpleName(), null, null, login, user.getId(), Level.INFO,sm == null ? null : sm.getClientIpAddress());
            return new DefaultUserPrincipal(login, user);
        } else {
            trace.trace("login", "failed", login + "/" + credentials, getClass().getSimpleName(), null, null, "anonymous", -1, Level.SEVERE,sm == null ? null : sm.getClientIpAddress());
            throw new UPAException("InvalidLogin");
        }
    }

}
