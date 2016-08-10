package hello;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class LoginValidate {

	static AmazonDynamoDBClient dynamoDB;
	 static User user;
	public static  User getUser()
	{	
		return user;	
	}
	public void setUser(User user)
	{
		this.user = user;
	}
	
	
	public static boolean validate(String username,String password)
	{
		 String tableName = "usr_reg";
		AWSCredentials credentials = null;
	       try {
	          
	           credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
	       } catch (Exception e) {
	           throw new AmazonClientException(
	                   "Cannot load the credentials from the credential profiles file. " +
	                   "Please make sure that your credentials file is at the correct " +
	                   "location (C:\\Users\\nas\\.aws\\credentials), and is in valid format.",
	                   e);
	       }
	       dynamoDB = new AmazonDynamoDBClient(credentials);
	       Region usWest2 = Region.getRegion(Regions.EU_WEST_1);
	       dynamoDB.setRegion(usWest2);
	       
	       DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
	        user = mapper.load(User.class, username);
	        try {
	        	if(user.getPassword().equals(password)){
	 	    	  // System.out.println(user.getPassword() +" true  " + password);
	 	    	   return true;
	 	    	   
	 	       } else 
	 	       {	
	 	    	   //System.out.println(user.getPassword() +"  false  " + password);
	 	    	   return false;
	 	       }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
	       
	       
	     /*  Map<String, Condition> scanFilter = new HashMap<String, Condition>();
           scanFilter.put("userID", new Condition().withAttributeValueList(new AttributeValue(username)).withComparisonOperator(ComparisonOperator.EQ));
           scanFilter.put("Password", new Condition().withAttributeValueList(new AttributeValue(password)).withComparisonOperator(ComparisonOperator.EQ));
           
           ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
           ScanResult scanResult = dynamoDB.scan(scanRequest);
          
           System.out.println("Result: " + scanResult);
         
           if(scanResult.getCount()!= 0 ){
        	   return false;
               } else
               {
            	   return true;
               }  */
		
		
	}

}
