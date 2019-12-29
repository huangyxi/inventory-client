package client;

import java.io.PrintStream;

/**
 * Log
 */
class Log {
	private final static String SEP = " ";
	private final static int COLUMNS = 80;
	private final static String BLOCK_LINE = "=";
	private final static PrintStream out = System.out;
	private final static PrintStream err = System.err;
	public static boolean LOG_INFO = true;
	public static boolean LOG_WARNING = true;
	public static boolean LOG_ERROR = true;
	public static boolean LOG_BLOCK = true;

	public static void info(final String s) {
		if (!LOG_INFO)
			return;
		out.println(ColorTB.reverse() + "INFO: " + ColorTB.reset() + showDataTime() + SEP + showCallInfo() + SEP + s);
	}

	public static void warning(final String s) {
		if (!LOG_WARNING)
			return;
		out.println(ColorTB.yellowB() + "WARN: " + ColorTB.reset() + showDataTime() + SEP + showCallInfo() + SEP + s);
	}

	public static <T extends Exception> void warning(final T e) {
		warning(e, "");
	}

	public static <T extends Exception> void warning(final T e, final String s) {
		if (!LOG_WARNING)
			return;
		out.println(ColorTB.yellowB() + "WARN: " + ColorTB.reset() + showDataTime() + SEP + showClass(e) + "@"
				+ showCallInfo(e) + SEP + s);
	}

	public static void error(final String s) {
		if (!LOG_ERROR)
			return;
		err.println(ColorTB.redB() + "ERR : " + ColorTB.reset() + showDataTime() + SEP + showCallInfo() + SEP + s);
	}

	public static <T extends Exception> void error(final T e) {
		error(e, "");
	}

	public static <T extends Exception> void error(final T e, final String s) {
		if (!LOG_ERROR)
			return;
		err.println(ColorTB.redB() + "ERR : " + ColorTB.reset() + showDataTime() + SEP + showClass(e) + "@"
				+ showCallInfo(e) + SEP + s);
	}

	public static void block(final String s) {
		if (!LOG_BLOCK)
			return;
		if (s.length() >= COLUMNS - 4) {
			out.println(repeatString(BLOCK_LINE, COLUMNS));
			out.println(ColorTB.bold() + s + ColorTB.reset());
			out.println(repeatString(BLOCK_LINE, COLUMNS));
		} else {
			out.println(repeatString(BLOCK_LINE, (COLUMNS - s.length() - 1) / 2) + " " + ColorTB.bold() + s
					+ ColorTB.reset() + " " + repeatString(BLOCK_LINE, (COLUMNS - s.length() - 2) / 2));
		}
	}

	private static String repeatString(final String str, final int n) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < n; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	private static String getDataTime() {
		return java.time.Clock.systemUTC().instant().toString();
	}

	private static String showCallInfo() {
		final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		final StackTraceElement s = stacktrace[3];
		final String className = s.getClassName();
		final String methodName = s.getMethodName();
		final int lineNumber = s.getLineNumber();
		return "@" + showClassName(className) + "::" + showMethodName(methodName) + showLineNumber(lineNumber);
	}

	private static <T extends Exception> String showCallInfo(T e) {
		final int logTrace = 4;
		final String currentMethodName = Thread.currentThread().getStackTrace()[logTrace].getClassName() + "::"
				+ Thread.currentThread().getStackTrace()[logTrace].getMethodName();
		// System.out.println(currentMethodName);
		final StackTraceElement[] arrE = e.getStackTrace();
		int call;
		for (call = 0; call < e.getStackTrace().length; call++) {
			// System.out.println(arrE[call].getMethodName());
			if (currentMethodName.equals(arrE[call].getClassName() + "::" + arrE[call].getMethodName())) {
				break;
			}
		}
		final String className = arrE[call].getClassName();
		final String methodName = arrE[call].getMethodName();
		final int lineNumber = arrE[call].getLineNumber();
		return "(" + call + ")@" + showClassName(className) + "::" + showMethodName(methodName)
				+ showLineNumber(lineNumber);
	}

	private static String showLineNumber(int lineNumber) {
		return ColorTB.blueT() + "[" + lineNumber + "]" + ColorTB.reset();
	}

	private static String showDataTime() {
		return ColorTB.cyanT() + getDataTime() + ColorTB.reset();
	}

	private static String showClassName(String name) {
		return ColorTB.bold() + ColorTB.whiteT() + name + ColorTB.reset();
	}

	private static String showMethodName(String name) {
		return ColorTB.bold() + ColorTB.whiteT() + name + ColorTB.reset();
	}

	private static <T extends Exception> String showClass(T e) {
		return ColorTB.bold() + e.getClass().toString() + ColorTB.reset();
	}
}