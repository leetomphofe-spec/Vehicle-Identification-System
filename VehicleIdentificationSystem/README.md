# Vehicle Identification System (VIS)
## Object Oriented Programming II – B/DIOP2210
### Limkokwing University of Creative Technology, Lesotho

---

## Technologies Used
- **Frontend**: JavaFX 21 with FXML
- **Backend**: PostgreSQL 15+
- **DB Driver**: JDBC (postgresql-42.7.1)
- **Build Tool**: Maven
- **Architecture**: MVC (Model-View-Controller)
- **Version Control**: Git / GitHub

---

## Project Structure
```
VehicleIdentificationSystem/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/vis/
│       │   ├── MainApp.java
│       │   ├── controller/
│       │   │   ├── LoginController.java
│       │   │   ├── DashboardController.java
│       │   │   ├── AddVehicleController.java
│       │   │   ├── AddCustomerController.java
│       │   │   ├── AddServiceController.java
│       │   │   └── AddReportController.java
│       │   ├── model/
│       │   │   ├── BaseEntity.java          ← Abstract base (Inheritance)
│       │   │   ├── Vehicle.java             ← extends BaseEntity
│       │   │   ├── Customer.java            ← extends BaseEntity
│       │   │   ├── PoliceReport.java        ← extends BaseEntity
│       │   │   ├── Violation.java           ← extends BaseEntity
│       │   │   ├── ServiceRecord.java       ← extends BaseEntity
│       │   │   ├── VehicleDAO.java
│       │   │   ├── CustomerDAO.java
│       │   │   ├── PoliceDAO.java
│       │   │   └── WorkshopDAO.java
│       │   └── db/
│       │       └── DatabaseConnection.java
│       └── resources/com/vis/
│           ├── fxml/
│           │   ├── Login.fxml
│           │   ├── Dashboard.fxml
│           │   ├── AddVehicle.fxml
│           │   ├── AddCustomer.fxml
│           │   ├── AddService.fxml
│           │   └── AddReport.fxml
│           ├── css/
│           │   └── styles.css
│           └── schema.sql
```

---

## Setup Instructions

### 1. Database Setup
```sql
-- Run in psql or pgAdmin:
\i src/main/resources/schema.sql
```
This creates the database, tables, views, stored procedure, and seed data.

### 2. Configure Database Connection
Edit `src/main/java/com/vis/db/DatabaseConnection.java`:
```java
private static final String URL      = "jdbc:postgresql://localhost:5432/vehicle_db";
private static final String USER     = "postgres";
private static final String PASSWORD = "your_password_here";  // ← change this
```

### 3. Build & Run
```bash
# Using Maven
mvn clean javafx:run

# Or compile and run manually
mvn clean package
java -jar target/VehicleIdentificationSystem-1.0-SNAPSHOT.jar
```

### 4. Login Credentials (from seed data)
| Username   | Password     | Role      |
|------------|--------------|-----------|
| admin      | admin123     | ADMIN     |
| workshop1  | workshop123  | WORKSHOP  |
| police1    | police123    | POLICE    |
| customer1  | cust123      | CUSTOMER  |

> **Note**: If no database is connected, the app runs in **demo mode** with sample data.

---

## Key Features Implemented

| Feature | Implementation |
|---------|---------------|
| Menu Bar & Menu Items | `Dashboard.fxml` → File, Vehicles, Modules, Help menus |
| TableView | Vehicle, Service, Police, Violation, Customer tables |
| Pagination | Dashboard tab – 5 pages |
| ScrollPane (20+ items) | Activity log with 23 entries |
| ProgressBar | Database sync indicator with animation |
| ProgressIndicator | Spinning indicator with DropShadow glow |
| DropShadow Effect | Login button, ProgressIndicator |
| FadeTransition | Login button continuously fades in/out |
| PostgreSQL via JDBC | All DAOs use JDBC; DatabaseConnection singleton |
| Exception Handling | try-catch in all controllers and DAOs |
| Inheritance | `BaseEntity` → Vehicle, Customer, PoliceReport, Violation, ServiceRecord |
| Polymorphism | `getDisplayInfo()` overridden in each subclass |
| MVC Pattern | Separate model/view(fxml)/controller packages |
| Stored Procedure | `add_vehicle()` called via `CallableStatement` |
| DB Views | `VehicleDetails`, `ActiveViolations` used in queries |

---

## GitHub
Push with:
```bash
git init
git add .
git commit -m "Initial commit - Vehicle Identification System"
git remote add origin https://github.com/YOUR_USERNAME/VehicleIdentificationSystem.git
git push -u origin main
```

---

## Group Members
| Name | Student ID |
|------|-----------|
|      |           |
|      |           |
|      |           |
