<!-- 129835 - Afonso da Cota Bonito -->
# Checkstyle: quality gate para nomes de métodos

**Regra escolhida:** `MethodName`, com o formato `^[a-z][a-zA-Z0-9]*$` (os nomes dos métodos começam por letra minúscula e seguem camelCase, sem sublinhados).

**Ficheiros:**
- `.github/workflows/checkstyle-129835.yaml` (workflow)
- `Checkstyle/checkstyle-129835.xml` (configuração do Checkstyle)

## Execuções

Pull request #22, com o ficheiro de teste `NomeErradoCheckstyle.java`:

- Execução que **falha** (método `Calcular_Pontos`, que não respeita o formato): https://github.com/LEI-130505/Battleship2/actions/runs/42308410217/job/128203520090?pr=22
- Execução que **passa** (método renomeado para `calcularPontos`, 0 erros): https://github.com/LEI-130505/Battleship2/actions/runs/42308918438/job/128205224522?pr=22

## Proteção do ramo principal

Não foi possível ativar a regra de proteção do `master`. O repositório pertence a outro membro do grupo (LEI-130505) e o meu utilizador não tem acesso às definições do repositório, por isso o botão de merge continua disponível mesmo com o gate vermelho. Só o proprietário pode exigir o check "Checkstyle gate 129835" como obrigatório.

## O que o Checkstyle não consegue verificar

O Checkstyle só olha para a forma do nome, não para o seu significado. Um método chamado `calcularPontos` passa na regra mesmo que, na prática, faça outra coisa, e um nome como `xyz` também passa, porque está em minúsculas e sem sublinhados. Se o nome é claro e descreve bem o que o método faz continua a depender da revisão humana.

A regra também só se aplica a métodos. Os construtores, os nomes de classes, variáveis e constantes ficam de fora, a não ser que se acrescentem as regras próprias para isso, como `TypeName` ou `LocalVariableName`.
