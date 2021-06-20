import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.flink.util.Collector;

import java.util.*;

public class Main {
    private static String bootstrapServers = "bigdata35.depts.bingosoft.net:29035," +
            "bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";
    private static ArrayList<String> topics = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        topics.add("lxh_buy_ticket_2559");
        topics.add("lxh_hotel_stay_2559");
        topics.add("lxh_monitoring_2559");

        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setParallelism(1);
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("group.id", UUID.randomUUID().toString());
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        DataStream<String> inputStream = environment
                .socketTextStream("localhost", 9999);

        FlinkKafkaConsumer010<ObjectNode> comsumer = new FlinkKafkaConsumer010<ObjectNode>(topics,
                new JSONKeyValueDeserializationSchema(true), properties);
        comsumer.setCommitOffsetsOnCheckpoints(true);
        DataStreamSource<ObjectNode> streamSource = environment.addSource(comsumer);

        inputStream.connect(streamSource).process(new CoProcessFunction<String, ObjectNode, Object>() {
            List<String> info = new ArrayList<>();
            List<String> name = new ArrayList<>();

            @Override
            public void processElement1(String s, Context context, Collector<Object> collector) throws Exception {
                if(name.contains('"' + s + '"')) {
                    for(int i = 0; i < name.size(); i++) {
                        if(name.get(i).equals('"' + s + '"'))
                            System.out.println(info.get(i));
                    }
                } else {
                    System.out.println("No results were found");
                }
            }

            @Override
            public void processElement2(ObjectNode jsonNodes, Context context, Collector<Object> collector) throws Exception {
                info.add(jsonNodes.get("value").toString());
                name.add(jsonNodes.get("value").get("username").toString());
            }
        });

        environment.execute();
    }
}

