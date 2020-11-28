/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.inbox.model;

import net.thevpc.app.vainruling.core.service.obj.AppFile;
import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.upa.RelationshipType;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Path("Social")
public class MailboxReceivedAttachment implements AppFile {
    @Id
    @Sequence
    private int id;
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private MailboxReceived parent;

    @Main
    private String name;

    @Properties({
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    @Summary
    private String path;


    private String style;

    private int position;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MailboxReceived getParent() {
        return parent;
    }

    public void setParent(MailboxReceived parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
