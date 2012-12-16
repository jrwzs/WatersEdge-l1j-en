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
package l1j.server.server.clientpackets;

import java.util.logging.Logger;
import java.util.logging.Level;

import l1j.server.server.ClientThread;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_CreateParty extends ClientBasePacket {

    private static final String C_CREATE_PARTY = "[C] C_CreateParty";

    private static Logger _log = Logger
            .getLogger(C_CreateParty.class.getName());

    @Override
    public void execute(byte[] decrypt, ClientThread client) {
        try {
            read(decrypt);

            L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            if (pc.isDead()) {
                return;
            }
            if (pc.isGhost()) {
                return;
            }

            int type = readC();
            if (type == 0 || type == 1) {
                int targetId = readD();
                L1Object temp = L1World.getInstance().findObject(targetId);
                if (temp instanceof L1PcInstance) {
                    L1PcInstance targetPc = (L1PcInstance) temp;
                    if (pc.getId() == targetPc.getId()) {
                        return;
                    }
                    if ((!pc.getLocation().isInScreen(targetPc.getLocation()) || (pc
                            .getLocation().getTileLineDistance(
                                    targetPc.getLocation()) > 7))) {
                        pc.sendPackets(new S_ServerMessage(952));
                        return;
                    }
                    if (targetPc.isInParty()) {
                        pc.sendPackets(new S_ServerMessage(415));
                        return;
                    }

                    if (pc.isInParty()) {
                        if (pc.getParty().isLeader(pc)) {
                            targetPc.setPartyID(pc.getId());
                            targetPc.sendPackets(new S_Message_YN(953, pc
                                    .getName()));
                        } else {
                            pc.sendPackets(new S_ServerMessage(416));
                        }
                    } else {
                        pc.setPartyType(type);
                        targetPc.setPartyID(pc.getId());
                        switch (type) {
                            case 0:
                                targetPc.sendPackets(new S_Message_YN(953, pc
                                        .getName()));
                                break;
                            case 1:
                                targetPc.sendPackets(new S_Message_YN(954, pc
                                        .getName()));
                                break;
                        }
                    }
                }
            } else if (type == 2) {
                String name = readS();
                L1PcInstance targetPc = L1World.getInstance().getPlayer(name);
                if (targetPc == null) {
                    pc.sendPackets(new S_ServerMessage(109));
                    return;
                }
                if (pc.getId() == targetPc.getId()) {
                    return;
                }
                if ((!pc.getLocation().isInScreen(targetPc.getLocation()) || (pc
                        .getLocation().getTileLineDistance(
                                targetPc.getLocation()) > 7))) {
                    pc.sendPackets(new S_ServerMessage(952));
                    return;
                }
                if (targetPc.isInChatParty()) {
                    pc.sendPackets(new S_ServerMessage(415));
                    return;
                }

                if (pc.isInChatParty()) {
                    if (pc.getChatParty().isLeader(pc)) {
                        targetPc.setPartyID(pc.getId());
                        targetPc.sendPackets(new S_Message_YN(951, pc.getName()));
                    } else {
                        pc.sendPackets(new S_ServerMessage(416));
                    }
                } else {
                    targetPc.setPartyID(pc.getId());
                    targetPc.sendPackets(new S_Message_YN(951, pc.getName()));
                }
            } else if (type == 3) {
                if ((pc.getParty() == null) || !pc.getParty().isLeader(pc)) {
                    pc.sendPackets(new S_ServerMessage(1697));
                    return;
                }

                int targetId = readD();

                L1Object obj = L1World.getInstance().findObject(targetId);

                if ((obj == null) || (pc.getId() == obj.getId())
                        || !(obj instanceof L1PcInstance)) {
                    return;
                }
                if ((!pc.getLocation().isInScreen(obj.getLocation()) || (pc
                        .getLocation().getTileLineDistance(obj.getLocation()) > 7))) {
                    pc.sendPackets(new S_ServerMessage(1695));
                    return;
                }
                L1PcInstance targetPc = (L1PcInstance) obj;
                if (!targetPc.isInParty()) {
                    pc.sendPackets(new S_ServerMessage(1696));
                    return;
                }
                pc.sendPackets(new S_Message_YN(1703, ""));
                pc.getParty().passLeader(targetPc);
            }
        } catch (final Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            finish();
        }
    }

	@Override
	public String getType() {
		return C_CREATE_PARTY;
	}
}