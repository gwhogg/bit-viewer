import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;


public class BitViewer {

    public class BitsPanel extends JPanel {

        BitsPanel(byte[] data, DisplayType displayType) {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            int rows = 50;
            int columns = 75;

            for (int r = 0; r < rows; r++) {
                gbc.gridy = r;
                for (int c = 0; c < columns; c++) {
                    gbc.gridx = c;

                    switch (displayType) {
                        case ASCII:

                            break;
                        case BINARY:
                            break;
                        case HEX:
                            break;
                    }
                    Color colour;
                    JLabel bitLabel = new JLabel();
                    bitLabel.setFont(new Font("Courier", Font.PLAIN, 16));

                    if ((c % 2 == 1 && r % 2 == 1)
                            || (c % 2 == 0 && r % 2 == 0)) {
                        bitLabel.setText("0");
                    } else {
                        bitLabel.setText("1");
                    }

                    add(bitLabel, gbc);
                    //add(new TexturePane(colour), gbc);
                }
            }

            //setPreferredSize(new Dimension(500, 500));
            //setBackground(Color.black);
        }

        public class TexturePane extends JPanel {

            TexturePane(Color background) {
                setBackground(background);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(10, 10);
            }

        }
    }

    public static void main(String[] args) {
        new BitViewer();
    }

    private BitViewer() {
        JFrame frame = new JFrame("Bit Viewer");
        JScrollPane scrPane = new JScrollPane(new BitsPanel(new byte[2], DisplayType.ASCII));
        frame.getContentPane().add(scrPane);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);

        final JPanel ButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonPanel.setLayout(new BorderLayout());
        JButton btnAsciiZoom = new JButton("X.");
        ButtonPanel.add(btnAsciiZoom);
        JButton btnBinary = new JButton("10");
        ButtonPanel.add(btnBinary);
        JButton btnHex = new JButton("0x");
        ButtonPanel.add(btnHex);

        Border LineBorder = new LineBorder(Color.lightGray);
        ButtonPanel.setBorder(LineBorder);
        BoxLayout horizontal = new BoxLayout(ButtonPanel, BoxLayout.X_AXIS);
        ButtonPanel.setLayout(horizontal);
        FlowLayout flow = new FlowLayout();
        ButtonPanel.setLayout(flow);
        frame.add(ButtonPanel, BorderLayout.SOUTH);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

}