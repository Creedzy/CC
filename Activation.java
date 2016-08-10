package hello;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

@ManagedBean(name = "Activation")
@RequestScoped
public class Activation {

    /**
	 * 
	 */
	
	@ManagedProperty(value="#{param.key}")
    private String key;
    @ManagedProperty(value="#{param.name}")
    private String name;
    private boolean valid;
    
    @PostConstruct
    public void init() {
    	
    	key = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("key");
    	name = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("name");
    
    	System.out.println("PostConstruct");
    	 System.out.println(valid);
        try {
			valid = check(key,name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // And auto-login if valid?
      
        
    }
    
    public void bogus(){
    	System.out.println("Bogus invoked");
    }
    public boolean check(String key,String name) throws Exception{
    	User user;
    	System.out.println(key);
    	System.out.println(name);
    	
			 user = DbConnect.getUser(name);
		
    	if(DbConnect.stringifyHash(user.getHashKey()).equals(key)) {
    		DbConnect.updateUserData(name);
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Account has been activated. You may now login"));
			return true;
		}
		else {
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Activation failed. Contact administrator to resolve this issue."));
			return false;
		}
    	
    }

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}