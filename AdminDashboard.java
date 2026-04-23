import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDashboard extends JFrame {
    private JPanel panel;
    private JTabbedPane tabbedPane;
    private JTable customersTable;
    private JButton refreshCustomersButton;
    private JButton deleteCustomerButton;
    private JButton editCustomerButton;
    private JTable employeesTable;
    private JButton refreshEmployeesButton;
    private JButton deleteEmployeeButton;
    private JButton editEmployeeButton;
    private JButton createEmployeeButton;
    private JTable ordersTable;
    private JButton refreshOrdersButton;
    private JButton updateOrderStatusButton;
    private JButton deleteOrderButton;
    private JButton homeButton;
    private JLabel welcomeLabel;

    private int adminId;
    private String adminName;
    private JTabbedPane tabbedPane1;
    private JButton viewOrderItemsButton;

    public AdminDashboard(int adminId, String adminName) {
        this.adminId = adminId;
        this.adminName = adminName;

        this.setContentPane(this.panel);
        this.setTitle("Admin Dashboard - " + adminName);
        this.setBounds(200, 50, 1100, 750);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        welcomeLabel.setText("Welcome, Admin " + adminName + "!");

        setupRefreshCustomersButton();
        setupDeleteCustomerButton();
        setupEditCustomerButton();
        setupRefreshEmployeesButton();
        setupDeleteEmployeeButton();
        setupEditEmployeeButton();
        setupCreateEmployeeButton();
        setupRefreshOrdersButton();
        setupUpdateOrderStatusButton();
        setupDeleteOrderButton();
        setupViewOrderItemsButton();
        setupHomeButton();

        showAllCustomers();
        showAllEmployees();
        showAllOrders();
    }

    public void setupHomeButton() {
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goHome();
            }
        });
    }

    public void goHome() {
        this.dispose();
        new MainMenu();
    }

    public void showAllCustomers() {
        try {
            String query = "SELECT user_id, username, full_name, address FROM users WHERE user_type = 'customer' ORDER BY full_name";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            ResultSet result = stm.executeQuery();
            customersTable.setModel(DbUtils.resultSetToTableModel(result));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void showAllEmployees() {
        try {
            String query = "SELECT user_id, username, full_name FROM users WHERE user_type = 'employee' ORDER BY full_name";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            ResultSet result = stm.executeQuery();
            employeesTable.setModel(DbUtils.resultSetToTableModel(result));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void showAllOrders() {
        try {
            String query = "SELECT o.order_id, u.full_name, o.order_date, o.total_amount, o.status " +
                    "FROM orders o JOIN users u ON o.customer_id = u.user_id " +
                    "ORDER BY o.order_date DESC";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            ResultSet result = stm.executeQuery();
            ordersTable.setModel(DbUtils.resultSetToTableModel(result));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setupRefreshCustomersButton() {
        refreshCustomersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllCustomers();
                JOptionPane.showMessageDialog(null, "Customers refreshed!");
            }
        });
    }

    public void setupDeleteCustomerButton() {
        deleteCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = customersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int userId = Integer.parseInt(customersTable.getValueAt(selectedRow, 0).toString());
                    String userName = customersTable.getValueAt(selectedRow, 2).toString();

                    int confirm = JOptionPane.showConfirmDialog(null, "Delete customer '" + userName + "'?\nThis will delete all their orders and data.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            String query = "DELETE FROM users WHERE user_id = ? AND user_type = 'customer'";
                            PreparedStatement stm = Database.connection.prepareStatement(query);
                            stm.setInt(1, userId);
                            stm.executeUpdate();

                            JOptionPane.showMessageDialog(null, "Customer deleted!");
                            showAllCustomers();
                            showAllOrders();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a customer first!");
                }
            }
        });
    }
    public void setupViewOrderItemsButton() {
        viewOrderItemsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewOrderItems();
            }
        });
    }

    public void setupEditCustomerButton() {
        editCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = customersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int userId = Integer.parseInt(customersTable.getValueAt(selectedRow, 0).toString());
                    editUser(userId, "customer");
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a customer first!");
                }
            }
        });
    }

    public void setupRefreshEmployeesButton() {
        refreshEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllEmployees();
                JOptionPane.showMessageDialog(null, "Employees refreshed!");
            }
        });
    }

    public void setupDeleteEmployeeButton() {
        deleteEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = employeesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int userId = Integer.parseInt(employeesTable.getValueAt(selectedRow, 0).toString());
                    String userName = employeesTable.getValueAt(selectedRow, 2).toString();

                    int confirm = JOptionPane.showConfirmDialog(null, "Delete employee '" + userName + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            String query = "DELETE FROM users WHERE user_id = ? AND user_type = 'employee'";
                            PreparedStatement stm = Database.connection.prepareStatement(query);
                            stm.setInt(1, userId);
                            stm.executeUpdate();

                            JOptionPane.showMessageDialog(null, "Employee deleted!");
                            showAllEmployees();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an employee first!");
                }
            }
        });
    }

    public void setupEditEmployeeButton() {
        editEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = employeesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int userId = Integer.parseInt(employeesTable.getValueAt(selectedRow, 0).toString());
                    editUser(userId, "employee");
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an employee first!");
                }
            }
        });
    }

    public void editUser(int userId, String userType) {
        try {
            String selectQuery = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement selectStm = Database.connection.prepareStatement(selectQuery);
            selectStm.setInt(1, userId);
            ResultSet rs = selectStm.executeQuery();

            if (rs.next()) {
                JTextField usernameField = new JTextField(rs.getString("username"));
                JTextField fullNameField = new JTextField(rs.getString("full_name"));
                JTextField addressField = new JTextField(rs.getString("address"));
                JPasswordField passwordField = new JPasswordField();

                Object[] message;
                if (userType.equals("customer")) {
                    message = new Object[]{
                            "Username:", usernameField,
                            "Full Name:", fullNameField,
                            "Address:", addressField,
                            "New Password (optional):", passwordField
                    };
                } else {
                    message = new Object[]{
                            "Username:", usernameField,
                            "Full Name:", fullNameField,
                            "New Password (optional):", passwordField
                    };
                }

                int option = JOptionPane.showConfirmDialog(null, message, "Edit " + userType, JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String username = usernameField.getText();
                    String fullName = fullNameField.getText();
                    String newPassword = new String(passwordField.getPassword());

                    if (username.isEmpty() || fullName.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Username and Full Name are required!");
                        return;
                    }

                    String updateQuery;
                    PreparedStatement updateStm;

                    if (!newPassword.isEmpty()) {
                        if (userType.equals("customer")) {
                            updateQuery = "UPDATE users SET username = ?, full_name = ?, address = ?, password = ? WHERE user_id = ?";
                            updateStm = Database.connection.prepareStatement(updateQuery);
                            updateStm.setString(1, username);
                            updateStm.setString(2, fullName);
                            updateStm.setString(3, addressField.getText());
                            updateStm.setString(4, newPassword);
                            updateStm.setInt(5, userId);
                        } else {
                            updateQuery = "UPDATE users SET username = ?, full_name = ?, password = ? WHERE user_id = ?";
                            updateStm = Database.connection.prepareStatement(updateQuery);
                            updateStm.setString(1, username);
                            updateStm.setString(2, fullName);
                            updateStm.setString(3, newPassword);
                            updateStm.setInt(4, userId);
                        }
                    } else {
                        if (userType.equals("customer")) {
                            updateQuery = "UPDATE users SET username = ?, full_name = ?, address = ? WHERE user_id = ?";
                            updateStm = Database.connection.prepareStatement(updateQuery);
                            updateStm.setString(1, username);
                            updateStm.setString(2, fullName);
                            updateStm.setString(3, addressField.getText());
                            updateStm.setInt(4, userId);
                        } else {
                            updateQuery = "UPDATE users SET username = ?, full_name = ? WHERE user_id = ?";
                            updateStm = Database.connection.prepareStatement(updateQuery);
                            updateStm.setString(1, username);
                            updateStm.setString(2, fullName);
                            updateStm.setInt(3, userId);
                        }
                    }

                    updateStm.executeUpdate();
                    JOptionPane.showMessageDialog(null, userType + " updated successfully!");

                    if (userType.equals("customer")) {
                        showAllCustomers();
                    } else {
                        showAllEmployees();
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public void setupCreateEmployeeButton() {
        createEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createEmployee();
            }
        });
    }

    public void createEmployee() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField fullNameField = new JTextField();

        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField,
                "Full Name:", fullNameField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Create Employee Account", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields!");
                return;
            }

            try {
                // FIXED: Added is_first_login = TRUE
                String query = "INSERT INTO users (username, password, full_name, address, user_type, is_first_login) VALUES (?, ?, ?, '', 'employee', TRUE)";
                PreparedStatement stm = Database.connection.prepareStatement(query);
                stm.setString(1, username);
                stm.setString(2, password);
                stm.setString(3, fullName);
                stm.executeUpdate();

                JOptionPane.showMessageDialog(null, "Employee account created successfully!\nThey will need to change password on first login.");
                showAllEmployees();
            } catch (Exception ex) {
                if (ex.getMessage().contains("Duplicate")) {
                    JOptionPane.showMessageDialog(null, "Username already exists!");
                } else {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        }
    }

    public void setupRefreshOrdersButton() {
        refreshOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllOrders();
                JOptionPane.showMessageDialog(null, "Orders refreshed!");
            }
        });
    }

    public void setupUpdateOrderStatusButton() {
        updateOrderStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int orderId = Integer.parseInt(ordersTable.getValueAt(selectedRow, 0).toString());
                    String currentStatus = ordersTable.getValueAt(selectedRow, 4).toString();
                    updateOrderStatus(orderId, currentStatus);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an order first!");
                }
            }
        });
    }
    public void viewOrderItems() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow >= 0) {
            int orderId = Integer.parseInt(ordersTable.getValueAt(selectedRow, 0).toString());

            try {
                String query = "SELECT p.product_name, oi.quantity, oi.price_at_time, (oi.quantity * oi.price_at_time) as subtotal " +
                        "FROM order_items oi " +
                        "JOIN products p ON oi.product_id = p.product_id " +
                        "WHERE oi.order_id = ?";
                PreparedStatement stm = Database.connection.prepareStatement(query);
                stm.setInt(1, orderId);
                ResultSet rs = stm.executeQuery();

                StringBuilder message = new StringBuilder();
                message.append("ORDER #").append(orderId).append(" ITEMS:\n");
                message.append("----------------------------------------\n\n");

                double total = 0;
                int itemNum = 1;
                while (rs.next()) {
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price_at_time");
                    double subtotal = rs.getDouble("subtotal");
                    total += subtotal;

                    message.append(itemNum++).append(". ").append(productName).append("\n");
                    message.append("   Quantity: ").append(quantity).append("\n");
                    message.append("   Price: $").append(String.format("%.2f", price)).append("\n");
                    message.append("   Subtotal: $").append(String.format("%.2f", subtotal)).append("\n\n");
                }

                message.append("----------------------------------------\n");
                message.append("TOTAL: $").append(String.format("%.2f", total));

                JOptionPane.showMessageDialog(this, message.toString(), "Order Items", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an order first!");
        }
    }

    public void updateOrderStatus(int orderId, String currentStatus) {
        String[] statuses = {"pending", "processing", "shipped", "delivered", "cancelled"};

        String newStatus = (String) JOptionPane.showInputDialog(null,
                "Select new status:",
                "Update Order Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            try {
                String query = "UPDATE orders SET status = ? WHERE order_id = ?";
                PreparedStatement stm = Database.connection.prepareStatement(query);
                stm.setString(1, newStatus);
                stm.setInt(2, orderId);
                stm.executeUpdate();

                JOptionPane.showMessageDialog(null, "Order status updated to: " + newStatus);
                showAllOrders();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }

    public void setupDeleteOrderButton() {
        deleteOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int orderId = Integer.parseInt(ordersTable.getValueAt(selectedRow, 0).toString());

                    int confirm = JOptionPane.showConfirmDialog(null, "Delete order #" + orderId + "?\nThis action cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            String query = "DELETE FROM orders WHERE order_id = ?";
                            PreparedStatement stm = Database.connection.prepareStatement(query);
                            stm.setInt(1, orderId);
                            stm.executeUpdate();

                            JOptionPane.showMessageDialog(null, "Order deleted successfully!");
                            showAllOrders();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an order first!");
                }
            }
        });
    }
}