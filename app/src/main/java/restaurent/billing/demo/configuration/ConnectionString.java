package restaurent.billing.demo.configuration;

public class ConnectionString 
{

	private static String SERVER_URL = "";
	
	public ConnectionString()
	{
		
		// SERVER_URL="http://192.168.43.53:81/gmc_manasa_api/";

		// SERVER_URL="http://172.16.80.12:81/gmc_manasa_api/";

		SERVER_URL="http://www.wildorchidsadventures.com/gmc_manasa_api/";
	}
	
	
	public String getURL()
	{
		return SERVER_URL;
	}
}