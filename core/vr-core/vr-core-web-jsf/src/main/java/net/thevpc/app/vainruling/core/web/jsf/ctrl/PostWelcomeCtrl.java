/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.content.AppArticle;
import net.thevpc.app.vainruling.core.service.model.content.AppArticleDisposition;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import net.thevpc.app.vainruling.core.web.jsf.VrJsf;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.vfs.VFile;
import net.thevpc.upa.UPA;
import net.thevpc.upa.types.DateTime;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author vpc
 */
@ManagedBean
@SessionScoped
public class PostWelcomeCtrl {

    private Model model = new Model();

    @PostConstruct
    public void init() {
        getModel().setAllowed(CorePlugin.get().isCurrentSessionAdminOrProfile("Publisher"));
        getModel().setDate(new Date());
        refreshFiles();
    }

    public void switchVisible() {
        cancelEdit();
        getModel().setVisible(true);
    }

    public void cancelEdit() {
        getModel().setContent("");
        getModel().setRecipients("");
        getModel().setTitle("");
        getModel().setSubTitle("");
        getModel().setVisible(false);
        getModel().setDate(new Date());
    }

    public void post() {
        try {
            refreshFiles();
            CorePlugin core = CorePlugin.get();
            AppArticleDisposition d = core.findArticleDisposition("Welcome");
            if (d != null && !StringUtils.isBlank(getModel().getTitle())
                    && !StringUtils.isBlank(getModel().getContent())) {
                AppArticle a = new AppArticle();
                a.setSendTime(new DateTime());
                a.setSubject(getModel().getTitle());
                a.setSubTitle(getModel().getSubTitle());
                a.setContent(getModel().getContent());
                a.setDisposition(d);
                //"danger"
                a.setDecoration(Vr.get().rand("sucess", "warning", "info", "default", "primary"));
                a.setRecipientProfiles(getModel().getRecipients());
                a.setIncludeSender(true);
                if(!getModel().getFiles().isEmpty()){
                    a.setLinkURL(getUploadsFolder().getBaseFile("vrfs").getPath());
                }
                a.setSender(core.getCurrentUser());
                UPA.getPersistenceUnit().persist(a);
                cancelEdit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public VFile getUploadsFolder() {
        return CorePlugin.get().getMyHomeFileSystem().get(getUploadsFolderPath());
    }
    
    public String getUploadsFolderPath() {
        return "Uploads/WelcomePosts/" + new SimpleDateFormat("yyyyMMdd-HHmmssSSS").format(getModel().getDate());
    }

    public void resetFiles() {
        for (VFile file : getUploadsFolder().listFiles(x -> !x.getName().startsWith("."))) {
            try {
                file.delete();
            } catch (IOException ex) {
                Logger.getLogger(PostWelcomeCtrl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void refreshFiles() {
        List<VFile> files = getModel().getFiles();
        files.clear();
        for (VFile file : getUploadsFolder().listFiles(x -> !x.getName().startsWith("."))) {
            files.add(file);
        }
        files.sort(new Comparator<VFile>() {
            @Override
            public int compare(VFile o1, VFile o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public void onHandleFileUpload(FileUploadEvent event) {
        try {
            VFile file = VrJsf.handleFileUpload(event, getUploadsFolderPath()+"/"+event.getFile().getFileName(), true, true);
            //PrimeFaces.current().ajax().update("myform:pathComp");
            refreshFiles();
        } catch (Exception ex) {
            FacesUtils.addErrorMessage(ex);
        }
    }

    public Model getModel() {
        return model;
    }

    public static class Model {

        private String recipients;
        private String title;
        private String subTitle;
        private String content;
        private Date date;
        private boolean visible;
        private boolean allowed;
        private List<VFile> files = new ArrayList<>();

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public boolean isAllowed() {
            return allowed;
        }

        public void setAllowed(boolean allowed) {
            this.allowed = allowed;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public List<VFile> getFiles() {
            return files;
        }

        public void setFiles(List<VFile> files) {
            this.files = files;
        }

        public String getRecipients() {
            return recipients;
        }

        public void setRecipients(String recipients) {
            this.recipients = recipients;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }
}
