package net.optifine.util;

import java.util.HashMap;
import java.util.Map;

public class TimedEvent
{
    private static final Map<String, Long> mapEventTimes = new HashMap<>();

    public static boolean isActive(String name, long timeIntervalMs)
    {
        synchronized (mapEventTimes)
        {
            long i = System.currentTimeMillis();

            long j = mapEventTimes.computeIfAbsent(name, k -> i);

            if (i < j + timeIntervalMs)
            {
                return false;
            }
            else
            {
                mapEventTimes.put(name, i);
                return true;
            }
        }
    }
}
