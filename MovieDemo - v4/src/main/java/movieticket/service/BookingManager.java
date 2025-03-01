package movieticket.service;

import movieticket.config.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class BookingManager {
    public boolean confirmBooking(int showtimeId, Map<String, Boolean> selectedSeats){
        if(selectedSeats.isEmpty()){
            JOptionPane.showMessageDialog(null, "Vui lòng chọn ít nhất 1 ghế");
            return false;
        }
        try (Connection conn = DatabaseConnection.getConnection()){
            conn.setAutoCommit(false);

            String updatedSeatQuery = "UPDATE seats SET is_booked = TRUE WHERE showtime_id = ?" +
                    " AND seat_number = ?";
            String insertTicketQuery = "INSERT INTO tickets (showtime_id, seat_number) VALUES" +
                    " (?,?)";
            PreparedStatement upStmt = conn.prepareStatement(updatedSeatQuery);
            PreparedStatement inStmt = conn.prepareStatement(insertTicketQuery);

            for(Map.Entry<String,Boolean> entry: selectedSeats.entrySet()){
                if(entry.getValue()){
                    upStmt.setInt(1,showtimeId);
                    upStmt.setString(2,entry.getKey());
                    upStmt.addBatch();

                    inStmt.setInt(1,showtimeId);
                    inStmt.setString(2,entry.getKey());
                    inStmt.addBatch();


                }
            }

            upStmt.executeBatch();
            inStmt.executeBatch();

            conn.commit();
            return true;


        }catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Lỗi đặt vé: " + e.getMessage());
            return false;
        }
    }
}
