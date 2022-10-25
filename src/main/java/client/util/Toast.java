package client.util;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 간단한 메시지를 사용자에게 전달하기 위한 클래스이다.
 */
public class Toast extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private Toast() {}
	
	/**
	 * 토스트 메시지를 전달한 밀리초동안 띄운다.
	 * @param msg 보일 메시지 
	 * @param millisec 토스트를 띄울 밀리초 단위 기간 
	 * @param contextComponent {@link JFrame}를 얻기위해 사용할 컴포넌트이다. 루트 프레임과 연결이 되어있어야 한다.
	 */
	public static void show(String msg, int millisec, JComponent contextComponent) {
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(contextComponent);
		if (frame == null) { 
			System.out.println("네비게이터의 rootFrame이 초기화 되지 않았습니다. 컴포넌트가 부모와 연결되기 전에 사용했는지 확인하세요.");
			return;
		}
		
		Toast toast = new Toast();
		
		// 다이어로그의 윈도우 바를 보이지 않게 한다.
		toast.setUndecorated(true);
		
		toast.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.GRAY);
		toast.getContentPane().add(panel, BorderLayout.CENTER);
		
		JLabel toastLabel = new JLabel("");
		toastLabel.setText(msg);
		toastLabel.setFont(Fonts.H6);
		toastLabel.setForeground(Color.WHITE);
		
		toast.setAlwaysOnTop(true);
		// 토스트 크기를 정한다.
		toast.setBounds(100, 100, toastLabel.getPreferredSize().width + 20, 32);
		
		// 윈도우의 중앙 하단부에 위치시킨다.
		int y = frame.getY() + frame.getHeight() / 2 - toast.getSize().height / 2;
		int x = frame.getX() + frame.getWidth() / 2 - toast.getSize().width / 2;
		toast.setLocation(x, y + y / 2);
		panel.add(toastLabel);
		toast.setVisible(true);
		
		new Thread(){
		    public void run() {
		        try {
		            Thread.sleep(millisec);
		            toast.dispose();
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    }
		}.start();
	}
	
	/**
	 * 토스트 메시지를 3초간 띄운다.
	 * @param msg 보일 메시지 
	 * @param contextComponent {@link JFrame}를 얻기위해 사용할 컴포넌트이다. 루트 프레임과 연결이 되어있어야 한다.
	 */
	public static void show(String msg, JComponent contextComponent) {
		show(msg, 3000, contextComponent);
	}
}
