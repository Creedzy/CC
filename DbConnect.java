package hello;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;





public class DbConnect {

	static AmazonDynamoDBClient dynamoDB;
 	
	   public static void init() throws Exception
	   {
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
	   }
	
	

	public static void addEntry(String name,String Email, String password, boolean activated,byte[] hash) throws Exception 
	{	init();
	
	User user = new User();
   	user.setName(name);
   	user.setEmail(Email);
   	user.setPassword(password);
   	user.setRole("USER");
   	user.setActivated(activated);
   	user.setHashKey(hash);
     System.out.println(user);
    
     DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
     mapper.save(user);
}
	public static User getUser(String hashKey) throws Exception
	{	init();
		User user = new User();
		DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
		user = mapper.load(User.class, hashKey);
		return user;
	}
	public static boolean checkName(String checkName){
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tableName = "usr_reg2";
		Map<String, Condition> scanFilter = new HashMap<String, Condition>();
		scanFilter.put("name", new Condition().withAttributeValueList(new AttributeValue(checkName)).withComparisonOperator(ComparisonOperator.EQ));
		
		ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
        ScanResult scanResult = dynamoDB.scan(scanRequest);
        
        if (scanResult.getCount()!=0)
        {
        	return false;
        }
        else
        {
        	return true;
        }
        
		
	}
	
	public static boolean activateEmails(String hashKey)
	{
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
		User user = mapper.load(User.class, hashKey);
		boolean emailBoolean = user.getEmails();
		System.out.println("emails before change: "+emailBoolean);
		emailBoolean = !emailBoolean;
		System.out.println("emails after change: "+emailBoolean);
		user.setEmails(emailBoolean);
		mapper.save(user);
		return emailBoolean;
	}
	public static boolean checkEmail(String checkEmail){
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tableName = "usr_reg2";
		Map<String, Condition> scanFilter = new HashMap<String, Condition>();
		scanFilter.put("email", new Condition().withAttributeValueList(new AttributeValue(checkEmail)).withComparisonOperator(ComparisonOperator.EQ));
		
		ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
        ScanResult scanResult = dynamoDB.scan(scanRequest);
        
        if (scanResult.getCount()!=0)
        {
        	return false;
        }
        else
        {
        	return true;
        }
        
		
	}
	
	public static void addVideoDesc(String userID,String desc,String name,String File,String Completed, String MCLink) throws Exception 
	{	init();
	
    try {
        String tableName = "video_table";

        
        // Add an item
        Map<String, AttributeValue> item = newItem(userID,desc,name,File,Completed,MCLink);
        PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
        System.out.println("Result: " + putItemResult);

       

    } catch (AmazonServiceException ase) {
        System.out.println("Caught an AmazonServiceException, which means your request made it "
                + "to AWS, but was rejected with an error response for some reason.");
        System.out.println("Error Message:    " + ase.getMessage());
        System.out.println("HTTP Status Code: " + ase.getStatusCode());
        System.out.println("AWS Error Code:   " + ase.getErrorCode());
        System.out.println("Error Type:       " + ase.getErrorType());
        System.out.println("Request ID:       " + ase.getRequestId());
    } 
}
	public static void addMCDesc(String nameMC,String bMC,String categMC,String descMC,String extentMC,String linkMC) {
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 Map<String, AttributeValue> item = mcItem(nameMC,bMC,categMC,descMC,extentMC,linkMC);
         PutItemRequest putItemRequest = new PutItemRequest("MCDatabase", item);
         PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
	}
	
	public static void getVideoItems(String userID)
	{
		 String tableName = "video_table";
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
	       
	       Map<String, Condition> scanFilter = new HashMap<String, Condition>();
           scanFilter.put("userID", new Condition().withAttributeValueList(new AttributeValue(userID)).withComparisonOperator(ComparisonOperator.EQ));
        
           
           ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
           ScanResult scanResult = dynamoDB.scan(scanRequest);
          
           System.out.println("Result: " + scanResult);
           
           
		
		
	}
	
	public static void updateUserData(String hashKey){
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
		User user = mapper.load(User.class, hashKey);
		user.setActivated(true);
		user.setHashKey(null);
		user.setEmailActive(true);
		mapper.save(user);
	}
	public static void updateEmail(String hashKey,String Value){
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
		User user = mapper.load(User.class, hashKey);
		user.setEmail(Value);
		mapper.save(user);
	}
	
	public static void RemoveFromMailingList(String Email){
		 String tableName = "usr_reg2";
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
		       
		       Map<String, Condition> scanFilter = new HashMap<String, Condition>();
	           scanFilter.put("email", new Condition().withAttributeValueList(new AttributeValue(Email)).withComparisonOperator(ComparisonOperator.EQ));   
	           ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
	           ScanResult scanResult = dynamoDB.scan(scanRequest);
	          
	           System.out.println("Result: " + scanResult);
	           if(scanResult.getCount()!=0) {
	        	   
	        	  
		           System.out.println(scanResult.getItems().get(0).get("name").getS());
		           
		           
		           DynamoDBMapper dbmapper = new DynamoDBMapper(dynamoDB);
		       		User user = dbmapper.load(User.class, scanResult.getItems().get(0).get("name").getS());
		       		user.setEmail("");
		       		dbmapper.save(user);
	           }
	}
	
	public static void updateHash(String hashKey,byte[] HASHKEY){
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
		User user = mapper.load(User.class, hashKey);
		user.setHashKey(HASHKEY);
		mapper.save(user);
	}
	
	public static String stringifyHash(byte[] theHash)
	{
		 String hexStr = "";
         for (int i = 0; i < theHash.length; i++) {
             hexStr +=  Integer.toString( ( theHash[i] & 0xff ) + 0x100, 16).substring( 1 );
         }
			return hexStr;
	}
	public static  void UpdateItem(String tableName,String attValue,String KeyToUpdate, String updateValue) {
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 HashMap<String,AttributeValueUpdate> updates=new HashMap<String,AttributeValueUpdate>();
         AttributeValueUpdate update = new AttributeValueUpdate().withValue(new AttributeValue(updateValue));
         Map<String,AttributeValue> key2 = new HashMap<String,AttributeValue>();
         key2.put("userID", new AttributeValue(attValue));
         updates.put(KeyToUpdate, update);
         try {
 		    UpdateItemRequest req=new UpdateItemRequest(tableName,key2,updates);
 		    System.out.println("requeest"+req);
 		    UpdateItemResult res=dynamoDB.updateItem(req);
 		   System.out.println("result"+res);
 		    
 		  }
 		 catch (  AmazonServiceException ase) {
 		    System.err.println("Failed to update item: " + ase.getMessage());
 		  }
	}
	private static Map<String, AttributeValue> newItem(String name,String email,String Password) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("userID", new AttributeValue(name));
        item.put("email", new AttributeValue(email));
        item.put("Password", new AttributeValue(Password));

	        return item;
	    }
	private static Map<String, AttributeValue> newItem(String userID,String desc,String videoName,String File,String Completed, String MCLink) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("userID", new AttributeValue(videoName));
        item.put("desc", new AttributeValue(desc));
        item.put("name", new AttributeValue(userID));
        item.put("File", new AttributeValue(File));
        item.put("Completed", new AttributeValue(Completed));
        item.put("MCLink", new AttributeValue(MCLink));
	        return item;
	    }
	private static Map<String, AttributeValue> mcItem(String MCName, String MCBodyparts, String MCCategory, String MCDescription,String MCExtention,String MCLink) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("MCName", new AttributeValue(MCName));
        item.put("MCBodyparts", new AttributeValue(MCBodyparts));
        item.put("MCCategory", new AttributeValue(MCCategory));
        item.put("MCDescription", new AttributeValue(MCDescription));
        item.put("MCExtention", new AttributeValue(MCExtention));
        item.put("MCLink", new AttributeValue(MCLink));
        

        return item;
    }
	
}
