package cn.molokymc.prideplus.viamcp.utils;

import com.viaversion.viaversion.api.platform.PlatformTask;

import java.util.concurrent.Future;

public class FutureTaskId implements PlatformTask<Future<?>>
{
    private final Future<?> object;

    public FutureTaskId(Future<?> object)
    {
        this.object = object;
    }

    @Override
    public Future<?> getObject()
    {
        return object;
    }

    @Override
    public void cancel()
    {
        object.cancel(false);
    }
}
