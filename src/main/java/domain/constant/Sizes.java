package domain.constant;

import java.awt.*;

public class Sizes {

	public static final int SCREEN_WIDTH = 1040;
	public static final int SCREEN_HEIGHT = 808;
	
	public static final int TILE_ROW_COUNT = 15;
	public static final int TILE_COLUMN_COUNT = 13;

	public static final Dimension TILE_SIZE = new Dimension(52, 52);

	public static final int MAP_WIDTH = TILE_ROW_COUNT * TILE_SIZE.width;
	public static final int MAP_HEIGHT = TILE_COLUMN_COUNT * TILE_SIZE.height;
}
