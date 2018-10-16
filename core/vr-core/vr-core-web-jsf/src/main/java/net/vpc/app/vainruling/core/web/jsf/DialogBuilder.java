package net.vpc.app.vainruling.core.web.jsf;

import net.vpc.app.vainruling.core.web.obj.DialogResult;
import org.primefaces.PrimeFaces;

import java.util.HashMap;
import java.util.Map;
import javax.faces.context.FacesContext;
import net.vpc.common.jsf.FacesUtils;
import org.primefaces.util.Constants;

public class DialogBuilder {

    private boolean resizable = true;
    private boolean draggable = true;
    private boolean modal = true;
    private String contentHeight;
    private String contentWidth;
    private String height;
    private String width;
    private String url;

    public DialogBuilder(String url) {
        this.url = url;
    }

    public String getContentHeight() {
        return contentHeight;
    }

    public DialogBuilder setContentHeight(String contentHeight) {
        this.contentHeight = contentHeight;
        return this;
    }

    public String getHeight() {
        return height;
    }

    public DialogBuilder setHeight(int height) {
        return setHeight(String.valueOf(height));
    }

    public DialogBuilder setWidth(int width) {
        return setWidth(String.valueOf(width));
    }

    public DialogBuilder setContentHeight(int height) {
        return setContentHeight(String.valueOf(height));
    }

    public DialogBuilder setContentWidth(int width) {
        return setContentWidth(String.valueOf(width));
    }

    public String getContentWidth() {
        return contentWidth;
    }

    public DialogBuilder setContentWidth(String contentWidth) {
        this.contentWidth = contentWidth;
        return this;
    }

    public DialogBuilder setHeight(String height) {
        this.height = height;
        return this;
    }

    public String getWidth() {
        return width;
    }

    public DialogBuilder setWidth(String width) {
        this.width = width;
        return this;
    }

    public boolean isResizable() {
        return resizable;
    }

    public DialogBuilder setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public DialogBuilder setDraggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    public boolean isModal() {
        return modal;
    }

    public DialogBuilder setModal(boolean modal) {
        this.modal = modal;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DialogBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public void open() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", resizable);
        options.put("draggable", draggable);
        options.put("modal", modal);
        if (height != null) {
            options.put("height", height);
        }
        if (width != null) {
            options.put("width", width);
        }
        if (contentHeight != null) {
            options.put("contentHeight", contentHeight);
        } else {
//            options.put("contentHeight", "100%");
        }
        if (contentWidth != null) {
            options.put("contentWidth", contentWidth);
        } else {
//            options.put("contentHeight", "100%");
        }
        PrimeFaces.current().dialog().openDynamic(url, options, null);
    }

    public static void closeCurrent() {
        // disable bug in Primefaces
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
////        Map<String, Object> session = facesContext.getExternalContext().getSessionMap();
//        if(params.get(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM)==null){
//            FacesUtils.getHttpRequest().getP.(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM,"DUMMY_CONVERSATION_PARAM");
////            params.put(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM,"DUMMY_CONVERSATION_PARAM");
//        }
        //end disable primefaces bug
        PrimeFaces.current().dialog().closeDynamic(null);
//        PrimeFaces.current().dialog().closeDynamic(new DialogResult(null, null));
    }
}
