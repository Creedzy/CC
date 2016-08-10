package hello;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.view.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@ManagedBean(name = "mcBean")
@ViewScoped
public class McBean {

	public McBean() {
		try {
			
			mcData = populateList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static AmazonDynamoDBClient dynamoDB;
	List<MCData> mcData;
	List<MCData> filteredMC;
	private StreamedContent file;

	public List<MCData> getFilteredMC() {
		return filteredMC;
	}

	public void setFilteredMC(List<MCData> filteredMC) {
		this.filteredMC = filteredMC;
	}

	private static void init() throws Exception {
		
			
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (C:\\Users\\nas\\.aws\\credentials), and is in valid format.", e);
		}
		dynamoDB = new AmazonDynamoDBClient(credentials);
		Region usWest2 = Region.getRegion(Regions.EU_WEST_1);
		((AmazonDynamoDB) dynamoDB).setRegion(usWest2);
	}

	public static class MCData {
		String mcName;
		String mcCategory;
		String mcDescription;
		String mcBodyparts;
		String mcExtention;
		String mCLink;

		public MCData(String Name, String Category, String desc, String Bodyparts, String Extention, String MCLink) {
			this.mcName = Name;
			this.mcCategory = Category;
			this.mcDescription = desc;
			this.mcBodyparts = Bodyparts;
			this.mcExtention = Extention;
			this.mCLink = MCLink;
		}

		public String getmcName() {
			return mcName;
		}

		public void setmcName(String name) {
			this.mcName = name;
		}

		public String getmcCategory() {
			return mcCategory;
		}

		public void setmcCategory(String Category) {
			this.mcCategory = Category;
		}

		public String getmcDescription() {
			return mcDescription;
		}

		public void setmcDescription(String desc) {
			this.mcDescription = desc;
		}

		public String getmcBodyparts() {
			return mcBodyparts;
		}

		public void setmcBodyparts(String Parts) {
			this.mcBodyparts = Parts;
		}

		public String getmcExtention() {
			return mcExtention;
		}

		public void setcompleted(String ext) {
			this.mcExtention = ext;
		}

		public String getmCLink() {
			return mCLink;
		}

		public void setmCLink(String MCLink) {
			this.mCLink = MCLink;
		}
	}

	public List<MCData> getmcData() {

		return mcData;
	}

	public List<MCData> populateList() throws Exception {
		init();
		mcData = new ArrayList<MCData>();
		String userID = "nas2";
		String tableName = "MCDatabase";
		

		ScanRequest scanRequest = new ScanRequest(tableName).withTableName(tableName);
		ScanResult scanResult = dynamoDB.scan(scanRequest);

		for (int i = 0; i < scanResult.getCount(); i++) {
			String tempmcName = scanResult.getItems().get(i).get("MCName").getS();
			String tempmcCategory = scanResult.getItems().get(i).get("MCCategory").getS();
			String tempmcDesc = scanResult.getItems().get(i).get("MCDescription").getS();
			String tempmcBodyparts = scanResult.getItems().get(i).get("MCBodyparts").getS();
			String tempmcExt = scanResult.getItems().get(i).get("MCExtention").getS();
			String tempMCLink = scanResult.getItems().get(i).get("MCLink").getS();
			/*
			 * System.out.println("User: " + tempUser); System.out.println(
			 * "Desc: " + tempDesc); System.out.println("File: " + tempFile);
			 * System.out.println("VideoName: " + tempVideoName);
			 * System.out.println("Completed: " + tempCompleted);
			 * System.out.println("MCLink: " + tempMCLink);
			 */
			mcData.add(new MCData(tempmcName, tempmcCategory, tempmcDesc, tempmcBodyparts, tempmcExt, tempMCLink));

		}
		System.out.println("ArrayList Size: " + mcData.size());

		return mcData;
	}
	public  StreamedContent downloadObject(MCData key) throws IOException{
		
		InputStream objectData = s3methods.downloadFile(key.getmCLink()).getObjectContent();
		
        file = new DefaultStreamedContent(objectData, "application/octet-stream", key.getmcName()+"." + getExtension(s3methods.downloadFile(key.getmCLink()).getKey()));
        System.out.println("file in McBean is: "+ getExtension(s3methods.downloadFile(key.getmCLink()).getKey()));
     // Process the objectData stream.
     		return file;
	}

	public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int extensionPos = filename.lastIndexOf('.');
        int lastUnixPos = filename.lastIndexOf('/');
        int lastWindowsPos = filename.lastIndexOf('\\');
        int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);
 
        int index = lastSeparator > extensionPos ? -1 : extensionPos;
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }
	
	public StreamedContent getFile() {
		return file;
	}
	
	
}
