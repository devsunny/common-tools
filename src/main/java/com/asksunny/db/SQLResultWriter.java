package com.asksunny.db;

import java.sql.ResultSet;

public interface SQLResultWriter 
{
	public void write(ResultSet rs) throws Exception;
}
