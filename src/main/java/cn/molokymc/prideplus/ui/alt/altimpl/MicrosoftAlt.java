package cn.molokymc.prideplus.ui.alt.altimpl;

import cn.molokymc.prideplus.ui.alt.AccountEnum;
import cn.molokymc.prideplus.ui.alt.Alt;

public final class MicrosoftAlt extends Alt {
    private final String refreshToken;

    public MicrosoftAlt(String userName,String refreshToken) {
        super(userName,AccountEnum.MICROSOFT);
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
