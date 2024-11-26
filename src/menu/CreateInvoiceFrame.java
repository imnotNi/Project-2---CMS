package menu;

import connect.MainConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class CreateInvoiceFrame extends javax.swing.JFrame {

    private Map<String, String> patientMap = new HashMap<>();
    private JTextField phoneField;
    private JComboBox<String> doctorComboBox;
    private JComboBox<String> patientComboBox; // Thêm ComboBox để chọn bệnh nhân
    private JComboBox<String> medicineComboBox;
    private JTextField quantityField;
    private JButton addButton;
    private JButton deleteButton; // Thêm nút để xóa sản phẩm khỏi giỏ hàng
    private DefaultTableModel invoiceTableModel = new DefaultTableModel(new Object[]{"Thuốc", "Số lượng"}, 0);
    JTable invoiceTable = new JTable(invoiceTableModel);

   public CreateInvoiceFrame() {
        setTitle("Create new invoice");
        setSize(500, 400); // Điều chỉnh kích thước để phù hợp với giao diện mới
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout()); // Sử dụng BorderLayout

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(5, 2, 8, 4)); // Adjusted layout to fit all components

        JLabel phoneLabel = new JLabel("Select patient:");
        patientComboBox = new JComboBox<>();
        loadPatients(); // Load danh sách bệnh nhân từ cơ sở dữ liệu
        topPanel.add(phoneLabel);
        topPanel.add(patientComboBox);

        JLabel doctorLabel = new JLabel("Choose a doctor:");
        doctorComboBox = new JComboBox<>();
        loadDoctors(); // Load danh sách bác sĩ từ cơ sở dữ liệu
        topPanel.add(doctorLabel);
        topPanel.add(doctorComboBox);

        JLabel medicineLabel = new JLabel("Choose medicine:");
        medicineComboBox = new JComboBox<>();
        topPanel.add(medicineLabel);
        topPanel.add(medicineComboBox);
        loadMedicine();

        JLabel quantityLabel = new JLabel("Enter quantity:");
        quantityField = new JTextField();
        topPanel.add(quantityLabel);
        topPanel.add(quantityField);

        addButton = new JButton("Add to invoice");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMedicineToInvoice();
            }
        });
        topPanel.add(addButton);

        deleteButton = new JButton("Remove from invoice");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMedicineFromInvoice();
            }
        });
        topPanel.add(deleteButton);

        mainPanel.add(topPanel, BorderLayout.NORTH); // Đặt topPanel ở phía trên
        invoiceTableModel = new DefaultTableModel(new Object[]{"Medicine", "Quantity"}, 0);
        invoiceTable = new JTable(invoiceTableModel);
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Đặt bảng giỏ hàng ở trung tâm

     // Create a new panel for additional buttons
JPanel buttonPanel = new JPanel();
buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Align buttons to the right

// Add the payment button
JButton paymentButton = new JButton("Pay");
paymentButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        createOrder();
    }
});
buttonPanel.add(paymentButton);

// Add the close button
JButton closeButton = new JButton("Close");
closeButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        dispose(); // Close the frame
    }
});
buttonPanel.add(closeButton);

mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Add the button panel to the main panel


        add(mainPanel); // Thêm mainPanel vào JFrame
        
    }

    private void loadPatients() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = MainConnection.getConnection();
            String query = "SELECT Patient_name,Phone FROM Patients";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String patientName = rs.getString("Patient_name");
                String phone = rs.getString("Phone");
                String displayString = patientName + " - " + phone;
                patientComboBox.addItem(displayString);
                patientMap.put(displayString, phone); // Lưu tên bệnh nhân và số điện thoại tương ứng
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }
    }

    private void deleteMedicineFromInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            return;
        }

        invoiceTableModel.removeRow(selectedRow);
        JOptionPane.showMessageDialog(this, "The product has been removed from the cart.");
    }

    private void loadDoctors() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = MainConnection.getConnection();
            String query = "SELECT Name FROM DoctorManager";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                doctorComboBox.addItem(rs.getString("Name"));
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }
    }

    private void loadMedicine() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = MainConnection.getConnection();
            String query = "SELECT Medicine_name FROM Medicine";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                medicineComboBox.addItem(rs.getString("Medicine_name"));
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }
    }

    private void addMedicineToInvoice() {
        String medicine = (String) medicineComboBox.getSelectedItem();
        String quantityText = quantityField.getText();
        int quantity = 0;

        if (!quantityText.isEmpty()) {
            try {
                quantity = Integer.parseInt(quantityText);
            } catch (NumberFormatException e) {
                // Xử lý nếu người dùng nhập không phải là số
                JOptionPane.showMessageDialog(this, "Invalid quantity.");
                return;
            }
        } else {
            // Hiển thị thông báo hoặc xử lý khác khi trường số lượng rỗng
            JOptionPane.showMessageDialog(this, "Please enter quantity.");
            return;
        }

        boolean found = false;

        // Kiểm tra xem sản phẩm đã tồn tại trong hóa đơn chưa
        for (int i = 0; i < invoiceTableModel.getRowCount(); i++) {
            if (medicine.equals(invoiceTableModel.getValueAt(i, 0))) {
                // Sản phẩm đã tồn tại, cộng thêm số lượng
                int currentQuantity = (int) invoiceTableModel.getValueAt(i, 1);
                invoiceTableModel.setValueAt(currentQuantity + quantity, i, 1);
                found = true;
                break;
            }
        }

        // Nếu sản phẩm chưa tồn tại trong hóa đơn, thêm mới
        if (!found) {
            Object[] row = {medicine, quantity};
            invoiceTableModel.addRow(row);
        }

        JOptionPane.showMessageDialog(this, "Added " + quantity + " " + medicine + " to the invoice.");
    }

    private void createOrder() {
        Connection conn = null;

        String insertOrderQuery = "INSERT INTO Invoices (Doctor_id,Patient_id,DateTime,TotalAmount,PaymentMethod,Status) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmtOrder = null;

        try {
            conn = MainConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // Lấy Patient_id từ patientComboBox
            int patientId = getPatientIdFromComboBox();

            // Lấy Doctor_id từ DoctorComboBox
            int doctorId = getDoctorIdFromComboBox();

            // Nếu patientId hoặc doctorId = -1, tức là không chọn bệnh nhân hoặc bác sĩ từ combobox
            if (patientId == -1 || doctorId == -1) {
                JOptionPane.showMessageDialog(this, "Please select or add patients and doctors.");
                return;
            }
            pstmtOrder = conn.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS);
           pstmtOrder.setInt(1, doctorId);
            pstmtOrder.setInt(2, patientId);

            pstmtOrder.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Ngày và giờ hiện tại

            // Tính tổng số tiền từ hóa đơn
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (int i = 0; i < invoiceTableModel.getRowCount(); i++) {
                String medicine = (String) invoiceTableModel.getValueAt(i, 0);
                int quantity = (int) invoiceTableModel.getValueAt(i, 1);
                double unitPrice = getPriceOfMedicine(medicine); // Lấy giá của thuốc từ cơ sở dữ liệu
                BigDecimal itemTotal = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(unitPrice));
                totalAmount = totalAmount.add(itemTotal);
            }

            pstmtOrder.setBigDecimal(4, totalAmount);
            pstmtOrder.setString(5, "Online payment"); // Ví dụ: thanh toán trực tuyến
            pstmtOrder.setString(6, "Paid"); // Mặc định là đã thanh toán

            pstmtOrder.executeUpdate();

            // Lấy ID của order vừa tạo
            ResultSet rs = pstmtOrder.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // Tạo các order detail tương ứng với các mặt hàng trong hóa đơn
            String insertOrderDetailQuery = "INSERT INTO InvoiceDetails (Invoice_id, Medicine_id, Quantity, UnitPrice, TotalPrice) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmtOrderDetail = conn.prepareStatement(insertOrderDetailQuery);
            for (int i = 0; i < invoiceTableModel.getRowCount(); i++) {
                String medicine = (String) invoiceTableModel.getValueAt(i, 0);
                int quantity = (int) invoiceTableModel.getValueAt(i, 1);
                double unitPrice = getPriceOfMedicine(medicine); // Lấy giá của thuốc từ cơ sở dữ liệu
                double totalPrice = quantity * unitPrice;
                pstmtOrderDetail.setInt(1, orderId);
                pstmtOrderDetail.setInt(2, getMedicineId(medicine)); // Lấy ID của thuốc từ cơ sở dữ liệu
                pstmtOrderDetail.setInt(3, quantity);
                pstmtOrderDetail.setDouble(4, unitPrice);
                pstmtOrderDetail.setDouble(5, totalPrice);
                pstmtOrderDetail.executeUpdate();
            }

            conn.commit(); // Hoàn thành giao dịch
            JOptionPane.showMessageDialog(this, "Order created and saved to database successfully.");

            // Xóa dữ liệu trong bảng invoiceTableModel và cập nhật giao diện
            invoiceTableModel.setRowCount(0);

        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback giao dịch nếu có lỗi
                }
            } catch (SQLException ex2) {
                System.out.println("Rollback failed: " + ex2.getMessage());
            }
            System.out.println("Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "An error occurred while creating the order.");
        } finally {
            try {
                if (pstmtOrder != null) {
                    pstmtOrder.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true); // Phục hồi trạng thái tự động commit
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }
    }


    private int getDoctorIdFromComboBox() {
        String doctorName = (String) doctorComboBox.getSelectedItem();
        if (doctorName != null && !doctorName.isEmpty()) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            int doctorId = 0;

            try {
                conn = MainConnection.getConnection();
                String query = "SELECT Doctor_id FROM DoctorManager WHERE Name = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, doctorName);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    doctorId = rs.getInt("Doctor_id");
                }
            } catch (SQLException ex) {
                System.out.println("Error: " + ex.getMessage());
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (pstmt != null) {
                        pstmt.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println("Error closing resources: " + ex.getMessage());
                }
            }

            return doctorId;
        } else {
            return 0; // Hoặc trả về một giá trị mặc định khác thích hợp
        }
    }

    private double getPriceOfMedicine(String medicineName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double price = 0;

        try {
            conn = MainConnection.getConnection();
            String query = "SELECT Price FROM Medicine WHERE Medicine_name = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, medicineName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                price = rs.getDouble("Price");
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }

        return price;
    }

    private int getMedicineId(String medicineName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int medicineId = 0;

        try {
            conn = MainConnection.getConnection();
            String query = "SELECT Medicine_id FROM Medicine WHERE Medicine_name = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, medicineName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                medicineId = rs.getInt("Medicine_id");
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }

        return medicineId;
    }
private int getPatientIdFromComboBox() {
    String selectedPatient = (String) patientComboBox.getSelectedItem();
    if (selectedPatient != null && !selectedPatient.isEmpty()) {
        String[] parts = selectedPatient.split(" - ");
        String patientName = parts[0]; // Lấy tên bệnh nhân từ phần tử đầu tiên
        return getPatientId(patientName); // Sử dụng tên bệnh nhân để lấy Patient_id
    } else {
        return 0; // Hoặc trả về một giá trị mặc định khác thích hợp
    }
}
    private int getPatientId(String patientName) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    int patientId = 0;

    try {
        conn = MainConnection.getConnection();
        String query = "SELECT Patient_id FROM Patients WHERE Patient_name = ?";
        pstmt = conn.prepareStatement(query);
        pstmt.setString(1, patientName);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            patientId = rs.getInt("Patient_id");
        }
    } catch (SQLException ex) {
        System.out.println("Error: " + ex.getMessage());
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println("Error closing resources: " + ex.getMessage());
        }
    }

    return patientId;
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CreateInvoiceFrame().setVisible(true);
            }
        });
    }
}
