package net.vpc.app.vainruling.core.service.content;

import net.vpc.app.vainruling.core.service.model.AppUser;

import java.util.Date;
import java.util.List;

/**
 * Created by vpc on 9/5/16.
 */
public interface ContentText {
    public int getId();

    public String getSubject();

    public String getContent();

    public String getImageURL();

    public AppUser getUser();

    public List<ContentPath> getAttachments();

    public void setAttachments(List<ContentPath> attachments);

    public List<ContentPath> getImageAttachments();

    public List<ContentPath> getNonImageAttachments();

    public String getLinkClassStyle();

    public boolean isImportant();

    public boolean isNoSubject();

    public String getLinkText();

    public String getLinkURL();

    public Date getPublishTime();

    public int getVisitCount();
}
