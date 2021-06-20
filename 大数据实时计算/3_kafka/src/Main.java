import com.bingocloud.util.json.JSONArray;
import com.bingocloud.util.json.JSONException;
import com.bingocloud.util.json.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.sql.*;
import java.util.Properties;

public class Main {
    private static String url = "jdbc:mysql://localhost:3306/lab2559?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    private static String user = "root";
    private static String pw = "db17875612798";

    private static String bootstrapServers = "bigdata35.depts.bingosoft.net:29035," +
            "bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";
    private static String topic = "lxh_buy_ticket_2559";

    public static JSONArray read() throws ClassNotFoundException, SQLException, JSONException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url, user, pw);
        PreparedStatement ps = conn.prepareStatement("select * from mn_buy_ticket");
        ResultSet rs = ps.executeQuery();

        JSONArray res = new JSONArray();
        JSONObject info = new JSONObject();
        info.put("col", rs.getMetaData().getColumnCount());
        for (int i = 1; i <= info.getInt("col"); i++) {
            String str = rs.getMetaData().getColumnName(i);
            info.put(String.valueOf(i), str);
        }
        res.put(info);
        while (rs.next()) {
            JSONObject temp = new JSONObject();
            for (int i = 1; i <= info.getInt("col"); i++) {
                String str = rs.getString(i);
                temp.put(info.getString(String.valueOf(i)), str);
            }
            res.put(temp);
        }

        rs.close();
        ps.close();
        conn.close();
        return res;
    }

    public static void write(String[] dataSet) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("acks", "all");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer producer = new KafkaProducer<String, String>(properties);
        for(String s : dataSet) {
            if(!s.trim().isEmpty()) {
                ProducerRecord record = new ProducerRecord<String, String>(topic, null, s);
                producer.send(record);
            }
        }
        producer.flush();
        producer.close();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, JSONException {
        JSONArray res = read();
        String[] dataSet = new String[res.length() - 1];
        for(int i = 1; i < res.length(); i++) {
             dataSet[i - 1] = res.get(i).toString();
            System.out.println(dataSet[i - 1]);
        }
        write(dataSet);
    }
}
