package model;

import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class GameObject extends JPanel {
	
	public static final int WIDTH = Constants.MAP_WIDTH / Constants.CELL_WIDTH_COUNT;
	public static final int HEIGHT = Constants.MAP_HEIGHT / Constants.CELL_HEIGHT_COUNT;
	
	protected Offset offset;
	protected final JLabel imageLabel;
	
	public GameObject(int x, int y, JLabel imageLabel) {
		this.offset = new Offset(x, y);
		this.imageLabel = imageLabel;
	}
	
	/**
	 * @return 오브젝트의 중앙 위치를 셀로 치환한 {@link Offset}을 반환한다. 이는 오브젝트 좌표와 크기를 이용하여 계산된다.
	 */
	protected Offset getCellOffset() {
		Offset centerOffset = getCenterOffset();
		return new Offset(centerOffset.x / WIDTH, centerOffset.y / HEIGHT);
	}
	
	/**
	 * @return 오브젝트의 중앙 위치를 반환한다. 이는 오브젝트 좌표와 크기를 이용하여 계산된다.
	 */
	private Offset getCenterOffset() {
		int widthHalf = WIDTH / 2;
		int heightHalf = HEIGHT / 2;
		return new Offset(offset.x + widthHalf, offset.y +heightHalf);
	}
}
