# Smart-Shopkeeper

A Java-based desktop application for managing shopping bills efficiently. This project provides functionalities for adding products, generating invoices, managing sales data, and printing receipts with a user-friendly graphical interface.

Features
Product Management: Add product details, including ID, name, quantity, and price.
Invoice Generation: Automatically calculate totals with SGST and CGST.
Sales Data Retrieval: Fetch sales records based on specific dates.
Receipt Printing: Print invoices directly from the application.
Database Integration: Store sales data in a MySQL database for future reference.
Technologies Used
Programming Language: Java
GUI Framework: Swing (Java Swing API)
Database: MySQL
Tools: JDBC for database connectivity
Installation
Prerequisites:
Install Java JDK.
Install MySQL Server.
Set up a database with the following details:
Database Name: project
Table Name: sales
Table Schema:

CREATE TABLE sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255),
    product_id VARCHAR(50),
    product_name VARCHAR(255),
    quantity INT,
    price DOUBLE,
    total_price DOUBLE,
    sale_date TIMESTAMP
);
Steps:

Clone or download this repository to your local machine.
Open the project in your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse, NetBeans).
Configure the database URL, username, and password in the saveToDatabase() and fetchSalesByDate() methods:

String jdbcURL = "jdbc:mysql://127.0.0.1:3306/project?user=root&password="password";
Run the ShoppingBillGUII.java file.
#How to Use
Launch the application.
Enter customer and product details in the respective fields.
Click on "Add Product" to save the product.
To generate the bill, click "Generate Bill".
Use the "Fetch Sales by Date" button to retrieve sales data for a specific date.
Print the receipt using the "Print Receipt" button.

#Future Enhancements
Add multi-language support.
Include product category filters.
Enable CSV export for sales data.
Implement user authentication for secure access.
#Contributing
Contributions are welcome! Feel free to fork the project and submit a pull request.

