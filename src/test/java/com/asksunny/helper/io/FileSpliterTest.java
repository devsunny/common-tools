package com.asksunny.helper.io;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileSpliterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSplit() throws Exception{
		String f = getClass().getResource("/test.dat").getPath();
		FileBlock[] blocks  =   FileSpliter.split("src/test/resources/test.dat", '\n', 5, 20L);
		assertNotNull(blocks);
		assertEquals(4, blocks.length);
		FileBlockInputStream fbin = new FileBlockInputStream(blocks[0]);
		StringBuilder buf = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(fbin));
		String line = null;
		while((line=reader.readLine())!=null){
			buf.append(line);
		}
		fbin.close();	
		assertEquals("AAABBBCCCDDDEEE1XXXXXXXXXXXXX12XXXXXXXXXXXXX2", buf.toString());
			
		
		fbin = new FileBlockInputStream(blocks[1]);
		buf = new StringBuilder();
		reader = new BufferedReader(new InputStreamReader(fbin));
		line = null;
		while((line=reader.readLine())!=null){
			buf.append(line);
		}
		fbin.close();	
		assertEquals("3XXXXXXXXXXXXX34XXXXXXXXXXXXX45XXXXXXXXXXXXX5", buf.toString());
		
		
		fbin = new FileBlockInputStream(blocks[2]);
		buf = new StringBuilder();
		reader = new BufferedReader(new InputStreamReader(fbin));
		line = null;
		while((line=reader.readLine())!=null){
			buf.append(line);
		}
		fbin.close();	
		assertEquals("6XXXXXXXXXXXXX67XXXXXXXXXXXXX78XXXXXXXXXXXXX8", buf.toString());
		
		fbin = new FileBlockInputStream(blocks[3]);
		buf = new StringBuilder();
		reader = new BufferedReader(new InputStreamReader(fbin));
		line = null;
		while((line=reader.readLine())!=null){
			buf.append(line);
		}
		fbin.close();	
		assertEquals("9XXXXXXXXXXXXX9", buf.toString());
		
		
	}
	
	
	@Test
	public void testSplitInputStream() throws Exception{
		String f = getClass().getResource("/test.dat").getPath();
		InputStream[] ins  =   FileSpliter.splitInputStream("src/test/resources/test.dat", '\n', 5, 20L);
		assertNotNull(ins);
		assertEquals(4, ins.length);
		
		StringBuilder buf = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(ins[0]));
		String line = null;
		while((line=reader.readLine())!=null){
			buf.append(line);
		}
		ins[0].close();	
		assertEquals("AAABBBCCCDDDEEE1XXXXXXXXXXXXX12XXXXXXXXXXXXX2", buf.toString());
		
		
		
		InputStream fbin = ins[1];
		buf = new StringBuilder();
		reader = new BufferedReader(new InputStreamReader(fbin));
		line = null;
		while((line=reader.readLine())!=null){
			buf.append(line);
		}
		fbin.close();	
		assertEquals("3XXXXXXXXXXXXX34XXXXXXXXXXXXX45XXXXXXXXXXXXX5", buf.toString());
		
		
		fbin =ins[2];
		buf = new StringBuilder();
		reader = new BufferedReader(new InputStreamReader(fbin));
		line = null;
		while((line=reader.readLine())!=null){
			buf.append(line);
		}
		fbin.close();	
		assertEquals("6XXXXXXXXXXXXX67XXXXXXXXXXXXX78XXXXXXXXXXXXX8", buf.toString());
		
		fbin = ins[3];
		buf = new StringBuilder();
		reader = new BufferedReader(new InputStreamReader(fbin));
		line = null;
		while((line=reader.readLine())!=null){
			buf.append(line);
		}
		fbin.close();	
		assertEquals("9XXXXXXXXXXXXX9", buf.toString());
		
		
	}
	
	

}
