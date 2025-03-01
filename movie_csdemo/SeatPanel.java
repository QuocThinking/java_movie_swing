package movieticket;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SeatPanel extends JPanel {
    private static final int ROWS = 6;
    private static final int COLS = 8;
    private JButton[][] seats;
    private int showtimeId;
    private SeatManager seatManager;
    private BookingManager bookingManager;

    public SeatPanel() {
        setLayout(new BorderLayout());
        seats = new JButton[ROWS][COLS];
        seatManager = new SeatManager();
        bookingManager = new BookingManager();

        JPanel seatGrid = new JPanel(new GridLayout(ROWS, COLS, 10, 10));
        JButton confirmButton = new JButton("Xác nhận đặt vé");
        confirmButton.addActionListener(e -> confirmBooking());

        add(seatGrid, BorderLayout.CENTER);
        add(confirmButton, BorderLayout.SOUTH);

        // Load mặc định với showtimeId = 1
        loadSeatsForShowtime(1);
    }

    public void loadSeatsForShowtime(int showtimeId) {
        this.showtimeId = showtimeId;
        seatManager.clearSelectedSeats();
        removeAll(); // Xóa giao diện cũ
        setLayout(new BorderLayout());

        JPanel seatGrid = new JPanel(new GridLayout(ROWS, COLS, 10, 10));
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT seat_number, is_booked FROM seats WHERE showtime_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, showtimeId);
            ResultSet rs = stmt.executeQuery();

            Map<String, Boolean> seatStatusMap = new HashMap<>();
            while (rs.next()) {
                seatStatusMap.put(rs.getString("seat_number"), rs.getBoolean("is_booked"));
            }

            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    String seatName = (char)('A' + row) + String.valueOf(col + 1);
                    JButton seat = new JButton(seatName);
                    seats[row][col] = seat;

                    boolean isBooked = seatStatusMap.getOrDefault(seatName, false);
                    seat.setBackground(isBooked ? Color.RED : Color.LIGHT_GRAY);

                    seat.addActionListener(e -> {
                        if (seatManager.toggleSeatSelection(seatName, showtimeId)) {
                            boolean isSelected = seatManager.getSelectedSeats().get(seatName);
                            seat.setBackground(isSelected ? Color.GREEN : Color.LIGHT_GRAY);
                            JOptionPane.showMessageDialog(this, isSelected ? "Đã chọn ghế " + seatName : "Đã bỏ chọn ghế " + seatName);
                        } else {
                            JOptionPane.showMessageDialog(this, "Ghế " + seatName + " đã được đặt!");
                        }
                    });

                    seatGrid.add(seat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu ghế: " + e.getMessage());
        }

        JButton confirmButton = new JButton("Xác nhận đặt vé");
        confirmButton.addActionListener(e -> confirmBooking());

        add(seatGrid, BorderLayout.CENTER);
        add(confirmButton, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void confirmBooking() {
        if (bookingManager.confirmBooking(showtimeId, seatManager.getSelectedSeats())) {
            for (Map.Entry<String, Boolean> entry : seatManager.getSelectedSeats().entrySet()) {
                if (entry.getValue()) {
                    for (JButton[] row : seats) {
                        for (JButton seat : row) {
                            if (seat.getText().equals(entry.getKey())) {
                                seat.setBackground(Color.RED);
                            }
                        }
                    }
                }
            }
            seatManager.clearSelectedSeats();
            JOptionPane.showMessageDialog(this, "Đặt vé thành công!");
        }
    }
}