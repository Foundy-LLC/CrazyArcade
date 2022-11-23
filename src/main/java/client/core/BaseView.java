package client.core;

import client.util.Toast;
import lombok.NonNull;

import java.awt.*;
import javax.swing.*;

public class BaseView extends JPanel {

    private static final long serialVersionUID = -2782978815613046608L;

    @NonNull
    private final Image backgroundImageIcon;

    public BaseView(ImageIcon backgroundImageIcon) {
        this.backgroundImageIcon = backgroundImageIcon.getImage();
        setLayout(null);
    }

    protected void onDestroyed() {
    }

    protected void navigateTo(JComponent panel) {
        onDestroyed();
        Navigator.navigateTo(this, panel);
    }

    protected void showToast(String message) {
        Toast.show(message, this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImageIcon, 0, 0, null);
        setOpaque(false);
    }

    /**
     * 게임 화면에서 페이지 전환을 담당하는 클래스이다.
     */
    private static class Navigator {

        private final JFrame rootFrame;

        private Navigator(JFrame rootFrame) {
            this.rootFrame = rootFrame;
        }

        /**
         * {@code contextComponent}가 속한 프레임을 가지고 있는 {@link Navigator} 인스턴스를 반환한다.
         *
         * @param contextComponent 프레임이 속한 컴포넌트
         * @return {@link Navigator} 인스턴스
         */
        public static Navigator of(JComponent contextComponent) {
            return new Navigator((JFrame) SwingUtilities.getWindowAncestor(contextComponent));
        }

        /**
         * {@code component}으로 화면을 전환한다.
         *
         * @param component 전환할 컴포넌트
         */
        public void navigateTo(JComponent component) {
            if (rootFrame == null) {
                System.out.println("네비게이터의 rootFrame이 초기화 되지 않았습니다. 컴포넌트가 부모와 연결되기 전에 사용했는지 확인하세요.");
                return;
            }
            Container c = rootFrame.getContentPane();
            c.removeAll();
            c.add(component);
            c.revalidate();
            c.repaint();
        }

        /**
         * {@code contextComponent}가 속한 프레임에서 {@code target}으로 화면을 전환한다.
         *
         * @param contextComponent 프레임이 속한 컴포넌트
         * @param target           전환할 컴포넌트
         */
        public static void navigateTo(JComponent contextComponent, JComponent target) {
            Navigator.of(contextComponent).navigateTo(target);
        }
    }
}
