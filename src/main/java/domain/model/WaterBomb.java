package domain.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.Calendar;

public class WaterBomb implements Serializable {

	public static final int FRAME_DELAY_MILLI = 300;

	private final long installedMilli = System.currentTimeMillis();

	@Getter
	private final int length;

	public WaterBomb(int length) {
		this.length = length;
	}

	public int getFrame() {
		long currentMilli =  System.currentTimeMillis();
		return (int) (((currentMilli - installedMilli) / FRAME_DELAY_MILLI) % 4);
	}
}
