package an;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Progress extends JFrame {
     // private final JProgressBar progressBar;
     // private final JLabel statusLabel;
      public Progress() {
            //this.setTitle("Loading Stable");
            this.setSize(860, 576);
            this.setAlwaysOnTop(true);
            this.setLayout(new BorderLayout());
            this.setLocationRelativeTo(null);
            this.setUndecorated(true);
            final ImageIcon backgroundImage = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/assets/minecraft/Pride/szy.png")));
            final JLabel backgroundLabel = new JLabel(backgroundImage);
            this.add(backgroundLabel, "Center");
         /*   this.progressBar = new JProgressBar(0, 100);
            this.statusLabel = new JLabel("Loading...");
            this.progressBar.setForeground(new Color(60, 255, 67));
            this.progressBar.setSize(10000,40);
            this.progressBar.setStringPainted(true);
            final JPanel bottomPanel = new JPanel(new FlowLayout(1));
            bottomPanel.add(this.progressBar);
            bottomPanel.add(this.statusLabel);*/
          //  this.add(bottomPanel, "South");
            this.setVisible(true);
            for (int i = 0; i <= 100; i++) {
                  try {
                        Thread.sleep(20);
                  } catch (InterruptedException e) {
                        e.printStackTrace();
                  }

            }
            this.setVisible(false);


      }


}
