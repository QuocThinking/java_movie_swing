package movieticket;

import movieticket.config.DatabaseConnection;
import movieticket.service.BookingManager;
import movieticket.service.SeatManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    //    private final boolean[][] seatStatus;
    private int showtimeId ; // lịch chiếu có ID = 1, sau đó lấy từ combo box
    private SeatManager seatManager;
    private BookingManager bookingManager;
    private JPanel seatGrid;

    public SeatPanel(int showtimeId) {
        this.showtimeId = showtimeId;
        setLayout(new BorderLayout());
        seats = new JButton[ROWS][COLS];
        seatManager = new SeatManager();
        bookingManager = new BookingManager();
        seatGrid = new JPanel(new GridLayout(ROWS,COLS,10,10));

        JLabel screenLabel = new JLabel("Màn hình", SwingConstants.CENTER);
        screenLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(screenLabel, BorderLayout.NORTH);

        JPanel seatPanel = new JPanel(new BorderLayout());
        seatPanel.add(seatGrid, BorderLayout.CENTER);

        JPanel legendPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        legendPanel.add(createLegend(Color.RED, "Đã đặt"));
        legendPanel.add(createLegend(Color.GREEN, "Bạn chọn"));
        legendPanel.add(createLegend(Color.MAGENTA, "Thường"));
        legendPanel.add(createLegend(Color.PINK, "VIP"));
        legendPanel.add(createLegend(Color.ORANGE, "Sweetbox"));
        legendPanel.add(createLegend(Color.BLUE, "Trung tâm"));
        add(legendPanel, BorderLayout.SOUTH);



//        JPanel seatGrid = new JPanel(new GridLayout(ROWS,COLS,10,10));
        JButton confirmButton = new JButton("Xác nhận đặt vé");
        confirmButton.addActionListener(e -> confirmBooking());
        seatPanel.add(confirmButton, BorderLayout.NORTH);

        add(seatPanel, BorderLayout.CENTER);

        loadSeatsForShowtime(showtimeId);

    }

    private JPanel createLegend(Color color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel colorBox = new JLabel();
        colorBox.setOpaque(true);
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        panel.add(colorBox);
        panel.add(new JLabel(text));
        return panel;
    }

    public void loadSeatsForShowtime(int showtimeId) {
        this.showtimeId = showtimeId;
        seatManager.clearSelectedSeats();
        seatGrid.removeAll();
        seats = new JButton[ROWS][COLS];


        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT seat_number, is_booked, seat_type FROM seats WHERE showtime_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, showtimeId);
            ResultSet rs = stmt.executeQuery();

            Map<String, Boolean> seatStatusMap = new HashMap<>();
            Map<String, String> seatTypeMap = new HashMap<>();
            while (rs.next()) {
                seatStatusMap.put(rs.getString("seat_number"), rs.getBoolean("is_booked"));
                seatTypeMap.put(rs.getString("seat_number"), rs.getString("seat_type"));
            }
            System.out.println("Loading seats for showtime_id: " + showtimeId + ", booked seats: " + seatStatusMap);

            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    String seatName = (char)('A' + row) + String.valueOf(col + 1);
                    JButton seat = new JButton(seatName);
                    seats[row][col] = seat;

                    boolean isBooked = seatStatusMap.getOrDefault(seatName, false);
                    final String seatType = seatTypeMap.getOrDefault(seatName, "NORMAL");
                    final int finalRow = row; // Sao chép thành biến final
                    final int finalCol = col; // Sao chép thành biến final

                    if (isBooked) {
                        seat.setBackground(Color.RED);
                    } else {
                        switch (seatType) {
                            case "VIP":
                                seat.setBackground(Color.PINK);
                                break;
                            case "SWEETBOX":
                                seat.setBackground(Color.ORANGE);
                                break;
                            default:
                                seat.setBackground(Color.MAGENTA);
                                if (finalRow >= 3 && finalRow <= 4 && finalCol >= 1 && finalCol <= 6) {
                                    seat.setBackground(Color.BLUE);
                                    seat.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                                }
                                break;
                        }
                    }

                    seat.addActionListener(e -> {
                        if (seatManager.toggleSeatSelection(seatName, showtimeId)) {
                            boolean isSelected = seatManager.getSelectedSeats().get(seatName);
                            seat.setBackground(isSelected ? Color.GREEN :
                                    (seatType.equals("VIP") ? Color.PINK : seatType.equals("SWEETBOX") ? Color.ORANGE :
                                            (finalRow >= 3 && finalRow <= 4 && finalCol >= 1 && finalCol <= 6) ? Color.BLUE : Color.MAGENTA));
                            JOptionPane.showMessageDialog(this, isSelected ? "Đã chọn ghế " + seatName :
                                    "Đã bỏ chọn ghế " + seatName);
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

        revalidate();
        repaint();
    }
   private void confirmBooking(){
        if(bookingManager.confirmBooking(showtimeId,seatManager.getSelectedSeats())){
            for (Map.Entry<String, Boolean> entry: seatManager.getSelectedSeats().entrySet()){
                if(entry.getValue()){
                    for(JButton[] row: seats){
                        for(JButton seat: row){
                            if(seat.getText().equals(entry.getKey())){
                                seat.setBackground(Color.RED);
                            }
                        }
                    }
                }
            }
            seatManager.clearSelectedSeats();
            JOptionPane.showMessageDialog(this,"Đăt vé thành công!");
        }
   }

}
