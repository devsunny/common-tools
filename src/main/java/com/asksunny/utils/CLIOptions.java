package com.asksunny.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CLIOptions extends HashMap<String, String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_DELIMITER_REGEX = ";|,";
	private static final String INVALID_KEY_MSG = "Option [%s] does not exists";
	private static final String ARGUMENT_INDEX_OB_MSG = "Cmd args index out of bound [%d]";
	private static final String PREFIX1 = "-";
	private static final String PREFIX_ESCAPE = "\\-";
	private static final String PREFIX2 = "--";
	private static final String SYSPRO_PREFIX = "-D";
	private static final Set<String> BOOLEAN_TRUE_VALUES = new HashSet<String>();

	static {
		BOOLEAN_TRUE_VALUES.add("TRUE");
		BOOLEAN_TRUE_VALUES.add("YES");
		BOOLEAN_TRUE_VALUES.add("ON");
		BOOLEAN_TRUE_VALUES.add("Y");
	}

	private String[] gvalues = null;
	private String[] cmdArgs = null;

	public CLIOptions(String[] cmdArgs) {
		super();
		this.cmdArgs = cmdArgs;
		List<String> values = new ArrayList<String>();
		int nextMax = cmdArgs.length - 1;
		for (int i = 0; i < cmdArgs.length; i++) {
			String cmdArg = cmdArgs[i];
			if (cmdArg.startsWith(SYSPRO_PREFIX)) {
				if (cmdArg.equals(SYSPRO_PREFIX)) {
					String next = (i < nextMax) ? cmdArgs[i + 1] : null;
					if (next.startsWith(PREFIX1)) {
						continue;
					} else {
						if (next.contains("=")) {
							int idx = next.indexOf("=");
							if (idx < next.length() - 1) {
								System.setProperty(next.substring(0, idx), next.substring(idx + 1));
							}
						} else {
							this.put(cmdArg.substring(1), next.substring(1));
						}
						i++;
					}
				}else{
					if (cmdArg.contains("=")){
						int idx = cmdArg.indexOf("=");
						if (idx < cmdArg.length() - 1) {
							System.setProperty(cmdArg.substring(2, idx), cmdArg.substring(idx + 1));
						}						
					}else{
						String next = (i < nextMax) ? cmdArgs[i + 1] : null;
						if (next == null || next.startsWith(PREFIX1)) {
							this.put(cmdArg.substring(1), "TRUE");
						} else if (next.startsWith(PREFIX_ESCAPE)) {
							this.put(cmdArg.substring(1), next.substring(1));
							i++;
						} else {
							this.put(cmdArg.substring(1), next);
							i++;
						}						
					}					
				}

			} else if (cmdArg.startsWith(PREFIX2)) {
				String next = (i < nextMax) ? cmdArgs[i + 1] : null;
				if (next == null || next.startsWith(PREFIX1)) {
					this.put(cmdArg.substring(2), "TRUE");
				} else if (next.startsWith(PREFIX_ESCAPE)) {
					this.put(cmdArg.substring(2), next.substring(1));
					i++;
				} else {
					this.put(cmdArg.substring(2), next);
					i++;
				}
			} else if (cmdArg.startsWith(PREFIX1)) {
				String next = (i < nextMax) ? cmdArgs[i + 1] : null;
				System.out.println(cmdArg);
				System.out.println(next);
				System.out.println("-------------------");
				if (next == null || next.startsWith(PREFIX1)) {
					this.put(cmdArg.substring(1), "TRUE");
				} else if (next.startsWith(PREFIX_ESCAPE)) {
					this.put(cmdArg.substring(1), next.substring(1));
					i++;
				} else {
					this.put(cmdArg.substring(1), next);
					i++;
				}
			} else {
				values.add(cmdArg);
			}
		}

		this.gvalues = new String[values.size()];
		this.gvalues = values.toArray(this.gvalues);
	}

	public String getArgumentByIndex(int idx, String defaultValue) {
		if (idx < this.cmdArgs.length) {
			return this.cmdArgs[idx];
		} else {
			return defaultValue;
		}

	}

	public String getArgumentByIndex(int idx) {
		if (idx < this.cmdArgs.length) {
			return this.cmdArgs[idx];
		} else {
			throw new IndexOutOfBoundsException(String.format(ARGUMENT_INDEX_OB_MSG, idx));
		}
	}

	public int getIntArgumentByIndex(int idx, int defaultValue) {
		if (idx < this.cmdArgs.length) {
			return Integer.valueOf(this.cmdArgs[idx]);
		} else {
			return defaultValue;
		}

	}

	public int getIntArgumentByIndex(int idx) {
		if (idx < this.cmdArgs.length) {
			return Integer.valueOf(this.cmdArgs[idx]);
		} else {
			throw new IndexOutOfBoundsException(String.format(ARGUMENT_INDEX_OB_MSG, idx));
		}
	}

	public long getLongArgumentByIndex(int idx, long defaultValue) {
		if (idx < this.cmdArgs.length) {
			return Long.valueOf(this.cmdArgs[idx]);
		} else {
			return defaultValue;
		}

	}

	public long getLongArgumentByIndex(int idx) {
		if (idx < this.cmdArgs.length) {
			return Long.valueOf(this.cmdArgs[idx]);
		} else {
			throw new IndexOutOfBoundsException(String.format(ARGUMENT_INDEX_OB_MSG, idx));
		}
	}

	public double getDoubleArgumentByIndex(int idx, double defaultValue) {
		if (idx < this.cmdArgs.length) {
			return Long.valueOf(this.cmdArgs[idx]);
		} else {
			return defaultValue;
		}

	}

	public double getDoubleArgumentByIndex(int idx) {
		if (idx < this.cmdArgs.length) {
			return Long.valueOf(this.cmdArgs[idx]);
		} else {
			throw new IndexOutOfBoundsException(String.format(ARGUMENT_INDEX_OB_MSG, idx));
		}
	}

	public boolean getBooleanArgumentByIndex(int idx, boolean defaultValue) {
		if (idx < this.cmdArgs.length) {
			return BOOLEAN_TRUE_VALUES.contains(this.cmdArgs[idx]);
		} else {
			return defaultValue;
		}

	}

	public boolean getBooleanArgumentByIndex(int idx) {
		if (idx < this.cmdArgs.length) {
			return BOOLEAN_TRUE_VALUES.contains(this.cmdArgs[idx]);
		} else {
			throw new IndexOutOfBoundsException(String.format(ARGUMENT_INDEX_OB_MSG, idx));
		}
	}

	public String getArgumentValue(int idx, String defaultValue) {
		if (idx < this.gvalues.length) {
			return this.gvalues[idx];
		} else {
			return defaultValue;
		}
	}

	public String getArgumentValue(int idx) {
		if (idx < this.gvalues.length) {
			return this.gvalues[idx];
		} else {
			throw new IndexOutOfBoundsException(
					String.format("value args index out of bound [%d], values %s", idx, Arrays.asList(this.gvalues)));
		}
	}

	public int getIntArgumentValue(int idx) {
		String val = getArgumentValue(idx);
		return Integer.valueOf(val);

	}

	public int getIntArgumentValue(int idx, int defaultValue) {
		if (idx < this.gvalues.length) {
			return Integer.valueOf(this.gvalues[idx]);
		} else {
			return defaultValue;
		}
	}

	public long getLongArgumentValue(int idx) {
		String val = getArgumentValue(idx);
		return Long.valueOf(val);

	}

	public long getLongArgumentValue(int idx, long defaultValue) {
		if (idx < this.gvalues.length) {
			return Long.valueOf(this.gvalues[idx]);
		} else {
			return defaultValue;
		}
	}

	public double getDoubleArgumentValue(int idx) {
		String val = getArgumentValue(idx);
		return Double.valueOf(val);

	}

	public double getDoubleArgumentValue(int idx, double defaultValue) {
		if (idx < this.gvalues.length) {
			return Double.valueOf(this.gvalues[idx]);
		} else {
			return defaultValue;
		}
	}

	public boolean getBooleanArgumentValue(int idx) {
		String val = getArgumentValue(idx);
		return BOOLEAN_TRUE_VALUES.contains(val.toUpperCase());

	}

	public boolean getBooleanArgumentValue(int idx, boolean defaultValue) {
		if (idx < this.gvalues.length) {
			return BOOLEAN_TRUE_VALUES.contains(this.gvalues[idx].toUpperCase());
		} else {
			return defaultValue;
		}
	}

	public String[] getValues() {

		return this.gvalues;
	}

	public String getOption(String optionKey, String defaultValue) {

		String ret = this.get(optionKey);
		if (ret == null) {
			return defaultValue;
		} else {
			return ret;
		}

	}

	public String getOption(String optionKey) {
		return this.get(optionKey);
	}

	public int getIntOption(String optionKey, int defaultValue) {
		String ret = this.get(optionKey);
		if (ret == null) {
			return defaultValue;
		} else {
			return Integer.valueOf(ret);
		}
	}

	public int getIntOption(String optionKey) {
		String ret = this.get(optionKey);
		if (ret == null) {
			throw new IllegalArgumentException(String.format(INVALID_KEY_MSG, optionKey));
		} else {
			return Integer.valueOf(ret);
		}
	}

	public long getLongOption(String optionKey, long defaultValue) {
		String ret = this.get(optionKey);
		if (ret == null) {
			return defaultValue;
		} else {
			return Long.valueOf(ret);
		}
	}

	public long getLongOption(String optionKey) {
		String ret = this.get(optionKey);
		if (ret == null) {
			throw new IllegalArgumentException(String.format(INVALID_KEY_MSG, optionKey));
		} else {
			return Long.valueOf(ret);
		}
	}

	public double getDoubleOption(String optionKey, double defaultValue) {
		String ret = this.get(optionKey);
		if (ret == null) {
			return defaultValue;
		} else {
			return Double.valueOf(ret);
		}
	}

	public double getDoubleOption(String optionKey) {
		String ret = this.get(optionKey);
		if (ret == null) {
			throw new IllegalArgumentException(String.format(INVALID_KEY_MSG, optionKey));
		} else {
			return Double.valueOf(ret);
		}
	}

	public boolean getBooleanOption(String optionKey, boolean defaultValue) {
		String ret = this.get(optionKey);
		if (ret == null) {
			return defaultValue;
		} else {
			return BOOLEAN_TRUE_VALUES.contains(ret.toUpperCase());
		}
	}

	public boolean getBooleanOption(String optionKey) {
		String ret = this.get(optionKey);
		if (ret == null) {
			throw new IllegalArgumentException(String.format(INVALID_KEY_MSG, optionKey));
		} else {
			return BOOLEAN_TRUE_VALUES.contains(ret.toUpperCase());
		}
	}

	public String[] getArrayOption(String optionKey, String delimiterRegx, String[] defaultValue) {
		String ret = this.get(optionKey);
		if (ret == null) {
			return defaultValue;
		} else {
			String[] rets = ret.split(delimiterRegx);
			return rets;
		}
	}

	public String[] getArrayOption(String optionKey, String delimiterRegx) {
		String ret = this.get(optionKey);
		if (ret == null) {
			return new String[0];
		} else {
			String[] rets = ret.split(delimiterRegx);
			return rets;
		}
	}

	public String[] getArrayOption(String optionKey) {
		return getArrayOption(optionKey, new String[0]);
	}

	public String[] getArrayOption(String optionKey, String[] defaultValue) {
		return getArrayOption(optionKey, DEFAULT_DELIMITER_REGEX, defaultValue);
	}

	public int[] getIntArrayOption(String optionKey, String delimiterRegx, int[] defaultValue) {
		String ret = this.get(optionKey);
		if (ret == null) {
			return defaultValue;
		} else {
			String[] rets = ret.split(delimiterRegx);
			int[] intRets = new int[rets.length];
			for (int i = 0; i < intRets.length; i++) {
				intRets[i] = Integer.valueOf(rets[i]);
			}
			return intRets;
		}
	}

	public int[] getIntArrayOption(String optionKey, String delimiterRegx) {
		return getIntArrayOption(optionKey, delimiterRegx, new int[0]);
	}

	public int[] getIntArrayOption(String optionKey) {
		return getIntArrayOption(optionKey, new int[0]);
	}

	public int[] getIntArrayOption(String optionKey, int[] defaultValue) {
		return getIntArrayOption(optionKey, DEFAULT_DELIMITER_REGEX, defaultValue);
	}

	public long[] getLongArrayOption(String optionKey, String delimiterRegx, long[] defaultValue) {
		String ret = this.get(optionKey);
		if (ret == null) {
			return defaultValue;
		} else {
			String[] rets = ret.split(delimiterRegx);
			long[] intRets = new long[rets.length];
			for (int i = 0; i < intRets.length; i++) {
				intRets[i] = Long.valueOf(rets[i]);
			}
			return intRets;
		}
	}

	public long[] getLongArrayOption(String optionKey, String delimiterRegx) {
		return getLongArrayOption(optionKey, delimiterRegx, new long[0]);
	}

	public long[] getLongArrayOption(String optionKey) {
		return getLongArrayOption(optionKey, new long[0]);
	}

	public long[] getLongArrayOption(String optionKey, long[] defaultValue) {
		return getLongArrayOption(optionKey, DEFAULT_DELIMITER_REGEX, defaultValue);
	}

	public double[] getDoubleArrayOption(String optionKey, String delimiterRegx, double[] defaultValue) {
		String ret = this.get(optionKey);
		if (ret == null) {
			return defaultValue;
		} else {
			String[] rets = ret.split(delimiterRegx);
			double[] intRets = new double[rets.length];
			for (int i = 0; i < intRets.length; i++) {
				intRets[i] = Double.valueOf(rets[i]);
			}
			return intRets;
		}
	}

	public double[] getDoubleArrayOption(String optionKey, String delimiterRegx) {
		return getDoubleArrayOption(optionKey, delimiterRegx, new double[0]);
	}

	public double[] getDoubleArrayOption(String optionKey) {
		return getDoubleArrayOption(optionKey, new double[0]);
	}

	public double[] getDoubleArrayOption(String optionKey, double[] defaultValue) {
		return getDoubleArrayOption(optionKey, DEFAULT_DELIMITER_REGEX, defaultValue);
	}

	public boolean[] getBooleanArrayOption(String optionKey, String delimiterRegx, boolean[] defaultValue) {
		String ret = this.get(optionKey);
		if (ret == null) {
			return defaultValue;
		} else {
			String[] rets = ret.split(delimiterRegx);
			boolean[] intRets = new boolean[rets.length];
			for (int i = 0; i < intRets.length; i++) {
				intRets[i] = BOOLEAN_TRUE_VALUES.contains(rets[i].toUpperCase());
			}
			return intRets;
		}
	}

	public boolean[] getBooleanArrayOption(String optionKey, String delimiterRegx) {
		return getBooleanArrayOption(optionKey, delimiterRegx, new boolean[0]);
	}

	public boolean[] getBooleanArrayOption(String optionKey) {
		return getBooleanArrayOption(optionKey, new boolean[0]);
	}

	public boolean[] getBooleanArrayOption(String optionKey, boolean[] defaultValue) {
		return getBooleanArrayOption(optionKey, DEFAULT_DELIMITER_REGEX, defaultValue);
	}

}
