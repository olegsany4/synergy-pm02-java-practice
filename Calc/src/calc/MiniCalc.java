package calc;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Этап 4 кейс-задания: мини-калькулятор.
 *
 * <p>Две операции над числами: сложение, вычитание, умножение, деление.
 * Результат выводится в метку. Деление на ноль и нечисловой ввод
 * обрабатываются сообщением, приложение не падает.</p>
 *
 * <p>Этот класс можно целиком вставить в NetBeans вместо автогенерированного
 * кода формы либо использовать как образец кода обработчиков (см.
 * {@code java/Ссылки-для-NetBeans-формы.md}).</p>
 */
public class MiniCalc extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JTextField firstNumberField = new JTextField("12");
    private final JTextField secondNumberField = new JTextField("4");
    private final JLabel resultLabel = new JLabel("Результат: —");

    public MiniCalc() {
        super("Мини-калькулятор — автошкола");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        inputPanel.add(new JLabel("Первое число:"));
        inputPanel.add(firstNumberField);
        inputPanel.add(new JLabel("Второе число:"));
        inputPanel.add(secondNumberField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 8, 8));
        buttonPanel.add(createOperationButton("Сложение", "+"));
        buttonPanel.add(createOperationButton("Вычитание", "-"));
        buttonPanel.add(createOperationButton("Умножение", "*"));
        buttonPanel.add(createOperationButton("Деление", "/"));

        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        content.add(inputPanel, BorderLayout.NORTH);
        content.add(buttonPanel, BorderLayout.CENTER);
        content.add(resultLabel, BorderLayout.SOUTH);
        add(content);

        setSize(560, 220);
        setLocationRelativeTo(null);
    }

    private JButton createOperationButton(String title, String operation) {
        JButton button = new JButton(title);
        button.setActionCommand(operation);
        button.addActionListener(event -> calculate(event.getActionCommand()));
        return button;
    }

    /**
     * Общий обработчик всех четырёх кнопок.
     *
     * @param operation символ операции: +, -, *, /
     */
    private void calculate(String operation) {
        double first;
        double second;
        try {
            first = Double.parseDouble(firstNumberField.getText().trim().replace(',', '.'));
            second = Double.parseDouble(secondNumberField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            resultLabel.setText("Введите числа, например 12 и 4");
            return;
        }

        double result;
        switch (operation) {
            case "+":
                result = first + second;
                break;
            case "-":
                result = first - second;
                break;
            case "*":
                result = first * second;
                break;
            case "/":
                if (second == 0) {
                    resultLabel.setText("Деление на ноль невозможно");
                    return;
                }
                result = first / second;
                break;
            default:
                resultLabel.setText("Неизвестная операция");
                return;
        }
        resultLabel.setText("Результат: " + format(result));
    }

    private String format(double value) {
        if (value == Math.rint(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.format("%.4f", value);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MiniCalc().setVisible(true));
    }
}
