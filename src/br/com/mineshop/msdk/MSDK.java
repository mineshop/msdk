package br.com.mineshop.msdk;

import br.com.mineshop.msdk.webservice.endpoints.v1.Queue;
import br.com.mineshop.msdk.exceptions.MsdkException;
import br.com.mineshop.msdk.exceptions.WebServiceException;
import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MSDK {
  public final String SDK_VERSION = "1.0.0";
  public final String API_ADDR = "https://api.mineshop.com.br/plugins";

  private int connectTimeout = 1500;
  private int readTimeout = 3000;
  private String authorization;

  public void setConnectTimeout(int ms) {
    this.connectTimeout = ms;
  }

  public void setReadTimeout(int ms) {
    this.readTimeout = ms;
  }

  public void setCredentials(String authorization) {
    this.authorization = authorization;
  }

  public Queue[] getQueue() throws WebServiceException, MsdkException {
    return this.getQueue("");
  }

  public Queue[] getQueue(String nickname) throws WebServiceException, MsdkException {
    String response = this.get(String.format("/v1/queue/%s", nickname));
    return new Gson().fromJson(response, Queue[].class);
  }

  public void hasBeenDelivered(String nickname, String queueItemUuid) throws MsdkException, WebServiceException {
    this.update(String.format("/v1/queue/%s/%s", nickname, queueItemUuid));
  }

  private String get(String endpoint) throws WebServiceException, MsdkException {
    HttpsURLConnection c = null;
    int statusCode = 0;

    try {
      URL u = new URL(this.API_ADDR + endpoint);

      c = (HttpsURLConnection) u.openConnection();
      c.setRequestMethod("GET");
      c.setRequestProperty("Authorization", this.authorization);
      c.setRequestProperty("Content-Type", "application/json");
      c.setUseCaches(false);
      c.setAllowUserInteraction(false);
      c.setConnectTimeout(this.connectTimeout);
      c.setReadTimeout(this.readTimeout);
      c.connect();

      statusCode = c.getResponseCode();

      if (statusCode == 200 || statusCode == 201 || statusCode == 204) {
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

    this.exceptionsByStatusCode(statusCode);
    return null;
  }

  private void update(String endpoint) throws WebServiceException, MsdkException {
    HttpsURLConnection c = null;
    int statusCode = 0;

    try {
      URL u = new URL(this.API_ADDR + endpoint);

      c = (HttpsURLConnection) u.openConnection();
      c.setRequestMethod("PUT");
      c.setRequestProperty("Authorization", this.authorization);
      c.setRequestProperty("Content-Type", "application/json");
      c.setDoOutput(true);
      c.setUseCaches(false);
      c.setAllowUserInteraction(false);
      c.setConnectTimeout(this.connectTimeout);
      c.setReadTimeout(this.readTimeout);
      c.connect();

      OutputStreamWriter osw = new OutputStreamWriter(c.getOutputStream());

      osw.write("{}");
      osw.flush();
      osw.close();

      statusCode = c.getResponseCode();

      if (statusCode == 200 || statusCode == 201 || statusCode == 204) {
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

    this.exceptionsByStatusCode(statusCode);
  }

  private void exceptionsByStatusCode(int statusCode) throws MsdkException, WebServiceException {
    if (statusCode == 0) {
      throw new MsdkException(String.format(
        "[%s] Servidor sem conexão com a internet",
        Integer.toString(statusCode)
      ));
    }

    if (statusCode == 401) {
      throw new MsdkException(String.format(
        "[%s] Conexão não autorizada! Por favor, verifique as credenciais do seu servidor...",
        Integer.toString(statusCode)
      ));
    }

    if (statusCode >= 500) {
      throw new WebServiceException(String.format(
        "[%s] Os servidores WebService do MS2 comportaram-se de maneira inesperada",
        Integer.toString(statusCode)
      ));
    }

    throw new MsdkException(String.format(
      "[%s] Provável falha causada por entrada de dados incompatíveis com o endpoint requisitado",
      Integer.toString(statusCode)
    ));
  }
}
