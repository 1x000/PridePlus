package cn.molokymc.prideplus.viamcp.platform;

import com.viaversion.viaversion.ViaAPIBase;

import java.util.UUID;

public class MCPViaAPI extends ViaAPIBase<UUID>
{
    @Override
    public int majorVersion() {
        return super.majorVersion();
    }

    @Override
    public int apiVersion() {
        return super.apiVersion();
    }

    @Override
    public int getPlayerVersion(UUID uuid) {
        return this.getPlayerProtocolVersion(uuid).getVersion();
    }
}
