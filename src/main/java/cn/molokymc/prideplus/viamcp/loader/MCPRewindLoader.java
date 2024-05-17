package cn.molokymc.prideplus.viamcp.loader;

import com.viaversion.viarewind.ViaRewindConfig;
import com.viaversion.viarewind.api.ViaRewindPlatform;
import com.viaversion.viaversion.api.Via;

import java.io.File;
import java.util.logging.Logger;

public class MCPRewindLoader implements ViaRewindPlatform
{
    public MCPRewindLoader(final File file)
    {
        final ViaRewindConfig conf = new ViaRewindConfig(file.toPath().resolve("ViaRewind").resolve("config.yml").toFile());
        conf.reload();
        this.init(file.toPath().resolve("ViaRewind").resolve("config.yml").toFile());
    }

    @Override
    public Logger getLogger()
    {
        return Via.getPlatform().getLogger();
    }

    @Override
    public File getDataFolder() {
        return null;
    }
}
