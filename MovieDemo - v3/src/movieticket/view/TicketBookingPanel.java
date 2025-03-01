package movieticket.view;

import movieticket.SeatPanel;
import movieticket.config.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TicketBookingPanel extends JPanel {
    private String movieName;
    private JComboBox<String> dayComboBox;
    private JList<String> theaterList;
    private JTextArea showtimeArea;
    private DefaultListModel<String> theaterModel;

    public TicketBookingPanel(String movieName) {
        this.movieName = movieName;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        // Tiêu đề
        JLabel title = new JLabel("Thông tin phim và vé: " + movieName, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(0, 102, 204));
        add(title, BorderLayout.NORTH);

        // Panel Nội dung
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dayPanel.setBorder(BorderFactory.createTitledBorder("Chọn ngày"));
        dayComboBox = new JComboBox<>(loadDays());
        dayComboBox.setMaximumRowCount(7);
        dayComboBox.setPreferredSize(new Dimension(150, 30));
        dayComboBox.addActionListener(e -> updateTheatersAndShowtimes());
        dayPanel.add(dayComboBox);
        contentPanel.add(dayPanel, BorderLayout.NORTH); // Thêm vào NORTH

        // Danh sách rạp
        JPanel theaterPanel = new JPanel(new BorderLayout());
        theaterPanel.setBorder(BorderFactory.createTitledBorder("Chọn rạp"));
        theaterModel = new DefaultListModel<>();
        theaterList = new JList<>(theaterModel);
        theaterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        theaterList.addListSelectionListener(e -> updateShowtimes());
        JScrollPane theaterScrollPane = new JScrollPane(theaterList);
        theaterScrollPane.setPreferredSize(new Dimension(150, 200));
        theaterPanel.add(theaterScrollPane, BorderLayout.CENTER);
        contentPanel.add(theaterPanel);

        // Lịch chiếu
        JPanel showtimePanel = new JPanel(new BorderLayout());
        showtimePanel.setBorder(BorderFactory.createTitledBorder("Lịch chiếu"));
        showtimeArea = new JTextArea(10, 20);
        showtimeArea.setEditable(false);
        showtimeArea.setLineWrap(true);
        showtimeArea.setWrapStyleWord(true);
        showtimeArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int caretPosition = showtimeArea.getCaretPosition();
                    String fullText = showtimeArea.getText();
                    String selectedLine = getSelectedLine(fullText, caretPosition);
                    if (selectedLine != null) {
                        openSeatSelection(selectedLine);
                    }
                }
            }
        });
        JScrollPane showtimeScrollPane = new JScrollPane(showtimeArea);
        showtimePanel.add(showtimeScrollPane, BorderLayout.CENTER);
        contentPanel.add(showtimePanel);

        add(contentPanel, BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        updateTheatersAndShowtimes();
    }

    private String[] loadDays() {
        List<String> days = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            String day = sdf.format(cal.getTime());
            String label = (i == 0 ? "Hôm nay " : i == 1 ? "Ngày mai: " : "Ngày ") + day;
            days.add(label);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return days.toArray(new String[0]);
    }

    private void updateTheatersAndShowtimes() {
        String selectedDay = (String) dayComboBox.getSelectedItem();
        if (selectedDay == null) return;

        String dayFormatted = selectedDay.split(" ")[selectedDay.split(" ").length - 1];
        theaterModel.clear();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT DISTINCT t.theater_name " +
                    "FROM theaters t " +
                    "JOIN showtimes s ON s.theater_id = t.theater_id " +
                    "JOIN movies m ON s.movie_id = m.movie_id " +
                    "WHERE m.movie_name = ? AND DATE(s.showtime) = STR_TO_DATE(?, '%d/%m/%Y')";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, movieName);
            stmt.setString(2, dayFormatted);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                theaterModel.addElement(rs.getString("theater_name"));
            }
            System.out.println("Theaters loaded for " + movieName + " on "
                    + dayFormatted + ": " + theaterModel.size());

            if (theaterModel.isEmpty()) {
                query = "SELECT DISTINCT t.theater_name " +
                        "FROM theaters t " +
                        "JOIN showtimes s ON s.theater_id = t.theater_id " +
                        "JOIN movies m ON s.movie_id = m.movie_id " +
                        "WHERE m.movie_name = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, movieName);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    theaterModel.addElement(rs.getString("theater_name"));
                }
                System.out.println("Fallback: All theaters loaded for " + movieName + ": " + theaterModel.size());
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Dùng tạm cho debug
        }

        if (!theaterModel.isEmpty()) {
            theaterList.setSelectedIndex(0);
        }
        updateShowtimes();
    }

    private void updateShowtimes() {
        String selectedDay = (String) dayComboBox.getSelectedItem();
        String selectedTheater = theaterList.getSelectedValue();
        if (selectedDay == null || selectedTheater == null) {
            showtimeArea.setText("");
            return;
        }

        String dayFormatted = selectedDay.split(" ")[selectedDay.split(" ").length - 1];
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT s.showtime_id, s.showtime " +
                    "FROM showtimes s " +
                    "JOIN theaters t ON s.theater_id = t.theater_id " +
                    "JOIN movies m ON s.movie_id = m.movie_id " +
                    "WHERE m.movie_name = ? AND t.theater_name = ? " +
                    "AND DATE(s.showtime) = STR_TO_DATE(?, '%d/%m/%Y')";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, movieName);
            stmt.setString(2, selectedTheater);
            stmt.setString(3, dayFormatted);
            ResultSet rs = stmt.executeQuery();

            StringBuilder showtimes = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            while (rs.next()) {
                String startTime = sdf.format(rs.getTimestamp("showtime"));
                showtimes.append(startTime).append(" ~ ").append(calculateEndTime(startTime)).append("\n");
            }
            showtimeArea.setText(showtimes.toString());
            System.out.println("Showtimes for " + movieName + " at " + selectedTheater + " on " + dayFormatted + ": " + showtimes.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String calculateEndTime(String startTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date = sdf.parse(startTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT duration FROM movies WHERE movie_name = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, movieName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    cal.add(Calendar.MINUTE, rs.getInt("duration"));
                }
            }
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return startTime;
        }
    }

    private String getSelectedLine(String fullText, int caretPosition) {
        String[] lines = fullText.split("\n");
        int currentPos = 0;
        for (String line : lines) {
            currentPos += line.length() + 1; // +1 cho ký tự xuống dòng
            if (currentPos > caretPosition) {
                return line.trim();
            }
        }
        return null;
    }

    private void openSeatSelection(String selectedTime) {
        String selectedDay = (String) dayComboBox.getSelectedItem();
        String selectedTheater = theaterList.getSelectedValue();
        if (selectedDay == null || selectedTheater == null || selectedTime == null) return;

        String dayFormatted = selectedDay.split(" ")[selectedDay.split(" ").length - 1];
        String startTime;
        try {
            startTime = selectedTime.split(" ~ ")[0]; // Lấy giờ bắt đầu từ "19:00 ~ 21:00"
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid selected time format: " + selectedTime);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT s.showtime_id " +
                    "FROM showtimes s " +
                    "JOIN theaters t ON s.theater_id = t.theater_id " +
                    "JOIN movies m ON s.movie_id = m.movie_id " +
                    "WHERE m.movie_name = ? AND t.theater_name = ? " +
                    "AND DATE(s.showtime) = STR_TO_DATE(?, '%d/%m/%Y') " +
                    "AND TIME(s.showtime) = STR_TO_DATE(?, '%H:%i')";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, movieName);
            stmt.setString(2, selectedTheater);
            stmt.setString(3, dayFormatted);
            stmt.setString(4, startTime);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int showtimeId = rs.getInt("showtime_id");
                JFrame seatFrame = new JFrame("Chọn ghế cho " + movieName + " - " + selectedTime);
                seatFrame.setSize(600, 400);
                seatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                seatFrame.add(new SeatPanel(showtimeId));
                seatFrame.setVisible(true);
                System.out.println("Opening seat selection for " + movieName + " at " + selectedTheater + " on " + dayFormatted + ", time: " + startTime + ", showtimeId: " + showtimeId);
            } else {
                System.out.println("No showtime found for " + movieName + " at " + selectedTheater + " on " + dayFormatted + ", time: " + startTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}