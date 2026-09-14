package ec.edu.monster.vista;

import ec.edu.monster.servicio.ClienteFederacion;
import ec.edu.monster.ws.generated.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;
    private boolean authenticated = false;
    private final ClienteFederacion cliente = new ClienteFederacion();

    public LoginFrame() {
        setTitle("🎟️ Iniciar Sesión - TicketPremium");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa grande
        
        initUI();
    }

    private void initUI() {
        // Contenedor principal con GridBagLayout para centrar la tarjeta de login
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(new Color(18, 18, 24)); // Fondo muy oscuro tipo Glassmorphism / Moderno
        
        // Tarjeta de login con dos lados (Izquierda: Ilustración/Marca, Derecha: Formulario)
        JPanel cardPanel = new JPanel(new GridLayout(1, 2));
        cardPanel.setPreferredSize(new Dimension(850, 500));
        cardPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        cardPanel.setBackground(new Color(30, 30, 40));
        
        // --- PANEL IZQUIERDO (Diseño / Marca / Mensaje) ---
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Degradado premium Indigo a Púrpura
                GradientPaint gp = new GradientPaint(0, 0, new Color(99, 102, 241), 
                                                     getWidth(), getHeight(), new Color(168, 85, 247));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.insets = new Insets(10, 20, 10, 20);
        gbcLeft.gridx = 0;
        
        JLabel lblBigIcon = new JLabel("🎟️", SwingConstants.CENTER);
        lblBigIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        gbcLeft.gridy = 0;
        leftPanel.add(lblBigIcon, gbcLeft);

        JLabel lblBrandName = new JLabel("TICKET PREMIUM", SwingConstants.CENTER);
        lblBrandName.setFont(new Font("Inter", Font.BOLD, 28));
        lblBrandName.setForeground(Color.WHITE);
        gbcLeft.gridy = 1;
        leftPanel.add(lblBrandName, gbcLeft);

        JLabel lblSlogan = new JLabel("Tu entrada a los mejores eventos deportivos", SwingConstants.CENTER);
        lblSlogan.setFont(new Font("Inter", Font.PLAIN, 14));
        lblSlogan.setForeground(new Color(233, 213, 255));
        gbcLeft.gridy = 2;
        leftPanel.add(lblSlogan, gbcLeft);
        
        cardPanel.add(leftPanel);

        // --- PANEL DERECHO (Formulario de Entrada) ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(30, 30, 40));
        rightPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.fill = GridBagConstraints.HORIZONTAL;
        gbcRight.insets = new Insets(8, 0, 8, 0);
        gbcRight.gridx = 0;

        JLabel lblWelcome = new JLabel("¡Bienvenido de nuevo!");
        lblWelcome.setFont(new Font("Inter", Font.BOLD, 24));
        lblWelcome.setForeground(Color.WHITE);
        gbcRight.gridy = 0;
        rightPanel.add(lblWelcome, gbcRight);

        JLabel lblSub = new JLabel("Ingresa tus credenciales para continuar");
        lblSub.setFont(new Font("Inter", Font.PLAIN, 12));
        lblSub.setForeground(new Color(156, 163, 175));
        gbcRight.gridy = 1;
        rightPanel.add(lblSub, gbcRight);

        // Espacio
        gbcRight.gridy = 2;
        rightPanel.add(Box.createVerticalStrut(15), gbcRight);

        // Campo Usuario
        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("Inter", Font.BOLD, 12));
        lblUser.setForeground(new Color(156, 163, 175));
        gbcRight.gridy = 3;
        rightPanel.add(lblUser, gbcRight);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Inter", Font.PLAIN, 14));
        txtUsername.setBackground(new Color(45, 45, 58));
        txtUsername.setForeground(Color.WHITE);
        txtUsername.setCaretColor(Color.WHITE);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(75, 75, 95), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        txtUsername.putClientProperty("JTextField.placeholderText", "Ej. MONSTER");
        gbcRight.gridy = 4;
        rightPanel.add(txtUsername, gbcRight);

        // Campo Contraseña
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Inter", Font.BOLD, 12));
        lblPass.setForeground(new Color(156, 163, 175));
        gbcRight.gridy = 5;
        rightPanel.add(lblPass, gbcRight);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Inter", Font.PLAIN, 14));
        txtPassword.setBackground(new Color(45, 45, 58));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(75, 75, 95), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        txtPassword.putClientProperty("JTextField.placeholderText", "••••••••");
        gbcRight.gridy = 6;
        rightPanel.add(txtPassword, gbcRight);

        // Espacio
        gbcRight.gridy = 7;
        rightPanel.add(Box.createVerticalStrut(5), gbcRight);

        // Etiqueta de Error
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setFont(new Font("Inter", Font.BOLD, 12));
        lblError.setForeground(new Color(239, 68, 68)); // Rojo suave moderno
        gbcRight.gridy = 8;
        rightPanel.add(lblError, gbcRight);

        // Botón Iniciar Sesión
        btnLogin = new JButton("Iniciar Sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(79, 70, 229));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(99, 102, 241).brighter());
                } else {
                    g2.setColor(new Color(99, 102, 241));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogin.setFont(new Font("Inter", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 42));
        gbcRight.gridy = 9;
        rightPanel.add(btnLogin, gbcRight);

        btnLogin.addActionListener(e -> authenticate());
        
        // Agregar enter en password para logearse
        txtPassword.addActionListener(e -> authenticate());
        txtUsername.addActionListener(e -> authenticate());

        cardPanel.add(rightPanel);

        // Efecto sombra para la tarjeta
        mainContainer.add(cardPanel);
        add(mainContainer);
    }

    private void authenticate() {
        lblError.setText("Conectando...");
        lblError.setForeground(new Color(99, 102, 241));
        btnLogin.setEnabled(false);
        
        // Ejecutar en hilo de fondo para no congelar la UI
        new SwingWorker<Usuario, Void>() {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            @Override
            protected Usuario doInBackground() throws Exception {
                return cliente.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    Usuario user = get();
                    if (user != null) {
                        authenticated = true;
                        lblError.setText("¡Éxito! Iniciando...");
                        lblError.setForeground(new Color(16, 185, 129));
                        
                        // Retardo corto estético antes de abrir la pantalla principal
                        Timer timer = new Timer(500, ev -> {
                            new VentanaPrincipal().setVisible(true);
                            dispose();
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        lblError.setText("⚠️ Usuario o contraseña incorrectos");
                        lblError.setForeground(new Color(239, 68, 68));
                        btnLogin.setEnabled(true);
                    }
                } catch (Exception ex) {
                    lblError.setText("⚠️ Error de conexión con el servidor");
                    lblError.setForeground(new Color(239, 68, 68));
                    btnLogin.setEnabled(true);
                }
            }
        }.execute();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
