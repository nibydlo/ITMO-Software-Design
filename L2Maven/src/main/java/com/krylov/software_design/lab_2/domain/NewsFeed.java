package com.krylov.software_design.lab_2.domain;

import java.time.Instant;
import java.util.Objects;

public class NewsFeed {

    private final long id;
    private final Instant instant;

    public NewsFeed(long id, Instant instant) {
        this.id = id;
        this.instant = instant;
    }

    public long getId() {
        return id;
    }

    public Instant getInstant() {
        return instant;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, instant);
    }

    @Override
    public String toString() {
        return getId() + " " + getInstant().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NewsFeed that = (NewsFeed) o;
        return id == that.id && instant.equals(that.instant);
    }
}
