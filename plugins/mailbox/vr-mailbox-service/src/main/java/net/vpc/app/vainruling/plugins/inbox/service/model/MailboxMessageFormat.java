/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inbox.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("/Social/Config")
public class MailboxMessageFormat {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN, UserFieldModifier.UNIQUE})
    private String name;
    @Property(name = UIConstants.FIELD_FORM_SPAN, value = "MAX_VALUE")
    private boolean preferFormattedText;
    @Field(max = "1024")
    private String subject;
    @Field(max = "20000")
    @Property(name = UIConstants.FIELD_FORM_CONTROL,value = UIConstants.ControlType.TEXTAREA)
    private String plainBody;
    @Field(max = "30000")
    @Property(name = UIConstants.FIELD_FORM_CONTROL,value = UIConstants.ControlType.TEXTAREA)
    private String formattedBody;
    @Field(max = "512")
    @Properties({@Property(name = UIConstants.FIELD_FORM_CONTROL,value = UIConstants.ControlType.FILE),
            @Property(name = UIConstants.FIELD_FORM_SPAN, value = "MAX_VALUE")})
    private String footerEmbeddedImage;

    public MailboxMessageFormat() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPreferFormattedText() {
        return preferFormattedText;
    }

    public void setPreferFormattedText(boolean preferFormattedText) {
        this.preferFormattedText = preferFormattedText;
    }

    public String getPlainBody() {
        return plainBody;
    }

    public void setPlainBody(String plainBody) {
        this.plainBody = plainBody;
    }

    public String getFormattedBody() {
        return formattedBody;
    }

    public void setFormattedBody(String formattedBody) {
        this.formattedBody = formattedBody;
    }

    public String getFooterEmbeddedImage() {
        return footerEmbeddedImage;
    }

    public void setFooterEmbeddedImage(String footerEmbeddedImage) {
        this.footerEmbeddedImage = footerEmbeddedImage;
    }

}
