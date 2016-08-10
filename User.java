package hello;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;




@DynamoDBTable(tableName = "usr_reg2")
public class User {
		private List<Role> roles;
		private String name;
		private String email;
		private String password;
		private String role;
		private boolean activated;
		private boolean emails;
		private boolean emailActive;
		private byte[] hashKey;
		
		@DynamoDBHashKey
	 public String getName() { return name; }
	    public void setName(String name) { this.name = name; }
	    
	    @DynamoDBAttribute
	    public String getEmail() { return email;}
	    public void setEmail(String Email) {this.email = Email; }
	    @DynamoDBAttribute
	 public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    @DynamoDBAttribute
    public String getRole() { return role; }
    public void setRole(String role){ this.role = role; }
    
    @DynamoDBAttribute
    public boolean getActivated() { return activated; }
    public void setActivated(boolean Activated) { this.activated = Activated; }
    
    @DynamoDBAttribute
    public boolean getEmails() { return emails; }
    public void setEmails(boolean Emails) { this.emails = Emails; }
    
    @DynamoDBAttribute
    public boolean getEmailActive() { return emailActive; }
    public void setEmailActive(boolean EmailActive) { this.emailActive = EmailActive; }
    
    @DynamoDBAttribute
    public byte[] getHashKey () { return hashKey;}
    public void setHashKey(byte[] HASHKEY) { this.hashKey = HASHKEY; }
    
    
    
		/*Role ADMIN;
		public User(Role Admin,String password,String name)
		{
			this.ADMIN = Admin;
			this.name = name;
			this.password = password;
		}*/
		
		
		public enum Role {

		    USER, ADMIN;

		}

		public boolean hasRole(String string) {
		 return role.contains(string);
			
		}
		
		

		
	}
