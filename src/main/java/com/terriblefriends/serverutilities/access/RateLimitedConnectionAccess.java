package com.terriblefriends.serverutilities.access;

public interface RateLimitedConnectionAccess {
    void increaseActionPacketsReceived();
}
