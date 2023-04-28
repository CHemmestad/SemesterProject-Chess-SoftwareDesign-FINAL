import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MenuItem extends JLabel implements MouseListener {
    private int type;
    private String imageName;
    private JLabel label;
    private Menu menu;

    public MenuItem(String imageName, Menu menu) {
        this.menu = menu;
        getImageType(imageName);
        this.imageName = imageName;
        // Getting the image and scaling it so it fits the size of the menu
        ImageIcon image1 = new ImageIcon(imageName);
        Image image = image1.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon scaled = new ImageIcon(image);
        this.label = new JLabel(scaled);
        this.label.addMouseListener(this);
    }
    
    public JLabel getLabel() { // Used in the Menu class
        return label;
    }

    // Precondition : Need something simple for the board type that isn't a long string
    // Postcondition : Represents each board type with a number based on the string
    private void getImageType(String imageName) {
        switch (imageName) {
            case "Images/DefaultChessBoard.png" :
                type = 0;
                break;
            case "Images/BlackChessBoard.png" :
                type = 1;
                break;
            case "Images/WoodChessBoard.png" :
                type = 2;
                break;
            case "Images/BrushedMetalChessBoard.png" :
                type = 3;
                break;
            case "Images/MarbleChessBoard.png" :
                type = 4;
                break;
            case "Images/SpaceChessBoard.png" :
                type = 5;
                break;
            case "Images/AIChessBoard.png" :
                type = 6;
                break;
            case "Images/CodeChessBoard.png" :
                type = 7;
                break;
            case "Images/FavoriteMovieChessBoard.png" :
                type = 8;
                break;
            default :
                type = 0;
        }
    }

    @Override
    // Adding a mouse listener so that when the player clicks on a board it changes the selected board to that
    public void mouseClicked(MouseEvent e) {
        sound("Click", .03f); // Making the clicking sound for the menu when a board is clicked on
        menu.setSelectedBoard(type);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // Precondition : Want to be able to play sound
    // Postcondition : Plays a sound
    public void sound(String file, float volume_) {
        File audioFile = new File("Sounds/"+file+".wav");
        final AudioInputStream[] audioStream = {null};
        try {
            audioStream[0] = AudioSystem.getAudioInputStream(audioFile);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        LineListener listener = new LineListener() {
            public void update(LineEvent event) {
                if (event.getType() == LineEvent.Type.STOP) {
                    event.getLine().close();
                    try {
                        audioStream[0].close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        clip.addLineListener(listener);
    
        try {
            clip.open(audioStream[0]);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = volume_;
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clip.start();
    }
}
