/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.integration;

/**
 * just option holder, is not used yet!
 *
 * @author taha.bensalah@gmail.com
 */
public class ImportOptions implements Cloneable {

    private int maxDepth = -1;

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
