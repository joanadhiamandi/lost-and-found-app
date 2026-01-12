#  Lost & Found Application

Enterprise-level web application for monitoring lost and found items in municipalities. Built with Spring Boot and MySQL as part of Digital Systems coursework at University of Piraeus.

##  Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [System Requirements](#system-requirements)
- [Installation & Setup](#installation--setup)
- [Database Configuration](#database-configuration)
- [Usage Guide](#usage-guide)
- [Project Structure](#project-structure)
- [UML Diagrams](#uml-diagrams)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Testing](#testing)
- [Contributors](#contributors)

---

##  Project Overview

The Lost & Found Application serves as a centralized platform for citizens to report and search for lost or found items. The system provides robust functionalities for item management, user authentication, role-based access control, and administrative oversight.

### Learning Outcomes Addressed

- **LO1:** Pattern and framework implementation in enterprise architecture
- **LO2:** Technology analysis and synthesis for application development
- **LO3:** Autonomous enterprise application construction
- **LO4:** Business goals and software development alignment
- **LO5:** Secure software element implementation

---

##  Features

### User Management
- ✅ Secure user registration and authentication (BCrypt password hashing)
- ✅ Role-Based Access Control (RBAC): Admin and Member roles
- ✅ Username uniqueness validation
- ✅ Session management

### Items Management
- ✅ CRUD operations for lost/found items
- ✅ Item categorization and status tracking
- ✅ Detailed item information (name, description, location, date, contact)
- ✅ Comments/Discussion board under each item
- ✅ View count tracking

### Search & Filtering
- ✅ Advanced search by category, type (Lost/Found), and location
- ✅ Real-time filtering
- ✅ Similar items suggestions

### Messaging System
- ✅ Direct messaging between users
- ✅ Conversation threads
- ✅ Unread message notifications

### Admin Panel
- ✅ User and item management
- ✅ Analytics dashboard with statistics
- ✅ Category breakdown visualization
- ✅ Most active members tracking
- ✅ Content moderation capabilities

### Reporting & Analytics
- ✅ Statistical dashboards with charts
- ✅ PDF export functionality
- ✅ Alert system for matching lost/found items

---

##  Technologies Used

### Backend
- **Framework:** Spring Boot 3.x
- **Language:** Java 17+
- **Database:** MySQL 8.0
- **Security:** Spring Security with BCrypt
- **Build Tool:** Maven
- **Template Engine:** Thymeleaf

### Frontend
- **HTML5, CSS3, JavaScript**
- **Responsive Design**
- **Chart.js** (for analytics visualization)

### Development Tools
- **IDE:** IntelliJ IDEA
- **Version Control:** Git & GitHub
- **Database Management:** MySQL Workbench

---

##  System Requirements

- **Java JDK:** 17 or higher
- **Maven:** 3.6+
- **MySQL:** 8.0 or higher
- **RAM:** Minimum 4GB
- **Disk Space:** 500MB free space

---

##  Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/joanadhiamandi/lost-and-found-app.git
cd lost-and-found-app
