/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import java.util.ArrayList;
import java.util.Date;
import net.vpc.app.vainruling.api.TraceService;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.security.UserSession;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.model.AppProfile;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.types.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class LoginService {

    @Autowired
    private TraceService trace;

    public UserSession getUserSession() {
        return VrApp.getContext().getBean(UserSession.class);
    }

    public void logout() {
        final UserSession s = getUserSession();
        VrApp.getBean(ActiveSessionsTracker.class).onDestroy(s);
    }

    public AppUser login(String login, String password) {
        final AppUser user = findEnabledUser(login, password);
        if (user != null) {
            user.setConnexionCount(user.getConnexionCount()+1);
            user.setLastConnexionDate(new DateTime());
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    UPA.getPersistenceUnit().merge(user);
                    return null;
                }
            }, null);
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            getUserSession().setDestroyed(false);
            UserSession s = getUserSession();
            VrApp.getBean(ActiveSessionsTracker.class).onCreate(s);
            trace.trace("login", "successfull", login, getClass().getSimpleName(), null, null, login, user.getId(), Level.INFO);
            getUserSession().setConnexionTime(user.getLastConnexionDate());
            getUserSession().setUser(user);
            final List<AppProfile> userProfiles = findUserProfiles(user.getId());
            Set<String> userProfilesNames = new HashSet<>();
            for (AppProfile u : userProfiles) {
                userProfilesNames.add(u.getName());
            }
            getUserSession().setProfiles(userProfiles);
            StringBuilder ps = new StringBuilder();
            for (AppProfile up : userProfiles) {
                if (ps.length() > 0) {
                    ps.append(", ");
                }
                ps.append(up.getName());
            }
            getUserSession().setProfileNames(userProfilesNames);
            getUserSession().setProfilesString(ps.toString());
            getUserSession().setAdmin(false);
            getUserSession().setRights(core.findUserRights(user.getId()));
            if (user.getLogin().equalsIgnoreCase("admin")) {
                getUserSession().setAdmin(true);
            }
        } else {
            getUserSession().reset();
            AppUser user2 = findUser(login);
            if (user2 == null) {
                trace.trace("login", "login not found. Failed as " + login + "/" + password, login + "/" + password, getClass().getSimpleName(), null, null, (login == null || login.length() == 0) ? "anonymous" : login, -1, Level.SEVERE);
            } else {
                if(user2.isDeleted() || !user2.isEnabled()){
                trace.trace("login", "invalid state. Failed as " + login + "/" + password, login + "/" + password
                        +". deleted="+user2.isDeleted()
                        +". enabled="+user2.isEnabled()
                        , getClass().getSimpleName(), null, null, (login == null || login.length() == 0) ? "anonymous" : login, user2.getId(), Level.SEVERE);
                }else{
                trace.trace("login", "invalid password. Failed as " + login + "/" + password, login + "/" + password, getClass().getSimpleName(), null, null, (login == null || login.length() == 0) ? "anonymous" : login, user2.getId(), Level.SEVERE);
                }
            }
        }
        return user;
    }

    private AppUser findUser(String login) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppUser) pu.findByField(AppUser.class, "login", login);
    }

    private List<AppProfile> findUserProfiles(int userId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u.profile from AppUserProfileBinding  u where u.userId=:userId")
                .setParameter("userId", userId)
                .getEntityList();
    }

    private AppUser findUser(String login, String password) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppUser) pu
                .createQuery("Select u from AppUser u "
                        + "where "
                        + "u.login=:login "
                        + "and u.password=:password")
                .setParameter("login", login)
                .setParameter("password", password)
                .getEntity();
    }
    
    private AppUser findEnabledUser(String login, String password) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppUser) pu
                .createQuery("Select u from AppUser u "
                        + "where "
                        + "u.login=:login "
                        + "and u.password=:password "
                        + "and u.enabled=true "
                        + "and u.deleted=false "
                        + "")
                .setParameter("login", login)
                .setParameter("password", password)
                .getEntity();
    }

}
