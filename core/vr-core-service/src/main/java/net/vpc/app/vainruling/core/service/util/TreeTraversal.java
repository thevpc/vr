/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import java.util.List;
import java.util.Stack;

/**
 * @author taha.bensalah@gmail.com
 */
public class TreeTraversal {

    public static <T> void preOrderTreeTraversal(TreeDefinition<T> tree, TreeVisitor<T> vis) {
        Stack<T> s = new Stack<>();
        s.push(tree.getRoot());
        while (!s.empty()) {
            T n = s.pop();
            vis.visit(n, tree);
            List<T> children = tree.getChildren(n);
            if (children != null) {
                for (T cc : children) {
                    s.push(cc);
                }
            }
        }
    }

    public static <T> void postOrderTreeTraversal(TreeDefinition<T> tree, TreeVisitor<T> vis) {
        postOrderTreeTraversal(tree.getRoot(), null, 0, tree, vis);
    }

//    private static <T> void postOrderTreeTraversal(T node, T parent, int index, TreeDefinition<T> tree,TreeVisitor<T> vis) {
//        if (node == null) {
//            return;
//        }
//        System.out.println("postOrderTreeTraversal "+node+" :: "+parent+" :: "+index);
//        List<T> nchildren = tree.getChildren(node);
//        if (nchildren.size() > 0) {
//            postOrderTreeTraversal(nchildren.get(0), node, 0, tree,vis);
//        }
//        vis.visit(node,tree);
//        if (parent != null) {
//            List<T> pchildren = tree.getChildren(parent);
//            if (pchildren.size() > index + 1) {
//                postOrderTreeTraversal(pchildren.get(index + 1), parent, index + 1, tree,vis);
//            }
//        }
//    }

    private static <T> void postOrderTreeTraversal(T node, T parent, int index, TreeDefinition<T> tree, TreeVisitor<T> vis) {
        if (node == null) {
            return;
        }
//        System.out.println("postOrderTreeTraversal "+node+" :: "+parent+" :: "+index);
        List<T> nchildren = tree.getChildren(node);

        for (int i = 0; i < nchildren.size(); i++) {
            postOrderTreeTraversal(nchildren.get(i), node, i, tree, vis);
        }
        vis.visit(node, tree);
//        if (parent != null) {
//            List<T> pchildren = tree.getChildren(parent);
//            if (pchildren.size() > index + 1) {
//                postOrderTreeTraversal(pchildren.get(index + 1), parent, index + 1, tree,vis);
//            }
//        }
    }


}
