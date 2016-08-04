/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.service.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.AccessLevel;
import net.vpc.upa.config.*;
import net.vpc.upa.types.DateTime;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "deleted, archived, position desc, sendTime desc")
@Path("Social")
@Properties(
        //i is a ObjRow!
        @Property(name = UIConstants.Grid.ROW_STYLE,
                value = "(i.object.deleted or i.object.archived or i.object.disposition eq null) ?'vr-row-not-relevant':''")
)
public class ArticlesItem {

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
                    @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private ArticlesDisposition disposition;

    private boolean includeSender;
    private boolean noSubject;

    @Main
    private String subject;

    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.RICHTEXTAREA))
    @Field(max = "max")
    private String content;

    @Properties({
            @Property(name = UIConstants.Form.SEPARATOR, value = "Attatchment"),
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    @Field(max = "1024")
    private String linkURL;

    private String linkText;

    private String linkClassStyle;

    @Properties({
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    private String imageURL;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Flags"))
    @Summary
    private int position;
    @Summary
    private boolean important;

    @Summary
    @Field(persistAccessLevel = AccessLevel.PROTECTED,
            updateAccessLevel = AccessLevel.PROTECTED
    )
    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "SourceAndDestination"))
    private AppUser sender;

    @Summary
    @Field(readAccessLevel = AccessLevel.PROTECTED)
    private String filterExpression;

    @Summary
    private DateTime sendTime;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    private boolean archived;

    @Summary
    private boolean deleted;

    private String deletedBy;

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
}
