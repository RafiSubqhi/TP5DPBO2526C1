import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PeopleMenu extends JFrame {
    private JPanel mainPanel;
    private JTable personTable;
    private JTextField idField;
    private JTextField namaField;
    private JTextField tahunField;
    private JComboBox<String> kategoriComboBox;
    private JCheckBox wfhCheckBox;
    private JButton addUpdateButton;
    private JButton deleteButton;
    private JButton cancelButton;
    private JLabel namaLabel;
    private JLabel tahunLabel;
    private JLabel titleLabel;

    private Database database;
    private String selectedId = ""; // Menggantikan selectedIndex karena ArrayList dihapus

    public static void main(String[] args) {
        PeopleMenu menu = new PeopleMenu();
        menu.setSize(800, 600);
        menu.setLocationRelativeTo(null);
        menu.setContentPane(menu.mainPanel);
        menu.getContentPane().setBackground(Color.WHITE);
        menu.setVisible(true);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public PeopleMenu() {
        // Inisialisasi Database
        database = new Database();

        String[] kategoriData = {"Developer", "Data Analyst", "UI/UX Designer", "DevOps", "QA Tester"};
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));

        // Panggil data awal untuk mengisi tabel
        personTable.setModel(setTable());

        deleteButton.setVisible(false);

        // Listener Add/Update
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedId.isEmpty()) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        // Listener Delete
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Apakah Anda yakin ingin menghapus data ini?",
                        "Konfirmasi Hapus",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    deleteData();
                }
            }
        });

        // Listener Cancel
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // Listener Klik Tabel
        personTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = personTable.getSelectedRow();

                // Ambil data dari baris tabel yang diklik (indeks kolom dimulai dari 0)
                selectedId = personTable.getModel().getValueAt(selectedRow, 1).toString();
                String curNama = personTable.getModel().getValueAt(selectedRow, 2).toString();
                String curTahun = personTable.getModel().getValueAt(selectedRow, 3).toString();
                String curKategori = personTable.getModel().getValueAt(selectedRow, 4).toString();
                String curWfh = personTable.getModel().getValueAt(selectedRow, 5).toString();

                idField.setText(selectedId);
                namaField.setText(curNama);
                tahunField.setText(curTahun);
                kategoriComboBox.setSelectedItem(curKategori);
                wfhCheckBox.setSelected(curWfh.equals("Ya"));

                addUpdateButton.setText("Update");
                deleteButton.setVisible(true);
                idField.setEditable(false); // Kunci textfield ID agar tidak diganti saat update
            }
        });
    }

    // CREATE (Ambil data dari DB ke Tabel)
    public final DefaultTableModel setTable() {
        Object[] cols = { "No", "ID", "Nama", "Tahun Lahir", "Kategori", "Status WFH" };
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM person");
            int i = 1;

            while (resultSet.next()) {
                Object[] row = new Object[6];
                row[0] = i;
                row[1] = resultSet.getString("id");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getInt("tahun_lahir");
                row[4] = resultSet.getString("kategori");

                boolean wfh = resultSet.getBoolean("wfh");
                row[5] = wfh ? "Ya" : "Tidak";

                tmp.addRow(row);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tmp;
    }

    // INSERT (Tambah data)
    public void insertData() {
        try {
            String id = idField.getText();
            String nama = namaField.getText();
            String tahunStr = tahunField.getText();
            String kategori = kategoriComboBox.getSelectedItem().toString();
            boolean isWfh = wfhCheckBox.isSelected();

            // Syarat TP5: Cek jika inputan kosong
            if(id.isEmpty() || nama.isEmpty() || tahunStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua kolom harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int tahun = Integer.parseInt(tahunStr);
            int wfhValue = isWfh ? 1 : 0;

            String sqlQuery = "INSERT INTO person (id, nama, tahun_lahir, kategori, wfh) VALUES ('"
                    + id + "', '" + nama + "', " + tahun + ", '" + kategori + "', " + wfhValue + ")";

            database.insertUpdateDeleteQuery(sqlQuery);
            personTable.setModel(setTable());
            clearForm();
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun Lahir harus berupa angka bulat!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Syarat TP5: Cek ID duplikat
            JOptionPane.showMessageDialog(null, "Gagal! Pastikan ID belum digunakan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // UPDATE (Ubah data)
    public void updateData() {
        try {
            String id = idField.getText(); // ID sebagai patokan WHERE (tidak diedit)
            String nama = namaField.getText();
            String tahunStr = tahunField.getText();
            String kategori = kategoriComboBox.getSelectedItem().toString();
            boolean isWfh = wfhCheckBox.isSelected();

            if(nama.isEmpty() || tahunStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua kolom harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int tahun = Integer.parseInt(tahunStr);
            int wfhValue = isWfh ? 1 : 0;

            String sqlQuery = "UPDATE person SET nama = '" + nama + "', tahun_lahir = " + tahun +
                    ", kategori = '" + kategori + "', wfh = " + wfhValue + " WHERE id = '" + id + "'";

            database.insertUpdateDeleteQuery(sqlQuery);
            personTable.setModel(setTable());
            clearForm();
            JOptionPane.showMessageDialog(null, "Data berhasil diubah!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun Lahir harus berupa angka bulat!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // DELETE (Hapus data)
    public void deleteData() {
        if (!selectedId.isEmpty()) {
            String sqlQuery = "DELETE FROM person WHERE id = '" + selectedId + "'";
            database.insertUpdateDeleteQuery(sqlQuery);
            personTable.setModel(setTable());
            clearForm();
            JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
        }
    }

    // CLEAR FORM
    public void clearForm() {
        idField.setText("");
        namaField.setText("");
        tahunField.setText("");
        kategoriComboBox.setSelectedIndex(0);
        wfhCheckBox.setSelected(false);

        idField.setEditable(true); // Buka kembali textfield ID
        addUpdateButton.setText("Add");
        deleteButton.setVisible(false);
        selectedId = "";
    }
}