package globalVal;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

// 配置变量
public class GlobalVal {
	private final static String accessKey = "AF09D809E3ECBAC11F8D";
	private final static String secretKey = "Wzg2MjU3Q0QxQzIyQTM5MDUxMTk4Njc2NkNDNzU4";
	private final static String bucketName = "lab2559";
	private final static String serviceEndpoint = "http://10.16.0.1:81";
	private final static String signingRegion = "";
	private final static String filePath = "C:/Users/29440/Desktop/大数据实训/lab";
	private final static long partSize = 5 << 20;
	
	private final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	private final ClientConfiguration ccfg = new ClientConfiguration().withUseExpectContinue(false);
	private final EndpointConfiguration ecfg = new EndpointConfiguration(serviceEndpoint, signingRegion);
	
	private final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withClientConfiguration(ccfg)
			.withEndpointConfiguration(ecfg)
			.withPathStyleAccessEnabled(true)
			.build();
	
	public String getBucket() {
		return bucketName;
	}
	
	public String getPath() {
		return filePath;
	}
	
	public long getPartSize() {
		return partSize;
	}
	
	public AmazonS3 getS3() {
		return s3;
	}
}
