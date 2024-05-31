/*
 * This file is part of ViaForgeMCP - https://github.com/MolokyMC/ViaForgeMCP
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.molokymc.prideplus.viamcp.common.protocoltranslator.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;
import cn.molokymc.prideplus.viamcp.common.ViaMCPCommon;

public class ViaMCPOldAuthProvider extends OldAuthProvider {

    @Override
    public void sendAuthRequest(UserConnection user, String serverId) throws Throwable {
        final ViaMCPCommon common = ViaMCPCommon.getManager();
        if (!common.getConfig().isVerifySessionInOldVersions()) {
            return;
        }
        common.getPlatform().joinServer(serverId);
    }

}
