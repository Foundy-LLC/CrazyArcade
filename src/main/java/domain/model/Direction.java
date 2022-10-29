package domain.model;

public enum Direction {
	UP, DOWN, LEFT, RIGHT;

	/**
	 * 반복문에서 이용하면 좋은 유틸 상수이다.
	 */
	public static final int[][] DIR = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
}
