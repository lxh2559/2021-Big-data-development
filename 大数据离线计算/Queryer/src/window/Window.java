package window;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import connect.Connect;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;

public class Window {
    private static Login login = new Login();
    private static Connect con = null;

    private static JFrame connecting = null;
    private static JProgressBar proBar = null;

    private static JFrame frame = null;
    private static DefaultMutableTreeNode top = null;
    private static JTable table = new JTable() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public static void resizeTable() {
        Enumeration columns = table.getColumnModel().getColumns();
        while(columns.hasMoreElements()) {
            TableColumn column = (TableColumn)columns.nextElement();
            int c = table.getTableHeader().getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int)table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent
                    (table, column.getIdentifier(), false, false, -1, c).getPreferredSize().getWidth();
            for(int r = 0; r < table.getRowCount(); r++) {
                int rowWidth = (int)table.getCellRenderer(r, c).getTableCellRendererComponent
                        (table, table.getValueAt(r, c), false, false, r, c).getPreferredSize().getWidth();
                width = Math.max(width, rowWidth);
            }
            table.getTableHeader().setResizingColumn(column);
            column.setWidth(width + table.getIntercellSpacing().width + 10);
        }
    }

    public static void execSQL(String command) throws SQLException {
        JSONArray res = con.getJsonArray(command);
        JSONObject info = (JSONObject)res.get(0);
        String[] col = new String[info.getIntValue("col")];
        Object[][] data = new Object[res.size() - 1][info.getIntValue("col")];
        for(int i = 1; i <= info.getIntValue("col"); i++) {
            col[i - 1] = info.getString(String.valueOf(i));
        }
        if(res.size() == 1)
            data = new Object[res.size()][info.getIntValue("col")];
        for(int i = 1; i < res.size(); i++) {
            JSONObject temp = (JSONObject)res.get(i);
            for(int j = 1; j <= info.getIntValue("col"); j++) {
                data[i - 1][j - 1] = temp.getString(info.getString(String.valueOf(j)));
            }
        }

        TableModel dataModel = new DefaultTableModel(data, col);
        table.setModel(dataModel);
        resizeTable();
    }

    public static void drawElemArea(JPanel elemArea) throws SQLException {
        elemArea.setLayout(null);
        elemArea.setBounds(0, 0, 300, 855);

        JTree tree = new JTree(top);
        tree.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if(path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
                        if(node.isLeaf()) {
                            try {
                                execSQL("select * from " + node.toString());
                            } catch (SQLException e2) {
                                JTextArea text = new JTextArea(e2.getMessage());
                                text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
                                JScrollPane scroll = new JScrollPane(text) {
                                    @Override
                                    public Dimension getPreferredSize() {
                                        return new Dimension(420, 240);
                                    }
                                };

                                JOptionPane.showMessageDialog(null, scroll, "ERROR", JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        JScrollPane scroll1 = new JScrollPane(tree);
        scroll1.setBounds(15, 15, 285, 830);
        elemArea.add(scroll1);
    }

    public static void drawWorkArea(JPanel workArea) {
        workArea.setLayout(null);
        workArea.setBounds(315, 15, 1080, 445);

        JTextArea input = new JTextArea();
        input.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        input.setLineWrap(true);

        JScrollPane scroll2 = new JScrollPane(input);
        scroll2.setBounds(0, 0, 850, 440);
        workArea.add(scroll2);

        JButton btn1 = new JButton("运行");
        btn1.setBounds(890, 120, 150, 60);
        btn1.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        workArea.add(btn1);

        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    execSQL(input.getText());
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

        JButton btn2 = new JButton("退出");
        btn2.setBounds(890, 270, 150, 60);
        btn2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        workArea.add(btn2);

        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                login.reshow();
            }
        });
    }

    public static void drawResArea(JPanel resArea) {
        resArea.setLayout(null);
        resArea.setBounds(315, 470, 1050, 390);

        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(30);

        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer dtr = new DefaultTableCellRenderer();
        dtr.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, dtr);

        JScrollPane scroll3 = new JScrollPane(table);
        scroll3.setBounds(0, 0, 1050, 375);
        resArea.add(scroll3);
    }

    public static void init() throws SQLException {
        frame = new JFrame(con.getUser());
        frame.setSize(1400, 900);
        frame.setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel elemArea = new JPanel();
        JPanel workArea = new JPanel();
        JPanel resArea = new JPanel();

        frame.add(elemArea);
        frame.add(workArea);
        frame.add(resArea);

        drawElemArea(elemArea);
        drawWorkArea(workArea);
        drawResArea(resArea);

        frame.setVisible(true);
    }

    public static void initProBar() {
        connecting = new JFrame("连接中...");
        connecting.setSize(300, 230);
        connecting.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connecting.setLayout(null);
        connecting.setLocationRelativeTo(null);
        connecting.setResizable(false);

        proBar = new JProgressBar();
        proBar.setBounds(50, 75, 200, 30);
        proBar.setStringPainted(true);
        proBar.setValue(0);
        connecting.add(proBar);

        connecting.setVisible(true);
    }

    public Window(Connect connect) throws SQLException {
        SwingWorker worker = new SwingWorker<Connect, Integer>() {
            @Override
            protected Connect doInBackground() throws Exception {
                top = new DefaultMutableTreeNode("DataBases");
                JSONArray dbs = connect.getJsonArray("show databases");
                for(int i = 1; i < dbs.size(); i++) {
                    JSONObject temp = (JSONObject)dbs.get(i);
                    DefaultMutableTreeNode db = new DefaultMutableTreeNode(temp.getString("databaseName"));
                    JSONArray tables = connect.getJsonArray("show tables in " + temp.getString("databaseName"));
                    for(int j = 1; j < tables.size(); j++) {
                        db.add(new DefaultMutableTreeNode(((JSONObject)tables.get(j)).getString("tableName")));
                    }
                    this.publish(i * 100 / dbs.size());
                    top.add(db);
                }
                return connect;
            }

            @Override
            protected void process(List<Integer> chunks) {
                for(Integer pro : chunks) {
                    if(connecting == null) {
                        initProBar();
                    }
                    proBar.setValue(pro);
                }
            }

            @Override
            protected void done() {
                try {
                    connecting.setVisible(false);
                    connecting = null;
                    init();
                    con = get();
                } catch (Exception e) {
                    JTextArea text = new JTextArea(e.getMessage());
                    text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
                    JScrollPane scroll = new JScrollPane(text) {
                        @Override
                        public Dimension getPreferredSize() {
                            return new Dimension(420, 240);
                        }
                    };
                    JOptionPane.showMessageDialog(null, scroll, "ERROR", JOptionPane.PLAIN_MESSAGE);
                }
            }
        };

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                worker.execute();
            }
        });

    }
}
