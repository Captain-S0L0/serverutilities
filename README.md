# serverutilities
a collection of exploit fixes, commands, and other things for fabric mc


This mod is not really intended to be an uber maintained project, it is more of a passion project for my minecraft server, Terrible Friends. Of course, if you have any bugs, PLEASE REPORT THEM!!!

This mod includes several features for the purposes of easing administration or general gameplay.


**ADMIN COMMANDS (operator power required in parenthesis):**
  - (2) /fly (player) (true/false) : set the mayFly ability
  - (2) /flyspeed (player) (float) : set the flyspeed (default 0.05)
  - (2) /god (player) (true/false) : set the invulnerable ability
  - (2) /setweathertime (rain/thunder/clear) (int) : set one of the internal weather counters
  - (4) /opwp (player) (1-4) : op a player with a custom operator power
  
  
**GENERAL COMMANDS (no permissions needed):**
  - /getweather : print all the internal weather counters
  - /setscoreboard (scoreboard) : set the sidebar scoreboard. Useful with a datapack that makes scoreboards for all statistics.
  - /suicide : kill the executor
  - /discord : prints a clickable discord link in chat, sadly hardcoded for now
  - /dynmaplink : prints a clickable dynmap link in chat, sadly hardcoded for now

  
**Anti-dupes / exploits:**
  - Prevents item transfers if the donkey is not loaded
  - Prevents item transfers if the shulker box is not placed
  - Prevents taking damage if player is logged out (elytra + damage fireworks + disconnect)
  - Prevents void trading generating experience orbs (does not patch void trading)
  - Prevents general packet nasties with a player action packet rate limit (configurable via gamerule, but will only work if you set a general rate limit in server.properties. I recommend a rate limit of 50.)


**Gamerules (default in parenthesis)**
  - (1) "horseSpeedMultiplier" : changes max speed horses can spawn with
  - (1) "minecartSpeedMultiplier" : currently does nothing / is broken. Remnant of experiements I didn't ever remove.
  - (false) "endDisabled" : if true, will prevent eyes from being placed into end portals. If you have a seed with a 12 eye portal / portals already opened, then you're out of luck. I just use this to prevent people from speedrunning the dragon before the server-wide event for it.
  - (false) "throwableFireballs" : if true, fire charges can be used in the air / on blocks to spawn fireballs. If shift is held, vanilla function will still work.
  - (1) "throwableFireballPower" : sets the explosion power of thrown fireballs
  - (20) "actionRateLimit" : set the action rate limit
  - (false) "ghostAdventureMode" : toggle ghost adventure mode
  
  
**"Ghost" Adventure Mode Modifiers:**
  
  "Ghost" mode is enabled with the gamerule "ghostAdventureMode". This basically removes any ability the adventure mode gamemode has to interact with the world. I used this for my graylisting system way back when, and left it in if I ever needed it again.
  
  - Defaults with invulnerable:1b
  - Cannot dispense items from dispenser onto player
  - Cannot pick up item entities
  - Cannot interact with blocks
  - Cannot interact with entities
  - Cannot attack entities
  - Cannot drop items
  
**Other / Misc changes:**
  - If a horse is loaded with a higher speed than allowed, the speed is reduced to the max (CANNOT REVERT)
  - Any word in chat starting with "http://" or "https://" will be clickable
  - The rate limit, instead of being checked every second, is checked every game tick to prevent nastyness asap. May be more laggy, but I don't give a shit.
  - There are now 2 rate limits, an "action" rate limit and a "general" rate limit. The action rate limit controls things like movement, look, block place, and interact packets. The general rate limit is the total of all packets. These are seperate because many nasty actions rely on a lot of player action packets, but setting a general rate limit low enough to prevent these nasty actions also kicks vanilla clients. Ergo, by splitting them, things like fast crafting still work while preventing things like the nocom crash exploit.
  

**TODO**
  - make a god damn config file for things like /discord and /dynmaplink.
  - allow the action rate limit to be configured in server.properties instead of gamerules
  - store horse speeds when they are reduced so they can be restored later
  - some general better anticheat things, like antifly
  - prevent explosions in spawn
  - prevent nether portal generation in spawn
