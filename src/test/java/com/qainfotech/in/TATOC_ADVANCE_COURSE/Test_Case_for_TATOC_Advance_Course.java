package com.qainfotech.in.TATOC_ADVANCE_COURSE;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
//import org.json.JSONException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.RestAssured;


public class Test_Case_for_TATOC_Advance_Course {
	WebDriver driver;
	
	  @Test
	  public void Open_Browser(){
		  System.setProperty("webdriver.chrome.driver", "/home/qainfotech/chromedriver");
	  driver = new ChromeDriver();
	  driver.get("http://10.0.1.86/tatoc");
	  }
	@Test
	  public void Select_Advance_Course() {
		  driver.findElement(By.linkText("Advanced Course")).click();
	  }
	  @Test(dependsOnMethods= {"Select_Advance_Course"})
	  public void Select_Menu2_and_Click_GO_To_nextPage() throws InterruptedException {
		  //driver.findElement(By.className("Menu 2")).click();
		  //driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/span[5]")).click();
		  WebElement menu2=driver.findElement(By.cssSelector(".menutitle"));
			
			Actions hover=new Actions(driver);
			hover.moveToElement(menu2).build().perform();
			Thread.sleep(1000);
			//driver.findElement(By.xpath("//div[@class='menutop m2']//span[text()='Go Next']")).click();
			driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/span[5]")).click();
		}
	  @Test(dependsOnMethods= {"Select_Menu2_and_Click_GO_To_nextPage"})
	  public void Query_gate() throws ClassNotFoundException, SQLException {
		driver.get("http://10.0.1.86/tatoc/advanced/query/gate");
		  // String Symbol = driver.findElement(By.cssSelector("#symboldisplay")).getText().toString();
		 //System.out.println(Symbol);
		// Database_TO_Get_Valid_Data db =new Database_TO_Get_Valid_Data(Symbol);
		 String value = null, name = null, passkey = null;
			String dbUrl = "jdbc:mysql://10.0.1.86/tatoc";
			//String symbol = driver.findElement(By.cssSelector("#symboldisplay")).getAttribute("innerHTML");
		String symbol = driver.findElement(By.xpath("//*[@id='symboldisplay']")).getText();
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(dbUrl, "tatocuser", "tatoc01");
			Statement statement = connection.createStatement();
			ResultSet resultSet1 = statement.executeQuery("select *  from identity;");
			while (resultSet1.next()) {
				if (symbol.toLowerCase().equals(resultSet1.getString(2))) {
					value = resultSet1.getString(1);
				}
			}
			ResultSet resultSet2 = statement.executeQuery("select *  from credentials;");
			while (resultSet2.next()) {
				if (value.equals(resultSet2.getString(1))) {
					name = resultSet2.getString(2);
					passkey = resultSet2.getString(3);
				}
			}
			driver.findElement(By.id("name")).sendKeys(name);
			driver.findElement(By.id("passkey")).sendKeys(passkey);
			driver.findElement(By.id("submit")).click();
	      //  driver.quit();
	  }
	  @Test(dependsOnMethods = {"Query_gate"})
	  public void rest_Service() throws InterruptedException, IOException, JSONException
		{
			driver.get("http://10.0.1.86/tatoc/advanced/rest");
			String sessid = driver.findElement(By.id("session_id")).getText();
	        sessid = sessid.substring(12,sessid.length());
	        String Resturl = "http://10.0.1.86/tatoc/advanced/rest/service/token/"+sessid;

	        URL url = new URL(Resturl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Accept", "application/json");

	        if (conn.getResponseCode() != 200) {
	            throw new RuntimeException("Failed : HTTP error code : "
	                    + conn.getResponseCode());
	        }

	        BufferedReader in = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();

	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();
	        String responsejson=new String(response);
	        
	        JSONObject obj=new JSONObject(responsejson);
	        responsejson=(String) obj.get("token");
	        
	        URL url1 = new URL("http://10.0.1.86/tatoc/advanced/rest/service/register");
	        HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
	        

	        conn1.setRequestMethod("POST");
	        
	        conn1.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

	        String urlParameters = "id="+sessid+"&signature="+responsejson+"&allow_access=1";
	        
	        conn1.setDoOutput(true);
	        DataOutputStream wr = new DataOutputStream(conn1.getOutputStream());
	        wr.writeBytes(urlParameters);
	        wr.flush();
	        wr.close();

	        int responseCode = conn1.getResponseCode();
	        
	        conn1.disconnect();
	        driver.findElement(By.xpath("/html/body/div/div[2]/a")).click();
	       
		    
		   
		   
		
		}
		@Test(dependsOnMethods = {"rest_Service"})
		public void test005_fileHandling() throws FileNotFoundException, InterruptedException
		{
			String element;
			driver.findElement(By.xpath("/html/body/div/div[2]/a")).click();
			Thread.sleep(2000);
			String path= "/home/qainfotech/Downloads/file_handle_test.dat";
			File file = new File(path);
			Scanner scnr = new Scanner(file);
			 List<String> storedlist= new ArrayList<String>();
			while(scnr.hasNextLine()){
			   String line = scnr.nextLine();
			   //System.out.println(line);
			   storedlist.add(line);
			  
			}
			element= storedlist.get(2);
			//System.out.println("element  "+signature);
			String [] arrayS= element.split(":");
			String signature=arrayS[1].trim();
			System.out.println("element  "+signature);
			
			driver.findElement(By.xpath("//*[@id='signature']")).sendKeys(signature);
					driver.findElement(By.xpath("/html/body/div/div[2]/form/input[2]")).click();
		}
	  }
