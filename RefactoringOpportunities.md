# Refactoring Opportunities

| Local | Cheiro no código | Refabricação | Nº aluno |
|---|---|---|---|
| `Move::processEnemyFire` | Long Method | Extract Method | 130505 |
| `Move::processEnemyFire` | Condição complexa (`validShots == 0 && repeatedShots > 0`) | Introduce Variable | 130505 |
| `IMove::readMove` | Inappropriate Intimacy (dependência cíclica entre `IMove` e `Move`) | Move Method | 130505 |
| `MoveTimer::stopAndFormat` | Textos fixos no código | Introduce Constant | 130505 |
| `PdfExporter::PageWriter` | Nomes pouco claros (`y`, `doc`) | Rename | 130505 |
| `Game::printBoardFrame` | Duplicated Code (a linha de cima e a de baixo da moldura constroem o mesmo texto) | Extract Method | 129835 |
| `Game::over` | Duplicated Code (o mesmo texto do banner repete-se duas vezes) | Introduce Constant | 129835 |
| `Game::getRemainingShips` | Variável temporária redundante (`floatingShips` só é usada uma vez) | Inline Variable | 129835 |
| `Game::coloredMarker` | Feature Envy (só usa métodos da `GameUI`) | Move Method | 129835 |
| `Game::fireSingleShot` | Duplicated Code (`!ship.stillFloating()` calculado duas vezes) | Introduce Variable | 129835 |
| `Game::printBoard` e `Game::buildBoard` (parâmetro `show_shots`) | Mysterious Name (nome com sublinhado, fora da convenção Java) | Rename | 129835 |