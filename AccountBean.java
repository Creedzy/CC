package hello;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.routines.EmailValidator;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import hello.McBean.MCData;




@ManagedBean(name ="AccountBean")
@ViewScoped
public class AccountBean  {
	/**
	 * 
	 */
	
	public AccountBean() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
		UserID=(User) session.getAttribute("user");
		emails = currentEmails(UserID.getName());
		System.out.println("emails in constructor: " + emails);
    	try {
			videoData = populateList();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		
    }
	
	
    static AmazonDynamoDBClient dynamoDB;
    static List<VideoData> videoData ;
     List<VideoData> filteredVideoData;
    User UserID;
    private boolean emails;
    private StreamedContent file;
    private String changeMail;
    

	
	public void updateTable() throws Exception{
    	System.out.println(videoData);
    	
    	videoData = populateList();
    	
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
		dynamoDB.setRegion(usWest2);
	}
    
    public static class VideoData{
    	
    	String desc;
    	String videoName;
    	String videoFile;
    	String completed;
    	String mCLink;
    	
    	public VideoData(String desc,String videoName,String File,String Completed,String MCLink)
    	{
    		
    		this.desc = desc;
    		this.videoName = videoName;
    		this.videoFile = File;
    		this.completed = Completed;
    		this.mCLink = MCLink;
    	}
    	
        public String getdesc()
        {
            return desc;
        }

        public void setdesc(String desc)
        {
            this.desc = desc;
        }
        public String getvideoName()
        {
            return videoName;
        }

        public void setvideoName(String videoName)
        {
            this.videoName = videoName;
        }
        public String getvideoFile()
        {
            return videoFile;
        }

        public void setvideoFile(String File)
        {
            this.videoFile = File;
        }
        public String getcompleted()
        {
            return completed;
        }

        public void setcompleted(String Completed)
        {
            this.completed = Completed;
        }
        public String getmCLink()
        {
            return mCLink;
        }

        public void setmCLink(String MCLink)
        {
            this.mCLink = MCLink;
        }
    }
    public List<VideoData> getvideoData()
    {

        return videoData;
    }
    
    public void setvideoData(List<VideoData> List)
    {
    	this.videoData = List;
    }
   
	 
	public List<VideoData> populateList() throws Exception {
		init();
		videoData = new ArrayList<VideoData>();
		

		String tableName = "video_table";
		AWSCredentials credentials = null;
		try {

			credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (C:\\Users\\nas\\.aws\\credentials), and is in valid format.", e);
		}
		dynamoDB = new AmazonDynamoDBClient(credentials);
		Region usWest2 = Region.getRegion(Regions.EU_WEST_1);
		dynamoDB.setRegion(usWest2);

		Map<String, Condition> scanFilter = new HashMap<String, Condition>();
		scanFilter.put("name", new Condition().withAttributeValueList(new AttributeValue(UserID.getName()))
				.withComparisonOperator(ComparisonOperator.EQ));

		ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
		ScanResult scanResult = dynamoDB.scan(scanRequest);

		for (int i = 0; i < scanResult.getCount(); i++) {
			
			String tempDesc = scanResult.getItems().get(i).get("desc").getS();
			String tempFile = scanResult.getItems().get(i).get("File").getS();
			String tempVideoName = scanResult.getItems().get(i).get("userID").getS();
			String tempCompleted = scanResult.getItems().get(i).get("Completed").getS();
			String tempMCLink = scanResult.getItems().get(i).get("MCLink").getS();
			/*
			 * System.out.println("User: " + tempUser); System.out.println(
			 * "Desc: " + tempDesc); System.out.println("File: " + tempFile);
			 * System.out.println("VideoName: " + tempVideoName);
			 * System.out.println("Completed: " + tempCompleted);
			 * System.out.println("MCLink: " + tempMCLink);
			 */
			videoData.add(new VideoData( tempDesc, tempVideoName, tempFile, tempCompleted, tempMCLink));

		}

		
		return videoData;
	}
	
public  StreamedContent downloadObject(VideoData key) throws IOException{
		
		InputStream objectData = s3methods.downloadFile(key.getmCLink()).getObjectContent();
		
        file = new DefaultStreamedContent(objectData, "application/octet-stream", key.getvideoName()+"." + getExtension(s3methods.downloadFile(key.getmCLink()).getKey()));
        System.out.println("download file in AB is: " + getExtension(s3methods.downloadFile(key.getmCLink()).getKey()));
     // Process the objectData stream.
     		return file;
	}
public  StreamedContent downloadVideo(VideoData key) throws IOException{
	
	InputStream objectData = s3methods.downloadFile(key.getvideoFile()).getObjectContent();
	
    file = new DefaultStreamedContent(objectData, "application/octet-stream", key.getvideoName()+"." + getExtension(s3methods.downloadFile(key.getvideoFile()).getKey()));
    System.out.println("download file in AB is: " + getExtension(s3methods.downloadFile(key.getmCLink()).getKey()));
 // Process the objectData stream.
 		return file;
}
	public void checkbox()
	{
		 if(emails){
	    	   emails = DbConnect.activateEmails(UserID.getName());
	    	   
	    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Email notifications deactivated"));
	       } else {
	    	   emails = DbConnect.activateEmails(UserID.getName());
	    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Email notifications activated"));
	           
	       }
	}
	public void addMessage() throws NoSuchAlgorithmException {
      
       if(changeMail!="") {
    	   System.out.println(changeMail);
    	   if(EmailValidator.getInstance().isValid(changeMail) && DbConnect.checkEmail(changeMail)){
    		   DbConnect.updateEmail(UserID.getName(),changeMail);
    		   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Email Updated. Please verify your new email."));
    		   byte[] hash = createHash();
    			
    			AmazonSES.sendUpdateEmail(UserID.getName(),changeMail, DbConnect.stringifyHash(hash));
    			DbConnect.updateHash(UserID.getName(), hash);
    	   } else {
    		   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Email is not in a valid format or is already in use."));
    	   }
    	   
       }
      
    }
	
	public byte[] createHash() throws NoSuchAlgorithmException
	{
		
		String uuid = UUID.randomUUID().toString();
		String key = UserID.getName() + "." + uuid;
		
		MessageDigest md = MessageDigest.getInstance( "SHA-512" );
	    md.reset();
	    md.update( key.getBytes(StandardCharsets.UTF_8) );
	    byte[] aMessageDigest = md.digest();
			
			return aMessageDigest;
		
	}
	
	public static boolean currentEmails(String hashKey)
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
		return emailBoolean;
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
	public String getEmail() throws Exception{
		return DbConnect.getUser(UserID.getName()).getEmail();
	}
	public User getUserID() {
		return UserID;
	}
	public void setUserID(User userID) {
		UserID = userID;
	}
	public  List<VideoData> getFilteredVideoData() {
		return filteredVideoData;
	}
	public void setFilteredVideoData(List<VideoData> filteredVideoData) {
		this.filteredVideoData = filteredVideoData;
	}
	public boolean isEmails() {
		return emails;
	}
	public void setEmails(boolean emails) {
		this.emails = emails;
	}
	public String getChangeMail() {
		return changeMail;
	}
	public void setChangeMail(String changeMail) {
		this.changeMail = changeMail;
	}
	
	
}
