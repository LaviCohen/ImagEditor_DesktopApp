package install;

public class Decoder {
	
	private static final int key;
	
	static {
		key = 32;
	}
	
	public static String encode(String s) {
		char[] chars = s.toCharArray();
		String ret = "";
		for (char c : chars) {
			ret += "," + (((int)c) + key);
		}
		return ret.substring(1);
	}
	public static String decode(String s) {
		String[] chars = s.split(",");
		String ret = "";
		for (String c : chars) {
			ret += (char)(Integer.parseInt(c) - key);
		}
		return ret;
	}
}
