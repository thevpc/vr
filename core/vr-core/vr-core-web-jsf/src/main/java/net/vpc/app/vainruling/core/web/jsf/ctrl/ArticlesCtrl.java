/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.CmsTextDisposition;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.content.AppArticle;
import net.vpc.app.vainruling.core.service.model.content.FullArticle;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@Controller
@Scope(value = "singleton")
public class ArticlesCtrl extends AbstractCmsTextService {

    @Override
    public boolean onAction(String action, int id) {
        if (id <= 0) {
            return false;
        }
        CorePlugin core = CorePlugin.get();
        AppArticle a = core.findArticle(id);
        if (a == null) {
            return false;
        }
        return onAction(action, a);
    }

//    @Override
    public boolean onAction(String action, AppArticle a) {
        if (a == null) {
            return false;
        }
        ExternalContext ec = FacesContext.getCurrentInstance()
                .getExternalContext();
        try {
            if ("edit".equals(action)) {
                if (ec != null) {
                    Vr.get().redirect(Vr.get().gotoPageObjItem(AppArticle.class.getSimpleName(), String.valueOf(a.getId())));
                    return true;
                }
            } else if ("delete".equals(action)) {
                if (!a.isDeleted()) {
                    core.remove("AppArticle", a.getId());
                    return true;
                }
            } else if ("archive".equals(action)) {
                if (!a.isArchived()) {
                    core.archive("AppArticle", a);
                    return true;
                }
            } else if ("important".equals(action)) {
                a.setImportant(!a.isImportant());
                core.save("AppArticle", a);
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onAction(String action, ContentText a) {
        if (a instanceof FullArticle) {
            return onAction(action, (FullArticle) a);
        }
        return false;
    }

//    @Override
    public boolean onAction(String action, FullArticle a) {
        if (a == null) {
            return false;
        }
        ExternalContext ec = FacesContext.getCurrentInstance()
                .getExternalContext();
        try {
            if ("edit".equals(action)) {
                if (ec != null) {
                    Vr.get().redirect(Vr.get().gotoPageObjItem(AppArticle.class.getSimpleName(), String.valueOf(a.getId())));
                    return true;
                }
            } else if ("delete".equals(action)) {
                if (!a.getArticle().isDeleted()) {
                    core.remove("AppArticle", a.getId());
                    return true;
                }
            } else if ("archive".equals(action)) {
                if (!a.getArticle().isArchived()) {
                    AppArticle aa = core.findArticle(a.getId());
                    if (aa != null && !aa.isArchived()) {
                        aa.setArchived(true);
                        core.archive("AppArticle", aa);
                    }
                    return true;
                }
            } else if ("important".equals(action)) {
                AppArticle aa = core.findArticle(a.getId());
                if (aa != null && !aa.isArchived()) {
                    aa.setImportant(aa.isImportant());
                    core.save("AppArticle", aa);
                }
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getSupport(String name) {
        if ("default".equals(name)) {
            return 1;
        }
        return -1;
    }

    @Override
    public CmsTextDisposition getContentDispositionByName(String name) {
        return core.findArticleDisposition(name);
    }

    @Override
    public FullArticle findArticle(int id) {
        return core.findFullArticle(id);
    }

    @Override
    public List<FullArticle> findArticles(String disposition) {
        return core.findFullArticlesByDisposition(null, disposition);
    }

    @Override
    public void updateVisit(int articleId) {
        core.markArticleVisited(articleId);
    }

    public AbstractCmsModel getModel() {
        return VrApp.getBean(ArticlesModel.class);
    }

    @Override
    public boolean isEnabledAction(String action, int id) {
        AppUser currentUser = core.getCurrentUser();
        if (currentUser != null) {
            AppArticle a = core.findArticle(id);
            if (a != null) {
                if (a.getSender() != null && currentUser.getId() == a.getSender().getId()) {
                    return true;
                }
                if (core.isCurrentSessionAdmin()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isEnabledAction(String action, ContentText a) {
        if (a == null) {
            return false;
        }
        AppUser currentUser = core.getCurrentUser();
        if (currentUser != null) {
            if (a.getUser() != null && currentUser.getId() == a.getUser().getId()) {
                return true;
            }
            if (core.isCurrentSessionAdmin()) {
                return true;
            }
        }
        return false;
    }

}
