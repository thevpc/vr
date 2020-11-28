/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.inbox.service.dto;

import net.thevpc.gomail.RecipientType;

import java.util.Properties;

/**
 * @author taha.bensalah@gmail.com
 */
public class MailData {

    private String subject;
    private String body;
    private String from;
    private String to;
    private String toFilter;
    private String category;
    private RecipientType emailType;
    private Integer templateId;
    private boolean external;
    private boolean richText;
    private Properties properties = new Properties();

    public boolean isRichText() {
        return richText;
    }

    public void setRichText(boolean richText) {
        this.richText = richText;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public RecipientType getEmailType() {
        return emailType;
    }

    public void setEmailType(RecipientType emailType) {
        this.emailType = emailType;
    }

    public String getToFilter() {
        return toFilter;
    }

    public void setToFilter(String toFilter) {
        this.toFilter = toFilter;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}
