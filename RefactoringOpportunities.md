# Refactoring Opportunities

| Local | Cheiro no código | Refabricação | Nº aluno |
|---|---|---|---|
| `Move::processEnemyFire` | Long Method | Extract Method | 130505 |
| `Move::processEnemyFire` | Condição complexa (`validShots == 0 && repeatedShots > 0`) | Introduce Variable | 130505 |
| `IMove::readMove` | Inappropriate Intimacy (dependência cíclica entre `IMove` e `Move`) | Move Method | 130505 |
| `MoveTimer::stopAndFormat` | Textos fixos no código | Introduce Constant | 130505 |
| `PdfExporter::PageWriter` | Nomes pouco claros (`y`, `doc`) | Rename | 130505 |