/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.util;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.service.fs.VFileInfo;
import net.vpc.app.vainruling.core.service.fs.VFileKind;
import net.vpc.common.io.PathInfo;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileFilter;
import net.vpc.common.vfs.VirtualFileACL;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class DocumentsUtils {

    private static final Logger log = Logger.getLogger(DocumentsUtils.class.getName());


    public static String evalVFileDesc(VFile file) {
        if (file.isFile()) {
            return VrUtils.formatFileSize(file.length());
        }
        if (file.isDirectory()) {
            VFile[] files = file.listFiles();
            int f = 0;
            int d = 0;
            if (files != null) {
                for (VFile ff : files) {
                    if (ff.isDirectory()) {
                        d++;
                    } else {
                        f++;
                    }
                }
            }
            if (f == 0 && d == 0) {
                return "vide";
            }
            StringBuilder sb = new StringBuilder();
            if (d > 0) {
                sb.append(d).append(" rep.");
            }
            if (f > 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(f).append(" fich.");
            }
            return sb.toString();
        }
        return "";
    }


//    public static VirtualFileSystem createFS() {
//        CorePlugin fsp = VrApp.getBean(CorePlugin.class);
//        VirtualFileSystem rootfs = fsp.getRootFileSystem();
//        VirtualFileSystem userfs = rootfs.filter(null);
//        return userfs;
//    }


    public static List<VFileInfo> searchFiles(VFile curr, String searchString) {
        if (StringUtils.isBlank(searchString)) {
            return loadFiles(curr);
        }
        VFileFilter fileFilter=null;
        if(searchString.startsWith("\"")
                && searchString.endsWith("\"")
                && searchString.length()>1
                && !searchString.substring(1,searchString.length()-1).contains("\"")
                ){
            searchString="*"+searchString+"*";
            fileFilter = new ExactFileFilter(searchString.substring(1,searchString.length()-1));
        }else if(!searchString.contains("*")){
            searchString="*"+searchString+"*";
            fileFilter = new WildcardFileFilter(searchString);
        }else{
            fileFilter = new WildcardFileFilter(searchString);
        }
        List<VFileInfo> result = new ArrayList<>();
        Stack<VFile> all = new Stack<>();
        all.push(curr);
        while (!all.isEmpty()) {
            VFile x = all.pop();
            if (fileFilter.accept(x)) {
                VFileInfo fileInfo = DocumentsUtils.createFileInfo(x);
                String path = x.getPath();
                if (path.startsWith(curr.getPath())) {
                    path = path.substring(curr.getPath().length());
                }
                fileInfo.setLongName(path);
                result.add(fileInfo);
            } else if (x.isDirectory()) {
                for (VFile y : x.listFiles()) {
                    all.push(y);
                }
            }
        }
        return result;
    }

    public static String wildcardToRegex(String pattern) {
        if (pattern == null) {
            pattern = "*";
        }
        int i = 0;
        char[] cc = pattern.toCharArray();
        StringBuilder sb = new StringBuilder("^");
        while (i < cc.length) {
            char c = cc[i];
            switch (c) {
                case '.':
                case '$':
                case '{':
                case '}':
                case '+':
                case '(':
                case ')':
                case '[':
                case ']':
                case '\\':
                case '/': {
                    sb.append('\\').append(c);
                    break;
                }
                case '?': {
                    sb.append("[.]");
                    break;
                }
                case '*': {
                    sb.append(".*");
                    break;
                }
                default: {
                    sb.append(c);
                }
            }
            i++;
        }
        sb.append('$');
        return sb.toString();
    }


    public static List<VFileInfo> loadFiles(VFile curr) {
        VFile[] all = curr.getFileSystem().listFiles(curr.getPath());
        ArrayList<VFileInfo> ret = new ArrayList<>();
        for (VFile a : all) {
            ret.add(DocumentsUtils.createFileInfo(a));
        }
        Collections.sort(ret);
        if (!"/".equals(curr.getPath())) {
            ret.add(0, DocumentsUtils.createFileInfo(CorePlugin.FOLDER_BACK, VFileKind.BACK, curr.getParentFile()));
        }
        return ret;
    }


    public static VFileInfo createFileInfo(VFile file) {
        return createFileInfo(file.getName(), VFileKind.ORDINARY, file);
    }

    public static VFileInfo createFileInfo(String name, VFileKind kind, VFile file) {
        String iconCss = "file";
        String labelCss = "";
        long downloads = 0;
        boolean homeFolder = false;
        boolean backFolder = false;
        if (file.isDirectory()) {
            iconCss = "folder";
            homeFolder = file.getPath().equals("/" + CorePlugin.FOLDER_MY_DOCUMENTS);
            if (homeFolder) {
                iconCss = "home";
            }
            backFolder = CorePlugin.FOLDER_BACK.equals(name);
            if (backFolder) {
                iconCss = "parent";
            }
        } else {
            String n = file.getName().toLowerCase();
            String e = PathInfo.create(n).getExtensionPart();
            iconCss = VrWebHelper.extensionsToCss.get(e);
            if (iconCss == null) {
                iconCss = "file";
            }
            CorePlugin fsp = VrApp.getBean(CorePlugin.class);
            downloads = fsp.getDownloadsCount(file);
        }
        String desc = backFolder ? "" : evalVFileDesc(file);
        labelCss = homeFolder ? "color:#349dc9;font-weight: bold;" : backFolder ? "color:#9e9e9e;" : "";
        if (!backFolder) {
            VirtualFileACL acl = file.getACL();
            if (acl != null) {
                //should never be null
                String view = acl.getProperty("ViewFormat");
                if (view != null) {
                    ViewFormat format = null;
                    try {
                        format = VrUtils.parseJSONObject(view, ViewFormat.class);
                    } catch (Exception ex) {
                        //ignore any error
                    }
                    if (format != null) {
                        if (StringUtils.isBlank(format.getIconCss())) {
                            iconCss = format.getIconCss();
                        }
                        if (StringUtils.isBlank(format.getIconCss())) {
                            iconCss = format.getIconCss();
                        }
                    }
                }
            }
        }
        return new VFileInfo(name, kind, file, labelCss, iconCss, downloads, desc);
    }

    public static class ViewFormat {
        private String labelCss;
        private String iconCss;

        public String getLabelCss() {
            return labelCss;
        }

        public void setLabelCss(String labelCss) {
            this.labelCss = labelCss;
        }

        public String getIconCss() {
            return iconCss;
        }

        public void setIconCss(String iconCss) {
            this.iconCss = iconCss;
        }
    }


    private static class WildcardFileFilter implements VFileFilter {
        private Pattern p;

        public WildcardFileFilter(String str) {
            p = Pattern.compile(wildcardToRegex(net.vpc.common.strings.StringUtils.normalizeString(str)));
        }

        @Override
        public boolean accept(VFile pathname) {
            return p.matcher(net.vpc.common.strings.StringUtils.normalizeString(pathname.getName())).matches();
        }
    }
    private static class ExactFileFilter implements VFileFilter {
        private String name;

        public ExactFileFilter(String str) {
            this.name=str;
        }

        @Override
        public boolean accept(VFile pathname) {
            return name.equals(pathname.getName());
        }
    }
}
