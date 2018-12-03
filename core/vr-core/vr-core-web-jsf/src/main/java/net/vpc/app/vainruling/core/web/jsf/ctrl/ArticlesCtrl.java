/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.CmsTextDisposition;
import net.vpc.app.vainruling.core.service.content.CmsTextService;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.content.AppArticleDisposition;
import net.vpc.app.vainruling.core.service.model.content.AppArticleFile;
import net.vpc.app.vainruling.core.service.model.content.AppArticle;
import net.vpc.app.vainruling.core.service.model.content.FullArticle;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@Controller
@Scope(value = "singleton")
public class ArticlesCtrl extends AbstractCmsTextService {

    @Override
    public boolean onAction(String action, int id) {
        ExternalContext ec = FacesContext.getCurrentInstance()
                .getExternalContext();
        try {
            if ("edit".equals(action)) {
                if (ec != null) {
                    Vr.get().redirect(Vr.get().gotoPageObjItem(AppArticle.class.getSimpleName(), String.valueOf(id)));
                    return true;
                }
            } else if ("delete".equals(action)) {
                CorePlugin core = CorePlugin.get();
                AppArticle a = core.findArticle(id);
                if (a != null && !a.isDeleted()) {
                    core.remove("AppArticle", a.getId());
                    return true;
                }
            } else if ("archive".equals(action)) {
                CorePlugin core = CorePlugin.get();
                AppArticle a = core.findArticle(id);
                if (a != null && !a.isArchived()) {
                    core.archive("AppArticle", a);
                    return true;
                }
            } else if ("important".equals(action)) {
                CorePlugin core = CorePlugin.get();
                AppArticle a = core.findArticle(id);
                if (a != null) {
                    a.setImportant(!a.isImportant());
                    core.save("AppArticle", a);
                    return true;
                }
            }
        } catch (IOException e) {
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

    public FullArticle findArticle(int id) {
        return core.findFullArticle(id);
    }

    public List<FullArticle> findArticles(String disposition) {
        return core.findFullArticlesByDisposition(null, disposition);
    }


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
}
