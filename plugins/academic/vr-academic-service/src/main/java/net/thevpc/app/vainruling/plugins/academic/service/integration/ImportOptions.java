/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.integration;

/**
 * just option holder, is not used yet!
 *
 * @author taha.bensalah@gmail.com
 */
public class ImportOptions implements Cloneable {

    private int maxDepth = -1;
    
    private String fileTypeName;

    public String getFileTypeName() {
        return fileTypeName;
    }

    public ImportOptions setContentTypeName(String contentTypeName) {
        this.fileTypeName = contentTypeName;
        return this;
    }

    
    public int getMaxDepth() {
        return maxDepth;
    }

    public ImportOptions setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public ImportOptions copy() {
        try {
            return (ImportOptions) clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
