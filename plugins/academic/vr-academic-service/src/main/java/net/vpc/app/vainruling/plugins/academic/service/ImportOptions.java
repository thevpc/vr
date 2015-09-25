/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

/**
 * just option holder, is not used yet!
 *
 * @author vpc
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
