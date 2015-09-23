package com.asksunny.utils;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

public class RestfulBeanUtil {

	
	
	public static void bind(Object pojo, HttpServletRequest request) throws Exception
	{
		
		Field[] fields = pojo.getClass().getFields();	
		for (Field field : fields) {
			String fname = null;
			FormParam fpAnno = field.getAnnotation(FormParam.class);
			if(fpAnno!=null){
				fname = fpAnno.value();
			}else{
				QueryParam qpAnno = field.getAnnotation(QueryParam.class);
				if(qpAnno!=null) fname = qpAnno.value();
			}
			if(fname==null) fname = field.getName();
			
			String[]  pvals = request.getParameterValues(fname);
		
			
			
			
		}
		
		
		
	}
	
	
	
	private  RestfulBeanUtil() {
		// TODO Auto-generated constructor stub
	}

	

}
