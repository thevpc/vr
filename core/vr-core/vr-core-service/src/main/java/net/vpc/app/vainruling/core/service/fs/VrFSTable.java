/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.fs;


import net.vpc.common.strings.StringBuilder2;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrFSTable {
    private static final Set<String> VALID_FILTER_TYPES = new HashSet<>(Arrays.asList("profile", "user"));
    private final List<VrFSEntry> entries = new ArrayList<>();

    private static VrFSEntry parseVrFSEntry(String line) {
        if (line != null) {
            line = line.trim();
            List<String> values = new ArrayList<String>();
            if (!line.startsWith("#")) {
                StringBuilder2 w = new StringBuilder2();
                int i = 0;
                final int WHITE = 0;
                final int QUOTED_WORD = 1;
                final int UNQUOTED_WORD = 2;
                int status = WHITE;

                while (i < line.length()) {
                    char c = line.charAt(i);
                    switch (status) {
                        case WHITE: {
                            switch (c) {
                                case '\t':
                                case ' ': {
                                    break;
                                }
                                case '\"': {
                                    status = QUOTED_WORD;
                                    break;
                                }
                                default: {
                                    w.delete();
                                    w.append(c);
                                    status = UNQUOTED_WORD;
                                    break;
                                }
                            }
                            break;
                        }
                        case UNQUOTED_WORD: {
                            switch (c) {
                                case '\t':
                                case ' ': {
                                    if (w.length() > 0) {
                                        values.add(w.toString());
                                    }
                                    w.delete();
                                    status = WHITE;
                                    break;
                                }
                                case '\"': {
                                    w.append(c);
                                    break;
                                }
                                case '\\': {
                                    if (i + 1 < line.length()) {
                                        w.append(line.charAt(i + 1));
                                        i++;
                                    } else {
                                        w.append(c);
                                    }
                                    break;
                                }
                                default: {
                                    w.append(c);
                                    break;
                                }
                            }
                            break;
                        }
                        case QUOTED_WORD: {
                            switch (c) {
                                case '\t':
                                case ' ': {
                                    w.append(c);
                                    break;
                                }
                                case '\"': {
                                    if (w.length() > 0) {
                                        values.add(w.toString());
                                    }
                                    w.delete();
                                    status = WHITE;
                                    break;
                                }
                                case '\\': {
                                    if (i + 1 < line.length()) {
                                        w.append(line.charAt(i + 1));
                                        i++;
                                    } else {
                                        w.append(c);
                                    }
                                    break;
                                }
                                default: {
                                    w.append(c);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    i++;
                }
                if (w.length() > 0) {
                    values.add(w.toString());
                }
                if (values.size() > 0) {
                    VrFSEntry e = new VrFSEntry();
                    e.setMountPoint(values.get(0));
                    if (values.size() > 1) {
                        e.setLinkPath(values.get(1));
                        if (values.size() > 2) {
                            e.setAllowedUsers(values.get(2));
                        }
                    }
                    return e;
                }
            }
        }
        return null;
    }

    public VrFSEntry[] getEntries() {
        return entries.toArray(new VrFSEntry[0]);
    }

    public int[] findPathEntries(String path) {
        if (StringUtils.isEmpty(path)) {
            return new int[0];
        }
        ArrayList<Integer> all = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            VrFSEntry e = entries.get(i);
            if (e.getLinkPath() != null && e.getLinkPath().equals(path)) {
                all.add(i);
            }
        }
        int[] ret = new int[all.size()];
        for (int i = 0; i < all.size(); i++) {
            ret[i] = all.get(i);
        }
        return ret;
    }

    public void addEntry(VrFSEntry e) {
        if (e != null) {
            checkEntry(e);
            int[] old = findPathEntries(e.getLinkPath());
            for (int i = 0; i < old.length; i++) {
                if (entries.get(old[i]).equals(e)) {
                    return;
                }
            }
            entries.add(e);
        }
    }

    public void removeEntry(VrFSEntry e) {
        if (e != null) {
            checkEntry(e);
            int[] old = findPathEntries(e.getLinkPath());
            for (int i = 0; i < old.length; i++) {
                if (entries.get(old[i]).equals(e)) {
                    entries.remove(i);
                    return;
                }
            }
        }
    }

    private void checkEntry(VrFSEntry e) {
        if (StringUtils.isEmpty(e.getLinkPath())) {
            throw new RuntimeException("Invalid Path");
        }
        if (StringUtils.isEmpty(e.getMountPoint()) || e.getMountPoint().contains("/")) {
            throw new RuntimeException("Invalid Mount Point " + e.getMountPoint());
        }
    }

    public void removeEntry(int index) {
        entries.remove(index);
    }

    public VrFSEntry[] getEntriesByAllowedUsers(String allowedUsers) {
        if(allowedUsers==null){
            allowedUsers="";
        }
        allowedUsers=allowedUsers.trim();
        List<VrFSEntry> lentries = new ArrayList<>();
        for (VrFSEntry e : this.entries) {
            String aa = e.getAllowedUsers();
            if(aa==null){
                aa="";
            }
            aa=aa.trim();
            if (aa.equalsIgnoreCase(allowedUsers)) {
                lentries.add(e);
            }
        }
        return lentries.toArray(new VrFSEntry[0]);
    }

    public VrFSEntry[] getEntriesByLinkPath(String linkPath) {
        List<VrFSEntry> lentries = new ArrayList<>();
        for (VrFSEntry e : this.entries) {
            if (Objects.equals(e.getLinkPath(),linkPath)) {
                lentries.add(e);
            }
        }
        return lentries.toArray(new VrFSEntry[0]);
    }

    public void store(VFile out) throws IOException {
        OutputStream os = null;
        try {
            os = out.getOutputStream();
            store(os);
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public void store(OutputStream out) throws IOException {
        if (out == null) {
            return;
        }
        PrintStream p = new PrintStream(out);
        p.println("# file system table definition\n" +
                "#\n" +
                "# target-folder-name shared-folder allowed-users\n" +
                "#\n" +
                "# example : \n" +
                "# NewFolderName /Documents/ByProfile/Teacher/MyFolderToShare Student\n" +
                "# mounts a folder named NewFolderNameMyFolderToShare under MyDocument of Profile Student that points to the given MyFolderToShare\n");
        for (VrFSEntry entry : entries) {
            p.print("\"" + entry.getMountPoint() + "\"\t");
            p.print("\"" + entry.getLinkPath() + "\"\t");
            p.print("\"" + entry.getAllowedUsers() + "\"\t");
            p.println();
        }
        p.flush();
    }

    public boolean loadSilently(VFile file) {
        try {
            if (file.isFile()) {
                load(file);
                return true;
            }
        } catch (Exception any) {
            //ignore
        }
        return false;
    }

    public void load(VFile file) throws IOException {
        InputStream in = null;
        try {
            in = file.getInputStream();
            load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public void load(InputStream in) throws IOException {
        if (in == null) {
            return;
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = r.readLine()) != null) {
            VrFSEntry e = parseVrFSEntry(line);
            if (e != null) {
                entries.add(e);
            }
        }

    }

    public void loadString(String text) {
        BufferedReader r = new BufferedReader(new StringReader(text));
        String line = null;
        try {
            while ((line = r.readLine()) != null) {
                VrFSEntry e = parseVrFSEntry(line);
                if (e != null) {
                    entries.add(e);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(VrFSTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
