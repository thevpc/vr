package net.vpc.app.vainruling.core.service.util;

import java.util.*;

public class ManyToManyMap<A, B> {
    private Map<A, Set<B>> a2b = new HashMap<>();
    private Map<B, Set<A>> b2a = new HashMap<>();
    private int size;

    public boolean contains(A a, B b) {
        Set list = a2b.get(a);
        return (list != null) && list.contains(b);
    }

    public Set<B> getSecondValues(A a) {
        Set<B> b = a2b.get(a);
        if (b == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(b);
    }

    public Set<A> getFirstValues(B b) {
        Set<A> a = b2a.get(b);
        if (a == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(a);
    }

    public boolean add(A a, B b) {
        boolean bb = false;
        Set<B> profilesForUser = a2b.computeIfAbsent(a, k -> new HashSet<>());
        bb |= profilesForUser.add(b);

        Set<A> usersForProfile = b2a.computeIfAbsent(b, k -> new HashSet<>());
        bb |= usersForProfile.add(a);
        if (bb) {
            size++;
        }
        return bb;
    }

    public int removeFirst(A a) {
        Set<B> bList = a2b.get(a);
        if (bList != null) {
            for (B b : bList) {
                Set<A> aList = b2a.get(b);
                if (aList != null) {
                    aList.remove(a);
                }
            }
            size -= bList.size();
            return bList.size();
        }
        return 0;
    }

    public int removeSecond(B b) {
        Set<A> aList = b2a.get(b);
        if (aList != null) {
            for (A a : aList) {
                Set<B> bList = a2b.get(a);
                if (bList != null) {
                    bList.remove(b);
                }
            }
            size -= aList.size();
            return aList.size();
        }
        return 0;
    }

    public boolean remove(A a, B b) {
        Set<B> bList = a2b.get(a);
        if (bList != null) {
            bList.remove(b);
            Set<A> aList = b2a.get(b);
            if (aList != null) {
                aList.remove(a);
            }
            size--;
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public Set<Map.Entry<A, B>> entrySet() {
        Set<Map.Entry<A, B>> set = new HashSet<>();
        for (Map.Entry<A, Set<B>> e : a2b.entrySet()) {
            A a = e.getKey();
            for (B b : e.getValue()) {
                set.add(new M2MEntry(a, b));
            }
        }
        return set;
    }

    public void clear() {
        a2b.clear();
        b2a.clear();
    }


    private class M2MEntry implements Map.Entry<A, B> {
        private final A a;
        private final B b;

        public M2MEntry(A a, B b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public A getKey() {
            return a;
        }

        @Override
        public B getValue() {
            return b;
        }

        @Override
        public B setValue(B value) {
            if (Objects.equals(b, value)) {
                remove(a, b);
                add(a, value);
            }
            return b;
        }
    }
}
