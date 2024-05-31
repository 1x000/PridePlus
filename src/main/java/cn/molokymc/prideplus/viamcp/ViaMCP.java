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

package cn.molokymc.prideplus.viamcp;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher;
import cn.molokymc.prideplus.viamcp.common.platform.VFPlatform;
import cn.molokymc.prideplus.viamcp.provider.ViaMCPGameProfileFetcher;

import java.io.File;
import java.util.function.Supplier;

public class ViaMCP implements VFPlatform {
    
    public static final ViaMCP PLATFORM = new ViaMCP();

    @Override
    public int getGameVersion() {
        return 47;
    }

    @Override
    public Supplier<Boolean> isSingleplayer() {
        return () -> Minecraft.getMinecraft().isSingleplayer();
    }

    @Override
    public File getLeadingDirectory() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public void joinServer(String serverId) throws Throwable {
        final Session session = Minecraft.getMinecraft().getSession();

        Minecraft.getMinecraft().getSessionService().joinServer(session.getProfile(), session.getToken(), serverId);
    }

    @Override
    public GameProfileFetcher getGameProfileFetcher() {
        return new ViaMCPGameProfileFetcher();
    }
    
}
