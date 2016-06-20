/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.notification;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
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
