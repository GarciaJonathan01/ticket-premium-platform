package ec.edu.monster.vista;

import ec.edu.monster.servicio.ClienteFederacion;
import ec.edu.monster.servicio.BancoSoapClient;
import ec.edu.monster.ws.generated.*;
import ec.edu.monster.util.FormatUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Interfaz gráfica de escritorio (Swing) de TicketPremium.
 * Funcionalidades: ver partidos, ver localidades, comprar boletos, reporte.
 */
public class VentanaPrincipal extends JFrame {

    private final ClienteFederacion cliente = new ClienteFederacion();

    // Componentes - Panel Partidos
    private JTable tblPartidos;
    private DefaultTableModel modelPartidos;
    private JButton btnVerLocalidades, btnReporte, btnRefrescar;

    // Componentes - Panel Localidades
    private JTable tblLocalidades;
    private DefaultTableModel modelLocalidades;
    private JLabel lblPartidoSeleccionado;
    private JButton btnComprar;

    // Componentes - Panel Reporte
    private JTable tblReporte;
    private DefaultTableModel modelReporte;
    private JLabel lblInfoReporte;

    private int partidoSeleccionado = -1;

    public VentanaPrincipal() {
        configurarVentana();
        inicializarComponentes();
        cargarPartidos();
    }

    private void configurarVentana() {
        setTitle("🎟️ TicketPremium - Sistema de Venta de Boletos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setFont(new Font("Inter", Font.PLAIN, 14));
    }

    private void inicializarComponentes() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Inter", Font.BOLD, 14));
        tabbedPane.putClientProperty("JTabbedPane.tabHeight", 40);

        Font headerFont = new Font("Inter", Font.BOLD, 18);
        Font tableFont = new Font("Inter", Font.PLAIN, 14);
        Font btnFont = new Font("Inter", Font.BOLD, 13);
        Color btnPrimaryColor = new Color(99, 102, 241); // Indigo
        Color btnSuccessColor = new Color(16, 185, 129); // Emerald

        // ============ TAB 1: PARTIDOS ============
        JPanel panelPartidos = new JPanel(new BorderLayout(15, 15));
        panelPartidos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        modelPartidos = new DefaultTableModel(
                new String[]{"Código", "Equipo Local", "Equipo Visita", "Fecha", "Lugar"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPartidos = new JTable(modelPartidos);
        tblPartidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPartidos.setRowHeight(30);
        tblPartidos.setFont(tableFont);
        tblPartidos.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));

        JPanel panelBotonesPartidos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnRefrescar = new JButton("🔄 Refrescar Partidos");
        btnRefrescar.setFont(btnFont);
        
        btnVerLocalidades = new JButton("🏟️ Ver Localidades");
        btnVerLocalidades.setFont(btnFont);
        btnVerLocalidades.setBackground(btnPrimaryColor);
        btnVerLocalidades.setForeground(Color.WHITE);
        
        btnReporte = new JButton("📊 Ver Reporte");
        btnReporte.setFont(btnFont);
        
        panelBotonesPartidos.add(btnRefrescar);
        panelBotonesPartidos.add(btnReporte);
        panelBotonesPartidos.add(btnVerLocalidades);

        JLabel lblTituloPartidos = new JLabel("⚽ Partidos de Fútbol Disponibles", JLabel.LEFT);
        lblTituloPartidos.setFont(headerFont);
        lblTituloPartidos.setForeground(new Color(129, 140, 248));
        
        panelPartidos.add(lblTituloPartidos, BorderLayout.NORTH);
        panelPartidos.add(new JScrollPane(tblPartidos), BorderLayout.CENTER);
        panelPartidos.add(panelBotonesPartidos, BorderLayout.SOUTH);

        // ============ TAB 2: LOCALIDADES ============
        JPanel panelLocalidades = new JPanel(new BorderLayout(15, 15));
        panelLocalidades.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblPartidoSeleccionado = new JLabel("Seleccione un partido en la pestaña de Partidos", JLabel.LEFT);
        lblPartidoSeleccionado.setFont(headerFont);
        lblPartidoSeleccionado.setForeground(btnSuccessColor);

        modelLocalidades = new DefaultTableModel(
                new String[]{"ID", "Localidad", "Disponibles", "Precio ($)", "Cant. a Comprar"}, 0) {
            public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        tblLocalidades = new JTable(modelLocalidades);
        tblLocalidades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLocalidades.setRowHeight(30);
        tblLocalidades.setFont(tableFont);
        tblLocalidades.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));

        JPanel panelBotonesLoc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        JButton btnVerMapa = new JButton("🏟️ Selección Gráfica");
        btnVerMapa.setBackground(btnPrimaryColor);
        btnVerMapa.setForeground(Color.WHITE);
        btnVerMapa.setFont(btnFont);
        
        btnComprar = new JButton("🛒 Comprar Boletos");
        btnComprar.setBackground(btnSuccessColor);
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setFont(btnFont);
        
        panelBotonesLoc.add(btnVerMapa);
        panelBotonesLoc.add(btnComprar);

        panelLocalidades.add(lblPartidoSeleccionado, BorderLayout.NORTH);
        panelLocalidades.add(new JScrollPane(tblLocalidades), BorderLayout.CENTER);
        panelLocalidades.add(panelBotonesLoc, BorderLayout.SOUTH);

        // ============ TAB 3: REPORTE ============
        JPanel panelReporte = new JPanel(new BorderLayout(15, 15));
        panelReporte.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblInfoReporte = new JLabel("📈 Reporte de Ventas", JLabel.LEFT);
        lblInfoReporte.setFont(headerFont);
        lblInfoReporte.setForeground(new Color(167, 139, 250)); // Purple

        modelReporte = new DefaultTableModel(
                new String[]{"Fecha", "Cliente", "Localidad(es)", "Boletos", "Total ($)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblReporte = new JTable(modelReporte);
        tblReporte.setRowHeight(30);
        tblReporte.setFont(tableFont);
        tblReporte.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));

        panelReporte.add(lblInfoReporte, BorderLayout.NORTH);
        panelReporte.add(new JScrollPane(tblReporte), BorderLayout.CENTER);

        // Agregar tabs
        tabbedPane.addTab("Partidos", panelPartidos);
        tabbedPane.addTab("Localidades", panelLocalidades);
        tabbedPane.addTab("Reporte de Ventas", panelReporte);
        add(tabbedPane);

        // ============ EVENTOS ============
        btnRefrescar.addActionListener(e -> cargarPartidos());

        btnVerLocalidades.addActionListener(e -> {
            int row = tblPartidos.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un partido."); return; }
            partidoSeleccionado = (int) modelPartidos.getValueAt(row, 0);
            String info = modelPartidos.getValueAt(row, 1) + " vs " + modelPartidos.getValueAt(row, 2);
            lblPartidoSeleccionado.setText("Localidades para: " + info);
            cargarLocalidades(partidoSeleccionado);
            tabbedPane.setSelectedIndex(1);
        });

        btnComprar.addActionListener(e -> realizarCompra());

        btnVerMapa.addActionListener(e -> {
            if (partidoSeleccionado < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un partido primero en la pestaña de Partidos o haga clic en Ver Localidades.");
                return;
            }
            int row = tblPartidos.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un partido primero en la pestaña de Partidos.");
                return;
            }
            String info = modelPartidos.getValueAt(row, 1) + " vs " + modelPartidos.getValueAt(row, 2);
            MapaAsientosDialog dialog = new MapaAsientosDialog(this, partidoSeleccionado, info);
            dialog.setVisible(true);
            cargarLocalidades(partidoSeleccionado);
        });

        btnReporte.addActionListener(e -> {
            int row = tblPartidos.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un partido."); return; }
            int cod = (int) modelPartidos.getValueAt(row, 0);
            cargarReporte(cod, modelPartidos.getValueAt(row, 1) + " vs " + modelPartidos.getValueAt(row, 2),
                    (String) modelPartidos.getValueAt(row, 3));
            tabbedPane.setSelectedIndex(2);
        });
    }

    private void cargarPartidos() {
        modelPartidos.setRowCount(0);
        List<PartidoFutbol> partidos = cliente.obtenerPartidosDisponibles();
        if (partidos != null) {
            for (PartidoFutbol p : partidos) {
                modelPartidos.addRow(new Object[]{
                        p.getCodigo(), FormatUtil.tilde(p.getEquipoLocal()), FormatUtil.tilde(p.getEquipoVisita()),
                        p.getFecha(), FormatUtil.tilde(p.getLugar())
                });
            }
        }
    }

    private void cargarLocalidades(int codigoPartido) {
        modelLocalidades.setRowCount(0);
        List<LocalidadPartido> locs = cliente.obtenerLocalidades(codigoPartido);
        if (locs != null) {
            for (LocalidadPartido l : locs) {
                modelLocalidades.addRow(new Object[]{
                        l.getId(), FormatUtil.tilde(l.getCodigoLocalidad()), l.getDisponibilidad(),
                        String.format("%.2f", l.getPrecio()), "0"
                });
            }
        }
    }

    private void realizarCompra() {
        if (partidoSeleccionado < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un partido primero.");
            return;
        }

        // Detener edición de celda si está activa
        if (tblLocalidades.isEditing()) {
            tblLocalidades.getCellEditor().stopCellEditing();
        }

        java.util.List<PeticionCompra> carrito = new java.util.ArrayList<>();
        
        for (int i = 0; i < modelLocalidades.getRowCount(); i++) {
            Object val = modelLocalidades.getValueAt(i, 4);
            int cantidad = 0;
            try {
                if (val != null) cantidad = Integer.parseInt(val.toString());
            } catch (NumberFormatException ignored) {}
            
            if (cantidad > 0) {
                int idLoc = (int) modelLocalidades.getValueAt(i, 0);
                String codLoc = (String) modelLocalidades.getValueAt(i, 1);
                int disp = (int) modelLocalidades.getValueAt(i, 2);
                double precio = Double.parseDouble(modelLocalidades.getValueAt(i, 3).toString().replace(",", "."));
                
                if (cantidad > disp) {
                    JOptionPane.showMessageDialog(this, "Cantidad excede disponibilidad para " + codLoc);
                    return;
                }
                
                PeticionCompra p = new PeticionCompra();
                p.setIdLocalidad(idLoc);
                p.setCodigoLocalidad(codLoc);
                p.setCantidad(cantidad);
                p.setPrecioUnitario(precio);
                carrito.add(p);
            }
        }

        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una cantidad mayor a 0 en al menos una localidad.");
            return;
        }

        // Diseñar panel de checkout dinámico
        JTextField txtCedula = new JTextField(15);
        JTextField txtNombre = new JTextField(15);
        JComboBox<String> cmbFormaPago = new JComboBox<>(new String[]{"Efectivo (12% desc.)", "Crédito Directo"});
        JComboBox<Integer> cmbPlazo = new JComboBox<>(new Integer[]{3, 6, 9, 12, 18});
        cmbPlazo.setEnabled(false);

        cmbFormaPago.addActionListener(evt -> {
            cmbPlazo.setEnabled(cmbFormaPago.getSelectedIndex() == 1);
        });

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Cédula:"));
        panel.add(txtCedula);
        panel.add(new JLabel("Nombre Ocupante:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Forma de Pago:"));
        panel.add(cmbFormaPago);
        panel.add(new JLabel("Plazo (meses):"));
        panel.add(cmbPlazo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Confirmar Compra", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar cédula y nombre.");
            return;
        }

        String formaPago = "EFECTIVO";
        Integer idCredito = null;

        if (cmbFormaPago.getSelectedIndex() == 1) {
            formaPago = "CREDITO";
            int plazo = (int) cmbPlazo.getSelectedItem();

            // Calcular total a financiar
            double subtotal = 0;
            for (PeticionCompra p : carrito) {
                subtotal += p.getCantidad() * p.getPrecioUnitario();
            }
            double total = Math.round(subtotal * 1.15 * 100.0) / 100.0;

            // Consultar el banco
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

        // Configurar codigoPartido y nombre en peticiones
        for (PeticionCompra p : carrito) {
            p.setCodigoPartido(partidoSeleccionado);
            p.setIdAsientoPartido(0);
            p.setNombreOcupante(nombre);
        }

        Factura factura = cliente.comprarBoletosMulti(cedula, carrito, formaPago, idCredito);

        if (factura == null) {
            JOptionPane.showMessageDialog(this, "Error al realizar la compra. Verifique disponibilidad.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder msg = new StringBuilder();
        msg.append("========= FACTURA =========\n")
           .append("N° Factura: ").append(factura.getId()).append("\n")
           .append("Cliente:    ").append(factura.getNombreCliente()).append("\n")
           .append("Forma Pago: ").append(formaPago).append("\n")
           .append("----------------------------\n");
           
        for (PeticionCompra p : carrito) {
            msg.append(p.getCantidad()).append("x ").append(p.getCodigoLocalidad())
               .append(" ($").append(String.format("%.2f", p.getPrecioUnitario())).append(" c/u)\n");
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
        cargarLocalidades(partidoSeleccionado); // refrescar
    }

    private void cargarReporte(int codigoPartido, String vsText, String fecha) {
        modelReporte.setRowCount(0);
        lblInfoReporte.setText("Partido: " + vsText + "  |  Fecha: " + fecha);
        
        // Primero cargamos el detalle de las ventas
        List<DetalleVentaReporte> detalles = cliente.obtenerDetalleVentas(codigoPartido);
        if (detalles != null) {
            for (DetalleVentaReporte d : detalles) {
                modelReporte.addRow(new Object[]{
                        d.getFecha(), d.getCliente(), FormatUtil.tilde(d.getLocalidades()),
                        d.getBoletosTotales(), String.format("$%.2f", d.getTotalVenta())
                });
            }
        }
        
        // Agregar resumen al final como filas extras para no crear otra tabla
        List<ResumenVenta> resumen = cliente.obtenerResumenVentas(codigoPartido);
        if (resumen != null && !resumen.isEmpty()) {
            modelReporte.addRow(new Object[]{"", "", "", "", ""});
            modelReporte.addRow(new Object[]{"RESUMEN", "POR LOCALIDAD", "", "", ""});
            for (ResumenVenta r : resumen) {
                modelReporte.addRow(new Object[]{
                        FormatUtil.tilde(r.getCodigoLocalidad()), r.getVendidos() + " vendidos", "", "", 
                        String.format("$%.2f", r.getTotalRecaudado())
                });
            }
        }
    }
}
