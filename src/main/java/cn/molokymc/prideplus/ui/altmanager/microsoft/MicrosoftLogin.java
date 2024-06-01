package cn.molokymc.prideplus.ui.altmanager.microsoft;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.compatibility.Sys;

public final class MicrosoftLogin
implements Closeable {
    private static final String CLIENT_ID = "67b74668-ef33-49c3-a75c-18cbb2481e0c";
    private static final String REDIRECT_URI = "http://localhost:3434/sad";
    private static final String SCOPE = "XboxLive.signin%20offline_access";
    private static final String URL = "https://login.live.com/oauth20_authorize.srf?client_id=<client_id>&redirect_uri=<redirect_uri>&response_type=code&display=touch&scope=<scope>".replace("<client_id>", "67b74668-ef33-49c3-a75c-18cbb2481e0c").replace("<redirect_uri>", "http://localhost:3434/sad").replace("<scope>", "XboxLive.signin%20offline_access");
    @Getter
    public volatile String uuid = null;
    @Getter
    public volatile String userName = null;
    @Getter
    public volatile String accessToken = null;
    public volatile String refreshToken = null;
    @Getter
    @Setter
    public volatile boolean logged = false;
    @Getter
    @Setter
    public volatile String status = EnumChatFormatting.YELLOW + "Login...";
    private final HttpServer httpServer;
    private final MicrosoftHttpHandler handler;

    public MicrosoftLogin() throws IOException {
        this.handler = new MicrosoftHttpHandler();
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", 3434), 0);
        this.httpServer.createContext("/sad", this.handler);
        this.httpServer.start();
        Sys.openURL(URL);
    }

    public MicrosoftLogin(String refreshToken) throws IOException {
        this.refreshToken = refreshToken;
        this.httpServer = null;
        this.handler = null;
        String microsoftTokenAndRefreshToken = this.getMicrosoftTokenFromRefreshToken(refreshToken);
        String xBoxLiveToken = this.getXBoxLiveToken(microsoftTokenAndRefreshToken);
        String[] xstsTokenAndHash = this.getXSTSTokenAndUserHash(xBoxLiveToken);
        String accessToken = this.getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1]);
        URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        String read = this.read(connection.getInputStream());
        JSONObject jsonObject = JSON.parseObject(read);
        String uuid = jsonObject.getString("id");
        String userName = jsonObject.getString("name");
        this.uuid = uuid;
        this.userName = userName;
        this.accessToken = accessToken;
        this.logged = true;
    }

    @Override
    public void close() {
        if (this.httpServer != null) {
            this.httpServer.stop(0);
        }
    }

    public void show() throws Exception {
        Desktop.getDesktop().browse(new URI(URL));
    }

    private String getAccessToken(String xstsToken, String uhs) throws IOException {
        this.status = EnumChatFormatting.YELLOW + "Getting access token";
        System.out.println("Getting access token");
        URL url = new URL("https://api.minecraftservices.com/authentication/login_with_xbox");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        JSONObject input = new JSONObject();
        input.put("identityToken", ("XBL3.0 x=" + uhs + ";" + xstsToken));
        this.write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), JSON.toJSONString(input));
        JSONObject jsonObject = JSON.parseObject(this.read(connection.getInputStream()));
        return jsonObject.getString("access_token");
    }

    public String getMicrosoftTokenFromRefreshToken(String refreshToken) throws IOException {
        this.status = EnumChatFormatting.YELLOW + "Getting microsoft token from refresh token";
        System.out.println("Getting microsoft token from refresh token");
        URL url = new URL("https://login.live.com/oauth20_token.srf");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        String param = "client_id=67b74668-ef33-49c3-a75c-18cbb2481e0c&refresh_token=" + refreshToken + "&grant_type=refresh_token&redirect_uri=" + REDIRECT_URI;
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        this.write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);
        JSONObject response_obj = JSON.parseObject(this.read(connection.getInputStream()));
        return response_obj.getString("access_token");
    }

    public String[] getMicrosoftTokenAndRefreshToken(String code) throws IOException {
        this.status = EnumChatFormatting.YELLOW + "Getting microsoft token";
        System.out.println("Getting microsoft token");
        URL url = new URL("https://login.live.com/oauth20_token.srf");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        String param = "client_id=67b74668-ef33-49c3-a75c-18cbb2481e0c&code=" + code + "&grant_type=authorization_code&redirect_uri=" + REDIRECT_URI + "&scope=" + SCOPE;
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        this.write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);
        JSONObject response_obj = JSON.parseObject(this.read(connection.getInputStream()));
        return new String[]{response_obj.getString("access_token"), response_obj.getString("refresh_token")};
    }

    public String getXBoxLiveToken(String microsoftToken) throws IOException {
        this.status = EnumChatFormatting.YELLOW + "Getting xbox live token";
        System.out.println("Getting xbox live token");
        URL connectUrl = new URL("https://user.auth.xboxlive.com/user/authenticate");
        JSONObject xbl_param = new JSONObject();
        JSONObject xbl_properties = new JSONObject();
        xbl_properties.put("AuthMethod", "RPS");
        xbl_properties.put("SiteName", "user.auth.xboxlive.com");
        xbl_properties.put("RpsTicket", "d=" + microsoftToken);
        xbl_param.put("Properties", xbl_properties);
        xbl_param.put("RelyingParty", "http://auth.xboxlive.com");
        xbl_param.put("TokenType", "JWT");
        String param = JSON.toJSONString(xbl_param);
        HttpURLConnection connection = (HttpURLConnection)connectUrl.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        this.write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);
        JSONObject response_obj = JSON.parseObject(this.read(connection.getInputStream()));
        return response_obj.getString("Token");
    }

    public String[] getXSTSTokenAndUserHash(String xboxLiveToken) throws IOException {
        this.status = EnumChatFormatting.YELLOW + "Getting xsts token and user hash";
        System.out.println("Getting xsts token and user hash");
        URL ConnectUrl = new URL("https://xsts.auth.xboxlive.com/xsts/authorize");
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add(xboxLiveToken);
        JSONObject xbl_param = new JSONObject();
        JSONObject xbl_properties = new JSONObject();
        xbl_properties.put("SandboxId", "RETAIL");
        xbl_properties.put("UserTokens", JSONArray.parse(JSON.toJSONString(tokens)));
        xbl_param.put("Properties", xbl_properties);
        xbl_param.put("RelyingParty", "rp://api.minecraftservices.com/");
        xbl_param.put("TokenType", "JWT");
        String param = JSON.toJSONString(xbl_param);
        HttpURLConnection connection = (HttpURLConnection)ConnectUrl.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        this.write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);
        JSONObject response_obj = JSON.parseObject(this.read(connection.getInputStream()));
        String token = response_obj.getString("Token");
        String uhs = response_obj.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
        return new String[]{token, uhs};
    }

    private void write(BufferedWriter writer, String s2) throws IOException {
        writer.write(s2);
        writer.close();
    }

    private String read(InputStream stream) throws IOException {
        String s2;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder stringBuilder = new StringBuilder();
        while ((s2 = reader.readLine()) != null) {
            stringBuilder.append(s2);
        }
        stream.close();
        reader.close();
        return stringBuilder.toString();
    }

    private class MicrosoftHttpHandler
    implements HttpHandler {
        private boolean got = false;

        private MicrosoftHttpHandler() {
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String query;
            if (!this.got && (query = httpExchange.getRequestURI().getQuery()).contains("code")) {
                this.got = true;
                String code = query.split("code=")[1];
                String[] microsoftTokenAndRefreshToken = MicrosoftLogin.this.getMicrosoftTokenAndRefreshToken(code);
                String xBoxLiveToken = MicrosoftLogin.this.getXBoxLiveToken(microsoftTokenAndRefreshToken[0]);
                String[] xstsTokenAndHash = MicrosoftLogin.this.getXSTSTokenAndUserHash(xBoxLiveToken);
                String accessToken = MicrosoftLogin.this.getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1]);
                URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                String read = MicrosoftLogin.this.read(connection.getInputStream());
                JSONObject jsonObject = JSON.parseObject(read);
                String uuid = jsonObject.getString("id");
                String userName = jsonObject.getString("name");
                MicrosoftLogin.this.uuid = uuid;
                MicrosoftLogin.this.userName = userName;
                MicrosoftLogin.this.accessToken = accessToken;
                MicrosoftLogin.this.refreshToken = microsoftTokenAndRefreshToken[1];
                MicrosoftLogin.this.logged = true;
            }
        }
    }
}

