package practice;

/**
 * Этап 2 кейс-задания, задание 3: вывод возраста в одной строке.
 *
 * <p>Показаны два способа: конкатенация строк и {@code System.out.printf}.</p>
 *
 * <p>Запуск: {@code java -cp bin practice.AgeTask}</p>
 */
public class AgeTask {

    public static void main(String[] args) {
        int age = 0; // [нужно получить: ваш возраст для скриншота задания]

        // способ 1 — конкатенация
        System.out.println("Возраст обучающегося: " + age + " лет.");

        // способ 2 — форматированная строка
        System.out.printf("Возраст обучающегося: %d лет.%n", age);
    }
}
