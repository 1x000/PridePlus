package cn.molokymc.prideplus.ui.altmanager.altimpl;

import cn.molokymc.prideplus.ui.altmanager.AccountEnum;
import cn.molokymc.prideplus.ui.altmanager.Alt;
import lombok.Getter;

@Getter
public final class MicrosoftAlt
extends Alt {
    private final String refreshToken;

    public MicrosoftAlt(String userName, String refreshToken) {
        super(userName, AccountEnum.MICROSOFT);
        this.refreshToken = refreshToken;
    }

}

