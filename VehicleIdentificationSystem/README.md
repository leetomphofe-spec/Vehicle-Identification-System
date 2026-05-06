
**Group Members:**
| Name | Student ID |
|------|-----------|
| Noncebe Roxa | 901019021 |
| Mphofe Leeto | 901019681 |
| Tebello Lerabe | 901019122 |
| Fumane Nqheku | 901019002 |
| Paballo Phuthi | 901019105 |
| Likomang Qhobosheane | 901019691 |

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

VehicleIdentificationSystem/
├── pom.xml
├── src/
│ └── main/
│ ├── java/com/vis/
│ │ ├── MainApp.java
│ │ ├── controller/
│ │ │ ├── LoginController.java
│ │ │ ├── DashboardController.java
│ │ │ ├── AddVehicleController.java
│ │ │ ├── AddCustomerController.java
│ │ │ ├── AddServiceController.java
│ │ │ ├── AddReportController.java
│ │ │ ├── AddViolationController.java
│ │ │ ├── AddInsuranceController.java
│ │ │ ├── EditInsuranceController.java
│ │ │ └── VehicleDetailPopupController.java
│ │ ├── model/
│ │ │ ├── BaseEntity.java ← Abstract base (Inheritance)
│ │ │ ├── Vehicle.java ← extends BaseEntity
│ │ │ ├── Customer.java ← extends BaseEntity
│ │ │ ├── PoliceReport.java ← extends BaseEntity
│ │ │ ├── Violation.java ← extends BaseEntity
│ │ │ ├── ServiceRecord.java ← extends BaseEntity
│ │ │ ├── Insurance.java ← extends BaseEntity
│ │ │ ├── VehicleImage.java
│ │ │ ├── VehicleDAO.java
│ │ │ ├── CustomerDAO.java
│ │ │ ├── PoliceDAO.java
│ │ │ ├── WorkshopDAO.java
│ │ │ ├── InsuranceDAO.java
│ │ │ └── ActivityLogDAO.java
│ │ └── db/
│ │ └── DatabaseConnection.java
│ └── resources/com/vis/
│ ├── fxml/
│ │ ├── Login.fxml
│ │ ├── Dashboard.fxml
│ │ ├── AddVehicle.fxml
│ │ ├── AddCustomer.fxml
│ │ ├── AddService.fxml
│ │ ├── AddReport.fxml
│ │ ├── AddViolation.fxml
│ │ ├── AddInsurance.fxml
│ │ ├── EditInsurance.fxml
│ │ └── VehicleDetailPopup.fxml
│ ├── css/
│ │ └── styles.css
│ └── images/
│ └── logo.png


---

## Setup Instructions

### 1. Database Setup
```sql
-- Run in pgAdmin or psql:
-- Create database
CREATE DATABASE vis_db;

-- Connect to database
\c vis_db;

-- Run the schema file
\i src/main/resources/schema.sql

4. Login Credentials (from Users table)
## Login Credentials
| Role | Username | Password |
|------|----------|----------|
| ADMIN | admin | admin123 |
| POLICE | police | police123 |
| USER | user | user123 |

## Database Setup
1. Create PostgreSQL database
2. Run schema.sql
3. Run procedures_views.sql
4. Update DatabaseConnection.java with your credentials

## How to Run
1. Clone repository
2. Open in IntelliJ IDEA
3. Configure JavaFX SDK
4. Run MainApp.java

## GitHub Repository
https://github.com/leetomphofe-spec/Vehicle-Identification-System
