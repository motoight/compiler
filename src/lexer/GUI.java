package lexer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {
    private static final long serialVersionUID=1L;
    private String[] table1_column = {
            "行号", "种别码", "属性值"
    };

    private String[] table2_column = {
            "行号", "错误类型", "字符"
    };
    private JTextArea source_code = new JTextArea();
    private JTable token_table = new JTable(new DefaultTableModel(new Object[][] {},table1_column)) ;
    private JTable error_table = new JTable(new DefaultTableModel(new Object[][] {},table2_column));
    private JButton button1 = new JButton("打开文件");
    private JButton button2 = new JButton("词法分析");
    private JButton button3 = new JButton("清空");





    public GUI(){
    }

    public void gui_initial(){
        // 蛇设置jframe参数
        getContentPane().setForeground(Color.WHITE);
        getContentPane().setFont(new Font("宋体", Font.PLAIN, 25));
        setTitle("词 法 分 析 程 序");    //设置显示窗口标题
        setSize(810,630);    //设置窗口显示尺寸
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //置窗口是否可以关闭
        getContentPane().setLayout(null);

        // 设置textarea， 添加入scrollpane，可以滚动显示
        JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBounds(10,10,500,500);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane1);

        this.source_code.setFont(new Font("宋体",Font.PLAIN,16));
        scrollPane1.setViewportView(this.source_code);
        // 添加显示行数的类
        scrollPane1.setRowHeaderView(new LineNumber());

        // 设置table1  tokentable
        JScrollPane scrollPane2 = new JScrollPane();
        // setbounds 需要自己计算，写GUI之前先画个草图，留出边框
        scrollPane2.setBounds(520,10,270,400);
        scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane2);
        token_table.setForeground(Color.BLACK);
        token_table.setFillsViewportHeight(true);
        token_table.setFont(new Font("黑体", Font.PLAIN, 16));
        token_table.setBackground(new Color(255, 255, 255));
        scrollPane2.setViewportView(token_table);




        // 设置table2
        JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane3.setBounds(520, 420, 270, 170);
        getContentPane().add(scrollPane3);
        error_table.setForeground(Color.RED);
        error_table.setFont(new Font("宋体", Font.PLAIN, 16));
        error_table.setFillsViewportHeight(true);
        error_table.setBackground(Color.WHITE);
        scrollPane3.setViewportView(error_table);

        // 设置button1
        button1.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                String file_name;
                JFileChooser file_open_filechooser = new JFileChooser();
                file_open_filechooser.setCurrentDirectory(new File("."));
                file_open_filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = file_open_filechooser.showOpenDialog(scrollPane1);

                if (result == JFileChooser.APPROVE_OPTION) // 证明有选择
                {
                    file_name = file_open_filechooser.getSelectedFile().getPath();
                    // 读取文件，写到JTextArea里面
                    source_code.setText("");
                    // 多次尝试，发现Jtextarea中需要字符以utf-8形式输入，才能正常显示中文字符
                    String rawinput = util.readfile(file_name).trim();
                    source_code.setText(rawinput);


                }
            }
        });
        button1.setFont(new Font("宋体", Font.PLAIN, 16));
        button1.setBounds(50, 510+20, 100, 60);
        getContentPane().add(button1);


        // 设置button2
        button2.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Scanner scanner = new Scanner();
                scanner.lex(source_code.getText());

                DefaultTableModel model1 = new DefaultTableModel(new Object[][]{},table1_column);
                token_table.setModel(model1);

                DefaultTableModel model2 = new DefaultTableModel(new Object[][]{},table2_column);
                error_table.setModel(model2);

                List<Token> tokens = scanner.getTokens();
                List<Error> errors = scanner.getErrors_info();

                for(Token t: tokens){
                    String[] arr = new String[3];
                    arr[0] = Integer.toString(t.getLine_num());
                    arr[1] = t.getType();
                    arr[2] = t.getValue();
                    model1.addRow(arr);
                }

                for (Error error:errors){
                    String[] arr = new String[3];
                    arr[0] = Integer.toString(error.getLine_num());
                    arr[1] = error.getType();
                    arr[2] = error.getValue();
                    model2.addRow(arr);
                }
                if (token_table.getRowCount() == 0 && error_table.getRowCount() == 0)
                {
                    JOptionPane.showMessageDialog(null, "没有可分析的程序", "Warning", JOptionPane.DEFAULT_OPTION);
                }

            }
        });
        button2.setFont(new Font("宋体", Font.PLAIN, 16));
        button2.setBounds(160, 510+20, 100, 60);
        getContentPane().add(button2);

        // 设置button3

        button3.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                source_code.setText("");
            }
        });
        button3.setFont(new Font("宋体", Font.PLAIN, 16));
        button3.setBounds(160+110, 510+20, 100, 60);
        getContentPane().add(button3);


        setVisible(true);



    }

    public static void main(String[] args) {
        new GUI().gui_initial();
    }

}
