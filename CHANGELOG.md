# Changelog

## 4.6.3
- Fixed block blacklist/whitelist not working on versions 1.13 and later

## 4.6.2
- Fixed Player drops disappearing when player is killed by another player and legacy drop detection is enabled
- Fixed legacy drop detection throwing errors on old Spigot versions

## 4.6.1
- Fixed StackOverflow when mining blocks if inventory is full

## 4.6.0
- Added support for all properly coded custom drop plugins
- Added support for plugins that create custom drops in ominous ways (toggleable)
- Added possibility to use permissions per tool
- Fixed "always-enabled" not working when the player disabled Drop2Inv before this setting was enabled

## 4.5.2
- Removed forgotten debug messages (sorry about that :( )

## 4.5.1
- Fixed Exception in MC 1.8
- Fixed Exception when attempting to drop air
- Changed event priority to support custom drops by other plugin

## 4.5.0
- Added limited compatibility for versions 1.8-1.12.2

## 4.4.1
- Auto-Condense now also works for gold/iron nuggets, bone meal and slime balls. It also works when killing mobs now.
- Fixed possible NullPointerException when Auto-Condense is enabled

## 4.4.0
- Added option to auto-condense mined blocks (9 diamonds -> diamond block, etc), disabled by default
  - See config option "auto-condense"

## 4.3.3
- Fixed compatibility with BannerBoard

## 4.3.2
- Item frames and their contents now get dropped directly into the inventory

## 4.3.1
- Fixed paintings not being destroyed when clicking on them

## 4.3.0
- Added option to force Drop2Inventory being enabled for every player (default: false). I suggest to enable this if you use Drop2Inventory for reducing server lag.
- Added Chinese (Simplified) translation

## 4.2.1
- Fixed debug message being displayed on every BlockBreakEvent

## 4.2.0
- Added mending when auto-collecting XP
  - Behaves exactly like vanilla: in 1.16+ it only tries to repair damaged items, in 1.15 and below it picks a random equipped item to be repaired
- Fixed items from a broken furnace being dropped on the ground
- Fixed overflow items not dropping when killing another player

## 4.1.1
- Fixed exception in MC < 1.14

## 4.1.0
- Shulkerboxes will now preserve their contents when broken
- Cacti, sugar cane, kelp, chorus trees etc. will drop the whole plant

## 4.0.0
- Changed detection of block drops. It should now always drop exactly what vanilla would have dropped.
- Only compatible with Spigot 1.13 and later

## 3.9.5
- Fixed prismarine dropping more than one block when using fortune enchantment

## 3.9.4
- Fixed beds not being dropped when breaking the lower half
- Added Spanish translation
- Updated Turkish translation
- Updated API to 1.16.1

## 3.9.3
- Fixed disabled-mobs list not working
- Fixed NullPointerException when killing a mob 

## 3.9.2
- This REALLY fixes the NullPointerException mentioned in 3.9.1

## 3.9.1
- Possible Fix for NullPointerException when breaking a block

## 3.9.0
- Jobs Reborn and mcMMO should now be able to detect blocks breaks and mob kills
- Fixed some items getting random duration NBT tags so they were not stackable
- Improved Update Checker
- Converted project to maven