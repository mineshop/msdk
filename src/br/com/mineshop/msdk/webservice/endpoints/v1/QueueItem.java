package br.com.mineshop.msdk.webservice.endpoints.v1;

public class QueueItem {
  private String uuid;
  private String nickname;
  private String command;
  private int slotsNeeded;
  private String type;
  private String status;

  public String getUuid() {
    return this.uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getNickname() {
    return this.nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getCommand() {
    return this.command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public int getSlotsNeeded() {
    return this.slotsNeeded;
  }

  public void setSlotsNeeded(int slotsNeeded) {
    this.slotsNeeded = slotsNeeded;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
