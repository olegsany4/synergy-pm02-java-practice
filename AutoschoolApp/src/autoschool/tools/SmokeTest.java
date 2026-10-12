package autoschool.tools;

import autoschool.model.ExamRules;
import autoschool.model.FallingSign;

/**
 * Проверка правил тренажёра без запуска окна (headless).
 *
 * <p>Запуск: {@code java -cp bin autoschool.tools.SmokeTest}</p>
 *
 * <p>Тесты разбиты по видам из индивидуального задания:
 * позитивные (правила работают), негативные (неверные данные не ломают логику),
 * граничные (значения на границе зоны захвата).</p>
 */
public final class SmokeTest {

    private static int passed = 0;
    private static int failed = 0;

    private SmokeTest() {
    }

    public static void main(String[] args) {
        System.out.println("Проверка правил тренажёра «Автошкола»");
        System.out.println("------------------------------------");

        // 1. Скорость падения растёт с очками и ускоряется на красный сигнал
        check("скорость при 0 очков равна базовой",
                ExamRules.signSpeed(0, false) == ExamRules.BASE_SPEED);
        check("скорость растёт вместе с очками",
                ExamRules.signSpeed(20, false) > ExamRules.signSpeed(0, false));
        check("скорость не превышает предел",
                ExamRules.signSpeed(100000, false) == ExamRules.MAX_SPEED);
        check("красный сигнал ускоряет падение",
                ExamRules.signSpeed(0, true) > ExamRules.signSpeed(0, false));
        check("отрицательные очки не замедляют падение ниже базовой скорости",
                ExamRules.signSpeed(-50, false) == ExamRules.BASE_SPEED);

        // 2. Интервал появления карточек
        check("на красный карточки появляются чаще",
                ExamRules.spawnInterval(true) < ExamRules.spawnInterval(false));

        // 3. Начисление очков
        check("правильная категория даёт плюс", ExamRules.pointsFor('B', 'B') > 0);
        check("чужая категория даёт штраф", ExamRules.pointsFor('B', 'C') < 0);

        // 4. Зона захвата
        double carLeft = 400;
        double carRight = 520;
        double catchLine = 546;
        check("карточка над машиной и по центру — поймана",
                ExamRules.isCaught(460, 550, carLeft, carRight, catchLine));
        check("карточка сбоку — не поймана",
                !ExamRules.isCaught(200, 550, carLeft, carRight, catchLine));
        check("карточка выше линии захвата — не поймана",
                !ExamRules.isCaught(460, 500, carLeft, carRight, catchLine));
        check("граница зоны захвата: ровно на линии — поймана",
                ExamRules.isCaught(460, catchLine, carLeft, carRight, catchLine));
        check("граница зоны захвата: ровно у левого края — поймана",
                ExamRules.isCaught(carLeft, 550, carLeft, carRight, catchLine));
        check("граница зоны захвата: чуть левее края — не поймана",
                !ExamRules.isCaught(carLeft - 0.5, 550, carLeft, carRight, catchLine));

        // 5. Смена требуемой категории
        check("категория не меняется до 5 попаданий",
                ExamRules.nextTarget('A', 4) == 'A');
        check("категория меняется на 5-м попадании",
                ExamRules.nextTarget('A', 5) == 'B');
        check("после D категория возвращается к A",
                ExamRules.nextTarget('D', 5) == 'A');
        check("нулевое число попаданий не меняет категорию",
                ExamRules.nextTarget('C', 0) == 'C');

        // 6. Итог экзамена
        check("1 промах — экзамен продолжается", !ExamRules.isExamFailed(1));
        check("2 промаха — экзамен продолжается", !ExamRules.isExamFailed(2));
        check("3 промаха — экзамен не сдан", ExamRules.isExamFailed(ExamRules.MAX_MISSES));

        // 7. Поведение карточки
        FallingSign sign = new FallingSign('A', 100, 0, 200, 78);
        sign.fall(0.5);
        check("за 0.5 с карточка пролетела 100 пикселей",
                Math.abs(sign.getY() - 100) < 0.001);
        sign.fall(0);
        check("нулевой шаг времени не двигает карточку",
                Math.abs(sign.getY() - 100) < 0.001);
        sign.fall(-1);
        check("отрицательный шаг времени игнорируется",
                Math.abs(sign.getY() - 100) < 0.001);
        check("центр карточки считается верно",
                Math.abs(new FallingSign('A', 10, 0, 100, 78).centerX() - 49) < 0.001);
        check("нижняя граница карточки считается верно",
                Math.abs(new FallingSign('A', 0, 20, 100, 78).bottom() - 98) < 0.001);
        check("карточка ниже поля считается пропущенной",
                new FallingSign('A', 0, 700, 100, 78).isOutOfField(620));
        check("карточка внутри поля не считается пропущенной",
                !new FallingSign('A', 0, 300, 100, 78).isOutOfField(620));

        // 8. Категории
        char[] categories = ExamRules.categories();
        check("категорий ровно четыре", categories.length == 4);
        check("первая категория A, последняя D",
                categories[0] == 'A' && categories[3] == 'D');
        categories[0] = 'Z';
        check("внутренний список категорий нельзя испортить снаружи",
                ExamRules.categories()[0] == 'A');

        System.out.println("------------------------------------");
        System.out.println("Пройдено: " + passed + ", провалено: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[ OK ]  " + name);
        } else {
            failed++;
            System.out.println("[FAIL]  " + name);
        }
    }
}
