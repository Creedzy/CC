package hello;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import com.sun.mail.util.MailLogger;

import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;



@ManagedBean(name="AdminBean")
@ViewScoped
public class AdminBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
    static AmazonDynamoDBClient dynamoDB;
    static List<VideoData> videoData ;
    List<VideoData> filteredData;
    private User UserID;
    private UploadedFile uploadedFile;
    private UploadedFile mcFile;
   

	private String key;
    private String tableName = "video_table";
    
    private String MCName;
    private String MCCategory;
    private String MCBodyparts;
    private String MCDescription;
    private String MCExtention;
    private String setEditMCName;
    
	public AdminBean() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
		UserID=(User) session.getAttribute("user");
		
    	try {
			videoData = populateList();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
    
    
    private static void init() throws Exception {
        
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
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
    
    public static class VideoData{
    	String userID;
    	String desc;
    	String videoName;
    	String videoFile;
    	String completed;
    	String mCLink;
    	private boolean editable;
    	
    	public VideoData(String userID,String desc,String videoName,String File,String Completed,String MCLink)
    	{
    		this.userID = userID;
    		this.desc = desc;
    		this.videoName = videoName;
    		this.videoFile = File;
    		this.completed = Completed;
    		this.mCLink = MCLink;
    	}
    	public String getUserID()
        {
            return userID;
        }

        public void setUserID(String userID)
        {
            this.userID = userID;
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
		public boolean isEditable() {
			return editable;
		}
		public void setEditable(boolean editable) {
			this.editable = editable;
		}
    }
    public String editAction(VideoData videoData) {
	    
    	videoData.setEditable(true);
    	return null;
    }
    public String hideAction(VideoData videoData) {
    	videoData.setEditable(false);
		return null;
    }
    public List<VideoData> getvideoData()
    {

        return videoData;
    }
    
    
	
	 public UploadedFile getMcFile() {
			return mcFile;
		}

		public void setMcFile(UploadedFile mcFile) {
			this.mcFile = mcFile;
		}
		public String getMCName() {
			return MCName;
		}

		public void setMCName(String mCName) {
			MCName = mCName;
		}

		public String getMCCategory() {
			return MCCategory;
		}

		public void setMCCategory(String mCCategory) {
			MCCategory = mCCategory;
		}

		public String getMCBodyparts() {
			return MCBodyparts;
		}

		public void setMCBodyparts(String mCBodyparts) {
			MCBodyparts = mCBodyparts;
		}

		public String getMCDescription() {
			return MCDescription;
		}

		public void setMCDescription(String mCDescription) {
			MCDescription = mCDescription;
		}

		public String getMCExtention() {
			return MCExtention;
		}

		public void setMCExtention(String mCExtention) {
			MCExtention = mCExtention;
		}

    public List<VideoData> populateList() throws Exception {
        init();
        videoData = new ArrayList<VideoData>();
       
       String completed = "no";
       
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
           scanFilter.put("Completed", new Condition().withAttributeValueList(new AttributeValue(completed)).withComparisonOperator(ComparisonOperator.EQ));
        
           
           ScanRequest scanRequestFilter = new ScanRequest(tableName).withScanFilter(scanFilter);
           ScanRequest scanRequest = new ScanRequest(tableName).withTableName(tableName);
           ScanResult scanResult = dynamoDB.scan(scanRequest);
        
        	 
        	 for(int i=0;i<scanResult.getCount();i++){
        		 String tempUser = scanResult.getItems().get(i).get("name").getS();
        		 String tempDesc = scanResult.getItems().get(i).get("desc").getS();
        		 String tempFile = scanResult.getItems().get(i).get("File").getS();
        		 String tempVideoName = scanResult.getItems().get(i).get("userID").getS();
        		 String tempCompleted = scanResult.getItems().get(i).get("Completed").getS();
        		 String tempMCLink = scanResult.getItems().get(i).get("MCLink").getS();
        		
        		 videoData.add(new VideoData(tempUser,tempDesc,tempVideoName,tempFile,tempCompleted,tempMCLink));
        		 
        	 }
        	 
        	
        	 for (VideoData p : videoData){
        		   if(p.userID=="nas2"){
        			   System.out.println("ArrayList: " + p.userID) ;
        		   }
        	 }
			return videoData;
    }
	public User getUserID() {
		return UserID;
	}
	public void setUserID(User userID) {
		UserID = userID;
	}
	public void saveAction(String attValue) throws Exception{
		init();
		String fileExtention = getExtension(uploadedFile.getFileName());
		
		System.out.println(fileExtention);
	    
	    if(allowedExtensions(fileExtention)) {
	    	key ="MotionCapture/"+ MCName+ "."+fileExtention;
	    	InputStream input = uploadedFile.getInputstream();
	   
	    try {
			DbConnect.UpdateItem(tableName,attValue,"MCLink",key);
			DbConnect.UpdateItem(tableName, attValue,"Completed", "yes");
			s3methods.uploadFile(key, createSampleFile(input,fileExtention));
			DbConnect.addMCDesc(MCName, MCBodyparts, MCCategory, MCDescription,MCExtention, key);
			if(DbConnect.getUser(UserID.getName()).getEmails() && DbConnect.getUser(UserID.getName()).getEmailActive() ){
				System.out.println(UserID.getEmails());
				for(VideoData video : videoData)
				{
					if(video.getvideoName().equals(attValue)){
						AmazonSES.sendCompletionEmail(DbConnect.getUser(video.getUserID()).getEmail(),MCName,s3methods.getURL(key));
						AmazonSES.ComplaintHandleRequest();
						AmazonSES.BounceHandleRequest();
					}
				}
				
			} else {
				System.out.println("nomail");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    } else 
	    {
	    	FacesContext.getCurrentInstance().addMessage("uploadForm:file", new FacesMessage("Please upload a video in .avi,.wmv or .mp4 format"));
	    }
		
	}
	public void uploadMC() throws Exception 
	{
		init();
		String fileExtention = getExtension(mcFile.getFileName());
		System.out.println(fileExtention);
	    InputStream input = mcFile.getInputstream();
	    
	    if(allowedExtensions(fileExtention)) {
	    	key ="MotionCapture/"+ MCName+ "."+fileExtention;
	  
	    try {
	    	  s3methods.uploadFile(key, createSampleFile(input,fileExtention));
			DbConnect.addMCDesc(MCName, MCBodyparts, MCCategory, MCDescription,MCExtention, key);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    }
	    else 
	    {
	    	
	    	FacesContext.getCurrentInstance().addMessage("uploadForm:file", new FacesMessage("Please upload a video in .avi,.wmv or .mp4 format"));
	    }
	}
	
	
	
	private static File createSampleFile(InputStream input,String extention) throws IOException {
        File temp = File.createTempFile("afsafasa", "."+extention);
        temp.deleteOnExit();
        
        OutputStream output = new FileOutputStream(temp);
        try {
	        IOUtils.copy(input, output);
	        System.out.println(temp);
	      
	        
	    } finally {
	        IOUtils.closeQuietly(input);
	        IOUtils.closeQuietly(output);
	    }
      

        return temp;
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
	 public static boolean allowedExtensions(String ext)
	 {
		 String[] extentions = {"fbx", "bvh","FBX","BVH"};  
		    if (Arrays.asList(extentions).contains(ext))
		    {
		    	return true;
		    } else {
		    	return false;
		    }
		
		 
	 }
	 
	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}


	public List<VideoData> getFilteredData() {
		return filteredData;
	}


	public void setFilteredData(List<VideoData> filteredData) {
		this.filteredData = filteredData;
	}


	public String getSetEditMCName() {
		return setEditMCName;
	}


	public void setSetEditMCName(String SetEditMCName) {
		setEditMCName = SetEditMCName;
	}
}
