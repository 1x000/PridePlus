package cn.molokymc.prideplus.ui.altmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.UUID;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;

public final class AltManager {
    public static AltManager Instance;
    @Getter
    private final ArrayList<Alt> altList = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public AltManager() {
        Instance = this;
    }

    public void addAlt(Alt alt) {
        this.altList.add(alt);
    }

    public static LoginStatus loginAlt(String account, String password) throws AuthenticationException {
        if (StringUtils.isNullOrEmpty(password)) {
            Minecraft.getMinecraft().session = new Session(account, UUID.randomUUID().toString(), "", "mojang");
            return LoginStatus.SUCCESS;
        }
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(account);
        auth.setPassword(password);
        auth.logIn();
        Minecraft.getMinecraft().session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        return LoginStatus.SUCCESS;
    }

    public static enum LoginStatus {
        FAILED,
        SUCCESS,
        EXCEPTION{
            private Exception exception;

            public Exception getException() {
                return this.exception;
            }

            public void setException(Exception exception) {
                this.exception = exception;
            }
        };

    }
}

