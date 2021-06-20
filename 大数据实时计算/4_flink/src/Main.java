import com.bingocloud.util.json.JSONException;
import com.bingocloud.util.json.JSONObject;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class Main {
    private static String bootstrapServers = "bigdata35.depts.bingosoft.net:29035," +
            "bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";
    private static String topic = "buy_ticket_lxh";

    private static String url = "jdbc:mysql://localhost:3306/lab2559?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    private static String user = "root";
    private static String pw = "db17875612798";
    private static String table = "lxh_buy_ticket";

    private static Connection conn = null;
    private static Statement statement = null;

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setParallelism(1);
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("group.id", UUID.randomUUID().toString());
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FlinkKafkaConsumer010 consumer = new FlinkKafkaConsumer010<String>(topic, new SimpleStringSchema(), properties);
        consumer.setCommitOffsetsOnCheckpoints(true);
        DataStreamSource streamSource = environment.addSource(consumer);
        streamSource.writeUsingOutputFormat(new dbStream());
        environment.execute();
    }

    public static class dbStream implements OutputFormat<String> {

        @Override
        public void configure(Configuration configuration) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, pw);
                statement = conn.createStatement();
                String drop = "DROP TABLE IF EXISTS `" + table + "`;";
                String create =
                        "CREATE TABLE `" + table + "` (" +
                        " `username` varchar(50) DEFAULT NULL COMMENT '姓名'," +
                        " `buy_time` datetime DEFAULT NULL COMMENT '购票时间'," +
                        " `buy_address` varchar(500) DEFAULT NULL COMMENT '购票地址'," +
                        " `origin` varchar(100) DEFAULT NULL COMMENT '出发地'," +
                        " `destination` varchar(100) DEFAULT NULL COMMENT '目的地'" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                statement.executeUpdate(drop);
                statement.executeUpdate(create);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void open(int i, int i1) throws IOException {

        }

        @Override
        public void writeRecord(String s) throws IOException {
            try {
                JSONObject obj = new JSONObject(s);
                String sql = "INSERT INTO `" + table + "` VALUES ('" + obj.get("username") + "', '" + obj.get("buy_time")
                        + "', '" + obj.get("buy_address") + "', '" + obj.get("origin") + "', '" + obj.get("destination") + "');";
                System.out.println(sql);
                statement.executeUpdate(sql);
            } catch (JSONException | SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close() throws IOException {
            try {
                conn.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
