import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;


public class BitViewer {

    private byte[] fileBytes = new byte[1];

    public class BitsPanel extends JPanel {

        final Map<Integer, Integer> maskMap = Map.of(0, 0x01, 1, 0x02, 2, 0x04, 3, 0x08,
                4, 0x10, 5, 0x20, 6, 0x40, 7, 0x80);
        final char[] hexArray = "0123456789ABCDEF".toCharArray();

        BitsPanel(byte[] data, DisplayType displayType) {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            int numCells;

            if (displayType == DisplayType.HEX) {
                // Dealing with nibbles
                numCells = data.length * 2;
            } else {
                // Dealing with bits
                numCells = data.length * 8;

            }

            int columns = 50;
            int rows = numCells / columns;
            int remainder = numCells % columns;

            int byteNumber = 0;
            int positionInByte = 0;

            if (remainder != 0) {
                rows++;
            }

            for (int r = 0; r < rows; r++) {
                gbc.gridy = r;
                for (int c = 0; c < columns; c++) {
                    if (displayType == DisplayType.HEX && c % 2 == 0) {
                        if (r == rows - 1 && remainder != 0 && c == remainder) {
                            break;
                        }
                        continue;
                    }

                    gbc.gridx = c;
                    boolean bit = false;
                    char nibbleOne = Character.MIN_VALUE;
                    char nibbleTwo = Character.MIN_VALUE;

                    if (r == rows - 1 && remainder != 0 && c == remainder) {
                        break;
                    } else {
                        byte currentByte = data[byteNumber];
                        if (displayType != DisplayType.HEX) {
                            bit = (currentByte & 0xFF & maskMap.get(positionInByte)) != 0;
                            if (positionInByte == 7) {
                                byteNumber++;
                                positionInByte = 0;
                            } else {
                                positionInByte++;
                            }
                        } else {
                            nibbleOne = hexArray[currentByte >>> 4];
                            nibbleTwo = hexArray[currentByte & 0x0F];
                            byteNumber++;
                        }
                    }

                    switch (displayType) {
                        case ASCII:
                            JLabel asciiBitLabel = new JLabel();
                            asciiBitLabel.setFont(new Font("Courier", Font.PLAIN, 16));

                            if (bit) {
                                asciiBitLabel.setText("X");
                            } else {
                                asciiBitLabel.setText(".");
                            }

                            add(asciiBitLabel, gbc);
                            break;
                        case BINARY:
                            JLabel bitLabel = new JLabel();
                            bitLabel.setFont(new Font("Courier", Font.PLAIN, 16));

                            if (bit) {
                                bitLabel.setText("1");
                            } else {
                                bitLabel.setText("0");
                            }

                            add(bitLabel, gbc);
                            break;
                        case BLOCK:
                            Color colour = bit ? Color.black : Color.white;
                            TexturePane texturePane = new TexturePane(colour);
                            add(texturePane, gbc);
                            break;
                        case HEX:
                            JLabel nibbleOneLabel = new JLabel();
                            nibbleOneLabel.setFont(new Font("Courier", Font.PLAIN, 16));
                            nibbleOneLabel.setText(Character.toString(nibbleOne));
                            add(nibbleOneLabel, gbc);
                            JLabel nibbleTwoLabel = new JLabel();
                            nibbleTwoLabel.setFont(new Font("Courier", Font.PLAIN, 16));
                            nibbleTwoLabel.setText(Character.toString(nibbleTwo));
                            gbc.gridx = c + 1;
                            add(nibbleTwoLabel, gbc);
                            break;
                    }
                }
            }
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
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);

        final JPanel ButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonPanel.setLayout(new BorderLayout());

        JButton btnLoadFile = new JButton("LOAD FILE");
        btnLoadFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(frame);

            try {
                Component[] components = frame.getContentPane().getComponents();
                for (Component component : components) {
                    if (component instanceof JScrollPane) {
                        frame.remove(component);
                    }
                }

                File file = fileChooser.getSelectedFile();
                fileBytes = Files.readAllBytes(file.toPath());
                JScrollPane scrPane = new JScrollPane(new BitsPanel(fileBytes, DisplayType.ASCII));
                frame.getContentPane().add(scrPane);
                frame.revalidate();
                frame.repaint();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        ButtonPanel.add(btnLoadFile);

        JButton btnAsciiZoom = new JButton("X.");
        btnAsciiZoom.addActionListener(generateActionListener(frame, DisplayType.ASCII));
        ButtonPanel.add(btnAsciiZoom);

        JButton btnBinary = new JButton("10");
        btnBinary.addActionListener(generateActionListener(frame, DisplayType.BINARY));
        ButtonPanel.add(btnBinary);

        JButton btnBlock = new JButton("BLOCK");
        btnBlock.addActionListener(generateActionListener(frame, DisplayType.BLOCK));
        ButtonPanel.add(btnBlock);

        JButton btnHex = new JButton("0x");
        btnHex.addActionListener(generateActionListener(frame, DisplayType.HEX));
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

        btnLoadFile.doClick();
    }

    private ActionListener generateActionListener(JFrame frame, DisplayType displayType) {
        return e -> {
            Component[] components = frame.getContentPane().getComponents();
            for (Component component : components) {
                if (component instanceof JScrollPane) {
                    frame.remove(component);
                }
            }

            JScrollPane scrPane = new JScrollPane(new BitsPanel(fileBytes, displayType));
            frame.getContentPane().add(scrPane);
            frame.revalidate();
            frame.repaint();
        };
    }

}