package connect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.*;
import java.util.Properties;

public class Connect {
    private static String user = null;

    private static Connection connection;
    public Connect(String url, String user, String pw) throws SQLException {
        this.user = user;
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("user", user);
        properties.setProperty("password", pw);
        this.connection = DriverManager.getConnection(url, properties);
    }

    public static JSONArray getJsonArray(String command) throws SQLException {

        Statement statement = connection.createStatement();
        if(!statement.execute(command)) {
            while (!statement.getMoreResults() && statement.getUpdateCount() != -1) { }
        }
        ResultSet resultSet = statement.getResultSet();
        JSONArray res = new JSONArray();
        JSONObject info = new JSONObject();
        info.put("col", resultSet.getMetaData().getColumnCount());
        for (int i = 1; i <= info.getIntValue("col"); i++) {
            String str = resultSet.getMetaData().getColumnName(i);
            info.put(String.valueOf(i), str);
        }
        res.add(info);
        while (resultSet.next()) {
            JSONObject temp = new JSONObject();
            for (int i = 1; i <= info.getIntValue("col"); i++) {
                String str = resultSet.getString(i);
                temp.put(info.getString(String.valueOf(i)), str);
            }
            res.add(temp);
        }
        resultSet.close();
        return res;
    }

    public static String getUser() throws SQLException {
        return user;
    }
}
