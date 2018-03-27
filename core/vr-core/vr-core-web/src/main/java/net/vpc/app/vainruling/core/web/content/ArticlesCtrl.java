/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.content;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.CmsTextDisposition;
import net.vpc.app.vainruling.core.service.content.CmsTextService;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.*;
import net.vpc.app.vainruling.core.service.model.content.ArticlesDisposition;
import net.vpc.app.vainruling.core.service.model.content.ArticlesFile;
import net.vpc.app.vainruling.core.service.model.content.ArticlesItem;
import net.vpc.app.vainruling.core.service.model.content.FullArticle;

/**
 * @author taha.bensalah@gmail.com
 */
@Controller
@Scope(value = "singleton")
public class ArticlesCtrl implements CmsTextService {

    @Autowired
    private CorePlugin core;
    private String[] imageSwitchEffects = new String[]{
            "blindX",
            "blindY",
            "blindZ",
            "cover",
            "curtainX",
            "curtainY",
            "fade",
            "fadeZoom",
            "growX",
            "growY",
            "none",
            "scrollUp",
            "scrollDown",
            "scrollLeft",
            "scrollRight",
            "scrollVert",
            "shuffle",
            "slideX",
            "slideY",
            "toss",
            "turnUp",
            "turnDown",
            "turnLeft",
            "turnRight",
            "uncover",
            "wipe",
            "zoom"
    };

    public ArticlesModel getModel() {
        return VrApp.getBean(ArticlesModel.class);
    }

    public void refresh() {
    }

    public String getDispositionActionName(String disposition){
        ArticlesDisposition articleDisposition = core.findArticleDisposition(disposition);
        String actionName=null;
        if(articleDisposition!=null){
            actionName=articleDisposition.getActionName();
        }
        if(StringUtils.isEmpty(actionName)){
            actionName="$$"+disposition;
        }
        return actionName;
    }

    public boolean isDispositionEnabled(String disposition){
        ArticlesDisposition articleDisposition = core.findArticleDisposition(disposition);
        if(articleDisposition!=null){
            return articleDisposition.isEnabled();
        }
        return false;
    }


    public void updateVisit(int articleId) {
        core.markArticleVisited(articleId);
    }

    public void loadContentTexts(String name) {
        List<FullArticle> a = findArticles(name);
//        if("Welcome".equalsIgnoreCase(name)){
//            //add further filter
//            for (Iterator<FullArticle> iterator = a.iterator(); iterator.hasNext(); ) {
//                FullArticle article = iterator.next();
//                DateTime d = article.getArticlesItem().getSendTime();
//                Date d2 = net.vpc.common.util.DateUtils.addMonths(new Date(), 1);
//                if(d.compareTo(d2)<0){
//                    iterator.remove();
//                }
//            }
//        }
        getModel().setDisposition(name);
        getModel().getArticles().put(name, a);
        if (getModel().getCurrent() == null) {
            if (a != null && a.size() > 0) {
                if(a.get(0)!=null) {
                    updateVisit(a.get(0).getId());
                }
                setSelectedContentTextById(a.get(0).getId());
            } else {
                getModel().setCurrent(null);
            }
        }
    }

    @Override
    public void setContentDisposition(String name) {
        loadContentTexts(name);
    }

    public String getImageSwitchRandomEffect() {
        return imageSwitchEffects[(int) (Math.random() * imageSwitchEffects.length)];
    }

    public List<FullArticle> findArticles(String disposition) {
        return core.findFullArticlesByCategory(disposition);
    }

    public List<ArticlesFile> findArticlesFiles(int articleId) {
        return core.findArticlesFiles(articleId);
    }

    public FullArticle getFullArticle(String disposition, int pos) {
        List<FullArticle> a = getModel().getArticles().get(disposition);
        if (a != null && a.size() > pos && pos >= 0) {
            return a.get(pos);
        }
        return null;
    }

//    public ArticlesItem getArticle(String disposition, int pos) {
//        FullArticle a = getFullArticle(disposition, pos);
//        if (a != null) {
//            return a.getArticlesItem();
//        }
//        return null;
//    }

    @Override
    public List<ContentText> getContentTextList(String id) {
        List list = getModel().getArticles().get(id);
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        return list;
    }

    public void setSelectedContentTextById(int id) {
        getModel().setCurrent((FullArticle) core.findFullArticle(id));
        FullArticle c = getModel().getCurrent();
        if(c!=null && c.getDisposition()!=null){
            updateVisit(c.getId());
            loadContentTexts(c.getDisposition().getName());
        }
    }

    public List<ContentText> getContentTextListHead(String id, int max) {
        List<ContentText> list = getContentTextList(id);
        if (list.size() > max) {
            return list.subList(0, max);
        }
        return list;
    }

    @Override
    public String getProperty(String name) {
        return core.getArticlesProperty(name);
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        return core.getArticlesPropertyOrCreate(name,defaultValue);
    }

    @Override
    public CmsTextDisposition getContentDispositionByName(String name) {
        return core.findArticleDisposition(name);
    }

    @Override
    public String getContentDispositionName() {
        return getModel().getDisposition();
    }

    @Override
    public CmsTextDisposition getContentDisposition() {
        return getContentDispositionByName(getContentDispositionName());
    }

    @Override
    public ContentText getSelectedContentText() {
        return getModel().getCurrent();
    }

    @Override
    public boolean isEnabledAction(String action,int id) {
        AppUser currentUser = core.getCurrentUser();
        if(currentUser!=null){
            CorePlugin core = CorePlugin.get();
            ArticlesItem a = core.findArticle(id);
            if(a!=null){
                if(a.getSender()!=null && currentUser.getId()==a.getSender().getId()){
                    return true;
                }
                if(core.isCurrentSessionAdmin()){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onAction(String action, int id) {
        ExternalContext ec = FacesContext.getCurrentInstance()
                .getExternalContext();
        try {
            if("edit".equals(action)) {
                if (ec != null) {
                    Vr.get().redirect(Vr.get().gotoPageObjItem(ArticlesItem.class.getSimpleName(), String.valueOf(id)));
                    return true;
                }
            }else if("delete".equals(action)){
                CorePlugin core = CorePlugin.get();
                ArticlesItem a = core.findArticle(id);
                if(a!=null && !a.isDeleted()){
                    core.remove("ArticlesItem",a.getId());
                    return true;
                }
            }else if("archive".equals(action)){
                CorePlugin core = CorePlugin.get();
                ArticlesItem a = core.findArticle(id);
                if(a!=null && !a.isArchived()){
                    core.archive("ArticlesItem",a);
                    return true;
                }
            }else if("important".equals(action)){
                CorePlugin core = CorePlugin.get();
                ArticlesItem a = core.findArticle(id);
                if(a!=null){
                    a.setImportant(!a.isImportant());
                    core.save("ArticlesItem",a);
                    return true;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
