/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.tools.ranking;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author vpc
 */
public class RankingTools {

    
    public static String strValOrNull(Object[] arr,int index) {
        if(index<arr.length && arr[index]!=null){
            return String.valueOf(arr[index]).trim();
        }
        return null;
    }
    
    public static int intValOrFF(Object[] arr,int index) {
        if(index<arr.length && arr[index]!=null){
            return Integer.parseInt(String.valueOf(arr[index]).trim());
        }
        return -1;
    }
    
    public static int diffLevenshtein(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }

        /*
           This implementation use two variable to record the previous cost counts,
           So this implementation use less memory than previous impl.
         */

        int n = left.length(); // length of left
        int m = right.length(); // length of right

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            final CharSequence tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.length();
        }

        final int[] p = new int[n + 1];

        // indexes into strings left and right
        int i; // iterates through left
        int j; // iterates through right
        int upperLeft;
        int upper;

        char rightJ; // jth character of right
        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            upperLeft = p[0];
            rightJ = right.charAt(j - 1);
            p[0] = j;

            for (i = 1; i <= n; i++) {
                upper = p[i];
                cost = left.charAt(i - 1) == rightJ ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
                upperLeft = upper;
            }
        }

        return p[n];
    }
    public static File resolveFile(String file){
        return new File(file.replace("${user.home}", System.getProperty("user.home")));
    }
    
    public static <T> T[] sortCopy(Class<T> cls, T[] t, Comparator<T> comp) {
        ArrayList<T> a = new ArrayList<>(Arrays.asList(t));
        a.sort(comp);
        return a.toArray((T[]) Array.newInstance(cls, t.length));
    }

    public static <T> T[] sortCopy(Class<T> cls, List<T> t, Comparator<T> comp) {
        ArrayList<T> a = new ArrayList(t);
        a.sort(comp);
        T[] a1 = (T[]) Array.newInstance(cls, t.size());
        return a.toArray(a1);
    }

    static int asInt(Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        if (o instanceof String) {
            String ss = String.valueOf(o);
            ss = ss.trim();
            if (ss.equals("null")) {
                return 0;
            }
            if (ss.isEmpty()) {
                return 0;
            }
        }
        return Integer.parseInt(String.valueOf(o));
    }

    static double asDouble(Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        if (o instanceof String) {
            String ss = String.valueOf(o);
            ss = ss.trim();
            if (ss.equals("null")) {
                return 0;
            }
            if (ss.isEmpty()) {
                return 0;
            }
        }
        return Double.parseDouble(String.valueOf(o));
    }
    
}
