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
package l1j.server.server.serverpackets;

import l1j.server.server.encryptions.Opcodes;

public class S_SendLocation extends ServerBasePacket {

	private static final String S_SEND_LOCATION = "[S] S_SendLocation";

	public S_SendLocation(int type, String senderName, int mapId, 
			int x, int y, int msgId) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(0x6f);
		writeS(senderName);
		writeH(mapId);
		writeH(x);
		writeH(y);
		writeC(msgId);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_SEND_LOCATION;
	}
}