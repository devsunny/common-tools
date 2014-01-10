package com.asksunny.demotest

import org.specs2._

import org.openqa.selenium._
import org.openqa.selenium.chrome._


//http://chromedriver.storage.googleapis.com/index.html

class Spec2DemoTest extends mutable.Specification
{

  "The chrome Driver test" should {
    "launch chrome broswer" in {     
          System.setProperty("webdriver.chrome.driver", "c:/java/webdriver/chromedriver.exe")
		  val driver = new ChromeDriver()
		  driver.get("http://www.google.com/xhtml")
		  Thread.sleep(5000)  
		  val searchBox = driver.findElement(By.name("q"))
		  searchBox.sendKeys("ChromeDriver")
		  searchBox.submit()			  
		  Thread.sleep(5000);  // Let the user actually see something!		  
		  val title = driver.getTitle()		
		  println("****************************************************Page title is: " + title)
		  driver.quit()	
		  title must startWith("ChromeDriver")		            
    }
    
  }
  
}
