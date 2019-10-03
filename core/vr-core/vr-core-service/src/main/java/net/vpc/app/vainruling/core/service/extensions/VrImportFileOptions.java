/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.extensions;

/**
 * just option holder, is not used yet!
 *
 * @author taha.bensalah@gmail.com
 */
public class VrImportFileOptions implements Cloneable {

    private int maxDepth = -1;

    public int getMaxDepth() {
        return maxDepth;
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
