#  Lost & Found Application

Enterprise-level web application for monitoring lost and found items in municipalities. 
Built with Spring Boot and MySQL as part of Digital Systems coursework at University of Greater Manchester.

## Table of Contents

- [Project Overview](#-project-overview)
- [Features](#-features)
- [Technologies Used](#-technologies-used)
- [System Requirements](#-system-requirements)
- [Installation & Setup](#-installation--setup)
- [Database Configuration](#-database-configuration)
- [Usage Guide](#-usage-guide)
- [Screenshots](#-screenshots)
- [Project Structure](#-project-structure)
- [API Endpoints](#-api-endpoints)
- [Security](#-security)
- [Contributors](#-contributors)

---

##  Project Overview
The Lost & Found Application serves as a centralized platform for citizens to report and search for lost or found items. The system provides robust functionalities for item management, user authentication, role-based access control, administrative oversight, and automated alert notifications.

### Learning Outcomes Addressed

- **LO1:** Pattern and framework implementation in enterprise architecture
- **LO2:** Technology analysis and synthesis for application development
- **LO3:** Autonomous enterprise application construction
- **LO4:** Business goals and software development alignment
- **LO5:** Secure software element implementation

---

##  Features

###  User Management
- ✅ Secure user registration and authentication (BCrypt password hashing)
- ✅ Role-Based Access Control (RBAC): Admin and Member roles
- ✅ Username uniqueness validation
- ✅ Session management with secure cookie handling
- ✅ User profile management

###  Items Management
- ✅ **CRUD operations** for lost/found items
- ✅ **Item categorization** with dynamic category system
- ✅ **Status tracking** (Active, Resolved, Deleted)
- ✅ **Detailed item pages** with rich information display
- ✅ **View count tracking** with auto-increment
- ✅ **Similar items suggestions** based on category
- ✅ **Item edit and delete** with authorization checks
- ✅ **Soft delete** functionality to preserve data integrity

###  Comments & Discussions
- ✅ **Threaded comment system** on item detail pages
- ✅ **Real-time comment posting** with validation
- ✅ **Comment moderation** (delete by author, item owner, or admin)
- ✅ **Comment count display** for engagement metrics
- ✅ **Timestamps** for all comments with formatting
- ✅ **Character limit enforcement** (500 chars max)

###  Search & Filtering
- ✅ **Advanced multi-filter search** (type, category, location, keywords)
- ✅ **Real-time filtering** with instant results
- ✅ **Keyword search** across item names and descriptions
- ✅ **Location-based filtering** with partial matching
- ✅ **Empty state handling** with helpful suggestions
- ✅ **Filter persistence** across page navigation

###  Search Alerts System
- ✅ **Create custom search alerts** with criteria matching
- ✅ **Automatic email notifications** when matching items are posted
- ✅ **Alert management dashboard** (view, edit, delete)
- ✅ **Multi-criteria matching** (type, category, keywords, location)
- ✅ **Alert history tracking**
- ✅ **Email templates** with item details and direct links

###  Messaging System
- ✅ **Direct messaging** between users
- ✅ **Conversation threads** with chronological ordering
- ✅ **Unread message notifications** with badge indicators
- ✅ **Message history preservation**
- ✅ **Reply functionality** within conversations
- ✅ **User search** for starting new conversations

### ️ Admin Panel & Analytics
- ✅ **Comprehensive analytics dashboard** with Chart.js visualizations
- ✅ **System statistics cards** (users, items, status breakdown)
- ✅ **Lost vs Found pie chart** with percentages
- ✅ **Items by category bar chart** with counts
- ✅ **Top 5 active members chart** with horizontal bars
- ✅ **User management** (view, delete users)
- ✅ **Item management** (view, moderate, delete items)
- ✅ **Category management** (create, delete categories)
- ✅ **Content moderation** capabilities
- ✅ **Admin-only access** with role verification

###  Reporting & Export
- ✅ **CSV export functionality** for data analysis
- ✅ **Export all items** or filter by type (Lost/Found)
- ✅ **Export from Browse page** with current filters applied
- ✅ **Export from Admin panel** with multiple options
- ✅ **Proper CSV formatting** with special character escaping
- ✅ **Excel/Google Sheets compatible** exports
- ✅ **Includes all item details** (ID, type, category, name, location, date, status, user, contact)

###  User Interface
- ✅ **Modern gradient-based design** with vibrant colors
- ✅ **Fully responsive layout** for mobile, tablet, and desktop
- ✅ **Intuitive navigation** with breadcrumbs and clear CTAs
- ✅ **Interactive cards and hover effects**
- ✅ **Toast notifications** for user feedback
- ✅ **Modal dialogs** for confirmations
- ✅ **Loading states** and empty states with helpful messages

---

##  Technologies Used

### Backend
- **Framework:** Spring Boot 3.x
- **Language:** Java 17+
- **Database:** MySQL 8.0
- **ORM:** Spring Data JPA / Hibernate
- **Security:** BCrypt password hashing
- **Build Tool:** Maven
- **Template Engine:** Thymeleaf
- **Email:** Spring Mail (for alert notifications)

### Frontend
- **HTML5, CSS3, JavaScript (ES6+)**
- **Responsive Design** with mobile-first approach
- **Chart.js 4.4.0** for analytics visualization
- **Custom CSS animations** and transitions
- **Modern gradient designs**

### Development Tools
- **IDE:** IntelliJ IDEA / VS Code
- **Version Control:** Git & GitHub
- **Database Management:** MySQL Workbench
- **Browser DevTools** for debugging


---
##  System Requirements

- **Java JDK:** 17 or higher
- **Maven:** 3.6+
- **MySQL:** 8.0 or higher
- **RAM:** Minimum 4GB (8GB recommended)
- **Disk Space:** 500MB free space
- **Browser:** Chrome, Firefox, Edge, Safari (latest versions)

---

##  Installation & Setup

### Clone the Repository

```bash
git clone https://github.com/joanadhiamandi/lost-and-found-app.git
cd lost-and-found-app

## Build and Run
```bash
mvn clean install
mvn spring-boot:run

### Access the Application
Open your browser and navigate to:
http://localhost:8080

### Default Admin Account
Username: admin
Password: admin123