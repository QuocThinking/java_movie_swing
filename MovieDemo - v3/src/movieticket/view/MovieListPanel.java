package movieticket.view;

import movieticket.config.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieListPanel extends JPanel {
    private JList<String> movieList;
    private JTextArea movieInfoArea;
    private JButton buyTicketButton;

    private static final Logger LOGGER = Logger.getLogger(MovieListPanel.class.getName());

    public MovieListPanel(){
        setLayout(new BorderLayout());
        setBackground(new Color(245,245,245));

        JLabel mainTitle = new JLabel("Hệ thống đặt vé xem phim", SwingConstants.CENTER);
        mainTitle.setFont(new Font("Arial", Font.BOLD, 20));
        mainTitle.setForeground(new Color(0, 102, 204));
        add(mainTitle, BorderLayout.NORTH);

        // Panel chứa 2 ô
        JPanel contentPanel = new JPanel(new GridLayout(1,2,10,0));
        contentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204)),
                "Chọn phim",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14)
        ));
        contentPanel.setBackground(new Color(245,245,245));

        // Bên trái tên phim
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Danh sách phim"));
        movieList = new JList<>(loadMovies());
        movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieList.addListSelectionListener(e -> updateMovieInfo());
        JScrollPane lisJScrollPane = new JScrollPane(movieList);
        lisJScrollPane.setPreferredSize(new Dimension(200,300));
        listPanel.add(lisJScrollPane, BorderLayout.CENTER);
        contentPanel.add(listPanel);

        // BÊn phải thông tin
        JPanel infoJPanel = new JPanel(new BorderLayout());
        infoJPanel.setBorder(BorderFactory.createTitledBorder("Thông tin phim"));
        movieInfoArea  = new JTextArea(10,20);
        movieInfoArea.setEditable(false);
        movieInfoArea.setLineWrap(true);
        movieInfoArea.setWrapStyleWord(true);
        movieInfoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane infoScrollPane = new JScrollPane(movieInfoArea);
        infoJPanel.add(infoScrollPane,BorderLayout.CENTER);
        contentPanel.add(infoJPanel);

        //Nút mua vé
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buyTicketButton = new JButton("Mua vé");
        buyTicketButton.setEnabled(false);
        buyTicketButton.setBackground(new Color(0,102,204));
        buyTicketButton.setForeground(Color.WHITE);
        buyTicketButton.setPreferredSize(new Dimension(100,40));
        buyTicketButton.addActionListener(e -> openTicketBookingWindow());
        buttonPanel.add(buyTicketButton);

        add(contentPanel,BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }

    private String[] loadMovies(){
        List<String> movies = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()){
            String query = "SELECT movie_name FROM movies";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                movies.add(rs.getString("movie_name"));
            }
        }catch (SQLException e){
//            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Lỗi tài danh sách phim", e);
        }

        return movies.toArray(new String[0]);

    }

    private void updateMovieInfo(){
        String selectedMovie = movieList.getSelectedValue();
        if(selectedMovie != null){
            try (Connection conn = DatabaseConnection.getConnection()){
                String query = "SELECT movie_name, duration, genre, age_rating, director" +
                        " FROM movies WHERE movie_name = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1,selectedMovie);
                ResultSet rs = stmt.executeQuery();

                if(rs.next()){
                    String info = "Tên phim: " + rs.getString("movie_name") + "\n"
                            + "Thời lượng: " + rs.getInt("duration") + "\n"
                            + "Thể loại: " + rs.getString("genre") + "\n"
                            + "Độ tuổi: " + rs.getString("age_rating") + "\n"
                            + "Đạo diễn: " + rs.getString("director");
                    movieInfoArea.setText(info);
                    buyTicketButton.setEnabled(true);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

    }

    private void openTicketBookingWindow(){
        String selectedMovie = movieList.getSelectedValue();
        if(selectedMovie != null){
            JFrame bookingFrame = new JFrame("Thông tin phim và vé: " + selectedMovie);
            bookingFrame.setSize(600,400);
            bookingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            bookingFrame.add(new TicketBookingPanel(selectedMovie));
            bookingFrame.setVisible(true);
        }
    }
}
