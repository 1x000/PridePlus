package net.minecraft.client.multiplayer;

import cn.molokymc.prideplus.viamcp.common.gui.ExtendedServerData;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class ServerData implements ExtendedServerData
{
    public String serverName;
    public String serverIP;

    /**
     * the string indicating number of players on and capacity of the server that is shown on the server browser (i.e.
     * "5/20" meaning 5 slots used out of 20 slots total)
     */
    public String populationInfo;

    /**
     * (better variable name would be 'hostname') server name as displayed in the server browser's second line (grey
     * text)
     */
    public String serverMOTD;

    /** last server ping that showed up in the server browser */
    public long pingToServer;
    public int version = 47;

    /** Game version for this server. */
    public String gameVersion = "1.8.9";
    public boolean field_78841_f;
    public String playerList;
    private ServerData.ServerResourceMode resourceMode = ServerData.ServerResourceMode.PROMPT;
    private String serverIcon;

    /** True if the server is a LAN server */
    private boolean lanServer;

    // ViaForgeMCP
    private ProtocolVersion viaMCPVersion;

    public ServerData(String name, String ip, boolean isLan)
    {
        this.serverName = name;
        this.serverIP = ip;
        this.lanServer = isLan;
    }

    /**
     * Returns an NBTTagCompound with the server's name, IP and maybe acceptTextures.
     */
    public NBTTagCompound getNBTCompound()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        // ViaForgeMCP
        if (viaMCPVersion != null) {
            nbttagcompound.setString("viaMCPVersion", viaMCPVersion.getName());
        }

        nbttagcompound.setString("name", this.serverName);
        nbttagcompound.setString("ip", this.serverIP);

        if (this.serverIcon != null)
        {
            nbttagcompound.setString("icon", this.serverIcon);
        }

        if (this.resourceMode == ServerData.ServerResourceMode.ENABLED)
        {
            nbttagcompound.setBoolean("acceptTextures", true);
        }
        else if (this.resourceMode == ServerData.ServerResourceMode.DISABLED)
        {
            nbttagcompound.setBoolean("acceptTextures", false);
        }

        return nbttagcompound;
    }

    public ServerData.ServerResourceMode getResourceMode()
    {
        return this.resourceMode;
    }

    public void setResourceMode(ServerData.ServerResourceMode mode)
    {
        this.resourceMode = mode;
    }

    /**
     * Takes an NBTTagCompound with 'name' and 'ip' keys, returns a ServerData instance.
     */
    public static ServerData getServerDataFromNBTCompound(NBTTagCompound nbtCompound)
    {
        ServerData serverdata = new ServerData(nbtCompound.getString("name"), nbtCompound.getString("ip"), false);

        if (nbtCompound.hasKey("icon", 8))
        {
            serverdata.setBase64EncodedIconData(nbtCompound.getString("icon"));
        }

        if (nbtCompound.hasKey("acceptTextures", 1))
        {
            if (nbtCompound.getBoolean("acceptTextures"))
            {
                serverdata.setResourceMode(ServerData.ServerResourceMode.ENABLED);
            }
            else
            {
                serverdata.setResourceMode(ServerData.ServerResourceMode.DISABLED);
            }
        }
        else
        {
            serverdata.setResourceMode(ServerData.ServerResourceMode.PROMPT);
        }

        // ViaForgeMCP
        if (nbtCompound.hasKey("viaMCPVersion")) {
            serverdata.setVersion(ProtocolVersion.getClosest(nbtCompound.getString("viaMCPVersion")));
        }

        return serverdata;
    }

    /**
     * Returns the base-64 encoded representation of the server's icon, or null if not available
     */
    public String getBase64EncodedIconData()
    {
        return this.serverIcon;
    }

    public void setBase64EncodedIconData(String icon)
    {
        this.serverIcon = icon;
    }

    /**
     * Return true if the server is a LAN server
     */
    public boolean isOnLAN()
    {
        return this.lanServer;
    }

    public void copyFrom(ServerData serverDataIn)
    {
        // ViaForgeMCP
        if (serverDataIn instanceof ExtendedServerData) {
            viaMCPVersion = serverDataIn.getVersion();
        }

        this.serverIP = serverDataIn.serverIP;
        this.serverName = serverDataIn.serverName;
        this.setResourceMode(serverDataIn.getResourceMode());
        this.serverIcon = serverDataIn.serverIcon;
        this.lanServer = serverDataIn.lanServer;
    }

    @Override
    public ProtocolVersion getVersion() {
        return viaMCPVersion;
    }

    @Override
    public void setVersion(ProtocolVersion version) {
        viaMCPVersion = version;
    }

    public static enum ServerResourceMode
    {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final IChatComponent motd;

        private ServerResourceMode(String name)
        {
            this.motd = new ChatComponentTranslation("addServer.resourcePack." + name, new Object[0]);
        }

        public IChatComponent getMotd()
        {
            return this.motd;
        }
    }
}
