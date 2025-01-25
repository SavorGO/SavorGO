package raven.modal.demo.utils;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import java.awt.event.*;
import java.text.NumberFormat;

public class CustomFormattedTextField extends JFormattedTextField {
    public CustomFormattedTextField() {
        super(NumberFormat.getNumberInstance());
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(3);
        setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(numberFormat)));
        setColumns(12);
        setValue(0.0d);
        setHorizontalAlignment(JFormattedTextField.RIGHT);
        
        // Thêm listener cho sự kiện focusLost
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    commitEdit(); // Định hình giá trị trước khi mất tiêu điểm
                } catch (java.text.ParseException ex) {
                    // Xử lý ngoại lệ nếu không thể định hình giá trị
                    ex.printStackTrace();
                }
                if (getText().trim().isEmpty()) {
                    setValue(0.0); // Gán lại giá trị mặc định
                }
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    transferFocus(); // Chuyển tiêu điểm đến thành phần tiếp theo
                }
            }
        });
    }
    public double getDoubleValue() {
        Object value = getValue();
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0; // Hoặc xử lý theo cách khác nếu không phải là số
    }
}