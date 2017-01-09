package org.minperf;

/**
 * The settings used to generate the hash function.
 */
public class Settings {

public static final boolean IMPROVED_SPLIT_RULES = true;

    /**
     * Some space could be saved by making the bucket header more complex. One
     * way is to adjust the dataBits by the start offset, before calculating the
     * start offset. This saves about 1 bit per bucket. Another option is to use
     * multiple levels for the bucket header, for example level 1 bucket each
     * pointing to 100 level 2 buckets. What needs to be analyzed is whether
     * such tricks improve evaluation time for a given MPHF size, versus just
     * increasing the bucket size, which also saves space.
     *
     * Another approach, which doesn't complicate the header, is to add skip
     * offsets in some levels of the bucket itself. This would allow using
     * larger buckets, without affecting evaluation cost much. And this data
     * could be variable size, which probably needs less space.
     */
    public static final boolean COMPLEX_BUCKET_HEADER = false;

    /**
     * The number of supplemental hash functions per universal hash is 2 ^ this
     * number. Could be increased to reduce the number of universal hash
     * function calls, which also speeds up evaluation time.
     */
    public static final int SUPPLEMENTAL_HASH_SHIFT = 18;

    /**
     * The number of times the same universal hash is mixed using the
     * supplemental hash function. Must be a power of 2.
     */
    private static final long SUPPLEMENTAL_HASH_CALLS = 1 << SUPPLEMENTAL_HASH_SHIFT;

    /**
     * The estimated space in bits for 1000 entries, where leafSize is the array
     * index.
     */
    private static final int[] ESTIMATED_SPACE = { 10000, 3571, 2574, 2322, 2124,
            1920, 1848, 1785, 1717, 1692, 1635, 1620, 1608, 1587, 1581, 1564,
            1554, 1552, 1543, 1534, 1528, 1524, 1522, 1517, 1517, 1495 };

    /**
     * The Rice parameter k to use for leaves of size = array index.
     */
    private static final int[] RICE_LEAF = { 0, 0, 0, 1, 3, 4, 5, 7, 8, 10, 11,
            12, 14, 15, 16, 18, 19, 21, 22, 23, 25, 26, 28, 29, 30, 32, 33, 35,
            36, 38, 39, 40, 42 };

    /**
     * The Rice parameter k that is used to split medium sized sets that are
     * evenly split into subsets.
     */
    private static final int[][] RICE_SPLIT_MORE = {
        // 0 .. 6
        { }, { }, { }, { }, { }, { 4}, { 4},
        // 7 .. 13
        { 4}, { 7}, { 7}, { 10, 7}, { 11, 7}, { 11, 7}, { 14, 8},
        // 14 .. 18
        { 14, 8}, { 15, 8}, { 18, 8}, { 18, 8}, { 19, 8},
        // 19 .. 23
        { 22, 13}, { 22, 13}, { 23, 14}, { 26, 14}, { 27, 14},
        // 24 .. 25
        { 27, 14}, { 31, 20, 12}};

    private static final int[][] SPLIT_RULES = {
    // leafSize 0
    {  },
    // leafSize 1
    {  },
    // leafSize 2
    {  },
    // leafSize 3
    {  },
    // leafSize 4
    {  },
    // leafSize 5
    { 12, 3, 3, 20, 2, 2 },
    // leafSize 6
    { 15, 3, 4, 18, 3, 4 },
    // leafSize 7
    { 18, 3, 4, 21, 3, 4 },
    // leafSize 8
    { 18, 3, 4, 21, 3, 4, 24, 3, 4, 28, 4, 7,
        32, 4, 7 },
    // leafSize 9
    { 16, 2, 2, 18, 2, 2, 21, 3, 4, 24, 3, 4,
        27, 3, 4, 28, 4, 7, 32, 4, 7, 36, 4, 7 },
    // leafSize 10
    { 16, 2, 2, 20, 2, 2, 24, 3, 4, 27, 3, 4,
        30, 3, 5, 32, 4, 7, 36, 4, 7, 40, 4, 7 },
    // leafSize 11
    { 16, 2, 2, 22, 2, 2, 24, 3, 4, 27, 3, 4,
        30, 3, 5, 33, 3, 5, 36, 4, 7, 40, 4, 7,
        44, 4, 8, 50, 5, 10, 55, 5, 11 },
    // leafSize 12
    { 16, 2, 2, 24, 2, 2, 27, 3, 4, 30, 3, 5,
        33, 3, 5, 36, 3, 5, 40, 4, 7, 44, 4, 8,
        48, 4, 8, 50, 5, 10, 55, 5, 11, 60, 5, 11 },
    // leafSize 13
    { 30, 3, 5, 33, 3, 5, 36, 3, 5, 39, 3, 5,
        44, 4, 8, 48, 4, 8, 52, 4, 8, 55, 5, 11,
        60, 5, 11, 65, 5, 11 },
    // leafSize 14
    { 30, 3, 5, 33, 3, 5, 36, 3, 5, 39, 3, 5,
        42, 3, 5, 44, 4, 8, 48, 4, 8, 52, 4, 8,
        56, 4, 8, 60, 5, 11, 65, 5, 11, 70, 5, 11,
        72, 6, 14, 78, 6, 14, 84, 6, 14 },
    // leafSize 15
    { 33, 3, 5, 36, 3, 5, 39, 3, 5, 42, 3, 5,
        45, 3, 5, 48, 4, 8, 52, 4, 8, 56, 4, 8,
        60, 4, 8, 65, 5, 11, 70, 5, 11, 75, 5, 11,
        78, 6, 14, 84, 6, 14, 90, 6, 15 },
    // leafSize 16
    { 36, 3, 5, 39, 3, 5, 42, 3, 5, 45, 3, 5,
        48, 3, 5, 52, 4, 8, 56, 4, 8, 60, 4, 8,
        64, 4, 8, 65, 5, 11, 70, 5, 11, 75, 5, 11,
        80, 5, 12, 84, 6, 14, 90, 6, 15, 96, 6, 15 },
    // leafSize 17
    { 36, 3, 5, 39, 3, 5, 42, 3, 5, 45, 3, 5,
        48, 3, 5, 51, 3, 5, 52, 4, 8, 56, 4, 8,
        60, 4, 8, 64, 4, 8, 68, 4, 9, 70, 5, 11,
        75, 5, 11, 80, 5, 12, 85, 5, 12, 90, 6, 15,
        96, 6, 15, 102, 6, 15 },
    // leafSize 18
    { 39, 3, 5, 42, 3, 5, 45, 3, 5, 48, 3, 5,
        51, 3, 5, 54, 3, 5, 56, 4, 8, 60, 4, 8,
        64, 4, 8, 68, 4, 9, 72, 4, 9, 75, 5, 11,
        80, 5, 12, 90, 5, 12, 96, 6, 15, 108, 6, 15 },
    // leafSize 19
    { 42, 3, 5, 45, 3, 5, 48, 3, 5, 51, 3, 5,
        54, 3, 5, 57, 3, 6, 60, 4, 8, 64, 4, 8,
        68, 4, 9, 72, 4, 9, 76, 4, 9, 80, 5, 12,
        90, 5, 12, 95, 5, 12, 119, 7, 18, 126, 7, 19,
        133, 7, 19 },
    // leafSize 20
    { 42, 3, 5, 45, 3, 5, 48, 3, 5, 51, 3, 5,
        54, 3, 5, 57, 3, 6, 60, 3, 6, 64, 4, 8,
        68, 4, 9, 72, 4, 9, 76, 4, 9, 80, 4, 9,
        90, 5, 12, 100, 5, 12, 126, 7, 19, 133, 7, 19,
        140, 7, 19 },
    // leafSize 21
    { 45, 3, 5, 48, 3, 5, 51, 3, 5, 54, 3, 5,
        57, 3, 6, 60, 3, 6, 63, 3, 6, 64, 4, 8,
        68, 4, 9, 72, 4, 9, 76, 4, 9, 80, 4, 9,
        84, 4, 9, 90, 5, 12, 105, 5, 12, 133, 7, 19,
        140, 7, 19, 147, 7, 19 },
    // leafSize 22
    { 48, 3, 5, 51, 3, 5, 54, 3, 5, 57, 3, 6,
        60, 3, 6, 63, 3, 6, 66, 3, 6, 68, 4, 9,
        72, 4, 9, 76, 4, 9, 80, 4, 9, 84, 4, 9,
        88, 4, 9, 90, 5, 12, 110, 5, 13, 114, 6, 15,
        132, 6, 16, 160, 8, 22, 168, 8, 23, 176, 8, 23 },
    // leafSize 23
    { 48, 3, 5, 51, 3, 5, 54, 3, 5, 57, 3, 6,
        60, 3, 6, 63, 3, 6, 66, 3, 6, 69, 3, 6,
        72, 4, 9, 76, 4, 9, 80, 4, 9, 84, 4, 9,
        92, 4, 9, 120, 6, 16, 126, 6, 16, 132, 6, 16,
        138, 6, 16, 168, 8, 23, 184, 8, 23 },
    // leafSize 24
    { 51, 3, 5, 54, 3, 5, 57, 3, 6, 60, 3, 6,
        63, 3, 6, 66, 3, 6, 69, 3, 6, 72, 3, 6,
        76, 4, 9, 80, 4, 9, 84, 4, 9, 92, 4, 9,
        96, 4, 9, 126, 6, 16, 132, 6, 16, 138, 6, 16,
        144, 6, 16, 184, 8, 23, 192, 8, 23 },
    // leafSize 25
    { 54, 3, 5, 57, 3, 6, 60, 3, 6, 63, 3, 6,
        66, 3, 6, 69, 3, 6, 72, 3, 6, 75, 3, 6,
        80, 4, 9, 84, 4, 9, 92, 4, 9, 100, 4, 9,
        132, 6, 16, 138, 6, 16, 144, 6, 16, 150, 6, 16,
        184, 8, 23, 200, 8, 24 },
    // leafSize 26
    { 54, 3, 5, 57, 3, 6, 60, 3, 6, 63, 3, 6,
        66, 3, 6, 69, 3, 6, 72, 3, 6, 75, 3, 6,
        78, 3, 6, 80, 4, 9, 84, 4, 9, 92, 4, 9,
        104, 4, 9, 132, 6, 16, 138, 6, 16, 144, 6, 16,
        150, 6, 16, 156, 6, 17, 200, 8, 24, 208, 8, 24 },
    // leafSize 27
    { 57, 3, 6, 60, 3, 6, 63, 3, 6, 66, 3, 6,
        69, 3, 6, 72, 3, 6, 75, 3, 6, 78, 3, 6,
        81, 3, 6, 84, 4, 9, 92, 4, 9, 108, 4, 10,
        138, 6, 16, 144, 6, 16, 150, 6, 16, 156, 6, 17,
        162, 6, 17, 200, 8, 24, 208, 8, 24, 216, 8, 24 },
    // leafSize 28
    { 60, 3, 6, 63, 3, 6, 66, 3, 6, 69, 3, 6,
        72, 3, 6, 75, 3, 6, 78, 3, 6, 81, 3, 6,
        84, 3, 6, 92, 4, 9, 112, 4, 10, 144, 6, 16,
        150, 6, 16, 156, 6, 17, 162, 6, 17, 168, 6, 17 },
    // leafSize 29
    { 60, 3, 6, 63, 3, 6, 66, 3, 6, 69, 3, 6,
        72, 3, 6, 75, 3, 6, 78, 3, 6, 81, 3, 6,
        84, 3, 6, 87, 3, 6, 92, 4, 9, 116, 4, 10,
        125, 5, 13, 145, 5, 13, 182, 7, 20, 189, 7, 20,
        196, 7, 20, 232, 8, 24, 297, 11, 35 },
    // leafSize 30
    { 63, 3, 6, 66, 3, 6, 69, 3, 6, 72, 3, 6,
        75, 3, 6, 78, 3, 6, 81, 3, 6, 84, 3, 6,
        90, 3, 6, 92, 4, 9, 120, 4, 10, 156, 6, 17,
        162, 6, 17, 168, 6, 17, 180, 6, 17, 280, 10, 31 },
    // leafSize 31
    { 66, 3, 6, 69, 3, 6, 72, 3, 6, 75, 3, 6,
        78, 3, 6, 81, 3, 6, 84, 3, 6, 90, 3, 6,
        93, 3, 6, 130, 5, 13, 135, 5, 13, 140, 5, 13,
        145, 5, 13, 150, 5, 13, 155, 5, 14, 189, 7, 20,
        196, 7, 20, 210, 7, 21, 248, 8, 25 },
    // leafSize 32
    { 69, 3, 6, 72, 3, 6, 75, 3, 6, 78, 3, 6,
        81, 3, 6, 84, 3, 6, 90, 3, 6, 96, 3, 6,
        130, 5, 13, 135, 5, 13, 140, 5, 13, 145, 5, 13,
        150, 5, 13, 155, 5, 14, 160, 5, 14, 196, 7, 20,
        210, 7, 21, 248, 8, 25, 256, 8, 25 }
    };

    /**
     * When splitting a set evenly into two subsets, the minimum size of the
     * set where k = array index should be used for the Rice parameter k.
     */
    private static final int[] RICE_SPLIT_2 = { 0, 4, 14, 50, 188, 726, 2858,
            11346, 45214, 180512 };

//    private static final int CACHE_SPLITS = 4 * 1024;
    private static final int CACHE_SPLITS = 10 * 1024;

    private final int leafSize;
    private final int loadFactor;

    private final int[] splits = new int[CACHE_SPLITS];
    private final int[] rice = new int[CACHE_SPLITS];

    /**
     * Constructor for settings.
     *
     * @param leafSize
     * @param loadFactor the load factor, at most 65536
     */
    public Settings(int leafSize, int loadFactor) {
        if (IMPROVED_SPLIT_RULES) {
            if (leafSize < 1 || leafSize > 32) {
                throw new IllegalArgumentException("leafSize out of range: " + leafSize);
            }
        } else {
        if (leafSize < 1 || leafSize > 25) {
            throw new IllegalArgumentException("leafSize out of range: " + leafSize);
        }
        }
        if (loadFactor < 2 || loadFactor > 65536) {
            throw new IllegalArgumentException("loadFactor out of range: " + loadFactor);
        }
        this.leafSize = leafSize;
        this.loadFactor = loadFactor;
        if (IMPROVED_SPLIT_RULES) {
            int[] splitRules = SPLIT_RULES[leafSize];
            for (int i = 0; i < splitRules.length; i += 3) {
                int size = splitRules[i];
                splits[size] = splitRules[i + 1];
                rice[size] = splitRules[i + 2];
            }
            for (int i = 0; i <= leafSize; i++) {
                splits[i] = i;
                rice[i] = RICE_LEAF[i];
            }
            int last = leafSize;
            for (int i = leafSize; i < CACHE_SPLITS; i++) {
                if (splits[i] != 0) {
                    last = i;
                } else {
                    splits[i] = -last;
                    rice[i] = calcRiceParamSplitByTwo(i);
                }
            }
        } else {
        for (int i = 0; i < CACHE_SPLITS; i++) {
            splits[i] = calcSplit(i, leafSize);
            rice[i] = calcGolombRiceShift(i, leafSize);
        }
        }
    }

    public int getMaxBucketSize() {
        // return loadFactor * 20;
        return 200 + loadFactor * 15 / 10;
    }

    private static int calcRiceParamSplitByTwo(int size) {
        // this will throw an exception for sizes >= 180172
        for (int i = 0;; i++) {
            if (RICE_SPLIT_2[i] > size) {
                return i - 1;
            }
        }
    }

    static int calcNextSplit(int factor) {
        return Math.max(2,  (int) (1.5 + factor * .35));
    }

    private static int calcSplit(int size, int leafSize) {
        for (int x = leafSize, f = x;;) {
            if (size < x) {
                return -(x / f);
            } else if (size == x) {
                return f;
            }
            f = calcNextSplit(f);
            x *= f;
        }
    }

    public long getEstimatedBits(long size) {
        return ESTIMATED_SPACE[leafSize] * size / 1000;
    }

    public int getSplit(int size) {
        if (size < CACHE_SPLITS) {
            return splits[size];
        }
        if (IMPROVED_SPLIT_RULES) {
            throw new IllegalArgumentException();
        }
        return calcSplit(size, leafSize);
    }

    private static int calcGolombRiceShift(int size, int leafSize) {
        if (size <= leafSize) {
            return RICE_LEAF[size];
        }
        int index = 0;
        for (int x = leafSize, f = x;;) {
            f = Settings.calcNextSplit(f);
            if (f <= 2) {
                break;
            }
            x *= f;
            if (size < x) {
                break;
            } else if (size == x) {
                return RICE_SPLIT_MORE[leafSize][index];
            }
            index++;
        }
        return calcRiceParamSplitByTwo(size);
    }

    public int getGolombRiceShift(int size) {
        if (size < CACHE_SPLITS) {
            return rice[size];
        }
        return calcGolombRiceShift(size, leafSize);
    }

    public static boolean needNewUniversalHashIndex(long index) {
        return (index & (SUPPLEMENTAL_HASH_CALLS - 1)) == 0;
    }

    public static long getUniversalHashIndex(long index) {
        return index >>> SUPPLEMENTAL_HASH_SHIFT;
    }

    public int getLeafSize() {
        return leafSize;
    }

    public int getLoadFactor() {
        return loadFactor;
    }

    public static int supplementalHash(long hash, long index) {
        // it would be better to use long,
        // but with some processors, 32-bit multiplication
        // seem to be much faster
        // (about 1200 ms for 32 bit, about 2000 ms for 64 bit)
        int x = (int) (Long.rotateLeft(hash, (int) index) ^ index);
        x = ((x >>> 16) ^ x) * 0x45d9f3b;
        x = ((x >>> 16) ^ x) * 0x45d9f3b;
        x = (x >>> 16) ^ x;
        return x;
    }

    public static long supplementalHashLong(long hash, long index) {
        long x = hash ^ index;
        // from http://zimbry.blogspot.it/2011/09/better-bit-mixing-improving-on.html
        // also used in it.unimi.dsi.fastutil
        x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L;
        x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
        x = x ^ (x >>> 31);
        return x;
    }

    public static int getBucketCount(long size, int loadFactor) {
        return (int) ((size + loadFactor - 1) / loadFactor);
    }

    public static int reduce(int hash, int n) {
        // http://lemire.me/blog/2016/06/27/a-fast-alternative-to-the-modulo-reduction/
        return (int) (((hash & 0xffffffffL) * n) >>> 32);
    }

}