# TicketPremium Platform

Academic platform for selling football match tickets, managing seating, and coordinating payment/credit operations.

## Overview

The project models matches, stadium locations, seats, purchases, invoices, users, sales reports, and a banking credit service. The Java SOAP delivery is complemented by a .NET RESTful implementation from the same TicketPremium domain.

## Architectures

- Java SOAP
- .NET RESTful

## Applications

- Java console client
- Java Swing desktop client
- Java web client
- Android/Kotlin mobile client
- Java SOAP federation and banking services
- .NET REST server, console client, desktop client, and web client

## Technologies

- Java, Maven, JAX-WS, Swing, JSP and Servlets
- Kotlin, Android and Gradle
- C#, ASP.NET Web API and Visual Studio
- MySQL and SQL Server scripts
- Python/PyMySQL database setup utility

## Project Structure

- `java-soap/` — database scripts, clients, mobile app, and SOAP services
- `dotnet-rest/` — TicketPremium RESTful .NET server and clients

## Features

- User login
- Match and locality browsing
- Stadium seat selection
- Ticket purchase and invoicing
- Sales reporting
- Credit simulation and banking-service integration

## Configuration

Database code reads `DB_HOST`, `DB_PORT`, `DB_URL`, `DB_USER`, and `DB_PASSWORD` as applicable. No cloud credentials are stored in this repository. Use `.env.example` as a variable-name reference only.

## Running the Project

Provision the databases with the included SQL or Python setup material after defining the required environment variables. Build Java modules from directories containing `pom.xml`; open the Android app with Android Studio. Open the .NET REST solution and clients through their `.sln` files in Visual Studio. Start backend services before their clients.

## Academic Context

This group academic project was produced for a Software Architecture course and explores a shared business system through SOAP, RESTful, web, desktop, console, and mobile clients.

