/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.editor;

import net.thevpc.upa.Document;

/**
 * @author taha.bensalah@gmail.com
 */
public class EditorRow {

    private boolean read;
    private boolean write;
    private boolean selected;
    private boolean selectable = true;
    private Document document;
    private Object object;
    private int rowPos;
    private String strId;

    public EditorRow(String strId,Document value, Object obj) {
        this.strId = strId;
        this.document = value;
        this.object = obj;
    }

    public String getStrId() {
        return strId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public int getRowPos() {
        return rowPos;
    }

    public void setRowPos(int rowPos) {
        this.rowPos = rowPos;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}
