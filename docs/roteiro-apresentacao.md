# Roteiro de apresentação — Movva (TGB POO)

**Duração:** até 15 min · **Apresentador:** 1 pessoa · **Ambiente:** Eclipse

## Estratégia

Em cada parte, primeiro **mostro o app fazendo algo** e depois **abro o código que faz aquilo**, explicando por que foi feito assim. O app fica rodando no Console do Eclipse do bloco 3 até o bloco 7; eu só alterno entre o Console e as abas de código.

| # | Bloco | Relógio | Duração | Requisitos cobertos |
|---|---|---|---|---|
| 1 | Abertura | 0:00 → 1:00 | 1 min | — |
| 2 | Estrutura e diagrama UML | 1:00 → 3:30 | 2,5 min | 1 (UML), relacionamentos |
| 3 | Frota: herança e polimorfismo | 3:30 → 6:00 | 2,5 min | 2, 3, 4, 5 |
| 4 | Cadastro: Factory Method | 6:00 → 7:30 | 1,5 min | padrão extra, sobrecarga |
| 5 | Locação e devolução: regras, encapsulamento, sobrecarga | 7:30 → 10:30 | 3 min | 7, encapsulamento |
| 6 | Relatórios: Strategy | 10:30 → 11:30 | 1 min | 4, 5, padrão extra |
| 7 | Persistência e Injeção de Dependência | 11:30 → 14:00 | 2,5 min | 6, padrão extra |
| 8 | Fechamento | 14:00 → 15:00 | 1 min | resumo dos 7 requisitos |

**Prioridade:** ★ = essencial, não cortar · ☆ = cortar se estiver atrasado.

**Pontos de controle do relógio:**
- **Aos 7:30**, eu deveria estar começando o bloco 5. Se estiver atrasado, corte todos os ☆ dos blocos 5 e 6.
- **Aos 12:00**, eu deveria estar no meio do bloco 7. Se não estiver, mostre só o CSV e o `Main`, e vá para o fechamento.

---

## Antes da aula (checklist)

- [ ] `git pull` e `git restore dados/`: dados de exemplo limpos (7 veículos, 4 clientes, 6 locações).
- [ ] Projeto importado no Eclipse, encoding **UTF-8** (*Properties → Resource → Text file encoding*), `Main` rodado uma vez para testar.
- [ ] Fonte do editor e do Console aumentada (*Ctrl + =*).
- [ ] Abas abertas, **nesta ordem**:
  1. `docs/movva-diagrama-uml.png`
  2. `Veiculo.java`
  3. `CarroEletrico.java`
  4. `VeiculoFactory.java`
  5. `Locadora.java`
  6. `Locacao.java`
  7. `MenuConsole.java`
  8. `PersistenciaCSV.java`
  9. `Main.java`
  10. `dados/locacoes.csv`
- [ ] Fechar outros projetos e a pasta `aulas/`.
- [ ] **Anotar a data de hoje + 3 dias** (usada no bloco 5).
- [ ] Ensaiar o roteiro inteiro uma vez, cronometrando.
- [ ] Cópia do projeto no pendrive ou na nuvem.
- [ ] Depois de ensaiar, rodar `git restore dados/` de novo.

---

## 1. Abertura (0:00 → 1:00) ★

**Tela:** diagrama aberto, ainda sem detalhar.

> "A Movva é uma locadora de veículos fictícia. Desenvolvi um sistema de console em Java puro que cobre a operação dela: frota com carros, motos e carros elétricos; cadastro de clientes; locação e devolução com cálculo de multa por atraso; relatórios; e persistência em CSV. Antes do código, modelei tudo em UML. Vou mostrar a estrutura e, para cada funcionalidade, o app rodando e o código por trás, explicando as decisões. Também apliquei três padrões de projeto da Aula 15."

---

## 2. Estrutura e diagrama UML (1:00 → 3:30) ★

**Tela:** `docs/movva-diagrama-uml.png`

**a) Pacotes: a arquitetura** ★
> "Separei o sistema por responsabilidade, seguindo o princípio da responsabilidade única da Aula 12:"
- **`modelo`**: o domínio e **todas** as regras de negócio (veículos, cliente, locação, locadora).
- **`ui`**: o `MenuConsole`, que só conversa com o usuário e não contém regra nenhuma.
- **`relatorio`** e **`persistencia`**: cada um isolado atrás de uma interface.
- **`util`**: a formatação de moeda e datas.

> "Por quê? Se amanhã o console virar uma tela gráfica, o pacote `modelo` é reaproveitado inteiro."

**b) Os seis tipos de relacionamento** ★
- **Generalização:** `Veiculo` → `Carro` / `Moto`; `Carro` → `CarroEletrico`.
- **Realização:** 5 relatórios implementam `Relatorio`; `PersistenciaCSV` implementa `Persistivel`.
- **Associação:** cada `Locacao` conhece 1 `Cliente`, 1 `Veiculo` e 1 `StatusLocacao`.
- **Agregação (◇)** de `Locadora` com `Veiculo` e `Cliente`: "eles são criados fora e só cadastrados; existem por si."
- **Composição (◆)** de `Locadora` com `Locacao`: "a locação só nasce dentro da locadora. No código, o construtor de 'nova locação' nem é público."
- **Dependência (tracejado):** relatórios e persistência *usam* a `Locadora`; a fábrica *cria* os veículos; o `Main` monta tudo.

**c) Padrões marcados no diagrama** ☆
> "Os três padrões estão marcados no diagrama com estereótipos e notas. Vou mostrar cada um quando a funcionalidade aparecer."

---

## 3. Frota: herança e polimorfismo (3:30 → 6:00) ★

**App:** rode o `Main` (*Run As → Java Application*) e clique no Console.

| Digitar | Aparece |
|---|---|
| *(abrir)* | `[OK] Dados carregados: 7 veículo(s), 4 cliente(s), 6 locação(ões).` |
| `1` → `2` | Lista da frota |

> "Repare na diária de cada veículo, com a mesma diária base dando valores diferentes:
> - Argo e Onix: +10% por terem 4 portas;
> - Up: sem acréscimo, porque tem 2 portas;
> - CG 160: R$ 68, que é 80 com 15% de desconto por ter até 300 cc;
> - Dolphin: R$ 190, que é 150 + 10% + R$ 25 de recarga.
>
> Cada tipo calcula do seu jeito."

**Deixe o app parado** (mostrando "Pressione Enter") e vá para o código:

| Mostrar | Dizer |
|---|---|
| `Veiculo.java`, linhas 10, 29 e 32 ★ | "**Classe abstrata** (requisito 4): não existe 'um veículo genérico' na locadora. Ela guarda o estado comum e **obriga** os filhos a implementar `calcularDiaria()` e `getTipo()`. Também uso interfaces: `Relatorio` e `Persistivel`." |
| `Moto.java`, linha 5 ☆ | "**Herança simples** (requisito 2): `Moto extends Veiculo`." |
| `CarroEletrico.java`, linhas 6 e 31 ★ | "**Herança multinível** (requisito 3): `CarroEletrico → Carro → Veiculo`. E o **polimorfismo** (requisito 5): o elétrico sobrescreve `calcularDiaria()` chamando **`super.calcularDiaria()`**. Ele reaproveita a regra das portas do `Carro` e só soma a taxa de recarga, em vez de copiar a regra do pai." |

> "Quem chama não sabe qual é o tipo. A listagem que vimos percorre uma `List<Veiculo>` e chama `toString()` e `calcularDiaria()`; o Java decide **em tempo de execução** qual versão rodar."

**Por que `BigDecimal`?** ☆ "Dinheiro com `double` tem erro de arredondamento; uso `BigDecimal` com 2 casas."

---

## 4. Cadastro: Factory Method (6:00 → 7:30) ★

**App:**

| Digitar | Aparece |
|---|---|
| Enter → `1` → `3` (ELETRICO) | pede os dados |
| `TES1A23` → `Volvo` → `EX30` → `2025` → `200` → `4` → `450` | `[OK] Veículo cadastrado: #8 ... Diária R$ 245,00` |
| Enter → `0` | volta ao menu principal |

> "200 + 10% + 25 = 245."

**Código:** `VeiculoFactory.java`, linha 40 ★

> "Problema: o menu recebe o tipo como uma escolha do usuário, e o CSV recebe o tipo como o texto `ELETRICO`. Alguém precisa decidir **qual classe instanciar**. Concentrei essa decisão na `VeiculoFactory`, que é o **padrão Factory Method**: ela devolve um `Veiculo`, e só ela conhece `Carro`, `Moto` e `CarroEletrico`. O menu e a persistência dependem só da abstração. Se eu criar um `Caminhao`, mudo a fábrica e crio a classe; menu e persistência não mudam. Isso é o princípio Aberto/Fechado."

☆ "Ela também tem **sobrecarga**: `criar` sem id, para o cadastro, e com id, para o CSV."

---

## 5. Locação e devolução: regras, encapsulamento, sobrecarga (7:30 → 10:30) ★

**App: nova locação** ★

| Digitar | Aparece |
|---|---|
| `3` → `1` → cliente `3` → veículo `8` → Enter (hoje) → **data de hoje + 3 dias** | `Locação #7 [ATIVA] ... Diária R$ 245,00 ... Previsto R$ 735,00` |

> "A diária foi **congelada** no contrato. Se eu aumentar o preço do Volvo agora, esta locação não muda, como numa locadora real."

**App: regra de negócio** ☆

| Digitar | Aparece |
|---|---|
| Enter → `1` → cliente `1` → veículo `8` | `[ERRO] O veículo TES1A23 já está locado.` |

**App: devolução com atraso** ★

| Digitar | Aparece |
|---|---|
| Enter → `2` → locação `5` → `24/11/2026` | `2 dia(s) de atraso ... Total R$ 1.216,00` |
| Enter → `0` | menu principal |

> "6 dias × 190 = 1.140, mais a multa de 20% da diária por dia de atraso: 2 × 38 = 76."

**Código:** `Locadora.java`

| Mostrar | Dizer |
|---|---|
| linhas 130 a 145 (`realizarLocacao`) ★ | "A `Locadora` é a guardiã das regras: cliente e veículo precisam estar cadastrados, e o veículo precisa estar disponível. Quando algo é inválido, ela lança exceção, e o menu só mostra a mensagem." |
| linhas 44 e 54 ★ | "**Sobrecarga** (requisito 7): `buscarVeiculo(int id)` e `buscarVeiculo(String placa)`. Mesmo nome, parâmetros diferentes, resolvido na **compilação**. Também há construtores sobrecarregados em `Cliente`, nos veículos e em `Locacao`, e `lerInteiro` e `lerData` no menu." |
| linhas 193 a 201 ★ | "**Encapsulamento:** as listas saem como `unmodifiableList`, então ninguém consegue fazer `getVeiculos().add(...)` e pular as validações." |
| `Locacao.java`, linhas 25 e 105 ☆ | "O construtor de nova locação e o `atribuirId` têm **visibilidade de pacote**: só a `Locadora` cria locações e define ids. É a composição do diagrama garantida pelo compilador." |
| `Locacao.java`, linha 77 (`finalizar`) ☆ | "A regra da multa fica no próprio objeto `Locacao`, que tem os dados para calcular." |

---

## 6. Relatórios: Strategy (10:30 → 11:30) ★

**App:**

| Digitar | Aparece |
|---|---|
| `4` → `2` | `Faturado (5 ...): R$ 3.382,00` e `Ticket médio: R$ 676,40` |
| Enter → `5` ☆ | Ranking, com o Argo em 1º |
| Enter → `0` | menu principal |

**Código:** `MenuConsole.java`, linhas 392 e 439 ★

> "Cada relatório implementa a interface `Relatorio` com o método `gerar()`. Esse é o **padrão Strategy**: o menu escolhe a estratégia em tempo de execução, numa variável do tipo da interface, e o `exibirRelatorio` não sabe qual relatório está rodando. Também é polimorfismo, agora por **interface**. Para um sexto relatório, crio uma classe nova sem mexer no `exibirRelatorio`."

---

## 7. Persistência e Injeção de Dependência (11:30 → 14:00) ★

**App: robustez** ☆

| Digitar | Aparece |
|---|---|
| `abc` | `[ERRO] Digite um número inteiro.` |

> "Entrada inválida não derruba o sistema: toda leitura repete até ser válida."

**App: salvar e reabrir** ★

| Ação | Aparece |
|---|---|
| `0` (sair) | `[OK] Dados salvos com sucesso.` |
| Abrir a aba `dados/locacoes.csv` (F5 para atualizar) | linha 7 nova (ATIVA); linha 5 agora FINALIZADA com 1216.00 |
| Rodar o `Main` de novo | `8 veículo(s), 4 cliente(s), 7 locação(ões).` |

**Código:** `PersistenciaCSV.java` ★

| Mostrar | Dizer |
|---|---|
| linha 62 | "**Persistência** (requisito 6): três CSV com separador `;`. Escrevo com `BufferedWriter` e `FileWriter`, e leio com `BufferedReader` e `readLine()`, como na Aula 14. Uso **try-with-resources**, que é a forma moderna do *close no finally*: o arquivo fecha mesmo se houver erro." |
| linha 156 | "Na leitura, a coluna `tipo` vai direto para a **fábrica**. É o motivo de ela existir." |
| (falar, sem mostrar) ☆ | "`IOException` é checked: a interface declara e o menu é obrigado a tratar. Se um CSV estiver corrompido, o sistema mostra arquivo e linha e **encerra sem sobrescrever** os dados bons." |

**Código:** `Main.java`, linha 16, e `MenuConsole.java`, linha 36 ★

> "**Injeção de Dependência:** o `Main` cria a `PersistenciaCSV` e entrega ao menu pelo construtor. O menu só conhece a interface `Persistivel`. Para trocar CSV por banco de dados, mudo uma linha no `Main`, e o menu continua igual."

---

## 8. Fechamento (14:00 → 15:00) ★

**Tela:** diagrama de volta.

> "Recapitulando os requisitos:
> 1. diagrama UML com todos os relacionamentos;
> 2. herança simples, `Veiculo → Moto`;
> 3. herança multinível, `Veiculo → Carro → CarroEletrico`;
> 4. classe abstrata `Veiculo` e as interfaces `Relatorio` e `Persistivel`;
> 5. polimorfismo em `calcularDiaria`, `toString` e nos relatórios;
> 6. persistência em CSV;
> 7. sobrecarga em buscas, construtores e na fábrica.
>
> Como extra, três padrões de projeto, cada um resolvendo um problema real: Factory Method na criação dos veículos, Strategy nos relatórios e Injeção de Dependência na persistência. Obrigado, fico à disposição para perguntas."

**Depois da apresentação:** `git restore dados/`.

---

## Plano de corte (se estiver atrasado)

Por ordem, corte primeiro:
1. ☆ do bloco 5: o erro de veículo locado e a parte de `Locacao.java`.
2. ☆ do bloco 6: o ranking.
3. ☆ do bloco 7: o teste com `abc` e a fala sobre `IOException`.
4. ☆ dos blocos 3 e 4: `Moto.java`, `BigDecimal` e a sobrecarga da fábrica.
5. No bloco 7, em último caso: não reabra o app, apenas mostre o CSV atualizado.

**Nunca corte:** diagrama e pacotes, `CarroEletrico` com `super`, a fábrica, a devolução com multa, `buscarVeiculo` sobrecarregado, o CSV e o fechamento com os 7 requisitos.

## Plano B (se algo falhar ao vivo)

- **O app não roda:** explique pelo código e mostre os CSV de `dados/` como prova da persistência.
- **Errou uma digitação na demo:** o sistema mostra `[ERRO]` e pede de novo. Use isso: "olha a validação funcionando".
- **Data de hoje + 3 rejeitada:** a previsão precisa ser **depois** da retirada. Digite uma data mais adiante.

---

## Possíveis perguntas e respostas

### Sobre os requisitos

**"Por que `Veiculo` é classe abstrata e não interface?"**
Os veículos compartilham **estado** (placa, marca, modelo, ano, diária) e **comportamento** (validações, `alterarDiariaBase`, `toString`). A classe abstrata permite herdar isso e ainda obriga cada filho a implementar `calcularDiaria()`. `Relatorio` e `Persistivel` são interfaces porque definem só um contrato, sem estado comum.

**"Qual a diferença entre sobrecarga e sobrescrita no seu projeto?"**
- *Sobrecarga:* mesmo nome e parâmetros diferentes, resolvida na **compilação**. Exemplo: `buscarVeiculo(int)` e `buscarVeiculo(String)`.
- *Sobrescrita:* mesma assinatura numa subclasse, resolvida na **execução** pelo tipo real do objeto. Exemplo: `calcularDiaria()`.

**"Onde exatamente está o polimorfismo?"**
- `calcularDiaria()`, `getTipo()` e `toString()` nas três subclasses de `Veiculo`, que são chamados por `Locacao`, pelo menu e pelos relatórios **sem saber o tipo concreto**;
- `gerar()` nos 5 relatórios, chamados pela interface;
- `salvar()` e `carregar()` chamados pela interface `Persistivel`.

**"Por que `CarroEletrico` herda de `Carro`, e não direto de `Veiculo`?"**
Porque um carro elétrico **é um** carro: tem portas e a mesma regra de diária por portas. Herdando de `Carro`, ele reaproveita essa regra com `super.calcularDiaria()` e só acrescenta a taxa de recarga.

**"Onde está a alocação dinâmica?"**
Todos os objetos são criados com `new` em tempo de execução, conforme o usuário cadastra ou os CSV são lidos. Eles ficam em `ArrayList`s que crescem sob demanda, na `Locadora`.

### Sobre a modelagem

**"Por que composição com `Locacao`, mas agregação com `Veiculo` e `Cliente`?"**
Veículos e clientes existem por si e são criados fora da locadora, que só os registra. A locação só nasce dentro da `Locadora`, por `realizarLocacao`, já que o construtor de nova locação tem visibilidade de pacote. Ela pertence à locadora.

**"Por que `atribuirId` não é público?"**
Para que só a `Locadora`, que está no mesmo pacote, controle os ids e garanta que não se repitam. Fora do pacote, ninguém consegue alterar o id de um objeto.

**"Por que `getVeiculos()` devolve uma lista não modificável?"**
Para manter o encapsulamento. Se a lista original saísse, alguém poderia fazer `add` direto e pular as validações de placa duplicada de `cadastrarVeiculo`.

**"A `Locadora` não está fazendo coisas demais?"**
Ela coordena cadastros e locações, que são o núcleo do negócio. As outras responsabilidades já foram separadas: cálculo de diária nos veículos, multa na `Locacao`, relatórios em classes próprias, arquivos na persistência, interação no menu. Se crescesse, eu separaria em serviços (por exemplo, `ServicoDeLocacao`).

**"Por que `StatusLocacao` é um enum?"**
Porque o status tem um conjunto fechado de valores (`ATIVA` e `FINALIZADA`). O enum impede valores inválidos, que uma `String` permitiria (por exemplo, "ativo" com erro de digitação).

**"Por que o menu não tem regras de negócio?"**
Responsabilidade única. O menu lê, chama o modelo e mostra o resultado. As validações ficam nos objetos, então o mesmo modelo funcionaria com outra interface.

### Sobre os padrões de projeto

**"Esse Factory Method é o do GoF?"**
Segui a definição do slide da Aula 15: *"retorna uma instância dentre muitas possíveis classes, dependendo dos dados providos"*. No GoF "clássico", subclasses de um criador sobrescrevem o método de fábrica. A minha é a variação estática, adequada porque a decisão depende de um dado de entrada (o tipo vindo do menu ou do CSV), e não de uma hierarquia de criadores.

**"Qual a vantagem real do Strategy aqui?"**
Adicionar um relatório não exige mudar o código que exibe relatórios. Cada relatório fica isolado, pequeno e fácil de testar.

**"E da Injeção de Dependência?"**
O menu não fica "preso" ao CSV. A implementação é escolhida num único lugar, o `Main`. Trocar para JSON ou banco de dados, ou passar uma persistência falsa para testes, não exige mudar o menu (Inversão de Dependência, Aula 12).

**"Por que não usou Singleton na `Locadora`?"**
Não havia necessidade. O `Main` cria uma única `Locadora` e a injeta onde precisa. Um Singleton criaria um acesso global e dificultaria trocar ou testar a instância.

### Sobre decisões técnicas

**"Na persistência você usa `instanceof`. Isso não vai contra o polimorfismo?"**
É uma escolha consciente. A persistência precisa ler atributos que só existem nas subclasses, como portas, cilindradas e autonomia. A alternativa seria cada veículo saber se converter em CSV, mas aí o modelo ficaria acoplado ao formato do arquivo. Preferi manter o domínio limpo e concentrar isso na camada de persistência. Um detalhe: `v instanceof Carro` também é verdadeiro para um `CarroEletrico`, porque ele **é um** carro.

**"Por que `BigDecimal` e não `double`?"**
`double` tem erro de arredondamento binário (`0.1 + 0.2` dá `0.30000000000000004`). Para dinheiro, uso `BigDecimal` com 2 casas e arredondamento `HALF_UP`.

**"E se mudar o preço de um carro que está alugado?"**
Não afeta a locação, porque `valorDiariaContratada` é gravada no momento da locação. Isso também se mantém no CSV: ao carregar, a locação é **restaurada** com os valores gravados, e não recalculada.

**"O que acontece se o CSV estiver corrompido?"**
O sistema informa o arquivo, a linha e o motivo (por exemplo, `veiculos.csv, linha 4: valor numérico inválido`) e **encerra sem salvar**. Se continuasse, salvaria por cima do arquivo bom uma versão incompleta.

**"E se a pasta `dados/` não existir?"**
O sistema inicia vazio e cria a pasta ao salvar (`File.mkdirs()`, Aula 14).

**"Por que `IllegalArgumentException` e `IllegalStateException`?"**
- `IllegalArgumentException`: o **dado** é inválido (CPF com 10 dígitos, placa repetida).
- `IllegalStateException`: o dado está certo, mas o **objeto está no estado errado** (veículo já locado, locação já finalizada).

**"Por que validar no construtor?"**
Assim um objeto inválido nunca chega a existir. Por exemplo, não há como existir um `Cliente` sem CPF válido.

**"Como você testou?"**
Manualmente, pelo console, a cada etapa do desenvolvimento:
- cenários de sucesso;
- erros esperados (duplicidades, veículo locado, devolução duplicada);
- entradas inválidas (letras, datas como 31/02);
- persistência (salvar, reabrir, CSV corrompido).

### Sobre evolução

**"Como adicionaria um Caminhão?"**
1. Criar `class Caminhao extends Veiculo` com a própria `calcularDiaria()`.
2. Adicionar um `case` na `VeiculoFactory`.
3. Tratar o atributo específico na persistência.

O menu já lista os tipos a partir de `VeiculoFactory.TIPOS`, e os relatórios funcionam sem mudança.

**"O que você melhoraria com mais tempo?"**
- testes automatizados com JUnit;
- exceções próprias do domínio (por exemplo, `VeiculoIndisponivelException`);
- o padrão State para o ciclo de vida da locação, se surgissem mais estados (reservada, cancelada);
- busca e cancelamento de locações;
- validação real do dígito verificador do CPF.
