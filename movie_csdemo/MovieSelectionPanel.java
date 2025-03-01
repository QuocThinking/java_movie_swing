package movieticket;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieSelectionPanel extends JPanel {
    private JComboBox<String> movieComboBox;
    private JComboBox<String> showtimeComboBox;
    private SeatPanel seatPanel;

    public MovieSelectionPanel(SeatPanel seatPanel) {
        this.seatPanel = seatPanel;
        setLayout(new FlowLayout());

        JLabel movieLabel = new JLabel("Chọn phim: ");
        movieComboBox = new JComboBox<>(loadMovies());
        movieComboBox.addActionListener(e -> updateShowtimes());

        JLabel showtimeLabel = new JLabel("Chọn lịch chiếu: ");
        showtimeComboBox = new JComboBox<>();
        showtimeComboBox.addActionListener(e -> updateSeatPanel());

        add(movieLabel);
        add(movieComboBox);
        add(showtimeLabel);
        add(showtimeComboBox);

        updateShowtimes(); // Load lịch chiếu ban đầu
    }

    private String[] loadMovies() {
        List<String> movies = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT movie_name FROM movies";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movies.add(rs.getString("movie_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies.toArray(new String[0]);
    }

    private void updateShowtimes() {
        showtimeComboBox.removeAllItems();
        String selectedMovie = (String) movieComboBox.getSelectedItem();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT showtime_id, showtime FROM showtimes WHERE movie_id = " +
                          "(SELECT movie_id FROM movies WHERE movie_name = ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, selectedMovie);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                showtimeComboBox.addItem(rs.getInt("showtime_id") + " - " + rs.getString("showtime"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateSeatPanel() {
        String selectedShowtime = (String) showtimeComboBox.getSelectedItem();
        if (selectedShowtime != null) {
            int showtimeId = Integer.parseInt(selectedShowtime.split(" - ")[0]);
            seatPanel.loadSeatsForShowtime(showtimeId);
        }
    }
}