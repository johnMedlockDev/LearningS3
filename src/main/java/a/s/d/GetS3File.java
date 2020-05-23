package a.s.d;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class GetS3File {

    public String fileName;
    private String id;
    private String secret;
    private String bucketName;
    private AmazonS3 s3client;

    public void run() {
        readProperties();
        setupCredentials();
        downloadCSV();
    }

    private void readProperties(){
        Properties prop = new Properties();
        InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream("creds.properties");
        try {
            prop.load(input);

        } catch (IOException io) {
            io.printStackTrace();
        }

         id = prop.getProperty("id");
         secret = prop.getProperty("secret");
         bucketName = prop.getProperty("bucketName");
    }

    private void setupCredentials(){
        AWSCredentials credentials = new BasicAWSCredentials(id, secret);

         s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    private void downloadCSV(){
        // Listing Objects
        ObjectListing objectListing = s3client.listObjects(bucketName);
        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
            if (os.getKey().endsWith("csv") && os.getKey().startsWith("part")) {
                fileName = os.getKey();
                System.out.println(os.getKey());
            }
        }
        // Downloading an Object
        com.amazonaws.services.s3.model.S3Object s3object = s3client.getObject(bucketName, fileName);

        S3ObjectInputStream inputStream = s3object.getObjectContent();

        try {
            FileUtils.copyInputStreamToFile(inputStream, new File(
                    fileName));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File already exist");
        }
    }

    public String getFileName() {
        return fileName;
    }

}
