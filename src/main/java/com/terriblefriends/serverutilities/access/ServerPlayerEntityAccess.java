package com.terriblefriends.serverutilities.access;

public interface ServerPlayerEntityAccess {
    boolean isAdventure();

    void setUniqueScoreboardName(String string);
    String getUniqueScoreboardName();
}
