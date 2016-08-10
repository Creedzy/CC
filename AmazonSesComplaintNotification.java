package hello;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.*;
public class AmazonSesComplaintNotification {
       
   
	 
      
          public ComplaintMessage Message;
        
          public ComplaintMessage getMessage() { return Message; }
    
      
         public void setMessage(ComplaintMessage s) { this.Message = s; }
       
        
       
   }
@JsonIgnoreProperties(ignoreUnknown=true)
 class ComplaintMessage
{
	 
	 @JsonCreator
	    public static ComplaintMessage Create(String jsonString) throws JsonParseException, JsonMappingException, IOException {
	        ObjectMapper mapper = new ObjectMapper();
	        ComplaintMessage message = null;
	        message = mapper.readValue(jsonString, ComplaintMessage.class);
	        return message;
	    }
	 
    public String NotificationType;
    public AmazonSesComplaint Complaint;
	public String getNotificationType() {
		return NotificationType;
	}
	public void setNotificationType(String notificationType) {
		NotificationType = notificationType;
	}
	public AmazonSesComplaint getComplaint() {
		return Complaint;
	}
	public void setComplaint(AmazonSesComplaint bounce) {
		Complaint = bounce;
	}
}
/// <summary>Represents meta data for the bounce notification from Amazon SES.</summary>
 @JsonIgnoreProperties(ignoreUnknown=true)
class AmazonSesComplaint
{
	 @JsonCreator
	    public static AmazonSesComplaint Create(String jsonString) throws JsonParseException, JsonMappingException, IOException {
	        ObjectMapper mapper = new ObjectMapper();
	        AmazonSesComplaint amazonComplaint = null;
	        amazonComplaint = mapper.readValue(jsonString, AmazonSesComplaint.class);
	        return amazonComplaint;
	    }
    
   
    public List<AmazonSesComplainedRecipient> ComplainedRecipients;
	
	
	public List<AmazonSesComplainedRecipient> getComplainedRecipients() {
		return ComplainedRecipients;
	}
	public void setComplainedRecipients(List<AmazonSesComplainedRecipient> complaintRecipient) {
		ComplainedRecipients = complaintRecipient;
	}
}
/// <summary>Represents the email address of recipients that bounced
/// when sending from Amazon SES.</summary>
 @JsonIgnoreProperties(ignoreUnknown=true)
 class AmazonSesComplainedRecipient
{
    public String EmailAddress;

	public String getEmailAddress() {
		return EmailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		EmailAddress = emailAddress;
	}
}

/// <summary>Represents an Amazon SES bounce notification.</summary>


