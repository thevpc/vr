package net.thevpc.app.vainruling.core.service.util;

/**
 * Created by vpc on 9/10/16.
 */
public class DiffHtmlStyle {
    private boolean fullPage=true;
    private String divClass;
    private String lineClass;
    private String insertedClass;
    private String deletedClass;

    public boolean isFullPage() {
        return fullPage;
    }

    public DiffHtmlStyle setFullPage(boolean fullPage) {
        this.fullPage = fullPage;
        return this;
    }

    public String getDivClass() {
        return divClass;
    }

    public DiffHtmlStyle setDivClass(String divClass) {
        this.divClass = divClass;
        return this;
    }

    public String getLineClass() {
        return lineClass;
    }

    public DiffHtmlStyle setLineClass(String lineClass) {
        this.lineClass = lineClass;
        return this;
    }

    public String getInsertedClass() {
        return insertedClass;
    }

    public DiffHtmlStyle setInsertedClass(String insertedClass) {
        this.insertedClass = insertedClass;
        return this;
    }

    public String getDeletedClass() {
        return deletedClass;
    }

    public DiffHtmlStyle setDeletedClass(String deletedClass) {
        this.deletedClass = deletedClass;
        return this;
    }

    public DiffHtmlStyle copy(){
        return new DiffHtmlStyle()
                .setInsertedClass(insertedClass)
                .setDeletedClass(deletedClass)
                .setDivClass(divClass)
                .setLineClass(lineClass)
                .setFullPage(fullPage)
                ;
    }

}
