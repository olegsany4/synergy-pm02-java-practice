# Производственная практика ПМ.02 «Ревьюирование программных модулей»

Проекты практики (9 семестр, группа ОКИП-12209МО, Лапинин Игорь Олегович).
Предметная область: информационная система для автошколы.
Стек: Java (JDK 21), Swing/AWT.

## Состав репозитория

| Папка | Что это | Этап кейс-задания |
|---|---|---|
| `Practice/` | консольные задания: надпись, слова в столбец, возраст | этап 2 |
| `Calc/` | мини-калькулятор (сложение, вычитание, умножение, деление) | этап 4 |
| `AutoschoolApp/` | приложение «Автошкола: тренажёр дорожных карточек» | этап 6 и кейс |

`Practice/`, `Calc/` и `AutoschoolApp/` — готовые проекты Eclipse (`.project`, `.classpath`).

## Приложение «Автошкола: тренажёр дорожных карточек»

Тренажёр проверяет знание категорий водительского удостоверения:

* сверху падают карточки категорий **A / B / C / D** (анимация);
* учебный автомобиль перемещается стрелками **← →** (или `A` / `D`) и покачивается;
* светофор переключает сигналы, жёлтый мигает; на красный карточки падают чаще и быстрее;
* поймал нужную категорию — **+10**, чужую — **−5**, пропустил карточку — промах;
* три промаха — «ЭКЗАМЕН НЕ СДАН»; `пробел` — пауза, `R` — начать заново;
* очки дублируются в заголовке окна.

Логотип и все изображения нарисованы с нуля скриптом `make_assets.py`
(кейс допускает собственные изображения); сторонние логотипы не используются.

## Структура исходного кода

```
AutoschoolApp/src/autoschool/
├── AutoschoolApp.java        точка входа, окно приложения
├── core/Assets.java          загрузка изображений
├── core/GamePanel.java       сцена, игровой цикл, отрисовка
├── model/ExamRules.java      правила тренажёра (чистые функции)
├── model/FallingSign.java    падающая карточка категории
├── tools/SmokeTest.java      31 проверка правил без запуска окна
└── *.png                     изображения приложения
```

## Сборка и запуск

```bash
JDK21=/path/to/jdk-21/Contents/Home

# приложение
cd AutoschoolApp
mkdir -p bin
"$JDK21/bin/javac" -encoding UTF-8 -d bin $(find src -name '*.java')
cp src/autoschool/*.png bin/autoschool/
"$JDK21/bin/java" -cp bin autoschool.AutoschoolApp

# проверка правил (без окна)
"$JDK21/bin/java" -cp bin autoschool.tools.SmokeTest

# консольные задания
cd ../Practice
mkdir -p bin
"$JDK21/bin/javac" -encoding UTF-8 -d bin $(find src -name '*.java')
"$JDK21/bin/java" -cp bin practice.HelloTask

# мини-калькулятор
cd ../Calc
mkdir -p bin
"$JDK21/bin/javac" -encoding UTF-8 -d bin $(find src -name '*.java')
"$JDK21/bin/java" -cp bin calc.MiniCalc
```

Флаг `-encoding UTF-8` обязателен: в коде есть русские строки.

## Импорт в Eclipse

`File → Import → General → Existing Projects into Workspace → Select root directory` →
указать эту папку (в ней сразу три проекта) → `Finish`.
