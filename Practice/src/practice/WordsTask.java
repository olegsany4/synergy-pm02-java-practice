package practice;

/**
 * Этап 2 кейс-задания, задание 2: вывод слов в столбец.
 *
 * <p>Каждое слово печатается отдельным вызовом {@code System.out.println()},
 * поэтому в консоли получается столбец.</p>
 *
 * <p>Запуск: {@code java -cp bin practice.WordsTask}</p>
 */
public class WordsTask {

    public static void main(String[] args) {
        String[] words = {"Автошкола", "Тренажёр", "Категория", "Экзамен", "Зачёт"};
        for (String word : words) {
            System.out.println(word);
        }
    }
}
