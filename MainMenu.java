import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    private JPanel panel;
    private JButton customerLoginButton;
    private JButton employeeLoginButton;
    private JButton adminLoginButton;
    private JButton registerButton;

    public MainMenu() {
        this.setContentPane(panel);
        this.setTitle("Online Store - Main Menu");
        this.setBounds(600, 200, 400, 300);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupCustomerLoginAction();
        setupEmployeeLoginAction();
        setupAdminLoginAction();
        setupRegisterAction();
    }

    public void setupCustomerLoginAction() {
        customerLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToCustomerLogin();
            }
        });
    }

    public void setupEmployeeLoginAction() {
        employeeLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToEmployeeLogin();
            }
        });
    }

    public void setupAdminLoginAction() {
        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToAdminLogin();
            }
        });
    }

    public void setupRegisterAction() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToRegister();
            }
        });
    }

    public void goToCustomerLogin() {
        this.dispose();
        new LoginUI("customer");
    }

    public void goToEmployeeLogin() {
        this.dispose();
        new LoginUI("employee");
    }

    public void goToAdminLogin() {
        this.dispose();
        new LoginUI("admin");
    }

    public void goToRegister() {
        this.dispose();
        new CustomerRegistrationUI();
    }

    public static void main(String[] args) {
        Database.connect();
        Database.autoCloseDB();
        new MainMenu();
    }
}