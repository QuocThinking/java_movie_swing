package movieticket;

import movieticket.view.MovieListPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame{
    public MainFrame(){
        setTitle("Hệ thống Đặt Vé Xem Phim");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,400);
//        setLayout(new BorderLayout());
//
//        SeatPanel seatPanel = new SeatPanel();
//        MovieSelectionPanel mvPanel = new MovieSelectionPanel(seatPanel);
//
//
//        getContentPane().add(mvPanel, BorderLayout.NORTH);
//        getContentPane().add(seatPanel, BorderLayout.CENTER);
//
//        setLocationRelativeTo(null);

        MovieListPanel movieListPanel = new MovieListPanel();
        add(movieListPanel);
        setLocationRelativeTo(null);

    }
}
