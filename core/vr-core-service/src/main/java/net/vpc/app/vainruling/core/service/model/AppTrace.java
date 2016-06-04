/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import java.sql.Timestamp;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "time desc")
@Path("Admin/Security")
public class AppTrace {

    @Id
    @Sequence
    private int id;
    
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE,value = "width:10%")
    )
    private Timestamp time;
    
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE,value = "width:8%")
    )
    private String action;
    
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE,value = "width:9%")
    )
    private String module;

    private String objectName;
    
    private String objectId;
    
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE,value = "width:9%")
    )
    private String user;
    
    private int userId;
    
    private int levelId;
    
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE,value = "width:7%")
    )
    private String level;

    @Field(max = "1024",modifiers = UserFieldModifier.SUMMARY)
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE,value = "width:30%")
    )
    private String message;
    
    @Field(max = "4096",modifiers = UserFieldModifier.SUMMARY)
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE,value = "width:40%")
    )
    private String data;

    private String ip;
    
    public AppTrace() {
    }

    public AppTrace(String message, String data, String module, Timestamp time, String user, int userId, int levelId, String level, String action, String objectName, String objectId,String ip) {
        this.message = message;
        this.data = data;
        this.module = module;
        this.time = time;
        this.user = user;
        this.userId = userId;
        this.levelId = levelId;
        this.level = level;
        this.action = action;
        this.objectName = objectName;
        this.objectId = objectId;
        this.ip = ip;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
    @Override
    public String toString() {
        return String.valueOf(message);
    }

}
