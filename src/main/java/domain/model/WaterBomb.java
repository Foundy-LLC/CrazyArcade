package domain.model;

import java.io.Serializable;
import java.util.Calendar;

public class WaterBomb implements Serializable {

	public static final int FRAME_DELAY_MILLI = 300;

	private final long installedMilli = System.currentTimeMillis();

	public WaterBomb() {
	}

	public int getFrame() {
		long currentMilli =  System.currentTimeMillis();
		return (int) (((currentMilli - installedMilli) / FRAME_DELAY_MILLI) % 4);
	}
}
