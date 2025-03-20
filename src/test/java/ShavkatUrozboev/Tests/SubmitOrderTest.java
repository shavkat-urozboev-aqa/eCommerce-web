package ShavkatUrozboev.Tests;

import ShavkatUrozboev.PageObjects.LandingPage;
import ShavkatUrozboev.PageObjects.OrderPage;
import ShavkatUrozboev.PageObjects.ProductCatalogue;
import ShavkatUrozboev.TestComponents.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class SubmitOrderTest extends BaseTest {
    String productName = "ZARA COAT 3";

    @Test(dataProvider = "getData", groups = {"Purchase"})
    public void submitOrder(HashMap<String, String> input) throws IOException, InterruptedException {
        
        LandingPage landingPage = new LandingPage(driver.get());
        ProductCatalogue productCatalogue = landingPage.loginApplication(input.get("email"), input.get("password"));
    }

    @Test(dependsOnMethods = {"submitOrder"})
    public void OrderHistoryTest() {
        //"ZARA COAT 3";
        LandingPage landingPage = new LandingPage(driver.get());
        ProductCatalogue productCatalogue = landingPage.loginApplication("anshika@gmail.com", "Iamking@000");
        OrderPage ordersPage = productCatalogue.goToOrdersPage();
        Assert.assertTrue(ordersPage.VerifyOrderDisplay(productName));
    }


    //Extent Reports -

    @DataProvider
    public Object[][] getData() throws IOException {
        List<HashMap<String, String>> data = getJsonDataToMap(System.getProperty("user.dir")+"//src//test//java//ShavkatUrozboev//Data//DataComponents.json");
        return new Object[][]{{data.get(0)}, {data.get(1)}};
    }

//	 @DataProvider
//	  public Object[][] getData()
//	  {
//	    return new Object[][]  {{"anshika@gmail.com","Iamking@000","ZARA COAT 3"}, {"shetty@gmail.com","Iamking@000","ADIDAS ORIGINAL" } };
//	    
//	  }
//	HashMap<String,String> map = new HashMap<String,String>();
//	map.put("email", "anshika@gmail.com");
//	map.put("password", "Iamking@000");
//	map.put("product", "ZARA COAT 3");
//	
//	HashMap<String,String> map1 = new HashMap<String,String>();
//	map1.put("email", "shetty@gmail.com");
//	map1.put("password", "Iamking@000");
//	map1.put("product", "ADIDAS ORIGINAL");


}
