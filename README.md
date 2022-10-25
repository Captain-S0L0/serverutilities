# serverutilities
a collection of exploit fixes, commands, and other things for fabric mc

DISCLAIMER: THIS MOD IS LIKELY GOING TO HAVE ISSUES. I AM NOT RESPONSIBLE FOR YOU BRICKING YOUR WORLD FROM USING IT. MAKE A BACKUP, PLEASE.

This mod is not really intended to be an uber maintained project, it is more of a passion project for my minecraft server, Terrible Friends. Of course, if you have any bugs, PLEASE REPORT THEM!!!

This mod includes several features for the purposes of easing administration or general gameplay.


**ADMIN COMMANDS (operator power required in parenthesis):**
  - (2) /fly (player) (true/false) : set the mayFly ability
  - (2) /flyspeed (player) (float) : set the flyspeed (default 0.05)
  - (2) /god (player) (true/false) : set the invulnerable ability
  - (4) /opwp (player) (1-4) : op a player with a custom operator power
  - (2) /setweathertime (rain/thunder/clear) (int) : set one of the internal weather counters
  - (2) /shadow : creates a shadow item from the item in your hand. For testing purposes
  - (3) /tempban (player) (days) (reason) : bans player for set amount of days
  - (3) /whereis (player) : gets last/current location of player. Can be online or offline
  
  
**GENERAL COMMANDS (no permissions needed):**
  - /discord : prints a clickable discord link in chat, configurable in config
  - /dynmaplink : prints a clickable dynmap link in chat, configurable in config
  - /getweather : print all the internal weather counters
  - /setscoreboard (scoreboard) : set the sidebar scoreboard. Useful with a datapack that makes scoreboards for all statistics.
  - /suicide : kill the executor
  - /vote : prints links to voting sites, configurable in config
  - /voteban (player) (reason) : initiates a vote ban to temp ban a player for 24 hours. Customizable player percentage required, customizable minimum vote requirement via gamerule

  
**Anti-dupes / exploits:**
  - Prevents item transfers in unloaded / removed blocks / entities
  - Prevents taking damage if player is logged out (elytra + damage fireworks + disconnect)
  - Prevents void trading generating experience orbs (does not patch void trading, because void trading is awesome)
  - Fixes nocom crash
  - Prevents a *lot* of dupes with shadowed items, including unloading to disk, transferStack, droppers, etc.
  - Fixes allays dropping items and teleporting in the same tick
  - Fixes lectern transferStack crash
  - Fixes general item duplication via update suppression and item frames (if you retroactively allow update suppression, of course)

**Gamerules (default in parenthesis)**
  - (false) "ghostAdventureMode" : toggle ghost adventure mode
  - (false) "endDisabled" : if true, will prevent eyes from being placed into end portals. If you have a seed with a 12 eye portal / portals already opened, then you're out of luck. I just use this to prevent people from speedrunning the dragon before the server-wide event for it.  
  - (1) "horseSpeedMultiplier" : changes max speed horses can spawn with
  - (false) "lobotomizePhantoms" :  if true, prevents phantoms from attacking  
  - (1) "minecartSpeedMultiplier" : currently does nothing / is broken. Remnant of experiements I didn't ever remove.
  - (false) "throwableFireballs" : if true, fire charges can be used in the air / on blocks to spawn fireballs. If shift is held, vanilla function will still work.
  - (1) "throwableFireballPower" : sets the explosion power of thrown fireballs
  - (66) "voteBanPercentage" : sets the percentage required for a vote ban to pass
  - (3) "voteBanMinimum" : sets the minimum amount of votes for a vote ban to pass
  
  
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
  - Fixes bees getting stuck in hives in the nether / end
  - Prevents explosions in spawn protection
  - Prevents survival mode players from changing spawners with spawn eggs
  - Furnaces named "Shadow" will take their input and place a shadowed item into the output slot
  

**TODO**
  - ~~make a god damn config file for things like /discord and /dynmaplink.~~ DONE!
  - fix block duplication with update suppression
  - store horse speeds when they are reduced so they can be restored later
  - some general better anticheat things, like antifly
  - prevent nether portal generation in spawn
  - better shadow item creation than the furnace method


**Credits (aka people's code I've used / taken inspiration from / adapted / etc.)**
- https://github.com/Chropal/nocom_patch/, for a much better patch than the one I made that I "borrowed".
- https://github.com/19MisterX98/SeedcrackerX/, for the config file lol

**Using my code**

I really don't mind if you use my code as long as you give credit. Most of my code is dogshit, so feel free to improve it yourself, or compile this mod differently to only include the features that you want / need / fit your purposes.
