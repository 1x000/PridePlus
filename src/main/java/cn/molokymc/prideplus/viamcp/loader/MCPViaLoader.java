package cn.molokymc.prideplus.viamcp.loader;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import cn.molokymc.prideplus.viamcp.ViaMCP;

public class MCPViaLoader implements ViaPlatformLoader
{
    @Override
    public void load()
    {
        // Via.getManager().getProviders().use(MovementTransmitterProvider.class, new BungeeMovementTransmitter());
        Via.getManager().getProviders().use(VersionProvider.class, new BaseVersionProvider()
        {
            @Override
            public ProtocolVersion getClosestServerProtocol(UserConnection connection) throws Exception
            {
                if (connection.isClientSide())
                {
                    return ProtocolVersion.getProtocol(ViaMCP.getInstance().getVersion());
                }

                return super.getClosestServerProtocol(connection);
            }
        });
    }

    @Override
    public void unload() { }
}
