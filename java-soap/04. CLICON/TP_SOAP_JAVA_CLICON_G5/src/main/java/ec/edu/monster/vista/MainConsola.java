package ec.edu.monster.vista;

import ec.edu.monster.servicio.ClienteFederacion;
import ec.edu.monster.servicio.BancoSoapClient;
import ec.edu.monster.modelo.RespuestaCredito;
import ec.edu.monster.ws.generated.*;
import ec.edu.monster.util.FormatUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Cliente de Consola de TicketPremium.
 * Permite: ver partidos, ver localidades, comprar boletos y ver reporte.
 */
public class MainConsola {

    private static final ClienteFederacion cliente = new ClienteFederacion();
    private static final Scanner sc = new Scanner(System.in);

    // ANSI Colors for premium console design
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";

    public static void main(String[] args) {
        System.out.println(CYAN + BOLD + "=========================================================" + RESET);
        System.out.println(CYAN + BOLD + "   🎟️  TICKET PREMIUM - Sistema de Venta de Boletos" + RESET);
        System.out.println(CYAN + BOLD + "=========================================================" + RESET);

        // --- LOGIN ---
        boolean logueado = false;
        while (!logueado) {
            System.out.println("\n" + BLUE + BOLD + "--- INICIAR SESIÓN ---" + RESET);
            System.out.print(YELLOW + "Usuario: " + RESET);
            String u = sc.nextLine();
            System.out.print(YELLOW + "Clave: " + RESET);
            String c = sc.nextLine();

            Usuario usr = cliente.login(u, c);
            if (usr != null) {
                System.out.println(GREEN + BOLD + "¡Bienvenido, " + usr.getUsername() + "!" + RESET);
                logueado = true;
            } else {
                System.out.println(RED + "Credenciales incorrectas. Intente nuevamente." + RESET);
            }
        }

        int opcion;
        do {
            System.out.println("\n" + BLUE + BOLD + "--- MENÚ PRINCIPAL ---" + RESET);
            System.out.println(GREEN + "1. " + RESET + "Ver partidos disponibles");
            System.out.println(GREEN + "2. " + RESET + "Ver localidades de un partido");
            System.out.println(GREEN + "3. " + RESET + "Comprar boletos");
            System.out.println(GREEN + "4. " + RESET + "Reporte: Resumen de Ventas");
            System.out.println(RED + "0. " + RESET + "Salir");
            System.out.print(YELLOW + "Opción: " + RESET);
            opcion = leerInt();

            switch (opcion) {
                case 1: mostrarPartidos(); break;
                case 2: mostrarLocalidades(); break;
                case 3: comprarBoletos(); break;
                case 4: mostrarReporte(); break;
                case 0: System.out.println(PURPLE + BOLD + "¡Hasta luego!" + RESET); break;
                default: System.out.println(RED + "Opción no válida." + RESET);
            }
        } while (opcion != 0);
    }

    private static void mostrarPartidos() {
        List<PartidoFutbol> partidos = cliente.obtenerPartidosDisponibles();
        if (partidos == null || partidos.isEmpty()) {
            System.out.println(RED + "No hay partidos disponibles." + RESET);
            return;
        }
        System.out.println("\n" + CYAN + BOLD + "⚽ PARTIDOS DISPONIBLES" + RESET);
        System.out.println(BLUE + "-".repeat(120) + RESET);
        System.out.printf(BLUE + BOLD + "%-6s %-25s %-25s %-20s %-40s%n" + RESET,
                "COD", "EQUIPO LOCAL", "EQUIPO VISITA", "FECHA", "LUGAR");
        System.out.println(BLUE + "-".repeat(120) + RESET);
        for (PartidoFutbol p : partidos) {
            System.out.printf("%-6d " + GREEN + "%-25s" + RESET + " " + RED + "vs" + RESET + " " + GREEN + "%-22s" + RESET + " %-20s %-40s%n",
                    p.getCodigo(), FormatUtil.tilde(p.getEquipoLocal()), FormatUtil.tilde(p.getEquipoVisita()),
                    p.getFecha(), FormatUtil.tilde(p.getLugar()));
        }
        System.out.println(BLUE + "-".repeat(120) + RESET);
    }

    private static void mostrarLocalidades() {
        System.out.print(YELLOW + "Ingrese código del partido: " + RESET);
        int cod = leerInt();
        List<LocalidadPartido> locs = cliente.obtenerLocalidades(cod);
        if (locs == null || locs.isEmpty()) {
            System.out.println(RED + "No hay localidades disponibles para ese partido." + RESET);
            return;
        }
        System.out.println("\n" + GREEN + BOLD + "🏟️ LOCALIDADES DISPONIBLES" + RESET);
        System.out.println(GREEN + "-".repeat(60) + RESET);
        System.out.printf(GREEN + BOLD + "%-6s %-20s %-15s %-10s%n" + RESET, "ID", "LOCALIDAD", "DISPONIBLES", "PRECIO");
        System.out.println(GREEN + "-".repeat(60) + RESET);
        for (LocalidadPartido l : locs) {
            System.out.printf("%-6d %-20s %-15d " + YELLOW + "$%-9.2f%n" + RESET,
                    l.getId(), FormatUtil.tilde(l.getCodigoLocalidad()), l.getDisponibilidad(), l.getPrecio());
        }
        System.out.println(GREEN + "-".repeat(60) + RESET);
    }

    private static void comprarBoletos() {
        mostrarPartidos();
        System.out.print("\n" + YELLOW + "Código del partido: " + RESET);
        int codPartido = leerInt();

        List<LocalidadPartido> locs = cliente.obtenerLocalidades(codPartido);
        if (locs == null || locs.isEmpty()) {
            System.out.println(RED + "No hay localidades disponibles." + RESET);
            return;
        }

        sc.nextLine(); // limpiar buffer
        System.out.print(YELLOW + "Cédula del cliente: " + RESET);
        String cedula = sc.nextLine().trim();
        if (cedula.isEmpty()) cedula = "1726354712";

        System.out.print(YELLOW + "Nombre del cliente: " + RESET);
        String nombre = sc.nextLine();

        List<PeticionCompra> carrito = new ArrayList<>();
        boolean seguirComprando = true;

        while (seguirComprando) {
            System.out.println("\n" + GREEN + BOLD + "Localidades Disponibles:" + RESET);
            for (LocalidadPartido l : locs) {
                System.out.printf("  ID: %d | %s | Disponibles: %d | Precio: " + YELLOW + "$%.2f%n" + RESET,
                        l.getId(), FormatUtil.tilde(l.getCodigoLocalidad()), l.getDisponibilidad(), l.getPrecio());
            }

            System.out.print(YELLOW + "ID de localidad a comprar (0 para finalizar selección): " + RESET);
            int idLoc = leerInt();
            if (idLoc == 0) break;

            LocalidadPartido seleccionada = null;
            for (LocalidadPartido l : locs) {
                if (l.getId() == idLoc) { seleccionada = l; break; }
            }
            if (seleccionada == null) {
                System.out.println(RED + "Localidad no encontrada." + RESET);
                continue;
            }

            System.out.print(YELLOW + "Cantidad de boletos para " + FormatUtil.tilde(seleccionada.getCodigoLocalidad()) + ": " + RESET);
            int cantidad = leerInt();

            if (cantidad > 0) {
                PeticionCompra pc = new PeticionCompra();
                pc.setIdLocalidad(idLoc);
                pc.setCodigoLocalidad(seleccionada.getCodigoLocalidad());
                pc.setCantidad(cantidad);
                pc.setPrecioUnitario(seleccionada.getPrecio());
                pc.setCodigoPartido(codPartido);
                pc.setIdAsientoPartido(0);
                pc.setNombreOcupante(nombre);
                carrito.add(pc);
                System.out.println(GREEN + "✅ Añadido al carrito." + RESET);
            }

            System.out.print(YELLOW + "¿Desea agregar otra localidad? (S/N): " + RESET);
            sc.nextLine();
            String resp = sc.nextLine();
            if (resp.equalsIgnoreCase("N")) seguirComprando = false;
        }

        if (carrito.isEmpty()) {
            System.out.println(YELLOW + "Compra cancelada. Carrito vacío." + RESET);
            return;
        }

        // Forma de pago
        System.out.println("\n" + CYAN + BOLD + "--- FORMA DE PAGO ---" + RESET);
        System.out.println(GREEN + "1. " + RESET + "Efectivo (12% descuento)");
        System.out.println(GREEN + "2. " + RESET + "Crédito Directo (financiamiento bancario)");
        System.out.print(YELLOW + "Seleccione forma de pago: " + RESET);
        int opPago = leerInt();

        String formaPago = "EFECTIVO";
        Integer idCredito = null;

        if (opPago == 2) {
            formaPago = "CREDITO";
            System.out.print(YELLOW + "Plazo en meses (3-18): " + RESET);
            int plazo = leerInt();

            double subtotal = 0;
            for (PeticionCompra p : carrito) subtotal += p.getCantidad() * p.getPrecioUnitario();
            double total = Math.round(subtotal * 1.15 * 100.0) / 100.0;

            System.out.println(CYAN + "Consultando el módulo bancario..." + RESET);
            BancoSoapClient banco = new BancoSoapClient();
            RespuestaCredito res = banco.verificarYCrearCredito(cedula, total, plazo);

            if (!res.isAprobado()) {
                System.out.println(RED + BOLD + "❌ Crédito rechazado: " + res.getMensaje() + RESET);
                return;
            }
            System.out.println(GREEN + BOLD + "✅ Crédito aprobado! Monto máximo: $" + res.getMontoMaximo() + RESET);
            idCredito = res.getIdCredito();

            // Mostrar tabla de amortizacion
            if (res.getTablaAmortizacion() != null && !res.getTablaAmortizacion().isEmpty()) {
                System.out.println("\n" + PURPLE + BOLD + "📋 TABLA DE AMORTIZACIÓN" + RESET);
                System.out.printf(PURPLE + "%-8s %-12s %-12s %-14s %-10s%n" + RESET,
                        "# Cuota", "Val. Cuota", "Interés", "Capital", "Saldo");
                System.out.println(PURPLE + "-".repeat(60) + RESET);
                for (ec.edu.monster.modelo.CuotaAmortizacion c : res.getTablaAmortizacion()) {
                    if (c.getNumCuota() == 0) continue;
                    System.out.printf("%-8d $%-11.2f $%-11.2f $%-13.2f $%-9.2f%n",
                            c.getNumCuota(), c.getValorCuota(), c.getInteresPagado(), c.getCapitalPagado(), c.getSaldo());
                }
                System.out.println(PURPLE + "-".repeat(60) + RESET);
            }
        }

        // Realizar la compra
        Factura factura = cliente.comprarBoletosMulti(cedula, carrito, formaPago, idCredito);

        if (factura == null) {
            System.out.println(RED + BOLD + "ERROR: No se pudo realizar la compra. Verifique disponibilidad." + RESET);
            return;
        }

        // Mostrar factura
        System.out.println("\n" + GREEN + BOLD + "========================================" + RESET);
        System.out.println(GREEN + BOLD + "        🧾 FACTURA DE COMPRA" + RESET);
        System.out.println(GREEN + BOLD + "========================================" + RESET);
        System.out.println("Factura N°:  " + factura.getId());
        System.out.println("Cliente:     " + BOLD + factura.getNombreCliente() + RESET);
        System.out.println("Forma pago:  " + formaPago);
        System.out.println(GREEN + "----------------------------------------" + RESET);
        System.out.printf("%-15s %-10s %-10s%n", "Localidad", "Cant", "Subtotal");
        for (PeticionCompra p : carrito) {
            System.out.printf("%-15s %-10d $%.2f%n", FormatUtil.tilde(p.getCodigoLocalidad()), p.getCantidad(), p.getCantidad() * p.getPrecioUnitario());
        }
        System.out.println(GREEN + "----------------------------------------" + RESET);
        System.out.printf("Subtotal:    " + YELLOW + "$%.2f%n" + RESET, factura.getSubtotal());
        if (factura.getDescuento() > 0) {
            System.out.printf("Descuento:   " + GREEN + "-$%.2f%n" + RESET, factura.getDescuento());
        }
        System.out.printf("IVA (15%%):   " + YELLOW + "$%.2f%n" + RESET, factura.getIva());
        System.out.printf(BOLD + "TOTAL:       " + GREEN + "$%.2f%n" + RESET, factura.getTotal());
        System.out.println(GREEN + BOLD + "========================================" + RESET);
    }

    private static void mostrarReporte() {
        System.out.print(YELLOW + "Código del partido para el reporte: " + RESET);
        int cod = leerInt();
        PartidoFutbol partido = cliente.obtenerPartido(cod);
        
        if (partido == null) {
            System.out.println(RED + "Partido no encontrado." + RESET);
            return;
        }

        System.out.println("\n" + PURPLE + BOLD + "==========================================================================================" + RESET);
        System.out.println(PURPLE + BOLD + "   📈 REPORTE DETALLADO DE VENTAS" + RESET);
        System.out.println(PURPLE + BOLD + "==========================================================================================" + RESET);
        System.out.println("Partido: " + BOLD + FormatUtil.tilde(partido.getEquipoLocal()) + " vs " + FormatUtil.tilde(partido.getEquipoVisita()) + RESET);
        System.out.println("Fecha:   " + partido.getFecha());
        System.out.println("Lugar:   " + FormatUtil.tilde(partido.getLugar()));
        System.out.println(PURPLE + "------------------------------------------------------------------------------------------" + RESET);
        System.out.printf(PURPLE + BOLD + "%-18s %-20s %-30s %-8s %-10s%n" + RESET, "Fecha Compra", "Cliente", "Localidad(es)", "Boletos", "Total ($)");
        System.out.println(PURPLE + "-".repeat(90) + RESET);

        List<DetalleVentaReporte> detalles = cliente.obtenerDetalleVentas(cod);
        if (detalles == null || detalles.isEmpty()) {
            System.out.println(YELLOW + "No hay ventas registradas para este partido." + RESET);
        } else {
            for (DetalleVentaReporte d : detalles) {
                System.out.printf("%-18s %-20s %-30s %-8d " + GREEN + "$%-9.2f%n" + RESET,
                        d.getFecha(), d.getCliente(), FormatUtil.tilde(d.getLocalidades()), d.getBoletosTotales(), d.getTotalVenta());
            }
        }
        
        System.out.println(PURPLE + "==========================================================================================" + RESET);
        System.out.println(PURPLE + BOLD + "   📊 RESUMEN POR LOCALIDAD" + RESET);
        System.out.println(PURPLE + "------------------------------------------------------------------------------------------" + RESET);
        System.out.printf(PURPLE + BOLD + "%-20s %-12s %-15s%n" + RESET, "Localidad", "Vendidos", "Total Recaudado");
        System.out.println(PURPLE + "-".repeat(50) + RESET);
        List<ResumenVenta> resumen = cliente.obtenerResumenVentas(cod);
        if (resumen != null && !resumen.isEmpty()) {
            for (ResumenVenta r : resumen) {
                System.out.printf("%-20s %-12d " + GREEN + "$%-14.2f%n" + RESET,
                        FormatUtil.tilde(r.getCodigoLocalidad()), r.getVendidos(), r.getTotalRecaudado());
            }
        }
        System.out.println(PURPLE + BOLD + "==========================================================================================" + RESET);
    }

    private static int leerInt() {
        try {
            return sc.nextInt();
        } catch (Exception e) {
            sc.nextLine();
            return -1;
        }
    }
}
