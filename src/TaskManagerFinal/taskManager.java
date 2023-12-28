package TaskManagerFinal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sqlite.JDBC;

// TODO: Make sure there is no redundant code in DB methods, Put resources and DB into "Resources" folder, Make tasks editable by using selectionListners and making functions for Task object that change its properties then refreshTable(), Colour code for importance score, Removing automatic focus, Overdue, Add My Work Planner Title in Box


public class taskManager extends JFrame {

    private JTextField titleField, subjectField;
    private JDateChooser dueDateChooser;
    private JComboBox<String> importanceField;
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> sortingComboBox;
    private JLabel titleLabel, subjectLabel, dateLabel, importanceLabel;

    Color GREY = new Color(150, 150, 150);
    Color BLACK = new Color(50, 50, 50);

    private List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        taskManager taskManager = new taskManager();
        taskManager.setTitle("My Work Planner");
        ImageIcon img = new ImageIcon("myWorkPlanner.png");
        taskManager.setIconImage(img.getImage());
        taskManager.setSize(600, 800);
        taskManager.setVisible(true);
    }

    public taskManager() {
        try {
            // Register the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // DB Connection:
            Connection connection = null;
            try {
                // Register the SQLite JDBC driver
                Class.forName("org.sqlite.JDBC");

                // Connect to the SQLite database
                String url = "jdbc:sqlite:./tasks.db";
                connection = DriverManager.getConnection(url);
                System.out.println("Connection to SQLite database established!");

                // Close the connection (may want to close it at a later point)
                // connection.close();
            }
            catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to connect to SQLite database: " + e.getMessage());
            }
            finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                    
                }
                																																																																		catch (SQLException e) {
                    System.out.println("Failed to close SQLite database connection: " + e.getMessage());
                }
            }

            // UI manager for aesthetics:
        	try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        	
        	
        	setLocation(new Point(0, 0)); // Spawn in top left of screen
        	
        	UIManager.put("control", new Color(255, 255, 153));
            Font myFont = new Font("Arial", Font.BOLD, 15);
            Font myHeaderFont = new Font("Arial", Font.BOLD, 30);

            JPanel panel = new JPanel(new BorderLayout());

            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setBorder(new EmptyBorder(10, 10, 0, 20));

            titleField = new JTextField("Title and/or description of task");
            subjectField = new JTextField("Can be academic subject or miscellaneous");
            dueDateChooser = new JDateChooser();

            String[] importanceList = { "1-5", "1", "2", "3", "4", "5" };
            importanceField = new JComboBox<String>(importanceList);

            titleField.setForeground(GREY);
            subjectField.setForeground(GREY);
            importanceField.setForeground(GREY);

            titleField.setFont(myFont);
            subjectField.setFont(myFont);
            dueDateChooser.setFont(myFont);
            importanceField.setFont(myFont);

            titleLabel = new JLabel("Title:");
            subjectLabel = new JLabel("Subject:");
            dateLabel = new JLabel("Due Date:");
            importanceLabel = new JLabel("Importance:");

            titleLabel.setFont(myFont);
            subjectLabel.setFont(myFont);
            dateLabel.setFont(myFont);
            importanceLabel.setFont(myFont);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridx = 0;
            gbc.gridy = 0;
            inputPanel.add(titleLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            inputPanel.add(titleField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            inputPanel.add(subjectLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            inputPanel.add(subjectField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            inputPanel.add(dateLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            inputPanel.add(dueDateChooser, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            inputPanel.add(importanceLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            inputPanel.add(importanceField, gbc);
            
  
//           	JLabel dummyLabel = new JLabel();
//           	inputPanel.add(dummyLabel);
//           	dummyLabel.requestFocus();
//            

            panel.add(inputPanel, BorderLayout.NORTH);

            tableModel = new DefaultTableModel(new String[] { "", "Title", "Subject", "Due Date", "Importance" }, 0) {
                Class[] types = new Class[] { Boolean.class, String.class, String.class, String.class, Integer.class };
                Boolean[] canEdit = { true, true, true, true, true };

                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
            };

            table = new JTable(tableModel);
            table.setFont(new Font("Arial", Font.BOLD, 17));
            table.setRowHeight(30);
            table.getColumnModel().getColumn(0).setMaxWidth(5);
            table.getColumnModel().getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));
            table.getColumnModel().getColumn(3).setPreferredWidth(5);
            table.getColumnModel().getColumn(4).setMaxWidth(100);
            JScrollPane tableScrollPane = new JScrollPane(table);
            panel.add(tableScrollPane, BorderLayout.CENTER);

            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

            sortingComboBox = new JComboBox<>(new String[] { "Sort by Title", "Sort by Subject", "Sort by Due Date", "Sort by Importance", "Sort by Unchecked", "Sort by Checked"});

            sortingComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sortTasks();
                }
            });

            JButton addButton = new JButton("Add Task");
            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addTask();
                }
            });

            JButton deleteButton = new JButton("Delete Task");
            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteSelectedTask();
                }
            });
            
            JButton exitButton = new JButton("Save & Exit");
            exitButton.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		saveTasksToDatabase();
            		dispose();
            	}
            });

            controlPanel.add(sortingComboBox);
            controlPanel.add(addButton);
            controlPanel.add(deleteButton);
            controlPanel.add(exitButton);

            panel.add(controlPanel, BorderLayout.SOUTH);

            getContentPane().add(panel);

            pack();
            
            // Adds all db tasks to the list
            loadTasksFromDatabase();
            
            // Sync db with task list
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    // Save tasks to the database before closing
                	int saveConfirm = JOptionPane.showConfirmDialog(null, 
                	        "Would you like to save your changes?", "Unsaved Changes!",
                	        JOptionPane.YES_NO_CANCEL_OPTION);

                	    if (saveConfirm == JOptionPane.YES_OPTION) {
                	    	saveTasksToDatabase();
                	    	dispose();
                	    }
                	    
                	    else if (saveConfirm == JOptionPane.NO_OPTION) {
                	    	dispose();
                	    }
                	    
                	    else {
                	    	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                	    }
                }
            });

            // selection listener that maintains checkmark status when list is modified
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        int selectedRow = table.getSelectedRow();
                        if (selectedRow >= 0) {
                            boolean checked = (boolean) table.getValueAt(selectedRow, 0);
                            String title = (String) table.getValueAt(selectedRow, 1);
                            String subject = (String) table.getValueAt(selectedRow, 2);
                            String dueDate = (String) table.getValueAt(selectedRow, 3);
                            int importance = (int) table.getValueAt(selectedRow,  4);
                            Task taskEditing = tasks.get(selectedRow);
                            taskEditing.setChecked(checked);
//                            taskEditing.setTitle(title); // These features make the tasks unselectable because refreshTable() deselects tasks
//                            taskEditing.setSubject(subject);
//                            taskEditing.setDueDate(dueDate);
//                            taskEditing.setImportance(importance);
//                            
                        }
                    }
                }
            });
            
            

            // Focus listeners for each text field
            titleField.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    if (titleField.getForeground() == GREY)
                        titleField.setText("");
                    titleField.setForeground(BLACK);
                }

                public void focusLost(FocusEvent e) {
                    if (titleField.getText().length() == 0) {
                        titleField.setText("Title and/or description of task");
                        titleField.setForeground(GREY);
                    }
                }
            });

            subjectField.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    if (subjectField.getForeground() == GREY)
                        subjectField.setText("");
                    subjectField.setForeground(BLACK);
                }

                public void focusLost(FocusEvent e) {
                    if (subjectField.getText().length() == 0) {
                        subjectField.setText("Can be academic subject or miscellaneous");
                        subjectField.setForeground(GREY);
                    }
                }
            });

            importanceField.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    if (importanceField.getForeground() == GREY)
                        importanceField.setSelectedIndex(0);
                    importanceField.setForeground(BLACK);
                }

                public void focusLost(FocusEvent e) {
                    if (importanceField.getSelectedItem() == "1-5") {
                        importanceField.setForeground(GREY);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(date);
    }

    private void addTask() {
        String title;
        String subject;
        String dueDate;
        int importance;

        String expectedDatePattern = "\\d{2}/\\d{2}/\\d{4}";

        if (titleField.getForeground() == BLACK && subjectField.getForeground() == BLACK
                && importanceField.getForeground() == BLACK) {
            title = titleField.getText();
            subject = subjectField.getText();
            dueDate = formatDate(dueDateChooser.getDate());
            try {
                importance = Integer.parseInt(importanceField.getSelectedItem().toString());
                if (importance < 1 || importance > 5) {
                    throw new NumberFormatException();
                }
                if (!dueDate.matches(expectedDatePattern)) {
                    throw new IllegalArgumentException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showOptionDialog(null, "Please enter a valid importance score between 1 and 5",
                        "Message Box", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null,
                        null);
                return;
            } catch (IllegalArgumentException ex) {
                JOptionPane.showOptionDialog(null, "Please enter a valid due date in the format MM/DD/YYYY", "Message Box",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                return;
            }

        } else {
            JOptionPane.showOptionDialog(null, "Please input valid information", "Message Box", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, null, null);
            return;
        }

        Task task = new Task(title, subject, dueDate, importance);
        tasks.add(task);

        refreshTable();

        clearInputFields();
    }

    private void clearInputFields() {
        titleField.setText("Add title and/or description of the task");
        subjectField.setText("Subject can be academic or miscellaneous");
        dueDateChooser.setDate(null);
        importanceField.setSelectedItem("1-5");
        titleField.setForeground(GREY);
        subjectField.setForeground(GREY);
        importanceField.setForeground(GREY);
    }

    private void deleteSelectedTask() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            tasks.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        }
    }

    private void sortTasks() {
        int selectedIndex = sortingComboBox.getSelectedIndex();
        Comparator<Task> comparator = null;

        switch (selectedIndex) {
            case 0: // Sort by Title
                comparator = (t1, t2) -> // Special comparator function defined to ignore capitalisation
                {
                    String title1 = t1.getTitle().toLowerCase();
                    String title2 = t2.getTitle().toLowerCase();
                    return title1.compareTo(title2);
                };
                break;
            case 1: // Sort by Subject
            	comparator = (t1, t2) -> 
                {
                    String subject1 = t1.getSubject().toLowerCase();
                    String subject2 = t2.getSubject().toLowerCase();
                    return subject1.compareTo(subject2);
                };
                break;
            case 2: // Sort by Due Date
                comparator = (t1, t2) -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    try {
                        Date date1 = dateFormat.parse(t1.getDueDate());
                        Date date2 = dateFormat.parse(t2.getDueDate());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        return 0;
                    }
                };
                break;
            case 3: // Sort by Importance (higher importance scores first)
                comparator = (t1, t2) -> Integer.compare(t2.getImportance(), t1.getImportance());
                break;
            case 4: // Sort by Unchecked
            	comparator = (t1, t2) -> Boolean.compare(t1.isChecked(), t2.isChecked());
                break;
            case 5: // Sort by Checked
            	comparator = (t1, t2) -> Boolean.compare(t2.isChecked(), t1.isChecked());
                break;
           
        }

        if (comparator != null) {
            Collections.sort(tasks, comparator);
            refreshTable();
        }
    }

    private void refreshTable() { // This method loops through the tasks ArrayList and renders all tasks to the tableModel
        tableModel.setRowCount(0); // Wipe current table
        for (Task task : tasks) { // Go through the tasks ArrayList with all the Task objects
            Object[] rowData = { task.isChecked(), task.getTitle(), task.getSubject(), task.getDueDate(),
                    task.getImportance() }; // Grab properties of each object and turn it into a dataRow object which can be added to a tableModel
            tableModel.addRow(rowData);
        }
        
    }
    
 // Method to load tasks from the database
    private void loadTasksFromDatabase() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Connect to the SQLite database
            String url = "jdbc:sqlite:./tasks.db";
            connection = DriverManager.getConnection(url);
            
            // Create table if it doesn't already exist
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "tasksTable", null);

            if (!tables.next()) {
                // Table doesn't exist, create it
                String createTableQuery = "CREATE TABLE tasksTable ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "checked BOOLEAN,"
                        + "title TEXT,"
                        + "subject TEXT,"
                        + "dueDate TEXT,"
                        + "importance INTEGER,"
                        + "sortingMethod INTEGER"
                        + ")";
                
                statement = connection.prepareStatement(createTableQuery);
                statement.executeUpdate();
            }


            // Query to select all tasks from the database
            String query = "SELECT checked, title, subject, dueDate, importance, sortingMethod FROM tasksTable";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Retrieve data from the result set
            	boolean checked = resultSet.getBoolean("checked");
                String title = resultSet.getString("title");
                String subject = resultSet.getString("subject");
                String dueDate = resultSet.getString("dueDate");
                int importance = resultSet.getInt("importance");
                int sortingMethod = resultSet.getInt("sortingMethod");

                // Create Task object
                Task task = new Task(title, subject, dueDate, importance);
                task.setChecked(checked);

                // Add the task to the list
                tasks.add(task);
                // Set sorting method
                sortingComboBox.setSelectedIndex(sortingMethod);
                
                refreshTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load tasks from the database: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Failed to close database resources: " + e.getMessage());
            }
        }
        
    }
    
    private void saveTasksToDatabase() {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Connect to the SQLite database
            String url = "jdbc:sqlite:./tasks.db";
            connection = DriverManager.getConnection(url);

            // Delete all existing tasks in the database
            String deleteQuery = "DELETE FROM tasksTable";
            statement = connection.prepareStatement(deleteQuery);
            statement.executeUpdate();

            // Insert the current tasks into the database
            String insertQuery = "INSERT INTO tasksTable (checked, title, subject, dueDate, importance, sortingMethod) VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insertQuery);

            for (Task task : tasks) {
            	statement.setBoolean(1, task.isChecked());
                statement.setString(2, task.getTitle());
                statement.setString(3, task.getSubject());
                statement.setString(4, task.getDueDate());
                statement.setInt(5, task.getImportance());
                statement.setInt(6, sortingComboBox.getSelectedIndex());
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Failed to save tasks to the database: " + ex.getMessage());
        } finally {
            // Close resources
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                System.out.println("Failed to close database resources: " + ex.getMessage());
            }
        }
    }

    class Task {
        private String title;
        private String subject;
        private String dueDate;
        private int importance;
        private boolean checked;

        public Task(String title, String subject, String dueDate, int importance) {
            this.title = title;
            this.subject = subject;
            this.dueDate = dueDate;
            this.importance = importance;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
            refreshTable();
        }

        public String getSubject() {
            return subject;
        }
        
        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getDueDate() {
            return dueDate;
        }
        
        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public int getImportance() {
            return importance;
        }
        
        public void setImportance(int importance) {
            this.importance = importance;
        }
        
    }
    
    
    
}
