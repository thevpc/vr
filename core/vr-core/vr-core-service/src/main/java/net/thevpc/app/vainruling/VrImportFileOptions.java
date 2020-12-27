/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling;

/**
 * just option holder, is not used yet!
 *
 * @author taha.bensalah@gmail.com
 */
public class VrImportFileOptions implements Cloneable {

    private int maxDepth = -1;
    private String fileTypeName;

    public int getMaxDepth() {
        return maxDepth;
    }

    public String getFileTypeName() {
        return fileTypeName;
    }

    public VrImportFileOptions setFileTypeName(String fileTypeName) {
        this.fileTypeName = fileTypeName;
        return this;
    }
    

    public VrImportFileOptions setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public VrImportFileOptions copy() {
        try {
            return (VrImportFileOptions) clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
