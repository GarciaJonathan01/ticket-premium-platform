-- =============================================
-- Base de Datos: TicketPremiumDB
-- Motor: SQL Server
-- =============================================

IF EXISTS (SELECT name FROM sys.databases WHERE name = N'TicketPremiumDB')
    DROP DATABASE TicketPremiumDB;
GO

CREATE DATABASE TicketPremiumDB;
GO

USE TicketPremiumDB;
GO

-- =============================================
-- TABLA: PARTIDO_FUTBOL
-- =============================================
CREATE TABLE PARTIDO_FUTBOL (
    CODIGO          INT IDENTITY(1,1) NOT NULL,
    EQUIPO_LOCAL    NVARCHAR(100) NOT NULL,
    EQUIPO_VISITA   NVARCHAR(100) NOT NULL,
    FECHA           DATETIME NOT NULL,
    LUGAR           NVARCHAR(200) NOT NULL,
    CONSTRAINT PK_PARTIDO_FUTBOL PRIMARY KEY (CODIGO)
);
GO

-- =============================================
-- TABLA: LOCALIDAD_PARTIDO
-- =============================================
CREATE TABLE LOCALIDAD_PARTIDO (
    ID                  INT IDENTITY(1,1) NOT NULL,
    CODIGO_PARTIDO      INT NOT NULL,
    CODIGO_LOCALIDAD    NVARCHAR(50) NOT NULL,
    DISPONIBILIDAD      INT NOT NULL DEFAULT 0,
    PRECIO              DECIMAL(10,2) NOT NULL,
    CONSTRAINT PK_LOCALIDAD_PARTIDO PRIMARY KEY (ID),
    CONSTRAINT FK_LOCALIDAD_PARTIDO
        FOREIGN KEY (CODIGO_PARTIDO) REFERENCES PARTIDO_FUTBOL(CODIGO)
);
GO

-- =============================================
-- TABLA: FACTURA
-- =============================================
CREATE TABLE FACTURA (
    ID              INT IDENTITY(1,1) NOT NULL,
    CODIGO_PARTIDO  INT NOT NULL,
    NOMBRE_CLIENTE  NVARCHAR(200) NOT NULL,
    FECHA           DATETIME NOT NULL DEFAULT GETDATE(),
    SUBTOTAL        DECIMAL(10,2) NOT NULL,
    IVA             DECIMAL(10,2) NOT NULL,
    TOTAL           DECIMAL(10,2) NOT NULL,
    CONSTRAINT PK_FACTURA PRIMARY KEY (ID),
    CONSTRAINT FK_FACTURA_PARTIDO
        FOREIGN KEY (CODIGO_PARTIDO) REFERENCES PARTIDO_FUTBOL(CODIGO)
);
GO

-- =============================================
-- TABLA: DETALLE_FACTURA
-- =============================================
CREATE TABLE DETALLE_FACTURA (
    ID                  INT IDENTITY(1,1) NOT NULL,
    ID_FACTURA          INT NOT NULL,
    CODIGO_PARTIDO      INT NOT NULL,
    CODIGO_LOCALIDAD    NVARCHAR(50) NOT NULL,
    CANTIDAD            INT NOT NULL,
    PRECIO_UNITARIO     DECIMAL(10,2) NOT NULL,
    SUBTOTAL            DECIMAL(10,2) NOT NULL,
    CONSTRAINT PK_DETALLE_FACTURA PRIMARY KEY (ID),
    CONSTRAINT FK_DETALLE_FACTURA
        FOREIGN KEY (ID_FACTURA) REFERENCES FACTURA(ID),
    CONSTRAINT FK_DETALLE_PARTIDO
        FOREIGN KEY (CODIGO_PARTIDO) REFERENCES PARTIDO_FUTBOL(CODIGO)
);
GO

-- =============================================
-- DATOS DE PRUEBA
-- =============================================

-- 5 partidos
INSERT INTO PARTIDO_FUTBOL (EQUIPO_LOCAL, EQUIPO_VISITA, FECHA, LUGAR) VALUES
(N'Barcelona SC',            N'Liga de Quito',        '2026-06-15 16:00:00', N'Estadio Monumental Banco Pichincha - Guayaquil'),
(N'Emelec',                  N'Deportivo Cuenca',     '2026-06-22 19:00:00', N'Estadio George Capwell - Guayaquil'),
(N'Independiente del Valle', N'El Nacional',          '2026-07-01 15:00:00', N'Estadio Rumiñahui - Sangolquí'),
(N'Aucas',                   N'Mushuc Runa',          '2026-07-10 12:00:00', N'Estadio Gonzalo Pozo Ripalda - Quito'),
(N'Universidad Católica',    N'Macará',               '2026-08-05 18:00:00', N'Estadio Olímpico Atahualpa - Quito');
GO

-- 20 localidades (4 por partido)
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO) VALUES
(1, N'PALCO',            50,  45.00), (1, N'TRIBUNA',         300,  25.00),
(1, N'GENERAL',        2000,   8.00), (1, N'GENERAL_VISITA', 1500,   8.00),
(2, N'PALCO',            40,  50.00), (2, N'TRIBUNA',         250,  28.00),
(2, N'GENERAL',        1800,  10.00), (2, N'GENERAL_VISITA', 1200,  10.00),
(3, N'PALCO',            30,  40.00), (3, N'TRIBUNA',         200,  22.00),
(3, N'GENERAL',        1500,   7.00), (3, N'GENERAL_VISITA', 1000,   7.00),
(4, N'PALCO',            25,  35.00), (4, N'TRIBUNA',         180,  20.00),
(4, N'GENERAL',        1200,   6.00), (4, N'GENERAL_VISITA',  800,   6.00),
(5, N'PALCO',            20,  30.00), (5, N'TRIBUNA',         150,  18.00),
(5, N'GENERAL',        1000,   5.00), (5, N'GENERAL_VISITA',  700,   5.00);
GO
