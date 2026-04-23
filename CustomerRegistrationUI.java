import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;

public class CustomerRegistrationUI extends JFrame {
    private JPanel panel;
    private JTextField usernameTF;
    private JPasswordField passwordPF;
    private JTextField fullNameTF;
    private JTextArea addressTA;
    private JButton registerButton;
    private JButton homeButton;

    public CustomerRegistrationUI() {
        this.setContentPane(this.panel);
        this.setTitle("Customer Registration");
        this.setBounds(600, 200, 400, 400);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupRegisterButton();
        setupHomeButton();
    }

    public void setupRegisterButton() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerCustomer();
            }
        });
    }

    public void setupHomeButton() {
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goHome();
            }
        });
    }

    public void registerCustomer() {
        String username = usernameTF.getText();
        String password = new String(passwordPF.getPassword());
        String fullName = fullNameTF.getText();
        String address = addressTA.getText();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields!");
            return;
        }

        try {
            String query = "INSERT INTO users (username, password, full_name, address, user_type, is_first_login) VALUES (?, ?, ?, ?, 'customer', false)";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            stm.setString(1, username);
            stm.setString(2, password);
            stm.setString(3, fullName);
            stm.setString(4, address);
            stm.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registration successful! Please login.");
            goHome();

        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate")) {
                JOptionPane.showMessageDialog(null, "Username already exists!");
            } else {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    public void goHome() {
        this.dispose();
        new MainMenu();
    }
}