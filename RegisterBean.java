package hello;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.util.TextUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.amazonaws.AmazonClientException;
import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

import edu.vt.middleware.password.AlphabeticalSequenceRule;
import edu.vt.middleware.password.CharacterCharacteristicsRule;
import edu.vt.middleware.password.DigitCharacterRule;
import edu.vt.middleware.password.LengthRule;
import edu.vt.middleware.password.LowercaseCharacterRule;
import edu.vt.middleware.password.NonAlphanumericCharacterRule;
import edu.vt.middleware.password.NumericalSequenceRule;
import edu.vt.middleware.password.Password;
import edu.vt.middleware.password.PasswordData;
import edu.vt.middleware.password.PasswordValidator;
import edu.vt.middleware.password.QwertySequenceRule;
import edu.vt.middleware.password.RepeatCharacterRegexRule;
import edu.vt.middleware.password.Rule;
import edu.vt.middleware.password.RuleResult;
import edu.vt.middleware.password.UppercaseCharacterRule;
import edu.vt.middleware.password.WhitespaceRule;

@Bean(name="RegisterBean")
@SessionScoped
public class RegisterBean implements Serializable{

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/**
 * LoginBean.java
 * 
 */private String name;
 	private String password;
 	private String email;
	private boolean registered;
 	private String link="aa";
public boolean validEmail()
{
	 boolean validEmail = EmailValidator.getInstance().isValid(email);
	 if(validEmail) {
		 return true;
	 } else {
		 FacesContext.getCurrentInstance().addMessage("registrationForm:email", new FacesMessage("Invalid Email"));
		 return false;
	 }
}

public void addToRegister() throws Exception
   {
	   try {
	  boolean validEmail = EmailValidator.getInstance().isValid(email);
	  boolean nameAvailable;
	  boolean emailInUse;
	  	if(DbConnect.checkName(name)) {
	  		System.out.println("name available");
	  		nameAvailable = true;
	  	} else {
	  		nameAvailable = false;
	  		FacesContext.getCurrentInstance().addMessage("registrationForm", new FacesMessage("Username already exists. Please choose an alternative one."));
	  	}
	  	
	  	if(DbConnect.checkEmail(email)) {
	  		System.out.println("email not used");
	  		emailInUse = false;
	  	} else {
	  		emailInUse = true;
	  		FacesContext.getCurrentInstance().addMessage("registrationForm", new FacesMessage("Email already in use. If you have forgotten your username or password please contact the administrator."));
	  	}
	  	
		if(nameAvailable && !emailInUse && validEmail && passwordValid()){
				
		byte[] hash = createHash();
		DbConnect.addEntry(name, email, password , false,hash);
		AmazonSES.sendConfirmationEmail(name,email, DbConnect.stringifyHash(hash));
		registered = true;
		FacesContext.getCurrentInstance().addMessage("registrationForm:registrationMessage", new FacesMessage("Successfully registered. An activation link has been sent to your email. You may proceed to the login page and enter your account after activation has been completed."));
		}
		else 
		{	
			registered = false;
			FacesContext.getCurrentInstance().addMessage("registrationForm", new FacesMessage("There has been an error processing your information. Please try again."));
		}
	   }
	   catch (Exception e) {
           throw new AmazonClientException(
                   "Cannot register",
                   e);
       }
	
   }
public byte[] createHash() throws NoSuchAlgorithmException
{
	
	String uuid = UUID.randomUUID().toString();
	String key = name + "." + uuid;
	
	MessageDigest md = MessageDigest.getInstance( "SHA-512" );
    md.reset();
    md.update( key.getBytes(StandardCharsets.UTF_8) );
    byte[] aMessageDigest = md.digest();
		
		return aMessageDigest;
	
}



public boolean passwordValid()
{
	LengthRule lengthRule = new LengthRule(8, 16);

	// don't allow whitespace
	WhitespaceRule whitespaceRule = new WhitespaceRule();

	// control allowed characters
	CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
	// require at least 1 digit in passwords
	charRule.getRules().add(new DigitCharacterRule(1));
	// require at least 1 non-alphanumeric char
	charRule.getRules().add(new NonAlphanumericCharacterRule(1));
	// require at least 1 upper case char
	charRule.getRules().add(new UppercaseCharacterRule(1));
	// require at least 1 lower case char
	charRule.getRules().add(new LowercaseCharacterRule(1));
	// require at least 3 of the previous rules be met
	charRule.setNumberOfCharacteristics(3);

	// don't allow alphabetical sequences
	AlphabeticalSequenceRule alphaSeqRule = new AlphabeticalSequenceRule();

	// don't allow numerical sequences of length 3
	NumericalSequenceRule numSeqRule = new NumericalSequenceRule(3, false);

	// don't allow qwerty sequences
	QwertySequenceRule qwertySeqRule = new QwertySequenceRule();

	// don't allow 4 repeat characters
	RepeatCharacterRegexRule repeatRule = new RepeatCharacterRegexRule(4);

	// group all rules together in a List
	List<Rule> ruleList = new ArrayList<Rule>();
	ruleList.add(lengthRule);
	ruleList.add(whitespaceRule);
	ruleList.add(charRule);
	ruleList.add(alphaSeqRule);
	ruleList.add(numSeqRule);
	ruleList.add(qwertySeqRule);
	ruleList.add(repeatRule);

	PasswordValidator validator = new PasswordValidator(ruleList);
	PasswordData passwordData = new PasswordData(new Password(password));

	RuleResult result = validator.validate(passwordData);
	if (result.isValid()) {
	  System.out.println("Valid password");
	  return true;
	} else {
	  System.out.println("Invalid password:");
	  
	  for (String msg : validator.getMessages(result)) {
		  FacesContext.getCurrentInstance().addMessage("registrationForm:password", new FacesMessage(msg));
	  }
	  return false;
	}
}


   
    public String getName ()
    {
        return name;
    }

    public String getEmail ()
    {
    	return email;
    }
    public void setEmail (final String email)
    {
    	this.email = email;
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
    public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}

