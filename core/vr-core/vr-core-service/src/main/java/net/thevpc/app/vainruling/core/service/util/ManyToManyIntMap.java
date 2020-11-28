package net.thevpc.app.vainruling.core.service.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.*;

public class ManyToManyIntMap {

    private Int2ObjectMap<IntSet> a2b = new Int2ObjectOpenHashMap<>();
    private Int2ObjectMap<IntSet> b2a = new Int2ObjectOpenHashMap();
    private int size;

    public boolean contains(int a, int b) {
        IntSet list = a2b.get(a);
        return (list != null) && list.contains(b);
    }

    public int[] getSecondValues(int a) {
        IntSet b = a2b.get(a);
        if (b == null) {
            return new int[0];
        }
        return b.toIntArray();
    }

    public int[] getFirstValues(int b) {
        IntSet a = b2a.get(b);
        if (a == null) {
            return new int[0];
        }
        return a.toIntArray();
    }

    public boolean add(int a, int b) {
        boolean bb = false;
        IntSet v = a2b.get(a);
        if (v == null) {
            v = new IntOpenHashSet();
            a2b.put(a, v);
        }
        bb |= v.add(b);

        v = b2a.get(b);
        if (v == null) {
            v = new IntOpenHashSet();
            b2a.put(b, v);
        }
        bb |= v.add(a);
        if (bb) {
            size++;
        }
        return bb;
    }

    public int removeFirst(int a) {
        IntSet bList = a2b.get(a);
        if (bList != null) {
            IntIterator ii = bList.iterator();
            while (ii.hasNext()) {
                int b = ii.nextInt();
                IntSet aList = b2a.get(b);
                if (aList != null) {
                    aList.remove(a);
                }
            }
            size -= bList.size();
            return bList.size();
        }
        return 0;
    }

    public int removeSecond(int b) {
        IntSet aList = b2a.get(b);
        if (aList != null) {
            IntIterator ii = aList.iterator();
            while (ii.hasNext()) {
                int a = ii.nextInt();
                IntSet bList = a2b.get(a);
                if (bList != null) {
                    bList.remove(b);
                }
            }
            size -= aList.size();
            return aList.size();
        }
        return 0;
    }

    public boolean remove(int a, int b) {
        IntSet bList = a2b.get(a);
        if (bList != null) {
            bList.remove(b);
            IntSet aList = b2a.get(b);
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

    public Set<Entry> entrySet() {
        Set<Entry> set = new HashSet<>();
        final ManyToManyIntMap _this = this;
        for (Map.Entry<Integer, IntSet> entry : a2b.entrySet()) {
            int a = entry.getKey();
            IntSet bList = entry.getValue();
            IntIterator ii = bList.iterator();
            while (ii.hasNext()) {
                int b = ii.nextInt();
                set.add(new Entry(a, b, _this));
            }
        }
        return set;
    }

    public void clear() {
        a2b.clear();
        b2a.clear();
    }

    public static class Entry {

        private ManyToManyIntMap m;
        private final int a;
        private final int b;

        private Entry(int a, int b, ManyToManyIntMap m) {
            this.a = a;
            this.b = b;
        }

        public int getKey() {
            return a;
        }

        public int getValue() {
            return b;
        }

        public int setValue(int value) {
            if (b != value) {
                m.remove(a, b);
                m.add(a, value);
            }
            return b;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 83 * hash + this.a;
            hash = 83 * hash + this.b;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Entry other = (Entry) obj;
            if (this.a != other.a) {
                return false;
            }
            if (this.b != other.b) {
                return false;
            }
            return true;
        }

    }
}
