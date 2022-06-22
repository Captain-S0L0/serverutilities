package com.terriblefriends.serverutilities.command;

import net.minecraft.text.Text;

import java.util.UUID;

public class VoteBanInstance {
    public UUID creator;
    public UUID target;
    public boolean confirmed;
    public int timer;
    public Text reason;

    VoteBanInstance(UUID uuid_creator, UUID uuid_target, Text text) {
        this.creator = uuid_creator;
        this.target = uuid_target;
        this.timer = 20*30;
        this.confirmed = false;
        this.reason = text;
    }
}
