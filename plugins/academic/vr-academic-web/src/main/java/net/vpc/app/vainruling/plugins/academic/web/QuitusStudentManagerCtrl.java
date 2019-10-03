/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import org.springframework.context.annotation.Scope;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.web.jsf.ctrl.BasePageCtrl;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        menu = "/Education",
        url = "modules/academic/quitus-student",
        securityKey = "Custom.Admin.QuitusStudentManager"
)
@Scope(value = "session")
@ManagedBean
public class QuitusStudentManagerCtrl extends BasePageCtrl {

    private Model model = new Model();

    public QuitusStudentManagerCtrl() {
    }

    public Model getModel() {
        return model;
    }

    @PostConstruct
    public void reloadPage() {
        reloadPage(null);
    }

    @OnPageLoad
    public void reloadPage(String cmd) {
        updateUsers();
    }

    public void updateUsers() {
        final CorePlugin c = CorePlugin.get();
        UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                getModel().setUsers(c.findUsersByProfile("Student"));
            }
        });
    }

    public void updateStatusMessages() {
        final AppUser u = getModel().getUser();
        if (u == null) {
            getModel().setContents(new ArrayList<>());
        } else {
            final CorePlugin core = CorePlugin.get();
            List<ContentText> contents = UPA.getPersistenceUnit().invokePrivileged(new Action<List<ContentText>>() {
                @Override
                public List<ContentText> run() {
                    List<?> fa = core.findAllCompletionFullArticles(u.getId(), "Hot", null, null, null, Level.WARNING);
                    return (List<ContentText>) fa;
                }
            });
            getModel().setContents(contents);
        }
    }

    public static class Model {

        private AppUser user;
        private List<AppUser> users;
        private List<ContentText> contents;

        public List<AppUser> getUsers() {
            return users;
        }

        public void setUsers(List<AppUser> users) {
            this.users = users;
        }

        public AppUser getUser() {
            return user;
        }

        public void setUser(AppUser user) {
            this.user = user;
        }

        public List<ContentText> getContents() {
            return contents;
        }

        public void setContents(List<ContentText> contents) {
            this.contents = contents;
        }

    }
}
