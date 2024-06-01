package cn.molokymc.prideplus.ui.alt.altimpl;

import cn.molokymc.prideplus.ui.alt.AccountEnum;
import cn.molokymc.prideplus.ui.alt.Alt;

public final class OfflineAlt extends Alt {
    public OfflineAlt(String userName) {
        super(userName,AccountEnum.OFFLINE);
    }
}
