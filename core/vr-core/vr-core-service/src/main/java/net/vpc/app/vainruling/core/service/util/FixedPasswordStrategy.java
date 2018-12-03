/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import net.vpc.app.vainruling.core.service.model.AppUser;

/**
 *
 * @author vpc
 */
public class FixedPasswordStrategy implements VrPasswordStrategy{
    private String password;

    public FixedPasswordStrategy(String password) {
        this.password = password;
    }

    @Override
    public String generatePassword(AppUser user) {
        return password;
    }
    
}
