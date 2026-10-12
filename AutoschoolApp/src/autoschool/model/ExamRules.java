package autoschool.model;

/**
 * Правила тренажёра «Автошкола» — чистые функции без графики.
 *
 * <p>Вынесены в отдельный класс, чтобы их можно было проверять автотестом
 * без запуска окна (см. {@code autoschool.tools.SmokeTest}) и чтобы логика
 * приложения была отделена от отрисовки.</p>
 */
public final class ExamRules {

    /** Сколько промахов допускается до «экзамен не сдан». */
    public static final int MAX_MISSES = 3;

    /** Очки за правильную карточку. */
    public static final int POINTS_FOR_CORRECT = 10;

    /** Штраф за карточку не той категории. */
    public static final int POINTS_FOR_WRONG = -5;

    /** Начальная скорость падения карточки, пикселей в секунду. */
    public static final double BASE_SPEED = 110.0;

    /** Прирост скорости за каждые набранные очки. */
    public static final double SPEED_GROWTH = 4.0;

    /** Предел скорости падения. */
    public static final double MAX_SPEED = 320.0;

    /** Во сколько раз быстрее падают карточки на красный сигнал светофора. */
    public static final double RED_LIGHT_FACTOR = 1.6;

    /** Интервал появления карточек, секунды. */
    public static final double SPAWN_INTERVAL = 1.10;

    /** Интервал появления карточек на красный сигнал, секунды. */
    public static final double SPAWN_INTERVAL_RED = 0.70;

    /** Сколько пикселей над крышей автомобиля считается зоной захвата. */
    public static final int CATCH_LINE_OFFSET = 10;

    /** Через сколько пойманных карточек меняется требуемая категория. */
    public static final int TARGET_CHANGE_EVERY = 5;

    /** Категории, которые встречаются в тренажёре. */
    private static final char[] CATEGORIES = {'A', 'B', 'C', 'D'};

    private ExamRules() {
        // утилитарный класс — экземпляры не нужны
    }

    /** @return список категорий (копия, чтобы нельзя было испортить внутренний массив). */
    public static char[] categories() {
        return CATEGORIES.clone();
    }

    /**
     * Скорость падения карточки.
     *
     * @param score    текущие очки
     * @param redLight горит ли красный сигнал
     */
    public static double signSpeed(int score, boolean redLight) {
        double speed = BASE_SPEED + Math.max(0, score) * SPEED_GROWTH;
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }
        if (redLight) {
            speed = speed * RED_LIGHT_FACTOR;
        }
        return speed;
    }

    /** @return интервал появления следующей карточки, секунды. */
    public static double spawnInterval(boolean redLight) {
        return redLight ? SPAWN_INTERVAL_RED : SPAWN_INTERVAL;
    }

    /** @return очки за пойманную карточку: правильная категория — плюс, чужая — штраф. */
    public static int pointsFor(char target, char card) {
        return target == card ? POINTS_FOR_CORRECT : POINTS_FOR_WRONG;
    }

    /**
     * Поймана ли карточка.
     *
     * @param signCenterX центр карточки по горизонтали
     * @param signBottom  нижняя граница карточки
     * @param carLeft     левая граница автомобиля
     * @param carRight    правая граница автомобиля
     * @param catchLine   высота, на которой срабатывает захват
     */
    public static boolean isCaught(double signCenterX, double signBottom,
                                   double carLeft, double carRight, double catchLine) {
        return signBottom >= catchLine && signCenterX >= carLeft && signCenterX <= carRight;
    }

    /**
     * Следующая требуемая категория: меняется каждый {@link #TARGET_CHANGE_EVERY}-й
     * правильно пойманный знак, иначе остаётся прежней.
     */
    public static char nextTarget(char current, int correctCatches) {
        if (correctCatches <= 0 || correctCatches % TARGET_CHANGE_EVERY != 0) {
            return current;
        }
        int index = 0;
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i] == current) {
                index = i;
                break;
            }
        }
        return CATEGORIES[(index + 1) % CATEGORIES.length];
    }

    /** @return true, если промахов набралось на «экзамен не сдан». */
    public static boolean isExamFailed(int misses) {
        return misses >= MAX_MISSES;
    }
}
