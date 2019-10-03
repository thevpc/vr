/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author vpc
 */
public class ProfileRightBuilder {

    private Map<Integer, Set<String>> rightsToAdd = new HashMap<>();

    public ProfileRightBuilder addProfileRight(int profieId, String... names) {
        return add(profieId, names);
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
        for (Map.Entry<Integer, Set<String>> entry : rightsToAdd.entrySet()) {
            gg.addProfileRights(entry.getKey(), entry.getValue().toArray(new String[0]));
        }
    }
}
