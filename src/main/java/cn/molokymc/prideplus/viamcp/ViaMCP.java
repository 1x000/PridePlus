package cn.molokymc.prideplus.viamcp;

import cn.molokymc.prideplus.viamcp.gui.AsyncVersionSlider;
import cn.molokymc.prideplus.viamcp.loader.MCPBackwardsLoader;
import cn.molokymc.prideplus.viamcp.loader.MCPRewindLoader;
import cn.molokymc.prideplus.viamcp.loader.MCPViaLoader;
import cn.molokymc.prideplus.viamcp.platform.MCPViaInjector;
import cn.molokymc.prideplus.viamcp.platform.MCPViaPlatform;
import cn.molokymc.prideplus.viamcp.utils.JLoggerToLog4j;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class ViaMCP
{
    public final static int PROTOCOL_VERSION = 47;
    @Getter
    private static final ViaMCP instance = new ViaMCP();

    @Getter
    private final Logger jLogger = new JLoggerToLog4j(LogManager.getLogger("ViaMCP"));
    private final CompletableFuture<Void> INIT_FUTURE = new CompletableFuture<>();

    private ExecutorService ASYNC_EXEC;
    private EventLoop EVENT_LOOP;

    @Setter
    @Getter
    private File file;
    @Getter
    private int version;
    @Setter
    @Getter
    private String lastServer;

    /**
     * Version Slider that works Asynchronously with the Version GUI
     * Please initialize this before usage, with initAsyncSlider() or initAsyncSlider(x, y, width (min. 110), height)
     */
    public AsyncVersionSlider asyncSlider;

    public void start()
    {
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaMCP-%d").build();
        ASYNC_EXEC = Executors.newFixedThreadPool(8, factory);

        EVENT_LOOP = new LocalEventLoopGroup(1, factory).next();
        EVENT_LOOP.submit(INIT_FUTURE::join);

        setVersion(PROTOCOL_VERSION);
        this.file = new File("ViaMCP");
        if (this.file.mkdir())
        {
            this.getJLogger().info("Creating ViaMCP Folder");
        }

        Via.init(ViaManagerImpl.builder().injector(new MCPViaInjector()).loader(new MCPViaLoader()).platform(new MCPViaPlatform(file)).build());

        MappingDataLoader.enableMappingsCache();
        ((ViaManagerImpl) Via.getManager()).init();

        new MCPBackwardsLoader(file);
        new MCPRewindLoader(file);

        INIT_FUTURE.complete(null);
    }

    public void initAsyncSlider()
    {
        this.initAsyncSlider(5, 5, 110, 20);
    }

    public void initAsyncSlider(int x, int y, int width, int height)
    {
        asyncSlider = new AsyncVersionSlider(-1, x, y, Math.max(width, 110), height);
    }

    public ExecutorService getAsyncExecutor()
    {
        return ASYNC_EXEC;
    }

    public EventLoop getEventLoop()
    {
        return EVENT_LOOP;
    }

    public void setVersion(int version) {
        this.version = version;
        if (asyncSlider != null) asyncSlider.setVersion(version);
    }
}
