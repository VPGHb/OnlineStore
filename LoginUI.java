import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginUI extends JFrame {
    private JPanel panel;
    private JTextField usernameTF;
    private JPasswordField passwordPF;
    private JButton loginButton;
    private JButton homeButton;
    private JLabel titleLabel;

    private String userType;

    public LoginUI(String userType) {
        this.userType = userType;

        this.setContentPane(this.panel);
        this.setTitle(userType.toUpperCase() + " Login");
        this.setBounds(600, 200, 350, 250);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        titleLabel.setText(userType.toUpperCase() + " Login");

        setupLoginButton();
        setupHomeButton();
    }

    public void setupLoginButton() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
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

    public void handleLogin() {
        String username = usernameTF.getText();
        String password = new String(passwordPF.getPassword());

        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ? AND user_type = ?";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            stm.setString(1, username);
            stm.setString(2, password);
            stm.setString(3, userType);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String fullName = rs.getString("full_name");
                boolean isFirstLogin = rs.getBoolean("is_first_login");

                JOptionPane.showMessageDialog(this, "Welcome " + fullName + "!");

                if (userType.equals("employee") && isFirstLogin) {
                    goToChangePassword(userId, fullName);
                } else {
                    this.dispose();

                    if (userType.equals("admin")) {
                        new AdminDashboard(userId, fullName);
                    } else if (userType.equals("employee")) {
                        new EmployeeDashboard(userId, fullName);
                    } else {
                        new CustomerDashboard(userId, fullName);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Login error: " + e.getMessage());
        }
    }

    public void goHome() {
        this.dispose();
        new MainMenu();
    }

    public void goToChangePassword(int userId, String fullName) {
        this.dispose();
        new ChangePasswordUI(userId, fullName);
    }
}