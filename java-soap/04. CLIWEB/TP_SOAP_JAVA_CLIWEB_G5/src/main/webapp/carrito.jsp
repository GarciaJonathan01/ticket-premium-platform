<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ec.edu.monster.ws.generated.PeticionCompra" %>
<%@ page import="ec.edu.monster.ws.generated.PartidoFutbol" %>
<%@ page import="ec.edu.monster.util.FormatUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium - Carrito de Compras</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Inter', sans-serif; background: #0f172a; color: #e2e8f0; min-height: 100vh; }

        .header {
            background: linear-gradient(135deg, #1e1b4b, #311042, #1e1b4b);
            padding: 24px 40px; position: relative; overflow: hidden;
            box-shadow: 0 4px 20px rgba(0,0,0,0.3);
            border-bottom: 1px solid rgba(255, 255, 255, 0.05);
        }
        .back-link { color: rgba(255,255,255,0.8); text-decoration: none; font-size: 0.9rem; font-weight: 500; transition: color 0.3s; }
        .back-link:hover { color: #fff; }
        .header h1 { font-size: 1.8rem; font-weight: 800; color: #fff; margin-top: 8px; }
        .header p { color: #c084fc; margin-top: 6px; font-size: 0.95rem; }

        .container { max-width: 1200px; margin: 0 auto; padding: 32px 24px; }

        /* Main layout split into 2 columns */
        .main-layout {
            display: flex; gap: 30px; align-items: flex-start;
        }

        .cart-items-container {
            flex: 1.6;
            background: linear-gradient(145deg, #1e293b, #0f172a);
            border: 1px solid rgba(255, 255, 255, 0.05);
            border-radius: 16px; padding: 24px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }

        .checkout-container {
            flex: 1;
            background: linear-gradient(145deg, #1e293b, #151d30);
            border: 1px solid rgba(192, 132, 252, 0.2);
            border-radius: 16px; padding: 24px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            position: sticky; top: 20px;
        }

        .cart-item {
            background: rgba(255, 255, 255, 0.02);
            border: 1px solid rgba(255, 255, 255, 0.05);
            border-radius: 12px; padding: 16px; margin-bottom: 16px;
            display: flex; justify-content: space-between; align-items: center;
            gap: 16px; transition: border-color 0.3s;
        }
        .cart-item:hover { border-color: rgba(192, 132, 252, 0.3); }

        .item-details { display: flex; flex-direction: column; gap: 6px; flex: 1; }
        .item-match { font-size: 1rem; font-weight: 700; color: #fff; }
        .item-info { font-size: 0.85rem; color: #94a3b8; }
        .item-seat-badge {
            display: inline-block; padding: 3px 8px; border-radius: 6px;
            font-size: 0.75rem; font-weight: 700; text-transform: uppercase;
            margin-top: 4px;
        }
        .badge-palco { background: rgba(16, 185, 129, 0.15); color: #10b981; border: 1px solid rgba(16, 185, 129, 0.3); }
        .badge-tribuna { background: rgba(56, 189, 248, 0.15); color: #38bdf8; border: 1px solid rgba(56, 189, 248, 0.3); }
        .badge-general { background: rgba(251, 191, 36, 0.15); color: #fbbf24; border: 1px solid rgba(251, 191, 36, 0.3); }

        .item-occupant-input {
            margin-top: 8px; display: flex; flex-direction: column; gap: 4px;
        }
        .item-occupant-input label { font-size: 0.75rem; color: #64748b; font-weight: 600; }
        .item-occupant-input input {
            padding: 6px 10px; border-radius: 6px; border: 1px solid #334155;
            background: #0f172a; color: #e2e8f0; font-size: 0.85rem; width: 100%; max-width: 220px;
        }

        .item-price-actions { display: flex; flex-direction: column; align-items: flex-end; gap: 12px; }
        .item-price { font-size: 1.1rem; font-weight: 700; color: #fff; }
        .btn-remove {
            background: transparent; border: none; color: #ef4444; font-size: 0.85rem;
            cursor: pointer; font-weight: 600; transition: color 0.2s;
        }
        .btn-remove:hover { color: #f87171; text-decoration: underline; }

        .btn-buy {
            background: linear-gradient(135deg, #c084fc, #8b5cf6); color: white;
            padding: 12px 24px; border: none; border-radius: 10px;
            font-size: 0.95rem; font-weight: 700; cursor: pointer;
            font-family: 'Inter', sans-serif; transition: all 0.3s;
            width: 100%; text-align: center;
        }
        .btn-buy:hover { background: linear-gradient(135deg, #a78bfa, #7c3aed); transform: scale(1.02); }

        .btn-outline {
            display: inline-block; background: transparent; color: #a78bfa;
            border: 1px solid rgba(167, 139, 250, 0.3); padding: 10px 20px;
            border-radius: 10px; font-size: 0.85rem; font-weight: 600;
            text-decoration: none; text-align: center; transition: all 0.3s;
        }
        .btn-outline:hover { background: rgba(167, 139, 250, 0.1); border-color: #a78bfa; }

        .empty-cart {
            text-align: center; padding: 60px 20px; color: #64748b;
            border: 1px dashed rgba(255,255,255,0.08); border-radius: 12px;
        }

        @media (max-width: 992px) {
            .main-layout { flex-direction: column; align-items: stretch; }
            .checkout-container { position: static; }
        }
    </style>
    <script>
        function handlePaymentChange() {
            const method = document.getElementById("formaPago").value;
            const plazoContainer = document.getElementById("plazo-container");
            const creditBox = document.getElementById("credit-estimation-box");
            
            if (method === "CREDITO") {
                plazoContainer.style.display = "block";
                creditBox.style.display = "block";
            } else {
                plazoContainer.style.display = "none";
                creditBox.style.display = "none";
            }
            
            calculateTotals();
        }

        function calculateTotals() {
            let subtotal = 0;
            const prices = document.getElementsByClassName("cart-item-price");
            for (let i = 0; i < prices.length; i++) {
                subtotal += parseFloat(prices[i].value);
            }
            
            const method = document.getElementById("formaPago").value;
            let discount = 0;
            if (method === "EFECTIVO") {
                discount = subtotal * 0.12;
            }
            
            const iva = (subtotal - discount) * 0.15;
            const total = subtotal - discount + iva;
            
            document.getElementById("summary-subtotal").innerText = "$" + subtotal.toFixed(2);
            document.getElementById("summary-discount").innerText = "-$" + discount.toFixed(2);
            document.getElementById("summary-iva").innerText = "$" + iva.toFixed(2);
            document.getElementById("summary-total").innerText = "$" + total.toFixed(2);
            
            if (method === "EFECTIVO") {
                document.getElementById("summary-discount-row").style.display = "flex";
            } else {
                document.getElementById("summary-discount-row").style.display = "none";
            }
            
            if (method === "CREDITO") {
                const plazo = parseInt(document.getElementById("plazoMeses").value);
                const annualRate = 0.165;
                const monthlyRate = annualRate / 12;
                
                let cuota = 0;
                if (total > 0) {
                    const temp = Math.pow(1 + monthlyRate, plazo);
                    cuota = total * (monthlyRate * temp) / (temp - 1);
                }
                
                document.getElementById("estimated-monthly-payment").innerText = "$" + cuota.toFixed(2);
            }
        }

        window.onload = function() {
            calculateTotals();
        };
    </script>
</head>
<body>
    <%
        List<PeticionCompra> cart = (List<PeticionCompra>) session.getAttribute("carrito_compras");
        Map<Integer, PartidoFutbol> matchMap = (Map<Integer, PartidoFutbol>) request.getAttribute("matchMap");
        if (matchMap == null) {
            matchMap = new HashMap<Integer, PartidoFutbol>();
        }
    %>

    <div class="header">
        <a href="ticket?accion=partidos" class="back-link">← Volver a Partidos</a>
        <h1>🛒 Carrito de Compras</h1>
        <p>Copa Mundial de Fútbol FIFA 2026</p>
    </div>

    <div class="container">
        <% if (cart != null && !cart.isEmpty()) { %>
        
        <div class="main-layout">
            <!-- Left Column: Cart items -->
            <div class="cart-items-container">
                <h2 style="font-size: 1.25rem; font-weight: 700; color: #fff; margin-bottom: 20px;">Boletos en el Carrito</h2>
                
                <form id="cartForm" method="post" action="ticket">
                    <input type="hidden" name="accion" value="finalizarCompra"/>
                    
                    <%
                        for (int i = 0; i < cart.size(); i++) {
                            PeticionCompra item = cart.get(i);
                            PartidoFutbol partido = matchMap.get(item.getCodigoPartido());
                            String badgeClass = "badge-general";
                            if ("PALCO".equalsIgnoreCase(item.getCodigoLocalidad())) badgeClass = "badge-palco";
                            else if ("TRIBUNA".equalsIgnoreCase(item.getCodigoLocalidad())) badgeClass = "badge-tribuna";
                    %>
                    <div class="cart-item">
                        <div class="item-details">
                            <div class="item-match">
                                <% if (partido != null) { %>
                                    <%= FormatUtil.tilde(partido.getEquipoLocal()) %> vs <%= FormatUtil.tilde(partido.getEquipoVisita()) %>
                                <% } else { %>
                                    Partido #<%= item.getCodigoPartido() %>
                                <% } %>
                            </div>
                            <div class="item-info">
                                <% if (partido != null) { %>
                                    📅 <%= partido.getFecha() %> &nbsp;|&nbsp; 🏟️ <%= FormatUtil.tilde(partido.getLugar()) %>
                                <% } %>
                            </div>
                            <div>
                                <span class="item-seat-badge <%= badgeClass %>">
                                    <%= item.getCodigoLocalidad() %> 
                                    <% if (item.getIdAsientoPartido() > 0) { %>
                                        — Asiento #<%= item.getIdAsientoPartido() %>
                                    <% } %>
                                </span>
                            </div>
                            
                            <!-- Index identifier -->
                            <input type="hidden" name="cartIndex" value="<%= i %>"/>
                            <!-- Input to dynamically change occupant name -->
                            <div class="item-occupant-input">
                                <label>Nombre del Ocupante:</label>
                                <input type="text" name="ocupante_<%= i %>" value="<%= FormatUtil.tilde(item.getNombreOcupante()) %>" required placeholder="Nombre para este boleto"/>
                            </div>
                        </div>
                        
                        <div class="item-price-actions">
                            <div class="item-price">$<%= String.format("%.2f", item.getPrecioUnitario()) %></div>
                            <!-- Hidden input for javascript calculation -->
                            <input type="hidden" class="cart-item-price" value="<%= item.getPrecioUnitario() %>"/>
                            
                            <a href="ticket?accion=eliminarCarrito&index=<%= i %>" class="btn-remove">❌ Eliminar</a>
                        </div>
                    </div>
                    <%
                        }
                    %>
                
            </div>

            <!-- Right Column: Finalize Checkout and details -->
            <div class="checkout-container">
                <h2 style="font-size: 1.25rem; font-weight: 800; color: #fff; margin-bottom: 16px; border-bottom: 1px solid rgba(255,255,255,0.08); padding-bottom: 10px;">
                    💳 Detalles de Facturación
                </h2>

                <!-- Invoice summary -->
                <div style="background: rgba(0,0,0,0.2); border-radius: 12px; padding: 16px; margin-bottom: 20px; border: 1px solid rgba(255,255,255,0.03);">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 0.9rem; color: #94a3b8;">
                        <span>Subtotal:</span>
                        <span id="summary-subtotal" style="font-weight: 600; color: #cbd5e1;">$0.00</span>
                    </div>
                    <div id="summary-discount-row" style="display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 0.9rem; color: #94a3b8;">
                        <span>Descuento (12%):</span>
                        <span id="summary-discount" style="font-weight: 600; color: #f43f5e;">-$0.00</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 0.9rem; color: #94a3b8;">
                        <span>IVA (15%):</span>
                        <span id="summary-iva" style="font-weight: 600; color: #cbd5e1;">$0.00</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding-top: 10px; border-top: 1px solid rgba(255,255,255,0.08);">
                        <span style="font-weight: bold; color: #fff;">Total a Pagar:</span>
                        <span id="summary-total" style="font-weight: bold; color: #10b981; font-size: 1.25rem;">$0.00</span>
                    </div>
                    
                    <!-- Credit Monthly Payment Estimation (French formula) -->
                    <div id="credit-estimation-box" style="display: none; margin-top: 12px; padding: 10px; background: rgba(192, 132, 252, 0.1); border: 1px solid rgba(192, 132, 252, 0.2); border-radius: 8px; font-size: 0.8rem; text-align: center; color: #e9d5ff;">
                        <span>Cuota Mensual Estimada: </span>
                        <strong id="estimated-monthly-payment" style="font-size: 0.95rem; color: #fff;">$0.00</strong>
                    </div>
                </div>

                <!-- Input billing details -->
                <div style="display: flex; flex-direction: column; gap: 12px;">
                    <div>
                        <label style="font-size: 0.8rem; color: #94a3b8; font-weight: 600; display: block; margin-bottom: 4px;">Cédula del Cliente (Banco):</label>
                        <input type="text" name="cedulaCliente" id="cedulaCliente" required placeholder="Ej: 1726354712"
                               style="width: 100%; padding: 10px 14px; border-radius: 8px; border: 1px solid #334155; background: #0f172a; color: #e2e8f0; font-size: 0.9rem;">
                    </div>
                    
                    <div>
                        <label style="font-size: 0.8rem; color: #94a3b8; font-weight: 600; display: block; margin-bottom: 4px;">Nombre del Cliente:</label>
                        <input type="text" name="nombreCliente" id="nombreCliente" required placeholder="Nombre para la factura"
                               style="width: 100%; padding: 10px 14px; border-radius: 8px; border: 1px solid #334155; background: #0f172a; color: #e2e8f0; font-size: 0.9rem;">
                    </div>

                    <div>
                        <label style="font-size: 0.8rem; color: #94a3b8; font-weight: 600; display: block; margin-bottom: 4px;">Forma de Pago:</label>
                        <select name="formaPago" id="formaPago" onchange="handlePaymentChange()"
                                style="width: 100%; padding: 10px 14px; border-radius: 8px; border: 1px solid #334155; background: #0f172a; color: #e2e8f0; font-size: 0.9rem;">
                            <option value="EFECTIVO">Efectivo (12% Descuento)</option>
                            <option value="CREDITO">Crédito Directo (Amortización Francesa)</option>
                        </select>
                    </div>

                    <div id="plazo-container" style="display: none;">
                        <label style="font-size: 0.8rem; color: #94a3b8; font-weight: 600; display: block; margin-bottom: 4px;">Plazo (Meses):</label>
                        <select name="plazoMeses" id="plazoMeses" onchange="calculateTotals()"
                                style="width: 100%; padding: 10px 14px; border-radius: 8px; border: 1px solid #334155; background: #0f172a; color: #e2e8f0; font-size: 0.9rem;">
                            <option value="3">3 Meses</option>
                            <option value="6" selected>6 Meses</option>
                            <option value="9">9 Meses</option>
                            <option value="12">12 Meses</option>
                            <option value="18">18 Meses</option>
                        </select>
                    </div>

                    <button type="submit" class="btn-buy" style="margin-top: 10px; display: flex; align-items: center; justify-content: center; gap: 8px;">
                        💳 Confirmar y Pagar
                    </button>
                    
                    <a href="ticket?accion=partidos" class="btn-outline" style="margin-top: 4px;">➕ Agregar Más Partidos</a>
                </div>
                </form>
                <% String error = (String) request.getAttribute("error"); if (error != null) { %>
                    <div style="margin-top: 16px; color: #ef4444; font-size: 0.9rem; font-weight: 600; text-align: center;"><%= error %></div>
                <% } %>
            </div>
        </div>
        
        <% } else { %>
            <div class="empty-cart">
                <span style="font-size: 3rem; display: block; margin-bottom: 16px;">🛒</span>
                <p style="font-size: 1.1rem; font-weight: 600; color: #fff; margin-bottom: 8px;">Tu carrito está vacío</p>
                <p style="margin-bottom: 24px;">Selecciona asientos de los partidos disponibles para agregarlos aquí.</p>
                <a href="ticket?accion=partidos" class="btn-outline">Ver Partidos Disponibles</a>
            </div>
        <% } %>
    </div>
</body>
</html>
