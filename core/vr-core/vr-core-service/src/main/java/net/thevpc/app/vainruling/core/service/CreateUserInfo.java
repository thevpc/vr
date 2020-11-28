/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service;

import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.util.VrPasswordStrategy;

/**
 *
 * @author vpc
 */
public class CreateUserInfo {
    private int userId;
    private int userTypeId;
    private int departmentId;
    private boolean attachToExistingUser;
    private String[] defaultProfiles;
    private VrPasswordStrategy passwordStrategy;
    private String firstName;
    private String lastName;
    private AppUser userPrototype;

    public int getUserTypeId() {
        return userTypeId;
    }

    public CreateUserInfo setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
        return this;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public CreateUserInfo setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
        return this;
    }

    public boolean isAttachToExistingUser() {
        return attachToExistingUser;
    }

    public CreateUserInfo setAttachToExistingUser(boolean attachToExistingUser) {
        this.attachToExistingUser = attachToExistingUser;
        return this;
    }

    public String[] getDefaultProfiles() {
        return defaultProfiles;
    }

    public CreateUserInfo setDefaultProfiles(String... defaultProfiles) {
        this.defaultProfiles = defaultProfiles;
        return this;
    }

    public VrPasswordStrategy getPasswordStrategy() {
        return passwordStrategy;
    }

    public CreateUserInfo setPasswordStrategy(VrPasswordStrategy passwordStrategy) {
        this.passwordStrategy = passwordStrategy;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public CreateUserInfo setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public CreateUserInfo setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public CreateUserInfo setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public AppUser getUserPrototype() {
        return userPrototype;
    }

    public CreateUserInfo setUserPrototype(AppUser userPrototype) {
        this.userPrototype = userPrototype;
        return this;
    }
    
    
}
