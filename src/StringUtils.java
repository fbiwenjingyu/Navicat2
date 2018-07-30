
public final class StringUtils {
	public static boolean isEmpty(String str) {
		if(str == null || "".equals(str.trim())) {
			return true;
		}
		return false;
	}
	
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	private StringUtils() {
		
	}
	
	public static void main(String[] args) {
		String s = null;
		s = "";
		s = "           ";
		System.out.println(isEmpty(s));
	}
}
