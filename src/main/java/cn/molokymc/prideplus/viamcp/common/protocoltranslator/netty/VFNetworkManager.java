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

package cn.molokymc.prideplus.viamcp.common.protocoltranslator.netty;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import cn.molokymc.prideplus.viamcp.common.protocoltranslator.provider.ViaMCPEncryptionProvider;

public interface VFNetworkManager {

    /**
     * API method to setup the decryption side of the pipeline.
     * This method is called by the {@link ViaMCPEncryptionProvider} class.
     */
    void setupPreNettyDecryption();

    /**
     * @return the target version of the connection
     */
    ProtocolVersion getTrackedVersion();

    /**
     * Sets the target version of the connection.
     *
     * @param version the target version
     */
    void setTrackedVersion(final ProtocolVersion version);

}
