## MSDK, SDK do MS2

Este repositório tem como finalidade expor nosso SDK para desenvolvimento de plugins para servidores de Minecraft. A **única** dependência deste SDK é a library `gson` do Google que já está embutida na versão compilada, e, portanto, deve funcionar em **qualquer servidor**.

<img src="https://ms2.nyc3.cdn.digitaloceanspaces.com/official/img/ms2small.png" width="250px" align="right" hspace="30px" vspace="140px">

### Crie sua loja

Ainda não tem uma loja consoco? <a href="https://mineshop.com.br" target="_blank">Crie agora uma mesmo</a>.

### Download do MSDK

Baixe a última versão compilada do MSDK clicando [aqui](https://github.com/clayderson/msdk/releases/download/msdk-1.1.0/msdk-1.1.0.jar).

### Documentação

Após fazer download do MSDK e adicioná-lo ao projeto do seu plugin, você pode instanciá-lo da seguinte forma:

```java
private MSDK msdk = new MSDK();
```

Uma vez instanciado o MSDK, você terá acesso a alguns métodos úteis ao seu futuro plugin. Veja os detalhes abaixo.

```java
/*
  public void setConnectTimeout(int ms)
  Define o tempo máximo, em milissegundos, que o MSDK deve aguardar por uma resposta ao fazer uma requisição a nossa API. Passado o tempo definido, uma `MsdkException` será lançada.
*/

this.msdk.setConnectTimeout(1000);
```

```java
/*
  public void setReadTimeout(int ms)
  Define o tempo máximo, em milissegundos, que o MSDK deve aguardar até que a leitura da resposta HTTP retornada por nossa API seja concluída. Passado o tempo definido, uma `MsdkException` será lançada.
*/

this.msdk.setReadTimeout(2000);
```

```java
/*
  public void setCredentials(String authorization)
  Define um token de autorização que será enviado em todas as requisições que seu plugin fizer para a nossa API. Este token pode ser obtido na página de servidores no painel da sua loja.
*/

this.msdk.setCredentials("token");
```

```java
/*
  public QueueItem[] getQueueItems([nickname=""])
  Ao chamar este método, o MSDK irá buscar em nossa API todos os comandos que estão aguardando para serem entregues no seu servidor. Se você passar um nickname como parâmetro, o mesmo irá acontecer só que com os resultados filtrados para o nickname informado.
  
  Neste ponto as credenciais do seu servidor já devem ter sido
  definidas através do método setCredentials()
*/

QueueItem[] queueItems = null;

try {
  queueItems = msdk.getQueueItems();
} catch (WebServiceException | MsdkException e) {
  Logger.getLogger(getClass().getName()).log(Level.WARNING, null, e);
}

/*
  A classe `QueueItem` possui os seguintes atributos, e todos eles possuem métodos `getters` e `setters` públicos:
  
  private String uuid;
  private String nickname;
  private String command;
  private int slotsNeeded;
  private String type;
  private String status;

  Desta forma, se você quer saber qual comando tem que executar, basta chamar o método `getter` do atributo `command`, que é `getCommand()`.
*/
```

```java
/*
  public void hasBeenDelivered(String nickname, String queueItemUuid)
  Este comando marca um comando/item da fila com status de entregue para que ele saia da fila imediatamente.
*/

for (QueueItem queueItem : queueItems) {
  try {
    msdk.hasBeenDelivered(queueItem.getNickname(), queueItem.getUuid());
  } catch (WebServiceException | MsdkException e) {
    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
  }
}
```

### Considerações finais

Até aqui nós vimos os métodos disponíveis no MSDK e como utilizá-los. Mas, existem alguns pontos a serem levados em consideração:

- Este SDK não te restringe em muita coisa e por isso você pode fazer praticamente o que quiser e criar um plugin 100% adaptado ao seu servidor, mas lembre-se: "Com grandes poderes, vêm grandes responsabilidades" – Stan Lee;
- Provavelmente você quer executar a maioria dos métodos deste SDK em uma thread separada da thread principal do seu servidor, e **recomendamos** que faça isso;
- Provavelmente você deseja ter um sistema de proteção para evitar que seu plugin faça multiplas chamadas ao método `getQueueItems()` ao mesmo tempo porque isso pode ser perigoso;
