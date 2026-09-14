<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ec.edu.monster.ws.generated.LocalidadPartido" %>
<%@ page import="ec.edu.monster.ws.generated.PartidoFutbol" %>
<%@ page import="ec.edu.monster.ws.generated.AsientoPartido" %>
<%@ page import="ec.edu.monster.ws.generated.Usuario" %>
<%@ page import="ec.edu.monster.ws.generated.PeticionCompra" %>
<%@ page import="ec.edu.monster.util.FormatUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium - Selección de Asientos</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Inter', sans-serif; background: #0f172a; color: #e2e8f0; min-height: 100vh; }

        .header {
            background: linear-gradient(135deg, #065f46, #047857, #059669);
            padding: 24px 40px; position: relative; overflow: hidden;
            box-shadow: 0 4px 20px rgba(0,0,0,0.3);
        }
        .header::after {
            content: ''; position: absolute; top: -50%; right: -10%; width: 300px; height: 300px;
            background: rgba(255,255,255,0.05); border-radius: 50%;
        }
        .back-link { color: rgba(255,255,255,0.8); text-decoration: none; font-size: 0.9rem; font-weight: 500; transition: color 0.3s; }
        .back-link:hover { color: #fff; }
        .header h1 { font-size: 1.8rem; font-weight: 800; color: #fff; margin-top: 8px; }
        .header .match-info { color: #a7f3d0; margin-top: 6px; font-size: 0.95rem; line-height: 1.6; }

        .container { max-width: 1200px; margin: 0 auto; padding: 32px 24px; }

        /* Main layout split into 2 columns */
        .main-layout {
            display: flex; gap: 30px; align-items: flex-start;
        }

        .map-container {
            flex: 1.6;
            background: linear-gradient(145deg, #1e293b, #0f172a);
            border: 1px solid rgba(255, 255, 255, 0.05);
            border-radius: 16px; padding: 24px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }

        .cart-container {
            flex: 1;
            background: linear-gradient(145deg, #1e293b, #151d30);
            border: 1px solid rgba(16, 185, 129, 0.15);
            border-radius: 16px; padding: 24px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            position: sticky; top: 20px;
        }

        /* Stadium Grid Layout */
        .stadium-layout {
            display: grid;
            grid-template-columns: 140px 1fr 140px;
            grid-template-rows: auto 240px auto;
            gap: 20px;
            align-items: center;
            justify-items: center;
            margin: 20px auto;
            max-width: 800px;
        }

        /* Stands */
        .stand-north {
            grid-column: 2; grid-row: 1;
            display: flex; flex-wrap: wrap; gap: 6px;
            justify-content: center; max-width: 400px; padding: 12px;
            background: rgba(255, 255, 255, 0.02); border-radius: 10px;
            border: 1px dashed rgba(255, 255, 255, 0.08);
        }
        .stand-south {
            grid-column: 2; grid-row: 3;
            display: flex; flex-wrap: wrap; gap: 6px;
            justify-content: center; max-width: 400px; padding: 12px;
            background: rgba(255, 255, 255, 0.02); border-radius: 10px;
            border: 1px dashed rgba(255, 255, 255, 0.08);
        }
        .stand-west {
            grid-column: 1; grid-row: 2;
            display: flex; align-items: center; justify-content: center;
            padding: 12px; background: rgba(255, 255, 255, 0.02); border-radius: 10px;
            border: 1px dashed rgba(255, 255, 255, 0.08);
        }
        .stand-east {
            grid-column: 3; grid-row: 2;
            display: flex; align-items: center; justify-content: center;
            padding: 12px; background: rgba(255, 255, 255, 0.02); border-radius: 10px;
            border: 1px dashed rgba(255, 255, 255, 0.08);
        }

        /* Soccer field styled central pitch */
        .soccer-field {
            grid-column: 2; grid-row: 2;
            width: 100%; height: 180px; max-width: 320px;
            background: radial-gradient(circle, #0f9f6e 0%, #046c4e 100%);
            border: 2px solid rgba(255,255,255,0.7); border-radius: 8px;
            position: relative; box-shadow: inset 0 0 15px rgba(0,0,0,0.5);
            display: flex; align-items: center; justify-content: center;
        }
        .soccer-field .center-circle {
            width: 50px; height: 50px; border: 2px solid rgba(255,255,255,0.7);
            border-radius: 50%; position: absolute;
        }
        .soccer-field .center-line {
            width: 2px; height: 100%; background: rgba(255,255,255,0.7);
            position: absolute; left: 50%;
        }
        .soccer-field .penalty-box-left {
            width: 35px; height: 80px; border: 2px solid rgba(255,255,255,0.7);
            border-left: none; position: absolute; left: 0;
        }
        .soccer-field .penalty-box-right {
            width: 35px; height: 80px; border: 2px solid rgba(255,255,255,0.7);
            border-right: none; position: absolute; right: 0;
        }

        /* Seats */
        .seat {
            width: 28px; height: 28px; border-radius: 6px;
            font-size: 0.65rem; font-weight: 700;
            display: flex; align-items: center; justify-content: center;
            color: #fff; cursor: pointer; user-select: none;
            transition: all 0.2s ease;
        }
        .seat.disponible {
            background-color: #10b981; border: 1px solid #059669;
        }
        .seat.disponible:hover {
            background-color: #34d399; transform: scale(1.15);
            box-shadow: 0 0 8px rgba(52, 211, 153, 0.6);
        }
        .seat.ocupado {
            background-color: #ef4444; border: 1px solid #dc2626;
        }
        .seat.ocupado:hover {
            background-color: #f87171; transform: scale(1.15);
            box-shadow: 0 0 8px rgba(248, 113, 113, 0.6);
        }
        .seat.reservado {
            background-color: #d97706; border: 1px solid #b45309; cursor: not-allowed;
        }
        .seat.selected {
            background-color: #fbbf24 !important; border: 1px solid #d97706 !important;
            color: #1e293b; transform: scale(1.15);
            box-shadow: 0 0 10px rgba(251, 191, 36, 0.8);
        }

        .btn-buy {
            background: linear-gradient(135deg, #10b981, #059669); color: white;
            padding: 12px 24px; border: none; border-radius: 10px;
            font-size: 0.95rem; font-weight: 700; cursor: pointer;
            font-family: 'Inter', sans-serif; transition: all 0.3s;
        }
        .btn-buy:hover { background: linear-gradient(135deg, #059669, #047857); transform: scale(1.02); }

        /* Modal styling */
        .modal {
            display: none; position: fixed; z-index: 1000;
            left: 0; top: 0; width: 100%; height: 100%;
            background-color: rgba(15, 23, 42, 0.6);
            backdrop-filter: blur(8px);
            align-items: center; justify-content: center;
        }
        .modal-content {
            background: linear-gradient(135deg, rgba(30, 41, 59, 0.95), rgba(15, 23, 42, 0.95));
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 16px; padding: 24px;
            width: 90%; max-width: 400px;
            box-shadow: 0 20px 50px rgba(0,0,0,0.5);
            position: relative;
            animation: modalSlideIn 0.3s ease-out;
        }
        .close-btn {
            position: absolute; top: 15px; right: 20px;
            font-size: 1.8rem; font-weight: 700; color: #94a3b8;
            cursor: pointer; transition: color 0.2s;
        }
        .close-btn:hover { color: #fff; }

        @keyframes modalSlideIn {
            from { transform: translateY(20px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }

        @media (max-width: 992px) {
            .main-layout { flex-direction: column; align-items: stretch; }
            .cart-container { position: static; }
        }

        @media (max-width: 576px) {
            .stadium-layout {
                grid-template-columns: 1fr;
                grid-template-rows: auto auto auto auto auto;
            }
            .stand-north { grid-column: 1; grid-row: 1; }
            .stand-west { grid-column: 1; grid-row: 2; }
            .soccer-field { grid-column: 1; grid-row: 3; }
            .stand-east { grid-column: 1; grid-row: 4; }
            .stand-south { grid-column: 1; grid-row: 5; }
        }
    </style>
</head>
<body>
    <%
        PartidoFutbol partido = (PartidoFutbol) request.getAttribute("partido");
        int codPartido = (int) request.getAttribute("codPartido");
        List<LocalidadPartido> localidades = (List<LocalidadPartido>) request.getAttribute("localidades");
        List<AsientoPartido> asientos = (List<AsientoPartido>) request.getAttribute("asientos");

        int idPalco = 0, idTribuna = 0, idGeneral = 0;
        double precioPalco = 0, precioTribuna = 0, precioGeneral = 0;
        if (localidades != null) {
            for (LocalidadPartido l : localidades) {
                if ("PALCO".equalsIgnoreCase(l.getCodigoLocalidad())) {
                    idPalco = l.getId();
                    precioPalco = l.getPrecio();
                } else if ("TRIBUNA".equalsIgnoreCase(l.getCodigoLocalidad())) {
                    idTribuna = l.getId();
                    precioTribuna = l.getPrecio();
                } else if ("GENERAL".equalsIgnoreCase(l.getCodigoLocalidad())) {
                    idGeneral = l.getId();
                    precioGeneral = l.getPrecio();
                }
            }
        }

        int maxGeneral = 0;
        if (asientos != null) {
            for (AsientoPartido a : asientos) {
                if ("GENERAL".equalsIgnoreCase(a.getSeccion())) {
                    if (a.getNumero() > maxGeneral) {
                        maxGeneral = a.getNumero();
                    }
                }
            }
        }
        int midGeneral = maxGeneral / 2;
    %>

    <div class="header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px;">
        <div style="text-align: left;">
            <a href="ticket?accion=partidos" class="back-link">← Volver a Partidos</a>
            <h1>🎟️ Selección de Asientos</h1>
            <div class="match-info">
                <% if (partido != null) { %>
                    <strong><%= FormatUtil.tilde(partido.getEquipoLocal()) %> vs <%= FormatUtil.tilde(partido.getEquipoVisita()) %></strong><br/>
                    📅 <%= partido.getFecha() %> &nbsp;|&nbsp; 📍 <%= FormatUtil.tilde(partido.getLugar()) %>
                <% } %>
            </div>
        </div>
        <div style="display: flex; align-items: center; gap: 16px;">
            <%
                List<PeticionCompra> cart = null;
                HttpSession sess = request.getSession(false);
                if (sess != null) {
                    cart = (List<PeticionCompra>) sess.getAttribute("carrito_compras");
                }
                int cartSize = (cart != null) ? cart.size() : 0;
            %>
            <a href="ticket?accion=verCarrito" class="btn" style="background: linear-gradient(135deg, #10b981, #059669); position: relative; padding: 10px 20px; font-size: 0.9rem; text-decoration: none; color: white; border-radius: 10px; font-weight: 600; display: inline-flex; align-items: center;">
                🛒 Ver Carrito <span style="background: #ef4444; color: white; border-radius: 50%; padding: 2px 6px; font-size: 0.75rem; margin-left: 4px;"><%= cartSize %></span>
            </a>
            <% if (sess != null && sess.getAttribute("usuario") != null) { 
                Usuario u = (Usuario) sess.getAttribute("usuario");
            %>
                <span style="color: #cbd5e1; font-size: 0.9rem; font-weight: 500;">👤 <%= FormatUtil.tilde(u.getUsername()) %></span>
                <a href="ticket?accion=logout" class="btn" style="font-size: 0.85rem; padding: 8px 16px; text-decoration: none; background: transparent; color: #a78bfa; border: 1px solid rgba(167,139,250,0.3); border-radius: 10px; font-weight: 600;">Cerrar Sesión</a>
            <% } %>
        </div>
    </div>

    <div class="container">
        <% if (localidades != null && !localidades.isEmpty()) { %>
        
        <div class="main-layout">
            <!-- Left Side: Stadium map -->
            <div class="map-container">
                <h2 style="font-size: 1.25rem; font-weight: 700; color: #fff; margin-bottom: 16px; display: flex; align-items: center; gap: 8px;">
                    🏟️ Mapa del Estadio
                </h2>
                
                <!-- Legend -->
                <div class="legend" style="display: flex; gap: 20px; justify-content: center; margin-bottom: 24px; padding: 12px; background: rgba(255,255,255,0.03); border-radius: 10px; font-size: 0.85rem;">
                    <div style="display: flex; align-items: center; gap: 6px;">
                        <span class="seat disponible" style="cursor: default; width:18px; height:18px;"></span> Disponible
                    </div>
                    <div style="display: flex; align-items: center; gap: 6px;">
                        <span class="seat selected" style="cursor: default; width:18px; height:18px;"></span> Seleccionado
                    </div>
                    <div style="display: flex; align-items: center; gap: 6px;">
                        <span class="seat ocupado" style="cursor: default; width:18px; height:18px;"></span> Ocupado
                    </div>
                </div>

                <div class="stadium-layout">
                    <!-- North General Stand -->
                    <div class="stand-north">
                        <div style="width: 100%; text-align: center; font-size: 0.75rem; color: #94a3b8; font-weight: 600; margin-bottom: 4px;">General Norte (1 a <%= midGeneral %>)</div>
                        <% 
                            if (asientos != null) {
                                for (AsientoPartido a : asientos) {
                                    if ("GENERAL".equalsIgnoreCase(a.getSeccion()) && a.getNumero() <= midGeneral) {
                                        String stateClass = a.getEstado().toLowerCase();
                        %>
                                        <div class="seat <%= stateClass %>" 
                                             data-id="<%= a.getIdAsientoPartido() %>"
                                             data-section="<%= a.getSeccion() %>"
                                             data-fila="<%= a.getFila() %>"
                                             data-numero="<%= a.getNumero() %>"
                                             data-precio="<%= a.getPrecio() %>"
                                             title="Asiento <%= a.getSeccion() %> <%= a.getFila() %>-<%= a.getNumero() %>: <%= a.getEstado() %>"
                                             onclick="handleSeatClick(this, '<%= a.getEstado() %>', <%= a.getIdAsientoPartido() %>)">
                                             <%= a.getNumero() %>
                                        </div>
                        <% 
                                    }
                                }
                            }
                        %>
                    </div>

                    <!-- West Palco Stand (VIP) -->
                    <div class="stand-west">
                        <div style="display: flex; flex-direction: column; align-items: center; gap: 6px;">
                            <div style="text-align: center; font-size: 0.75rem; color: #10b981; font-weight: 700; margin-bottom: 4px;">Palco (VIP)</div>
                            <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 6px;">
                            <% 
                                if (asientos != null) {
                                    for (AsientoPartido a : asientos) {
                                        if ("PALCO".equalsIgnoreCase(a.getSeccion())) {
                                            String stateClass = a.getEstado().toLowerCase();
                            %>
                                            <div class="seat <%= stateClass %>" 
                                                 data-id="<%= a.getIdAsientoPartido() %>"
                                                 data-section="<%= a.getSeccion() %>"
                                                 data-fila="<%= a.getFila() %>"
                                                 data-numero="<%= a.getNumero() %>"
                                                 data-precio="<%= a.getPrecio() %>"
                                                 title="Asiento <%= a.getSeccion() %> <%= a.getFila() %>-<%= a.getNumero() %>: <%= a.getEstado() %>"
                                                 onclick="handleSeatClick(this, '<%= a.getEstado() %>', <%= a.getIdAsientoPartido() %>)">
                                                 P<%= a.getNumero() %>
                                            </div>
                            <% 
                                        }
                                    }
                                }
                            %>
                            </div>
                        </div>
                    </div>

                    <!-- Soccer Field -->
                    <div class="soccer-field">
                        <div class="center-line"></div>
                        <div class="center-circle"></div>
                        <div class="penalty-box-left"></div>
                        <div class="penalty-box-right"></div>
                        <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); color: rgba(255,255,255,0.2); font-weight: 800; font-size: 0.85rem; letter-spacing: 2px; pointer-events: none;">FIFA 2026</div>
                    </div>

                    <!-- East Tribuna Stand -->
                    <div class="stand-east">
                        <div style="display: flex; flex-direction: column; align-items: center; gap: 6px;">
                            <div style="text-align: center; font-size: 0.75rem; color: #38bdf8; font-weight: 700; margin-bottom: 4px;">Tribuna</div>
                            <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 6px;">
                            <% 
                                if (asientos != null) {
                                    for (AsientoPartido a : asientos) {
                                        if ("TRIBUNA".equalsIgnoreCase(a.getSeccion())) {
                                            String stateClass = a.getEstado().toLowerCase();
                            %>
                                            <div class="seat <%= stateClass %>" 
                                                 data-id="<%= a.getIdAsientoPartido() %>"
                                                 data-section="<%= a.getSeccion() %>"
                                                 data-fila="<%= a.getFila() %>"
                                                 data-numero="<%= a.getNumero() %>"
                                                 data-precio="<%= a.getPrecio() %>"
                                                 title="Asiento <%= a.getSeccion() %> <%= a.getFila() %>-<%= a.getNumero() %>: <%= a.getEstado() %>"
                                                 onclick="handleSeatClick(this, '<%= a.getEstado() %>', <%= a.getIdAsientoPartido() %>)">
                                                 T<%= a.getNumero() %>
                                            </div>
                            <% 
                                        }
                                    }
                                }
                            %>
                            </div>
                        </div>
                    </div>

                    <!-- South General Stand -->
                    <div class="stand-south">
                        <% 
                            if (asientos != null) {
                                for (AsientoPartido a : asientos) {
                                    if ("GENERAL".equalsIgnoreCase(a.getSeccion()) && a.getNumero() > midGeneral) {
                                        String stateClass = a.getEstado().toLowerCase();
                        %>
                                        <div class="seat <%= stateClass %>" 
                                             data-id="<%= a.getIdAsientoPartido() %>"
                                             data-section="<%= a.getSeccion() %>"
                                             data-fila="<%= a.getFila() %>"
                                             data-numero="<%= a.getNumero() %>"
                                             data-precio="<%= a.getPrecio() %>"
                                             title="Asiento <%= a.getSeccion() %> <%= a.getFila() %>-<%= a.getNumero() %>: <%= a.getEstado() %>"
                                             onclick="handleSeatClick(this, '<%= a.getEstado() %>', <%= a.getIdAsientoPartido() %>)">
                                             <%= a.getNumero() %>
                                        </div>
                        <% 
                                    }
                                }
                            }
                        %>
                        <div style="width: 100%; text-align: center; font-size: 0.75rem; color: #94a3b8; font-weight: 600; margin-top: 4px;">General Sur (<%= midGeneral + 1 %> a <%= maxGeneral %>)</div>
                    </div>
                </div>
            </div>

            <!-- Right Side: Shopping Cart / Add to Cart Form -->
            <div class="cart-container">
                <form method="post" action="ticket" id="compraForm" onsubmit="return validateForm()">
                    <input type="hidden" name="accion" value="agregarCarrito"/>
                    <input type="hidden" name="codPartido" value="<%= codPartido %>"/>

                    <h2 style="font-size: 1.25rem; font-weight: 800; color: #fff; margin-bottom: 16px; border-bottom: 1px solid rgba(255,255,255,0.08); padding-bottom: 10px; display: flex; align-items: center; gap: 8px;">
                        🛒 Asientos Seleccionados
                    </h2>

                    <!-- Container for Selected Seats list -->
                    <div id="selected-seats-list" style="display: flex; flex-direction: column; gap: 12px; max-height: 250px; overflow-y: auto; padding-right: 4px; margin-bottom: 20px;">
                        <!-- Placeholder if no seats selected -->
                        <div id="no-seats-placeholder" style="text-align: center; padding: 30px; color: #64748b; font-size: 0.9rem; border: 1px dashed rgba(255,255,255,0.08); border-radius: 8px;">
                            Seleccione seats libres (verdes) en el mapa del estadio.
                        </div>
                    </div>

                    <!-- Bill details -->
                    <div style="background: rgba(0,0,0,0.2); border-radius: 12px; padding: 16px; margin-bottom: 20px; border: 1px solid rgba(255,255,255,0.03);">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 0.9rem; color: #94a3b8;">
                            <span>Subtotal Selección:</span>
                            <span id="summary-subtotal" style="font-weight: 600; color: #cbd5e1;">$0.00</span>
                        </div>
                        <div style="display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 0.9rem; color: #94a3b8;">
                            <span>IVA (15%):</span>
                            <span id="summary-iva" style="font-weight: 600; color: #cbd5e1;">$0.00</span>
                        </div>
                        <div style="display: flex; justify-content: space-between; padding-top: 10px; border-top: 1px solid rgba(255,255,255,0.08);">
                            <span style="font-weight: bold; color: #fff;">Total Selección:</span>
                            <span id="summary-total" style="font-weight: bold; color: #10b981; font-size: 1.25rem;">$0.00</span>
                        </div>
                    </div>

                    <!-- Client info and checkout inputs -->
                    <div style="display: flex; flex-direction: column; gap: 12px;">
                        <div>
                            <label style="font-size: 0.8rem; color: #94a3b8; font-weight: 600; display: block; margin-bottom: 4px;">Nombre del Comprador/Ocupante Base:</label>
                            <input type="text" name="nombreCliente" id="nombreCliente" required placeholder="Nombre para los boletos"
                                   style="width: 100%; padding: 10px 14px; border-radius: 8px; border: 1px solid #334155; background: #0f172a; color: #e2e8f0; font-size: 0.9rem;">
                        </div>

                        <!-- Hidden Inputs Area where javascript will place the dynamic seat objects -->
                        <div id="hidden-inputs-area"></div>

                        <button type="submit" class="btn-buy" style="width: 100%; padding: 12px; font-size: 1rem; margin-top: 10px; display: flex; align-items: center; justify-content: center; gap: 8px;">
                            ➕ Agregar al Carrito
                        </button>
                        
                        <a href="ticket?accion=verCarrito" class="btn btn-outline" style="width: 100%; padding: 12px; font-size: 0.95rem; text-align: center; text-decoration: none; border-radius: 10px; display: inline-block;">
                            🛒 Ir al Carrito
                        </a>
                    </div>
                    <% String error = (String) request.getAttribute("error"); if (error != null) { %>
                        <div style="margin-top: 16px; color: #ef4444; font-size: 0.9rem; font-weight: 600; text-align: center;"><%= error %></div>
                    <% } %>
                </form>
            </div>
        </div>
        
        <% } else { %>
        <div style="text-align:center;padding:40px;color:#64748b;">No hay localidades disponibles para este partido.</div>
        <% } %>
    </div>

    <!-- Modal de Detalles de Asiento Ocupado -->
    <div id="detailModal" class="modal">
        <div class="modal-content">
            <span class="close-btn" onclick="closeModal()">&times;</span>
            <h2 style="color: #ef4444; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; font-size: 1.25rem;">
                🎫 Información de Boleto
            </h2>
            <div id="modal-body">
                <!-- Loaded dynamically via AJAX -->
            </div>
        </div>
    </div>

    <script type="text/javascript">
        // Mapping from section string to Localidad ID and Price
        const localidadMap = {
            'PALCO': { id: <%= idPalco %>, precio: <%= precioPalco %> },
            'TRIBUNA': { id: <%= idTribuna %>, precio: <%= precioTribuna %> },
            'GENERAL': { id: <%= idGeneral %>, precio: <%= precioGeneral %> }
        };

        let selectedSeats = [];

        function handleSeatClick(element, estado, idAsiento) {
            const est = estado.toLowerCase();
            if (est === 'ocupado') {
                showSeatDetails(<%= codPartido %>, idAsiento);
                return;
            }
            if (est === 'reservado') {
                alert("Este asiento está reservado y no está disponible para compra.");
                return;
            }
            
            // Available seat selection/deselection
            const isSelected = element.classList.toggle('selected');
            const seatId = parseInt(element.getAttribute('data-id'));
            
            if (isSelected) {
                const section = element.getAttribute('data-section');
                const fila = element.getAttribute('data-fila');
                const numero = parseInt(element.getAttribute('data-numero'));
                const precio = parseFloat(element.getAttribute('data-precio'));
                
                selectedSeats.push({
                    idAsiento: seatId,
                    section: section,
                    fila: fila,
                    numero: numero,
                    precio: precio,
                    occupant: ''
                });
            } else {
                selectedSeats = selectedSeats.filter(s => s.idAsiento !== seatId);
            }
            
            updateCartUI();
        }

        function updateCartUI() {
            const list = document.getElementById("selected-seats-list");
            const placeholder = document.getElementById("no-seats-placeholder");
            
            list.innerHTML = "";
            
            if (selectedSeats.length === 0) {
                list.appendChild(placeholder);
            } else {
                selectedSeats.forEach((seat) => {
                    const item = document.createElement("div");
                    item.className = "cart-item";
                    item.style.cssText = "display: flex; flex-direction: column; gap: 8px; padding: 12px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.05); border-radius: 10px;";
                    
                    item.innerHTML = `
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <span style="font-weight: bold; color: #10b981; font-size: 0.9rem;">\${tildeSection(seat.section)} - Fila \${seat.fila} N°\${seat.numero}</span>
                            <span style="font-weight: 700; color: #34d399; font-size: 0.95rem;">$\${seat.precio.toFixed(2)}</span>
                        </div>
                        <div style="display: flex; gap: 8px; align-items: center;">
                            <input type="text" placeholder="Nombre del Ocupante" 
                                   value="\${seat.occupant}" 
                                   oninput="updateOccupantName(\${seat.idAsiento}, this.value)"
                                   style="flex: 1; padding: 6px 10px; border-radius: 6px; border: 1px solid #334155; background: #0f172a; color: #e2e8f0; font-size: 0.8rem;" required>
                            <button type="button" onclick="removeSeat(\${seat.idAsiento})"
                                    style="padding: 6px 10px; border-radius: 6px; border: none; background: #ef4444; color: #fff; font-size: 0.75rem; cursor: pointer; font-weight: 600;">
                                Quitar
                            </button>
                        </div>
                    `;
                    list.appendChild(item);
                });
            }
            
            calculatePrices();
        }

        function tildeSection(section) {
            if (section === 'PALCO') return 'Palco';
            if (section === 'TRIBUNA') return 'Tribuna';
            if (section === 'GENERAL') return 'General';
            return section;
        }

        function updateOccupantName(idAsientoStr, name) {
            const idAsiento = parseInt(idAsientoStr);
            const seat = selectedSeats.find(s => s.idAsiento === idAsiento);
            if (seat) {
                seat.occupant = name;
            }
        }

        function removeSeat(idAsiento) {
            selectedSeats = selectedSeats.filter(s => s.idAsiento !== idAsiento);
            const element = document.querySelector(`.seat[data-id="\${idAsiento}"]`);
            if (element) {
                element.classList.remove('selected');
            }
            updateCartUI();
        }

        function calculatePrices() {
            let subtotal = 0;
            selectedSeats.forEach(s => {
                subtotal += s.precio;
            });
            
            const iva = subtotal * 0.15;
            const total = subtotal + iva;
            
            document.getElementById("summary-subtotal").innerText = "$" + subtotal.toFixed(2);
            document.getElementById("summary-iva").innerText = "$" + iva.toFixed(2);
            document.getElementById("summary-total").innerText = "$" + total.toFixed(2);
        }

        function validateForm() {
            if (selectedSeats.length === 0) {
                alert("Debe seleccionar al menos un asiento en el mapa para agregar al carrito.");
                return false;
            }
            
            const baseNameInput = document.getElementById("nombreCliente");
            const baseName = baseNameInput ? baseNameInput.value : "";
            
            const area = document.getElementById("hidden-inputs-area");
            area.innerHTML = "";
            
            selectedSeats.forEach(seat => {
                const idLocalidad = localidadMap[seat.section].id;
                const occupantName = (seat.occupant && seat.occupant.trim() !== "") ? seat.occupant : baseName;
                
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = "selectedSeats";
                // We send in format: idAsiento:idLocalidad:codLocalidad:precio:ocupante
                input.value = `\${seat.idAsiento}:\${idLocalidad}:\${seat.section}:\${seat.precio.toFixed(2)}:\${occupantName}`;
                area.appendChild(input);
            });
            
            return true;
        }

        function showSeatDetails(codPartido, idAsientoPartido) {
            const xhr = new XMLHttpRequest();
            xhr.open("GET", "ticket?accion=detalleAsiento&codPartido=" + codPartido + "&idAsientoPartido=" + idAsientoPartido, true);
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    const data = JSON.parse(xhr.responseText);
                    const modalBody = document.getElementById("modal-body");
                    if (data.error) {
                        modalBody.innerHTML = `<div style="color: #ef4444; text-align: center; font-weight: 600; padding: 20px;">\${data.error}</div>`;
                    } else {
                        modalBody.innerHTML = `
                            <div style="display: flex; flex-direction: column; gap: 12px; margin-top: 10px; font-size: 0.9rem;">
                                <div style="display: flex; justify-content: space-between; border-bottom: 1px solid rgba(255,255,255,0.08); padding-bottom: 8px;">
                                    <span style="color: #94a3b8;">Ocupante:</span>
                                    <span style="font-weight: 600; color: #fff;">\${data.ocupante}</span>
                                </div>
                                <div style="display: flex; justify-content: space-between; border-bottom: 1px solid rgba(255,255,255,0.08); padding-bottom: 8px;">
                                    <span style="color: #94a3b8;">Comprador:</span>
                                    <span style="font-weight: 600; color: #fff;">\${data.cliente}</span>
                                </div>
                                <div style="display: flex; justify-content: space-between; border-bottom: 1px solid rgba(255,255,255,0.08); padding-bottom: 8px;">
                                    <span style="color: #94a3b8;">Factura N°:</span>
                                    <span style="font-weight: 600; color: #10b981;">#\${data.factura}</span>
                                </div>
                                <div style="display: flex; justify-content: space-between; border-bottom: 1px solid rgba(255,255,255,0.08); padding-bottom: 8px;">
                                    <span style="color: #94a3b8;">Fecha Compra:</span>
                                    <span style="font-weight: 600; color: #fff;">\${data.fecha}</span>
                                </div>
                                <div style="display: flex; justify-content: space-between; border-bottom: 1px solid rgba(255,255,255,0.08); padding-bottom: 8px;">
                                    <span style="color: #94a3b8;">Precio Localidad:</span>
                                    <span style="font-weight: 600; color: #fff;">$\${parseFloat(data.precio).toFixed(2)}</span>
                                </div>
                                <div style="display: flex; justify-content: space-between; padding-top: 8px;">
                                    <span style="color: #94a3b8; font-weight: bold;">Total Factura:</span>
                                    <span style="font-weight: bold; color: #34d399; font-size: 1.2rem;">$\${parseFloat(data.total).toFixed(2)}</span>
                                </div>
                            </div>
                        `;
                    }
                    document.getElementById("detailModal").style.display = "flex";
                }
            };
            xhr.send();
        }

        function closeModal() {
            document.getElementById("detailModal").style.display = "none";
        }

        // Close modal when clicking outside content
        window.onclick = function(event) {
            const modal = document.getElementById("detailModal");
            if (event.target === modal) {
                modal.style.display = "none";
            }
        }
    </script>
</body>
</html>
