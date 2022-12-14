package client.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextField;
import javax.swing.text.Document;

import client.constant.Fonts;
import client.util.Cursor;

public class TextField extends JTextField {

    private static final long serialVersionUID = 8486388918278004112L;

    private String placeholder;

    public TextField() {
        init();
    }

    public TextField(
            final Document pDoc,
            final String pText,
            final int pColumns
    ) {
        super(pDoc, pText, pColumns);
        init();
    }

    public TextField(final int pColumns) {
        super(pColumns);
        init();
    }

    public TextField(final String pText) {
        super(pText);
        init();
    }

    public TextField(final String pText, final int pColumns) {
        super(pText, pColumns);
        init();
    }

    private void init() {
        setFont(Fonts.BUTTON);
        setCursor(Cursor.mouseCursor());
    }

    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (placeholder == null || placeholder.length() == 0 || getText().length() > 0) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(
                placeholder,
                getInsets().left,
                pG.getFontMetrics().getMaxAscent() + getInsets().top + 10);
    }

    public void setPlaceholder(final String s) {
        placeholder = s;
    }

}
