/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.content.CmsTextDisposition;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.model.content.AppArticle;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.List;
import net.thevpc.app.vainruling.core.service.content.VrContentText;

/**
 * @author taha.bensalah@gmail.com
 */
@Controller
@Scope(value = "singleton")
public class ArticlesCtrl extends AbstractCmsTextService {

    @Override
    public boolean onActionById(String action, int id) {
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

//    @Override
    public boolean onAction(String action, VrContentText a) {
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
    public VrContentText findArticle(int id) {
        return core.findFullArticle(id,null);
    }

    @Override
    public List<VrContentText> findArticles(String disposition, net.thevpc.app.vainruling.core.service.model.content.VrContentTextConfig config) {
        return core.findFullArticlesByDisposition(null, disposition,config);
    }

    @Override
    public void updateVisit(int articleId) {
        core.markArticleVisited(articleId);
    }

    public AbstractCmsModel getModel() {
        return VrApp.getBean(ArticlesModel.class);
    }

    @Override
    public boolean isEnabledActionById(String action, int id) {
        return isEnabledAction(action,findArticle(id));
    }

    @Override
    public boolean isEnabledAction(String action, VrContentText a) {
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
