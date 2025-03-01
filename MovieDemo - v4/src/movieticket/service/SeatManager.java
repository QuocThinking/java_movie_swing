package movieticket.service;

import movieticket.SeatPanel;
import movieticket.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SeatManager {
    private Map<String,Boolean> selectedSeats;

    public SeatManager(){
        selectedSeats = new HashMap<>();
    }

    public boolean toggleSeatSelection(String seatName, int showtimeId){
        try (Connection conn = DatabaseConnection.getConnection()){
            String query = "SELECT is_booked FROM seats WHERE showtime_id = ? AND seat_number = ? ";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1,showtimeId);
            stmt.setString(2,seatName);

            ResultSet rs = stmt.executeQuery();
            boolean isBooked = rs.next() ? rs.getBoolean("is_booked") : false;
            if(isBooked){
                return false; // Ghế đã booked, no change
            }else {
                boolean currentSelection = selectedSeats.getOrDefault(seatName, false);
                boolean newSelection = !currentSelection;
                selectedSeats.put(seatName,newSelection);
                return true;
            }
        }catch (SQLException sql){
            sql.printStackTrace();
            return false;
        }
    }

    public Map<String,Boolean> getSelectedSeats(){
        return selectedSeats;
    }
    public void clearSelectedSeats(){
        selectedSeats.clear();
    }

}
