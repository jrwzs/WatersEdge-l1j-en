-- update 62

-- double devils blood drops
UPDATE droplist SET chance = chance*2 WHERE itemid = 40031;

-- make lasta non rtele
UPDATE mapids SET teleportable = 0 WHERE mapid > 490 AND mapid < 497;

-- make chaos agro players
UPDATE npc SET agro=1, agrososc = 1, agrocoi = 1 where npcid = 45625;

-- minor adjustment on lasta ele room spawn counts
UPDATE spawnlist SET count = 8 WHERE mapid = 464 AND count = 10;

-- remove incorrect salamander spawn in forrest of mirror
DELETE FROM spawnlist WHERE id = 9756;