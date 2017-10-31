/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "time desc")
@Path("Admin/Security")
@Properties(
        {
                @Property(name = "ui.auto-filter.module", value = "{expr='this.module',order=1}"),
                @Property(name = "ui.auto-filter.action", value = "{expr='this.action',order=2}"),
                @Property(name = "ui.auto-filter.user", value = "{expr='this.user',order=3}"),
                @Property(name = "ui.auto-filter.level", value = "{expr='this.level',order=4}")
        }
)
public class AppTrace {

    @Id
    @Sequence
    private int id;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:10%")
    )
    private Timestamp time;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:9%")
    )
    private String module;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:8%")
    )
    private String action;


    private String objectName;

    private String objectId;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:9%")
    )
    private String user;

    private int userId;

    private int levelId;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:7%")
    )
    private String level;

    private String ip;

    @Summary
    @Field(max = "1024")
    @Properties({
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:30%"),
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    })
    private String message;

    @Summary
    @Field(max = "4096")
    @Properties({
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40%"),
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    })
    private String data;

    public AppTrace() {
    }

    public AppTrace(String message, String data, String module, Timestamp time, String user, int userId, int levelId, String level, String action, String objectName, String objectId, String ip) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppTrace appTrace = (AppTrace) o;

        return id == appTrace.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
