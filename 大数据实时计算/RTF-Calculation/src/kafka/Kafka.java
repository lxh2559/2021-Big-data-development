package kafka;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import s3Stream.S3Stream;

import java.util.Properties;
import java.util.UUID;

public class Kafka {
    private static String bootstrapServers = "bigdata35.depts.bingosoft.net:29035," +
            "bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";
    private static String topic = "buy_ticket_2559";

    // 存入数据到kafka
    public static void write(String content) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("acks", "all");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer producer = new KafkaProducer<String, String>(properties);
        String[] dataSet = content.split("\n");
        for(String s : dataSet) {
            if(!s.trim().isEmpty()) {
                ProducerRecord record = new ProducerRecord<String, String>(topic, null, s);
                producer.send(record);
            }
        }
        producer.flush();
        producer.close();
    }

    // 存入数据到S3
    public static void upload() throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setParallelism(1);
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("group.id", UUID.randomUUID().toString());
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FlinkKafkaConsumer010<String> consumer =
                new FlinkKafkaConsumer010<String>(topic, new SimpleStringSchema(), properties);
        consumer.setCommitOffsetsOnCheckpoints(true);
        DataStreamSource<String> streamSource = environment.addSource(consumer);
        streamSource.writeUsingOutputFormat(new S3Stream());
        environment.execute();
    }

}
