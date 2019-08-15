package net.vpc.app.vainruling.plugins.inbox.service.dto;

import net.vpc.common.gomail.RecipientType;

/**
 * Created by vpc on 10/25/16.
 */
public class SendExternalMailConfig {

    private RecipientType emailType;
    private Integer templateId;

    public RecipientType getEmailType() {
        return emailType;
    }

    public void setEmailType(RecipientType emailType) {
        this.emailType = emailType;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

}
