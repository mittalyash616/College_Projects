
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.sql.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class AptitudeTest extends JFrame {
    private List<Question> questions;
    private int currentQuestion = 0;
    private int score = 0;
    private JLabel questionLabel;
    private JPanel optionsPanel;
    private ButtonGroup buttonGroup;
    private JButton nextButton;
    private JProgressBar progressBar;
    String currentUsername;
    private JButton previousButton;
    private List<Integer> selectedAnswers;
    int z=0;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Ykr@2024";
    private Connection connection;

    public AptitudeTest() {
        initializeDatabase();
        showLoginScreen();
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Database connection failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

private void showLoginScreen() {
    setTitle("QUIZ-X - Login");
    getContentPane().removeAll();
    getContentPane().setLayout(new BorderLayout());

    // Background with gradient
    JPanel backgroundPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(0, 0, new Color(28, 170, 255), getWidth(), getHeight(), new Color(20, 120, 255));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    backgroundPanel.setLayout(new BorderLayout());
    getContentPane().add(backgroundPanel, BorderLayout.CENTER);

    // Login Panel
    JPanel loginPanel = new JPanel(new GridBagLayout());
    loginPanel.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(15, 15, 15, 15);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel titleLabel = new JLabel("QUIZ-X Login");
    titleLabel.setFont(new Font("Roboto", Font.BOLD, 28));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    titleLabel.setForeground(Color.WHITE);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    loginPanel.add(titleLabel, gbc);

    JLabel userLabel = new JLabel("Username:");
    userLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
    userLabel.setForeground(Color.WHITE);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    loginPanel.add(userLabel, gbc);

    JTextField userText = new JTextField(15);
    userText.setFont(new Font("Roboto", Font.PLAIN, 14));
    userText.setBackground(new Color(245, 245, 245));
    userText.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
    userText.setCaretColor(new Color(0, 102, 204));
    userText.setSelectionColor(new Color(0, 102, 204));
    gbc.gridx = 1;
    gbc.gridy = 1;
    loginPanel.add(userText, gbc);

    JLabel passwordLabel = new JLabel("Password:");
    passwordLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
    passwordLabel.setForeground(Color.WHITE);
    gbc.gridx = 0;
    gbc.gridy = 2;
    loginPanel.add(passwordLabel, gbc);

    JPasswordField passwordText = new JPasswordField(15);
    passwordText.setFont(new Font("Roboto", Font.PLAIN, 14));
    passwordText.setBackground(new Color(245, 245, 245));
    passwordText.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
    passwordText.setCaretColor(new Color(0, 102, 204));
    passwordText.setSelectionColor(new Color(0, 102, 204));
    gbc.gridx = 1;
    gbc.gridy = 2;
    loginPanel.add(passwordText, gbc);

    JButton loginButton = new JButton("Login");
    styleButton(loginButton);
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    loginPanel.add(loginButton, gbc);

    JButton signupButton = new JButton("Don't have an account? Sign Up");
    signupButton.setFont(new Font("Roboto", Font.PLAIN, 12));
    signupButton.setBorderPainted(false);
    signupButton.setContentAreaFilled(false);
    signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    signupButton.setForeground(new Color(0, 191, 255));
    signupButton.setFocusPainted(false);
    signupButton.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent evt) {
            signupButton.setForeground(new Color(255, 255, 255));
            signupButton.setText("<html><u>Don't have an account? Sign Up</u></html>");
        }

        public void mouseExited(MouseEvent evt) {
            signupButton.setForeground(new Color(0, 191, 255));
            signupButton.setText("Don't have an account? Sign Up");
        }
    });
    gbc.gridx = 0;
    gbc.gridy = 4;
    loginPanel.add(signupButton, gbc);

    loginButton.addActionListener(e -> {
        String username = userText.getText();
        String password = new String(passwordText.getPassword());

        if (authenticate(username, password)) {
            currentUsername = username;
            showPostLoginScreen(); // show dashboard
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    });

    signupButton.addActionListener(e -> showSignupScreen());

    backgroundPanel.add(loginPanel, BorderLayout.CENTER);

    revalidate();
    repaint();
    pack();
    setSize(420, 350);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
}

private void showPostLoginScreen() {
    currentQuestion = 0;
    score = 0;
    setTitle("QUIZ-X - Dashboard");
    getContentPane().removeAll();
    getContentPane().setLayout(new BorderLayout());

    JPanel backgroundPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(0, 0, new Color(28, 170, 255), getWidth(), getHeight(), new Color(20, 120, 255));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    backgroundPanel.setLayout(new BorderLayout());
    getContentPane().add(backgroundPanel, BorderLayout.CENTER);

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);
    topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel welcomeLabel = new JLabel("Welcome, " + currentUsername + "!");
    welcomeLabel.setFont(new Font("Roboto", Font.BOLD, 22));
    welcomeLabel.setForeground(Color.WHITE);
    topPanel.add(welcomeLabel, BorderLayout.WEST);

    JButton logoutButton = new JButton("Logout");
    logoutButton.setFont(new Font("Roboto", Font.BOLD, 14));
    logoutButton.setForeground(Color.WHITE);
    logoutButton.setBackground(new Color(255, 69, 58));
    logoutButton.setBorder(BorderFactory.createLineBorder(new Color(200, 50, 50), 2));
    logoutButton.setOpaque(true);
    logoutButton.setFocusPainted(false);
    logoutButton.setPreferredSize(new Dimension(100, 35));
    logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    logoutButton.addActionListener(e -> logout());
    topPanel.add(logoutButton, BorderLayout.EAST);

    backgroundPanel.add(topPanel, BorderLayout.NORTH);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    buttonPanel.setOpaque(false);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80)); // Padding

    JButton startQuizButton = new JButton("Start Quiz");
    styleInteractiveButton(startQuizButton);
    startQuizButton.addActionListener(e -> initializeTest());
    buttonPanel.add(startQuizButton);
    buttonPanel.add(Box.createVerticalStrut(30)); // Gap

    JButton viewResultsButton = new JButton("View Previous Results");
    styleInteractiveButton(viewResultsButton);
    viewResultsButton.addActionListener(e -> showPreviousResults());
    buttonPanel.add(viewResultsButton);
    buttonPanel.add(Box.createVerticalStrut(30)); // Gap
    backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

    revalidate();
    repaint();
    pack();
    setSize(450, 350);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
}

    private void styleInteractiveButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 45));
        button.setFont(new Font("Roboto", Font.BOLD, 16));
        button.setBackground(new Color(0, 150, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2, true));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(0, 120, 255));
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2, true));
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(0, 150, 255));
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2, true));
            }

            public void mousePressed(MouseEvent evt) {
                button.setBackground(new Color(0, 100, 200));
            }

            public void mouseReleased(MouseEvent evt) {
                button.setBackground(new Color(0, 120, 255));
            }
        });
    }
    private void logout() {
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            dispose();
            showLoginScreen();
        }
    }

    private void showPreviousResults() {
        setTitle("QUIZ-X - Previous Results");
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(28, 170, 255), getWidth(), getHeight(), new Color(20, 120, 255));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        getContentPane().add(backgroundPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Roboto", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(70, 130, 180)); // Steel blue color
        backButton.setBorder(BorderFactory.createLineBorder(new Color(50, 100, 150), 2));
        backButton.setOpaque(true);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> showPostLoginScreen());
        topPanel.add(backButton, BorderLayout.WEST);

        JLabel heading = new JLabel("Your Previous Quiz Results", JLabel.CENTER);
        heading.setFont(new Font("Roboto", Font.BOLD, 22));
        heading.setForeground(Color.WHITE);
        topPanel.add(heading, BorderLayout.CENTER);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"QUIZ_ID", "Date", "Score (out of 15)"};
        List<Object[]> dataList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT id, timestamp, score FROM results where username='"+currentUsername+"' ORDER BY timestamp DESC")) {

            while (rs.next()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = dateFormat.format(rs.getTimestamp("timestamp"));
                String formattedScore = rs.getInt("score") + " /15";

                dataList.add(new Object[]{
                        rs.getInt("id"),
                        formattedDate,
                        formattedScore
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Object[][] data = dataList.toArray(new Object[0][]);

        JTable table = new JTable(data, columnNames) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(240, 248, 255) : new Color(230, 240, 255));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };

        table.setFont(new Font("Roboto", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(200, 220, 255));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Roboto", Font.BOLD, 14));
        header.setBackground(new Color(50, 120, 200));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createLineBorder(new Color(30, 100, 180)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        backgroundPanel.add(tablePanel, BorderLayout.CENTER);

        revalidate();
        repaint();
        pack();
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void showSignupScreen() {
        setTitle("QUIZ-X - Sign Up");
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(28, 170, 255), getWidth(), getHeight(), new Color(20, 120, 255));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        getContentPane().add(backgroundPanel, BorderLayout.CENTER);

        JPanel signupPanel = new JPanel(new GridBagLayout());
        signupPanel.setOpaque(false); // Allow background gradient to show through

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create an Account");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        signupPanel.add(titleLabel, gbc);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        signupPanel.add(userLabel, gbc);

        JTextField userText = new JTextField(15);
        userText.setFont(new Font("Roboto", Font.PLAIN, 14));
        userText.setBackground(new Color(245, 245, 245));
        userText.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        userText.setCaretColor(new Color(0, 102, 204));
        userText.setSelectionColor(new Color(0, 102, 204));
        gbc.gridx = 1;
        signupPanel.add(userText, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        signupPanel.add(passwordLabel, gbc);

        JPasswordField passwordText = new JPasswordField(15);
        passwordText.setFont(new Font("Roboto", Font.PLAIN, 14));
        passwordText.setBackground(new Color(245, 245, 245));
        passwordText.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        passwordText.setCaretColor(new Color(0, 102, 204));
        passwordText.setSelectionColor(new Color(0, 102, 204));
        gbc.gridx = 1;
        signupPanel.add(passwordText, gbc);

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        confirmLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        signupPanel.add(confirmLabel, gbc);

        JPasswordField confirmText = new JPasswordField(15);
        confirmText.setFont(new Font("Roboto", Font.PLAIN, 14));
        confirmText.setBackground(new Color(245, 245, 245));
        confirmText.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        confirmText.setCaretColor(new Color(0, 102, 204));
        confirmText.setSelectionColor(new Color(0, 102, 204));
        gbc.gridx = 1;
        signupPanel.add(confirmText, gbc);

        JButton signupButton = new JButton("Sign Up");
        styleButton(signupButton);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        signupPanel.add(signupButton, gbc);

        JButton loginButton = new JButton("Already have an account? Login");
        loginButton.setFont(new Font("Roboto", Font.PLAIN, 12));
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setForeground(new Color(0, 191, 255)); // Brighter link color
        loginButton.setFocusPainted(false);
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginButton.setForeground(Color.WHITE);
                loginButton.setText("<html><u>Already have an account? Login</u></html>");
            }

            public void mouseExited(MouseEvent evt) {
                loginButton.setForeground(new Color(0, 191, 255));
                loginButton.setText("Already have an account? Login");
            }
        });

        gbc.gridy = 5;
        signupPanel.add(loginButton, gbc);

        signupButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());
            String confirmPassword = new String(confirmText.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            handleSignup(username, password);
        });

        loginButton.addActionListener(e -> showLoginScreen());

        backgroundPanel.add(signupPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
        pack();
        setSize(420, 370);
        setLocationRelativeTo(null);
    }

    private void handlePreviousQuestion() {
        ButtonModel selectedButton = buttonGroup.getSelection();
        if (selectedButton != null) {
            int selectedAnswer = Integer.parseInt(selectedButton.getActionCommand());
            selectedAnswers.set(currentQuestion, selectedAnswer);
        }
        if (currentQuestion > 0) {
            currentQuestion--;
            displayQuestion();
            updateButtonStates();
        }
    }

private void styleButton(JButton button) {
    button.setBackground(new Color(0, 150, 255));
    button.setFont(new Font("Roboto", Font.BOLD, 14));
    button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding around text
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setOpaque(true);
    button.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2, true));

    button.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            button.setBackground(new Color(0, 120, 255));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            button.setBackground(new Color(0, 150, 255));
        }
    });
}
    private boolean authenticate(String username, String password) {
        String sql = "SELECT id, password FROM users WHERE username = ?";
        currentUsername=username;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                z=rs.getInt("id");
                String storedPassword = rs.getString("password");
                return password.equals(storedPassword);
            }
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    private void handleSignup(String username, String password) {
        String sql = "INSERT INTO users (id,username, password) VALUES (?,?, ?)";
        String sql2 = "SELECT MAX(id) FROM users";
        String intid = null;

        try (PreparedStatement pstmt1 = connection.prepareStatement(sql2)) {
            ResultSet rs1 = pstmt1.executeQuery();

            if (rs1.next()) {
                int maxId = rs1.getInt(1);
                intid = String.valueOf(maxId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int id = Integer.parseInt(intid);
        id++;


        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, String.valueOf(id));
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            showLoginScreen();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                JOptionPane.showMessageDialog(this,
                        "Username already exists",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private int getCurrentUserId() {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, currentUsername);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("User not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to retrieve user ID: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }
private void initializeTest() {
    questions = Question.getQuestions(connection);
    if (questions.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "No questions found in database",
                "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    selectedAnswers = new ArrayList<>(Collections.nCopies(questions.size(), null));

    Collections.shuffle(questions);
    setupUI();
    displayQuestion();
    updateButtonStates();  // Add this line
    setTitle("QUIZ-X - Aptitude Test");
    setSize(600, 450);
    setLocationRelativeTo(null);
}

private void setupUI() {
    getContentPane().removeAll();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel backgroundPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(240, 243, 244),
                    getWidth(), getHeight(), new Color(20, 120, 255)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    backgroundPanel.setLayout(new BorderLayout());
    getContentPane().add(backgroundPanel, BorderLayout.CENTER);

    JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
    mainPanel.setOpaque(false);

    JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
    centerPanel.setOpaque(false);

    questionLabel = new JLabel();
    questionLabel.setFont(new Font("Roboto", Font.BOLD, 18));
    questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    questionLabel.setForeground(Color.WHITE);
    centerPanel.add(questionLabel, BorderLayout.NORTH);

    optionsPanel = new JPanel();
    optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
    optionsPanel.setOpaque(false);
    optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

    JPanel optionsWrapper = new JPanel(new BorderLayout());
    optionsWrapper.setOpaque(false);
    optionsWrapper.add(optionsPanel, BorderLayout.NORTH);
    centerPanel.add(optionsWrapper, BorderLayout.CENTER);

    mainPanel.add(centerPanel, BorderLayout.CENTER);

    previousButton = new JButton("Previous");
    styleInteractiveButton(previousButton);
    previousButton.setPreferredSize(new Dimension(150, 50)); // Larger size
    previousButton.setEnabled(false);
    previousButton.addActionListener(e -> handlePreviousQuestion());

    nextButton = new JButton("Next");
    styleInteractiveButton(nextButton);
    nextButton.setPreferredSize(new Dimension(150, 50)); // Larger size
    nextButton.addActionListener(e -> handleNextQuestion());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
    buttonPanel.setOpaque(false);
    buttonPanel.add(previousButton);
    buttonPanel.add(nextButton);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    progressBar = new JProgressBar(1, questions.size() + 1);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    progressBar.setForeground(new Color(255, 255, 255, 200)); // Semi-transparent white
    progressBar.setBackground(new Color(255, 255, 255, 100)); // Semi-transparent track
    progressBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 150), 2, true),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
    ));
    progressBar.setPreferredSize(new Dimension(600, 25)); // Slightly larger
    progressBar.setFont(new Font("Roboto", Font.BOLD, 12));

    JPanel progressPanel = new JPanel(new BorderLayout());
    progressPanel.setOpaque(false);
    progressPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
    progressPanel.add(progressBar, BorderLayout.CENTER);

    mainPanel.add(progressPanel, BorderLayout.NORTH);
    backgroundPanel.add(mainPanel, BorderLayout.CENTER);

    revalidate();
    repaint();
    pack();
    setSize(850, 650);
    setLocationRelativeTo(null);
}
    private void displayQuestion() {
    Question question = questions.get(currentQuestion);
    questionLabel.setText((currentQuestion + 1) + ". " + question.getText());

    optionsPanel.removeAll();
    buttonGroup = new ButtonGroup();

    for (int i = 0; i < question.getOptions().length; i++) {
        JRadioButton option = new JRadioButton(question.getOptions()[i]);
        option.setActionCommand(String.valueOf(i));
        option.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        option.setBackground(new Color(245, 245, 245));

        if (selectedAnswers.get(currentQuestion) != null &&
                selectedAnswers.get(currentQuestion) == i) {
            option.setSelected(true);
        }

        JPanel optionWrapper = new JPanel(new BorderLayout());
        optionWrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        optionWrapper.setBackground(new Color(245, 245, 245));
        optionWrapper.add(option, BorderLayout.WEST);
        buttonGroup.add(option);
        optionsPanel.add(optionWrapper);
    }

    progressBar.setValue(currentQuestion + 1);
    optionsPanel.revalidate();
    optionsPanel.repaint();
    updateButtonStates();
}
    private void updateButtonStates() {
        previousButton.setEnabled(currentQuestion > 0);

        if (currentQuestion == questions.size() - 1) {
            nextButton.setText("Submit");
        } else {
            nextButton.setText("Next");
        }

        nextButton.setEnabled(currentQuestion < questions.size());
    }

private void handleNextQuestion() {
    ButtonModel selectedButton = buttonGroup.getSelection();
    if (selectedButton == null) {
        JOptionPane.showMessageDialog(this, "Please select an answer!");
        return;
    }

    int selectedAnswer = Integer.parseInt(selectedButton.getActionCommand());
    selectedAnswers.set(currentQuestion, selectedAnswer);

    if (selectedAnswer == questions.get(currentQuestion).getCorrectAnswer()) {
        score++;
    }

    currentQuestion++;

    if (currentQuestion < questions.size()) {
        displayQuestion();
        updateButtonStates();
    } else {
        showResults();
    }
}


private void showResults() {
    setTitle("QUIZ-X - Test Results");
    getContentPane().removeAll();

    JPanel backgroundPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(28, 170, 255),
                    getWidth(), getHeight(), new Color(20, 120, 255));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    backgroundPanel.setLayout(new BorderLayout());
    getContentPane().add(backgroundPanel, BorderLayout.CENTER);

    JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    mainPanel.setOpaque(false);

    double percentage = (score * 100.0) / questions.size();
    String recommendation = getCompanyRecommendation(percentage);
    JTextArea textArea = new JTextArea();
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setFont(new Font("Roboto", Font.PLAIN, 14));
    textArea.setForeground(Color.WHITE);
    textArea.setOpaque(false);
    textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    StringBuilder resultsText = new StringBuilder();
    resultsText.append("TEST RESULTS\n\n");
    resultsText.append("Score: ").append(score).append("/").append(questions.size()).append("\n");
    resultsText.append("Percentage: ").append(String.format("%.2f", percentage)).append("%\n\n");
    resultsText.append("Recommendation: ").append(recommendation).append("\n\n");
    boolean hasWrongAnswers = false;
    for (int i = 0; i < questions.size(); i++) {
        Integer userAnswer = selectedAnswers.get(i);
        if (userAnswer != null && userAnswer != questions.get(i).getCorrectAnswer()) {
            if (!hasWrongAnswers) {
                resultsText.append("INCORRECT ANSWERS:\n\n");
                hasWrongAnswers = true;
            }
            Question q = questions.get(i);
            resultsText.append("Question ").append(i+1).append(": ").append(q.getText()).append("\n");
            resultsText.append("Your answer: ").append(q.getOptions()[userAnswer]).append("\n");
            resultsText.append("Correct answer: ").append(q.getOptions()[q.getCorrectAnswer()]).append("\n\n");
        }
    }
    if (!hasWrongAnswers) {
        resultsText.append("PERFECT SCORE! All answers correct!\n");
    }
    textArea.setText(resultsText.toString());
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            "Your Results",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Roboto", Font.BOLD, 16),
            Color.WHITE
    ));

    mainPanel.add(scrollPane, BorderLayout.CENTER);
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
    buttonPanel.setOpaque(false);
    JButton backButton = new JButton("Back to Dashboard");
    styleInteractiveButton(backButton);
    backButton.addActionListener(e -> showPostLoginScreen());
    buttonPanel.add(backButton);
    JButton exitButton = new JButton("Exit");
    styleInteractiveButton(exitButton);
    exitButton.addActionListener(e -> System.exit(0));
    buttonPanel.add(exitButton);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    backgroundPanel.add(mainPanel, BorderLayout.CENTER);
    saveResultsToDatabase(score);
    revalidate();
    repaint();
    setSize(800, 600);
    setLocationRelativeTo(null);
}
    private void saveResultsToDatabase(double score) {
        String sql2 = "SELECT MAX(id) FROM results";
        String intid = null;

        try (PreparedStatement pstmt1 = connection.prepareStatement(sql2)) {
            ResultSet rs1 = pstmt1.executeQuery();

            if (rs1.next()) {
                int maxId = rs1.getInt(1);
                intid = String.valueOf(maxId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int id = Integer.parseInt(intid);
        id++;



        int userId = getCurrentUserId();
        if (userId != -1) {
            String sql = "INSERT INTO results " +
                    "(id,user_id,username,score,timestamp) " +
                    "VALUES (?,?, ?,?, CURRENT_TIMESTAMP)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.setInt(2, z);
                pstmt.setString(3, currentUsername);
                pstmt.setDouble(4, score);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Could not save results: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private String getCompanyRecommendation(double percentage) {
        if (percentage >= 80) {
            return "You're eligible for top-tier tech companies like Google, Microsoft, and Amazon!";
        } else if (percentage >= 60) {
            return "You're a good fit for mid-sized tech companies and established startups!";
        } else if (percentage >= 40) {
            return "Consider applying to growing startups and regional tech companies!";
        } else {
            return "We recommend gaining more practice before applying. Try some coding bootcamps or online courses!";
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AptitudeTest().setVisible(true);
        });
    }
}