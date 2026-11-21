# SMART PORTER – Smart Luggage Assistance & Booking Platform

**BTech 2nd Year DBMS Project**  
A complete, production-style web application for booking railway porters (inspired by Rapido / Uber).

---

## Features

### Passenger
- Registration & secure login
- Station + pickup/drop location selection
- Luggage details & service type selection
- Automatic fare calculation with full breakdown
- View available verified porters
- Book porter (transaction-safe)
- Live status tracking (simulated)
- Booking history, cancel, rate & review
- Payment history

### Porter
- Registration & profile
- Admin verification workflow
- Online / Offline availability toggle
- Receive & accept/reject booking requests
- Update booking status
- Daily & total earnings
- Ratings view

### Admin
- Professional dashboard with live statistics & charts (Chart.js)
- Manage / verify porters
- View all bookings & revenue
- Manage stations, service types & fares

### DBMS Highlights
- Fully normalized schema (3NF)
- Primary / Foreign keys, Unique, Check, Not Null constraints
- Indexes for performance
- Views (3+), Stored Procedures (3), Functions (2), Triggers (3)
- JDBC transactions with COMMIT / ROLLBACK for concurrent booking safety
- Prepared statements (SQL injection prevention)
- Password hashing (SHA-256 + salt)
- 15+ demonstration SQL queries covering JOINs, Aggregates, Subqueries, GROUP BY, HAVING, etc.

---

## Technology Stack

| Layer      | Technology                          |
|------------|-------------------------------------|
| Frontend   | HTML5, CSS3, JavaScript             |
| Backend    | Java, Servlets, JDBC                |
| Database   | MySQL 8.x                           |
| Server     | Apache Tomcat 9 / 10                |
| IDE        | VS Code / Eclipse / IntelliJ        |

---

## Project Structure

```
SmartPorter/
├── frontend/
│   ├── index.html, login.html, register.html
│   ├── passenger-dashboard.html, porter-dashboard.html
│   ├── admin-dashboard.html, booking.html
│   ├── css/style.css
│   └── js/
├── backend/
│   ├── src/model, dao, controller, util, filter
│   └── webapp/WEB-INF/web.xml
├── database/
│   ├── schema.sql, sample_data.sql
│   ├── views.sql, procedures.sql, functions.sql, triggers.sql
│   └── demo_queries.sql
└── README.md
```

---

## Setup Instructions

### 1. Database Setup
1. Install MySQL Server & MySQL Workbench.
2. Run the scripts **in this order**:
   ```
   SOURCE database/schema.sql;
   SOURCE database/sample_data.sql;
   SOURCE database/views.sql;
   SOURCE database/functions.sql;
   SOURCE database/procedures.sql;
   SOURCE database/triggers.sql;
   ```
3. (Optional) Run `demo_queries.sql` to verify advanced queries.

### 2. Configure Database Connection
Edit `backend/src/util/DBConnection.java` and set your MySQL password:
```java
private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";
```

### 3. Deploy on Apache Tomcat
1. Install Apache Tomcat 9 or 10.
2. Place `mysql-connector-j-8.x.x.jar` into Tomcat `lib/` folder.
3. In Eclipse: Create Dynamic Web Project, copy Java sources to `src`, frontend to `WebContent`, `web.xml` to `WEB-INF`.
4. Build, deploy, start Tomcat.
5. Open: `http://localhost:8080/SmartPorter/`

---

## Demo Login Credentials

| Role      | Email                        | Password      |
|-----------|------------------------------|---------------|
| Admin     | admin@smartporter.com        | admin123      |
| Passenger | rahul.sharma@email.com       | passenger123  |
| Porter    | ramesh.porter@email.com      | porter123     |

All demo passengers use password `passenger123`.  
All demo porters use password `porter123`.

---

## Security Implemented
- Password hashing (SHA-256 + application salt)
- Session-based authentication
- Role-based access control
- Prepared statements
- Input validation
- Transaction isolation for concurrent porter assignment

---

## Notes for Evaluation
- All advanced DBMS objects are present and executable.
- Booking flow uses JDBC transactions with row-level locking (`FOR UPDATE`) to prevent double-booking.
- Frontend is fully responsive and designed like a commercial product.
- Payment module is demonstration only (Cash / UPI / Online mock).
- Tracking is simulated (clearly marked).

---

**SMART PORTER** – Your Luggage. Our Responsibility.  
Book trusted railway porters in seconds.
```
