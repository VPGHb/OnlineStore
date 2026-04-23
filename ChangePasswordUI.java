import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;

public class ChangePasswordUI extends JFrame {
    private JPanel panel;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton changePasswordButton;
    private JLabel welcomeLabel;

    private int userId;
    private String fullName;

    public ChangePasswordUI(int userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;

        this.setContentPane(this.panel);
        this.setTitle("Change Password - First Login");
        this.setBounds(600, 300, 400, 250);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        welcomeLabel.setText("Welcome " + fullName + "! Please set your new password.");

        setupChangePasswordButton();
    }

    public void setupChangePasswordButton() {
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });
    }

    public void changePassword() {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password cannot be empty!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "Passwords do not match!");
            return;
        }

        try {
            String query = "UPDATE users SET password = ?, is_first_login = false WHERE user_id = ?";
            PreparedStatement stm = Database.connection.prepareStatement(query);
            stm.setString(1, newPassword);
            stm.setInt(2, userId);
            stm.executeUpdate();

            JOptionPane.showMessageDialog(null, "Password changed successfully! Please login again.");
            this.dispose();
            new MainMenu();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}