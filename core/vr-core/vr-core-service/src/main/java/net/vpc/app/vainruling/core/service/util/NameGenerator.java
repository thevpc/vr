package net.vpc.app.vainruling.core.service.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 2/13/17.
 */
public class NameGenerator {
    private NameExistenceChecker checker;
    private NameSequence sequence;

    public NameGenerator(NameExistenceChecker checker, IndexNameSequence sequence) {
        this.checker = checker;
        this.sequence = sequence;
    }

    public String nextName() {
        while (true) {
            String n = sequence.next();
            if(!checker.nameRegistered(n)){
                checker.registerName(n);
                return n;
            }
        }
    }

    public NameExistenceChecker getChecker() {
        return checker;
    }

    public NameSequence getSequence() {
        return sequence;
    }

    interface NameSequence {
        String next();
    }

    public static class IndexNameSequence implements NameSequence {
        String prefix;
        int start;
        int index;
        boolean skipFirst;

        public IndexNameSequence(String prefix, int start,boolean skipFirst) {
            this.prefix = prefix;
            this.start = start;
            this.index = start;
            this.skipFirst = skipFirst;
        }

        @Override
        public String next() {
            String s = prefix + ((skipFirst && index == start)? "" : index);
            index++;
            return s;
        }
    }

    interface NameExistenceChecker {
        boolean nameRegistered(String name);

        void registerName(String name);
    }

    public static class SetNameExistenceChecker implements NameExistenceChecker {
        private Set<String> names = new HashSet<>();

        @Override
        public boolean nameRegistered(String name) {
            return names.contains(name);
        }

        @Override
        public void registerName(String name) {
            names.add(name);
        }
    }
}
