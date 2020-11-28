/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.common.strings.StringUtils;

/**
 *
 * @author vpc
 */
public class NameMap<I, T> {

    private static final Logger LOG = Logger.getLogger(NameMap.class.getName());

    private Map<String, Set<T>> valuesByName = new HashMap<String, Set<T>>();
    private Map<I, T> valuesById = new HashMap<I, T>();
    private boolean allowDuplicates = false;
    private String name = "NameMap";

    public NameMap(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }

    public NameMap(boolean allowDuplicates, Map m) {
        if (m != null) {
            m.clear();
            valuesByName = m;
        }
        this.allowDuplicates = allowDuplicates;
    }

    public boolean isAllowDuplicates() {
        return allowDuplicates;
    }

    public void setAllowDuplicates(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }

    public boolean containsDuplicates() {
        for (Map.Entry<String, Set<T>> entry : valuesByName.entrySet()) {
            if (entry.getValue().size() > 1) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getDuplicates() {
        Set<String> r = new LinkedHashSet<>();
        for (Map.Entry<String, Set<T>> entry : valuesByName.entrySet()) {
            if (entry.getValue().size() > 1) {
                r.add(entry.getKey());
            }
        }
        return r;
    }

    public String getName() {
        return name;
    }

    public NameMap<I, T> setName(String name) {
        this.name = name;
        return this;
    }

    public void put(I id, T value, String name, String otherNames) {
        put(id, value, new String[]{name}, otherNames);
    }

    public void put(I id, T value, String[] names, String... otherNames) {
        valuesById.put(id, value);
        LinkedHashSet<String> allNames = new LinkedHashSet<String>();
        if (names != null) {
            for (String _name : names) {
                if (!StringUtils.isBlank(_name)) {
                    allNames.add(VrUtils.normalizeName(_name));
                }
            }
            if (otherNames != null) {
                for (String otherName : otherNames) {
                    allNames.addAll(VrUtils.parseNormalizedOtherNames(otherName));
                }
            }
        }
        for (String v : allNames) {
            Set<T> li = valuesByName.get(v);
            if (li == null) {
                li = new LinkedHashSet<>();
                valuesByName.put(v, li);
            }
            li.add(value);
            if (li.size() > 1) {
                if (!isAllowDuplicates()) {
                    LOG.log(Level.SEVERE, "Conflict in {0} : id={1} , name={2} values={3}. ignore older", new Object[]{name, id, v, li, value});
                    li.clear();
                    li.add(value);
                }
            }
        }
    }

    public T getById(I id) {
        return valuesById.get(id);
    }

    public T getByName(String name) {
        Set<T> li = valuesByName.get(VrUtils.normalizeName(name));
        if (li == null) {
            return null;
        }
        switch (li.size()) {
            case 0:
                return null;
            case 1: {
                Iterator<T> i = li.iterator();
                if (i.hasNext()) {
                    return i.next();
                }
                return null;
            }
        }
        LOG.log(Level.SEVERE, "Conflict in {0} : name={1} values={2}", new Object[]{this.name, name, li});
        T last = null;
        for (T t : li) {
            last = t;
        }
        return last;
    }

    public List<T> getAllByName(String name) {
        Set<T> li = valuesByName.get(VrUtils.normalizeName(name));
        if (li == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(li);
    }
}
