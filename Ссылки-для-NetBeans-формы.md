# Справочные фрагменты для формы NetBeans (этап 3 кейс-задания)

Эти фрагменты нужны для слайдов 12–13 отчёта. Форму, кнопки, метки и группу
переключателей вы создаёте сами в конструкторе NetBeans (как в кейсе), а обработчики
событий — по образцу ниже. Имена переменных соответствуют автогенерации NetBeans:
`jButton1`, `jTextField1`, `jLabel1`, `jRadioButton1`, `jRadioButton2`, `buttonGroup1`.

## 1. Кнопка выводит текст в метку

Двойной щелчок по кнопке на форме → NetBeans создаёт метод-обработчик:

```java
private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
    jLabel1.setText("Автошкола: форма создана, обработчик события работает.");
}
```

## 2. Перенос текста из текстового поля в метку

```java
private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
    String text = jTextField1.getText().trim();
    if (text.isEmpty()) {
        jLabel1.setText("Сначала введите текст в поле.");
    } else {
        jLabel1.setText("Вы ввели: " + text);
    }
}
```

## 3. Группа переключателей (buttonGroup) меняет текст метки

Перед использованием обработчиков переключатели нужно добавить в `buttonGroup1`
(свойство `buttonGroup` у каждого переключателя — `buttonGroup1`).

```java
private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {
    jLabel1.setText("Выбрана категория A — мотоциклы.");
}

private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {
    jLabel1.setText("Выбрана категория B — легковые автомобили.");
}
```

## 4. Счётчик нажатий (дополнительный элемент формы)

```java
private int clicks = 0;

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
    clicks++;
    jLabel1.setText("Кнопка нажата раз: " + clicks);
}
```

## 5. Как это выглядит в обработчике кнопки «Сложение» мини-калькулятора

```java
private void jButtonCalcActionPerformed(java.awt.event.ActionEvent evt) {
    try {
        double a = Double.parseDouble(jTextField1.getText().replace(',', '.'));
        double b = Double.parseDouble(jTextField2.getText().replace(',', '.'));
        jLabel1.setText("Результат: " + (a + b));
    } catch (NumberFormatException e) {
        jLabel1.setText("Введите числа");
    }
}
```

---

⚠️ Готовый листинг игры из кейс-задания (класс `Game`) вставлять в отчёт нельзя —
это чужой пример. Для слайда 21 используется собственное приложение
`AutoschoolApp` (см. `java/AutoschoolApp/`), которое вы запускаете и снимаете сами.
