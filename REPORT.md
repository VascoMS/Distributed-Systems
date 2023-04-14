# Relatório Gossip Architecture

Começámos por inicializar dois vectorClocks a zeros em cada servidor (valueTS e replicaTS), um  no utilizador (prev) e dois nas operações (prevTS e TS), assumindo a existência de três servidores cada um com conhecimento prévio da quantidade de servidores envolvidos.

### Leituras:
Quando o utilizador tenta efetuar uma leitura envia ao servidor o seu timestamp (prev) que este vai utilizar para verificar se o utilizador tem uma versão mais atualizada ou não que ele (comparando o valuteTS com o prev recebido). Se a versao do utilizador for maior que a do server este devolve uma mensagem ao cliente a avisar que se encontra desatualizado. Caso contrario o servidor devolve o valor lido ou erros originados pela ação de leitura.

### Escritas:
Quando o utilizador tenta efetuar uma escrita envia também ao servidor o seu timestamp (prev). Independentemente do valor do prev recebido o servidor envia sempre "OK" ao utilizador e guarda na sua log a operação incrementando o valor de replicaTS e atualizando os timestamps da operacao (TS = replicaTS e prevTS = prev do utilizador). Se o prev for menor que o valueTS este mete a operação a stable e executa-a e atualiza o valueTS do servidor fazendo merge(valuteTS, TS da operação), caso contario a operação fica registada como instável na log à espera de poder ser executada.

### Gossip:
Este operação pode ser chamada pelo admin de modo a propagar para todos os outros servidores a log e replicaTS de um servidor específico. Para passar o log e a replicaTS, é feito um lookup ao naming server que visa encontrar os servidores disponíveis caso já não estejam em cache, e criam-se channnels e stubs de forma a enviar a informação necessária para que os servidores disponíveis ponham "em dia", se necessário, as informações enviadas pelo servidor selecionado pelo admin.

Os servidores que recebem a log e o vectorClock, verificam se o seu replicaTS é menor do que cada TS das operações contidas na log recebida e caso se verifique estas sao adicionadas à log como instáveis. Se o valueTS for maior ou igual ao prevTS de cada operação adicionada, isso significa que estas são executadas e passam a ser estáveis. Após percorrer a log recebida, o servidor atualiza o seu replicaTS fazendo merge(replicaTS, replicaTS recebido), fazendo por fim uma iteração pela sua própria log verificando se ficou alguma operação possível por executar e declarar como estável, atualizando sempre o seu valueTS (merge(valueTS, TS da operação)).