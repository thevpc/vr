/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model.content;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.AccessLevel;
import net.vpc.upa.ProtectionLevel;
import net.vpc.upa.config.*;
import net.vpc.upa.types.DateTime;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.deleted, this.archived, this.position desc, this.sendTime desc")
@Path("Social")
@Properties(
        {
                //i is a ObjRow!
                @Property(name = UIConstants.Grid.ROW_STYLE,
                        value = "(i.object.deleted or i.object.archived) ?'vr-row-deleted':(i.object.disposition eq null) ?'vr-row-invalid':(i.object.important) ?'vr-row-important' : ''"),
                @Property(name = "ui.auto-filter.dispositionGroup", value = "{expr='this.dispositionGroup',order=1}"),
                @Property(name = "ui.auto-filter.disposition", value = "{expr='this.disposition',order=2}"),
                @Property(name = "ui.auto-filter.sender", value = "{expr='this.sender',order=3}")
        }
)
public class ArticlesItem {

    @Path("Main")
    @Id
    @Sequence
    private int id;

    @Summary
    @Properties(
            {
                    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.PROFILE_EXPRESSION)
                    , @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private String recipientProfiles;


    @Summary
    @Properties(
            {
                    @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE"),
                    @Property(name = UIConstants.Grid.COLUMN_STYLE_CLASS, value = "#{" +
                            " hashToStringArr(this.disposition.name,'vr-label-bg01','vr-label-bg02','vr-label-bg03','vr-label-bg04','vr-label-bg05','vr-label-bg06','vr-label-bg07','vr-label-bg08','vr-label-bg09','vr-label-bg10','vr-label-bg11','vr-label-bg12','vr-label-bg13','vr-label-bg14','vr-label-bg15','vr-label-bg16','vr-label-bg17','vr-label-bg18','vr-label-bg19','vr-label-bg20')" +
                            "}")
            }
    )
    private ArticlesDisposition disposition;

    @Field(readProtectionLevel = ProtectionLevel.PROTECTED,defaultValue = "true")
    private boolean includeSender=true;
    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private boolean noSubject;

    @Summary
    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private ArticlesDispositionGroup dispositionGroup;
    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private String decoration;

    @Main
    private String subject;
    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private String subTitle;


    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.RICHTEXTAREA))
    @Field(max = "max")
    private String content;

    @Path("Attachment")
    @Properties({
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Attachment"),
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    @Field(max = "1024")
    private String linkURL;

    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private String linkText;

    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private String linkClassStyle;

    @Properties({
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private String imageURL;

    @Path("Flags")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Flags"))
    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private int position;

    private boolean important;

    @Summary
    @Path("SourceAndDestination")
    @Field(protectionLevel = ProtectionLevel.PROTECTED)
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "SourceAndDestination"))
    private AppUser sender;

    @Summary
    @Field(protectionLevel = ProtectionLevel.PROTECTED)
    @Properties(
            {
//                    @Property(name = UIConstants.Form.SEPARATOR, value = "SourceAndDestination"),
                    @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")

            }
    )
    private int visitCount;

    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private String filterExpression;

    @Summary
    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private DateTime sendTime;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    private boolean archived;

    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private boolean deleted;

    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private String deletedBy;

    @Field(readProtectionLevel = ProtectionLevel.PROTECTED)
    private Timestamp deletedOn;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public AppUser getSender() {
        return sender;
    }

    public void setSender(AppUser sender) {
        this.sender = sender;
    }

    public String getRecipientProfiles() {
        return recipientProfiles;
    }

    public void setRecipientProfiles(String recipientProfiles) {
        this.recipientProfiles = recipientProfiles;
    }

    public DateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(DateTime sendTime) {
        this.sendTime = sendTime;
    }

    public ArticlesDisposition getDisposition() {
        return disposition;
    }

    public void setDisposition(ArticlesDisposition disposition) {
        this.disposition = disposition;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Timestamp getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Timestamp deletedOn) {
        this.deletedOn = deletedOn;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getLinkClassStyle() {
        return linkClassStyle;
    }

    public void setLinkClassStyle(String linkClassStyle) {
        this.linkClassStyle = linkClassStyle;
    }

    public String getFilterExpression() {
        return filterExpression;
    }

    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
    }

    public boolean isIncludeSender() {
        return includeSender;
    }

    public void setIncludeSender(boolean includeSender) {
        this.includeSender = includeSender;
    }

    public boolean isNoSubject() {
        return noSubject;
    }

    public void setNoSubject(boolean noSubject) {
        this.noSubject = noSubject;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArticlesItem that = (ArticlesItem) o;

        if (id != that.id) return false;
        if (includeSender != that.includeSender) return false;
        if (noSubject != that.noSubject) return false;
        if (position != that.position) return false;
        if (important != that.important) return false;
        if (visitCount != that.visitCount) return false;
        if (archived != that.archived) return false;
        if (deleted != that.deleted) return false;
        if (recipientProfiles != null ? !recipientProfiles.equals(that.recipientProfiles) : that.recipientProfiles != null)
            return false;
        if (disposition != null ? !disposition.equals(that.disposition) : that.disposition != null) return false;
        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (linkURL != null ? !linkURL.equals(that.linkURL) : that.linkURL != null) return false;
        if (decoration != null ? !decoration.equals(that.decoration) : that.decoration != null) return false;
        if (linkText != null ? !linkText.equals(that.linkText) : that.linkText != null) return false;
        if (linkClassStyle != null ? !linkClassStyle.equals(that.linkClassStyle) : that.linkClassStyle != null)
            return false;
        if (imageURL != null ? !imageURL.equals(that.imageURL) : that.imageURL != null) return false;
        if (sender != null ? !sender.equals(that.sender) : that.sender != null) return false;
        if (filterExpression != null ? !filterExpression.equals(that.filterExpression) : that.filterExpression != null)
            return false;
        if (sendTime != null ? !sendTime.equals(that.sendTime) : that.sendTime != null) return false;
        if (deletedBy != null ? !deletedBy.equals(that.deletedBy) : that.deletedBy != null) return false;
        return deletedOn != null ? deletedOn.equals(that.deletedOn) : that.deletedOn == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (recipientProfiles != null ? recipientProfiles.hashCode() : 0);
        result = 31 * result + (disposition != null ? disposition.hashCode() : 0);
        result = 31 * result + (includeSender ? 1 : 0);
        result = 31 * result + (noSubject ? 1 : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (linkURL != null ? linkURL.hashCode() : 0);
        result = 31 * result + (linkText != null ? linkText.hashCode() : 0);
        result = 31 * result + (decoration != null ? decoration.hashCode() : 0);
        result = 31 * result + (linkClassStyle != null ? linkClassStyle.hashCode() : 0);
        result = 31 * result + (imageURL != null ? imageURL.hashCode() : 0);
        result = 31 * result + position;
        result = 31 * result + (important ? 1 : 0);
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
        result = 31 * result + visitCount;
        result = 31 * result + (filterExpression != null ? filterExpression.hashCode() : 0);
        result = 31 * result + (sendTime != null ? sendTime.hashCode() : 0);
        result = 31 * result + (archived ? 1 : 0);
        result = 31 * result + (deleted ? 1 : 0);
        result = 31 * result + (deletedBy != null ? deletedBy.hashCode() : 0);
        result = 31 * result + (deletedOn != null ? deletedOn.hashCode() : 0);
        return result;
    }

    public ArticlesDispositionGroup getDispositionGroup() {
        return dispositionGroup;
    }

    public void setDispositionGroup(ArticlesDispositionGroup dispositionGroup) {
        this.dispositionGroup = dispositionGroup;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
