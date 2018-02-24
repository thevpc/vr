/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.notification;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrNotificationQueue {

    private String id;
    private String label;
    private int size;

    public VrNotificationQueue() {
    }

    public VrNotificationQueue(String id, String label, int size) {
        this.id = id;
        this.label = label;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
