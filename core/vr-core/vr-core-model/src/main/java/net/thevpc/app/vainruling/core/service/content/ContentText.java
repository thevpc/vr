package net.thevpc.app.vainruling.core.service.content;

import net.thevpc.app.vainruling.core.service.model.strict.AppUserStrict;

import java.util.Date;
import java.util.List;

/**
 * Created by vpc on 9/5/16.
 */
public interface ContentText {
    int getId();

    String getCategory();

    String getDecoration();
    
    String getRecipients();

    String getSubject();

    String getSubTitle();

    String getContent();

    String getImageURL();

    AppUserStrict getUser();

    List<ContentPath> getAttachments();


    List<ContentPath> getImageAttachments();

    List<ContentPath> getNonImageAttachments();

    String getLinkClassStyle();

    boolean isImportant();

    boolean isNoSubject();

    String getLinkText();

    String getLinkURL();

    Date getPublishTime();

    int getVisitCount();
}
