package movieticket;

import movieticket.config.DatabaseConnection;

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
    private int showtimeId = 1; // lịch chiếu có ID = 1, sau đó lấy từ combo box
    private Map<String,Boolean> selectedSeats;

    public SeatPanel() {
        setLayout(new BorderLayout());
        selectedSeats = new HashMap<>();

        JPanel seatGrid = new JPanel(new GridLayout(ROWS,COLS,10,10));
        seats = new JButton[ROWS][COLS];
        loadSeatsFromDatabase(seatGrid);

        JButton confirmButton = new JButton("Xác nhận đặt vé");
        confirmButton.addActionListener(e -> confirmBooking());

        add(seatGrid,BorderLayout.CENTER);
        add(confirmButton,BorderLayout.SOUTH);

    }

    private void loadSeatsFromDatabase(JPanel seatGrid) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT seat_number, is_booked FROM seats WHERE showtime_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, showtimeId);
            ResultSet rs = stmt.executeQuery();

            Map<String, Boolean> seatStatusMap = new HashMap<>();
            while (rs.next()) {
                seatStatusMap.put(rs.getString("seat_number"),
                        rs.getBoolean("is_booked"));
            }
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    String seatName = (char) ('A' + row) + String.valueOf(col + 1);
                    // JButton seat = new JButton("R" + (row + 1) + "C" + (col + 1)); // R1C1
                    JButton seat = new JButton(seatName);
                    seats[row][col] = seat;

                    boolean isBooked = seatStatusMap.getOrDefault(seatName, false);
                    seat.setBackground(isBooked ? Color.RED : Color.LIGHT_GRAY);


                    // sự kiện khi click chọn ghế
                    seat.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            toggleSeat(seat, seatName);
                            // co the dung lambda

                        }
                    });

                    seatGrid.add(seat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu ghế: " + e.getMessage());
        }
    }


    // Hàm đổi trạng thái ghế(đã chọn/chưa chọn)
    private void toggleSeat(JButton seat, String seatName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT is_booked FROM seats WHERE showtime_id = ? AND seat_number = ?";
            PreparedStatement stmt = conn.prepareStatement(checkQuery);
            stmt.setInt(1, showtimeId);
            stmt.setString(2, seatName);
            ResultSet rs = stmt.executeQuery();

            boolean isBooked = rs.next() ? rs.getBoolean("is_booked") : false;

            if(isBooked){
                JOptionPane.showMessageDialog(this,"Ghế "  + seatName + " đã được đặt");
            }else{
                boolean currentSelection = selectedSeats.getOrDefault(seatName,false);
                boolean newSelection = !currentSelection;
                selectedSeats.put(seatName,newSelection);
                seat.setBackground(newSelection ? Color.GREEN:Color.LIGHT_GRAY);
                JOptionPane.showMessageDialog(this, newSelection ? "Đã chọn ghế " + seatName
                        : " Đã bỏ chọn ghế " + seatName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật ghế: " + e.getMessage());
        }
    }

    private void confirmBooking(){
        if(selectedSeats.isEmpty()){
            JOptionPane.showMessageDialog(this,"Vui lòng chọn ít nhất một ghế!");
            return;
        }
        try(Connection conn = DatabaseConnection.getConnection()){
            conn.setAutoCommit(false);

            String updateSeatQuery = "UPDATE seats SET is_booked = TRUE WHERE showtime_id = ? AND seat_number = ?";
            String insertTicketQuery = "INSERT INTO tickets (showtime_id, seat_number) VALUE (?,?)";
            PreparedStatement updateStatement = conn.prepareStatement(updateSeatQuery);
            PreparedStatement insertStatement = conn.prepareStatement(insertTicketQuery);

            for(Map.Entry<String,Boolean> entry: selectedSeats.entrySet()){
                if(entry.getValue()){
                    updateStatement.setInt(1,showtimeId);
                    updateStatement.setString(2,entry.getKey());
                    updateStatement.addBatch();

                    insertStatement.setInt(1,showtimeId);
                    insertStatement.setString(2, entry.getKey());
                    insertStatement.addBatch();

                }
            }

            updateStatement.executeBatch();
            insertStatement.executeBatch();
            conn.commit();

            for(Map.Entry<String,Boolean> entry : selectedSeats.entrySet()){
                if(entry.getValue()){
                    for(JButton[] row : seats){
                        for(JButton seat : row){
                            if(seat.getText().equals(entry.getKey())){
                                seat.setBackground(Color.RED);
                            }
                        }
                    }
                }
            }
            selectedSeats.clear();
            JOptionPane.showMessageDialog(this," Đặt vé thành công!");
        }catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi đặt vé: " + e.getMessage());
        }
    }
}
