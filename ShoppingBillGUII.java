import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.awt.print.*;

public class ShoppingBillGUII extends JFrame {
    private JTextField customerNameField, productIdField, productNameField, quantityField, priceField, dateField;
    private JTextArea billArea;
    private List<Product> products;
    private double overallPrice = 0.0;

    public ShoppingBillGUII() {
        products = new ArrayList<>();

        // Set up the frame
        setTitle("Shopping Bill Management");
        setSize(900, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        JLabel headerLabel = new JLabel("Shopping Bill Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Edu Australia VIC WA NT Hand Arrow", Font.BOLD, 24));
        headerLabel.setForeground(new Color(0xB59F78));  // Color: #B59F78
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.setBackground(new Color(0xFAF6E3));  // Color: #FAF6E3

        // Input Panel with GridBagLayout
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xB59F78), 2),
                "Product Details", 0, 0, new Font("Arial", Font.BOLD, 16), new Color(0xB59F78)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add fields and labels with rounded borders and shadow effects
        customerNameField = createTextField();
        productIdField = createTextField();
        productNameField = createTextField();
        quantityField = createTextField();
        priceField = createTextField();
        dateField = createTextField();

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Customer Name:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(customerNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Product ID:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(productIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Product Name:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(productNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("Price per Unit:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(priceField, gbc);

        // Add buttons with hover effect
        JButton addButton = createButton("Add Product");
        JButton generateBillButton = createButton("Generate Bill");
        JButton fetchSalesButton = createButton("Fetch Sales by Date");
        JButton printButton = createButton("Print Receipt");

        gbc.gridx = 0; gbc.gridy = 5;
        inputPanel.add(addButton, gbc);

        gbc.gridx = 1;
        inputPanel.add(generateBillButton, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        inputPanel.add(fetchSalesButton, gbc);

        gbc.gridx = 1;
        inputPanel.add(dateField, gbc);

        gbc.gridx = 1; gbc.gridy = 7;
        inputPanel.add(printButton, gbc);

        // Bill Display Area
        billArea = new JTextArea(15, 40);
        billArea.setEditable(false);
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(billArea);

        // Add components to frame
        add(headerPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addProduct());
        generateBillButton.addActionListener(e -> generateBill());
        fetchSalesButton.addActionListener(e -> fetchSalesByDate());
        printButton.addActionListener(e -> printReceipt());
    }

    private void addProduct() {
        try {
            String id = productIdField.getText();
            String name = productNameField.getText();
            int qty = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            double totalPrice = price * qty;

            Product product = new Product(id, name, qty, price, totalPrice);
            products.add(product);
            overallPrice += totalPrice;

            // Save to database
            saveToDatabase(product, customerNameField.getText());

            // Clear input fields
            productIdField.setText("");
            productNameField.setText("");
            quantityField.setText("");
            priceField.setText("");

            JOptionPane.showMessageDialog(this, "Product added successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity and price!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void saveToDatabase(Product product, String customerName) throws SQLException {
        String sql = "INSERT INTO sales (customer_name, product_id, product_name, quantity, price, total_price, sale_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String jdbcURL = "jdbc:mysql://127.0.0.1:3306/project?user=root&password=123456";

        try (Connection conn = DriverManager.getConnection(jdbcURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerName);
            pstmt.setString(2, product.getId());
            pstmt.setString(3, product.getPname());
            pstmt.setInt(4, product.getQty());
            pstmt.setDouble(5, product.getPrice());
            pstmt.setDouble(6, product.getTotalPrice());
            pstmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();
        }
    }

    private void generateBill() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = formatter.format(new java.util.Date());

        StringBuilder bill = new StringBuilder();
        bill.append("\t\t\tTAX INVOICE\n");
        bill.append("\t\t\tD MART BUDHWARPETH\n");
        bill.append("Date: ").append(date).append("\n");
        bill.append("Customer Name: ").append(customerNameField.getText()).append("\n\n");
        bill.append(String.format("%-10s %-20s %-10s %-10s %-10s\n", "ID", "Name", "Quantity", "Price", "Total"));
        bill.append("--------------------------------------------------\n");

        for (Product p : products) {
            bill.append(String.format("%-10s %-20s %-10d %-10.2f %-10.2f\n",
                    p.getId(), p.getPname(), p.getQty(), p.getPrice(), p.getTotalPrice()));
        }

        bill.append("--------------------------------------------------\n");
        double sgst = overallPrice * 0.12;
        double cgst = overallPrice * 0.12;
        double finalAmount = overallPrice + sgst + cgst;

        bill.append(String.format("\nSGST (12%%): %.2f\n", sgst));
        bill.append(String.format("CGST (12%%): %.2f\n", cgst));
        bill.append(String.format("Final Amount: %.2f\n", finalAmount));

        billArea.setText(bill.toString());
    }

    private void fetchSalesByDate() {
        String inputDate = dateField.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            java.util.Date date = sdf.parse(inputDate);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            String sql = "SELECT * FROM sales WHERE DATE(sale_date) = ?";
            String jdbcURL = "jdbc:mysql://127.0.0.1:3306/project?user=root&password=123456";

            try (Connection conn = DriverManager.getConnection(jdbcURL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, sqlDate);
                ResultSet rs = pstmt.executeQuery();

                StringBuilder salesData = new StringBuilder();
                salesData.append(String.format("%-10s %-20s %-10s %-10s %-10s\n", "ID", "Name", "Quantity", "Price", "Total"));
                salesData.append("--------------------------------------------------\n");

                while (rs.next()) {
                    salesData.append(String.format("%-10s %-20s %-10d %-10.2f %-10.2f\n",
                            rs.getString("product_id"), rs.getString("product_name"),
                            rs.getInt("quantity"), rs.getDouble("price"),
                            rs.getDouble("total_price")));
                }

                billArea.setText(salesData.toString());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format! Please enter date in dd/MM/yyyy format.");
        }
    }

    private void printReceipt() {
        try {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPrintable((Graphics g, PageFormat pf, int page) -> {
                if (page > 0) {
                    return Printable.NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pf.getImageableX(), pf.getImageableY());
                billArea.printAll(g2d);
                return Printable.PAGE_EXISTS;
            });

            if (printerJob.printDialog()) {
                printerJob.print();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error printing receipt: " + ex.getMessage());
        }
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setBackground(new Color(0xFAF6E3)); // Color: #FAF6E3
        textField.setBorder(BorderFactory.createLineBorder(new Color(0xB59F78), 2));
        return textField;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0xB59F78));  // Color: #B59F78
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(new Color(0xB59F78), 2));
        button.setFocusPainted(false);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0xD6C798));  // Hover Color
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0xB59F78));  // Original Color
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ShoppingBillGUII().setVisible(true);
        });
    }

    static class Product {
        private String id;
        private String pname;
        private int qty;
        private double price;
        private double totalPrice;

        public Product(String id, String pname, int qty, double price, double totalPrice) {
            this.id = id;
            this.pname = pname;
            this.qty = qty;
            this.price = price;
            this.totalPrice = totalPrice;
        }

        public String getId() {
            return id;
        }

        public String getPname() {
            return pname;
        }

        public int getQty() {
            return qty;
        }

        public double getPrice() {
            return price;
        }

        public double getTotalPrice() {
            return totalPrice;
        }
    }
}
