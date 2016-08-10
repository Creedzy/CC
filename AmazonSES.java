package hello;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AmazonSES {

	public static void sendCompletionEmail(String TO, String MCName, String Link) {
		if(TO != ""){
		final String FROM = "administrator@cgrepository.co.uk"; // Replace with
																// your "From"
																// address. This
																// address must
																// be verified.
		// Replace with a "To" address. If your account is still in the
		// sandbox, this address must be verified.
		final String BODY = "This is an automated Email to notify you that the request for a Motion Capture recording "
				+ MCName
				+ " has been completed. You may find it in the database,searching by the name you specified or in your account tab.";
		final String SUBJECT = "Your request has been completed!";

		// Construct an object to contain the recipient address.
		Destination destination = new Destination().withToAddresses(new String[] { TO });

		// Create the subject and body of the message.
		Content subject = new Content().withData(SUBJECT);
		Content textBody = new Content().withData(BODY);
		Body body = new Body().withText(textBody);

		// Create a message with the specified subject and body.
		Message message = new Message().withSubject(subject).withBody(body);

		// Assemble the email.
		SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination)
				.withMessage(message);

		try {
			System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");

			// Instantiate an Amazon SES client, which will make the service
			// call. The service call requires your AWS credentials.
			// Because we're not providing an argument when instantiating the
			// client, the SDK will attempt to find your AWS credentials
			// using the default credential provider chain. The first place the
			// chain looks for the credentials is in environment variables
			// AWS_ACCESS_KEY_ID and AWS_SECRET_KEY.
			// For more information, see
			// http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html
			AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient();

			Region REGION = Region.getRegion(Regions.EU_WEST_1);
			client.setRegion(REGION);

			// Send the email.
			client.sendEmail(request);
			System.out.println("Email sent!");
		} catch (Exception ex) {
			System.out.println("The email was not sent.");
			System.out.println("Error message: " + ex.getMessage());
		}}
		
	}

	public static void sendConfirmationEmail(String name, String TO, String bs) {
		if(TO != ""){
		final String FROM = "administrator@cgrepository.co.uk"; // Replace with
																// your "From"
																// address. This
																// address must
																// be verified.
		// Replace with a "To" address. If your account is still in the
		// sandbox, this address must be verified.
		final String BODY = "Your account has been created. Please activate it by following the link below:\n"
				+ "http://mcdatabase.cgrepository.co.uk/activate.xhtml?key=" + bs + "&&name=" + name;
		final String SUBJECT = "Verification required";
		
		// Construct an object to contain the recipient address.
		Destination destination = new Destination().withToAddresses(new String[] { TO });

		// Create the subject and body of the message.
		Content subject = new Content().withData(SUBJECT);
		Content textBody = new Content().withData(BODY);
		Body body = new Body().withText(textBody);

		// Create a message with the specified subject and body.
		Message message = new Message().withSubject(subject).withBody(body);

		// Assemble the email.
		SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination)
				.withMessage(message);

		try {
			System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");

			// Instantiate an Amazon SES client, which will make the service
			// call. The service call requires your AWS credentials.
			// Because we're not providing an argument when instantiating the
			// client, the SDK will attempt to find your AWS credentials
			// using the default credential provider chain. The first place the
			// chain looks for the credentials is in environment variables
			// AWS_ACCESS_KEY_ID and AWS_SECRET_KEY.
			// For more information, see
			// http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html
			AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient();

			Region REGION = Region.getRegion(Regions.EU_WEST_1);
			client.setRegion(REGION);

			// Send the email.
			client.sendEmail(request);
			System.out.println("Email sent!");
		} catch (Exception ex) {
			System.out.println("The email was not sent.");
			System.out.println("Error message: " + ex.getMessage());
		}
		}
	}

	public static void sendUpdateEmail(String name, String TO, String bs) {
		if(TO != ""){
		final String FROM = "administrator@cgrepository.co.uk"; // Replace with
																// your "From"
																// address. This
																// address must
																// be verified.
		// Replace with a "To" address. If your account is still in the
		// sandbox, this address must be verified.
		final String BODY = "This email is to confirm your new email address. Please activate it by following the link below:\n"
				+ "http://mcdatabase.cgrepository.co.uk/emailchange.xhtml?key=" + bs + "&&name=" + name;
		final String SUBJECT = "Email update request";

		// Construct an object to contain the recipient address.
		Destination destination = new Destination().withToAddresses(new String[] { TO });

		// Create the subject and body of the message.
		Content subject = new Content().withData(SUBJECT);
		Content textBody = new Content().withData(BODY);
		Body body = new Body().withText(textBody);

		// Create a message with the specified subject and body.
		Message message = new Message().withSubject(subject).withBody(body);

		// Assemble the email.
		SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination)
				.withMessage(message);

		try {
			System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");

			// Instantiate an Amazon SES client, which will make the service
			// call. The service call requires your AWS credentials.
			// Because we're not providing an argument when instantiating the
			// client, the SDK will attempt to find your AWS credentials
			// using the default credential provider chain. The first place the
			// chain looks for the credentials is in environment variables
			// AWS_ACCESS_KEY_ID and AWS_SECRET_KEY.
			// For more information, see
			// http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html
			AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient();

			Region REGION = Region.getRegion(Regions.EU_WEST_1);
			client.setRegion(REGION);

			// Send the email.
			client.sendEmail(request);
			System.out.println("Email sent!");
		} catch (Exception ex) {
			System.out.println("The email was not sent.");
			System.out.println("Error message: " + ex.getMessage());
		}
		}
	}

	public static void BounceHandleRequest() {
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
		AmazonSQS sqs = new AmazonSQSClient(credentials);
		Region eu = Region.getRegion(Regions.EU_WEST_1);
		sqs.setRegion(eu);

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon SQS");
		System.out.println("===========================================\n");

		try {
			// Create a queue

			// List queues
			System.out.println("Listing all queues in your account.\n");
			for (String queueUrl : sqs.listQueues().getQueueUrls()) {
				System.out.println("  QueueUrl: " + queueUrl);
			}
			System.out.println();
			GetQueueUrlResult QueueUrl = sqs.getQueueUrl("ses-bounces-queue");

			// Send a message

			System.out.println("Receiving messages from MyQueue.\n");
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(QueueUrl.getQueueUrl());
			List<com.amazonaws.services.sqs.model.Message> messages = sqs.receiveMessage(receiveMessageRequest)
					.getMessages();

			for (com.amazonaws.services.sqs.model.Message message : messages) {

				System.out.println("    Body:          " + message.getBody());
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				AmazonSesBounceNotification obj = mapper.readValue(message.getBody(), AmazonSesBounceNotification.class);
				System.out.println(obj.getMessage().getBounce().getBouncedRecipients().get(0).getEmailAddress());
				System.out.println(obj.getMessage().getBounce().getBounceSubType());
				List<AmazonSesBouncedRecipient> recipient = obj.Message.getBounce().getBouncedRecipients();

				switch (obj.getMessage().getBounce().getBounceType()) {
				case "Transient":
					// Per our sample organizational policy, we will remove all
					// recipients
					// that generate an AttachmentRejected bounce from our
					// mailing list.
					// Other bounces will be reviewed manually.
					switch (obj.getMessage().getBounce().getBounceSubType()) {
					case "AttachmentRejected":
						for (AmazonSesBouncedRecipient rec : recipient) {
							DbConnect.RemoveFromMailingList(rec.getEmailAddress());
						}
						break;
					default:
						System.out.println("manual");
						break;
					}
					break;
				default:
					// Remove all recipients that generated a permanent bounce
					// or an unknown bounce.
					for (AmazonSesBouncedRecipient rec : recipient) {
						System.out.println("Permanent,removing...");
						DbConnect.RemoveFromMailingList(rec.getEmailAddress());

					}
					break;
				}
			}

			System.out.println();

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SQS, such as not "
					+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void ComplaintHandleRequest()
	{
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
	       
		AmazonSQS sqs = new AmazonSQSClient(credentials);
        Region eu = Region.getRegion(Regions.EU_WEST_1);
        sqs.setRegion(eu);
        

        try {
          
            GetQueueUrlResult ComplaintUrl = sqs.getQueueUrl("ses-complaints-queue");
         
         System.out.println("Receiving messages from MyQueue.\n");
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(ComplaintUrl.getQueueUrl());
            List<com.amazonaws.services.sqs.model.Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            
           
          for (com.amazonaws.services.sqs.model.Message message : messages) {
                
                System.out.println("    Body:          " + message.getBody());
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                AmazonSesComplaintNotification obj = mapper.readValue(message.getBody(), AmazonSesComplaintNotification.class);
                System.out.println(obj.getMessage().getNotificationType());
                System.out.println(obj.getMessage().getComplaint().getComplainedRecipients());
                
                List<AmazonSesComplainedRecipient> recipient = obj.getMessage().getComplaint().getComplainedRecipients();
                for (AmazonSesComplainedRecipient rec : recipient)
                {	
                	System.out.println("Email" + rec.getEmailAddress());
                    DbConnect.RemoveFromMailingList(rec.getEmailAddress());
                }
               
	        }
          
            
            System.out.println();

           
            
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
