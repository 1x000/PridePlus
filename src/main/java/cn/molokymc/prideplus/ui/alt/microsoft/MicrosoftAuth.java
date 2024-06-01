package cn.molokymc.prideplus.ui.alt.microsoft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.compatibility.Sys;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MicrosoftAuth implements Closeable {
    private static final String CLIENT_ID = "67b74668-ef33-49c3-a75c-18cbb2481e0c";
    private static final String REDIRECT_URI = "http://localhost:9643/sad";
    private static final String SCOPE = "XboxLive.signin%20offline_access";

    private static final String URL = "https://login.live.com/oauth20_authorize.srf?client_id=<client_id>&redirect_uri=<redirect_uri>&response_type=code&display=touch&scope=<scope>&prompt=select_account"
            .replace("<client_id>", CLIENT_ID)
            .replace("<redirect_uri>", REDIRECT_URI)
            .replace("<scope>", SCOPE);

    private static final Gson gson = new GsonBuilder().create();
    private static final JsonParser parser = new JsonParser();
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger("Microsoft Auth");

    private final Callback<String> statusCallback;
    private final Runnable closeRunnable;

    private boolean closed = false;

    public MicrosoftAuth(Callback<Data> callback, Callback<String> statusCallback) throws IOException {
        this.statusCallback = statusCallback;

        HttpServer redirectServer = HttpServer.create(new InetSocketAddress("localhost", 9643), 0);

        closeRunnable = () -> redirectServer.stop(0);

        redirectServer.createContext("/sad", exchange -> {
            String query = exchange.getRequestURI().getQuery();

            if (query.contains("code")) {
                String code = query.split("code=")[1];

                String[] microsoftTokenAndRefreshToken = getMicrosoftTokenAndRefreshToken(code);
                String xBoxLiveToken = getXBoxLiveToken(microsoftTokenAndRefreshToken[0]);
                String[] xstsTokenAndHash = getXSTSTokenAndUserHash(xBoxLiveToken);
                String accessToken = getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1]);

                JsonObject jsonObject = parser.parse(
                        Objects.requireNonNull(get("https://api.minecraftservices.com/minecraft/profile",
                                Collections.singletonMap("Authorization", "Bearer " + accessToken)))
                ).getAsJsonObject();

                callback.call(new Data(
                        jsonObject.get("name").getAsString(),
                        jsonObject.get("id").getAsString(),
                        accessToken,
                        microsoftTokenAndRefreshToken[1]
                ));

                setStatus("登录完成");

                exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                os.write("login successful".getBytes(StandardCharsets.UTF_8));
                os.close();

                close();
            } else {
                exchange.sendResponseHeaders(404, 0);
            }

            exchange.close();
        });

        redirectServer.start();

        Sys.openURL(URL);
    }

    public MicrosoftAuth(Callback<Data> callback, Callback<String> statusCallback, String refreshToken) {
        this.statusCallback = statusCallback;
        closeRunnable = null;

        String microsoftToken = getMicrosoftTokenFromRefreshToken(refreshToken);
        String xBoxLiveToken = getXBoxLiveToken(microsoftToken);
        String[] xstsTokenAndHash = getXSTSTokenAndUserHash(xBoxLiveToken);
        String accessToken = getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1]);

        JsonObject jsonObject = parser.parse(
                Objects.requireNonNull(get(
                        "https://api.minecraftservices.com/minecraft/profile",
                        Collections.singletonMap("Authorization", "Bearer " + accessToken)))
        ).getAsJsonObject();

        callback.call(new Data(
                jsonObject.get("name").getAsString(),
                jsonObject.get("id").getAsString(),
                accessToken,
                refreshToken
        ));

        setStatus("登录完成");
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        System.out.println("Close");

        if (closeRunnable != null) {
            closeRunnable.run();
        }
        closed = true;
    }

    private void setStatus(String status) {
        logger.info(status);

        statusCallback.call(status);
    }

    private String getMicrosoftTokenFromRefreshToken(String refreshToken) {
        setStatus("Get microsoft token from refreshToken");

        JsonObject jsonObject = parser.parse(
                Objects.requireNonNull(post(
                        "https://login.live.com/oauth20_token.srf",
                        "client_id=" + CLIENT_ID + "&refresh_token=" + refreshToken + "&grant_type=refresh_token",
                        Collections.singletonMap("Content-Type", "application/x-www-form-urlencoded")))
        ).getAsJsonObject();

        return jsonObject.get("access_token").getAsString();
    }

    private String[] getMicrosoftTokenAndRefreshToken(String code) {
        setStatus("Get microsoft token and refreshToken");

        JsonObject jsonObject = parser.parse(
                Objects.requireNonNull(post(
                        "https://login.live.com/oauth20_token.srf",
                        "client_id=" + CLIENT_ID + "&code=" + code + "&grant_type=authorization_code&redirect_uri=" + REDIRECT_URI + "&scope=" + SCOPE,
                        Collections.singletonMap("Content-Type", "application/x-www-form-urlencoded")))
        ).getAsJsonObject();

        return new String[]{
                jsonObject.get("access_token").getAsString(),
                jsonObject.get("refresh_token").getAsString()
        };
    }

    private String getXBoxLiveToken(String microsoftToken) {
        setStatus("Get XBox live token");

        JsonObject paramObj = new JsonObject();
        JsonObject propertiesObj = new JsonObject();

        propertiesObj.addProperty("AuthMethod", "RPS");
        propertiesObj.addProperty("SiteName", "user.auth.xboxlive.com");
        propertiesObj.addProperty("RpsTicket", "d=" + microsoftToken);
        paramObj.add("Properties", propertiesObj);
        paramObj.addProperty("RelyingParty", "http://auth.xboxlive.com");
        paramObj.addProperty("TokenType", "JWT");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        JsonObject jsonObject = parser.parse(
                Objects.requireNonNull(post(
                        "https://user.auth.xboxlive.com/user/authenticate",
                        gson.toJson(paramObj),
                        headers
                ))
        ).getAsJsonObject();

        return jsonObject.get("Token").getAsString();
    }

    private String[] getXSTSTokenAndUserHash(String xboxLiveToken) {
        setStatus("Get xsts token and user hash");

        JsonObject paramObj = new JsonObject();
        JsonObject propertiesObj = new JsonObject();

        propertiesObj.addProperty("SandboxId", "RETAIL");
        propertiesObj.add("UserTokens", parser.parse(gson.toJson(new String[]{xboxLiveToken})));
        paramObj.add("Properties", propertiesObj);
        paramObj.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        paramObj.addProperty("TokenType", "JWT");

        JsonObject jsonObject = parser.parse(
                Objects.requireNonNull(post("https://xsts.auth.xboxlive.com/xsts/authorize",
                        gson.toJson(paramObj),
                        Collections.singletonMap("Content-Type", "application/json")))
        ).getAsJsonObject();

        return new String[]{
                jsonObject.get("Token").getAsString(),
                jsonObject.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString()
        };
    }

    private String getAccessToken(String xstsToken, String uhs) {
        setStatus("Get access token");

        JsonObject paramObj = new JsonObject();
        paramObj.addProperty("identityToken", "XBL3.0 x=" + uhs + ";" + xstsToken);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        JsonObject jsonObject = parser.parse(
                Objects.requireNonNull(post("https://api.minecraftservices.com/authentication/login_with_xbox",
                        gson.toJson(paramObj),
                        headers))
        ).getAsJsonObject();

        return jsonObject.get("access_token").getAsString();
    }

    private String post(String urlString, String param, Map<String, String> requestProperty) {
        try
        {
            HttpURLConnection connection = getHttpURLConnection(urlString, param, requestProperty);

            String readText;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                readText = responseBuilder.toString();
            }

            connection.disconnect();

            return readText;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static HttpURLConnection getHttpURLConnection(String urlString, String param, Map<String, String> requestProperty) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoInput(true);
        connection.setDoOutput(true);

        connection.setConnectTimeout(20000);
        connection.setReadTimeout(20000);

        connection.setRequestMethod("POST");

        for (Map.Entry<String, String> entry : requestProperty.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        connection.connect();

        try (OutputStream outputStream = connection.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            writer.write(param);
        }
        return connection;
    }

    @SuppressWarnings("SameParameterValue")
    private String get(String urlString, Map<String, String> requestProperty) {
        try
        {
            HttpURLConnection connection = getHttpURLConnection(urlString, requestProperty);

            String readText;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                readText = responseBuilder.toString();
            }

            connection.disconnect();

            return readText;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static HttpURLConnection getHttpURLConnection(String urlString, Map<String, String> requestProperty) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoInput(true);
        connection.setRequestMethod("GET");

        connection.setConnectTimeout(20000);
        connection.setReadTimeout(20000);

        for (Map.Entry<String, String> entry : requestProperty.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        connection.connect();
        return connection;
    }

    public interface Callback<T> {
        void call(T obj);
    }

    @Getter
    public static class Data {
        private final String userName;
        private final String uuid;
        private final String accessToken;
        private final String refreshToken;

        public Data(String userName, String uuid, String accessToken, String refreshToken) {
            this.userName = userName;
            this.uuid = uuid;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

    }
}


