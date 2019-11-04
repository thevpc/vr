/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inbox.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("/Repository/Social")
public class MailboxMessageFormat {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    @Unique
    private String name;
    @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    private boolean preferFormattedText;
    @Field(max = "1024")
    private String subject;
    @Field(max = "max")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String plainBody;
    @Field(max = "max")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String formattedBody;
    @Field(max = "512")
    @Properties({@Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")})
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
