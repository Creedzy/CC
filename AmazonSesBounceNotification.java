package hello;

import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.*;

public class AmazonSesBounceNotification {
       
   
	 
      
          public Message Message;
        
          public Message getMessage() { return Message; }
    
      
         public void setMessage(Message s) { this.Message = s; }
       
        
       
   }
@JsonIgnoreProperties(ignoreUnknown=true)
 class Message
{
	 
	 @JsonCreator
	    public static Message Create(String jsonString) throws JsonParseException, JsonMappingException, IOException {
	        ObjectMapper mapper = new ObjectMapper();
	        Message message = null;
	        message = mapper.readValue(jsonString, Message.class);
	        return message;
	    }
	 
    public String NotificationType;
    public AmazonSesBounce Bounce;
	public String getNotificationType() {
		return NotificationType;
	}
	public void setNotificationType(String notificationType) {
		NotificationType = notificationType;
	}
	public AmazonSesBounce getBounce() {
		return Bounce;
	}
	public void setBounce(AmazonSesBounce bounce) {
		Bounce = bounce;
	}
}
/// <summary>Represents meta data for the bounce notification from Amazon SES.</summary>
 @JsonIgnoreProperties(ignoreUnknown=true)
class AmazonSesBounce
{
	 @JsonCreator
	    public static AmazonSesBounce Create(String jsonString) throws JsonParseException, JsonMappingException, IOException {
	        ObjectMapper mapper = new ObjectMapper();
	        AmazonSesBounce amazonBounce = null;
	        amazonBounce = mapper.readValue(jsonString, AmazonSesBounce.class);
	        return amazonBounce;
	    }
    public String BounceType;
    public String BounceSubType;
   
    public List<AmazonSesBouncedRecipient> BouncedRecipients;
	public String getBounceType() {
		return BounceType;
	}
	public void setBounceType(String bounceType) {
		BounceType = bounceType;
	}
	public String getBounceSubType() {
		return BounceSubType;
	}
	public void setBounceSubType(String bounceSubType) {
		BounceSubType = bounceSubType;
	}
	
	public List<AmazonSesBouncedRecipient> getBouncedRecipients() {
		return BouncedRecipients;
	}
	public void setBouncedRecipients(List<AmazonSesBouncedRecipient> bouncedRecipients) {
		BouncedRecipients = bouncedRecipients;
	}
}
/// <summary>Represents the email address of recipients that bounced
/// when sending from Amazon SES.</summary>
 @JsonIgnoreProperties(ignoreUnknown=true)
 class AmazonSesBouncedRecipient
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

