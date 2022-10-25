package domain.util;

import domain.constant.Protocol;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	public static void AppendText(String str) {
		System.out.println(getTime() + str);
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
//		textArea.append(str + "\n");
//		textArea.setCaretPosition(textArea.getText().length());
	}

	static String getTime() {
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	} // get

	public static byte[] MakePacket(String msg) {
		byte[] packet = new byte[Protocol.BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < Protocol.BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}
}
