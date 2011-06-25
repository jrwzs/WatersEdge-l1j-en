/* This program is free software; you can redistribute it and/or modify
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

import l1j.server.server.ClientThread;
import l1j.server.server.Pinger;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket
public class C_KeepALIVE extends ClientBasePacket {
	private static Logger _log = Logger.getLogger(C_KeepALIVE.class.getName());
	private static final String C_KEEP_ALIVE = "[C] C_KeepALIVE";

	public C_KeepALIVE(byte decrypt[], ClientThread client) {
		super(decrypt);
		int pING;
	    int ping;
	    int mtu;
	    pING = readD();
	    ping = readD();
	    mtu = readD();
    	
	    L1PcInstance activeChar = client.getActiveChar();
    	
    	if (activeChar == null || activeChar != null && !activeChar.isPrivateShop())
        return;
        
        Pinger.getInstance().answerPing(activeChar.getId());
        System.out.println("PING:"+ping+":MTU:"+mtu);
        System.out.println("C_Keepalive Sended");
    }

	@Override
	public String getType() {
		return C_KEEP_ALIVE;
	}
}