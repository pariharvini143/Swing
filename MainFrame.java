package com.info.ssw;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainFrame extends JFrame {	
	 LoginPage obj=new LoginPage();
    // Inventory components
    JTable inventoryTable;
    DefaultTableModel inventoryModel;
    JTextField tfBloodGroup, tfBloodType, tfStock, tfCollectionDate, tfExpiryDate, tfLocation;
    JButton btnAdd, btnUpdate, btnDelete, btnRefresh;

    // Donor components
    JTable donorTable;
    DefaultTableModel donorModel;
    JTextField tfDonorName, tfDonorAge, tfDonorGroup, tfContact, tfAddress, tfLastDonation;
    JButton btnDonorAdd, btnDonorUpdate, btnDonorDelete, btnDonorRefresh;

    Connection conn;

    public MainFrame() {
        setTitle("Blood Bank Management");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);  

        JTabbedPane tabbedPane = new JTabbedPane();

        // --------- Inventory Panel ---------
        JPanel inventoryPanel = new JPanel(new BorderLayout());

        inventoryModel = new DefaultTableModel();
        inventoryModel.setColumnIdentifiers(new String[]{"Inventory ID", "Blood Group", "Blood Type", "Stock", "Collection Date", "Expiry Date", "Storage Location"});
        inventoryTable = new JTable(inventoryModel);
        inventoryPanel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        JPanel inventoryForm = new JPanel(new GridLayout(3, 4, 10, 10));
        tfBloodGroup = new JTextField(); tfBloodType = new JTextField();
        tfStock = new JTextField(); tfCollectionDate = new JTextField();
        tfExpiryDate = new JTextField(); tfLocation = new JTextField();

        inventoryForm.add(new JLabel("Blood Group")); inventoryForm.add(tfBloodGroup);
        inventoryForm.add(new JLabel("Blood Type")); inventoryForm.add(tfBloodType);
        inventoryForm.add(new JLabel("Stock")); inventoryForm.add(tfStock);
        inventoryForm.add(new JLabel("Collection Date")); inventoryForm.add(tfCollectionDate);
        inventoryForm.add(new JLabel("Expiry Date")); inventoryForm.add(tfExpiryDate);
        inventoryForm.add(new JLabel("Storage Location")); inventoryForm.add(tfLocation);
        inventoryPanel.add(inventoryForm, BorderLayout.NORTH);

        JPanel inventoryButtons = new JPanel();
        btnAdd = new JButton("Add"); btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete"); btnRefresh = new JButton("Refresh");
        inventoryButtons.add(btnAdd); inventoryButtons.add(btnUpdate);
        inventoryButtons.add(btnDelete); inventoryButtons.add(btnRefresh);
        inventoryPanel.add(inventoryButtons, BorderLayout.SOUTH);

        // --------- Donor Panel ---------
        JPanel donorPanel = new JPanel(new BorderLayout());

        donorModel = new DefaultTableModel();
        donorModel.setColumnIdentifiers(new String[]{"Donor ID", "Name", "Age", "Blood Group", "Contact", "Address", "Last Donation Date"});
        donorTable = new JTable(donorModel);
        donorPanel.add(new JScrollPane(donorTable), BorderLayout.CENTER);

        JPanel donorForm = new JPanel(new GridLayout(3, 4, 10, 10));
        tfDonorName = new JTextField(); tfDonorAge = new JTextField();
        tfDonorGroup = new JTextField(); tfContact = new JTextField();
        tfAddress = new JTextField(); tfLastDonation = new JTextField();

        donorForm.add(new JLabel("Name")); donorForm.add(tfDonorName);
        donorForm.add(new JLabel("Age")); donorForm.add(tfDonorAge);
        donorForm.add(new JLabel("Blood Group")); donorForm.add(tfDonorGroup);
        donorForm.add(new JLabel("Contact")); donorForm.add(tfContact);
        donorForm.add(new JLabel("Address")); donorForm.add(tfAddress);
        donorForm.add(new JLabel("Last Donation Date")); donorForm.add(tfLastDonation);
        donorPanel.add(donorForm, BorderLayout.NORTH);

        JPanel donorButtons = new JPanel();
        btnDonorAdd = new JButton("Add"); btnDonorUpdate = new JButton("Update");
        btnDonorDelete = new JButton("Delete"); btnDonorRefresh = new JButton("Refresh");
        donorButtons.add(btnDonorAdd); donorButtons.add(btnDonorUpdate);
        donorButtons.add(btnDonorDelete); donorButtons.add(btnDonorRefresh);
        donorPanel.add(donorButtons, BorderLayout.SOUTH);

       
        tabbedPane.addTab("Inventory", inventoryPanel);
        tabbedPane.addTab("Donor", donorPanel);
        add(tabbedPane);

        
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/blood_bank", "root", "143143");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Connection Failed: " + e.getMessage());
        }
        
        
        loadInventory();
        loadDonor();

        
        btnAdd.addActionListener(e -> addInventory());
        btnUpdate.addActionListener(e -> updateInventory());
        btnDelete.addActionListener(e -> deleteInventory());
        btnRefresh.addActionListener(e -> loadInventory());

        inventoryTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = inventoryTable.getSelectedRow();
                tfBloodGroup.setText(inventoryModel.getValueAt(row, 1).toString());
                tfBloodType.setText(inventoryModel.getValueAt(row, 2).toString());
                tfStock.setText(inventoryModel.getValueAt(row, 3).toString());
                tfCollectionDate.setText(inventoryModel.getValueAt(row, 4).toString());
                tfExpiryDate.setText(inventoryModel.getValueAt(row, 5).toString());
                tfLocation.setText(inventoryModel.getValueAt(row, 6).toString());
            }
        });

        
        btnDonorAdd.addActionListener(e -> addDonor());
        btnDonorUpdate.addActionListener(e -> updateDonor());
        btnDonorDelete.addActionListener(e -> deleteDonor());
        btnDonorRefresh.addActionListener(e -> loadDonor());

        donorTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = donorTable.getSelectedRow();
                tfDonorName.setText(donorModel.getValueAt(row, 1).toString());
                tfDonorAge.setText(donorModel.getValueAt(row, 2).toString());
                tfDonorGroup.setText(donorModel.getValueAt(row, 3).toString());
                tfContact.setText(donorModel.getValueAt(row, 4).toString());
                tfAddress.setText(donorModel.getValueAt(row, 5).toString());
                tfLastDonation.setText(donorModel.getValueAt(row, 6).toString());
            }
        });
    }

    // ================= Inventory CRUD =====================
    void loadInventory() {
        inventoryModel.setRowCount(0);
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM swingbloodinventory"); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                inventoryModel.addRow(new Object[]{
                    rs.getInt("inventory_id"), rs.getString("blood_group"), rs.getString("blood_type"),
                    rs.getInt("blood_stock"), rs.getString("collection_date"),
                    rs.getString("expiry_date"), rs.getString("storage_location")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Load Error: " + e.getMessage());
        }
    }

    void addInventory() {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO swingbloodinventory (blood_group, blood_type, blood_stock, collection_date, expiry_date, storage_location) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, tfBloodGroup.getText());
            stmt.setString(2, tfBloodType.getText());
            stmt.setInt(3, Integer.parseInt(tfStock.getText()));
            stmt.setString(4, tfCollectionDate.getText());
            stmt.setString(5, tfExpiryDate.getText());
            stmt.setString(6, tfLocation.getText());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Inventory Added");
            loadInventory();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Insert Error: " + e.getMessage());
        }
    }

    void updateInventory() {
        int row = inventoryTable.getSelectedRow();
        if (row == -1) return;
        int id = Integer.parseInt(inventoryModel.getValueAt(row, 0).toString());
        try (PreparedStatement stmt = conn.prepareStatement("UPDATE swingbloodinventory SET blood_group=?, blood_type=?, blood_stock=?, collection_date=?, expiry_date=?, storage_location=? WHERE inventory_id=?")) {
            stmt.setString(1, tfBloodGroup.getText());
            stmt.setString(2, tfBloodType.getText());
            stmt.setInt(3, Integer.parseInt(tfStock.getText()));
            stmt.setString(4, tfCollectionDate.getText());
            stmt.setString(5, tfExpiryDate.getText());
            stmt.setString(6, tfLocation.getText());
            stmt.setInt(7, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Inventory Updated");
            loadInventory();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update Error: " + e.getMessage());
        }
    }

    void deleteInventory() {
        int row = inventoryTable.getSelectedRow();
        if (row == -1) return;
        int id = Integer.parseInt(inventoryModel.getValueAt(row, 0).toString());
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM swingbloodinventory WHERE inventory_id=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Inventory Deleted");
            loadInventory();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + e.getMessage());
        }
    }

    // ================= Donor CRUD =====================
    void loadDonor() {
        donorModel.setRowCount(0);
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM swingdonor"); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                donorModel.addRow(new Object[]{
                    rs.getInt("donor_id"), rs.getString("name"), rs.getInt("age"),
                    rs.getString("blood_group"), rs.getString("contact"),
                    rs.getString("address"), rs.getString("last_donation_date")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Donor Load Error: " + e.getMessage());
        }
    }

    void addDonor() {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO swingdonor (name, age, blood_group, contact, address, last_donation_date) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, tfDonorName.getText());
            stmt.setInt(2, Integer.parseInt(tfDonorAge.getText()));
            stmt.setString(3, tfDonorGroup.getText());
            stmt.setString(4, tfContact.getText());
            stmt.setString(5, tfAddress.getText());
            stmt.setString(6, tfLastDonation.getText());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Donor Added");
            loadDonor();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Insert Error: " + e.getMessage());
        }
    }

    void updateDonor() {
        int row = donorTable.getSelectedRow();
        if (row == -1) return;
        int id = Integer.parseInt(donorModel.getValueAt(row, 0).toString());
        try (PreparedStatement stmt = conn.prepareStatement(
            "UPDATE swingdonor SET name=?, age=?, blood_group=?, contact=?, address=?, last_donation_date=? WHERE donor_id=?")) {

            stmt.setString(1, tfDonorName.getText());
            stmt.setInt(2, Integer.parseInt(tfDonorAge.getText()));
            stmt.setString(3, tfDonorGroup.getText());
            stmt.setString(4, tfContact.getText());
            stmt.setString(5, tfAddress.getText());
            stmt.setString(6, tfLastDonation.getText());
            stmt.setInt(7, id);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Donor Updated");
            loadDonor();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update Error: " + e.getMessage());
        }
    }

    void deleteDonor() {
        int row = donorTable.getSelectedRow();
        if (row == -1) return;
        int id = Integer.parseInt(donorModel.getValueAt(row, 0).toString());
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM swingdonor WHERE donor_id=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this,  "Donor Deleted");
            loadDonor();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
        
    }
}
