/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import com.google.gson.Gson;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.wiki.VrWikiParser;
import net.vpc.common.io.IOUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.CustomDefaultObject;
import net.vpc.upa.filters.ObjectFilter;
import net.vpc.upa.types.DateTime;
import net.vpc.upa.types.StringType;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrUtils {

    public static final CustomDefaultObject DEFAULT_OBJECT_CURRENT_USER = new CustomDefaultObject() {

        @Override
        public Object getObject() {
            return UserSession.getCurrentUser();
        }
    };
    public static final CustomDefaultObject DEFAULT_OBJECT_CURRENT_DATETIME = new CustomDefaultObject() {

        @Override
        public Object getObject() {
            return new DateTime();
        }
    };
    public static final CustomDefaultObject DEFAULT_OBJECT_CURRENT_DATEONLY = new CustomDefaultObject() {

        @Override
        public Object getObject() {
            return new net.vpc.upa.types.Date();
        }
    };
    private static final DecimalFormat FILE_SIZE_FORMAT = new DecimalFormat("0.0");

    public static String text2html(String html) {
        return StringEscapeUtils.escapeHtml(html == null ? "" : html);
    }

    public static String html2text(String html) {
        return Jsoup.parse(html == null ? "" : html).text();
    }

    public static String toValidFileName(String s) {
        if (StringUtils.isEmpty(s)) {
            s = "EMPTY_NAME";
        }
        s = StringUtils.normalize(s);
        char[] ca = s.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            char c = ca[i];
            if ("()[]*?/\\:<>&".indexOf(c) >= 0) {
                ca[i] = '_';
            }
        }
        return new String(ca);
    }

    public static String formatFileSize(Number value) {
        final int KO = 1024;
        final int MO = KO * KO;
        final int GO = KO * MO;
        final int TO = KO * GO;
        if (value == null) {
            return "";
        }
        long b = value.longValue();
        if (b < 0) {
            return "??" + b;
        }
        if (b == 0) {
            return "0";
        }
        if (b < KO) {
            return b + " b";
        }
        if (b < (MO)) {
            return FILE_SIZE_FORMAT.format((((double) b) / KO)) + " Kb";
        }
        if (b < (GO)) {
            return FILE_SIZE_FORMAT.format((((double) b) / MO)) + " Mb";
        }
        if (b < (TO)) {
            return FILE_SIZE_FORMAT.format((((double) b) / GO)) + " Gb";
        }
        return FILE_SIZE_FORMAT.format((((double) b) / TO)) + " Tb";
    }

    public static String extractPureHTML(String html) {
        if (html == null) {
            html = "";
        }
        if (html.startsWith("<")) {
            Document d = Jsoup.parse(html);
            for (Element e : d.select("font")) {
                if (e.childNodeSize() > 0) {
                    try {
                        e.replaceWith(e.childNode(0));
                    } catch (java.lang.IndexOutOfBoundsException ex) {
                        //why
                        System.out.print("");
                    }
                } else {
                    e.remove();
                }
            }
            for (Element e : d.select("span")) {
                String s = e.attr("style");
                if (!StringUtils.isEmpty(s)) {
                    e.attributes().remove("style");
                }
            }
            StringBuilder sb = new StringBuilder();
            for (Node cc : d.body().childNodes()) {
                sb.append(cc);
            }
            return sb.toString();
        } else {
            return VrWikiParser.convertToHtml(html, "Wiki");
        }
    }

    public static String getRelativeDateMessage(Date dte, Locale loc) {
        if (dte == null) {
            return "";
        }
        if (loc == null) {
            loc = Locale.getDefault();
        }
        boolean fr = loc.getLanguage() != null && loc.getLanguage().equalsIgnoreCase("fr");
//        boolean en = en = !fr;
        Date now = new Date();
        SimpleDateFormat dateOnlyFormat = DateFormatUtils.getFormat(fr ? "dd/MM/yyyy" : "yyyy-MM-dd");
        SimpleDateFormat dayMonthFormat = DateFormatUtils.getFormat("dd MMM");
        SimpleDateFormat yearMonthFormat = DateFormatUtils.getFormat(fr ? "MM/yyyy" : "yyyy-MM");
        SimpleDateFormat yearFormat = DateFormatUtils.getFormat("yyyy");
        if (dateOnlyFormat.format(dte).equals(dateOnlyFormat.format(now))) {
            return DateFormatUtils.getFormat("HH:mm").format(dte);
        }
        if (yearMonthFormat.format(dte).equals(yearMonthFormat.format(now))) {
            Calendar dte0 = Calendar.getInstance();
            dte0.setTime(dte);
            Calendar now0 = Calendar.getInstance();
            now0.setTime(now);
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) - 1) {
                return fr ? "hier" : "yesterdy";
            }
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) - 2) {
                return fr ? "il y'a 2 jours" : "2 days ago";
            }
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) - 3) {
                return fr ? "il y'a 3 jours" : "3 days ago";
            }
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) + 1) {
                return fr ? "demain" : "tomorrow";
            }
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) + 2) {
                return fr ? "dans 2 jours" : "in 2 days";
            }
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) + 3) {
                return "dans 3 jours";
            }

            return dayMonthFormat.format(dte);
        }
        if (yearFormat.format(dte).equals(yearFormat.format(now))) {
            return dayMonthFormat.format(dte);
        }
        return dateOnlyFormat.format(dte);
    }

    public static String str(Object... a) {
        StringBuilder sb = new StringBuilder();
        for (Object x : a) {
            if (x != null) {
                sb.append(String.valueOf(x));
            }
        }
        return sb.toString();
    }

    public static String fstr(String format, Object... a) {
        UserSession s = null;
        s = CorePlugin.get().getUserSession();
        Locale loc = s == null ? null : s.getLocale();
        if (loc == null) {
            loc = Locale.getDefault(Locale.Category.DISPLAY);
        }
        MessageFormat mf = new MessageFormat(format, loc);
        return mf.format(a);
    }

    public static String date(Date d, String format) {
        return d == null ? "" : new SimpleDateFormat(format).format(d);
    }

    public static String strcut(String value, net.vpc.upa.Entity e, String fieldName) {
        StringType d = (StringType) e.getField("data").getDataType();
        int m = d.getMax();
        if (m <= 0) {
            m = 255;
        }
        return VrUtils.strcut(value, m);
    }

    public static String strcut(String value, int max) {
        if (value == null) {
            value = "";
        }
        if (value.length() > max) {
            value = value.substring(0, max - 3) + "...";
        }
        return value;
    }

    public static String strexpand(String value, String chars, int min) {
        if (value == null) {
            value = "";
        }
        while (value.length() < min) {
            if (value.length() + chars.length() < min) {
                value = value + chars;
            } else {
                value = value + chars.substring(0, min - (value.length() + chars.length()));
            }
        }
        return value;
    }

    public static String html2txt(String value) {
        return VrUtils.html2text(value);
    }

    public static String formatJSONObject(Object cmd) {
        Gson gson = new Gson();
        return gson.toJson(cmd);
    }

    public static String dformat(Number nbr, String format) {
        if (nbr == null) {
            return "";
        }
        if (nbr instanceof Double) {
            double d = (double) nbr;
            double di = (long) d;
            if (di == d) {
                nbr = new Long((long) d);
                return String.valueOf(nbr);
            }
        }
        DecimalFormat f = new DecimalFormat(format);
        return f.format(nbr);
    }

    public static <T> T parseJSONObject(String cmd, Class<T> type) {
        Object arg = null;
        if (cmd != null) {
            Class pt = type;
            if (pt.equals(String.class)) {
                arg = cmd;
            } else if (pt.equals(Boolean.class)) {
                arg = StringUtils.isEmpty(cmd) ? null : Boolean.parseBoolean(cmd);
            } else if (pt.equals(Boolean.TYPE)) {
                arg = StringUtils.isEmpty(cmd) ? Boolean.FALSE : Boolean.parseBoolean(cmd);
            } else if (pt.equals(Integer.class)) {
                arg = StringUtils.isEmpty(cmd) ? null : Integer.parseInt(cmd);
            } else if (pt.equals(Integer.TYPE)) {
                arg = StringUtils.isEmpty(cmd) ? 0 : Integer.parseInt(cmd);
            } else if (pt.equals(Double.class)) {
                arg = StringUtils.isEmpty(cmd) ? null : Double.parseDouble(cmd);
            } else if (pt.equals(Double.TYPE)) {
                arg = StringUtils.isEmpty(cmd) ? 0.0 : Double.parseDouble(cmd);
            } else if (pt.equals(Long.class)) {
                arg = StringUtils.isEmpty(cmd) ? null : Long.parseLong(cmd);
            } else if (pt.equals(Long.TYPE)) {
                arg = StringUtils.isEmpty(cmd) ? 0L : Long.parseLong(cmd);
            } else if (pt.isPrimitive()) {
                throw new IllegalArgumentException("Not yet supported");
            } else if (pt.isInstance(Number.class)) {
                throw new IllegalArgumentException("Not yet supported");
            } else {
                Gson gson = new Gson();
                arg = gson.fromJson(cmd, pt);
            }
        }
        return (T) arg;
    }

    public static String getFirstNonNull(String... vals) {
        for (String val : vals) {
            if (!StringUtils.isEmpty(val)) {
                return val;
            }
        }
        return "";
    }

    public static String getValidString(String locale, String fr, String ar, String en) {
        if (locale.equals("fr")) {
            return getFirstNonNull(fr, en, ar);
        } else if (locale.equals("ar")) {
            return getFirstNonNull(ar, fr, en);
        } else {
            return getFirstNonNull(en, fr, ar);
        }
    }

    public static String replaceLineSeparators(String string) {
        return replaceLineSeparators(string, "\n");
    }

    public static String replaceLineSeparators(String string, String newLineSeparator) {
        if (string == null) {
            return null;
        }
        string = string.replaceAll("\r\n", "\n");
        string = string.replaceAll("\r", "\n");
        return string;
//        BufferedReader br = new BufferedReader(new StringReader(string));
//        StringBuffer sb = new StringBuffer();
//        String line = null;
//        boolean firstLine = true;
//        try {
//            while ((line = br.readLine()) != null) {
//                if (!firstLine) {
//                    sb.append(newLineSeparator);
//                }
//                sb.append(line);
//                if (firstLine) {
//                    firstLine = false;
//                }
//            }
//        } catch (IOException e) {
//            throw new UPAIllegalArgumentException("No expected");
//        }
//        return sb.toString();
    }

    public static void diffToHtml(VFile file1, VFile file2, VFile output, DiffHtmlStyle style) throws IOException {
        PrintStream out = null;
        try {
            String html = diffToHtml(file1, file2, style);
            VFile parentFile = output.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            out = new PrintStream(output.getOutputStream());
            out.print(html);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static String diffToHtml(VFile file1, VFile file2, DiffHtmlStyle style) throws IOException {
        InputStream in1 = null;
        InputStream in2 = null;
        try {
            String text1 = (file1 != null && file1.isFile() && file1.exists()) ? IOUtils.toString(in1 = file1.getInputStream()) : "";
            String text2 = (file2 != null && file2.isFile() && file2.exists()) ? IOUtils.toString(in2 = file2.getInputStream()) : "";
            return diffToHtml(text1, text2, style);
        } finally {
            if (in1 != null) {
                in1.close();
            }
            if (in2 != null) {
                in2.close();
            }
        }
    }

    public static String diffToHtml(String text1, String text2, DiffHtmlStyle style) {
        DiffHtmlStyle d = style == null ? new DiffHtmlStyle() : style.copy();
        if (StringUtils.isEmpty(d.getInsertedClass())) {
            d.setInsertedClass("diff-inserted");
        }
        if (StringUtils.isEmpty(d.getDeletedClass())) {
            d.setDeletedClass("diff-deleted");
        }
        if (StringUtils.isEmpty(d.getLineClass())) {
            d.setLineClass("diff-line");
        }
        if (StringUtils.isEmpty(d.getDivClass())) {
            d.setDivClass("diff-div");
        }
        StringBuilder html = new StringBuilder();
        if (d.isFullPage()) {
            html.append("<html>");
            html.append("\n<head>");
            html.append("\n<style>\n" +
                    "." + d.getInsertedClass() + " {\n" +
                    "    background-color: #d2efb2;\n" +
                    "}\n" +
                    "." + d.getDeletedClass() + " {\n" +
                    "    background-color: #f9ae97;\n" +
                    "    text-decoration: line-through;\n" +
                    "}\n" +
                    "." + d.getLineClass() + " {\n" +
                    "    \n" +
                    "}\n" +
                    "." + d.getDivClass() + " {\n" +
                    "    \n" +
                    "}\n" +
                    "</style>"
            );
            html.append("\n</head>");
            html.append("\n<body>");
        }
        html.append("\n<div class='" + d.getDivClass() + "'>");
        html.append("\n<p class='" + d.getLineClass() + "'>");
        for (GoogleDiffMatchPatch.Diff diff : diff(text1, text2)) {
            String cls = "";
            switch (diff.operation) {
                case EQUAL: {
                    break;
                }
                case DELETE: {
                    cls = d.getDeletedClass();
                    break;
                }
                case INSERT: {
                    cls = d.getInsertedClass();
                    break;
                }
            }
            String tolf = replaceLineSeparators(diff.text);
            StringTokenizer st = new StringTokenizer(tolf, "\n", true);
            while (st.hasMoreTokens()) {
                String portion = st.nextToken();
                if (portion.startsWith("\n")) {
                    for (int i = 0; i < portion.length(); i++) {
                        html.append("\n</p>\n<p class='" + d.getLineClass() + "'>");
                    }
                } else {
                    html.append("<span");
                    if (!StringUtils.isEmpty(cls)) {
                        html.append(" class='" + cls + "'");
                    }
                    html.append(">");
                    html.append(text2html(portion));
                    html.append("</span>");
                }
            }
        }
        html.append("\n</p>");
        html.append("\n</div>");
        if (d.isFullPage()) {
            html.append("\n</body>");
            html.append("\n</html>");
        }
        return html.toString();
    }

    public static List<GoogleDiffMatchPatch.Diff> diff(String text1, String text2) {
        GoogleDiffMatchPatch dmp = new GoogleDiffMatchPatch();
        if (text1 == null) {
            text1 = "";
        }
        if (text2 == null) {
            text2 = "";
        }
        LinkedList<GoogleDiffMatchPatch.Diff> diffs = dmp.diff_main(text1, text2, true);
        dmp.diff_cleanupSemantic(diffs);
        return diffs;
    }

    public static String getBeanName(Class cls) {
        String s = cls.getSimpleName();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String validateContactName(String name) {
        if (name == null) {
            name = "";
        }
        name = name.trim();

        StringBuilder sb = new StringBuilder();
        boolean wasSpace = true;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isWhitespace(c)) {
                if (wasSpace) {
                    //do nothing
                } else {
                    sb.append(c);
                }
                wasSpace = true;
            } else if (Character.isLetter(c)) {
                if (wasSpace) {
                    sb.append(Character.toUpperCase(c));
                } else {
                    sb.append(Character.toLowerCase(c));
                }
                wasSpace = false;
            } else {
                sb.append(Character.toLowerCase(c));
                wasSpace = false;
            }
        }
        return sb.toString();
    }

    public static <T> List<T> filterList(List<T> list, ObjectFilter<T> filter) {
        ArrayList li = new ArrayList();
        for (T t : list) {
            if (filter == null || filter.accept(t)) {
                li.add(t);
            }
        }
        return li;
    }

    public static double divOrZ(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b) || b == 0) {
            return 0;
        }
        return a / b;
    }

    public static String getURLName(String url) {
        if (url == null) {
            url = "";
        }
        int i = url.lastIndexOf('/');
        if (i >= 0) {
            url = url.substring(i + 1);
        }
        i = url.lastIndexOf('?');
        if (i >= 0) {
            url = url.substring(0, i);
        }
        return url;
    }

    public static <T> List<T> sortPreserveIndex(List<T> list, Comparator<T> comp) {
        class IndexedItem<T> {
            T item;
            int index;

            public IndexedItem(T item, int index) {
                this.item = item;
                this.index = index;
            }
        }
        IndexedItem<T>[] items = new IndexedItem[list.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = new IndexedItem<>(list.get(i), i);
        }
        Arrays.sort(items, new Comparator<IndexedItem<T>>() {
            @Override
            public int compare(IndexedItem<T> o1, IndexedItem<T> o2) {
                int i = comp.compare(o1.item, o2.item);
                if (i == 0) {
                    i = o1.index - o2.index;
                }
                return i;
            }
        });
        for (int i = 0; i < items.length; i++) {
            list.set(i, items[i].item);
        }
        return list;
    }

    public static void incKey(Map<String, Number> map, String key) {
        Number v = map.get(key);
        if (v == null) {
            v = 1;
            map.put(key, v);
        } else {
            double d = v.doubleValue() + 1;
            if (((int) d) == d) {
                map.put(key, ((int) d));
            } else {
                map.put(key, d);
            }
        }
    }

    public static void mergeMapKeys(Map<String, Number>... maps) {
        LinkedHashSet<String> keys = new LinkedHashSet<String>();
        for (Map<String, Number> m : maps) {
            for (Map.Entry<String, Number> i : m.entrySet()) {
                keys.add(i.getKey());
            }
        }
        for (Map<String, Number> m : maps) {
            Map<String, Number> list2 = new LinkedHashMap<>();
            for (String k : keys) {
                if (m.containsKey(k)) {
                    list2.put(k, m.get(k));
                } else {
                    list2.put(k, 0);
                }
            }
            m.clear();
            m.putAll(list2);
        }
    }

    public static List<KeyValStruct> toKeyValStructList(Map<String, Number> list) {
        List<KeyValStruct> ll = new ArrayList<KeyValStruct>();
        for (Map.Entry<String, Number> entry : list.entrySet()) {
            ll.add(new KeyValStruct(entry.getKey(), entry.getValue()));
        }
        return ll;
    }

    public static List<ValueCount> reverseSortCountValueCountList(Map<String, Number> list) {
        List<KeyValStruct> ll = toKeyValStructList(list);
        Collections.sort(ll);
        Collections.reverse(ll);
        List<ValueCount> ret = new ArrayList<>();
        for (KeyValStruct s : ll) {
            ret.add(new ValueCount(s.n, s.n, s.v.intValue()));
        }
        return ret;
    }

    public static Map<String, Number> namedValueCountToMap(List<NamedValueCount> list) {
        Map<String, Number> aa = new LinkedHashMap<>();
        for (NamedValueCount v : list) {
            aa.put(v.getName(), v.getCount());
        }
        return aa;
    }

    public static List<NamedValueCount> reverseSortCountNamedValueCountList(Map<String, Number> list) {
        List<KeyValStruct> ll = toKeyValStructList(list);
        Collections.sort(ll);
        Collections.reverse(ll);
        List<NamedValueCount> ret = new ArrayList<>();
        for (KeyValStruct s : ll) {
            ret.add(new NamedValueCount(s.n, s.n, s.v.intValue()));
        }
        return ret;
    }

    public static Map<String, Number> reverseSortCount(Map<String, Number> list, int groupsCount, String othersName) {
        List<KeyValStruct> ll = toKeyValStructList(list);
        Collections.sort(ll);
        Collections.reverse(ll);

        if (ll.size() > groupsCount) {
            StringBuilder sb = new StringBuilder();
            Number count = 0;
            List<KeyValStruct> ll2 = new ArrayList<>();
            for (int i = 0; i < groupsCount; i++) {
                ll2.add(ll.get(i));
            }
            for (int i = groupsCount; i < ll.size(); i++) {
                KeyValStruct v = ll.get(i);
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(v.n);
                count = v.v.doubleValue() + count.doubleValue();
            }
            if (count.intValue() == count.doubleValue()) {
                count = count.intValue();
            }
            if (othersName != null) {
                ll2.add(new KeyValStruct(othersName, count));
            }
            ll = ll2;
        }
        LinkedHashMap<String, Number> list2 = new LinkedHashMap<String, Number>();
        for (KeyValStruct a : ll) {
            list2.put(a.n, a.v);
        }
        return list2;
    }

    public static Map<String, Number> reverseSortCount(Map<String, Number> list) {
        List<KeyValStruct> ll = toKeyValStructList(list);
        Collections.sort(ll);
        Collections.reverse(ll);
        LinkedHashMap<String, Number> list2 = new LinkedHashMap<String, Number>();
        for (KeyValStruct a : ll) {
            list2.put(a.n, a.v);
        }
        return list2;
    }

    public static Map<String, Number> sortCount(Map<String, Number> list) {

        List<KeyValStruct> ll = toKeyValStructList(list);
        Collections.sort(ll);
        LinkedHashMap<String, Number> list2 = new LinkedHashMap<String, Number>();
        for (KeyValStruct a : ll) {
            list2.put(a.n, a.v);
        }
        return list2;
    }

    public static Map<String, Number> sortKey(Map<String, Number> list) {

        List<KeyValStruct> ll = toKeyValStructList(list);
        Collections.sort(ll, new Comparator<KeyValStruct>() {
            @Override
            public int compare(KeyValStruct o1, KeyValStruct o2) {
                String n1 = o1.n;
                String n2 = o2.n;
                return n1.compareTo(n2);
            }
        });
        LinkedHashMap<String, Number> list2 = new LinkedHashMap<String, Number>();
        for (KeyValStruct a : ll) {
            list2.put(a.n, a.v);
        }
        return list2;
    }

    /**
     * *
     * **
     *
     * @param pattern
     * @return
     */
    public static String simpexpToRegexp(String pattern, boolean contains) {
        if (pattern == null) {
            pattern = "*";
        }
        int i = 0;
        char[] cc = pattern.toCharArray();
        StringBuilder sb = new StringBuilder();
        while (i < cc.length) {
            char c = cc[i];
            switch (c) {
                case '.':
                case '!':
                case '$':
                case '[':
                case ']':
                case '(':
                case ')':
                case '?':
                case '^':
                case '\\': {
                    sb.append('\\').append(c);
                    break;
                }
                case '*': {
//                    if (i + 1 < cc.length && cc[i + 1] == '*') {
//                        i++;
//                        sb.append("[a-zA-Z_0-9_$.-]*");
//                    } else {
//                        sb.append("[a-zA-Z_0-9_$-]*");
//                    }
                    sb.append(".*");
                    break;
                }
                default: {
                    sb.append(c);
                }
            }
            i++;
        }
        if (!contains) {
            sb.insert(0, '^');
            sb.append('$');
        }
        return sb.toString();
    }

    public static class KeyValStruct implements Comparable<KeyValStruct> {

        String n;
        Number v;

        public KeyValStruct(String n, Number v) {
            this.n = n;
            this.v = v;
        }

        @Override
        public int compareTo(KeyValStruct o) {
            if (v == null) {
                if (o.v == null) {
                    //check next
                } else {
                    return -1;
                }
            } else if (o.v == null) {
                return 1;
            } else if (v.doubleValue() > o.v.doubleValue()) {
                return 1;
            } else if (v.doubleValue() < o.v.doubleValue()) {
                return -1;
            }
            return n.compareTo(o.n);
        }

    }
}
