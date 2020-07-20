# Changelog

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