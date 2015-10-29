package com.asksunny;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.asksunny.maven.PomXmlParser;

public class PomXmlParserTest {

	private File baseDir = null;
	
	@Before
	public void setUp() throws Exception {
		URL url = getClass().getResource("/");
		Path path = Paths.get(url.toURI());
		baseDir = path.getParent().getParent().toFile();		
	}

	@Test
	public void test() throws Exception{
		FileReader f = null;
		try{
			f = new FileReader(new File(baseDir, "pom.xml"));
			PomXmlParser parser = new PomXmlParser(f).parse();				
			assertNotNull(parser.getVersion());
			System.out.println(parser.getVersion());
		}finally{
			if(f!=null){
				f.close();
			}
		}
		
	}

}
