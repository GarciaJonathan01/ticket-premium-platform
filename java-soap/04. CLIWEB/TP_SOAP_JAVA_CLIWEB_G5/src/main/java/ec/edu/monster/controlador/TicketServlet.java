package ec.edu.monster.controlador;

import ec.edu.monster.servicio.ClienteFederacion;
import ec.edu.monster.servicio.BancoSoapClient;
import ec.edu.monster.modelo.RespuestaCredito;
import ec.edu.monster.ws.generated.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet principal de TicketPremium Web.
 * Maneja todas las acciones: listar partidos, localidades, comprar, reporte.
 */
@WebServlet(name = "TicketServlet", urlPatterns = {"/ticket"})
public class TicketServlet extends HttpServlet {

    private ClienteFederacion cliente;

    @Override
    public void init() throws ServletException {
        cliente = new ClienteFederacion();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        String accion = request.getParameter("accion");
        if (accion == null) accion = "partidos";

        if ("login".equals(accion)) {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        if ("logout".equals(accion)) {
            if (session != null) session.invalidate();
            response.sendRedirect("ticket?accion=login");
            return;
        }

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("ticket?accion=login");
            return;
        }

        switch (accion) {
            case "localidades":
                int codPartido = Integer.parseInt(request.getParameter("codPartido"));
                request.setAttribute("localidades", cliente.obtenerLocalidades(codPartido));
                request.setAttribute("asientos", cliente.obtenerAsientosPartido(codPartido));
                request.setAttribute("codPartido", codPartido);
                PartidoFutbol partido = cliente.obtenerPartido(codPartido);
                request.setAttribute("partido", partido);

                HttpSession sessLocal = request.getSession(false);
                if (sessLocal != null) {
                    String cartError = (String) sessLocal.getAttribute("cart_error");
                    if (cartError != null) {
                        request.setAttribute("error", cartError);
                        sessLocal.removeAttribute("cart_error");
                    }
                }

                request.getRequestDispatcher("/localidades.jsp").forward(request, response);
                break;
            case "reporte":
                int codRep = Integer.parseInt(request.getParameter("codPartido"));
                PartidoFutbol pRep = cliente.obtenerPartido(codRep);
                request.setAttribute("partido", pRep);
                request.setAttribute("detalles", cliente.obtenerDetalleVentas(codRep));
                request.setAttribute("resumen", cliente.obtenerResumenVentas(codRep));
                request.getRequestDispatcher("/reporte.jsp").forward(request, response);
                break;
            case "detalleAsiento":
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try {
                    int idAsiento = Integer.parseInt(request.getParameter("idAsientoPartido"));
                    int matchCode = Integer.parseInt(request.getParameter("codPartido"));
                    List<AsientoPartido> seats = cliente.obtenerAsientosPartido(matchCode);
                    AsientoPartido found = null;
                    if (seats != null) {
                        for (AsientoPartido s : seats) {
                            if (s.getIdAsientoPartido() == idAsiento) {
                                found = s;
                                break;
                            }
                        }
                    }
                    if (found != null) {
                        String json = "{"
                            + "\"ocupante\":\"" + escapeJson(found.getNombreOcupante()) + "\","
                            + "\"cliente\":\"" + escapeJson(found.getNombreCliente()) + "\","
                            + "\"factura\":" + found.getIdFactura() + ","
                            + "\"fecha\":\"" + found.getFechaCompra() + "\","
                            + "\"total\":" + found.getTotalFactura() + ","
                            + "\"precio\":" + found.getPrecio()
                            + "}";
                        response.getWriter().write(json);
                    } else {
                        response.getWriter().write("{\"error\": \"Asiento no encontrado\"}");
                    }
                } catch (Exception ex) {
                    response.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
                }
                return;
            case "verCarrito":
                List<PeticionCompra> cart = null;
                if (session != null) {
                    cart = (List<PeticionCompra>) session.getAttribute("carrito_compras");
                }
                java.util.Map<Integer, PartidoFutbol> matchMap = new java.util.HashMap<>();
                if (cart != null) {
                    for (PeticionCompra p : cart) {
                        if (!matchMap.containsKey(p.getCodigoPartido())) {
                            matchMap.put(p.getCodigoPartido(), cliente.obtenerPartido(p.getCodigoPartido()));
                        }
                    }
                }
                request.setAttribute("matchMap", matchMap);
                request.getRequestDispatcher("/carrito.jsp").forward(request, response);
                break;
            case "eliminarCarrito":
                if (session != null) {
                    List<PeticionCompra> currentCart = (List<PeticionCompra>) session.getAttribute("carrito_compras");
                    String indexStr = request.getParameter("index");
                    if (currentCart != null && indexStr != null) {
                        int idx = Integer.parseInt(indexStr);
                        if (idx >= 0 && idx < currentCart.size()) {
                            PeticionCompra removed = currentCart.remove(idx);
                            if (removed != null) {
                                cliente.reservarAsientoTemporal(removed.getIdAsientoPartido(), false);
                            }
                        }
                    }
                }
                response.sendRedirect("ticket?accion=verCarrito");
                break;
            default:
                request.setAttribute("partidos", cliente.obtenerPartidosDisponibles());
                request.getRequestDispatcher("/partidos.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        if ("login".equals(accion)) {
            String u = request.getParameter("username");
            String c = request.getParameter("password");
            Usuario usr = cliente.login(u, c);
            if (usr != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", usr);
                response.sendRedirect("ticket?accion=partidos");
            } else {
                request.setAttribute("error", "Credenciales incorrectas.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
            return;
        }

        if ("agregarCarrito".equals(accion)) {
            int codPartido = Integer.parseInt(request.getParameter("codPartido"));
            String nombre = request.getParameter("nombreCliente");

            String[] selectedSeats = request.getParameterValues("selectedSeats");
            List<PeticionCompra> tempSeats = new ArrayList<>();
            int failedCount = 0;

            if (selectedSeats != null && selectedSeats.length > 0) {
                for (String seatStr : selectedSeats) {
                    if (seatStr != null && !seatStr.trim().isEmpty()) {
                        String[] parts = seatStr.split(":", 5);
                        if (parts.length >= 4) {
                            int idAsiento = Integer.parseInt(parts[0]);
                            int idLocalidad = Integer.parseInt(parts[1]);
                            String codLocalidad = parts[2];
                            double precio = Double.parseDouble(parts[3]);
                            String ocupante = (parts.length > 4 && !parts[4].trim().isEmpty()) ? parts[4] : nombre;

                            boolean reservedOk = cliente.reservarAsientoTemporal(idAsiento, true);
                            if (reservedOk) {
                                PeticionCompra p = new PeticionCompra();
                                p.setIdLocalidad(idLocalidad);
                                p.setCodigoLocalidad(codLocalidad);
                                p.setPrecioUnitario(precio);
                                p.setCantidad(1);
                                p.setCodigoPartido(codPartido);
                                p.setIdAsientoPartido(idAsiento);
                                p.setNombreOcupante(ocupante);
                                tempSeats.add(p);
                            } else {
                                failedCount++;
                            }
                        }
                    }
                }
            }

            if (!tempSeats.isEmpty()) {
                HttpSession session = request.getSession(true);
                List<PeticionCompra> cart = (List<PeticionCompra>) session.getAttribute("carrito_compras");
                if (cart == null) {
                    cart = new ArrayList<>();
                }
                cart.addAll(tempSeats);
                session.setAttribute("carrito_compras", cart);
            }

            if (failedCount > 0) {
                HttpSession session = request.getSession(true);
                session.setAttribute("cart_error", "Algunos asientos ya no estaban disponibles y no se agregaron.");
                response.sendRedirect("ticket?accion=localidades&codPartido=" + codPartido);
            } else {
                response.sendRedirect("ticket?accion=partidos");
            }
            return;
        }

        if ("finalizarCompra".equals(accion)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuario") == null) {
                response.sendRedirect("ticket?accion=login");
                return;
            }

            List<PeticionCompra> cart = (List<PeticionCompra>) session.getAttribute("carrito_compras");
            if (cart == null || cart.isEmpty()) {
                response.sendRedirect("ticket?accion=partidos");
                return;
            }

            // Update occupant names from form
            String[] indices = request.getParameterValues("cartIndex");
            if (indices != null) {
                for (String idxStr : indices) {
                    int idx = Integer.parseInt(idxStr);
                    String ocupante = request.getParameter("ocupante_" + idx);
                    if (ocupante != null && !ocupante.trim().isEmpty() && idx < cart.size()) {
                        cart.get(idx).setNombreOcupante(ocupante);
                    }
                }
            }

            String formaPago = request.getParameter("formaPago");
            if (formaPago == null) formaPago = "EFECTIVO";
            String cedula = request.getParameter("cedulaCliente");
            String nombre = request.getParameter("nombreCliente");
            
            if (cedula == null || cedula.trim().isEmpty()) {
                cedula = "1726354712"; // default test fallback
            }

            Factura factura = null;
            if ("CREDITO".equalsIgnoreCase(formaPago)) {
                String plazoStr = request.getParameter("plazoMeses");
                int plazo = (plazoStr != null && !plazoStr.trim().isEmpty()) ? Integer.parseInt(plazoStr) : 6;

                double subtotal = 0;
                for (PeticionCompra p : cart) {
                    subtotal += p.getCantidad() * p.getPrecioUnitario();
                }
                // Credit purchase doesn't get discount. Subtotal gets 15% IVA.
                double iva = Math.round(subtotal * 0.15 * 100.0) / 100.0;
                double total = subtotal + iva;

                BancoSoapClient banco = new BancoSoapClient();
                RespuestaCredito res = banco.verificarYCrearCredito(cedula, total, plazo);
                if (res.isAprobado()) {
                    factura = cliente.comprarBoletosMulti(cedula, cart, "CREDITO", res.getIdCredito());
                    if (factura != null) {
                        request.setAttribute("factura", factura);
                        request.setAttribute("carrito", new ArrayList<>(cart));
                        request.setAttribute("respuestaCredito", res);
                        
                        // Pass match details map to factura.jsp
                        java.util.Map<Integer, PartidoFutbol> matchMap = new java.util.HashMap<>();
                        for (PeticionCompra p : cart) {
                            if (!matchMap.containsKey(p.getCodigoPartido())) {
                                matchMap.put(p.getCodigoPartido(), cliente.obtenerPartido(p.getCodigoPartido()));
                            }
                        }
                        request.setAttribute("matchMap", matchMap);

                        session.removeAttribute("carrito_compras");
                        request.getRequestDispatcher("/factura.jsp").forward(request, response);
                        return;
                    } else {
                        request.setAttribute("error", "Error al registrar la compra en la federación.");
                    }
                } else {
                    request.setAttribute("error", "Crédito Bancario Rechazado: " + res.getMensaje());
                }
            } else {
                // EFECTIVO
                factura = cliente.comprarBoletosMulti(cedula, cart, "EFECTIVO", null);
                if (factura != null) {
                    request.setAttribute("factura", factura);
                    request.setAttribute("carrito", new ArrayList<>(cart));
                    
                    // Pass match details map to factura.jsp
                    java.util.Map<Integer, PartidoFutbol> matchMap = new java.util.HashMap<>();
                    for (PeticionCompra p : cart) {
                        if (!matchMap.containsKey(p.getCodigoPartido())) {
                            matchMap.put(p.getCodigoPartido(), cliente.obtenerPartido(p.getCodigoPartido()));
                        }
                    }
                    request.setAttribute("matchMap", matchMap);

                    session.removeAttribute("carrito_compras");
                    request.getRequestDispatcher("/factura.jsp").forward(request, response);
                    return;
                } else {
                    request.setAttribute("error", "Error al registrar la compra en la federación.");
                }
            }

            // Return to checkout cart screen with error
            java.util.Map<Integer, PartidoFutbol> matchMap = new java.util.HashMap<>();
            for (PeticionCompra p : cart) {
                if (!matchMap.containsKey(p.getCodigoPartido())) {
                    matchMap.put(p.getCodigoPartido(), cliente.obtenerPartido(p.getCodigoPartido()));
                }
            }
            request.setAttribute("matchMap", matchMap);
            request.getRequestDispatcher("/carrito.jsp").forward(request, response);
            return;
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
