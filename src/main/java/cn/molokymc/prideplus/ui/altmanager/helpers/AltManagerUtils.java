package cn.molokymc.prideplus.ui.altmanager.helpers;

import cn.molokymc.prideplus.Pride;
import cn.molokymc.prideplus.microsoft.MicrosoftLogin;
import cn.molokymc.prideplus.ui.notifications.NotificationManager;
import cn.molokymc.prideplus.ui.notifications.NotificationType;
import cn.molokymc.prideplus.utils.Utils;
import cn.molokymc.prideplus.utils.misc.Multithreading;
import cn.molokymc.prideplus.utils.objects.TextField;
import cn.molokymc.prideplus.utils.time.TimerUtil;
import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Session;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.alibaba.fastjson2.JSONWriter.Feature.*;

public class AltManagerUtils implements Utils {

    @Getter
    private static List<Alt> alts = new ArrayList<>();
    private final TimerUtil timerUtil = new TimerUtil();
    public static File altsFile = new File(Pride.DIRECTORY, "Alts.json");

    public AltManagerUtils() {
        if (!altsFile.exists()) {
            try {
                if (altsFile.getParentFile().mkdirs()) {
                    altsFile.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            byte[] content = Files.readAllBytes(altsFile.toPath());
            alts = JSON.parseArray(new String(content), Alt.class);
            alts.forEach(this::getHead);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAltsToFile() {
        if (timerUtil.hasTimeElapsed(15000, true)) {
            new Thread(() -> {
                try {
                    if (!altsFile.exists()) {
                        if (altsFile.getParentFile().mkdirs()) {
                            altsFile.createNewFile();
                        }
                    }
                    String json = JSON.toJSONString(alts, PrettyFormat, IgnoreNonFieldGetter);
                    Files.write(altsFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
                    //Files.write(altsFile.toPath(), new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create().toJson(alts.toArray(new Alt[0])).getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void removeAlt(Alt alt) {
        if (alt != null) {
            alts.remove(alt);
        }
    }

    public static void writeAlts() {
        Multithreading.runAsync(() -> {
            try {
                String json = JSON.toJSONString(alts, PrettyFormat, WriteNullStringAsEmpty);
                Files.write(altsFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
                //Files.write(altsFile.toPath(), new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create().toJson(alts.toArray(new Alt[0])).getBytes(StandardCharsets.UTF_8));
                //Show success message
            } catch (IOException e) {
                e.printStackTrace();
                //    Notification.post(NotificationType.WARNING, "Failed to save", "Failed to save alt list due to an IOException");
            }
        });
    }


    public void login(TextField username, TextField password) {
        String usernameS;
        String passwordS;
        if (username.getText().contains(":")) {
            String[] combo = username.getText().split(":");
            usernameS = combo[0];
            passwordS = combo[1];
        } else {
            usernameS = username.getText();
            passwordS = password.getText();
        }
        boolean microsoft = Alt.currentLoginMethod == Alt.AltType.MICROSOFT;

        if (usernameS.isEmpty() && passwordS.isEmpty()) return;

        loginWithString(usernameS, passwordS, false);
    }


    public void microsoftLoginAsync(String email, String password) {
        microsoftLoginAsync(null, email, password);
    }


    public void microsoftLoginAsync(Alt alt, String email, String password) {
        NotificationManager.post(NotificationType.INFO, "Alt Manager", "Opening browser login Microsoft Account..", 12);
        if (alt == null) {
            alt = new Alt(email, password);
        }
        Alt finalAlt = alt;
        Multithreading.runAsync(() -> {
            CompletableFuture<Session> future = new CompletableFuture<>();
            MicrosoftLogin.getRefreshToken(refreshToken -> {
                if (refreshToken != null) {
                    MicrosoftLogin.LoginData login = MicrosoftLogin.login(refreshToken);
                    future.complete(new Session(login.username, login.uuid, login.mcToken, "microsoft"));
                }
            });
            Session auth = future.join();
            if (auth != null) {
                mc.session = auth;
                finalAlt.uuid = auth.getPlayerID();
                finalAlt.altType = Alt.AltType.MICROSOFT;
                finalAlt.username = auth.getUsername();
                if (auth.getUsername() == null) {
                    NotificationManager.post(NotificationType.WARNING, "Alt Manager", "Please set an username on your Minecraft account!", 12);
                }
                Alt.stage = 2;
                finalAlt.altState = Alt.AltState.LOGIN_SUCCESS;
                AltManagerUtils.getAlts().add(finalAlt);
                writeAlts();
                Pride.INSTANCE.getAltManager().currentSessionAlt = finalAlt;
                Pride.INSTANCE.getAltManager().getAltPanel().refreshAlts();
            } else {
                Alt.stage = 1;
                finalAlt.altState = Alt.AltState.LOGIN_FAIL;
            }
        });

    }

    public void loginWithString(String username, String password, boolean microsoft) {
        for (Alt alt : alts) {
            if (alt.email.equals(username) && alt.password.equals(password)) {
                Alt.stage = 0;
                alt.loginAsync(microsoft);
                return;
            }
        }
        Alt alt = new Alt(username, password);
        alts.add(alt);
        Alt.stage = 0;
        alt.loginAsync(microsoft);
    }

    public void getHead(Alt alt) {
        if (alt.uuid == null || alt.head != null || alt.headTexture || alt.headTries > 5) return;
        Multithreading.runAsync(() -> {
            alt.headTries++;
            try {
                BufferedImage image = ImageIO.read(new URL("https://visage.surgeplay.com/bust/160/" + alt.uuid));
                alt.headTexture = true;
                // run on main thread for OpenGL context
                mc.addScheduledTask(() -> {
                    DynamicTexture texture = new DynamicTexture(image);
                    alt.head = mc.getTextureManager().getDynamicTextureLocation("HEAD-" + alt.uuid, texture);
                });
            } catch (IOException e) {
                alt.headTexture = false;
            }
        });
    }

}
