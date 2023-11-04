import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class ToDoListApp extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/todo";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "shristi@1234#";

    private JTextField taskField;
    private JTextField dueDateField;
    private JButton addButton;
    private JList<String> taskList;
    private JTable taskTable;
    private DefaultTableModel tableModel;

    private Connection connection;

    public ToDoListApp() {
        setTitle("TaskMaster: to-do list");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        taskField = new JTextField(20);
        dueDateField = new JTextField(10);
        addButton = new JButton("Add Task");
        taskList = new JList<>();

        add(new JLabel("Task:"));
        add(taskField);
        add(new JLabel("Due Date:"));
        add(dueDateField);
        add(addButton);

        String[] columnNames = {"Task", "Due Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        taskTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(taskTable);
        add(scrollPane);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("\nConnected to database successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
        }

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String task = taskField.getText();
                String dueDate = dueDateField.getText();

                try {
                    String query = "INSERT INTO tasks (task, due_date) VALUES (?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, task);
                    preparedStatement.setString(2, dueDate);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                refreshTaskList();
            }
        });

        refreshTaskList();

        pack();
        setVisible(true);
    }

    private void refreshTaskList() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT task, due_date FROM tasks";
            ResultSet resultSet = statement.executeQuery(query);

            DefaultListModel<String> model = new DefaultListModel<>();
            tableModel.setRowCount(0);

            while (resultSet.next()) {
                String task = resultSet.getString("task");
                String dueDate = resultSet.getString("due_date");
                model.addElement(task);
                tableModel.addRow(new String[]{task, dueDate});
            }

            taskList.setModel(model);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ToDoListApp();
            }
        });
    }
}
