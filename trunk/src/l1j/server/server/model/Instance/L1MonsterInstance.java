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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;

public class L1MonsterInstance extends L1NpcInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1MonsterInstance.class.getName());

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
				setPassispeed(640);
				setAtkspeed(900); 
				for (L1PcInstance pc : L1World.getInstance()
						.getRecognizePlayer(this)) {
					pc.sendPackets(new S_RemoveObject(this));
					pc.removeKnownObject(this);
					pc.updateObject();
				}
			}
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) { 
			if (this.getInt() > 13) // only mobs with > 13 int will use pots
			{
				useItem(USEITEM_HEAL, 30); 
			}
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.sendPackets(new S_Light(this.getId(), getLightSize()));
		perceivedFrom.addKnownObject(this);
		if (0 < getCurrentHp()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
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
			
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()
					|| pc.isMonitor() || pc.isGhost()) {
				continue;
			}
 
			if ((getNpcTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1)
					|| (getNpcTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1)) {
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
				} else if (getNpcTemplate().is_agrochao()) { 
					if (pc.getLawful() < -10) {
						targetPlayer = pc;
						break;
					}
				}
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
		startChat(CHAT_TIMING_APPEARANCE); 
	}

	@Override
	public void onAction(L1PcInstance pc) {
		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			pc.set_currentState(1);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.calcStaffOfMana();
				attack.addPcPoisonAttack(pc, this);
			}
			attack.action();
			attack.commit();
			pc.set_currentState(0);
			if (getNpcTemplate().is_recall()) {
				if (getLocation().getTileLineDistance(pc.getLocation()) > 4) {
					int rdir = getRnd().nextInt(8);
					int nx, ny, dir;
					for (int i = 0; i < 8; i++) {
						nx = getX();
						ny = getY();
						dir = rdir + i;
						if (dir > 7) {
							dir -= 8;
						}
						if (dir == 1) {
							nx++;
							ny--;
						} else if (dir == 2) {
							nx++;
						} else if (dir == 3) {
							nx++;
							ny++;
						} else if (dir == 4) {
							ny++;
						} else if (dir == 5) {
							nx--;
							ny++;
						} else if (dir == 6) {
							nx--;
						} else if (dir == 7) {
							nx--;
							ny--;
						} else if (dir == 0) {
							ny--;
						}
						if (getMap().isPassable(getX(), getY(), dir)) {
							dir += 4;
							if (dir > 7) {
								dir -= 8;
							}
							L1Teleport.teleport(pc, nx, ny, (short) getMapId(), dir, false);//im not sure if this one is fine should find out soon
							break;
						}
					}
				}
			}
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
			if (getHiddenStatus() != HIDDEN_STATUS_NONE) {
				return;
			}
			if (damage >= 0) {
				if (!(attacker instanceof L1EffectInstance)) {
					setHate(attacker, damage);
				}
			}
			if (damage > 0) {
				removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
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
					setCurrentHpDirect(0);
					setDead(true);
					Death death = new Death(attacker);
					GeneralThreadPool.getInstance().execute(death);
				} 
				else {
					transform(transformId);
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
				hide();
			}
		} else if (!isDead()) { 
			setDead(true);
			Death death = new Death(attacker);
			GeneralThreadPool.getInstance().execute(death);
		}
	}

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
		if (currentMp >= get_maxMp()) {
			currentMp = get_maxMp();
		}
		setCurrentMpDirect(currentMp);

		if (get_maxMp() > getCurrentMp()) {
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
			int targetobjid = getId();

			getMap().setPassable(getLocation(), true);
			
			broadcastPacket(new S_DoActionGFX(targetobjid,ActionCodes.ACTION_Die));
			startChat(CHAT_TIMING_DEAD);

			L1PcInstance player = null;
			if (_lastAttacker instanceof L1PcInstance) {
				player = (L1PcInstance) _lastAttacker;
			} else if (_lastAttacker instanceof L1PetInstance) {
				player = (L1PcInstance) ((L1PetInstance) _lastAttacker).getMaster();
			} else if (_lastAttacker instanceof L1SummonInstance) {
				player = (L1PcInstance) ((L1SummonInstance) _lastAttacker).getMaster();
			}

			if (player != null) {
				ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
				ArrayList<Integer> hateList = _hateList.toHateArrayList();
				int exp = getExp();
				CalcExp.calcExp(player, targetobjid, targetList, hateList, exp);

				ArrayList<L1Character> dropTargetList = _dropHateList.toTargetArrayList();
				ArrayList<Integer> dropHateList = _dropHateList.toHateArrayList();
				try {
					DropTable.getInstance().dropShare(L1MonsterInstance.this,dropTargetList, dropHateList);
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
				int karma = getKarma();
				if (karma != 0) {
					int karmaSign = Integer.signum(karma);
					int playerKarmaLevel = player.getKarmaLevel();
					int playerKarmaLevelSign = Integer.signum(playerKarmaLevel);
					if (playerKarmaLevelSign != 0 && karmaSign != playerKarmaLevelSign) {
						karma *= 5;
					}
					player.addKarma((int) (karma * Config.RATE_KARMA));
				  }   
				}  
			     else if (_lastAttacker instanceof L1EffectInstance) {  
			    	 ArrayList<L1Character> targetList = _hateList.toTargetArrayList();    
			    	 ArrayList<Integer> hateList = _hateList.toHateArrayList();   
                     if (hateList.size() != 0) {    
                	 int maxHate = 0;    
                	 for (int i = hateList.size() - 1; i >= 0; i--) {    
                     if (maxHate < ((Integer) hateList.get(i))) {   
                    	 maxHate = ((Integer) hateList.get(i));    
                    	 _lastAttacker = (L1Character) targetList.get(i);    
                    	 }    
                     }   
                	 if (_lastAttacker instanceof L1PcInstance) {   
                		 player = (L1PcInstance) _lastAttacker;    
                		 }  
                	 else if (_lastAttacker instanceof L1PetInstance) {   
                		 player = (L1PcInstance) ((L1PetInstance) _lastAttacker).getMaster();    
                	   }  
                	 else if (_lastAttacker instanceof L1SummonInstance) {    
                		 player = (L1PcInstance) ((L1SummonInstance)    
                				 _lastAttacker).getMaster();    
                		 }    
                	 int exp = getExp();    
                	 CalcExp.calcExp(player, targetobjid, targetList, hateList, exp);    
                	 ArrayList<L1Character> dropTargetList = _dropHateList.toTargetArrayList();    
                	 ArrayList<Integer> dropHateList = _dropHateList.toHateArrayList();   
                	 try {   
                		 DropTable.getInstance().dropShare(L1MonsterInstance.this, dropTargetList, dropHateList);    
                		 } catch (Exception e) {    
                			 _log.log(Level.SEVERE, e.getLocalizedMessage(), e);    
                			 }   
                		 int karma = getKarma();    
                		 if (karma != 0) {    
                			 int karmaSign = Integer.signum(karma);    
                			 int playerKarmaLevel = player.getKarmaLevel();    
                			 int playerKarmaLevelSign = Integer.signum(playerKarmaLevel);   
                			 if (playerKarmaLevelSign != 0 && karmaSign != playerKarmaLevelSign) {   
                				 karma *= 5;    
                				 }  
                			 player.addKarma((int) (karma * Config.RATE_KARMA));    
                		} 
                  }
			}
			if (getUbSealCount() != 0) {
				L1UltimateBattle ub = UBTable.getInstance().getUb(getUbId());
				if (ub != null) {
					for (L1PcInstance pc : ub.getMembersArray()) {
						if (pc != null && !pc.isDead()) {
							L1ItemInstance item = pc.getInventory().storeItem(41402, getUbSealCount());
							pc.sendPackets(new S_ServerMessage(403, item.getLogName())); 
						}
					}
				}
			}
			setDeathProcessing(false);
			setExp(0);
			setKarma(0);
			allTargetClear();
			startDeleteTimer();
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

	private int _ubId = 0;

	public boolean receiveDamage;

	public boolean receiveDamage(boolean damage)
	{
      return receiveDamage = damage;
    }
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
				Random random = new Random();
				int rnd = random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),ActionCodes.ACTION_Hide));
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
					broadcastPacket(new S_DoActionGFX(getId(),ActionCodes.ACTION_AntharasHide));
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
					broadcastPacket(new S_DoActionGFX(getId(),ActionCodes.ACTION_Moveup));
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
					broadcastPacket(new S_DoActionGFX(getId(),ActionCodes.ACTION_Moveup));
					setStatus(11);
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
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (npcid == 45045 
				|| npcid == 45126 
				|| npcid == 45134 
				|| npcid == 45281) {
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setStatus(4);
			}
		} else if (npcid == 45067 
				|| npcid == 45264 
				|| npcid == 45452 
				|| npcid == 45090 
				|| npcid == 45321 
				|| npcid == 45445) { 
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
			setStatus(4);
		} else if (npcid == 45681) { 
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
			setStatus(11);
		}
	}

	public void initHideForMinion(L1NpcInstance leader) {
		int npcid = getNpcTemplate().get_npcId();
		if (leader.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_SINK) {
			if (npcid == 45061
					|| npcid == 45161
					|| npcid == 45181
					|| npcid == 45455) { 
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setStatus(13);
			} else if (npcid == 45045 
					|| npcid == 45126 
					|| npcid == 45134
					|| npcid == 45281) { 
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setStatus(4);
			}
		} else if (leader.getHiddenStatus() == L1NpcInstance
				.HIDDEN_STATUS_FLY) {
			if (npcid == 45067 
					|| npcid == 45264 
					|| npcid == 45452
					|| npcid == 45090 
					|| npcid == 45321 
					|| npcid == 45445) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
				setStatus(4);
			} else if (npcid == 45681) { 
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
				setStatus(11);
			}
		}
	}

	@Override
	protected void transform(int transformId) {
		super.transform(transformId);

		getInventory().clearItems();
		DropTable.getInstance().setDrop(this, getInventory());
		getInventory().shuffle();
	}
}