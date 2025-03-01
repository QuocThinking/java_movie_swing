package movieticket;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame{
    public MainFrame(){
        setTitle("Hệ thống Đặt Vé Xem Phim");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,400);
        setLayout(new BorderLayout());

        MovieSelectionPanel mvPanel = new MovieSelectionPanel();
        SeatPanel seatPanel = new SeatPanel();

        getContentPane().add(mvPanel, BorderLayout.NORTH);
        getContentPane().add(seatPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);

    }
}
