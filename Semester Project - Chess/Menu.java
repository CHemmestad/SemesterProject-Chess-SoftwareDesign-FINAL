import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame { // Extends JFrame to create a new frame when a new menu is made
    private int selectedBoard;
    
    public Menu() {
        //setTitle("Pick a chess board");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 3)); // Creating a panel for the menu
        panel.setPreferredSize(new Dimension(600, 600));

        // MenuItem extends JLabel so they can be added to the panel
        panel.add(new MenuItem("Images/DefaultChessBoard.png", this).getLabel());
        panel.add(new MenuItem("Images/BlackChessBoard.png", this).getLabel());
        panel.add(new MenuItem("Images/WoodChessBoard.png", this).getLabel());
        panel.add(new MenuItem("Images/BrushedMetalChessBoard.png", this).getLabel());
        panel.add(new MenuItem("Images/MarbleChessBoard.png", this).getLabel());
        panel.add(new MenuItem("Images/SpaceChessBoard.png", this).getLabel());
        panel.add(new MenuItem("Images/AIChessBoard.png", this).getLabel());
        panel.add(new MenuItem("Images/CodeChessBoard.png", this).getLabel());
        panel.add(new MenuItem("Images/FavoriteMovieChessBoard.png", this).getLabel());
        
        this.add(panel); // Adding panel to the frame
        pack(); // Making it as small as possible while still being able to fit all the images
        setLocationRelativeTo(null);
    }

    // Precondition : Need to be able to change the selected board type based on what the player clicked on
    // Postcondition : Changes the board type based on what the player clicked on in the MenuItem class
    public void setSelectedBoard(int boardType) {
        selectedBoard = boardType;
    }

    // Precondition : Need to know what the selected board type is 
    // Postcondition : Gets the selected board type for drawing the right board in the game
    public int getSelectedBoard() {
        return selectedBoard;
    }
}
