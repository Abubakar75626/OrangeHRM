package framework;

public class Pages {

	private static final ThreadLocal<Pages> T = new ThreadLocal<Pages>();

	public static Pages get() {
		return T.get();
	}

	public static void set(Pages pages) {
		T.set(pages);
	}
	
//*********************************************************************************************************************

}
