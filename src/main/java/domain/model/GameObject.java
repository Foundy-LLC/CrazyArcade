package domain.model;

import domain.constant.Sizes;

public abstract class GameObject {
	
	public static final int WIDTH = Sizes.MAP_WIDTH / Sizes.CELL_WIDTH_COUNT;
	public static final int HEIGHT = Sizes.MAP_HEIGHT / Sizes.CELL_HEIGHT_COUNT;
	
	protected Offset offset;

	public GameObject(int x, int y) {
		this.offset = new Offset(x, y);
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
