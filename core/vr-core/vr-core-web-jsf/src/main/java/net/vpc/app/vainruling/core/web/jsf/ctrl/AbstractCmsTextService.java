/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.content.CmsTextDisposition;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.content.FullArticle;
import net.vpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import net.vpc.app.vainruling.VrCmsTextService;

/**
 * @author vpc
 */
public abstract class AbstractCmsTextService implements VrCmsTextService {

    @Autowired
    protected CorePlugin core;
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

    public abstract AbstractCmsModel getModel();

    public void refresh() {
    }

    @Override
    public void loadContentTexts(String disposition) {
        List<FullArticle> a = findArticles(disposition);
        getModel().setDisposition(disposition);
        getModel().getArticles().put(disposition, a);
        if (getModel().getCurrent() == null) {
            if (a != null && a.size() > 0) {
                if (a.get(0) != null) {
                    updateVisit(a.get(0).getId());
                }
                setSelectedContentTextById(a.get(0).getDisposition().getName(), a.get(0).getId());
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

    public FullArticle getFullArticle(String disposition, int pos) {
        List<FullArticle> a = getModel().getArticles().get(disposition);
        if (a != null && a.size() > pos && pos >= 0) {
            return a.get(pos);
        }
        return null;
    }

    //    public AppArticle getArticle(String disposition, int pos) {
//        FullArticle a = getFullArticle(disposition, pos);
//        if (a != null) {
//            return a.getArticle();
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

    @Override
    public void setSelectedContentTextById(String disposition, int id) {
        getModel().setCurrent(findArticle(id));
        FullArticle c = getModel().getCurrent();
        if (c != null && c.getDisposition() != null) {
            updateVisit(c.getId());
            loadContentTexts(c.getDisposition().getName());
        }
    }

    @Override
    public List<ContentText> getContentTextListHead(String id, int max) {
        List<ContentText> list = getContentTextList(id);
        if (list.size() > max) {
            return list.subList(0, max);
        }
        return list;
    }

    @Override
    public CmsTextDisposition getContentDisposition() {
        return getContentDispositionByName(getModel().getDisposition());
    }

    @Override
    public ContentText getSelectedContentText(String name) {
        return getModel().getCurrent();
    }

    @Override
    public boolean isEnabledActionById(String action, int id) {
        return isEnabledAction(action,findArticle(id));
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

    @Override
    public boolean isDispositionEnabled(String disposition) {
        CmsTextDisposition articleDisposition = getContentDispositionByName(disposition);
        if (articleDisposition != null) {
            return articleDisposition.isEnabled();
        }
        return false;
    }

    @Override
    public String getDispositionActionName(String disposition) {
        CmsTextDisposition articleDisposition = getContentDispositionByName(disposition);
        String actionName = null;
        if (articleDisposition != null) {
            actionName = articleDisposition.getActionName();
        }
        if (StringUtils.isBlank(actionName)) {
            actionName = "$$" + disposition;
        }
        return actionName;
    }

    protected FullArticle findArticle(int id) {
        for (List<FullArticle> fullArticles : getModel().getArticles().values()) {
            for (FullArticle fullArticle : fullArticles) {
                if (fullArticle.getId() == id) {
                    return fullArticle;
                }
            }
        }
        return null;
    }

    protected abstract List<FullArticle> findArticles(String disposition);

    public abstract void updateVisit(int articleId);
}
