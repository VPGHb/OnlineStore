import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class CustomerDashboard extends JFrame {
    private JPanel panel;
    private JTabbedPane tabbedPane;
    private JTable productsTable;
    private JButton addToCartButton;
    private JButton viewCartButton;
    private JTable ordersTable;
    private JButton cancelOrderButton;
    private JButton refreshOrdersButton;
    private JButton updateProfileButton;
    private JButton homeButton;
    private JLabel welcomeLabel;
    private JButton viewOrderItemsButton;

    private int customerId;
    private String customerName;
    private HashMap<Integer, Integer> cart; // product_id -> quantity

    public CustomerDashboard(int customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.cart = new HashMap<>();

        this.setContentPane(this.panel);
        this.setTitle("Customer Dashboard - " + customerName);
        this.setBounds(200, 100, 900, 600);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        welcomeLabel.setText("Welcome, " + customerName + "!");

        setupAddToCartButton();
        setupViewCartButton();
        setupCancelOrderButton();
        setupRefreshOrdersButton();
        setupViewOrderItemsButton();
        setupUpdateProfileButton();
        setupHomeButton();

        showAllProducts();
        showMyOrders();
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
            String query = "SELECT product_id, product_name, price, stock_quantity FROM products WHERE stock_quantity > 0";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            ResultSet result = stm.executeQuery();
            productsTable.setModel(DbUtils.resultSetToTableModel(result));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void showMyOrders() {
        try {
            String query = "SELECT order_id, order_date, total_amount, status FROM orders WHERE customer_id = ? ORDER BY order_date DESC";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            stm.setInt(1, customerId);
            ResultSet result = stm.executeQuery();
            ordersTable.setModel(DbUtils.resultSetToTableModel(result));
        } catch (Exception e) {
            System.out.println(e);
        }
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

    public void setupAddToCartButton() {
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int productId = Integer.parseInt(productsTable.getValueAt(selectedRow, 0).toString());
                    String productName = productsTable.getValueAt(selectedRow, 1).toString();
                    String quantityStr = JOptionPane.showInputDialog("Enter quantity:");

                    if (quantityStr != null) {
                        int quantity = Integer.parseInt(quantityStr);
                        if (quantity > 0) {
                            cart.put(productId, cart.getOrDefault(productId, 0) + quantity);
                            JOptionPane.showMessageDialog(null, quantity + " x " + productName + " added to cart!");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a product first!");
                }
            }
        });
    }

    public void setupViewCartButton() {
        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCart();
            }
        });
    }

    public void viewCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Your cart is empty!");
            return;
        }

        StringBuilder cartContent = new StringBuilder("YOUR CART:\n\n");
        double total = 0;

        for (int productId : cart.keySet()) {
            try {
                String query = "SELECT product_name, price FROM products WHERE product_id = ?";
                PreparedStatement stm = Database.connection.prepareStatement(query);
                stm.setInt(1, productId);
                ResultSet rs = stm.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("product_name");
                    double price = rs.getDouble("price");
                    int quantity = cart.get(productId);
                    double subtotal = price * quantity;
                    total += subtotal;
                    cartContent.append(name).append(" x").append(quantity).append(" = $").append(subtotal).append("\n");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        cartContent.append("\nTOTAL: $").append(total);

        String couponCode = JOptionPane.showInputDialog(cartContent.toString() + "\n\nEnter coupon code (or click Cancel to skip):");

        if (couponCode != null && !couponCode.isEmpty()) {
            applyCouponAndCheckout(couponCode, total);
        } else if (couponCode != null) {
            checkout(total, null, 0);
        }
    }

    public void applyCouponAndCheckout(String couponCode, double total) {
        try {
            String query = "SELECT discount_percent FROM coupons WHERE coupon_code = ? AND is_active = 'YES'";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            stm.setString(1, couponCode);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                double discountPercent = rs.getDouble("discount_percent");
                double discountAmount = total * (discountPercent / 100);
                double newTotal = total - discountAmount;

                int confirm = JOptionPane.showConfirmDialog(null,
                        "Coupon applied!\nOriginal: $" + total +
                                "\nDiscount: $" + discountAmount +
                                "\nNew Total: $" + newTotal +
                                "\n\nProceed with checkout?",
                        "Confirm Order", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    checkout(newTotal, couponCode, discountAmount);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid coupon code!");
                viewCart();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void checkout(double total, String couponCode, double discountAmount) {
        try {
            // Insert order
            String orderQuery = "INSERT INTO orders (customer_id, total_amount, coupon_code, discount_amount, status) VALUES (?, ?, ?, ?, 'pending')";
            PreparedStatement orderStm = Database.connection.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            orderStm.setInt(1, customerId);
            orderStm.setDouble(2, total);
            orderStm.setString(3, couponCode);
            orderStm.setDouble(4, discountAmount);
            orderStm.executeUpdate();

            ResultSet generatedKeys = orderStm.getGeneratedKeys();
            int orderId = 0;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            }

            // Insert order items and update stock
            for (int productId : cart.keySet()) {
                int quantity = cart.get(productId);

                String priceQuery = "SELECT price FROM products WHERE product_id = ?";
                PreparedStatement priceStm = Database.connection.prepareStatement(priceQuery);
                priceStm.setInt(1, productId);
                ResultSet rs = priceStm.executeQuery();
                rs.next();
                double price = rs.getDouble("price");

                String itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price_at_time) VALUES (?, ?, ?, ?)";
                PreparedStatement itemStm = Database.connection.prepareStatement(itemQuery);
                itemStm.setInt(1, orderId);
                itemStm.setInt(2, productId);
                itemStm.setInt(3, quantity);
                itemStm.setDouble(4, price);
                itemStm.executeUpdate();

                String updateStock = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
                PreparedStatement stockStm = Database.connection.prepareStatement(updateStock);
                stockStm.setInt(1, quantity);
                stockStm.setInt(2, productId);
                stockStm.executeUpdate();
            }

            cart.clear();
            JOptionPane.showMessageDialog(null, "Order placed successfully! Order ID: " + orderId);
            showMyOrders();
            showAllProducts();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error placing order: " + e.getMessage());
        }
    }

    public void setupCancelOrderButton() {
        cancelOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int orderId = Integer.parseInt(ordersTable.getValueAt(selectedRow, 0).toString());
                    String status = ordersTable.getValueAt(selectedRow, 3).toString();

                    if (status.equals("cancelled")) {
                        JOptionPane.showMessageDialog(null, "Order already cancelled!");
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(null, "Cancel this order?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            cancelOrder(orderId);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an order first!");
                }
            }
        });
    }

    public void cancelOrder(int orderId) {
        try {
            String query = "DELETE FROM orders WHERE order_id = ? AND customer_id = ?";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            stm.setInt(1, orderId);
            stm.setInt(2, customerId);
            stm.executeUpdate();

            JOptionPane.showMessageDialog(null, "Order cancelled and removed from database!");
            showMyOrders();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setupRefreshOrdersButton() {
        refreshOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMyOrders();
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

    public void setupUpdateProfileButton() {
        updateProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });
    }

    public void updateProfile() {
        String newName = JOptionPane.showInputDialog("Enter new name:", customerName);
        String newAddress = JOptionPane.showInputDialog("Enter new address:");

        if (newName != null && !newName.isEmpty() && newAddress != null && !newAddress.isEmpty()) {
            try {
                String query = "UPDATE users SET full_name = ?, address = ? WHERE user_id = ?";
                PreparedStatement stm = Database.connection.prepareStatement(query);
                stm.setString(1, newName);
                stm.setString(2, newAddress);
                stm.setInt(3, customerId);
                stm.executeUpdate();

                JOptionPane.showMessageDialog(null, "Profile updated successfully!");
                customerName = newName;
                welcomeLabel.setText("Welcome, " + customerName + "!");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}