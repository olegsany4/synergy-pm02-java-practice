package autoschool;

import autoschool.core.GamePanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Точка входа приложения «Автошкола: тренажёр дорожных карточек».
 *
 * <p>Экспериментально-практическая работа по производственной практике ПМ.02:
 * приложение для выбранной предметной области (информационная система для
 * автошколы) с собственным логотипом и анимацией.</p>
 *
 * <p>Запуск в IDE: открыть проект, нажать Run. Запуск из командной строки:
 * {@code java -cp bin autoschool.AutoschoolApp}</p>
 */
public class AutoschoolApp {

    private static final String WINDOW_TITLE = "Автошкола: тренажёр дорожных карточек";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AutoschoolApp::createAndShowWindow);
    }

    private static void createAndShowWindow() {
        JFrame window = new JFrame(WINDOW_TITLE);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setResizable(false);

        GamePanel panel = new GamePanel(
                score -> window.setTitle(WINDOW_TITLE + " | очки: " + score));
        window.add(panel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        panel.start();
        panel.requestFocusInWindow();
    }
}
