/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.notification;

import net.vpc.common.strings.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrNotificationEvent {

    private String queueId;
    private int timeout;
    private Date creationTime;
    private Date deathTime;
    private String title;
    private Object userObject;
    private Level level;

    public VrNotificationEvent(String queueId, int timeout, Date creationTime, String title, Object userObject, Level level) {
        this.queueId = queueId;
        this.level = level;
        this.timeout = timeout <= 0 ? 60 : timeout;
        this.creationTime = creationTime == null ? new Date() : creationTime;
        this.title = StringUtils.isBlank(title) ? "?" : title;
        this.userObject = userObject;
        Calendar c = Calendar.getInstance();
        c.setTime(this.creationTime);
        c.add(Calendar.SECOND, timeout);
        this.deathTime = c.getTime();
    }

    public boolean isDead() {
        return new Date().after(deathTime);
    }

    public String getQueueId() {
        return queueId;
    }

    public int getTimeout() {
        return timeout;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public Date getDeathTime() {
        return deathTime;
    }

    public String getTitle() {
        return title;
    }

    public Object getUserObject() {
        return userObject;
    }

    public Level getLevel() {
        return level;
    }

}
