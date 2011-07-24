/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model.Instance;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.encryptions.IdFactory;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.L1World;
import l1j.server.server.model.L1DragonSlayer;
import l1j.server.server.model.skill.L1BuffUtil;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;
import static l1j.server.server.model.skill.L1SkillId.*;

public class L1MonsterInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1MonsterInstance.class
			.getName());

	private static Random _random = new Random();

	private boolean _storeDroped;

	@Override
	public void onItemUse() {
		if (!isActived() && _target != null) {
			useItem(USEITEM_HASTE, 40); 

			if (getNpcTemplate().is_doppel() && _target instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) _target;
				setName(_target.getName());
				setNameId(_target.getName());
				setTitle(_target.getTitle());
				setTempLawful(_target.getLawful());
				setTempCharGfx(targetPc.getClassId());
				setGfxId(targetPc.getClassId());
				setPassispeed(SprTable.getInstance().getMoveSpeed(getTempCharGfx(),
                        getStatus()));
                setAtkspeed(SprTable.getInstance().getAttackSpeed(getTempCharGfx(),
                        getStatus() + 1)); 
				for (L1PcInstance pc : L1World.getInstance()
						.getRecognizePlayer(this)) {
					pc.sendPackets(new S_RemoveObject(this));
					pc.removeKnownObject(this);
					pc.updateObject();
				}
			}
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) { 
			if (this.getInt() > Config.MONSTERPOTIONINTUSE) // only mobs with > 13 int will use pots
			{
				useItem(USEITEM_HEAL, 30); 
			}
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		//TRICIDTODO: wont compile after merge
		//perceivedFrom.sendPackets(new S_Light(this.getId(), getLightSize()));
		perceivedFrom.addKnownObject(this);
		if (0 < getCurrentHp()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK
					|| getHiddenStatus() == HIDDEN_STATUS_ICE) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Hide));
			} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Moveup));
			}
			perceivedFrom.sendPackets(new S_NPCPack(this));
			onNpcAI(); 
			if (getBraveSpeed() == 1) {
				perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
			}
		} else {
			perceivedFrom.sendPackets(new S_NPCPack(this));
		}
	}

	public static int[][] _classGfxId = { { 0, 1 }, { 48, 61 }, { 37, 138 },
			{ 734, 1186 }, { 2786, 2796 } };

	@Override
	public void searchTarget() {
		L1PcInstance targetPlayer = null;
		
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (!getNpcTemplate().is_agro() && !getNpcTemplate().is_agrososc()
					&& getNpcTemplate().is_agrogfxid1() < 0
					&& getNpcTemplate().is_agrogfxid2() < 0
					&& !getNpcTemplate().is_agrochao()) {
				return; 
			}

			// NOTE: Don't remove non-aggro to shopmode
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()
					|| pc.isMonitor() || pc.isGhost() || pc.isPrivateShop()) {
				continue;
			}

			int mapId = getMapId();
			if (mapId == 88 || mapId == 98 || mapId == 92 || mapId == 91
					|| mapId == 95) {
				if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) {
					targetPlayer = pc;
					break;
				}
			}

			if (get_npcId() == 45600){
				if (pc.isCrown() || pc.isDarkelf()
						|| pc.getTempCharGfx() != pc.getClassId()) { 
					targetPlayer = pc;
					break;
				}
			}

			if ((getNpcTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1)
					|| (getNpcTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1)) {
				continue;
			}

			if (pc.getTempCharGfx() == 6034 && getNpcTemplate().getKarma() < 0 
					|| pc.getTempCharGfx() == 6035 && getNpcTemplate().getKarma() > 0
					|| pc.getTempCharGfx() == 6035 && getNpcTemplate().get_npcId() == 46070
					|| pc.getTempCharGfx() == 6035 && getNpcTemplate().get_npcId() == 46072) {
				continue;
			}

			if (!getNpcTemplate().is_agro() && !getNpcTemplate().is_agrososc()
					&& getNpcTemplate().is_agrogfxid1() < 0
					&& getNpcTemplate().is_agrogfxid2() < 0) {
				if (pc.getLawful() < -1000) {
					targetPlayer = pc;
					break;
				}
				continue;
			}

			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) { 
				if (pc.hasSkillEffect(67)) {
					if (getNpcTemplate().is_agrososc()) { 
						targetPlayer = pc;
						break;
					}
				} else if (getNpcTemplate().is_agro()) { 
					targetPlayer = pc;
					break;
				}
				if (getNpcTemplate().is_agrogfxid1() >= 0
						&& getNpcTemplate().is_agrogfxid1() <= 4) {
					if (_classGfxId[getNpcTemplate().is_agrogfxid1()][0] == pc
							.getTempCharGfx()
							|| _classGfxId[getNpcTemplate().is_agrogfxid1()][1] == pc
									.getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid1()) { 
					targetPlayer = pc;
					break;
				}
				if (getNpcTemplate().is_agrogfxid2() >= 0
						&& getNpcTemplate().is_agrogfxid2() <= 4) {
					if (_classGfxId[getNpcTemplate().is_agrogfxid2()][0] == pc
							.getTempCharGfx()
							|| _classGfxId[getNpcTemplate().is_agrogfxid2()][1] == pc
									.getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid2()) { 
					targetPlayer = pc;
					break;
				}
			}
		}
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
	}
	@Override
	public void setLink(L1Character cha) {
		if (cha != null && _hateList.isEmpty()) {
			_hateList.add(cha, 0);
			checkTarget();
		}
	}

	public L1MonsterInstance(L1Npc template) {
		super(template);
		_storeDroped = false;
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			getZoneType();
			return;
		}
		if (!_storeDroped)
		{
			DropTable.getInstance().setDrop(this, getInventory());
			getInventory().shuffle();
			_storeDroped = true;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		String htmlid = null;
		String[] htmldata = null;


			if (htmlid != null) { 
				if (htmldata != null) { 
					pc.sendPackets(new S_NPCTalkReturn(objid, htmlid,
							htmldata));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
				}
			} else {
				if (pc.getLawful() < -1000) { 
					pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		}
	
	@Override
	public void onAction(L1PcInstance pc) {
		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.calcStaffOfMana();
				attack.addPcPoisonAttack(pc, this);
				attack.addChaserAttack();
			}
			attack.action();
			attack.commit();
		}
	}

	@Override
	public void ReceiveManaDamage(L1Character attacker, int mpDamage) { 
		if (mpDamage > 0 && !isDead()) {
			setHate(attacker, mpDamage);

			onNpcAI();

			if (attacker instanceof L1PcInstance) { 
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.get_family());
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) { 
		if (getCurrentHp() > 0 && !isDead()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK
					|| getHiddenStatus() == HIDDEN_STATUS_FLY) {
				return;
			}
			if (damage >= 0) {
				if (!(attacker instanceof L1EffectInstance)) {
					setHate(attacker, damage);
				}
			}
			if (damage > 0) {
				removeSkillEffect(FOG_OF_SLEEPING);
			}

			onNpcAI();

			if (attacker instanceof L1PcInstance) { 
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.get_family());
			}

			if (attacker instanceof L1PcInstance && damage > 0) {
				L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);

				if (getNpcTemplate().get_npcId() == 45681
						|| getNpcTemplate().get_npcId() == 45682
						|| getNpcTemplate().get_npcId() == 45683 
						|| getNpcTemplate().get_npcId() == 45684) 
				{
					recall(player);
				}
			}

			int newHp = getCurrentHp() - damage;
			if (newHp <= 0 && !isDead()) {
				int transformId = getNpcTemplate().getTransformId();
				if (transformId == -1) {
					if (getPortalNumber() != -1) {
						if (getNpcTemplate().get_npcId() == 97006 || getNpcTemplate().get_npcId() == 97044) {
							L1DragonSlayer.getInstance().startDragonSlayer2rd(getPortalNumber());
						} else if (getNpcTemplate().get_npcId() == 97007 || getNpcTemplate().get_npcId() == 97045) {
							L1DragonSlayer.getInstance().startDragonSlayer3rd(getPortalNumber());
						} else if (getNpcTemplate().get_npcId() == 97008 || getNpcTemplate().get_npcId() == 97046) {
							bloodstain();
							L1DragonSlayer.getInstance().endDragonSlayer(getPortalNumber());
						}
					}
					setCurrentHpDirect(0);
					setDead(true);
					setStatus(ActionCodes.ACTION_Die);
					openDoorWhenNpcDied(this);
					openDragonDoorWhenNpcDied(this);
					Death death = new Death(attacker);
					GeneralThreadPool.getInstance().execute(death);
					// Death(attacker);
					if (getPortalNumber() == -1
							&& (getNpcTemplate().get_npcId() == 97006 || getNpcTemplate().get_npcId() == 97007
								|| getNpcTemplate().get_npcId() == 97044 || getNpcTemplate().get_npcId() == 97045)) {
						doNextDragonStep(attacker, getNpcTemplate().get_npcId());
					}
				} else {
// distributeExpDropKarma(attacker);
					transform(transformId);
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
				hide();
			}
		} else if (!isDead()) { 
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			Death death = new Death(attacker);
			GeneralThreadPool.getInstance().execute(death);
			// Death(attacker);
		}
	}

	private static void openDoorWhenNpcDied(L1NpcInstance npc) {
		int[] npcId = { 46143, 46144, 46145, 46146, 46147, 46148,
				46149, 46150, 46151, 46152};
		int[] doorId = { 5001, 5002, 5003, 5004, 5005, 5006,
				5007, 5008, 5009, 5010};

		for (int i = 0; i < npcId.length; i++) {
			if (npc.getNpcTemplate().get_npcId() == npcId[i]) {
				openDoorInCrystalCave(doorId[i]);
				break;
			}
		}
	}

	private static void openDragonDoorWhenNpcDied(L1NpcInstance npc) {
		int[] npcId = { 97011, 97012, 97013 };
		int[][][] doorId = {
			{ {7001, 7004, 7007, 7010}, {7013, 7016, 7019, 7022}, {7025, 7028, 7031, 7034},
				{7037, 7040, 7043, 7046}, {7049, 7052, 7055, 7058}, {7061, 7064, 7067, 7070} },
			{ {7002, 7005, 7008, 7011}, {7014, 7017, 7020, 7023}, {7026, 7029, 7032, 7035},
				{7038, 7041, 7044, 7047}, {7050, 7053, 7056, 7059}, {7062, 7065, 7068, 7071} },
			{ {7003, 7006, 7009, 7012}, {7015, 7018, 7021, 7024}, {7027, 7030, 7033, 7036},
				{7039, 7042, 7045, 7048}, {7051, 7054, 7057, 7060}, {7063, 7066, 7069, 7072} }
		};
		for (int i = 0; i < npcId.length; i++) {
			if (npc.getNpcTemplate().get_npcId() == npcId[i]) {
				for (int j = 0; j < doorId[i].length; j++) {
					if (j == (npc.getMapId() - 1005)) {
						for (int k = 0; k < doorId[i][j].length; k++) {
							if (k == npc.getPortalNumber()) {
								openDoorInCrystalCave(doorId[i][j][k]);
								break;
							}
						}
					}
				}
			}
		}
	}

	private static void openDoorInCrystalCave(int doorId) {
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1DoorInstance) {
				L1DoorInstance door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					door.open();
				}
			}
		}
	}

	/**
	 * 
	 * @param pc
	 */
	private void recall(L1PcInstance pc) {
		if (getMapId() != pc.getMapId()) {
			return;
		}
		if (getLocation().getTileLineDistance(pc.getLocation()) > 4) {
			for (int count = 0; count < 10; count++) {
				L1Location newLoc = getLocation().randomLocation(3, 4, false);
				if (glanceCheck(newLoc.getX(), newLoc.getY())) {
					L1Teleport.teleport(pc, newLoc.getX(), newLoc.getY(),
							getMapId(), 5, true);
					break;
				}
			}
		}
	}

	@Override
	public void setCurrentHp(int i) {
		int currentHp = i;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
		setCurrentHpDirect(currentHp);

		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
	}

	@Override
	public void setCurrentMp(int i) {
		int currentMp = i;
		if (currentMp >= getMaxMp()) {
			currentMp = getMaxMp();
		}
		setCurrentMpDirect(currentMp);

		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}

	class Death implements Runnable {
		L1Character _lastAttacker;

		public Death(L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			setDeathProcessing(true);
			setCurrentHpDirect(0);
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);

			getMap().setPassable(getLocation(), true);

			broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));

			startChat(CHAT_TIMING_DEAD);

			distributeExpDropKarma(_lastAttacker);
			giveUbSeal();

			setDeathProcessing(false);

			setExp(0);
			setKarma(0);
			allTargetClear();

			startDeleteTimer();
		}
	}

	private void distributeExpDropKarma(L1Character lastAttacker) {
		if (lastAttacker == null) {
			return;
		}
		L1PcInstance pc = null;
		if (lastAttacker instanceof L1PcInstance) {
			pc = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			pc = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			pc = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
		}

		if (pc != null) {
			ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			ArrayList<Integer> hateList = _hateList.toHateArrayList();
			int exp = getExp();
			CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
			//
			if (isDead()) {
				distributeDrop();
				giveKarma(pc);
			}
		} else if (lastAttacker instanceof L1EffectInstance) { //
			ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			ArrayList<Integer> hateList = _hateList.toHateArrayList();
			//
			if (hateList.size() != 0) {
				//
				int maxHate = 0;
				for (int i = hateList.size() - 1; i >= 0; i--) {
					if (maxHate < ((Integer) hateList.get(i))) {
						maxHate = (hateList.get(i));
						lastAttacker = targetList.get(i);
					}
				}
				if (lastAttacker instanceof L1PcInstance) {
					pc = (L1PcInstance) lastAttacker;
				} else if (lastAttacker instanceof L1PetInstance) {
					pc = (L1PcInstance) ((L1PetInstance) lastAttacker)
							.getMaster();
				} else if (lastAttacker instanceof L1SummonInstance) {
					pc = (L1PcInstance) ((L1SummonInstance)
							lastAttacker).getMaster();
				}
				if (pc != null) {
					int exp = getExp();
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);

					if (isDead()) {
						distributeDrop();
						giveKarma(pc);
					}
				}
			}
		}
	}

	private void distributeDrop() {
		ArrayList<L1Character> dropTargetList = _dropHateList
				.toTargetArrayList();
		ArrayList<Integer> dropHateList = _dropHateList.toHateArrayList();
		try {
			int npcId = getNpcTemplate().get_npcId();
			if (npcId != 45640
					|| (npcId == 45640 && getTempCharGfx() == 2332)) { 
				DropTable.getInstance().dropShare(L1MonsterInstance.this,
						dropTargetList, dropHateList);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private void giveKarma(L1PcInstance pc) {
		int karma = getKarma();
		if (karma != 0) {
			int karmaSign = Integer.signum(karma);
			int pcKarmaLevel = pc.getKarmaLevel();
			int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
			//
			if (pcKarmaLevelSign != 0 && karmaSign != pcKarmaLevelSign) {
				karma *= 5;
			}
			//
			pc.addKarma((int) (karma * Config.RATE_KARMA));
		}
	}

	private void giveUbSeal() {
		if (getUbSealCount() != 0) { //
			L1UltimateBattle ub = UBTable.getInstance().getUb(getUbId());
			if (ub != null) {
				for (L1PcInstance pc : ub.getMembersArray()) {
					if (pc != null && !pc.isDead() && !pc.isGhost()) {
						L1ItemInstance item = pc.getInventory()
								.storeItem(41402, getUbSealCount());
						pc.sendPackets(new S_ServerMessage(403, item
								.getLogName())); //
					}
				}
			}
		}
	}

	public boolean is_storeDroped() {
		return _storeDroped;
	}

	public void set_storeDroped(boolean flag) {
		_storeDroped = flag;
	}

	private int _ubSealCount = 0;

	public int getUbSealCount() {
		return _ubSealCount;
	}

	public void setUbSealCount(int i) {
		_ubSealCount = i;
	}

	private int _ubId = 0; // UBID

	public int getUbId() {
		return _ubId;
	}

	public void setUbId(int i) {
		_ubId = i;
	}

	private void hide() {
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 
				|| npcid == 45161 
				|| npcid == 45181
				|| npcid == 45455) {
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Hide));
					setStatus(13);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 45682) { 
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_AntharasHide));
					setStatus(20);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 45067 
				|| npcid == 45264 
				|| npcid == 45452 
				|| npcid == 45090 
				|| npcid == 45321
				|| npcid == 45445) { 
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Moveup));
					setStatus(4);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 45681) { 
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Moveup));
					setStatus(11);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 46107 
				 || npcid == 46108) { 
			if (getMaxHp() / 4 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Hide));
					setStatus(13);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		}
	}

	public void initHide() {
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 
				|| npcid == 45161 
				|| npcid == 45181 
				|| npcid == 45455) { 
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (npcid == 45045 
				|| npcid == 45126 
				|| npcid == 45134 
				|| npcid == 45281) {
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(4);
			}
		} else if (npcid == 45067 
				|| npcid == 45264 
				|| npcid == 45452 
				|| npcid == 45090 
				|| npcid == 45321 
				|| npcid == 45445) { 
			setHiddenStatus(HIDDEN_STATUS_FLY);
			setStatus(4);
		} else if (npcid == 45681) { 
			setHiddenStatus(HIDDEN_STATUS_FLY);
			setStatus(11);
		} else if (npcid == 46107 
				 || npcid == 46108) { 
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (npcid >= 46125 && npcid <= 46128) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
			setStatus(4);
		}
	}

	public void initHideForMinion(L1NpcInstance leader) {

		int npcid = getNpcTemplate().get_npcId();
		if (leader.getHiddenStatus() == HIDDEN_STATUS_SINK) {
			if (npcid == 45061 
					|| npcid == 45161 
					|| npcid == 45181 
					|| npcid == 45455) { 
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			} else if (npcid == 45045 
					|| npcid == 45126 
					|| npcid == 45134 
					|| npcid == 45281) { 
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(4);
			} else if (npcid == 46107 
					 || npcid == 46108) { 
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (leader.getHiddenStatus() == HIDDEN_STATUS_FLY) {
			if (npcid == 45067 
					|| npcid == 45264 
					|| npcid == 45452 
					|| npcid == 45090 
					|| npcid == 45321 
					|| npcid == 45445) { 
				setHiddenStatus(HIDDEN_STATUS_FLY);
				setStatus(4);
			} else if (npcid == 45681) { 
				setHiddenStatus(HIDDEN_STATUS_FLY);
				setStatus(11);
			}
		} else if (npcid >= 46125 && npcid <= 46128) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
			setStatus(4);
		}
	}

	@Override
	protected void transform(int transformId) {
		super.transform(transformId);

		getInventory().clearItems();
		DropTable.getInstance().setDrop(this, getInventory());
		getInventory().shuffle();
	}

	private boolean _nextDragonStepRunning = false;
	protected void setNextDragonStepRunning(boolean nextDragonStepRunning) {
		_nextDragonStepRunning = nextDragonStepRunning;
	}

	protected boolean isNextDragonStepRunning() {
		return _nextDragonStepRunning;
	}

	private void bloodstain() {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this, 50)) {
			if (getNpcTemplate().get_npcId() == 97008) {
				pc.sendPackets(new S_ServerMessage(1580));
				L1BuffUtil.bloodstain(pc, (byte) 0, 4320, true);
			} else if (getNpcTemplate().get_npcId() == 97046) {
				pc.sendPackets(new S_ServerMessage(1668));
				L1BuffUtil.bloodstain(pc, (byte) 1, 4320, true);
			}
		}
	}

	private void doNextDragonStep(L1Character attacker, int npcid) {
		if (!isNextDragonStepRunning()) {
			int[] dragonId = { 97006, 97007, 97044, 97045 };
			int[] nextStepId = { 97007, 97008, 97045, 97046 };
			int nextSpawnId = 0;
			for (int i = 0; i < dragonId.length; i++) {
				if (npcid == dragonId[i]) {
					nextSpawnId = nextStepId[i];
					break;
				}
			}
			if (attacker != null && nextSpawnId > 0) {
				L1PcInstance _pc = null;
				if (attacker instanceof L1PcInstance) {
					_pc = (L1PcInstance) attacker;
				}
				else if (attacker instanceof L1PetInstance) {
					L1PetInstance pet = (L1PetInstance) attacker;
					L1Character cha = pet.getMaster();
					if (cha instanceof L1PcInstance) {
						_pc = (L1PcInstance) cha;
					}
				}
				else if (attacker instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) attacker;
					L1Character cha = summon.getMaster();
					if (cha instanceof L1PcInstance) {
						_pc = (L1PcInstance) cha;
					}
				}
				if (_pc != null) {
					NextDragonStep nextDragonStep = new NextDragonStep(_pc, this, nextSpawnId);
					GeneralThreadPool.getInstance().execute(nextDragonStep);
				}
			}
		}
	}

	class NextDragonStep implements Runnable {
		L1PcInstance _pc;
		L1MonsterInstance _mob;
		int _npcid;
		int _transformId;
		int _x;
		int _y;
		int _h;
		short _m;
		L1Location _loc = new L1Location();

		public NextDragonStep(L1PcInstance pc, L1MonsterInstance mob, int transformId) {
			_pc = pc;
			_mob = mob;
			_transformId = transformId;
			_x = mob.getX();
			_y = mob.getY();
			_h = mob.getHeading();
			_m = mob.getMapId();
			_loc = mob.getLocation();
		}

		@Override
		public void run() {
			setNextDragonStepRunning(true);
			try {
				Thread.sleep(10500);
				L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(_transformId);
				npc.setId(IdFactory.getInstance().nextId());
				npc.setMap((short) _m);
				npc.setHomeX(_x);
				npc.setHomeY(_y);
				npc.setHeading(_h);
				npc.getLocation().set(_loc);
				npc.getLocation().forward(_h);
				npc.setPortalNumber(getPortalNumber());

				broadcastPacket(new S_NPCPack(npc));
				broadcastPacket(new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_Hide));

				L1World.getInstance().storeObject(npc);
				L1World.getInstance().addVisibleObject(npc);
				npc.turnOnOffLight();
				npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
				setNextDragonStepRunning(false);
			}
			catch (InterruptedException e) {
			}
		}
	}
}