package ec.edu.monster.vista;

import ec.edu.monster.servicio.ClienteFederacion;
import ec.edu.monster.servicio.BancoSoapClient;
import ec.edu.monster.ws.generated.*;
import ec.edu.monster.util.FormatUtil;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MapaAsientosDialog extends JDialog {

    private final ClienteFederacion cliente = new ClienteFederacion();
    private final int codigoPartido;
    private final String partidoInfo;

    private List<AsientoPartido> todosAsientos = new ArrayList<>();
    private final List<AsientoPartido> asientosSeleccionados = new ArrayList<>();

    // Componentes UI
    private JPanel panelEstadio;
    private JLabel lblTotal;
    private JTextField txtCedula;
    private JTextField txtNombre;
    private JComboBox<String> cmbFormaPago;
    private JComboBox<Integer> cmbPlazo;
    private JButton btnComprar;

    private double precioPalco = 150.0;
    private double precioTribuna = 80.0;
    private double precioGeneral = 40.0;
    
    private int idPalcoLoc = 0;
    private int idTribunaLoc = 0;
    private int idGeneralLoc = 0;

    public MapaAsientosDialog(Frame parent, int codigoPartido, String partidoInfo) {
        super(parent, "🏟️ Mapa de Selección de Asientos - FIFA 2026", true);
        this.codigoPartido = codigoPartido;
        this.partidoInfo = partidoInfo;

        configurarDialogo();
        cargarDatosLocalidades();
        inicializarComponentes();
        cargarAsientos();
    }

    private void configurarDialogo() {
        setSize(1100, 850);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void cargarDatosLocalidades() {
        List<LocalidadPartido> locs = cliente.obtenerLocalidades(codigoPartido);
        if (locs != null) {
            for (LocalidadPartido l : locs) {
                if ("PALCO".equalsIgnoreCase(l.getCodigoLocalidad())) {
                    precioPalco = l.getPrecio();
                    idPalcoLoc = l.getId();
                } else if ("TRIBUNA".equalsIgnoreCase(l.getCodigoLocalidad())) {
                    precioTribuna = l.getPrecio();
                    idTribunaLoc = l.getId();
                } else if ("GENERAL".equalsIgnoreCase(l.getCodigoLocalidad())) {
                    precioGeneral = l.getPrecio();
                    idGeneralLoc = l.getId();
                }
            }
        }
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // --- PANEL SUPERIOR: Información y Leyenda ---
        JPanel panelNorte = new JPanel(new GridLayout(2, 1, 5, 5));
        panelNorte.setBackground(new Color(15, 23, 42));
        panelNorte.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblInfo = new JLabel("Partido: " + partidoInfo, JLabel.CENTER);
        lblInfo.setFont(new Font("Inter", Font.BOLD, 18));
        lblInfo.setForeground(Color.WHITE);

        JPanel panelLeyenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelLeyenda.setOpaque(false);
        panelLeyenda.add(crearItemLeyenda("Disponible", new Color(16, 185, 129)));
        panelLeyenda.add(crearItemLeyenda("Seleccionado", new Color(251, 191, 36)));
        panelLeyenda.add(crearItemLeyenda("Ocupado", new Color(239, 68, 68)));
        panelNorte.add(lblInfo);
        panelNorte.add(panelLeyenda);
        add(panelNorte, BorderLayout.NORTH);

        // --- PANEL CENTRAL: Estadio ---
        panelEstadio = new JPanel(new GridBagLayout());
        panelEstadio.setBackground(new Color(30, 41, 59));
        panelEstadio.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panelEstadio, BorderLayout.CENTER);

        // --- PANEL DERECHO: Facturación / Compra ---
        JPanel panelFactura = new JPanel(new GridBagLayout());
        panelFactura.setPreferredSize(new Dimension(320, 600));
        panelFactura.setBackground(new Color(15, 23, 42));
        panelFactura.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;

        JLabel lblFactTitle = new JLabel("🛒 Resumen de Compra", JLabel.CENTER);
        lblFactTitle.setFont(new Font("Inter", Font.BOLD, 18));
        lblFactTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        panelFactura.add(lblFactTitle, gbc);

        lblTotal = new JLabel("Total Selección: $0.00", JLabel.LEFT);
        lblTotal.setFont(new Font("Inter", Font.BOLD, 15));
        lblTotal.setForeground(new Color(16, 185, 129));
        gbc.gridy = 1;
        panelFactura.add(lblTotal, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));
        gbc.gridy = 2;
        panelFactura.add(sep, gbc);

        JLabel lblCedula = new JLabel("Cédula del Comprador:");
        lblCedula.setForeground(new Color(148, 163, 184));
        lblCedula.setFont(new Font("Inter", Font.BOLD, 12));
        gbc.gridy = 3;
        panelFactura.add(lblCedula, gbc);

        txtCedula = new JTextField();
        txtCedula.setPreferredSize(new Dimension(200, 32));
        txtCedula.setFont(new Font("Inter", Font.PLAIN, 13));
        gbc.gridy = 4;
        panelFactura.add(txtCedula, gbc);

        JLabel lblNombre = new JLabel("Nombre Ocupante Base:");
        lblNombre.setForeground(new Color(148, 163, 184));
        lblNombre.setFont(new Font("Inter", Font.BOLD, 12));
        gbc.gridy = 5;
        panelFactura.add(lblNombre, gbc);

        txtNombre = new JTextField();
        txtNombre.setPreferredSize(new Dimension(200, 32));
        txtNombre.setFont(new Font("Inter", Font.PLAIN, 13));
        gbc.gridy = 6;
        panelFactura.add(txtNombre, gbc);

        JLabel lblPago = new JLabel("Forma de Pago:");
        lblPago.setForeground(new Color(148, 163, 184));
        lblPago.setFont(new Font("Inter", Font.BOLD, 12));
        gbc.gridy = 7;
        panelFactura.add(lblPago, gbc);

        cmbFormaPago = new JComboBox<>(new String[]{"Efectivo (12% desc.)", "Crédito Directo"});
        cmbFormaPago.setPreferredSize(new Dimension(200, 32));
        cmbFormaPago.setFont(new Font("Inter", Font.PLAIN, 13));
        gbc.gridy = 8;
        panelFactura.add(cmbFormaPago, gbc);

        JLabel lblPlazo = new JLabel("Plazo (meses):");
        lblPlazo.setForeground(new Color(148, 163, 184));
        lblPlazo.setFont(new Font("Inter", Font.BOLD, 12));
        gbc.gridy = 9;
        panelFactura.add(lblPlazo, gbc);

        cmbPlazo = new JComboBox<>(new Integer[]{3, 6, 9, 12, 18});
        cmbPlazo.setPreferredSize(new Dimension(200, 32));
        cmbPlazo.setFont(new Font("Inter", Font.PLAIN, 13));
        cmbPlazo.setEnabled(false);
        gbc.gridy = 10;
        panelFactura.add(cmbPlazo, gbc);

        cmbFormaPago.addActionListener(e -> {
            cmbPlazo.setEnabled(cmbFormaPago.getSelectedIndex() == 1);
        });

        btnComprar = new JButton("Confirmar y Comprar");
        btnComprar.setBackground(new Color(16, 185, 129));
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setFont(new Font("Inter", Font.BOLD, 14));
        btnComprar.setPreferredSize(new Dimension(200, 40));
        gbc.gridy = 11;
        gbc.insets = new Insets(20, 0, 0, 0);
        panelFactura.add(btnComprar, gbc);

        btnComprar.addActionListener(e -> realizarCompra());

        add(panelFactura, BorderLayout.EAST);
    }

    private JPanel crearItemLeyenda(String labelText, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JPanel box = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
            }
        };
        box.setPreferredSize(new Dimension(16, 16));
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Inter", Font.PLAIN, 12));
        p.add(box);
        p.add(lbl);
        return p;
    }

    private void cargarAsientos() {
        todosAsientos = cliente.obtenerAsientosPartido(codigoPartido);
        renderizarEstadio();
    }

    private void renderizarEstadio() {
        panelEstadio.removeAll();

        // 1. Clasificar asientos por sección
        List<AsientoPartido> norte = new ArrayList<>();
        List<AsientoPartido> sur = new ArrayList<>();
        List<AsientoPartido> palco = new ArrayList<>();
        List<AsientoPartido> tribuna = new ArrayList<>();

        int maxGeneral = 0;
        for (AsientoPartido a : todosAsientos) {
            if ("GENERAL".equalsIgnoreCase(a.getSeccion())) {
                if (a.getNumero() > maxGeneral) maxGeneral = a.getNumero();
            }
        }
        int midGeneral = maxGeneral / 2;

        for (AsientoPartido a : todosAsientos) {
            if ("GENERAL".equalsIgnoreCase(a.getSeccion())) {
                if (a.getNumero() <= midGeneral) norte.add(a);
                else sur.add(a);
            } else if ("PALCO".equalsIgnoreCase(a.getSeccion())) {
                palco.add(a);
            } else if ("TRIBUNA".equalsIgnoreCase(a.getSeccion())) {
                tribuna.add(a);
            }
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- General Norte ---
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelEstadio.add(crearSeccionPanel("General Norte (1 a " + midGeneral + ")", norte, new GridLayout(2, 6, 4, 4)), gbc);

        // --- Palco (VIP) ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panelEstadio.add(crearSeccionPanel("Palco (VIP)", palco, new GridLayout(5, 2, 4, 4)), gbc);

        // --- Cancha de Fútbol Central ---
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        CanchaPanel canchaPanel = new CanchaPanel();
        canchaPanel.setPreferredSize(new Dimension(300, 180));
        panelEstadio.add(canchaPanel, gbc);

        // --- Tribuna ---
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panelEstadio.add(crearSeccionPanel("Tribuna", tribuna, new GridLayout(5, 3, 4, 4)), gbc);

        // --- General Sur ---
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelEstadio.add(crearSeccionPanel("General Sur (" + (midGeneral + 1) + " a " + maxGeneral + ")", sur, new GridLayout(2, 7, 4, 4)), gbc);

        panelEstadio.revalidate();
        panelEstadio.repaint();
    }

    private JPanel crearSeccionPanel(String titulo, List<AsientoPartido> asientos, LayoutManager layout) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(titulo, JLabel.CENTER);
        lbl.setFont(new Font("Inter", Font.BOLD, 12));
        lbl.setForeground(new Color(203, 213, 225));
        panel.add(lbl, BorderLayout.NORTH);

        JPanel panelGrid = new JPanel(layout);
        panelGrid.setOpaque(false);

        for (AsientoPartido a : asientos) {
            JButton btnSeat = new JButton(FormatUtil.tilde(
                "PALCO".equalsIgnoreCase(a.getSeccion()) ? "P" + a.getNumero() :
                "TRIBUNA".equalsIgnoreCase(a.getSeccion()) ? "T" + a.getNumero() :
                "" + a.getNumero()
            ));
            btnSeat.setPreferredSize(new Dimension(34, 34));
            btnSeat.setFont(new Font("Inter", Font.BOLD, 10));
            btnSeat.setMargin(new Insets(0, 0, 0, 0));
            btnSeat.setFocusPainted(false);

            Color colorDisponible = new Color(16, 185, 129);
            Color colorOcupado = new Color(239, 68, 68);
            Color colorSeleccionado = new Color(251, 191, 36);

            String estado = a.getEstado().toUpperCase();
            if ("OCUPADO".equals(estado)) {
                btnSeat.setBackground(colorOcupado);
                btnSeat.setForeground(Color.WHITE);
                
                String tooltip = "<html><b>Asiento Ocupado</b><br/>"
                        + "Ocupante: " + a.getNombreOcupante() + "<br/>"
                        + "Comprador: " + a.getNombreCliente() + "<br/>"
                        + "Factura: #" + a.getIdFactura() + "<br/>"
                        + "Fecha: " + a.getFechaCompra() + "<br/>"
                        + "Total Factura: $" + String.format("%.2f", a.getTotalFactura()) + "</html>";
                btnSeat.setToolTipText(tooltip);

                btnSeat.addActionListener(e -> {
                    String msg = "🎫 INFORMACIÓN DE BOLETO\n"
                            + "--------------------------------\n"
                            + "Localidad: " + a.getSeccion() + "\n"
                            + "Fila-N°:   " + a.getFila() + "-" + a.getNumero() + "\n"
                            + "Ocupante:  " + a.getNombreOcupante() + "\n"
                            + "Comprador: " + a.getNombreCliente() + "\n"
                            + "Factura N°: #" + a.getIdFactura() + "\n"
                            + "Fecha Compra: " + a.getFechaCompra() + "\n"
                            + "Total Factura: $" + String.format("%.2f", a.getTotalFactura());
                    JOptionPane.showMessageDialog(this, msg, "Detalles del Boleto", JOptionPane.INFORMATION_MESSAGE);
                });
            } else if ("RESERVADO".equals(estado)) {
                btnSeat.setBackground(new Color(217, 119, 6));
                btnSeat.setForeground(Color.WHITE);
                btnSeat.setToolTipText("Asiento temporalmente reservado.");
                btnSeat.setEnabled(false);
            } else {
                btnSeat.setBackground(colorDisponible);
                btnSeat.setForeground(Color.WHITE);
                btnSeat.setToolTipText("Asiento disponible.");

                btnSeat.addActionListener(e -> {
                    if (asientosSeleccionados.contains(a)) {
                        asientosSeleccionados.remove(a);
                        btnSeat.setBackground(colorDisponible);
                    } else {
                        asientosSeleccionados.add(a);
                        btnSeat.setBackground(colorSeleccionado);
                    }
                    actualizarTotal();
                });
            }
            panelGrid.add(btnSeat);
        }

        panel.add(panelGrid, BorderLayout.CENTER);
        return panel;
    }

    private void actualizarTotal() {
        double subtotal = 0;
        for (AsientoPartido a : asientosSeleccionados) {
            subtotal += a.getPrecio();
        }
        double total = subtotal * 1.15; // subtotal + iva
        lblTotal.setText(String.format("Total Selección: $%.2f (IVA incl.)", total));
    }

    private void realizarCompra() {
        if (asientosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un asiento libre.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar cédula y nombre.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String formaPago = "EFECTIVO";
        Integer idCredito = null;

        // Armar lista de Peticiones
        List<PeticionCompra> carrito = new ArrayList<>();
        for (AsientoPartido a : asientosSeleccionados) {
            PeticionCompra p = new PeticionCompra();
            p.setCodigoPartido(codigoPartido);
            p.setCantidad(1);
            p.setCodigoLocalidad(a.getSeccion());
            p.setPrecioUnitario(a.getPrecio());
            p.setIdAsientoPartido(a.getIdAsientoPartido());
            p.setNombreOcupante(nombre);

            int idLoc = 0;
            if ("PALCO".equalsIgnoreCase(a.getSeccion())) idLoc = idPalcoLoc;
            else if ("TRIBUNA".equalsIgnoreCase(a.getSeccion())) idLoc = idTribunaLoc;
            else if ("GENERAL".equalsIgnoreCase(a.getSeccion())) idLoc = idGeneralLoc;
            p.setIdLocalidad(idLoc);
            
            carrito.add(p);
        }

        if (cmbFormaPago.getSelectedIndex() == 1) {
            formaPago = "CREDITO";
            int plazo = (int) cmbPlazo.getSelectedItem();

            double subtotal = 0;
            for (PeticionCompra p : carrito) {
                subtotal += p.getCantidad() * p.getPrecioUnitario();
            }
            double total = Math.round(subtotal * 1.15 * 100.0) / 100.0;

            BancoSoapClient banco = new BancoSoapClient();
            ec.edu.monster.modelo.RespuestaCredito res = banco.verificarYCrearCredito(cedula, total, plazo);

            if (!res.isAprobado()) {
                JOptionPane.showMessageDialog(this, "Crédito Bancario Rechazado:\n" + res.getMensaje(), "Crédito Rechazado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            idCredito = res.getIdCredito();

            // Mostrar tabla de amortización
            if (res.getTablaAmortizacion() != null && !res.getTablaAmortizacion().isEmpty()) {
                StringBuilder tablaText = new StringBuilder();
                tablaText.append(String.format("%-8s %-12s %-12s %-14s %-10s\n", "# Cuota", "Val. Cuota", "Interés", "Capital", "Saldo"));
                tablaText.append("-".repeat(60)).append("\n");
                for (ec.edu.monster.modelo.CuotaAmortizacion c : res.getTablaAmortizacion()) {
                    if (c.getNumCuota() == 0) continue;
                    tablaText.append(String.format("%-8d $%-11.2f $%-11.2f $%-13.2f $%-9.2f\n",
                            c.getNumCuota(), c.getValorCuota(), c.getInteresPagado(), c.getCapitalPagado(), c.getSaldo()));
                }

                JTextArea txtArea = new JTextArea(tablaText.toString());
                txtArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                txtArea.setEditable(false);
                JOptionPane.showMessageDialog(this, new JScrollPane(txtArea), "✅ Crédito Aprobado - Tabla de Amortización", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        Factura factura = cliente.comprarBoletosMulti(cedula, carrito, formaPago, idCredito);

        if (factura == null) {
            JOptionPane.showMessageDialog(this, "Error al realizar la compra. Verifique que no se hayan ocupado los asientos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder msg = new StringBuilder();
        msg.append("========= FACTURA =========\n")
           .append("N° Factura: ").append(factura.getId()).append("\n")
           .append("Cliente:    ").append(factura.getNombreCliente()).append("\n")
           .append("Forma Pago: ").append(formaPago).append("\n")
           .append("----------------------------\n");
           
        for (AsientoPartido a : asientosSeleccionados) {
            msg.append("1x ").append(a.getSeccion())
               .append(" - Fila ").append(a.getFila())
               .append(" N°").append(a.getNumero())
               .append(" ($").append(String.format("%.2f", a.getPrecio())).append(")\n");
        }
        
        msg.append("----------------------------\n")
           .append("Subtotal:  $").append(String.format("%.2f", factura.getSubtotal())).append("\n");
        if (factura.getDescuento() > 0) {
            msg.append("Descuento: -$").append(String.format("%.2f", factura.getDescuento())).append("\n");
        }
        msg.append("IVA (15%): $").append(String.format("%.2f", factura.getIva())).append("\n")
           .append("TOTAL:     $").append(String.format("%.2f", factura.getTotal())).append("\n")
           .append("===========================");

        JOptionPane.showMessageDialog(this, msg.toString(), "Compra Exitosa", JOptionPane.INFORMATION_MESSAGE);
        
        // Limpiar selección y recargar
        asientosSeleccionados.clear();
        actualizarTotal();
        cargarAsientos();
    }

    // --- PANEL CANCHA DE FÚTBOL ---
    private static class CanchaPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Fondo de la cancha
            g2d.setColor(new Color(4, 108, 78));
            g2d.fillRoundRect(0, 0, w, h, 10, 10);

            // Líneas blancas
            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.setStroke(new BasicStroke(2));

            // Borde exterior
            g2d.drawRect(5, 5, w - 10, h - 10);

            // Línea central
            g2d.drawLine(w / 2, 5, w / 2, h - 5);

            // Círculo central
            g2d.drawOval(w / 2 - 25, h / 2 - 25, 50, 50);

            // Área penal izquierda
            g2d.drawRect(5, h / 2 - 35, 30, 70);

            // Área penal derecha
            g2d.drawRect(w - 35, h / 2 - 35, 30, 70);

            // Texto FIFA 2026
            g2d.setFont(new Font("Inter", Font.BOLD, 12));
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.drawString("FIFA 2026", w / 2 - 28, h / 2 + 5);
        }
    }
}
