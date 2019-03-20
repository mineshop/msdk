package br.com.mineshop.spigot.msdk;

import br.com.mineshop.spigot.msdk.Endpoints.CheckToken;
import br.com.mineshop.spigot.msdk.Endpoints.Queue;
import br.com.mineshop.spigot.msdk.Exceptions.WSException;
import com.google.gson.Gson;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MSDK extends JavaPlugin {
  private final String api = "https://api.mineshop.com.br/plugin";
  private String servername;
  private String authorization;

  public void setAuthorization(String token) throws WSException {
    String response = this.get(String.format("/v1/check-token/%s", token.trim().toLowerCase()));
    CheckToken checkToken = new Gson().fromJson(response, CheckToken.class);
    if (!checkToken.getName().isEmpty() && !checkToken.getToken().isEmpty()) {
      this.servername = checkToken.getName();
      this.authorization = checkToken.getToken();
    }
  }

  public String getServername() {
    return this.servername;
  }

  public Queue[] getQueue() throws WSException {
    return this.getQueue("");
  }

  public Queue[] getQueue(String nickname) throws WSException {
    nickname = nickname.trim();
    if (!nickname.isEmpty()) {
      nickname = String.format("/%s", nickname);
    }

    String response = this.get(String.format("/v1/queue%s", nickname));
    return new Gson().fromJson(response, Queue[].class);
  }

  public void delivered(Queue queue) throws WSException {
    this.delivered(queue.getUuid());
  }

  public void delivered(String uuid) throws WSException {
    this.update(String.format("/v1/queue/delivered/%s", uuid));
  }

  private String get(String endpoint) throws WSException {
    HttpsURLConnection c = null;
    int statusCode = 0;

    try {
      URL u = new URL((this.api + endpoint).toLowerCase());

      c = (HttpsURLConnection) u.openConnection();
      c.setRequestMethod("GET");
      c.setRequestProperty("Authorization", this.authorization);
      c.setRequestProperty("Content-Type", "application/json");
      c.setUseCaches(false);
      c.setAllowUserInteraction(false);
      c.setConnectTimeout(1000);
      c.setReadTimeout(1000);
      c.connect();

      statusCode = c.getResponseCode();

      if (statusCode >= 200 && statusCode <= 299) {
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder sb = new StringBuilder();

        while ((line = br.readLine()) != null) {
          sb.append(line).append("\n");
        }

        br.close();

        return sb.toString();
      }
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (c != null) {
        try {
          c.disconnect();
        } catch (Exception ex) {
          Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

    throw new WSException(String.format("[%s] %s Falha ao tentar comunicar-se com a web api", this.getDescription().getName(), Integer.toString(statusCode)));
  }

  private void update(String endpoint) throws WSException {
    HttpsURLConnection c = null;
    int statusCode = 0;

    try {
      URL u = new URL((this.api + endpoint).toLowerCase());

      c = (HttpsURLConnection) u.openConnection();
      c.setRequestMethod("PUT");
      c.setRequestProperty("Authorization", this.authorization);
      c.setRequestProperty("Content-Type", "application/json");
      c.setDoOutput(true);
      c.setUseCaches(false);
      c.setAllowUserInteraction(false);
      c.setConnectTimeout(1000);
      c.setReadTimeout(1000);
      c.connect();

      OutputStreamWriter osw = new OutputStreamWriter(c.getOutputStream());

      osw.write("{}");
      osw.flush();
      osw.close();

      statusCode = c.getResponseCode();

      if (statusCode >= 200 && statusCode <= 299) {
        return;
      }
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (c != null) {
        try {
          c.disconnect();
        } catch (Exception ex) {
          Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

    throw new WSException(String.format("[%s] %s Falha ao tentar comunicar-se com a web api", this.getDescription().getName(), Integer.toString(statusCode)));
  }
}
