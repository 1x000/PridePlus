package cn.molokymc.prideplus.viamcp.platform;

import cn.molokymc.prideplus.viamcp.ViaMCP;
import cn.molokymc.prideplus.viamcp.handler.CommonTransformer;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.libs.gson.JsonObject;

public class MCPViaInjector implements ViaInjector
{
    @Override
    public void inject()
    {
        // In a nutshell, this is not forge
    }

    @Override
    public void uninject()
    {
        // Update! Still not forge!
    }

    @Override
    public int getServerProtocolVersion()
    {
        return ViaMCP.PROTOCOL_VERSION;
    }

    @Override
    public String getEncoderName()
    {
        return CommonTransformer.HANDLER_ENCODER_NAME;
    }

    @Override
    public String getDecoderName()
    {
        return CommonTransformer.HANDLER_DECODER_NAME;
    }

    @Override
    public JsonObject getDump()
    {
        return new JsonObject();
    }
}
