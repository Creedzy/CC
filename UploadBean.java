package hello;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import javax.servlet.ServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;


@ManagedBean(name = "UploadBean",eager = true)
@RequestScoped
public class UploadBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	@ManagedProperty(value = "#{LoginBean}")
	private LoginBean loginBean;
	private UploadedFile uploadedFile;
	private String name;
	private String description;
	private String FileLink="file link";
	private String Completed = "no";
	private String MCLink = "the link";
	private String key;
	
	public void save() throws IOException  {
	     
		String fileExtention = getExtension(uploadedFile.getFileName());
		System.out.println(fileExtention);
	    InputStream input = uploadedFile.getInputstream();
	    
	    if(allowedExtensions(fileExtention)) {
	    	key ="Users/"+loginBean.getName()+"/"+"Videos/"+ name+ "."+fileExtention;
	    s3methods.uploadFile(key, createSampleFile(input,fileExtention));
	    try {
			DbConnect.addVideoDesc(loginBean.getName(), description, name, s3methods.getURL(key), Completed, MCLink);
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
	
	public void addMessage() {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("File uploaded!"));
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
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
		 String[] extentions = {"avi", "mp4", "wmv"};  
		    if (Arrays.asList(extentions).contains(ext))
		    {
		    	return true;
		    } else {
		    	return false;
		    }
		
		 
	 }
	 
	 private Integer progress;
	 
	    public Integer getProgress() {
	        if(progress == null) {
	            progress = 0;
	        }
	        else {
	            progress = progress + (int)(Math.random() * 35);
	             
	            if(progress > 100)
	                progress = 100;
	        }
	         
	        return progress;
	    }
	 
	    public void setProgress(Integer progress) {
	        this.progress = progress;
	    }
	     
	    public void onComplete() {
	        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Progress Completed"));
	    }
	     
	    public void cancel() {
	        progress = null;
	    }
	/*
	 * Getters and Setters
	 */
	 
	 	public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
		public LoginBean getloginBean()
		{
			return loginBean;
		}
		 public void setloginBean(LoginBean loginBean) {
		        this.loginBean = loginBean;
		}
		
		public void setName(String nm) {
			this.name = nm;
		}
		public String getName(){
			return name;
		}
		public UploadedFile getUploadedFile() {
			return uploadedFile;
		}
	
		public String getFileLink() {
			return FileLink;
		}


		public void setFileLink(String fileLink) {
			FileLink = fileLink;
		}


		public String getCompleted() {
			return Completed;
		}


		public void setCompleted(String completed) {
			Completed = completed;
		}


		public String getMCLink() {
			return MCLink;
		}


		public void setMCLink(String mCLink) {
			MCLink = mCLink;
		}
}
