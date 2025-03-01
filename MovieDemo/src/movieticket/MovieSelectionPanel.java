package movieticket;

import javax.swing.*;
import java.awt.*;

public class MovieSelectionPanel extends JPanel {


    public MovieSelectionPanel(){
        setLayout(new FlowLayout());
        JLabel movieLabel = new JLabel("Chọn phim: ");
        String[] movies = {
                "Avengers", "Titanic","Harry Potter","Avatar"
        };
        JComboBox<String> movieComboBox = new JComboBox<>(movies);

        add(movieLabel);
        add(movieComboBox);
    }
}
