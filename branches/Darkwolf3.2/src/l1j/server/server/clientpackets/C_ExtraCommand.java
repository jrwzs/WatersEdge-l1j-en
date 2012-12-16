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
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import static l1j.server.server.model.skill.L1SkillId.*;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket
public class C_ExtraCommand extends ClientBasePacket {
	private static final String C_EXTRA_COMMAND = "[C] C_ExtraCommand";
	private static Logger _log = Logger.getLogger(C_ExtraCommand.class.getName());

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
	            if (pc.isInvisble()) {
	                return;
	            }
	            if (pc.isTeleport()) {
	                return;
	            }
	            if (pc.hasSkillEffect(SHAPE_CHANGE)) {
	                int gfxId = pc.getTempCharGfx();
	                if (gfxId != 6080 && gfxId != 6094) {
	                    return;
	                }
	            }
	            int actionId = readC();
	            S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), actionId);
	            pc.broadcastPacket(gfx);
	        } catch (final Exception e) {
	            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
	        } finally {
	            finish();
	        }
	    }

	    @Override
	    public String getType() {
	        return C_EXTRA_COMMAND;
	    }
	}