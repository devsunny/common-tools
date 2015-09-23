package com.asksunny.data;

import java.io.PrintWriter;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.asksunny.utils.CLIOptions;

public class SampleDataGenerator {

	public static final Pattern FIELD_SPEC_PATTERN = Pattern
			.compile(
					"((int|long|letters|digits|string|seq|date|tsmilli|tsnano|time|timestamp)(_(\\w+))?)\\s*\\(\\s*(\\d+(\\.\\d+)?)\\s*(,\\s*(\\d+(\\.\\d+)?))?\\s*\\)",
					Pattern.CASE_INSENSITIVE);
	public static final Pattern FIELD_SPEC_DOUBLE_PATTERN = Pattern
			.compile(
					"((double)(_(\\w+))?)\\s*\\(\\s*(\\d+(\\.\\d+)?)\\s*(,\\s*(\\d+(\\.\\d+)?))?\\s*(,\\s*(\\d+))?\\s*\\)",
					Pattern.CASE_INSENSITIVE);
	
	public static final Pattern FIELD_SPEC_ENUM_PATTERN = Pattern
			.compile(
					"((enum)(_(\\w+))?)\\s*\\(\\s*((\\w+)(,[^,()]+)*)\\s*\\)",
					Pattern.CASE_INSENSITIVE);
	
	

	public static final Pattern FIELD_SPEC_DT_PATTERN = Pattern
			.compile(
					"((date|tsmilli|tsnano|timestamp)(_(\\w+))?)\\s*\\(\\s*((NOW)?\\s*((\\+|-)\\s*(\\d+)(Y|M|D|H|N|S)?)?)\\s*(,\\s*((NOW)?\\s*((\\+|-)\\s*(\\d+)(Y|M|D|H|N|S)?)?))?\\s*\\)",
					Pattern.CASE_INSENSITIVE);

	public static final Pattern FIELD_SPEC_FMTDIGITS_PATTERN = Pattern.compile(
			"((fmtdigits)(_(\\w+))?)\\s*\\(((([^d])|(d))+)\\)",
			Pattern.CASE_INSENSITIVE);

	public static final Pattern FIELD_SPEC_D_PATTERN = Pattern.compile("d",
			Pattern.CASE_INSENSITIVE);

	public static void main(String[] args) throws Exception {
		doMain(args);
	}
	
	public static void doMain(String[] args) throws Exception {

		CLIOptions cliopts = new CLIOptions(args);
		String out = cliopts.getOption("out");
		String prefix = cliopts.getOption("prefix");
		String suffix = cliopts.getOption("suffix");
		String spec = cliopts.getOption("spec");
		String delimiter = cliopts.getOption("delimiter", ",");
		if (spec == null) {
			StringBuilder buf = new StringBuilder();
			buf.append("Usage: <prog> -spec <field_spec_detail> [-delimiter <delimiter>] [-out <output_path>] [-prefix <prefix_for_each_row>] [-suffix <suffix_for_each_row>] [-length <proximate_length_of_output>]\n");
			buf.append("\t -spec \n");
			buf.append("\t       field_spec_detail grammar: <field_spec>[;<field_spec>]*\n");
			buf.append("\t       <field_spec>: \n");
			buf.append("\t       <field_type>(_<field_name>)?(<min_value>, <max_value>)\n");
			buf.append("\t       <field_type>(_<field_name>)?(<max_value>)\n");
			buf.append("\t       <field_type>(_<field_name>)?(<fix_value>)\n");
			buf.append("\t       <field_type>(_<field_name>)?(<format_string>)\n");
			buf.append("\t       <field_type>\n");
			buf.append("\t                    int - integer, example int(12, 100), int(150) \n");
			buf.append("\t                    long - long,   example long(12, 100), long(150) \n");
			buf.append("\t                    double - double, example double(1.0, 100.2), double(150.23) \n");
			buf.append("\t                    string - String, example string(2, 32), string(32) \n");
			buf.append("\t                    digits - integer, example digits(12, 100), digits(150) \n");
			buf.append("\t                    letters - integer, example letters(12, 100), letters(150) \n");
			buf.append("\t                    seq - generate sequence number with min value specified, example seq(1) \n");
			buf.append("\t                    fmtdigits - Digits with format string, example  fmtdigits(ddd-dd-dddd) \n");
			buf.append("\t                    date - JDBC efault date format yyyy-mm-dd, date(yyyy, yyyy), example date(2012, 2014) \n");
			buf.append("\t                    time - JDBC efault time format HH:mm:ss, time(yyyy, yyyy), example time(2012, 2014) \n");
			buf.append("\t                    timestamp - JDBC efault timestamp format yyyy-mm-dd HH:mm:ss, timestamp(yyyy, yyyy), example timestamp(2012, 2014) \n");
			buf.append("\t                    tsmilli - timestamp in millisecond format yyyy-mm-dd HH:mm:ss.ffffff, tsmilli(yyyy, yyyy), example tsmilli(2012, 2014) \n");
			buf.append("\t                    tsnano - timestamp in nanosecond format yyyy-mm-dd HH:mm:ss.fffffffff, tsnano(yyyy, yyyy), example tsnano(2012, 2014) \n");
			buf.append("\t                    enum - randomly select a vlues from a comma separated values enumuration, example enum(test1,test2,test3) \n");
			buf.append("\t       All of date and time field support arithmetic formating, with gramma:\n");
			buf.append("\t       (NOW)?(+|-)<quantity><interval>\n");
			buf.append("\t       <quantity> - integeral value \n");
			buf.append("\t       <interval> - 'Y' year, 'M' month, 'D' day, 'H' hour, 'N' minutes, 'S' second and default is day. \n");
			buf.append("\t       Example: date(now-20y, now+3m), tsnano(now-1d, now+0s) \n");
			buf.append("\t       Spec examples: seq(1);int(20);int(10, 200);double(10.0, 20.0);date(2012, 2014);fmtdigits(ddd-dd-dddd);string(5);tsmilli(-5h,+0s)\n");

			System.err.println(buf);
			System.exit(1);
		}
		PrintWriter pout = null;
		try {
			long size = cliopts.getLongOption("length", 1000);
			pout = (out == null) ? new PrintWriter(System.out)
					: new PrintWriter(out);
			SampleDataGenerator generator = new SampleDataGenerator();
			generator.generateSample(pout, spec, delimiter.charAt(0), size,
					prefix, suffix);
			pout.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		} finally {
			if (out != null && pout != null) {
				pout.close();
			}
		}
	}

	public static final String SPEC_DELIMITER = ";";
	public static final long SECOND_IN_MILLI = 1000L;
	public static final long MINUTE_IN_MILLI = SECOND_IN_MILLI * 60L;
	public static final long HOUR_IN_MILLI = MINUTE_IN_MILLI * 60L;
	public static final long DAY_IN_MILLI = HOUR_IN_MILLI * 24L;
	public static final long MONTH_IN_MILLI = DAY_IN_MILLI * 30L;
	public static final long YEAR_IN_MILLI = DAY_IN_MILLI * 365L;

	public static final Map<String, Integer> typeMaps = new HashMap<String, Integer>();
	public static final Map<String, Long> TIME_INTERVALS = new HashMap<String, Long>();
	static {
		typeMaps.put("INT", FieldSpec.INT);
		typeMaps.put("LONG", FieldSpec.LONG);
		typeMaps.put("DOUBLE", FieldSpec.DOUBLE);
		typeMaps.put("STRING", FieldSpec.STRING);
		typeMaps.put("DATE", FieldSpec.DATE);
		typeMaps.put("TIME", FieldSpec.TIME);
		typeMaps.put("TIMESTAMP", FieldSpec.TIMESTAMP);
		typeMaps.put("TSMILLI", FieldSpec.TIMESTAMP_MILLI);
		typeMaps.put("TSNANO", FieldSpec.TIMESTAMP_NANO);
		typeMaps.put("DIGITS", FieldSpec.DIGITS);
		typeMaps.put("LETTERS", FieldSpec.LETTERS);
		typeMaps.put("SEQ", FieldSpec.SEQUENCE);
		typeMaps.put("ENUM", FieldSpec.ENUM);
		typeMaps.put("FMTDIGITS", FieldSpec.FORMAT_DIGITS);

		TIME_INTERVALS.put("Y", YEAR_IN_MILLI);
		TIME_INTERVALS.put("M", MONTH_IN_MILLI);
		TIME_INTERVALS.put("D", DAY_IN_MILLI);
		TIME_INTERVALS.put("H", HOUR_IN_MILLI);
		TIME_INTERVALS.put("N", MINUTE_IN_MILLI);
		TIME_INTERVALS.put("S", SECOND_IN_MILLI);

	}
	public final static char[] ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_- "
			.toCharArray();
	public final static char[] ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();
	public final static char[] NUMERIC = "1234567890".toCharArray();

	private final SimpleDateFormat JDBC_DATE_SDF = new SimpleDateFormat(
			"yyyy-MM-dd");
	private final SimpleDateFormat JDBC_TIME_SDF = new SimpleDateFormat(
			"HH:mm:ss");
	private final SimpleDateFormat JDBC_DATETIME_SDF = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private SecureRandom random = null;
	public Map<String, Long> sequences = new HashMap<String, Long>();

	public SampleDataGenerator() {
		random = new SecureRandom(new Date().toString().getBytes());
	}

	protected int genInt(int max) {
		return random.nextInt(max);
	}

	protected String genFormatedDigits(FormatDigitFieldSpec fmtSpec) {
		StringBuilder buf = new StringBuilder();
		buf.append(fmtSpec.getFormat());
		int[] idxs = fmtSpec.getIndexes();
		for (int i = 0; i < idxs.length; i++) {
			buf.setCharAt(idxs[i], NUMERIC[genPositiveInt(10) % NUMERIC.length]);
		}
		return buf.toString();
	}

	protected long genSequence(String name, long min) {
		Long x = sequences.get(name);
		if (x == null) {
			x = min;
		} else {
			x = x.longValue() + 1;
		}
		sequences.put(name, x);
		return x;
	}

	protected int genInt(int min, int max) {
		int diff = max - min;
		int ret = (diff == 0) ? min : genPositiveInt(diff) + min;
		return ret;
	}

	protected int genPositiveInt(int max) {
		if (max == 0)
			return 0;
		int ret = random.nextInt(max);
		if (ret < 0) {
			ret = Math.abs(ret) % max;
		}
		return ret;
	}

	protected int genPositiveInt(int min, int max) {
		int diff = max - min;
		int ret = (diff == 0) ? min : genPositiveInt(diff) + min;
		return ret;
	}

	protected long genLong(long max) {
		return random.nextLong() % max;
	}

	protected long genLong(long min, long max) {
		long diff = max - min;
		return genPositiveLong(diff) + min;
	}

	protected long genPositiveLong(long max) {
		long ret = Math.abs(random.nextLong()) % max;
		return ret;
	}

	protected long genPositiveLong(long min, long max) {
		long diff = max - min;
		long ret = (genPositiveLong(diff) + min) % max;
		return ret;
	}

	protected double genDouble(double max) {
		double d = random.nextDouble();
		if (d > max && max != 0) {
			d = d % max;
		}
		return d;
	}

	protected double genDouble(double min, double max) {
		double diff = max - min;
		return genPositiveDouble(diff) * diff + min;
	}

	protected double genPositiveDouble(double max) {
		double ret = genDouble(max);
		if (ret < 0) {
			ret = Math.abs(ret) * max;
		}
		return ret;
	}

	protected double genPositiveDouble(double min, double max) {
		return genPositiveDouble(max - min) + min;
	}

	public String genString(int min, int max) {
		int len = max == min ? min : genPositiveInt(min, max);
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < len; i++) {
			buf.append(ALPHA_NUMERIC[genPositiveInt(ALPHA_NUMERIC.length)
					% ALPHA_NUMERIC.length]);
		}
		return buf.toString();
	}

	public String genLetters(int min, int max) {
		int len = max == min ? min : genPositiveInt(min, max);
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < len; i++) {
			buf.append(ALPHA[genPositiveInt(ALPHA.length) % ALPHA.length]);
		}
		return buf.toString();
	}

	public String genNumerics(int min, int max) {
		int len = max == min ? min : genPositiveInt(min, max);
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < len; i++) {
			buf.append(NUMERIC[genPositiveInt(NUMERIC.length) % NUMERIC.length]);
		}
		return buf.toString();
	}

	public Date genDate(Date min, Date max) {
		long mi = (min == null ? 0 : min.getTime());
		long ma = (max == null ? System.currentTimeMillis() : max.getTime());
		long diff = ma - mi;
		long x = genPositiveLong(diff);
		Date t = new Date(mi + x);
		return t;
	}

	public Calendar genDate(Calendar min, Calendar max) {
		long mi = (min == null ? 0 : min.get(Calendar.MILLISECOND));
		long ma = (max == null ? System.currentTimeMillis() : max
				.get(Calendar.MILLISECOND));
		long diff = ma - mi;
		long x = genPositiveLong(diff);
		Date t = new Date(mi + x);
		Calendar ret = Calendar.getInstance();
		ret.setTime(t);
		return ret;
	}

	public String genDate(String min, String max, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date dmin = (min == null) ? null : parseDate(sdf, min);
		Date dmax = (max == null) ? null : parseDate(sdf, max);
		Date ret = genDate(dmin, dmax);
		return sdf.format(ret);
	}

	private Date parseDate(SimpleDateFormat sdf, String dstr) {
		try {
			return sdf.parse(dstr);
		} catch (ParseException e) {
			return null;
		}
	}

	public FieldSpec[] parseSpec(String spec) {
		String[] specs = spec.split(SPEC_DELIMITER);
		FieldSpec[] fspecs = new FieldSpec[specs.length];
		for (int i = 0; i < fspecs.length; i++) {
			FieldSpec fspec = new FieldSpec();
			Matcher m = FIELD_SPEC_PATTERN.matcher(specs[i]);
			Matcher m4 = FIELD_SPEC_DOUBLE_PATTERN.matcher(specs[i]);
			Matcher m2 = FIELD_SPEC_DT_PATTERN.matcher(specs[i]);
			Matcher m3 = FIELD_SPEC_FMTDIGITS_PATTERN.matcher(specs[i]);
			Matcher m5 = FIELD_SPEC_ENUM_PATTERN.matcher(specs[i]);
			if (m.find()) {
				fspec = initFieldSpec(fspec, m, i);
			} else if (m4.find()) {
				DoubleFieldSpec dfspec = new DoubleFieldSpec();	
				String p = m4.group(11);
				if(p!=null){
					dfspec.setPrecision(Integer.valueOf(p));
				}
				fspec = initFieldSpec(dfspec, m4, i);
			} else if (m5.find()) {
				fspec = getEnumFieldSpec(m5, i);
			}else if (m2.find()) {
				fspec = getDateTimeFieldSpec(m2, i);
			} else if (m3.find()) {
				fspec = getFormatDigitFieldSpec(m3, i);
			} else {
				fspec.setType(FieldSpec.FIXED_VALUE);
				fspec.setName(specs[i]);
			}
			fspecs[i] = fspec;
			
		}
		return fspecs;
	}

	protected FormatDigitFieldSpec getFormatDigitFieldSpec(Matcher m, int idx) {
		FormatDigitFieldSpec fspec = new FormatDigitFieldSpec();
		String type = m.group(2).toUpperCase();
		fspec.setType(typeMaps.get(type));
		String name = m.group(4);
		if (name == null)
			name = String.format("$%d", idx);

		String fmtString = m.group(5);
		Matcher fm = FIELD_SPEC_D_PATTERN.matcher(fmtString);
		int[] indices = new int[1024];
		int pos = 0;
		while (fm.find()) {
			indices[pos++] = fm.start();
		}
		int[] indx = new int[pos];
		System.arraycopy(indices, 0, indx, 0, pos);
		fspec.setFormat(fmtString);
		fspec.setIndexes(indx);
		return fspec;
	}

	protected FieldSpec initFieldSpec(FieldSpec fspec, Matcher m, int idx) {
		String type = m.group(2).toUpperCase();
		fspec.setType(typeMaps.get(type));
		String name = m.group(4);
		if (name == null)
			name = String.format("$%d", idx);
		fspec.setName(name);
		String max = m.group(8);
		String min = m.group(5);
		if (max == null) {
			max = min;
			if (fspec.getType() != FieldSpec.STRING
					&& fspec.getType() != FieldSpec.LETTERS
					&& fspec.getType() != FieldSpec.SEQUENCE
					&& fspec.getType() != FieldSpec.DIGITS) {
				min = null;
			}
		}
		fspec.setMin(min);
		fspec.setMax(max);
		return fspec;
	}
	
	
	protected EnumFieldSpec getEnumFieldSpec(Matcher m2, int idx) {
		EnumFieldSpec dtfspec = new EnumFieldSpec();
		String type = m2.group(2).toUpperCase();
		dtfspec.setType(typeMaps.get(type));
		String name = m2.group(4);
		if (name == null)
			name = String.format("$%d", idx);
		dtfspec.setName(name);		
		String val = m2.group(5);
		if(val!=null){
			dtfspec.setEnumVlaues(val.split(","));
		}else{
			dtfspec.setEnumVlaues(new String[]{" "});
		}		
		return dtfspec;
	}
	
	
	

	protected DateTimeFieldSpec getDateTimeFieldSpec(Matcher m2, int idx) {
		DateTimeFieldSpec dtfspec = new DateTimeFieldSpec();
		String type = m2.group(2).toUpperCase();
		dtfspec.setType(typeMaps.get(type));
		String name = m2.group(4);
		if (name == null)
			name = String.format("$%d", idx);
		dtfspec.setName(name);

		String plus_minus = m2.group(8);
		String quantity = m2.group(9);
		String interval = m2.group(10);
		dtfspec.setMinLongValue(calculateDate(plus_minus, quantity, interval));

		plus_minus = m2.group(15);
		quantity = m2.group(16);
		interval = m2.group(17);
		dtfspec.setMaxLongValue(calculateDate(plus_minus, quantity, interval));
		return dtfspec;
	}

	public long generateSample(PrintWriter out, String strspecs,
			char delimiter, long sizeLimit) {
		FieldSpec[] specs = parseSpec(strspecs);
		return generateSample(out, specs, sizeLimit, delimiter, null, null);
	}

	public long generateSample(PrintWriter out, String strspecs,
			char delimiter, long sizeLimit, String prefix, String postfix) {
		FieldSpec[] specs = parseSpec(strspecs);
		return generateSample(out, specs, sizeLimit, delimiter, prefix, postfix);
	}

	public long generateSample(PrintWriter out, FieldSpec[] specs,
			long sizeLimit, char delimiter, String prefix, String postfix) {
		long length = 0;
		while (length < sizeLimit) {
			length += generateSample(out, specs, delimiter, prefix, postfix);
			if (length < sizeLimit) {
				out.print("\n");
				length++;
			}
		}
		out.flush();
		return length;
	}

	protected long calculateDate(String plus, String quantity, String interval) {
		long ret = System.currentTimeMillis();
		long q = quantity != null ? Long.valueOf(quantity) : 0;
		if (q == 0)
			return ret;
		long it = interval != null ? TIME_INTERVALS.get(interval.toUpperCase())
				: DAY_IN_MILLI;
		if (plus == null || "+".equals(plus)) {
			ret += (q * it);
		} else {
			ret -= (q * it);
		}
		return ret;
	}

	protected String genDateTime(SimpleDateFormat sdf, long min, long max) {
		long ts = genPositiveLong(min, max);
		Date dt = new Date(ts);
		return sdf.format(dt);
	}

	protected int generateSample(PrintWriter out, FieldSpec[] specs,
			char delimiter, String prefix, String postfix) {
		int length = 0;
		StringBuilder buf = new StringBuilder();
		if (prefix != null) {
			buf.append(prefix);
		}
		for (int i = 0; i < specs.length; i++) {
			boolean hasMin = specs[i].hasMin();
			switch (specs[i].getType()) {
			case FieldSpec.INT:
				if (hasMin) {
					buf.append(genInt(specs[i].getMinIntValue(),
							specs[i].getMaxIntValue()));
				} else {
					buf.append(genInt(specs[i].getMaxIntValue()));
				}
				break;
			case FieldSpec.LONG:
				if (hasMin) {
					buf.append(genLong(specs[i].getMinLongValue(),
							specs[i].getMaxLongValue()));
				} else {
					buf.append(genLong(specs[i].getMaxLongValue()));
				}
				break;
			case FieldSpec.DOUBLE:
				DoubleFieldSpec df = ((DoubleFieldSpec)specs[i]);
				double dval = 0;
				if (hasMin) {
					 dval = genDouble(specs[i].getMinDoubleValue(),
							specs[i].getMaxDoubleValue());										
				} else {
					 dval = (genDouble(specs[i].getMaxDoubleValue()));
				}
				buf.append(df.toString(dval));				
				break;
			case FieldSpec.STRING:
				buf.append(genString(specs[i].getMinIntValue(),
						specs[i].getMaxIntValue()));
				break;
			case FieldSpec.DIGITS:
				buf.append(genNumerics(specs[i].getMinIntValue(),
						specs[i].getMaxIntValue()));
				break;
			case FieldSpec.LETTERS:
				buf.append(genLetters(specs[i].getMinIntValue(),
						specs[i].getMaxIntValue()));
				break;
			case FieldSpec.DATE:
				buf.append(genDateTime(JDBC_DATE_SDF,
						specs[i].getMinLongValue(), specs[i].getMaxLongValue()));
				break;

			case FieldSpec.TIME:
				buf.append(genDateTime(JDBC_TIME_SDF,
						specs[i].getMinLongValue(), specs[i].getMaxLongValue()));
				break;
			case FieldSpec.TIMESTAMP_MILLI:
				buf.append(genDateTime(JDBC_DATETIME_SDF,
						specs[i].getMinLongValue(), specs[i].getMaxLongValue()));
				buf.append(String.format(".%06d", genPositiveLong(999999L)));
				break;
			case FieldSpec.TIMESTAMP_NANO:
				buf.append(genDateTime(JDBC_DATETIME_SDF,
						specs[i].getMinLongValue(), specs[i].getMaxLongValue()));
				buf.append(String.format(".%09d", genPositiveLong(999999999L)));
				break;
			case FieldSpec.TIMESTAMP:
				buf.append(genDateTime(JDBC_DATETIME_SDF,
						specs[i].getMinLongValue(), specs[i].getMaxLongValue()));
				break;
			case FieldSpec.SEQUENCE:
				buf.append(genSequence(specs[i].getName(),
						specs[i].getMinLongValue()));
				break;
			case FieldSpec.ENUM:
				EnumFieldSpec ef = (EnumFieldSpec)specs[i];
				int eix = genPositiveInt(ef.getEnumVlaues().length) % ef.getEnumVlaues().length;
				buf.append(ef.getEnumVlaues()[eix]);
				break;
			case FieldSpec.FORMAT_DIGITS:
				buf.append(genFormatedDigits((FormatDigitFieldSpec) specs[i]));
				break;
			case FieldSpec.FIXED_VALUE:
				buf.append(specs[i].getName());
				break;
			}
			if (i < specs.length - 1) {
				buf.append(delimiter);
			}
		}

		if (prefix != null) {
			buf.append(postfix);
		}
		out.print(buf);
		length = buf.length();
		return length;
	}

	public static class InvalidGrammarException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public InvalidGrammarException(String message) {
			super(message);
		}

		public static InvalidGrammarException throwException(String msg) {
			return new InvalidGrammarException(String.format(
					"Invalid grammar syntax:%s", msg));
		}

	}

}
