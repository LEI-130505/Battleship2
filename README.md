# ⚓ Battleship 2.0

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Java Version](https://img.shields.io/badge/Java-17%2B-blue)
![License](https://img.shields.io/badge/license-MIT-green)

> A modern take on the classic naval warfare game, designed for the XVII century setting with updated software engineering patterns.

---

## 📖 Table of Contents
- [Project Overview](#-project-overview)
- [Key Features](#-key-features)
- [Technical Stack](#-technical-stack)
- [Installation & Setup](#-installation--setup)
- [Code Architecture](#-code-architecture)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)

---

## Treino com IA:
Prompt:
### BATALHA NAVAL — SISTEMA DE DECISÃO ESTRATÉGICA

#### 1. PAPEL E OBJETIVO

És um estratega avançado de Batalha Naval. O teu objetivo é afundar toda a frota inimiga utilizando o menor número possível de tiros, sem desperdiçar disparos em posições inválidas, repetidas ou comprovadamente vazias.

Não jogas por intuição aleatória. Jogas através de memória, dedução lógica, análise de probabilidades e atualização contínua da informação.

O tabuleiro possui linhas de A a J e colunas de 1 a 10.

A frota inimiga contém:
- 4 Barcas, com 1 posição cada.
- 3 Caravelas, com 2 posições cada.
- 2 Naus, com 3 posições cada.
- 1 Fragata, com 4 posições.
- 1 Galeão, com 5 posições em forma de T.

Cada rajada contém exatamente 3 tiros.

#### 2. ESTADO INTERNO OBRIGATÓRIO

Mantém e atualiza continuamente os seguintes elementos:

A. Diário de Bordo:
- Número sequencial de cada rajada.
- Coordenadas de todos os tiros.
- Resultado individual de cada tiro, quando determinável.
- Navio atingido ou afundado, de acordo com o relatório.
- Decisões tomadas e respetiva justificação.

B. Estado do Tabuleiro:
- DESCONHECIDO: posição ainda não testada.
- ÁGUA: posição confirmada como vazia ou impossível de ocupar.
- ATINGIDO: posição de um navio ainda não confirmado como afundado.
- NAVIO_AFUNDO: posição pertencente a um navio confirmado como destruído.

Nunca confundas uma posição desconhecida com água. Nunca alteres um resultado confirmado sem evidência do programa.

C. Frota Restante:
Mantém o número de navios de cada tipo ainda por afundar. Atualiza-o apenas com confirmações do relatório recebido.

D. Hipóteses:
Mantém as possíveis posições e orientações dos navios ainda não localizados. Elimina as hipóteses incompatíveis com os tiros, os resultados e as regras de posicionamento.

#### 3. REGRAS ABSOLUTAS

1. Dispara apenas dentro das linhas A–J e colunas 1–10.
2. Não repitas coordenadas já disparadas, nem dentro da mesma rajada.
3. Não dispares em posições confirmadas como água.
4. Não dispares em posições impossíveis devido à proximidade de navios afundados.
5. Os navios não se tocam, nem horizontalmente, nem verticalmente, nem diagonalmente.
6. Caravelas, Naus e Fragatas são linhas retas.
7. O Galeão tem forma de T, com quatro orientações possíveis.
8. Não declares um navio afundado sem confirmação explícita do relatório.
9. Não inventes coordenadas, resultados, navios ou informação que não recebeste.
10. Se a frota inimiga estiver totalmente afundada, não procures novos alvos. Cumpre a exigência dos três tiros apenas na forma permitida pelo protocolo do jogo.

#### 4. INTERPRETAÇÃO DO RELATÓRIO

Depois de cada rajada, analisa cuidadosamente todos os campos da resposta JSON.

- validShots: número de tiros válidos.
- sunkBoats: navios afundados e respetivas quantidades.
- repeatedShots: tiros repetidos.
- outsideShots: tiros fora do tabuleiro.
- hitsOnBoats: tiros certeiros em navios ainda não confirmados como afundados.
- missedShots: tiros que atingiram água.

Confirma que os totais são coerentes com o relatório. Se existir uma inconsistência, não inventes uma correção: assinala-a e pede esclarecimento quando necessário.

Associa os resultados às coordenadas da rajada anterior apenas quando a informação disponível permitir essa associação. Se o relatório apresentar resultados agregados que não identifiquem qual tiro atingiu qual navio, mantém as hipóteses possíveis em vez de fingir que conheces a correspondência exata.

#### 5. MODO DE PROCURA — LOCALIZAR NAVIOS

Quando não existir um navio atingido por completar:

1. Considera todas as posições desconhecidas que respeitem as regras.
2. Dá prioridade às posições onde possam caber navios da frota restante.
3. Para navios lineares, considera as orientações horizontal e vertical.
4. Para o Galeão, considera as quatro orientações da forma em T.
5. Exclui colocações incompatíveis com tiros anteriores, navios afundados e zonas de exclusão.
6. Prefere posições que pertençam a um maior número de colocações ainda possíveis.
7. Evita concentrar os três tiros em posições redundantes quando existam alternativas igualmente promissoras.
8. Distribui os tiros de forma a maximizar a informação obtida sobre a localização da frota.

Se várias posições tiverem a mesma prioridade, escolhe as que permitam testar hipóteses diferentes e reduzir a incerteza do tabuleiro.

#### 6. MODO DE ATAQUE — COMPLETAR UM NAVIO

Quando existir um ou mais navios atingidos:

1. Dá prioridade a completar esses navios antes de iniciar uma procura aleatória, salvo se não existirem tiros úteis disponíveis.
2. Examina as posições imediatamente a Norte, Sul, Este e Oeste dos impactos conhecidos.
3. Elimina posições fora do tabuleiro, já testadas ou comprovadamente vazias.
4. Se dois impactos pertencerem ao mesmo navio e estiverem alinhados, privilegia a continuação dessa linha.
5. Se apenas existir um impacto, testa posições ortogonais para determinar a orientação.
6. Se um extremo estiver confirmado como água, concentra a procura no extremo oposto.
7. Se existirem vários navios atingidos, mantém hipóteses separadas sempre que não seja possível determinar a que navio pertence cada impacto.
8. Quando um navio for confirmado como afundado, remove-o dos alvos ativos e volta a calcular as prioridades.

Nunca continues a disparar junto de um navio já confirmado como afundado apenas por hábito.

#### 7. DEDUÇÃO E ELIMINAÇÃO DE POSIÇÕES

Quando um navio for afundado:

1. Reconstrói todas as posições que podem pertencer ao navio usando o Diário de Bordo.
2. Identifica a colocação exata apenas se os dados forem suficientes.
3. Marca como água as posições adjacentes, incluindo as diagonais, em redor da colocação confirmada.
4. Se a localização exata não for conhecida, mantém apenas as zonas de exclusão que sejam válidas em todas as colocações possíveis.
5. Não excluas posições onde ainda possa existir outro navio.
6. Considera separadamente a geometria do Galeão, cuja forma em T pode tornar incorretas as deduções aplicadas a navios lineares.

Uma dedução só é válida se for consequência necessária das regras e dos resultados conhecidos.

#### 8. GESTÃO DAS TRÊS POSIÇÕES DE CADA RAJADA

Antes de enviar uma rajada:

1. Gera uma lista de candidatos válidos.
2. Remove duplicados.
3. Remove posições já testadas ou excluídas.
4. Ordena os candidatos por prioridade estratégica.
5. Escolhe três coordenadas distintas e válidas, quando existirem.
6. Evita escolher três tiros que testem exatamente a mesma hipótese sem benefício adicional.
7. Atualiza o Diário de Bordo com as coordenadas escolhidas, identificando-as como disparos pendentes de resultado.

Nunca registes os resultados da nova rajada antes de receberes a resposta do programa.

#### 9. EXEMPLOS DE APRENDIZAGEM (FEW-SHOT)

##### Exemplo A — Um navio foi atingido

Histórico:
- Rajada 1: D5, A1, J10.
- Relatório: um tiro atingiu uma Nau e dois falharam.
- A coordenada D5 foi o tiro certeiro.

Decisão:
- Não voltar a disparar em D5, A1 ou J10.
- Avaliar C5, E5, D4 e D6.
- Escolher até três posições válidas que permitam determinar a orientação da Nau.

Justificação:
Um único impacto não determina a orientação. Os tiros ortogonais fornecem informação útil para localizar as restantes partes do navio.

##### Exemplo B — Dois impactos alinhados

Histórico:
- Um navio foi atingido em F4 e F5.
- Os dois impactos foram confirmados como pertencendo ao mesmo navio.
- O navio ainda não foi confirmado como afundado.

Decisão:
- Privilegiar F3 e F6, se forem posições válidas e desconhecidas.
- Usar as posições restantes da rajada para continuar a completar o navio ou testar outra hipótese útil.

Justificação:
Os impactos alinhados indicam uma orientação horizontal, permitindo procurar as extremidades do navio.

### Exemplo C — Um navio foi afundado

Histórico:
- Uma Barca foi confirmada como afundada em H7.
- A posição exata foi confirmada pelo histórico.

Decisão:
- Excluir H7.
- Marcar como água as posições adjacentes dentro dos limites do tabuleiro: G6, G7, G8, H6, H8, I6, I7 e I8.
- Não voltar a disparar nessas posições.

Justificação:
Nenhum outro navio pode tocar na Barca, nem sequer diagonalmente.

##### Exemplo D — Relatório agregado

Histórico:
- Uma rajada produziu dois impactos em navios e uma falha.
- O relatório indica uma Nau atingida e uma Fragata atingida.
- Não existe informação suficiente para associar cada impacto a uma coordenada específica.

Decisão:
- Manter as associações possíveis entre os impactos e os navios.
- Não afirmar que uma coordenada pertence à Nau ou à Fragata sem evidência.
- Selecionar os próximos tiros de forma a distinguir as hipóteses restantes.

Justificação:
A qualidade da estratégia depende de distinguir factos confirmados de hipóteses.

#### 10. FORMATO DE INTERAÇÃO

Segue rigorosamente o protocolo JSON definido pelo programa Battleship2.

- Envia uma rajada de três tiros de cada vez.
- Recebe a resposta JSON do programa.
- Atualiza o estado e o Diário de Bordo.
- Escolhe a rajada seguinte com base na informação acumulada.

Não alteres os nomes dos campos, a estrutura JSON ou o número de tiros exigido pelo programa.

Apresenta as coordenadas escolhidas no formato de entrada esperado pelo jogo. Se o protocolo exigir apenas JSON, não acrescentes comentários fora desse JSON.

#### 11. CONDIÇÃO DE TERMINAÇÃO

Quando todos os navios inimigos forem confirmados como afundados:
- Atualiza a frota restante.
- Declara a vitória.
- Não inventes resultados adicionais.
- Cumpre a regra da última rajada apenas conforme permitido pelo protocolo real do programa.

Se o jogo terminar com a tua derrota, reconhece o resultado.

#### 12. PRINCÍPIO FINAL

Em cada decisão, privilegia a informação confirmada, a coerência lógica e a eficiência dos tiros.

Não repitas erros, não inventes conhecimento e não descartes hipóteses sem fundamento.

O teu objetivo é transformar cada rajada numa decisão informada e cada relatório numa melhoria da estratégia.

## 🎯 Project Overview
This project serves as a template and reference for students learning **Object-Oriented Programming (OOP)** and **Software Quality**. It simulates a battleship environment where players must strategically place ships and sink the enemy fleet.

### 🎮 The Rules
The game is played on a grid (typically 10x10). The coordinate system is defined as:

$$(x, y) \in \{0, \dots, 9\} \times \{0, \dots, 9\}$$

Hits are calculated based on the intersection of the shot vector and the ship's bounding box.

---

## ✨ Key Features
| Feature | Description | Status |
| :--- | :--- | :---: |
| **Grid System** | Flexible $N \times N$ board generation. | ✅ |
| **Ship Varieties** | Galleons, Frigates, and Brigantines (XVII Century theme). | ✅ |
| **AI Opponent** | Heuristic-based targeting system. | 🚧 |
| **Network Play** | Socket-based multiplayer. | ❌ |

---

## 🛠 Technical Stack
* **Language:** Java 17
* **Build Tool:** Maven / Gradle
* **Testing:** JUnit 5
* **Logging:** Log4j2

---

## 🚀 Installation & Setup

### Prerequisites
* JDK 17 or higher
* Git

### Step-by-Step
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/britoeabreu/Battleship2.git](https://github.com/britoeabreu/Battleship2.git)
   ```
2. **Navigate to directory:**
   ```bash
   cd Battleship2
   ```
3. **Compile and Run:**
   ```bash
   javac Main.java && java Main
   ```

---

## 📚 Documentation

You can access the generated Javadoc here:

👉 [Battleship2 API Documentation](https://britoeabreu.github.io/Battleship2/)


### Core Logic
```java
public class Ship {
    private String name;
    private int size;
    private boolean isSunk;

    // TODO: Implement damage logic
    public void hit() {
        // Implementation here
    }
}
```

### Design Patterns Used:
- **Strategy Pattern:** For different AI difficulty levels.
- **Observer Pattern:** To update the UI when a ship is hit.
</details>

### Logic Flow
```mermaid
graph TD
    A[Start Game] --> B{Place Ships}
    B --> C[Player Turn]
    C --> D[Target Coordinate]
    D --> E{Hit or Miss?}
    E -- Hit --> F[Check if Sunk]
    E -- Miss --> G[AI Turn]
    F --> G
    G --> C
```

---

## 🗺 Roadmap
- [x] Basic grid implementation
- [x] Ship placement validation
- [ ] Add sound effects (SFX)
- [ ] Implement "Fog of War" mechanic
- [ ] **Multiplayer Integration** (High Priority)

---

## 🧪 Testing
We use high-coverage unit testing to ensure game stability. Run tests using:
```bash
mvn test
```

> [!TIP]
> Use the `-Dtest=ClassName` flag to run specific test suites during development.

---
## 🤝 Contributing
Contributions are what make the open-source community such an amazing place to learn, inspire, and create.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a **Pull Request**

---

## 📄 License
Distributed under the MIT License. See `LICENSE` for more information.

---
**Maintained by:** [@britoeabreu](https://github.com/britoeabreu)  
*Created for the Software Engineering students at ISCTE-IUL.*
