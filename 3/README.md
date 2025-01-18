## Zadanie 1: Brainfuck interpreter

Należy zaimplementować interpreter języka Brainufck, zgodnie z definicją opisaną tutaj: https://en.wikipedia.org/wiki/Brainfuck.

Do testów jest używana metoda fabryczna `Brainfuck.createInstance(java.lang.String, java.io.PrintStream, java.io.InputStream, int)`. Do operacji wejścia-wyjścia nie należy używać bezpośrednio `System.in` lub `System.out`, lecz parametrów z metody, odpowiednio: `in` oraz `out`. 

**UWAGA 1:** polska wersja wikipedii zawiera błąd w opisie rozkazów! Należy stosować się do wersji anglojęzycznej.

**UWAGA 2:** specyfikacja języka nie definiuje części zachowań (np co jeśli wartość po zmniejszeniu spadnie poniżej 0). W takich wypadkach interpreter ma dowolność, testy nie obejmują tego typu nieokreślonych cech języka.

## Zadanie 2: zbudować kreator losowej planszy do gry w statki

Należy napisać kreator do losowania poprawnych plansz do gry w statki (zaimplementować `uj.wmii.pwj.collections.collections.BattleshipGenerator.generateMap`).

Metoda `defaultInstance` w interfejsie `BattleshipGenerator` jest fabryką - powinna zwracać instancję stworzonej klasy.

Plansza do gry w statki jest kwadratem 10x10 (API powinno zwracać `String` o rozmiarze 100. indeksy 0-9: pierwszy wiersz, 10-19 drugi wiersz, itd). Każde pole może zawierać element statku (maszt), oznaczony znakiem `*`, lub zawierać wodę oznaczoną przez `.`.

Statki mogą być 1, 2, 3, lub 4 masztowe. Statek to jedno, lub więcej stykających się bokiem, pole zawierające maszt. Maszty stykające się tylko rogami nie są statkiem.

Przykłady prawidłowych statków (w otoczeniu wody):
```
...
.#.  -> jednomasztowiec
...

......
.##.#. -> dwa dwumasztowce
....#.

.....
..#..  -> trójmasztowiec
.##..

.........
......##.
.####.##. -> dwa czteromasztowce
.........
```

Przykłady nieprawidłowych statków:
```
......
..#...  -> nieprawidłowy dwumasztowiec
...#..
......
.......
...#...
..#.#..  -> nieprawidłowy czteromasztowiec
...#...
```

Prawidłowa plansza zawiera: 4 jednomasztowce, 3 dwumasztowce, 2 trójmasztowce, oraz 1 czteromasztowiec. Pomiędzy statkami musi być przynajmniej jedno pole odstępu (statki nie mogą dotykać się rogami).

Przykładowa prawidłowa plansza:
```
..#.......#......#..#..#........##............##...##................#..##...#...##....#.#.......#..
```

Ta sama plansza z dodatkowe znakami końca linii co 10 znaków dla czytelności:
```
..#.......
#......#..
#..#......
..##......
......##..
.##.......
.........#
..##...#..
.##....#.#
.......#..
```
