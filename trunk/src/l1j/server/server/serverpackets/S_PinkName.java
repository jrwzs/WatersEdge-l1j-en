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

import l1j.server.packethandler.Opcodes;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_PinkName extends ServerBasePacket {

	public S_PinkName(int objecId, int time) {
		writeC(Opcodes.S_OPCODE_PINKNAME);
		writeD(objecId);
		writeD(time);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return _S__2C_PINKNAME;
	}

	private static final String _S__2C_PINKNAME = "[S] S_PinkName";
}
