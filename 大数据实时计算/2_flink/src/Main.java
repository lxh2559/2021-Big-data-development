import com.bingocloud.util.json.JSONObject;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static String bootstrapServers = "bigdata35.depts.bingosoft.net:29035," +
            "bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";
    private static String topic = "mn_buy_ticket_1";

    private static HashMap<String, Integer> map = new HashMap<String, Integer>();

    private static Integer index = 0;

    public static void statistics() {
        Comparator<Map.Entry<String, Integer>> comparator = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        };
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list, comparator);
        for(int i = 0; i < map.size(); i++) {
            if(i > 4)
                break;
            System.out.println("乘客到达次数第" + (i + 1) + "的城市: " + list.get(i).getKey()
                    + " 到达次数: " + list.get(i).getValue());
        }
    }

    public static boolean check(String s) {
        Pattern pattern = Pattern.compile("([\\u4e00-\\u9fa5])");
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("group.id", UUID.randomUUID().toString());
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FlinkKafkaConsumer010 consumer = new FlinkKafkaConsumer010<String>(topic, new SimpleStringSchema(), properties);
        consumer.setCommitOffsetsOnCheckpoints(true);
        DataStreamSource<String> streamSource = environment.addSource(consumer);
        streamSource.map(item -> {
            try {
                JSONObject obj = new JSONObject(item.toString());
                String s = obj.getString("destination");
                Integer i = 0;
                if(check(s)) {
                    if(map.containsKey(s)) {
                        i = map.get(s);
                    }
                    map.put(s, i + 1);
                }
            } catch(Exception e) { }
            if(index++ < 53000) {
                if(index == 53000) {
                    statistics();
                }
            }
            return item;
        });
        environment.execute();
    }
}
