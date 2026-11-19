# Roteiro de apresentação — Movva (TGB POO)

**Duração:** até 15 min · **Grupo:** 5 integrantes · **Ambiente:** Eclipse

| Bloco | Tempo | Quem | Conteúdo |
|---|---|---|---|
| 1. Abertura | 1 min | Integrante 1 | Problema e visão geral |
| 2. Modelagem (UML) | 2,5 min | Integrante 2 | Diagrama, pacotes, relacionamentos |
| 3. Herança e polimorfismo | 3 min | Integrante 3 | Requisitos 2, 3, 4 e 5 |
| 4. Sobrecarga, encapsulamento e persistência | 2,5 min | Integrante 4 | Requisitos 6 e 7 |
| 5. Padrões de projeto | 2 min | Integrante 5 | Factory Method, Strategy, Injeção de Dependência |
| 6. Demonstração ao vivo | 3,5 min | Integrante 1 (pilota) + quem explicou cada parte | Sistema rodando |
| 7. Fechamento | 0,5 min | Integrante 5 | Resumo e abertura para perguntas |

> Troque "Integrante N" pelos nomes. Quem pilota a demo não deve ser quem está falando: um opera o teclado e o outro narra.

---

## Antes da aula (checklist)

- [ ] `git pull` e `git restore dados/`: dados de exemplo limpos (7 veículos, 4 clientes, 6 locações).
- [ ] Abrir no Eclipse, confirmar **UTF-8** (*Properties → Resource → Text file encoding*) e rodar o `Main` uma vez para testar.
- [ ] Aumentar a fonte do editor e do console (*Ctrl + =* ou *Preferences → Appearance → Colors and Fonts*).
- [ ] Deixar abertas, em abas, na ordem do roteiro:
  1. `docs/movva-diagrama-uml.png`
  2. `Veiculo.java`
  3. `Carro.java`
  4. `CarroEletrico.java`
  5. `Moto.java`
  6. `Locadora.java`
  7. `VeiculoFactory.java`
  8. `MenuConsole.java`
  9. `PersistenciaCSV.java`
  10. `dados/locacoes.csv`
- [ ] Fechar a pasta `aulas/` e outros projetos no Eclipse para não poluir a tela.
- [ ] Ensaiar a demo do bloco 6 pelo menos uma vez, cronometrando.
- [ ] Ter uma cópia do projeto num pendrive ou na nuvem, caso o computador da sala falhe.

---

## 1. Abertura (1 min): Integrante 1

**Fala sugerida:**
> "A Movva é uma locadora de veículos fictícia. Nosso sistema roda no console, em Java puro, e cobre o dia a dia de uma locadora: cadastrar a frota (carros, motos e carros elétricos) e os clientes, registrar locações e devoluções com cálculo de multa por atraso, emitir relatórios e salvar tudo em arquivos CSV. Antes de escrever código, modelamos o sistema em UML. Vamos mostrar o diagrama, como cada requisito foi atendido, os padrões de projeto que usamos e, no final, o sistema rodando."

---

## 2. Modelagem UML (2,5 min): Integrante 2

**Tela:** `docs/movva-diagrama-uml.png`

**Roteiro da fala:**
1. **Pacotes, organizados por responsabilidade:**
   - `modelo`: domínio e regras de negócio;
   - `ui`: menu do console;
   - `relatorio`;
   - `persistencia`;
   - `util`.
   - O menu não tem regra de negócio, só conversa com o usuário (responsabilidade única, Aula 12).
2. **Os seis tipos de relacionamento do diagrama:**
   - **Generalização:** `Veiculo` → `Carro` / `Moto`, e `Carro` → `CarroEletrico`.
   - **Realização:** as 5 classes de relatório implementam `Relatorio`; `PersistenciaCSV` implementa `Persistivel`.
   - **Associação:** uma `Locacao` conhece 1 `Cliente`, 1 `Veiculo` e 1 `StatusLocacao`.
   - **Agregação (◇):** `Locadora` e `Veiculo` / `Cliente`. Eles são criados fora e apenas cadastrados na locadora.
   - **Composição (◆):** `Locadora` e `Locacao`. A locação só é criada pela própria locadora e não existe fora dela. No código, o construtor de "nova locação" nem é público.
   - **Dependência (tracejado):** relatórios e persistência *usam* a `Locadora`; a fábrica *cria* os veículos.
3. **Destaques visuais:** os três padrões estão marcados no diagrama com estereótipos e notas.

---

## 3. Herança e polimorfismo (3 min): Integrante 3

**Tela:** `Veiculo.java`, depois `Moto.java`, `Carro.java` e `CarroEletrico.java`

| Requisito | Mostrar | O que dizer |
|---|---|---|
| **Classe abstrata** (req. 4) | `Veiculo.java`, linhas 10, 29 e 32 | "`Veiculo` é abstrata porque não existe 'um veículo genérico' na locadora. Ela declara `calcularDiaria()` e `getTipo()` sem corpo, e cada filho é **obrigado** a implementar. Também temos interfaces: `Relatorio` e `Persistivel`." |
| **Herança simples** (req. 2) | `Moto.java`, linha 5 | "`Moto extends Veiculo`: herda placa, marca, modelo, ano e diária, e acrescenta as cilindradas." |
| **Herança multinível** (req. 3) | `CarroEletrico.java`, linha 6 | "`CarroEletrico extends Carro extends Veiculo`: três níveis. O elétrico é um carro, e todo carro é um veículo." |
| **Polimorfismo** (req. 5) | `Carro.java` linha 28, `Moto.java` linha 29, `CarroEletrico.java` linha 31 | "O mesmo método `calcularDiaria()` tem três comportamentos. Carro: +10% com 4 portas ou mais. Moto: −15% até 300 cc. O elétrico chama **`super.calcularDiaria()`**, reaproveitando a regra do carro, e soma R$ 25 de recarga. Ele não copia a regra do pai, ele a estende." |

**Frase de impacto:**
> "A `Locacao` chama `veiculo.calcularDiaria()` (`Locacao.java`, linha 27) sem saber se é carro, moto ou elétrico. O Java decide em tempo de execução qual versão rodar. Isso é polimorfismo. Também acontece com `toString()`, `getTipo()` e com os 5 relatórios, por meio da interface."

---

## 4. Sobrecarga, encapsulamento e persistência (2,5 min): Integrante 4

**Tela:** `Locadora.java`, depois `Cliente.java` e `PersistenciaCSV.java`

1. **Sobrecarga (req. 7):**
   - `Locadora.java`, linhas 44 e 54: `buscarVeiculo(int id)` e `buscarVeiculo(String placa)`, com o mesmo nome e parâmetros diferentes.
   - `Cliente.java`, linhas 11 e 16: dois construtores. Um é para o cadastro novo, sem id. O outro é para restaurar do CSV, com o id.
   - Também há sobrecarga em `VeiculoFactory.criar`, em `MenuConsole.lerInteiro` (com e sem intervalo) e em `lerData` (com e sem data padrão).
   - "Sobrecarga é decidida na **compilação**, pela assinatura. Sobrescrita é decidida na **execução**, pelo objeto."
2. **Encapsulamento:**
   - Todos os atributos são `private` e não existem setters "soltos".
   - `atribuirId(int)` tem **visibilidade de pacote** (`Cliente.java`, linha 31): só a `Locadora` consegue definir ids.
   - As listas saem da `Locadora` como `unmodifiableList` (`Locadora.java`, linhas 193 a 201): ninguém pula as validações fazendo `getVeiculos().add(...)`.
3. **Persistência (req. 6):**
   - Três CSV com separador `;`.
   - Gravação com `BufferedWriter` + `FileWriter` e leitura com `BufferedReader` + `FileReader` e `readLine()`, como na Aula 14.
   - **try-with-resources** (`PersistenciaCSV.java`, linha 62): o arquivo é sempre fechado.
   - `IOException` é *checked*: a interface declara `throws` e o menu é obrigado a tratar.
   - Carrega ao iniciar e salva ao sair ou pela opção 5.
   - Se um CSV estiver corrompido, o sistema mostra arquivo e linha e **encerra sem sobrescrever** os dados bons.

---

## 5. Padrões de projeto (2 min): Integrante 5

"Não era requisito, mas aplicamos três padrões vistos na Aula 15, cada um resolvendo um problema real do sistema."

1. **Factory Method (criacional):** `VeiculoFactory.java`, linha 40.
   - **Problema:** ao ler o `veiculos.csv`, chega o texto `"ELETRICO"`. Quem decide qual classe instanciar? O mesmo acontece no menu de cadastro.
   - **Solução:** `VeiculoFactory.criar(tipo, ...)` devolve um `Veiculo`, que é um `Carro`, uma `Moto` ou um `CarroEletrico`. É **o único lugar** do sistema que conhece as classes concretas. O menu e a persistência (`PersistenciaCSV.java`, linha 156) só conhecem `Veiculo`.
   - **Ganho:** para criar um `Caminhao`, basta a classe nova e um `case` na fábrica. Menu e persistência ficam intactos (Aberto/Fechado).
2. **Strategy (comportamental):** `Relatorio.java` e `MenuConsole.java`, linhas 392 e 439.
   - Cada relatório é um algoritmo diferente atrás da mesma interface `gerar(locadora)`.
   - O menu escolhe a estratégia em tempo de execução, e `exibirRelatorio(Relatorio)` não sabe qual está rodando.
3. **Injeção de Dependência:** `Main.java`, linha 16, e `MenuConsole.java`, linha 36.
   - O `Main` cria a `PersistenciaCSV` e entrega ao menu pelo construtor.
   - O menu depende só da interface `Persistivel`. Para trocar CSV por banco de dados, muda-se uma linha no `Main`.

---

## 6. Demonstração ao vivo (3,5 min)

**Rodar:** botão direito em `Main.java` → *Run As → Java Application*. Clique no Console antes de digitar.

Os valores abaixo foram conferidos numa execução real com os dados de exemplo.

| # | Digitar | O que deve aparecer | O que narrar |
|---|---|---|---|
| a | *(abrir)* | `[OK] Dados carregados: 7 veículo(s), 4 cliente(s), 6 locação(ões).` | "Os dados vieram dos CSV." |
| b | `1` → `2` | Lista da frota | "**Polimorfismo:** Argo e Onix com +10% (4 portas), Up sem acréscimo (2 portas), CG 160 com desconto (R$ 68,00), Dolphin com R$ 190,00 = 165 + 25 de recarga." |
| c | Enter → `1` → `3` (ELETRICO) → placa `TES1A23` → `Volvo` → `EX30` → `2025` → `200` → `4` → `450` | `[OK] Veículo cadastrado: #8 ... Diária R$ 245,00` | "O menu passou o tipo para a **fábrica**, que criou um `CarroEletrico`: 200 + 10% + 25 = 245." |
| d | Enter → `0` → `3` → `1` → cliente `3` → veículo `8` → Enter (hoje) → **hoje + 3 dias** (ex.: `22/11/2026`) | `Locação #7 [ATIVA] ... Previsto R$ 735,00` | "A diária foi **congelada** no contrato." |
| e | Enter → `1` → cliente `1` → veículo `8` | `[ERRO] O veículo TES1A23 já está locado.` | "Regra de negócio: o mesmo carro não pode estar em duas locações ativas." |
| f | Enter → `2` → locação `5` → `24/11/2026` | `2 dia(s) de atraso ... Total R$ 1.216,00` | "6 dias × 190 = 1.140, mais multa de 20% × 190 × 2 dias = 76." |
| g | Enter → `0` → `4` → `2` | `Faturado (5 ...): R$ 3.382,00`, `Ticket médio R$ 676,40` | "Cada relatório é uma **estratégia**." |
| h | Enter → `5` | Ranking, com o Argo em 1º | (seguir rápido) |
| i | Enter → `0` → digitar `abc` | `[ERRO] Digite um número inteiro.` | "Entrada inválida não derruba o sistema." |
| j | `0` (sair) | `[OK] Dados salvos com sucesso.` | Abrir `dados/locacoes.csv`: "a locação 7 está gravada, e a 5 agora está FINALIZADA." |
| k | *(rodar de novo)* | `8 veículo(s), 4 cliente(s), 7 locação(ões).` | "Os dados persistiram." |

**Se o tempo apertar:** pule os passos e e h.

**Plano B:** se a demo travar, mostre os CSV e o relatório pelo código.

**Depois da apresentação:** `git restore dados/` para voltar aos dados de exemplo.

> ⚠️ No passo d, a previsão precisa ser **depois de hoje**, porque o Enter usa a data do dia. No passo f, a data `24/11/2026` funciona em qualquer dia, porque a locação 5 tem datas fixas no CSV.

---

## 7. Fechamento (30 s): Integrante 5

> "Resumindo: atendemos os 7 requisitos (diagrama UML, herança simples e multinível, classe abstrata e interfaces, polimorfismo, persistência em CSV e sobrecarga) e acrescentamos três padrões de projeto que resolvem problemas reais do sistema. Obrigado. Ficamos à disposição para perguntas."

---

## Perguntas prováveis do professor

**"Por que `Veiculo` é classe abstrata e não interface?"**
Porque os veículos compartilham **estado** (placa, marca, modelo, ano, diária) e **comportamento** (validações, `alterarDiariaBase`, `toString`). A classe abstrata permite herdar isso e ainda obriga cada filho a implementar `calcularDiaria()`. Já `Relatorio` e `Persistivel` são interfaces porque definem só um contrato, sem estado em comum.

**"Qual a diferença entre sobrecarga e sobrescrita no projeto de vocês?"**
- *Sobrecarga:* mesmo nome e parâmetros diferentes, na mesma classe, resolvida na compilação. Exemplo: `buscarVeiculo(int)` e `buscarVeiculo(String)`.
- *Sobrescrita:* mesma assinatura em classes diferentes da hierarquia, resolvida na execução. Exemplo: `calcularDiaria()`.

**"O Factory Method de vocês é o do GoF?"**
Seguimos a definição do slide da Aula 15: *"retorna uma instância dentre muitas possíveis classes, dependendo dos dados providos"*. No GoF "clássico", subclasses de um criador sobrescrevem o método de fábrica. A nossa é a variação estática, adequada porque a decisão depende de um dado de entrada (o tipo vindo do menu ou do CSV), e não de uma hierarquia de criadores.

**"Por que a `Locadora` tem composição com `Locacao`, mas agregação com `Veiculo` e `Cliente`?"**
Veículos e clientes são criados fora e apenas registrados: fazem sentido por si só. A locação só nasce dentro da `Locadora`, por `realizarLocacao`, já que o construtor de "nova locação" tem visibilidade de pacote. Ela pertence à locadora.

**"Na persistência vocês usam `instanceof`. Isso não vai contra o polimorfismo?"**
É um ponto de atenção consciente. A persistência precisa ler atributos que só existem nas subclasses, como portas, cilindradas e autonomia. A alternativa seria cada veículo saber se converter em CSV, mas aí o modelo ficaria acoplado ao formato do arquivo, contra a responsabilidade única. Preferimos manter o domínio limpo e concentrar esse conhecimento na camada de persistência. Curiosidade: `v instanceof Carro` também é verdadeiro para um `CarroEletrico`, porque ele **é um** carro.

**"Por que `BigDecimal` e não `double`?"**
`double` tem erro de arredondamento binário (`0.1 + 0.2 != 0.3`). Para dinheiro, usamos `BigDecimal` com 2 casas e `HALF_UP`.

**"E se mudar o preço de um carro que está alugado?"**
Não afeta a locação: `valorDiariaContratada` é gravada no momento da locação, como num contrato real.

**"O que acontece se o CSV estiver corrompido?"**
O sistema informa o arquivo, a linha e o motivo, e **encerra sem salvar**. Se continuasse, sobrescreveria o arquivo bom com dados incompletos.

**"Como adicionariam um novo tipo de veículo, por exemplo um Caminhão?"**
1. Criar `class Caminhao extends Veiculo` com a própria `calcularDiaria()`.
2. Adicionar um `case` na `VeiculoFactory`.
3. Tratar o atributo específico na persistência.

O menu de cadastro já mostra os tipos a partir de `VeiculoFactory.TIPOS`, e os relatórios funcionam sem mudança.

**"Onde está a alocação dinâmica?"**
Todos os objetos são criados com `new` em tempo de execução, conforme o usuário cadastra, e guardados em `ArrayList`s que crescem sob demanda (`Locadora.java`).
