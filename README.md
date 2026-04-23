# Online Store Management System - CSCI 300 Final Project

A comprehensive desktop application for an online store with three-tier user access (Customers, Employees, and Admins). Built with Java Swing UI and MySQL database.

## 🏆 Grade: Final Project - CSCI 300 Database Management System

## 👥 User Roles & Features

### 👤 Customers
- Create account with username, password, full name, and address
- Browse products and add items to shopping cart
- Apply coupon codes for discounts
- Place orders (cart data only persists after checkout)
- View complete order history
- Cancel orders (permanently deletes from database)
- Update profile name and address

### 👔 Employees
- Add new products to store (name, price, stock quantity)
- View complete list of all customer accounts
- View all orders placed (sorted alphabetically by customer name)
- Create discount coupon codes for customers

### 👑 Administrators
- Create employee accounts (full name, username, password)
- First-login password change requirement for employees
- Modify order status (pending → processing → shipped → delivered → cancelled)
- Delete any order from the system
- Delete customer or employee accounts
- Edit customer information (name, address, username, password)
- Edit employee information (name, username, password)

## 🛠️ Technologies Used

- **Language:** Java (JDK 21)
- **UI Framework:** Swing (IntelliJ GUI Designer)
- **Database:** MySQL
- **JDBC Driver:** MySQL Connector/J 8.0.19
- **Table Utility:** rs2xml.jar (DbUtils)
