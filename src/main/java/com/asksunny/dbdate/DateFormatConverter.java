package com.asksunny.dbdate;

import java.io.StringReader;

public class DateFormatConverter {

	public static String toJavaDateFormat(String dbDateFormat)  {
		StringBuilder buf = new StringBuilder();		
		try {
			DbDateParser parser = new DbDateParser(new StringReader(dbDateFormat));
			java.util.List<DateFormatPart> dfps = parser.parseDateFormat();
			for (DateFormatPart dateFormatPart : dfps) {
				if(dateFormatPart.getType()==DateFormatPart.Type.YYYY){
					buf.append("yyyy");
				}else if(dateFormatPart.getType()==DateFormatPart.Type.YY){
					buf.append("yy");
				}else if(dateFormatPart.getType()==DateFormatPart.Type.MONTH_MM){
					buf.append("MM");
				}else if(dateFormatPart.getType()==DateFormatPart.Type.MONTH_MON){
					buf.append("MMM");
				}else if(dateFormatPart.getType()==DateFormatPart.Type.MONTH_MONTH){
					buf.append("MMMMM");
				}else if(dateFormatPart.getType()==DateFormatPart.Type.DAY){
					buf.append("dd");
				}else if(dateFormatPart.getType()==DateFormatPart.Type.HOUR){
					 if(dfps.get(dfps.size()-1).getType()==DateFormatPart.Type.AMPM) {
						 buf.append("hh");
					 }else{
						 buf.append("HH");
					 }
				}else if(dateFormatPart.getType()==DateFormatPart.Type.MINUTE){
					 buf.append("mm");
				}else if(dateFormatPart.getType()==DateFormatPart.Type.SECOND){
					 buf.append("ss");
				}else if(dateFormatPart.getType()==DateFormatPart.Type.MILLISECOND){
					 buf.append("SSS");
				}else if(dateFormatPart.getType()==DateFormatPart.Type.AMPM){
					 buf.append("aaa");
				}else if(dateFormatPart.getType()==DateFormatPart.Type.SEPARATOR){
					 buf.append(dateFormatPart.toString());
				}
			}
		} catch (ParseException e) {
			throw new DateFormatParsingException(String.format("Invalid date format String:%s", dbDateFormat), e);
		}
		return buf.toString();
	}

}
