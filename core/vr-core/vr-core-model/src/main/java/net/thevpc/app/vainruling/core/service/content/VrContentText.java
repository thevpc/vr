package net.thevpc.app.vainruling.core.service.content;

import net.thevpc.app.vainruling.core.service.model.strict.AppUserStrict;

import java.util.Date;
import java.util.List;

/**
 * Created by vpc on 9/5/16.
 */
public interface VrContentText {

    int getId();

    String[] getCategories();

    String getDecoration();

    String getRecipients();

    String getTitle();

    String getSubTitle();

    String getContent();

    AppUserStrict getUser();

    VrContentPath getMainPath();

    List<VrContentPath> getAttachments();

    boolean isHasImageAttachments();

    boolean isHasNonImageAttachments();

    boolean isImportant();

    boolean isNoTitle();

    boolean isDeleted();

    boolean isArchived();

    Date getPublishTime();

    int getVisitCount();
}
