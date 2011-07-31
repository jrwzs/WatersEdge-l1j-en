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
package l1j.server.server.model.dolls;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1MagicDoll;

public class RegistFreezeByDoll extends TimerTask {
	private static Logger _log = Logger.getLogger(RegistFreezeByDoll.class
			.getName());

	private final L1PcInstance _pc;

	public RegistFreezeByDoll(L1PcInstance pc) {
		_pc = pc;
	}
	
	@Override
	public void run() {
		try {
			if (_pc.isDead()) {
				return;
			}
			freeze();			
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void freeze() {
		//_pc.getCurrentHp() + L1MagicDoll.getRegistFreezeByDoll(_pc);
		//pc.sendPackets(new S_OwnCharStatus(pc));
		//pc.sendPackets(new S_OwnCharAttrDef(pc));
		//int freeze = _pc.get_regist_freeze() + L1MagicDoll.getRegistFreezeByDoll(_pc);
		//if (freeze < 7) {
		//	freeze = 0;
		//}
		//_pc.set_regist_freeze(freeze);
		L1MagicDoll.getRegistFreezeByDoll(_pc);
	    }
}