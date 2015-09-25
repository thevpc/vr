/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.util;

import java.util.List;
import java.util.Stack;

/**
 *
 * @author vpc
 */
public class TreeTraversal {

    public static <T> void preOrderTreeTraversal(TreeDefinition<T> tree, TreeVisitor<T> vis) {
        Stack<T> s = new Stack<>();
        s.push(tree.getRoot());
        while (!s.empty()) {
            T n = s.pop();
            vis.visit(n,tree);
            List<T> children = tree.getChildren(n);
            if (children != null) {
                for (T cc : children) {
                    s.push(cc);
                }
            }
        }
    }

    public static <T> void postOrderTreeTraversal(TreeDefinition<T> tree,TreeVisitor<T> vis) {
        postOrderTreeTraversal(tree.getRoot(), null, 0, tree,vis);
    }

    private static <T> void postOrderTreeTraversal(T n, T parent, int index, TreeDefinition<T> tree,TreeVisitor<T> vis) {
        if (n == null) {
            return;
        }
        List<T> nchildren = tree.getChildren(n);
        if (nchildren.size() > 0) {
            postOrderTreeTraversal(nchildren.get(0), n, 0, tree,vis);
        }
        vis.visit(n,tree);
        if (parent != null) {
            List<T> pchildren = tree.getChildren(parent);
            if (pchildren.size() > index + 1) {
                postOrderTreeTraversal(pchildren.get(index + 1), parent, index + 1, tree,vis);
            }
        }
    }


}
