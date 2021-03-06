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
package l1j.server.server.command.executor;

import l1j.server.Config;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class L1Pbuff implements L1CommandExecutor {
	private L1Pbuff() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Pbuff();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		L1World world = L1World.getInstance();
		if (Config.POWER_BUFF == false) {
			Config.POWER_BUFF = true;
			world.broadcastServerMessage("The power buff command has been enabled!");
		} else {
			Config.POWER_BUFF = false;
			world.broadcastServerMessage("The power buff command has been disabled!");
		}
	}
}
