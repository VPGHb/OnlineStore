import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeDashboard extends JFrame {
    private JPanel panel;
    private JTabbedPane tabbedPane;
    private JTable productsTable;
    private JButton addProductButton;
    private JButton refreshProductsButton;
    private JTable customersTable;
    private JButton refreshCustomersButton;
    private JTable ordersTable;
    private JButton refreshOrdersButton;
    private JButton createCouponButton;
    private JButton homeButton;
    private JTabbedPane tabbedPane1;
    private JLabel welcomeLabel;
    private JButton viewOrderItemsButton;

    private int employeeId;
    private String employeeName;

    public EmployeeDashboard(int employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;

        this.setContentPane(this.panel);
        this.setTitle("Employee Dashboard - " + employeeName);
        this.setBounds(200, 100, 1000, 700);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        welcomeLabel.setText("Welcome, Employee " + employeeName + "!");

        setupAddProductButton();
        setupRefreshProductsButton();
        setupRefreshCustomersButton();
        setupRefreshOrdersButton();
        setupViewOrderItemsButton();
        setupCreateCouponButton();
        setupHomeButton();

        showAllProducts();
        showAllCustomers();
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

    public void showAllProducts() {
        try {
            String query = "SELECT product_id, product_name, price, stock_quantity FROM products";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            ResultSet result = stm.executeQuery();
            productsTable.setModel(DbUtils.resultSetToTableModel(result));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void showAllCustomers() {
        try {
            String query = "SELECT user_id, username, full_name, address FROM users WHERE user_type = 'customer'";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            ResultSet result = stm.executeQuery();
            customersTable.setModel(DbUtils.resultSetToTableModel(result));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void showAllOrders() {
        try {
            // Orders sorted by customer name as required
            String query = "SELECT o.order_id, u.full_name, o.order_date, o.total_amount, o.status " +
                    "FROM orders o JOIN users u ON o.customer_id = u.user_id " +
                    "ORDER BY u.full_name ASC";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            ResultSet result = stm.executeQuery();
            ordersTable.setModel(DbUtils.resultSetToTableModel(result));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setupAddProductButton() {
        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
    }

    public void addProduct() {
        String name = JOptionPane.showInputDialog("Enter product name:");
        if (name == null || name.isEmpty()) return;

        String priceStr = JOptionPane.showInputDialog("Enter price:");
        if (priceStr == null || priceStr.isEmpty()) return;

        String stockStr = JOptionPane.showInputDialog("Enter stock quantity:");
        if (stockStr == null || stockStr.isEmpty()) return;

        String description = JOptionPane.showInputDialog("Enter description (optional):");

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            String query = "INSERT INTO products (product_name, price, stock_quantity, description) VALUES (?, ?, ?, ?)";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            stm.setString(1, name);
            stm.setDouble(2, price);
            stm.setInt(3, stock);
            stm.setString(4, description);
            stm.executeUpdate();

            JOptionPane.showMessageDialog(null, "Product added successfully!");
            showAllProducts();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public void setupRefreshProductsButton() {
        refreshProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllProducts();
                JOptionPane.showMessageDialog(null, "Products refreshed!");
            }
        });
    }

    public void setupRefreshCustomersButton() {
        refreshCustomersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllCustomers();
                JOptionPane.showMessageDialog(null, "Customers list refreshed!");
            }
        });
    }

    public void setupRefreshOrdersButton() {
        refreshOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllOrders();
                JOptionPane.showMessageDialog(null, "Orders list refreshed!");
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

    public void setupCreateCouponButton() {
        createCouponButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createCoupon();
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

    public void createCoupon() {
        String code = JOptionPane.showInputDialog("Enter coupon code (e.g., SAVE20):");
        if (code == null || code.isEmpty()) return;

        String discountStr = JOptionPane.showInputDialog("Enter discount percentage (1-100):");
        if (discountStr == null || discountStr.isEmpty()) return;

        try {
            double discount = Double.parseDouble(discountStr);

            String query = "INSERT INTO coupons (coupon_code, discount_percent, is_active, created_by) VALUES (?, ?, 'YES', ?)";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            stm.setString(1, code.toUpperCase());
            stm.setDouble(2, discount);
            stm.setInt(3, employeeId);
            stm.executeUpdate();

            JOptionPane.showMessageDialog(null, "Coupon '" + code + "' created with " + discount + "% discount!");
        } catch (Exception ex) {
            if (ex.getMessage().contains("Duplicate")) {
                JOptionPane.showMessageDialog(null, "Coupon code already exists!");
            } else {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }
}