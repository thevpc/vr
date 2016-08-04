/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.fs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrFSTable {

    private final List<VrFSEntry> entries = new ArrayList<>();

    private static VrFSEntry parseVrFSEntry(String line) {
        if (line != null) {
            line = line.trim();
            List<String> values = new ArrayList<String>();
            if (!line.startsWith("#")) {
                StringBuilder w = new StringBuilder();
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
                                    w.delete(0, w.length());
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
                                    w.delete(0, w.length());
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
                                    w.delete(0, w.length());
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
                    e.setFilterType(values.get(0));
                    if (values.size() > 1) {
                        e.setFilterName(values.get(1));
                        if (values.size() > 2) {
                            e.setMountPoint(values.get(2));
                            if (values.size() > 3) {
                                e.setLinkPath(values.get(3));
                            }
                        }
                    }
                    return e;
                }
            }
        }
        return null;
    }

    public VrFSEntry[] getEntries() {
        return entries.toArray(new VrFSEntry[entries.size()]);
    }

    public VrFSEntry[] getEntries(String filterName, String filterType) {
        List<VrFSEntry> lentries = new ArrayList<>();
        for (VrFSEntry e : this.entries) {
            if (e.getFilterName() != null && e.getFilterName().equalsIgnoreCase(filterName)
                    && e.getFilterType() != null && e.getFilterType().equalsIgnoreCase(filterType)) {
                lentries.add(e);
            }
        }
        return lentries.toArray(new VrFSEntry[lentries.size()]);
    }

    public VrFSEntry[] getEntriesByType(String filterType) {
        List<VrFSEntry> lentries = new ArrayList<>();
        for (VrFSEntry e : this.entries) {
            if (e.getFilterType() != null && e.getFilterType().equalsIgnoreCase(filterType)) {
                lentries.add(e);
            }
        }
        return lentries.toArray(new VrFSEntry[lentries.size()]);
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
