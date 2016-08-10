package hello;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import com.amazonaws.services.dynamodbv2.document.Table;
@ManagedBean(name = "LoginBean",eager = true)
@SessionScoped
public class LoginBean implements Serializable {

	@PostConstruct
	public void userFromValidate()
	{
		 user = LoginValidate.getUser();
	}
	
	

	private static final long serialVersionUID = 1L;
	
	
	
	private String name;
 	private String password;
 	private User user;
 	private boolean logged = false;
 	
 	
 	
	DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(
		    new ProfileCredentialsProvider()));
  
   	
    public String getName ()
    {
        return name;
    }

  
    public void setName (final String name)
    {
        this.name = name;
    }


    public String getPassword ()
    {
        return password;
    }


    public void setPassword (final String password)
    {
        this.password = password;
    }
    
    public void setlogged(boolean login)
    {
    	this.logged = login;
    }
    public boolean getlogged()
    {
    	return logged;
    }

	public String validateUserPassword() {

		boolean valid = LoginValidate.validate(name, password);
		if (valid) {

			System.out.println(valid);
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
			user = LoginValidate.getUser();
			session.setAttribute("user", user);
			
			System.out.println("User from LoginBean is: " + user);
			System.out.println("Session User from LoginBean is: " + session.getAttribute("user"));
			System.out.println(user.getActivated());
			if(user.getActivated()){
			logged = true;
			return "login";}
			
			else{
				logged = false;
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage( "Account is not activated. Please check your email for a verification link."));
				return null;
			}
			

			
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Wrong Username or Password"));
			System.out.println("User from LoginBean is: " + user);
			return null;
		}
		

	}
    
    public String logout() {
    	FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        session.invalidate();
        
        logged = false;
        user = null;
        System.out.println(logged);
        System.out.println(user);
        return "/login.xhtml?faces-redirect=true";
    }


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}
	
	public boolean isAdmin()
	{
		return user.hasRole("ADMIN");
		
	}
    
    
}
