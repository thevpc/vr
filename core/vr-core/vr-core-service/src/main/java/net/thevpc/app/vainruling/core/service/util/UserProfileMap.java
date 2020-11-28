package net.thevpc.app.vainruling.core.service.util;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class UserProfileMap {

    private ManyToManyIntMap userToProfilesMap = new ManyToManyIntMap();
    private ManyToManyIntMap profileToParentsMap = new ManyToManyIntMap();

//    public static void main(String[] args) {
//        UserProfileMap m = new UserProfileMap();
//        m.addProfileParent(1, 2);
//        m.addProfileParent(3, 4);
//        m.addProfileParent(2, 3);
//        System.out.println(m.getProfileParents(1));
//        System.out.println(m.getProfileChildren(3));
//        System.out.println(m.isProfileChildrenOf(1, 4));
//        System.out.println(m.isProfileAncestorOf(3, 1));
//    }

    public boolean isProfileChildrenOf(int profileChildId, int profileAncestorId) {
        return profileToParentsMap.contains(profileChildId, profileAncestorId);
    }

    public boolean isProfileAncestorOf(int profileAncestorId, int profileChildId) {
        return profileToParentsMap.contains(profileChildId, profileAncestorId);
    }

    public int[] getProfileParents(int profileId) {
        return profileToParentsMap.getSecondValues(profileId);
    }

    public int[] getProfileChildren(int profileId) {
        return profileToParentsMap.getFirstValues(profileId);
    }

    public boolean addProfileParent(int profile, int parentProfileId) {
        profileToParentsMap.add(profile, parentProfileId);
        boolean b = false;
        int[] parents = profileToParentsMap.getSecondValues(parentProfileId);
        for (Integer pp : parents) {
            b |= profileToParentsMap.add(profile, pp);
        }
        int[] children = profileToParentsMap.getFirstValues(profile);
        for (Integer child : children) {
            b |= profileToParentsMap.add(child, parentProfileId);
            for (Integer pp : parents) {
                b |= profileToParentsMap.add(child, pp);
            }
        }
        return b;
    }

    public void buildParentsTree() {

    }

    public void clear() {
        userToProfilesMap.clear();
        profileToParentsMap.clear();
    }

    public boolean contains(int userId, int profileId) {
        if (userToProfilesMap.contains(userId, profileId)) {
            return true;
        }
        int[] allParents = profileToParentsMap.getSecondValues(profileId);
        if (allParents != null) {
            for (Integer o : allParents) {
                if (userToProfilesMap.contains(userId, o)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int[] getProfiles(int userId) {
        int[] p = userToProfilesMap.getSecondValues(userId);
        IntSet n = new IntOpenHashSet(p);
        for (int x : p) {
            n.addAll(new IntOpenHashSet(profileToParentsMap.getSecondValues(x)));
        }
        return n.toIntArray();
    }

    public int[] getUsers(int profileId) {
        IntSet p = new IntOpenHashSet(userToProfilesMap.getFirstValues(profileId));
        for (int c : getProfileChildren(profileId)) {
            p.addAll(new IntOpenHashSet(userToProfilesMap.getFirstValues(c)));
        }
        return p.toIntArray();
    }

    public int[] getProfilesImmediate(int userId) {
        return userToProfilesMap.getSecondValues(userId);
    }

    public int[] getUsersImmediate(int profileId) {
        return userToProfilesMap.getFirstValues(profileId);
    }

    public boolean containsImmediate(int userId, int profileId) {
        return userToProfilesMap.contains(userId, profileId);
    }

    public boolean add(int userId, int profileId) {
        return userToProfilesMap.add(userId, profileId);
    }

    public boolean remove(int userId, int profileId) {
        return userToProfilesMap.remove(userId, profileId);
    }

    public int removeUser(int userId) {
        return userToProfilesMap.removeFirst(userId);
    }

    public int removeProfile(int profileId) {
        return userToProfilesMap.removeSecond(profileId);
    }

    public int size() {
        return userToProfilesMap.size();
    }

}
