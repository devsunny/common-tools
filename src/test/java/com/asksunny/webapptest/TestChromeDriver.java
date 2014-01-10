package com.asksunny.webapptest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;



public class TestChromeDriver {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		  
		  System.setProperty("webdriver.chrome.driver", "c:/java/webdriver/chromedriver.exe");
		  WebDriver driver = new ChromeDriver();
		  driver.get("http://www.google.com/xhtml");
		  Thread.sleep(5000);  // Let the user actually see something!
		  WebElement searchBox = driver.findElement(By.name("q"));
		  searchBox.sendKeys("ChromeDriver");
		  searchBox.submit();	
		  
		  Thread.sleep(5000);  // Let the user actually see something!		  
		  System.out.println("****************************************************Page title is: " + driver.getTitle());		  
		  driver.quit();		
		
	}

}
