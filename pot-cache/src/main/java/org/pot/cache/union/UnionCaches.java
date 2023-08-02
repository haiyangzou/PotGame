package org.pot.cache.union;

public class UnionCaches {
    private static volatile UnionSnapshotCache unionSnapshotCache;

    public static UnionSnapshotCache snapshot() {
        return unionSnapshotCache;
    }
}
