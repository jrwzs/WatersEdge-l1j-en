-- IMPORTANT: THIS UPDATE IS NOT YET FINISHED, DONT USE!

-- Queries that are pending an update should be placed here. This allows them
-- to be verified as working together. Once complete, the -pending suffix will
-- be removed.

-- update 62

-- make lasta non rtele
UPDATE mapids SET teleportable = 0 WHERE mapid > 490 AND mapid < 497;

-- make chaos agro players
UPDATE npc SET agro=1, agrososc = 1, agrocoi = 1 where npcid = 45625;

-- minor adjustment on lasta ele room spawn counts
UPDATE spawnlist SET count = 8 WHERE mapid = 464 AND count = 10;

-- remove incorrect salamander spawn in forrest of mirror
DELETE FROM spawnlist WHERE id = 9756;

-- girtas fix
update npc set passispeed = 0, alt_atk_speed = 2200, ranged=10 where npcid = 81163;
INSERT INTO `spawnlist_boss` VALUES ('211', 'Girtas', 'Girtas', '1', '81163', '0', '32868', '32862', '0', '0', '0', '0', '0', '0', '5', '535', '1', '0', '0', '0', '100');