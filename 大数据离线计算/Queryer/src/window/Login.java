package window;

import connect.Connect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Login {
    private static JFrame login = new JFrame("配置连接");

    private final static JLabel host = new JLabel("主机: ");
    private final static JLabel port = new JLabel("端口: ");
    private final static JLabel db = new JLabel("数据库: ");
    private final static JLabel user = new JLabel("用户: ");
    private final static JLabel pw = new JLabel("密码: ");

    private static JTextField jHost = new JTextField("jdbc:hive2://bigdata118.depts.bingosoft.net");
    private static JTextField jPort = new JTextField("22118");
    private static JTextField jDb = new JTextField("user25_db");
    private static JTextField jUser = new JTextField("user25");
    private static JPasswordField jPw = new JPasswordField();

    private static  JButton btn = new JButton("连接");

    private static Connect connect;

    public static void drawLogin(JPanel panel) {
        host.setBounds(30, 20, 70, 30);
        host.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        panel.add(host);

        jHost.setBounds(130, 20, 390, 35);
        jHost.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        panel.add(jHost);

        port.setBounds(30, 70, 70, 30);
        port.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        panel.add(port);

        jPort.setBounds(130, 70, 390, 35);
        jPort.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        panel.add(jPort);

        db.setBounds(30, 120, 90, 30);
        db.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        panel.add(db);

        jDb.setBounds(130, 120, 390, 35);
        jDb.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        panel.add(jDb);

        user.setBounds(30, 170, 70, 30);
        user.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        panel.add(user);

        jUser.setBounds(130, 170, 390, 35);
        jUser.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        panel.add(jUser);

        pw.setBounds(30, 220, 70, 30);
        pw.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        panel.add(pw);

        jPw.setBounds(130, 220, 390, 35);
        jPw.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        panel.add(jPw);

        btn.setBounds(180, 280, 180, 50);
        btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        panel.add(btn);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connect = new Connect(jHost.getText() + ":" + jPort.getText() + "/" + jDb.getText(),
                            jUser.getText(), String.valueOf(jPw.getPassword()));
                    Window window = new Window(connect);
                    login.setVisible(false);
                } catch (SQLException e2) {
                    JTextArea text = new JTextArea(e2.getMessage());
                    text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
                    JScrollPane scroll = new JScrollPane(text) {
                        @Override
                        public Dimension getPreferredSize() {
                            return new Dimension(420, 240);
                        }
                    };
                    JOptionPane.showMessageDialog(null, scroll,
                            "ERROR", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
    }

    public static void configure() {
        login.setSize(560, 390);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setLayout(null);
        login.setLocationRelativeTo(null);
        login.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 550, 350);

        login.add(panel);
        drawLogin(panel);

        login.setVisible(true);
    }

    public static void reshow() {
        login.setVisible(true);
    }
}
