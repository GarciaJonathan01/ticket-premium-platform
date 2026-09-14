import os
import pymysql
from datetime import datetime, timedelta

def run_setup():
    conn = pymysql.connect(
        host=os.environ["DB_HOST"],
        user=os.environ["DB_USER"],
        password=os.environ["DB_PASSWORD"],
        port=int(os.getenv("DB_PORT", "3306")),
        autocommit=True
    )
    cursor = conn.cursor()

    print("Creating databases...")
    cursor.execute("DROP DATABASE IF EXISTS ticketpremium_db")
    cursor.execute("DROP DATABASE IF EXISTS banco_core_db")
    
    cursor.execute("CREATE DATABASE ticketpremium_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci")
    cursor.execute("CREATE DATABASE banco_core_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci")
    
    # -------------------------------------------------------------
    # BANCO CORE SCHEMA
    # -------------------------------------------------------------
    print("Creating banco_core_db tables...")
    cursor.execute("USE banco_core_db")
    
    cursor.execute("""
    CREATE TABLE CLIENTE (
        COD_CLIENTE INT NOT NULL AUTO_INCREMENT,
        CEDULA VARCHAR(100) NOT NULL UNIQUE,
        NOMBRE VARCHAR(100) NOT NULL,
        GENERO VARCHAR(1) NOT NULL,
        FECHA_NACIMIENTO DATE NOT NULL,
        CONSTRAINT PK_CLIENTE PRIMARY KEY (COD_CLIENTE)
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE CUENTA (
        NUM_CUENTA VARCHAR(8) NOT NULL,
        COD_CLIENTE INT NOT NULL,
        SALDO DECIMAL(10,2) NOT NULL DEFAULT 0.00,
        CONSTRAINT PK_CUENTA PRIMARY KEY (NUM_CUENTA),
        CONSTRAINT FK_CUENTA_CLIENTE FOREIGN KEY (COD_CLIENTE) REFERENCES CLIENTE(COD_CLIENTE) ON DELETE CASCADE
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE MOVIMIENTO (
        COD_MOVIMIENTO INT NOT NULL AUTO_INCREMENT,
        NUM_CUENTA VARCHAR(8) NOT NULL,
        TIPO VARCHAR(3) NOT NULL, -- 'DEP' o 'RET'
        VALOR DECIMAL(10,2) NOT NULL,
        FECHA DATE NOT NULL,
        CONSTRAINT PK_MOVIMIENTO PRIMARY KEY (COD_MOVIMIENTO),
        CONSTRAINT FK_MOVIMIENTO_CUENTA FOREIGN KEY (NUM_CUENTA) REFERENCES CUENTA(NUM_CUENTA) ON DELETE CASCADE
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE CREDITO (
        ID_CREDITO INT NOT NULL AUTO_INCREMENT,
        COD_CLIENTE INT NOT NULL,
        NUM_CUENTA VARCHAR(8) NOT NULL,
        MONTO_PRESTAMO DECIMAL(10,2) NOT NULL,
        INTERES_ANUAL DECIMAL(5,2) NOT NULL DEFAULT 16.50,
        PLAZO_MESES INT NOT NULL,
        CUOTA_MENSUAL DECIMAL(10,2) NOT NULL,
        FECHA_APROBACION DATE NOT NULL,
        ESTADO VARCHAR(20) NOT NULL DEFAULT 'APROBADO',
        CONSTRAINT PK_CREDITO PRIMARY KEY (ID_CREDITO),
        CONSTRAINT FK_CREDITO_CLIENTE FOREIGN KEY (COD_CLIENTE) REFERENCES CLIENTE(COD_CLIENTE) ON DELETE CASCADE,
        CONSTRAINT FK_CREDITO_CUENTA FOREIGN KEY (NUM_CUENTA) REFERENCES CUENTA(NUM_CUENTA) ON DELETE CASCADE
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE TABLA_AMORTIZACION (
        ID_AMORTIZACION INT NOT NULL AUTO_INCREMENT,
        ID_CREDITO INT NOT NULL,
        NUM_CUOTA INT NOT NULL,
        VALOR_CUOTA DECIMAL(10,2) NOT NULL,
        INTERES_PAGADO DECIMAL(10,2) NOT NULL,
        CAPITAL_PAGADO DECIMAL(10,2) NOT NULL,
        SALDO DECIMAL(10,2) NOT NULL,
        CONSTRAINT PK_TABLA_AMORTIZACION PRIMARY KEY (ID_AMORTIZACION),
        CONSTRAINT FK_AMORTIZACION_CREDITO FOREIGN KEY (ID_CREDITO) REFERENCES CREDITO(ID_CREDITO) ON DELETE CASCADE
    ) ENGINE=InnoDB
    """)

    # -------------------------------------------------------------
    # TICKETPREMIUM SCHEMA
    # -------------------------------------------------------------
    print("Creating ticketpremium_db tables...")
    cursor.execute("USE ticketpremium_db")
    
    cursor.execute("""
    CREATE TABLE PAIS (
        ID_PAIS INT NOT NULL AUTO_INCREMENT,
        NOMBRE VARCHAR(100) NOT NULL UNIQUE,
        CONSTRAINT PK_PAIS PRIMARY KEY (ID_PAIS)
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE ESTADIO (
        ID_ESTADIO INT NOT NULL AUTO_INCREMENT,
        NOMBRE VARCHAR(100) NOT NULL,
        CIUDAD VARCHAR(100) NOT NULL,
        ID_PAIS INT NOT NULL,
        CAPACIDAD INT NOT NULL,
        CONSTRAINT PK_ESTADIO PRIMARY KEY (ID_ESTADIO),
        CONSTRAINT FK_ESTADIO_PAIS FOREIGN KEY (ID_PAIS) REFERENCES PAIS(ID_PAIS) ON DELETE CASCADE
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE EQUIPO (
        ID_EQUIPO INT NOT NULL AUTO_INCREMENT,
        NOMBRE VARCHAR(100) NOT NULL UNIQUE,
        ID_PAIS INT NOT NULL,
        CONSTRAINT PK_EQUIPO PRIMARY KEY (ID_EQUIPO),
        CONSTRAINT FK_EQUIPO_PAIS FOREIGN KEY (ID_PAIS) REFERENCES PAIS(ID_PAIS) ON DELETE CASCADE
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE CLIENTE (
        ID_CLIENTE INT NOT NULL AUTO_INCREMENT,
        CEDULA VARCHAR(20) NOT NULL UNIQUE,
        NOMBRE VARCHAR(100) NOT NULL,
        EMAIL VARCHAR(100) NOT NULL,
        TELEFONO VARCHAR(20) NOT NULL,
        CONSTRAINT PK_CLIENTE PRIMARY KEY (ID_CLIENTE)
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE PARTIDO_FUTBOL (
        CODIGO INT NOT NULL AUTO_INCREMENT,
        ID_EQUIPO_LOCAL INT NOT NULL,
        ID_EQUIPO_VISITA INT NOT NULL,
        FECHA DATETIME NOT NULL,
        ID_ESTADIO INT NOT NULL,
        GRUPO VARCHAR(2) NOT NULL,
        CONSTRAINT PK_PARTIDO_FUTBOL PRIMARY KEY (CODIGO),
        CONSTRAINT FK_PARTIDO_LOCAL FOREIGN KEY (ID_EQUIPO_LOCAL) REFERENCES EQUIPO(ID_EQUIPO) ON DELETE RESTRICT,
        CONSTRAINT FK_PARTIDO_VISITA FOREIGN KEY (ID_EQUIPO_VISITA) REFERENCES EQUIPO(ID_EQUIPO) ON DELETE RESTRICT,
        CONSTRAINT FK_PARTIDO_ESTADIO FOREIGN KEY (ID_ESTADIO) REFERENCES ESTADIO(ID_ESTADIO) ON DELETE RESTRICT
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE LOCALIDAD_PARTIDO (
        ID INT NOT NULL AUTO_INCREMENT,
        CODIGO_PARTIDO INT NOT NULL,
        CODIGO_LOCALIDAD VARCHAR(50) NOT NULL,
        DISPONIBILIDAD INT NOT NULL DEFAULT 0,
        PRECIO DECIMAL(10,2) NOT NULL,
        CONSTRAINT PK_LOCALIDAD_PARTIDO PRIMARY KEY (ID),
        CONSTRAINT FK_LOCALIDAD_PARTIDO FOREIGN KEY (CODIGO_PARTIDO) REFERENCES PARTIDO_FUTBOL(CODIGO) ON DELETE CASCADE
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE ASIENTO (
        ID_ASIENTO INT NOT NULL AUTO_INCREMENT,
        ID_ESTADIO INT NOT NULL,
        SECCION VARCHAR(50) NOT NULL, -- 'PALCO', 'TRIBUNA', 'GENERAL'
        FILA VARCHAR(5) NOT NULL,
        NUMERO INT NOT NULL,
        CONSTRAINT PK_ASIENTO PRIMARY KEY (ID_ASIENTO),
        CONSTRAINT FK_ASIENTO_ESTADIO FOREIGN KEY (ID_ESTADIO) REFERENCES ESTADIO(ID_ESTADIO) ON DELETE CASCADE
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE FACTURA (
        ID INT NOT NULL AUTO_INCREMENT,
        ID_CLIENTE INT NOT NULL,
        FECHA DATETIME NOT NULL,
        SUBTOTAL DECIMAL(10,2) NOT NULL,
        DESCUENTO DECIMAL(10,2) NOT NULL DEFAULT 0.00,
        IVA DECIMAL(10,2) NOT NULL,
        TOTAL DECIMAL(10,2) NOT NULL,
        FORMA_PAGO VARCHAR(20) NOT NULL, -- 'EFECTIVO', 'CREDITO'
        ID_CREDITO INT NULL,
        CONSTRAINT PK_FACTURA PRIMARY KEY (ID),
        CONSTRAINT FK_FACTURA_CLIENTE FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE) ON DELETE RESTRICT
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE DETALLE_FACTURA (
        ID INT NOT NULL AUTO_INCREMENT,
        ID_FACTURA INT NOT NULL,
        CODIGO_PARTIDO INT NOT NULL,
        CODIGO_LOCALIDAD VARCHAR(50) NOT NULL,
        CANTIDAD INT NOT NULL,
        PRECIO_UNITARIO DECIMAL(10,2) NOT NULL,
        SUBTOTAL DECIMAL(10,2) NOT NULL,
        CONSTRAINT PK_DETALLE_FACTURA PRIMARY KEY (ID),
        CONSTRAINT FK_DETALLE_FACTURA FOREIGN KEY (ID_FACTURA) REFERENCES FACTURA(ID) ON DELETE CASCADE,
        CONSTRAINT FK_DETALLE_PARTIDO FOREIGN KEY (CODIGO_PARTIDO) REFERENCES PARTIDO_FUTBOL(CODIGO) ON DELETE RESTRICT
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE ASIENTO_PARTIDO (
        ID_ASIENTO_PARTIDO INT NOT NULL AUTO_INCREMENT,
        CODIGO_PARTIDO INT NOT NULL,
        ID_ASIENTO INT NOT NULL,
        ESTADO VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE', -- 'DISPONIBLE', 'RESERVADO', 'OCUPADO'
        ID_DETALLE_FACTURA INT NULL,
        NOMBRE_OCUPANTE VARCHAR(100) NULL,
        FECHA_RESERVA DATETIME NULL,
        CONSTRAINT PK_ASIENTO_PARTIDO PRIMARY KEY (ID_ASIENTO_PARTIDO),
        CONSTRAINT FK_AP_PARTIDO FOREIGN KEY (CODIGO_PARTIDO) REFERENCES PARTIDO_FUTBOL(CODIGO) ON DELETE CASCADE,
        CONSTRAINT FK_AP_ASIENTO FOREIGN KEY (ID_ASIENTO) REFERENCES ASIENTO(ID_ASIENTO) ON DELETE CASCADE,
        CONSTRAINT FK_AP_DETALLE FOREIGN KEY (ID_DETALLE_FACTURA) REFERENCES DETALLE_FACTURA(ID) ON DELETE SET NULL
    ) ENGINE=InnoDB
    """)
    
    cursor.execute("""
    CREATE TABLE USUARIO (
        ID INT NOT NULL AUTO_INCREMENT,
        USERNAME VARCHAR(50) NOT NULL UNIQUE,
        PASSWORD VARCHAR(100) NOT NULL,
        CONSTRAINT PK_USUARIO PRIMARY KEY (ID)
    ) ENGINE=InnoDB
    """)

    # -------------------------------------------------------------
    # INSERT INITIAL DATA
    # -------------------------------------------------------------
    print("Inserting user database initial data...")
    # Admin User
    cursor.execute("INSERT INTO USUARIO (USERNAME, PASSWORD) VALUES ('MONSTER', 'MONSTER9')")
    
    # Countries (Unaccented)
    countries = ["Estados Unidos", "Canada", "Mexico", "Alemania", "Ecuador", "Brasil", "Marruecos", "Haiti", "Escocia", "Sudafrica", "Corea del Sur", "Suiza", "Catar", "Paraguay", "Curazao", "Japon", "Costa de Marfil", "Tunez", "Espana", "Cabo Verde", "Belgica", "Egipto", "Arabia Saudita", "Uruguay", "Iran", "Nueva Zelanda", "Francia", "Senegal", "Noruega", "Argentina", "Argelia", "Austria", "Jordania", "Portugal", "Colombia", "Uzbekistan", "Inglaterra", "Croacia", "Panama", "Ghana", "Australia", "Paises Bajos"]
    for country in countries:
        cursor.execute("INSERT INTO PAIS (NOMBRE) VALUES (%s)", (country,))
        
    # Get country map
    cursor.execute("SELECT NOMBRE, ID_PAIS FROM PAIS")
    country_map = {row[0]: row[1] for row in cursor.fetchall()}
    
    # Teams (Unaccented)
    teams = [
        ("Estados Unidos", "Estados Unidos"), ("Canada", "Canada"), ("Mexico", "Mexico"),
        ("Alemania", "Alemania"), ("Ecuador", "Ecuador"), ("Brasil", "Brasil"),
        ("Marruecos", "Marruecos"), ("Haiti", "Haiti"), ("Escocia", "Escocia"),
        ("Sudafrica", "Sudafrica"), ("Corea del Sur", "Corea del Sur"), ("Suiza", "Suiza"),
        ("Catar", "Catar"), ("Paraguay", "Paraguay"), ("Curazao", "Curazao"),
        ("Japon", "Japon"), ("Costa de Marfil", "Costa de Marfil"), ("Tunez", "Tunez"),
        ("Espana", "Espana"), ("Cabo Verde", "Cabo Verde"), ("Belgica", "Belgica"),
        ("Egipto", "Egipto"), ("Arabia Saudita", "Arabia Saudita"), ("Uruguay", "Uruguay"),
        ("Iran", "Iran"), ("Nueva Zelanda", "Nueva Zelanda"), ("Francia", "Francia"),
        ("Senegal", "Senegal"), ("Noruega", "Noruega"), ("Argentina", "Argentina"),
        ("Argelia", "Argelia"), ("Austria", "Austria"), ("Jordania", "Jordania"),
        ("Portugal", "Portugal"), ("Colombia", "Colombia"), ("Uzbekistan", "Uzbekistan"),
        ("Inglaterra", "Inglaterra"), ("Croacia", "Croacia"), ("Panama", "Panama"), ("Ghana", "Ghana"),
        ("Australia", "Australia"), ("Paises Bajos", "Paises Bajos"),
        ("UEFA A", "Estados Unidos"), ("UEFA B", "Mexico"), ("UEFA C", "Canada"), ("UEFA D", "Mexico"),
        ("Repechaje 1", "Estados Unidos"), ("Repechaje 2", "Canada")
    ]
    for team_name, country_name in teams:
        cursor.execute("INSERT INTO EQUIPO (NOMBRE, ID_PAIS) VALUES (%s, %s)", (team_name, country_map[country_name]))

    # Get team map
    cursor.execute("SELECT NOMBRE, ID_EQUIPO FROM EQUIPO")
    team_map = {row[0]: row[1] for row in cursor.fetchall()}
    
    # Stadiums (Unaccented)
    stadiums = [
        ("Estadio Ciudad de Mexico", "Ciudad de Mexico", "Mexico", 87523),
        ("Estadio Monterrey", "Monterrey", "Mexico", 53500),
        ("Estadio Guadalajara", "Guadalajara", "Mexico", 48071),
        ("Toronto Stadium", "Toronto", "Canada", 45000),
        ("BC Place Vancouver", "Vancouver", "Canada", 54500),
        ("Los Angeles Stadium", "Los Angeles", "Estados Unidos", 70240),
        ("San Francisco Area Stadium", "Santa Clara", "Estados Unidos", 68500),
        ("Nueva Jersey Stadium", "East Rutherford", "Estados Unidos", 82500),
        ("Boston Stadium", "Foxborough", "Estados Unidos", 65878),
        ("Houston Stadium", "Houston", "Estados Unidos", 72220),
        ("Dallas Stadium", "Arlington", "Estados Unidos", 80000),
        ("Philadelphia Stadium", "Filadelfia", "Estados Unidos", 69796),
        ("Atlanta Stadium", "Atlanta", "Estados Unidos", 71000),
        ("Seattle Stadium", "Seattle", "Estados Unidos", 69000),
        ("Miami Stadium", "Miami", "Estados Unidos", 64767),
        ("Kansas City Stadium", "Kansas City", "Estados Unidos", 76416)
    ]
    for name, city, country, cap in stadiums:
        cursor.execute("INSERT INTO ESTADIO (NOMBRE, CIUDAD, ID_PAIS, CAPACIDAD) VALUES (%s, %s, %s, %s)",
                       (name, city, country_map[country], cap))
                       
    # Get stadium map
    cursor.execute("SELECT NOMBRE, ID_ESTADIO FROM ESTADIO")
    stadium_map = {row[0]: row[1] for row in cursor.fetchall()}

    # Helper function for dynamic stadium capacities
    def get_stadium_seat_counts(stadium_name):
        if "Ciudad de Mexico" in stadium_name:
            return 20, 30, 50
        elif "Monterrey" in stadium_name:
            return 14, 20, 36
        elif "Guadalajara" in stadium_name:
            return 12, 18, 30
        elif "Toronto" in stadium_name:
            return 10, 15, 25
        elif "Vancouver" in stadium_name:
            return 14, 22, 38
        elif "Los Angeles" in stadium_name:
            return 18, 26, 46
        elif "San Francisco" in stadium_name:
            return 16, 24, 44
        elif "Nueva Jersey" in stadium_name:
            return 20, 28, 48
        elif "Boston" in stadium_name:
            return 15, 22, 40
        elif "Houston" in stadium_name:
            return 18, 25, 45
        elif "Dallas" in stadium_name:
            return 20, 28, 50
        elif "Philadelphia" in stadium_name or "Filadelfia" in stadium_name:
            return 16, 24, 44
        elif "Atlanta" in stadium_name:
            return 18, 25, 45
        elif "Seattle" in stadium_name:
            return 16, 24, 44
        elif "Miami" in stadium_name:
            return 15, 22, 40
        elif "Kansas City" in stadium_name:
            return 18, 26, 46
        else:
            return 10, 15, 25

    # Generate seats for each stadium
    print("Generating seats for stadiums...")
    for stad_name, id_stadium in stadium_map.items():
        palcos, tribunas, generales = get_stadium_seat_counts(stad_name)
        # PALCO: Row P, numbers 1-palcos
        for i in range(1, palcos + 1):
            cursor.execute("INSERT INTO ASIENTO (ID_ESTADIO, SECCION, FILA, NUMERO) VALUES (%s, 'PALCO', 'P', %s)", (id_stadium, i))
        # TRIBUNA: Row T, numbers 1-tribunas
        for i in range(1, tribunas + 1):
            cursor.execute("INSERT INTO ASIENTO (ID_ESTADIO, SECCION, FILA, NUMERO) VALUES (%s, 'TRIBUNA', 'T', %s)", (id_stadium, i))
        # GENERAL: Row G, numbers 1-generales
        for i in range(1, generales + 1):
            cursor.execute("INSERT INTO ASIENTO (ID_ESTADIO, SECCION, FILA, NUMERO) VALUES (%s, 'GENERAL', 'G', %s)", (id_stadium, i))

    # Real Matches (Unaccented)
    print("Inserting matches...")
    now = datetime.now()
    dates = [
        now + timedelta(days=2),
        now + timedelta(days=2, hours=4),
        now + timedelta(days=3),
        now + timedelta(days=3, hours=5),
        now + timedelta(days=4),
        now + timedelta(days=4, hours=2),
        now + timedelta(days=4, hours=5),
        now + timedelta(days=5),
        now + timedelta(days=5, hours=3),
        now + timedelta(days=5, hours=6)
    ]
    
    matches = [
        ("Mexico", "Sudafrica", dates[0], "Estadio Ciudad de Mexico", "A"),
        ("Corea del Sur", "UEFA D", dates[1], "Estadio Guadalajara", "A"),
        ("Canada", "UEFA A", dates[2], "Toronto Stadium", "B"),
        ("Estados Unidos", "Paraguay", dates[3], "Los Angeles Stadium", "D"),
        ("Catar", "Suiza", dates[4], "San Francisco Area Stadium", "B"),
        ("Brasil", "Marruecos", dates[5], "Nueva Jersey Stadium", "C"),
        ("Haiti", "Escocia", dates[6], "Boston Stadium", "C"),
        ("Australia", "UEFA C", dates[7], "BC Place Vancouver", "C"),
        ("Alemania", "Curazao", dates[8], "Houston Stadium", "E"),
        ("Paises Bajos", "Japon", dates[9], "Dallas Stadium", "F")
    ]
    
    for local, visita, date, stad, grupo in matches:
        cursor.execute("INSERT INTO PARTIDO_FUTBOL (ID_EQUIPO_LOCAL, ID_EQUIPO_VISITA, FECHA, ID_ESTADIO, GRUPO) VALUES (%s, %s, %s, %s, %s)",
                       (team_map[local], team_map[visita], date.strftime('%Y-%m-%d %H:%M:%S'), stadium_map[stad], grupo))

    # Get matches codes with stadium name
    cursor.execute("SELECT p.CODIGO, p.ID_ESTADIO, e.NOMBRE FROM PARTIDO_FUTBOL p JOIN ESTADIO e ON p.ID_ESTADIO = e.ID_ESTADIO")
    match_rows = cursor.fetchall()
    
    # Initialize localidades and seats
    print("Initializing localidad_partido and seat states for each match...")
    for code, id_estadio, stad_name in match_rows:
        palcos, tribunas, generales = get_stadium_seat_counts(stad_name)
        cursor.execute("INSERT INTO LOCALIDAD_PARTIDO (CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO) VALUES (%s, 'PALCO', %s, 150.00)", (code, palcos))
        cursor.execute("INSERT INTO LOCALIDAD_PARTIDO (CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO) VALUES (%s, 'TRIBUNA', %s, 80.00)", (code, tribunas))
        cursor.execute("INSERT INTO LOCALIDAD_PARTIDO (CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO) VALUES (%s, 'GENERAL', %s, 40.00)", (code, generales))
        
        cursor.execute("SELECT ID_ASIENTO FROM ASIENTO WHERE ID_ESTADIO = %s", (id_estadio,))
        asiento_ids = [r[0] for r in cursor.fetchall()]
        
        for seat_id in asiento_ids:
            cursor.execute("INSERT INTO ASIENTO_PARTIDO (CODIGO_PARTIDO, ID_ASIENTO, ESTADO) VALUES (%s, %s, 'DISPONIBLE')", (code, seat_id))

    # Test Client in TicketPremium
    cursor.execute("INSERT INTO CLIENTE (CEDULA, NOMBRE, EMAIL, TELEFONO) VALUES ('1726354712', 'JUAN PEREZ', 'juan.perez@test.com', '0991234567')")
    cursor.execute("INSERT INTO CLIENTE (CEDULA, NOMBRE, EMAIL, TELEFONO) VALUES ('1756123456', 'MARIA ALVEAR', 'maria.alvear@test.com', '0995556677')")
    cursor.execute("INSERT INTO CLIENTE (CEDULA, NOMBRE, EMAIL, TELEFONO) VALUES ('1798765432', 'CARLOS LOPEZ', 'carlos.lopez@test.com', '0987654321')")

    # -------------------------------------------------------------
    # INSERT BANK CORE INITIAL DATA
    # -------------------------------------------------------------
    print("Inserting banco_core_db initial data...")
    cursor.execute("USE banco_core_db")
    
    cursor.execute("INSERT INTO CLIENTE (CEDULA, NOMBRE, GENERO, FECHA_NACIMIENTO) VALUES ('1726354712', 'JUAN PEREZ', 'M', '1996-05-10')")
    cursor.execute("INSERT INTO CLIENTE (CEDULA, NOMBRE, GENERO, FECHA_NACIMIENTO) VALUES ('1798765432', 'CARLOS LOPEZ', 'M', '2004-08-15')")
    cursor.execute("INSERT INTO CLIENTE (CEDULA, NOMBRE, GENERO, FECHA_NACIMIENTO) VALUES ('1756123456', 'MARIA ALVEAR', 'F', '2006-03-20')")
    cursor.execute("INSERT INTO CLIENTE (CEDULA, NOMBRE, GENERO, FECHA_NACIMIENTO) VALUES ('1712345678', 'JOSE CASTRO', 'M', '1981-12-01')")
    cursor.execute("INSERT INTO CLIENTE (CEDULA, NOMBRE, GENERO, FECHA_NACIMIENTO) VALUES ('1787654321', 'LUIS MORENO', 'M', '1991-01-10')")
    
    # Get client codes
    cursor.execute("SELECT NOMBRE, COD_CLIENTE FROM CLIENTE")
    bank_client_map = {row[0]: row[1] for row in cursor.fetchall()}
    
    # Accounts for clients
    cursor.execute("INSERT INTO CUENTA (NUM_CUENTA, COD_CLIENTE, SALDO) VALUES ('CTA00001', %s, 1500.00)", (bank_client_map['JUAN PEREZ'],))
    cursor.execute("INSERT INTO CUENTA (NUM_CUENTA, COD_CLIENTE, SALDO) VALUES ('CTA00002', %s, 200.00)", (bank_client_map['CARLOS LOPEZ'],))
    cursor.execute("INSERT INTO CUENTA (NUM_CUENTA, COD_CLIENTE, SALDO) VALUES ('CTA00003', %s, 2500.00)", (bank_client_map['MARIA ALVEAR'],))
    cursor.execute("INSERT INTO CUENTA (NUM_CUENTA, COD_CLIENTE, SALDO) VALUES ('CTA00004', %s, 100.00)", (bank_client_map['JOSE CASTRO'],))
    cursor.execute("INSERT INTO CUENTA (NUM_CUENTA, COD_CLIENTE, SALDO) VALUES ('CTA00005', %s, 4000.00)", (bank_client_map['LUIS MORENO'],))
    
    # Insert movements for past 3 months
    today = datetime.now().date()
    m_1 = today - timedelta(days=15) # Last month
    m_2 = today - timedelta(days=45) # 2 months ago
    m_3 = today - timedelta(days=75) # 3 months ago
    
    # Movements for JUAN PEREZ:
    # Deposits: 800, 700, 900 -> Avg Deposit: 800
    # Withdrawals: 300, 200, 400 -> Avg Withdraw: 300
    # Diff = 500. Max Credit = 500 * 0.30 * 6 = 900
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00001', 'DEP', 800.00, %s)", (m_1,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00001', 'DEP', 700.00, %s)", (m_2,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00001', 'DEP', 900.00, %s)", (m_3,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00001', 'RET', 300.00, %s)", (m_1,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00001', 'RET', 200.00, %s)", (m_2,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00001', 'RET', 400.00, %s)", (m_3,))

    # Movements for CARLOS LOPEZ:
    # Deposits: 500, 400, 600
    # Withdrawals: 100, 150, 120
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00002', 'DEP', 500.00, %s)", (m_1,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00002', 'DEP', 400.00, %s)", (m_2,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00002', 'DEP', 600.00, %s)", (m_3,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00002', 'RET', 100.00, %s)", (m_1,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00002', 'RET', 150.00, %s)", (m_2,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00002', 'RET', 120.00, %s)", (m_3,))

    # Movements for MARIA ALVEAR:
    # Deposits: 2000, 1500, 1800 -> Avg Deposit: 1766.66
    # Withdrawals: 500, 600, 400 -> Avg Withdraw: 500
    # Diff = 1266.66. Max Credit = 1266.66 * 0.30 * 6 = 2280
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00003', 'DEP', 2000.00, %s)", (m_1,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00003', 'DEP', 1500.00, %s)", (m_2,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00003', 'DEP', 1800.00, %s)", (m_3,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00003', 'RET', 500.00, %s)", (m_1,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00003', 'RET', 600.00, %s)", (m_2,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00003', 'RET', 400.00, %s)", (m_3,))

    # Movements for JOSE CASTRO (no movements in last month, i.e., in m_1):
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00004', 'DEP', 1000.00, %s)", (m_2,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00004', 'DEP', 1000.00, %s)", (m_3,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00004', 'RET', 200.00, %s)", (m_2,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00004', 'RET', 200.00, %s)", (m_3,))

    # Movements for LUIS MORENO:
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00005', 'DEP', 1000.00, %s)", (m_1,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00005', 'DEP', 1000.00, %s)", (m_2,))
    cursor.execute("INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES ('CTA00005', 'DEP', 1000.00, %s)", (m_3,))
    
    # Active Credit for LUIS MORENO:
    cursor.execute("INSERT INTO CREDITO (COD_CLIENTE, NUM_CUENTA, MONTO_PRESTAMO, INTERES_ANUAL, PLAZO_MESES, CUOTA_MENSUAL, FECHA_APROBACION, ESTADO) VALUES (%s, 'CTA00005', 1000.00, 16.50, 6, 174.77, %s, 'ACTIVO')",
                   (bank_client_map['LUIS MORENO'], m_1))

    conn.close()
    print("Database configuration and initialization complete!")

if __name__ == "__main__":
    run_setup()
