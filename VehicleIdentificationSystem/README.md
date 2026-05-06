
**Group Members:**
| Name | Student ID |
|------|-----------|
| Noncebe Roxa | 901019021 |
| Phoebe Leeto | 901019681 |
| Tebello Lerabe | 901019122 |
| Fumane Ngheku | 901019002 |
| Paballo Phuthi | 901019105 |
| Likomang Eshobosheane | 901019691 |

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
Username	Password	Role
admin	admin123	ADMIN
police	police123	POLICE
user	user123	USER


