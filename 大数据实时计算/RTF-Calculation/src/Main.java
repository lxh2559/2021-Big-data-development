import kafka.Kafka;
import s3Stream.S3Stream;

public class Main {
    private static S3Stream stream = new S3Stream();
    private static Kafka kafka = new Kafka();

    public static void main(String[] args) throws Exception {

        String content = stream.readFile("test.txt");
        kafka.write(content);
        kafka.upload();
    }
}
