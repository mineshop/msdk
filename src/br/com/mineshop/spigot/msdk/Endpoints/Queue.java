package br.com.mineshop.spigot.msdk.Endpoints;

public class Queue {
  private String uuid;
  private String nickname;
  private String commands;
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

  public String[] getCommands() {
    return this.commands.split("\n");
  }

  public void setCommands(String commands) {
    this.commands = commands;
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
