package com.krylov.software_design.lab_2.domain;

public class HashTagStat {

    private final String hashTag;
    private final long count;
    private final int hour;

    public HashTagStat(String hashTag, long count, int hour) {
        this.hashTag = hashTag;
        this.count = count;
        this.hour = hour;
    }

    public String getHashTag() {
        return hashTag;
    }

    public long getCount() {
        return count;
    }

    public int getHour() {
        return hour;
    }

    @Override
    public String toString() {
        return hashTag + " " + count + " " + hour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HashTagStat that = (HashTagStat) o;
        return hour == that.hour &&
                count == that.count &&
                hashTag.equals(that.hashTag);
    }
}
