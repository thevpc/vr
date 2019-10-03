/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.CmsTextDisposition;
import net.vpc.app.vainruling.core.service.model.content.AppArticleDisposition;
import net.vpc.app.vainruling.core.service.model.content.FullArticle;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.logging.Level;

/**
 * @author vpc
 */
@Controller
@Scope(value = "singleton")
public class HotCmsTextService extends AbstractCmsTextService {

    @Override
    public int getSupport(String name) {
        if ("hot".equals(name)) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean onAction(String action, int id) {
        ExternalContext ec = FacesContext.getCurrentInstance()
                .getExternalContext();
        try {
            if ("edit".equals(action)) {
//                if (ec != null) {
//                    Vr.get().redirect(Vr.get().gotoPageObjItem(AppArticle.class.getSimpleName(), String.valueOf(id)));
//                    return true;
//                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CmsTextDisposition getContentDispositionByName(String name) {
        AppArticleDisposition a = new AppArticleDisposition();
        a.setName(name);
        a.setEnabled(true);
        return a;
    }

    @Override
    public List<FullArticle> findArticles(String disposition) {
        return core.findAllCompletionFullArticles(core.getCurrentUserId(), disposition, null, null, null, Level.WARNING);
    }

    @Override
    public void updateVisit(int articleId) {
//        core.markArticleVisited(articleId);
    }

    @Override
    public AbstractCmsModel getModel() {
        return VrApp.getBean(HotModel.class);
    }

}
