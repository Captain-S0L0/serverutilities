package com.terriblefriends.serverutilities.access;

import com.mojang.authlib.GameProfile;

public interface PlayerManagerAccess {
    void addToOperatorsWithPower(GameProfile profile, int power);
}
