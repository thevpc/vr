/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
@Scope(value = "session")
public class VrNotificationSession {

    private final static Map<String, VrNotificationSessionItem> map = new HashMap<>();

    private static class VrNotificationSessionItem {

        private Queue<VrNotificationEvent> queue = new LinkedList<>();
        private VrNotificationQueue def;

        public VrNotificationSessionItem(VrNotificationQueue def) {
            this.def = def;
        }

    }

    private VrNotificationSessionItem get(String s) {
        VrNotificationSessionItem item = null;
        synchronized (map) {
            item = map.get(s);
            if (item == null) {
                VrNotificationManager cc = VrApp.getBean(VrNotificationManager.class);
                VrNotificationQueue q = cc.getQueue(s);
                if (q == null) {
                    return null;
                }
                item = new VrNotificationSessionItem(q);
                map.put(s, item);
            }
        }
        return item;
    }

    public void publish(VrNotificationEvent e) {
        VrNotificationSessionItem i = get(e.getQueueId());
        Queue<VrNotificationEvent> q = i.queue;
        if (q.size() > i.def.getSize()) {
            while (q.size() > i.def.getSize()) {
                q.poll();
            }
        }
        q.offer(e);
    }

    public List<VrNotificationEvent> findAll(String queueId) {
        VrNotificationSessionItem i = get(queueId);
        Queue<VrNotificationEvent> q = i.queue;
        return new ArrayList<>(q);
    }

    public void clear(String queueId) {
        VrNotificationSessionItem i = get(queueId);
        Queue<VrNotificationEvent> q = i.queue;
        q.clear();
    }
    
    public VrNotificationEvent consume(String queueId) {
        VrNotificationSessionItem i = get(queueId);
        Queue<VrNotificationEvent> q = i.queue;
        return q.poll();
    }

    public void consume(VrNotificationEvent e) {
        VrNotificationSessionItem i = get(e.getQueueId());
        Queue<VrNotificationEvent> q = i.queue;
        q.remove(e);
    }
}
