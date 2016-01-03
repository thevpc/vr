/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
public class VrNotificationManager {

    private final static Map<String, VrNotificationQueue> queues = new HashMap<>();

    public VrNotificationQueue getQueue(String id) {
        return queues.get(id);
    }
    
    public boolean register(String id, String label, int size) {
        synchronized (queues) {
            VrNotificationQueue q = queues.get(id);
            if (q == null) {
                q = new VrNotificationQueue(id, label, size);
                queues.put(id, q);
                return true;
            }
        }
        return false;
    }
}
