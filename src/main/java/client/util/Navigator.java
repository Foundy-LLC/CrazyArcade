package client.util;

import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * 게임 화면에서 페이지 전환을 담당하는 클래스이다.
 */
public class Navigator {
	
	private JFrame rootFrame;
	
	private Navigator(JFrame rootFrame) {
		this.rootFrame = rootFrame;
	}
	
	/**
	 * {@code contextComponent}가 속한 프레임을 가지고 있는 {@link Navigator} 인스턴스를 반환한다.
	 * @param contextComponent 프레임이 속한 컴포넌트 
	 * @return {@link Navigator} 인스턴스
	 */
	public static Navigator of(JComponent contextComponent) {
		return new Navigator((JFrame) SwingUtilities.getWindowAncestor(contextComponent));
	}
	
	/**
	 * {@code component}으로 화면을 전환한다. 
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
	 * @param contextComponent 프레임이 속한 컴포넌트 
	 * @param target 전환할 컴포넌트 
	 */
	public static void navigateTo(JComponent contextComponent, JComponent target) {
		Navigator.of(contextComponent).navigateTo(target);
	}
}