package hello;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class s3methods {
static AmazonS3Client s3;
static String bucketName = "elasticbeanstalk-eu-west-1-296450163037";
public static void init(){	AWSCredentials credentials = null;
    try {
       
        credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
    } catch (Exception e) {
        throw new AmazonClientException(
                "Cannot load the credentials from the credential profiles file. " +
                "Please make sure that your credentials file is at the correct " +
                "location (C:\\Users\\nas\\.aws\\credentials), and is in valid format.",
                e);
    }
    s3 = new AmazonS3Client(credentials);
    AmazonS3 s3 = new AmazonS3Client(credentials);
    Region euWest1 = Region.getRegion(Regions.EU_WEST_1);
    s3.setRegion(euWest1);
  
}

public static void uploadFile(String name,File file)
{	
	init();
	AccessControlList acl = new AccessControlList();
	s3.putObject(new PutObjectRequest(bucketName, name, file).withAccessControlList(acl));
	acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
	
}
public static String getURL(String name){
	return s3.getResourceUrl(bucketName, name);
}
public static S3Object downloadFile(String key) throws IOException
{
	AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());        
	S3Object object = s3Client.getObject(
	                  new GetObjectRequest(bucketName, key));
	System.out.println("file from amazon is: "+ object.getKey());
	return object;
}

public String listFiles(List<String> videoList)
{
	System.out.println("Listing objects");
    ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
            .withBucketName(bucketName)
            .withPrefix("User"));
    for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
        System.out.println(" - " + objectSummary.getKey() + "  " +
                           "(size = " + objectSummary.getSize() + ")");
        videoList.add(objectSummary.getKey());
        
    }
    System.out.println();
	return null;
	
}

}