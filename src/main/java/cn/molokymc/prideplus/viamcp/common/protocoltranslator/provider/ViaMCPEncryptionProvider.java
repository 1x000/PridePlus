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
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.providers.EncryptionProvider;
import cn.molokymc.prideplus.viamcp.common.ViaMCPCommon;

public class ViaMCPEncryptionProvider extends EncryptionProvider {

    @Override
    public void enableDecryption(UserConnection user) {
        user.getChannel().attr(ViaMCPCommon.VF_NETWORK_MANAGER).getAndRemove().setupPreNettyDecryption();
    }

}
