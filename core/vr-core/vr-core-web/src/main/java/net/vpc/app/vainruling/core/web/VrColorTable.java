package net.vpc.app.vainruling.core.web;

import org.springframework.stereotype.Service;


/**
 * Created by vpc on 7/3/16.
 */
@Service
public class VrColorTable {
    public final String[] bgcolors = new String[]{
            "#DDE6CB",
            "#C0F7BA",
            "aliceblue",
            "#E6E7F9",
            "#EAD3F9",
            "#FBFFBE",
            "bisque",
            "beige",
            "#FDD5E0",
            "peachpuff",
            "lightcyan",
            "mistyrose",
            "lightgoldenrodyellow"
    };
    public final String[] fgcolors = new String[]{
            "mediumseagreen",
            "#3FB3B3",
            "#B563FF",
            "darkorange",
            "darksalmon",
            "deeppink"
    };

    public int pos(int pos) {
        return Math.abs(pos) % 20 + 1;
    }

    public int pos(String val) {
        return pos(val.hashCode());
    }

    public String getBgColor(int pos) {
        return getColor(pos, bgcolors);
    }

    public String getBgColor(String val) {
        return getColor(val, bgcolors);
    }

    public String getFgColor(int pos) {
        return getColor(pos, fgcolors);
    }

    public String getFgColor(String val) {
        return getColor(val, fgcolors);
    }

    private String getColor(int pos, String[] arr) {
        return arr[Math.abs(pos) % arr.length];
    }

    private String getColor(String val, String[] arr) {
        return getColor(val.hashCode(), arr);
    }
}
