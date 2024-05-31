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

package cn.molokymc.prideplus.viamcp.common.platform;

import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher;

import java.io.File;
import java.util.function.Supplier;

/**
 * This interface is used to access platform specific fields.
 */
public interface VFPlatform {

    /**
     * @return the native version of the platform
     */
    int getGameVersion();

    /**
     * @return if the client is in singleplayer
     */
    Supplier<Boolean> isSingleplayer();

    /**
     * @return the leading directory of the platform
     */
    File getLeadingDirectory();

    /**
     * Sends the joinServer API request to Mojang's authentication servers.
     *
     * @param serverId    the server id of the server
     */
    void joinServer(final String serverId) throws Throwable;

    /**
     * @return the game profile fetcher of the platform for ViaLegacy
     */
    GameProfileFetcher getGameProfileFetcher();

}
