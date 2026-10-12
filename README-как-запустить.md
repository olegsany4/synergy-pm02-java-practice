# Java-проекты практики — как запустить (macOS)

Три проекта закрывают этапы кейс-задания и слайды 7–22 отчёта:

| Папка | Что это | Какой этап кейса |
|---|---|---|
| `Practice/` | консольные задания: надпись, слова в столбец, возраст | этап 2 (слайды 7–9) |
| `Calc/` | мини-калькулятор на Swing (4 операции) | этап 4 (слайд 13) |
| `AutoschoolApp/` | главное приложение: «Автошкола: тренажёр дорожных карточек» с логотипом и анимацией | этап 6 + кейс (слайд 21) |

Все три папки — готовые проекты Eclipse (есть `.project` и `.classpath`).

---

## 1. Что нужно установить (шаги 4–6 отчёта)

1. **JDK** — например Temurin 21: https://adoptium.net/temurin/releases/ → macOS → aarch64 → `.pkg`
   После установки проверить в Терминале: `java -version`
2. **Eclipse IDE for Java Developers**: https://www.eclipse.org/downloads/packages/
3. **Apache NetBeans**: https://netbeans.apache.org/front/main/download/

> Правила установки описаны в кейс-задании (`lms-исходники/kejs-zadaniya.docx`).
> Каждый шаг снимайте на скриншот по списку `../список-скриншотов.md`.

## 2. Импорт проектов в Eclipse

`File → Import → General → Existing Projects into Workspace → Select root directory` →
указать папку `java` (в ней сразу три проекта) → `Finish`.

Затем: правый клик по проекту → `Run As` → `Java Application`.

## 3. Запуск из командной строки (проверка без IDE)

```bash
# 1) консольные задания
cd java/Practice
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name '*.java')
java -cp bin practice.HelloTask
java -cp bin practice.WordsTask

# 2) проверка правил приложения (без окна, 30 проверок)
cd ../AutoschoolApp
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name '*.java')
cp src/autoschool/*.png bin/autoschool/     # картинки должны лежать рядом с классами
java -cp bin autoschool.tools.SmokeTest

# 3) само приложение (откроется окно)
java -cp bin autoschool.AutoschoolApp
```

⚠️ Флаг `-encoding UTF-8` обязателен: в коде есть русские строки.

## 4. Что делает приложение `AutoschoolApp`

- **Анимация**: карточки категорий A/B/C/D падают сверху вниз; учебный автомобиль
  ездит влево-вправо и покачивается; светофор переключает сигналы (жёлтый мигает).
  На красный сигнал карточки падают чаще и быстрее — как на экзамене в ГИБДД.
- **Логотип**: собственный логотип `logo.png` (нарисован в `tools/make_assets.py`,
  ничего чужого не используется).
- **Управление**: `←` / `→` (или `A` / `D`) — перемещать автомобиль,
  `пробел` — пауза, `R` — начать заново.
- **Правила**: поймал карточку нужной категории — +10 очков, чужую — −5,
  пропустил карточку — промах. Три промаха → «ЭКЗАМЕН НЕ СДАН».
- **Очки** дублируются в заголовке окна.

Правила вынесены в `autoschool.model.ExamRules` — это позволяет проверять логику
автотестом `autoschool.tools.SmokeTest` без запуска графики.

## 5. Структура исходников

```
AutoschoolApp/src/autoschool/
├── AutoschoolApp.java          — точка входа, окно приложения
├── core/
│   ├── Assets.java             — загрузка изображений
│   └── GamePanel.java          — сцена, игровой цикл, отрисовка
├── model/
│   ├── ExamRules.java          — правила (чистые функции)
│   └── FallingSign.java        — падающая карточка
├── tools/
│   └── SmokeTest.java          — 30 проверок правил без окна
└── *.png                       — изображения (рисуются tools/make_assets.py)
```

## 6. Если картинки не нашлись

Приложение не упадёт — оно выведет в консоль `Не найдено изображение: …` и продолжит
работу без картинок. В этом случае скопируйте `src/autoschool/*.png` рядом с
скомпилированными классами (`bin/autoschool/`) или создайте папку `assets` в корне
проекта и положите картинки туда.
