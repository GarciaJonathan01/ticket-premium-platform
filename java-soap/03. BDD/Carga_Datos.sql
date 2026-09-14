-- =============================================================
-- DATOS INICIALES PARA BANCO CORE Y TICKETPREMIUM FIFA WORLD CUP 2026
-- =============================================================

-- =============================================================
-- 1. CARGA DE DATOS: TICKETPREMIUM
-- =============================================================
USE ticketpremium_db;

-- Usuario Administrativo
INSERT INTO USUARIO (USERNAME, PASSWORD) VALUES ('MONSTER', 'MONSTER9');

-- Países (Nombres sin acento para BD, mapeados en UI con acento)
INSERT INTO PAIS (NOMBRE) VALUES
('Estados Unidos'), ('Canada'), ('Mexico'), ('Alemania'), ('Ecuador'), 
('Brasil'), ('Marruecos'), ('Haiti'), ('Escocia'), ('Sudafrica'), 
('Corea del Sur'), ('Suiza'), ('Catar'), ('Paraguay'), ('Curazao'), 
('Japon'), ('Costa de Marfil'), ('Tunez'), ('Espana'), ('Cabo Verde'), 
('Belgica'), ('Egipto'), ('Arabia Saudita'), ('Uruguay'), ('Iran'), 
('Nueva Zelanda'), ('Francia'), ('Senegal'), ('Noruega'), ('Argentina'), 
('Argelia'), ('Austria'), ('Jordania'), ('Portugal'), ('Colombia'), 
('Uzbekistan'), ('Inglaterra'), ('Croacia'), ('Panama'), ('Ghana'), 
('Australia'), ('Paises Bajos');

-- Equipos / Selecciones
INSERT INTO EQUIPO (NOMBRE, ID_PAIS) VALUES
('Estados Unidos', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos')),
('Canada', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Canada')),
('Mexico', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Mexico')),
('Alemania', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Alemania')),
('Ecuador', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Ecuador')),
('Brasil', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Brasil')),
('Marruecos', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Marruecos')),
('Haiti', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Haiti')),
('Escocia', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Escocia')),
('Sudafrica', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Sudafrica')),
('Corea del Sur', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Corea del Sur')),
('Suiza', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Suiza')),
('Catar', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Catar')),
('Paraguay', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Paraguay')),
('Curazao', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Curazao')),
('Japon', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Japon')),
('Costa de Marfil', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Costa de Marfil')),
('Tunez', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Tunez')),
('Espana', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Espana')),
('Cabo Verde', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Cabo Verde')),
('Belgica', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Belgica')),
('Egipto', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Egipto')),
('Arabia Saudita', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Arabia Saudita')),
('Uruguay', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Uruguay')),
('Iran', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Iran')),
('Nueva Zelanda', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Nueva Zelanda')),
('Francia', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Francia')),
('Senegal', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Senegal')),
('Noruega', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Noruega')),
('Argentina', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Argentina')),
('Argelia', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Argelia')),
('Austria', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Austria')),
('Jordania', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Jordania')),
('Portugal', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Portugal')),
('Colombia', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Colombia')),
('Uzbekistan', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Uzbekistan')),
('Inglaterra', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Inglaterra')),
('Croacia', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Croacia')),
('Panama', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Panama')),
('Ghana', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Ghana')),
('Australia', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Australia')),
('Paises Bajos', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Paises Bajos')),
('UEFA A', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos')),
('UEFA B', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Mexico')),
('UEFA C', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Canada')),
('UEFA D', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Mexico')),
('Repechaje 1', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos')),
('Repechaje 2', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Canada'));

-- Estadios Sedes Reales Mundial 2026
INSERT INTO ESTADIO (NOMBRE, CIUDAD, ID_PAIS, CAPACIDAD) VALUES
('Estadio Ciudad de Mexico', 'Ciudad de Mexico', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Mexico'), 87523),
('Estadio Monterrey', 'Monterrey', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Mexico'), 53500),
('Estadio Guadalajara', 'Guadalajara', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Mexico'), 48071),
('Toronto Stadium', 'Toronto', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Canada'), 45000),
('BC Place Vancouver', 'Vancouver', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Canada'), 54500),
('Los Angeles Stadium', 'Los Angeles', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos'), 70240),
('San Francisco Area Stadium', 'Santa Clara', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos'), 68500),
('Nueva Jersey Stadium', 'East Rutherford', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos'), 82500),
('Boston Stadium', 'Foxborough', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos'), 65878),
('Houston Stadium', 'Houston', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos'), 72220),
('Dallas Stadium', 'Arlington', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos'), 80000),
('Philadelphia Stadium', 'Filadelfia', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos'), 69796),
('Atlanta Stadium', 'Atlanta', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos'), 71000),
('Seattle Stadium', 'Seattle', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos'), 69000),
('Miami Stadium', 'Miami', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos'), 64767),
('Kansas City Stadium', 'Kansas City', (SELECT ID_PAIS FROM PAIS WHERE NOMBRE = 'Estados Unidos'), 76416);

-- Partidos del Mundial (10 partidos del Mundial FIFA 2026)
INSERT INTO PARTIDO_FUTBOL (ID_EQUIPO_LOCAL, ID_EQUIPO_VISITA, FECHA, ID_ESTADIO, GRUPO) VALUES
((SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Mexico'), (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Sudafrica'), DATE_ADD(NOW(), INTERVAL 2 DAY), (SELECT ID_ESTADIO FROM ESTADIO WHERE NOMBRE = 'Estadio Ciudad de Mexico'), 'A'),
((SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Corea del Sur'), (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'UEFA D'), DATE_ADD(NOW(), INTERVAL 2 DAY + INTERVAL 4 HOUR), (SELECT ID_ESTADIO FROM ESTADIO WHERE NOMBRE = 'Estadio Guadalajara'), 'A'),
((SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Canada'), (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'UEFA A'), DATE_ADD(NOW(), INTERVAL 3 DAY), (SELECT ID_ESTADIO FROM ESTADIO WHERE NOMBRE = 'Toronto Stadium'), 'B'),
((SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Estados Unidos'), (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Paraguay'), DATE_ADD(NOW(), INTERVAL 3 DAY + INTERVAL 5 HOUR), (SELECT ID_ESTADIO FROM ESTADIO WHERE NOMBRE = 'Los Angeles Stadium'), 'D'),
((SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Catar'), (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Suiza'), DATE_ADD(NOW(), INTERVAL 4 DAY), (SELECT ID_ESTADIO FROM ESTADIO WHERE NOMBRE = 'San Francisco Area Stadium'), 'B'),
((SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Brasil'), (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Marruecos'), DATE_ADD(NOW(), INTERVAL 4 DAY + INTERVAL 2 HOUR), (SELECT ID_ESTADIO FROM ESTADIO WHERE NOMBRE = 'Nueva Jersey Stadium'), 'C'),
((SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Haiti'), (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Escocia'), DATE_ADD(NOW(), INTERVAL 4 DAY + INTERVAL 5 HOUR), (SELECT ID_ESTADIO FROM ESTADIO WHERE NOMBRE = 'Boston Stadium'), 'C'),
((SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Australia'), (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'UEFA C'), DATE_ADD(NOW(), INTERVAL 5 DAY), (SELECT ID_ESTADIO FROM ESTADIO WHERE NOMBRE = 'BC Place Vancouver'), 'C'),
((SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Alemania'), (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Curazao'), DATE_ADD(NOW(), INTERVAL 5 DAY + INTERVAL 3 HOUR), (SELECT ID_ESTADIO FROM ESTADIO WHERE NOMBRE = 'Houston Stadium'), 'E'),
((SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Paises Bajos'), (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = 'Japon'), DATE_ADD(NOW(), INTERVAL 5 DAY + INTERVAL 6 HOUR), (SELECT ID_ESTADIO FROM ESTADIO WHERE NOMBRE = 'Dallas Stadium'), 'F');

-- Clientes registrados en TicketPremium
INSERT INTO CLIENTE (CEDULA, NOMBRE, EMAIL, TELEFONO) VALUES
('1726354712', 'JUAN PEREZ', 'juan.perez@test.com', '0991234567'),
('1756123456', 'MARIA ALVEAR', 'maria.alvear@test.com', '0995556677'),
('1798765432', 'CARLOS LOPEZ', 'carlos.lopez@test.com', '0987654321');

-- Procedimiento almacenado para insertar asientos por cada uno de los 16 estadios
-- e inicializar localidades y disponibilidad por cada partido
DELIMITER $$
CREATE PROCEDURE PopulaAsientosYPartidos()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE stadId INT;
    DECLARE stadName VARCHAR(100);
    DECLARE matchId INT;
    DECLARE i INT;
    DECLARE numPalcos INT;
    DECLARE numTribunas INT;
    DECLARE numGenerales INT;
    
    DECLARE curStad CURSOR FOR SELECT ID_ESTADIO, NOMBRE FROM ESTADIO;
    DECLARE curMatch CURSOR FOR SELECT p.CODIGO, p.ID_ESTADIO, e.NOMBRE FROM PARTIDO_FUTBOL p JOIN ESTADIO e ON p.ID_ESTADIO = e.ID_ESTADIO;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 1. Popular plantilla de asientos
    OPEN curStad;
    read_stad: LOOP
        FETCH curStad INTO stadId, stadName;
        IF done THEN
            LEAVE read_stad;
        END IF;
        
        -- Determine seat counts based on stadium name
        IF stadName LIKE '%Ciudad de Mexico%' THEN
            SET numPalcos = 20, numTribunas = 30, numGenerales = 50;
        ELSEIF stadName LIKE '%Monterrey%' THEN
            SET numPalcos = 14, numTribunas = 20, numGenerales = 36;
        ELSEIF stadName LIKE '%Guadalajara%' THEN
            SET numPalcos = 12, numTribunas = 18, numGenerales = 30;
        ELSEIF stadName LIKE '%Toronto%' THEN
            SET numPalcos = 10, numTribunas = 15, numGenerales = 25;
        ELSEIF stadName LIKE '%Vancouver%' THEN
            SET numPalcos = 14, numTribunas = 22, numGenerales = 38;
        ELSEIF stadName LIKE '%Los Angeles%' THEN
            SET numPalcos = 18, numTribunas = 26, numGenerales = 46;
        ELSEIF stadName LIKE '%San Francisco%' THEN
            SET numPalcos = 16, numTribunas = 24, numGenerales = 44;
        ELSEIF stadName LIKE '%Nueva Jersey%' THEN
            SET numPalcos = 20, numTribunas = 28, numGenerales = 48;
        ELSEIF stadName LIKE '%Boston%' THEN
            SET numPalcos = 15, numTribunas = 22, numGenerales = 40;
        ELSEIF stadName LIKE '%Houston%' THEN
            SET numPalcos = 18, numTribunas = 25, numGenerales = 45;
        ELSEIF stadName LIKE '%Dallas%' THEN
            SET numPalcos = 20, numTribunas = 28, numGenerales = 50;
        ELSEIF stadName LIKE '%Philadelphia%' OR stadName LIKE '%Filadelfia%' THEN
            SET numPalcos = 16, numTribunas = 24, numGenerales = 44;
        ELSEIF stadName LIKE '%Atlanta%' THEN
            SET numPalcos = 18, numTribunas = 25, numGenerales = 45;
        ELSEIF stadName LIKE '%Seattle%' THEN
            SET numPalcos = 16, numTribunas = 24, numGenerales = 44;
        ELSEIF stadName LIKE '%Miami%' THEN
            SET numPalcos = 15, numTribunas = 22, numGenerales = 40;
        ELSEIF stadName LIKE '%Kansas City%' THEN
            SET numPalcos = 18, numTribunas = 26, numGenerales = 46;
        ELSE
            SET numPalcos = 10, numTribunas = 15, numGenerales = 25;
        END IF;
        
        -- Palcos (Row P)
        SET i = 1;
        while_palco: WHILE i <= numPalcos DO
            INSERT INTO ASIENTO (ID_ESTADIO, SECCION, FILA, NUMERO) VALUES (stadId, 'PALCO', 'P', i);
            SET i = i + 1;
        END WHILE while_palco;
        
        -- Tribunas (Row T)
        SET i = 1;
        while_trib: WHILE i <= numTribunas DO
            INSERT INTO ASIENTO (ID_ESTADIO, SECCION, FILA, NUMERO) VALUES (stadId, 'TRIBUNA', 'T', i);
            SET i = i + 1;
        END WHILE while_trib;
        
        -- Generales (Row G)
        SET i = 1;
        while_gen: WHILE i <= numGenerales DO
            INSERT INTO ASIENTO (ID_ESTADIO, SECCION, FILA, NUMERO) VALUES (stadId, 'GENERAL', 'G', i);
            SET i = i + 1;
        END WHILE while_gen;
        
    END LOOP;
    CLOSE curStad;
    
    -- Reset flag
    SET done = FALSE;
    
    -- 2. Inicializar Localidades y Estados de Asientos por cada partido programado
    OPEN curMatch;
    read_match: LOOP
        FETCH curMatch INTO matchId, stadId, stadName;
        IF done THEN
            LEAVE read_match;
        END IF;
        
        -- Determine seat counts based on stadium name for match localidades
        IF stadName LIKE '%Ciudad de Mexico%' THEN
            SET numPalcos = 20, numTribunas = 30, numGenerales = 50;
        ELSEIF stadName LIKE '%Monterrey%' THEN
            SET numPalcos = 14, numTribunas = 20, numGenerales = 36;
        ELSEIF stadName LIKE '%Guadalajara%' THEN
            SET numPalcos = 12, numTribunas = 18, numGenerales = 30;
        ELSEIF stadName LIKE '%Toronto%' THEN
            SET numPalcos = 10, numTribunas = 15, numGenerales = 25;
        ELSEIF stadName LIKE '%Vancouver%' THEN
            SET numPalcos = 14, numTribunas = 22, numGenerales = 38;
        ELSEIF stadName LIKE '%Los Angeles%' THEN
            SET numPalcos = 18, numTribunas = 26, numGenerales = 46;
        ELSEIF stadName LIKE '%San Francisco%' THEN
            SET numPalcos = 16, numTribunas = 24, numGenerales = 44;
        ELSEIF stadName LIKE '%Nueva Jersey%' THEN
            SET numPalcos = 20, numTribunas = 28, numGenerales = 48;
        ELSEIF stadName LIKE '%Boston%' THEN
            SET numPalcos = 15, numTribunas = 22, numGenerales = 40;
        ELSEIF stadName LIKE '%Houston%' THEN
            SET numPalcos = 18, numTribunas = 25, numGenerales = 45;
        ELSEIF stadName LIKE '%Dallas%' THEN
            SET numPalcos = 20, numTribunas = 28, numGenerales = 50;
        ELSEIF stadName LIKE '%Philadelphia%' OR stadName LIKE '%Filadelfia%' THEN
            SET numPalcos = 16, numTribunas = 24, numGenerales = 44;
        ELSEIF stadName LIKE '%Atlanta%' THEN
            SET numPalcos = 18, numTribunas = 25, numGenerales = 45;
        ELSEIF stadName LIKE '%Seattle%' THEN
            SET numPalcos = 16, numTribunas = 24, numGenerales = 44;
        ELSEIF stadName LIKE '%Miami%' THEN
            SET numPalcos = 15, numTribunas = 22, numGenerales = 40;
        ELSEIF stadName LIKE '%Kansas City%' THEN
            SET numPalcos = 18, numTribunas = 26, numGenerales = 46;
        ELSE
            SET numPalcos = 10, numTribunas = 15, numGenerales = 25;
        END IF;
        
        -- Crear existencias de localidades por partido
        INSERT INTO LOCALIDAD_PARTIDO (CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO) VALUES
        (matchId, 'PALCO', numPalcos, 150.00),
        (matchId, 'TRIBUNA', numTribunas, 80.00),
        (matchId, 'GENERAL', numGenerales, 40.00);
        
        -- Instanciar estado de asientos para el partido
        INSERT INTO ASIENTO_PARTIDO (CODIGO_PARTIDO, ID_ASIENTO, ESTADO)
        SELECT matchId, ID_ASIENTO, 'DISPONIBLE'
        FROM ASIENTO
        WHERE ID_ESTADIO = stadId;
        
    END LOOP;
    CLOSE curMatch;
    
END$$
DELIMITER ;


CALL PopulaAsientosYPartidos();
DROP PROCEDURE PopulaAsientosYPartidos;


-- =============================================================
-- 2. CARGA DE DATOS: BANCO CORE
-- =============================================================
USE banco_core_db;

-- Clientes Financieros
INSERT INTO CLIENTE (CEDULA, NOMBRE, GENERO, FECHA_NACIMIENTO) VALUES
('1726354712', 'JUAN PEREZ', 'M', '1996-05-10'),
('1798765432', 'CARLOS LOPEZ', 'M', '2004-08-15'),
('1756123456', 'MARIA ALVEAR', 'F', '2006-03-20'),
('1712345678', 'JOSE CASTRO', 'M', '1981-12-01'),
('1787654321', 'LUIS MORENO', 'M', '1991-01-10');

-- Cuentas de Ahorros
INSERT INTO CUENTA (NUM_CUENTA, COD_CLIENTE, SALDO) VALUES
('CTA00001', (SELECT COD_CLIENTE FROM CLIENTE WHERE NOMBRE = 'JUAN PEREZ'), 1500.00),
('CTA00002', (SELECT COD_CLIENTE FROM CLIENTE WHERE NOMBRE = 'CARLOS LOPEZ'), 200.00),
('CTA00003', (SELECT COD_CLIENTE FROM CLIENTE WHERE NOMBRE = 'MARIA ALVEAR'), 2500.00),
('CTA00004', (SELECT COD_CLIENTE FROM CLIENTE WHERE NOMBRE = 'JOSE CASTRO'), 100.00),
('CTA00005', (SELECT COD_CLIENTE FROM CLIENTE WHERE NOMBRE = 'LUIS MORENO'), 4000.00);

-- Movimientos Transaccionales (Últimos 3 meses para cálculo de capacidad crediticia)
-- CTA00001 (Juan Pérez): Dep: 800, 700, 900. Ret: 300, 200, 400. Promedio neto mensual: 500. Capacidad máx: 900.
INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES
('CTA00001', 'DEP', 800.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
('CTA00001', 'DEP', 700.00, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
('CTA00001', 'DEP', 900.00, DATE_SUB(CURDATE(), INTERVAL 75 DAY)),
('CTA00001', 'RET', 300.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
('CTA00001', 'RET', 200.00, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
('CTA00001', 'RET', 400.00, DATE_SUB(CURDATE(), INTERVAL 75 DAY));

-- CTA00002 (Carlos López): Menor de 25 masculino. Debe ser rechazado por edad.
INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES
('CTA00002', 'DEP', 500.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
('CTA00002', 'DEP', 400.00, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
('CTA00002', 'DEP', 600.00, DATE_SUB(CURDATE(), INTERVAL 75 DAY)),
('CTA00002', 'RET', 100.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
('CTA00002', 'RET', 150.00, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
('CTA00002', 'RET', 120.00, DATE_SUB(CURDATE(), INTERVAL 75 DAY));

-- CTA00003 (María Alvear): Mujer (aplica a cualquier edad). Promedio dep: 1766. Ret: 500. Capacidad máx: 2280.
INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES
('CTA00003', 'DEP', 2000.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
('CTA00003', 'DEP', 1500.00, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
('CTA00003', 'DEP', 1800.00, DATE_SUB(CURDATE(), INTERVAL 75 DAY)),
('CTA00003', 'RET', 500.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
('CTA00003', 'RET', 600.00, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
('CTA00003', 'RET', 400.00, DATE_SUB(CURDATE(), INTERVAL 75 DAY));

-- CTA00004 (José Castro): No registra depósitos en el último mes. Debe ser rechazado.
INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES
('CTA00004', 'DEP', 1000.00, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
('CTA00004', 'DEP', 1000.00, DATE_SUB(CURDATE(), INTERVAL 75 DAY)),
('CTA00004', 'RET', 200.00, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
('CTA00004', 'RET', 200.00, DATE_SUB(CURDATE(), INTERVAL 75 DAY));

-- CTA00005 (Luis Moreno): Tiene un crédito ACTIVO en curso. Debe ser rechazado.
INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES
('CTA00005', 'DEP', 1000.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
('CTA00005', 'DEP', 1000.00, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
('CTA00005', 'DEP', 1000.00, DATE_SUB(CURDATE(), INTERVAL 75 DAY));

-- Registro de Crédito Activo e Historial de Amortizaciones para Luis Moreno
INSERT INTO CREDITO (COD_CLIENTE, NUM_CUENTA, MONTO_PRESTAMO, INTERES_ANUAL, PLAZO_MESES, CUOTA_MENSUAL, FECHA_APROBACION, ESTADO) VALUES
((SELECT COD_CLIENTE FROM CLIENTE WHERE NOMBRE = 'LUIS MORENO'), 'CTA00005', 1000.00, 16.50, 6, 174.77, DATE_SUB(CURDATE(), INTERVAL 1 MONTH), 'ACTIVO');

-- Guardar amortización del crédito activo en la base de datos
SET @cred_id = LAST_INSERT_ID();
INSERT INTO TABLA_AMORTIZACION (ID_CREDITO, NUM_CUOTA, VALOR_CUOTA, INTERES_PAGADO, CAPITAL_PAGADO, SALDO) VALUES
(@cred_id, 0, 0.00, 0.00, 0.00, 1000.00),
(@cred_id, 1, 174.77, 13.75, 161.02, 838.98),
(@cred_id, 2, 174.77, 11.54, 163.23, 675.75),
(@cred_id, 3, 174.77, 9.29, 165.48, 510.27),
(@cred_id, 4, 174.77, 7.02, 167.75, 342.52),
(@cred_id, 5, 174.77, 4.71, 170.06, 172.46),
(@cred_id, 6, 174.77, 2.37, 172.46, 0.00);
