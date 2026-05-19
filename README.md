# SHOP.JAVA 🛍️

A full-stack e-commerce web application built with Java Servlets,Postgesql, and vanilla HTML/CSS/JS.

## Features
- User Authentication (Login/Register)
- Product Catalog with Images
- Shopping Cart with GST Calculation
- Order Placement & Invoice Generation
- Email Notification to Admin after every order
- Customer Address Management
- Admin Panel for Product & Order Management

## Tech Stack
| Layer | Technology                |
|-------|---------------------------|
| Language | Java JDK 22               |
| Backend | Java Servlets             |
| Server | Apache Tomcat 10          |
| Database | Postgresql                |             
| Frontend | HTML5, CSS3, JavaScript   |
| Email | Jakarta Mail + Angus Mail |

## Project Structure
ECommerceApp/
├── src/
│   ├── db/          → Database connection
│   ├── model/       → Data models
│   ├── dao/         → Database queries
│   ├── service/     → Business logic
│   └── servlet/     → REST API endpoints
└── web/
├── index.html   → Login page
├── pages/       → All frontend pages
└── WEB-INF/     → Config and JARs
## Setup Instructions
1. Import project in IntelliJ IDEA Ultimate
2. Setup Postgresql database using `ecommerce_db.sql`
3. Update `DBConnection.java` with your Postgesql password
4. Add Tomcat 10 server in IntelliJ
5. Run the project

## Developer
Made by Ishu Rana
