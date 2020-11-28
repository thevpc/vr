/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.thevpc.app.vainruling.core.service.model.AppRightName;

/**
 *
 * @author vpc
 */
public class ProfileRightBuilder {

    private Map<String, String> rightsNamesToAdd = new HashMap<>();
    private Map<Integer, Set<String>> rightsToAdd = new HashMap<>();

    public ProfileRightBuilder addProfileRight(int profieId, String... names) {
        return add(profieId, names);
    }

    public ProfileRightBuilder addName(String name) {
        rightsNamesToAdd.put(name, name);
        return this;
    }

    public ProfileRightBuilder addName(String name, String desc) {
        rightsNamesToAdd.put(name, desc == null ? name : desc);
        return this;
    }

    public ProfileRightBuilder addNames(String... names) {
        for (String name : names) {
            rightsNamesToAdd.put(name, name);
        }
        return this;
    }

    public ProfileRightBuilder add(int profieId, String... names) {
        Set<String> p = rightsToAdd.get(profieId);
        if (p == null) {
            p = new HashSet<>();
            rightsToAdd.put(profieId, p);
        }
        p.addAll(Arrays.asList(names));
        return this;
    }

    public void execute() {
        CorePlugin gg = CorePlugin.get();
        Map<String, AppRightName> rights = new HashMap<>(gg.findProfileRightNames().stream().collect(Collectors.toMap(AppRightName::getName, java.util.function.Function.identity())));
        for (Map.Entry<String, String> s : rightsNamesToAdd.entrySet()) {
            if (!rights.containsKey(s.getKey())) {
                gg.addProfileRightName(s.getKey(), s.getValue());
            }
        }
        for (Map.Entry<Integer, Set<String>> entry : rightsToAdd.entrySet()) {
            gg.addProfileRights(entry.getKey(), entry.getValue().toArray(new String[0]));
        }
    }
}
