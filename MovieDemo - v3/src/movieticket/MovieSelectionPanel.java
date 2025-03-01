package movieticket;

import movieticket.config.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MovieSelectionPanel extends JPanel {

    private JComboBox<String> movieComboBox;
    private JComboBox<String> showtimeComboBox;
    private SeatPanel seatPanel;
    private JLabel movieInfoLabel;

    public MovieSelectionPanel(SeatPanel seatPanel) {
        this.seatPanel = seatPanel;
        setLayout(new FlowLayout());

        JLabel movieLabel = new JLabel("Chọn phim: ");
        movieComboBox = new JComboBox<>(loadMovies());
        movieComboBox.addActionListener(e -> updateShowtimesAndInfo());

        JLabel showtimeLabel = new JLabel("Chọn lịch chiếu: ");
        showtimeComboBox = new JComboBox<>();
        showtimeComboBox.addActionListener(e -> updateSeatPanel());

        movieInfoLabel = new JLabel("Thông tin phim: Chưa chọn phim");

        add(movieLabel);
        add(movieComboBox);
        add(showtimeLabel);
        add(showtimeComboBox);
        add(movieInfoLabel);

        updateShowtimesAndInfo();
    }

    private String[] loadMovies(){
        List<String> movies = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()){
            String query = "SELECT DISTINCT movie_name FROM movies";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                movies.add(rs.getString("movie_name"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return movies.toArray(new String[0]);
    }

    private void updateShowtimesAndInfo(){
        showtimeComboBox.removeAllItems();
        String selectedMovie = (String) movieComboBox.getSelectedItem();
        if(selectedMovie == null) return;


        try (Connection conn = DatabaseConnection.getConnection()){

            String infoQuery = "SELECT duration, genre FROM movies WHERE movie_name = ?";
            PreparedStatement infoStmt = conn.prepareStatement(infoQuery);
            infoStmt.setString(1, selectedMovie);
            ResultSet infoRs = infoStmt.executeQuery();
            if(infoRs.next()){
                movieInfoLabel.setText("Thông tin phim: " + selectedMovie + "-" +
                        infoRs.getInt("duration") + " phút - " + infoRs.getString("genre"));
            }

            String query = "SELECT s.showtime_id, s.showtime, s.room" +
                    " FROM showtimes s " +
                    " JOIN movies m ON s.movie_id = m.movie_id " +
                    " WHERE m.movie_name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, selectedMovie);
            ResultSet rs = stmt.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            while (rs.next()){
                String fmTime = sdf.format(rs.getTimestamp("showtime"));
                showtimeComboBox.addItem(rs.getInt("showtime_id") + " - "
                + rs.getString("room") + " - " + fmTime);
            }
        }catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải lịch chiếu: " + e.getMessage());
        }
    }

    private void updateSeatPanel(){
        String selectedShowtime = (String) showtimeComboBox.getSelectedItem();
        if(selectedShowtime != null){
            int showtimeId = Integer.parseInt(selectedShowtime.split(" - ")[0]);
            seatPanel.loadSeatsForShowtime(showtimeId);
        }
    }


}
