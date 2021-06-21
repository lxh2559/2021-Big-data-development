package s3Stream;

import com.bingocloud.ClientConfiguration;
import com.bingocloud.Protocol;
import com.bingocloud.auth.BasicAWSCredentials;
import com.bingocloud.services.s3.AmazonS3Client;
import com.bingocloud.services.s3.model.S3Object;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.configuration.Configuration;
import org.nlpcn.commons.lang.util.IOUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class S3Stream implements OutputFormat<String> {
    private static String accessKey = "AF09D809E3ECBAC11F8D";
    private static String secretKey = "Wzg2MjU3Q0QxQzIyQTM5MDUxMTk4Njc2NkNDNzU4";
    private static String endPoint = "scuts3.depts.bingosoft.net:29997";
    private static String bucket = "lab2559";
    private static String keyPrefix = "upload/";

    private static String[] keyword = {"吴诗", "陈希", "李静"};
    private static File[] file = new File[keyword.length];
    private static FileWriter[] fileWriter = new FileWriter[keyword.length];
    private static long[] length = new long[keyword.length];

    private static AmazonS3Client s3Client;
    private static Timer timer;

    // 读取S3数据
    public static String readFile(String fileName) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        AmazonS3Client s3Client = new AmazonS3Client(credentials, clientConfig);
        s3Client.setEndpoint(endPoint);
        S3Object s3Object = s3Client.getObject(bucket, fileName);
        return IOUtil.getContent(s3Object.getObjectContent(), "UTF-8");
    }

    // 存入数据到S3
    public void upload() throws IOException {
        synchronized(this) {
            for(int i = 0; i < keyword.length; i++) {
                if(length[i] > 0) {
                    fileWriter[i].close();
                    String targetKey = keyPrefix + keyword[i] + "_" + System.nanoTime() + ".txt";
                    s3Client.putObject(bucket, targetKey, file[i]);
                    System.out.println(targetKey);
                    file[i] = null;
                    fileWriter[i] = null;
                    length[i] = 0;
                }
            }
        }
    }

    @Override
    public void configure(Configuration configuration) {
        timer = new Timer("S3Writer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    upload();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 5000);
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        s3Client = new AmazonS3Client(credentials, clientConfig);
        s3Client.setEndpoint(endPoint);
    }

    @Override
    public void open(int i, int i1) throws IOException {

    }

    // 消息分类读取
    @Override
    public void writeRecord(String s) throws IOException {
        synchronized(this) {
            for(int i = 0; i < keyword.length; i++) {
                if(StringUtils.isNoneBlank(s) && s.contains(keyword[i])) {
                    if(fileWriter[i] == null) {
                        file[i] = new File("result/" + keyword[i]
                                + "_" + System.nanoTime() + ".txt");
                        fileWriter[i] = new FileWriter(file[i], true);
                    }
                    fileWriter[i].append(s + "\n");
                    length[i] += s.length();
                    fileWriter[i].flush();
                }
            }

        }
    }

    @Override
    public void close() throws IOException {
        timer.cancel();
    }
}
