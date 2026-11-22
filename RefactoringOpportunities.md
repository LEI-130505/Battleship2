# Refactoring Opportunities

| Local | Cheiro no código | Refabricação | Nº aluno |
|---|---|---|---|
| `Move::processEnemyFire` | Long Method | Extract Method | 130505 |
| `Move::processEnemyFire` | Condição complexa (`validShots == 0 && repeatedShots > 0`) | Introduce Variable | 130505 |
| `IMove::readMove` | Inappropriate Intimacy (dependência cíclica entre `IMove` e `Move`) | Move Method | 130505 |
| `MoveTimer::stopAndFormat` | Textos fixos no código | Introduce Constant | 130505 |
| `PdfExporter::PageWriter` | Nomes pouco claros (`y`, `doc`) | Rename | 130505 |
| `Game::randomShots` | Duplicated Code (os dois `while` repetem a escolha aleatória de uma posição e o teste `!shots.contains`) | Extract Method | 129835 |
| `Game::fireShots` e `Game::fireMyShots` | Duplicated Code (a mensagem `"Must fire exactly " + NUMBER_SHOTS + " shots per move."` aparece nos dois métodos) | Introduce Constant | 129835 |
| `Game::printShips`, `Game::printMyBoard` e `Game::printAlienBoard` | Mysterious Name (`ship_pos`, `adjacent_pos`, `show_shots` e `show_legend` com sublinhado) | Rename | 129835 |
| `Ship::isInsideBoard` | Expressão repetida (`Game.BOARD_SIZE - 1` aparece duas vezes) | Introduce Variable | 129835 |
| `Ship::size` e `Ship::getSize` | Boxing desnecessário (`Integer` quando o construtor recebe `int`) | Type Migration (Integer para int) | 129835 |