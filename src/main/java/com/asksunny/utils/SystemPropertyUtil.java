package com.asksunny.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SystemPropertyUtil {

	private static final Pattern SYS_PROP_PATTERN = Pattern.compile("\\$\\{([^\\{\\}]+)\\}");

	public static String resolve(CharSequence text) {
		Matcher matcher = SYS_PROP_PATTERN.matcher(text);
		StringBuffer buf = new StringBuffer();
		while (matcher.find()) {
			String key = matcher.group(1);
			String value = System.getProperty(key);
			if (value != null) {
				if (SYS_PROP_PATTERN.matcher(value).find()) {
					value = resolve(value);
				}
				matcher.appendReplacement(buf, value);
			}
		}
		matcher.appendTail(buf);
		return buf.toString();
	}

	private SystemPropertyUtil() {		
	}

}
