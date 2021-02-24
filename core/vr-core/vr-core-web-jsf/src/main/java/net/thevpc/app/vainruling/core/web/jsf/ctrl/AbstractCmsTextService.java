/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.VrCmsTextService;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.content.CmsTextDisposition;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import net.thevpc.app.vainruling.core.service.content.VrContentText;

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
        List<VrContentText> a = findArticles(disposition, null);
        getModel().setDisposition(disposition);
        getModel().getArticles().put(disposition, a);
        if (getModel().getCurrent() == null) {
            if (a != null && a.size() > 0) {
                if (a.get(0) != null) {
                    updateVisit(a.get(0).getId());
                }
                setSelectedContentTextById(a.get(0).getContent(), a.get(0).getId());
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

    public VrContentText getFullArticle(String disposition, int pos) {
        List<VrContentText> a = getModel().getArticles().get(disposition);
        if (a != null && a.size() > pos && pos >= 0) {
            return a.get(pos);
        }
        return null;
    }

    //    public AppArticle getArticle(String disposition, int pos) {
//        VrFullArticle a = getFullArticle(disposition, pos);
//        if (a != null) {
//            return a.getArticle();
//        }
//        return null;
//    }
    @Override
    public List<VrContentText> getContentTextList(String id) {
        List list = getModel().getArticles().get(id);
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        return list;
    }

    @Override
    public void setSelectedContentTextById(String disposition, int id) {
        getModel().setCurrent(findArticle(id));
        VrContentText c = getModel().getCurrent();
        if (c.getCategories().length > 0) {
            updateVisit(c.getId());
            loadContentTexts(c.getCategories()[0]);
        }
    }

    @Override
    public List<VrContentText> getContentTextListHead(String id, int max) {
        List<VrContentText> list = getContentTextList(id);
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
    public VrContentText getSelectedContentText(String name) {
        return getModel().getCurrent();
    }

    @Override
    public boolean isEnabledActionById(String action, int id) {
        return isEnabledAction(action, findArticle(id));
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

    protected VrContentText findArticle(int id) {
        for (List<VrContentText> fullArticles : getModel().getArticles().values()) {
            for (VrContentText fullArticle : fullArticles) {
                if (fullArticle.getId() == id) {
                    return fullArticle;
                }
            }
        }
        return null;
    }

    protected abstract List<VrContentText> findArticles(String disposition, net.thevpc.app.vainruling.core.service.model.content.VrContentTextConfig config);

    public abstract void updateVisit(int articleId);

    @Override
    public void runAction(String action, VrContentText a) {
        onAction(action, a);
    }

}
