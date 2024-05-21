import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class QuantityFilter extends DocumentFilter {
    private final JTextField newProductQuantityTextField;
    QuantityFilter(JTextField newProductQuantityTextField){
        this.newProductQuantityTextField = newProductQuantityTextField;
    }

    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string.isEmpty()
                || isDigit(string)
                || string.equals(".")
                && !newProductQuantityTextField.getText().contains(".")) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text.isEmpty() || isDigit(text) || text.equals(".")) {
            if ((text.equals(".")) && !newProductQuantityTextField.getText().contains(".")) {
                super.replace(fb, offset, length, text, attrs);
            } else if (!text.equals(".")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
    private boolean isDigit(String text) {
        return text != null && text.matches("\\d");
    }
}
