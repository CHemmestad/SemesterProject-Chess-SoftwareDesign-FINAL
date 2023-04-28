import java.awt.*;
import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.event.*;
import javax.swing.event.MouseInputListener;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ChessGame {
    public static Piece selectedPiece;
    public static String cheatPieceType;
    public static Boolean godMode = false;
    public static Boolean delete = false;
    public static Boolean turnsOn = true;
    public static int boardSelection = 0;
    public static Image[] gameStatus = new Image[2];
    public static Menu menu = new Menu();
    public static boolean play = true;

    public static void main(String[] Args) {
        sound("Start", .03f); // Playing the sound at the beginning of the game
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Getting the dimensions of the screen
        // Adjusting the size of the board based on the size of the screen
        int borderDecoration = ((int)screenSize.getHeight()-(int)screenSize.getHeight()%10)/15;
        int boardHeight = (int)screenSize.getHeight()-(int)screenSize.getHeight()%10;
        int boardWidth = (int)screenSize.getWidth()-(int)screenSize.getWidth()%10;
        int sideX = boardHeight-boardHeight/4;
        int sideY = sideX;
        int pieceSize = sideX/8;
        int boardSize = sideX+borderDecoration*2;

        // Loading the images for when black or white wins
        try {
            gameStatus[0] = ImageIO.read(new File("Images/WhiteWins.png")).getScaledInstance(sideX, sideY, Image.SCALE_SMOOTH);
            gameStatus[1] = ImageIO.read(new File("Images/BlackWins.png")).getScaledInstance(sideX, sideY, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Making the frame for the game
        JFrame frame = new JFrame();
        frame.setBounds(boardWidth/2-(boardSize)/2, boardHeight/2-(boardSize)/2, boardSize, boardSize);
        frame.setUndecorated(true);

        // Making the board to play in and adding all the pieces
        Board chessBoard = new Board(pieceSize, boardSize);

        // Adding white pieces
        for(int x = 0; x < 8; x++) {
            chessBoard.addPiece(new Pawn("pawn", "Images/PawnWnew.png", true, pieceSize), x, 6, true);
        }
        chessBoard.addPiece(new Rook("rook", "Images/RookWnew.png", true, pieceSize), 0, 7, true);
        chessBoard.addPiece(new Rook("rook", "Images/RookWnew.png", true, pieceSize), 7, 7, true);
        chessBoard.addPiece(new Knight("knight", "Images/KnightWnew.png", true, pieceSize), 1, 7, true);
        chessBoard.addPiece(new Knight("knight", "Images/KnightWnew.png", true, pieceSize), 6, 7, true);
        chessBoard.addPiece(new Bishop("bishop", "Images/BishopWnew.png", true, pieceSize), 2, 7, true);
        chessBoard.addPiece(new Bishop("bishop", "Images/BishopWnew.png", true, pieceSize), 5, 7, true);
        chessBoard.addPiece(new King("king", "Images/KingWnew.png", true, pieceSize), 3, 7, true);
        chessBoard.addPiece(new Queen("queen", "Images/QueenWnew.png", true, pieceSize), 4, 7, true);

        // Adding black pieces
        for(int x = 0; x < 8; x++) {
            chessBoard.addPiece(new Pawn("pawn", "Images/PawnBnew.png", false, pieceSize), x, 1, true);
        }
        chessBoard.addPiece(new Rook("rook", "Images/RookBnew.png", false, pieceSize), 0, 0, true);
        chessBoard.addPiece(new Rook("rook", "Images/RookBnew.png", false, pieceSize), 7, 0, true);
        chessBoard.addPiece(new Knight("knight", "Images/KnightBnew.png", false, pieceSize), 1, 0, true);
        chessBoard.addPiece(new Knight("knight", "Images/KnightBnew.png", false, pieceSize), 6, 0, true);
        chessBoard.addPiece(new Bishop("bishop", "Images/BishopBnew.png", false, pieceSize), 2, 0, true);
        chessBoard.addPiece(new Bishop("bishop", "Images/BishopBnew.png", false, pieceSize), 5, 0, true);
        chessBoard.addPiece(new King("king", "Images/KingBnew.png", false, pieceSize), 3, 0, true);
        chessBoard.addPiece(new Queen("queen", "Images/QueenBnew.png", false, pieceSize), 4, 0, true);

        // Creating a panel with graphics for repainting and adding it to the frame
        JPanel panel = new JPanel()
        {
            @Override
            public void paint(Graphics g) {
                boolean white = true;
                g.drawImage(chessBoard.chessBoards[boardSelection], 0, 0, this); // Drawing the board selected
                if(godMode) {
                    g.drawImage(chessBoard.chessBoards[9], 0, 0, this); // Adding flame layer for god mode
                }
                for ( int y = 0 ; y < 8 ; y++ ){
                    for ( int x = 0 ; x < 8 ; x++ ){
                        Piece current = chessBoard.getPiece(x, y);
                        if (white) {
                            //HSB COLOR PEN https://codepen.io/HunorMarton/full/eWvewo
                            g.drawImage(chessBoard.moveColors[4], borderDecoration+x*pieceSize, borderDecoration+y*pieceSize, this); // Adding the white little squares
                        } else {
                            g.drawImage(chessBoard.moveColors[3], borderDecoration+x*pieceSize, borderDecoration+y*pieceSize, this); // Adding the black little squares
                        }
                        white =! white;
                        if(selectedPiece != null) {
                            if(selectedPiece.validMoves[x][y] == -1) {
                                g.drawImage(chessBoard.moveColors[0], borderDecoration+x*pieceSize, borderDecoration+y*pieceSize, this); // Adds blue highlight
                            }
                            if(selectedPiece.validMoves[x][y] == 2) {
                                g.drawImage(chessBoard.moveColors[1], borderDecoration+x*pieceSize, borderDecoration+y*pieceSize, this); // Adds red highlight
                            }
                        }
                        if(current != null) {
                            if(chessBoard.isPlayerChecked()) {
                                if(current.getPieceType() == 5) {
                                    if(((King)current).isChecked()) {
                                        g.drawImage(current.getImage()[0], borderDecoration+current.getPosX()*pieceSize, borderDecoration+current.getPosY()*pieceSize, this); // Drawing the piece image
                                        g.drawImage(chessBoard.moveColors[2], borderDecoration+(x*pieceSize), borderDecoration+(y*pieceSize), this); // Adding checked if checked
                                    } else {
                                        g.drawImage(current.getImage()[0], borderDecoration+current.getPosX()*pieceSize, borderDecoration+current.getPosY()*pieceSize, this);
                                    }
                                } else {
                                    if(chessBoard.blockingPieces.contains(current)) {
                                        g.drawImage(chessBoard.moveColors[5], borderDecoration+(x*pieceSize), borderDecoration+(y*pieceSize), this); // Adding green border
                                    }
                                    g.drawImage(current.getImage()[0], borderDecoration+current.getPosX()*pieceSize, borderDecoration+current.getPosY()*pieceSize, this);
                                }
                            } else {
                                g.drawImage(current.getImage()[0], borderDecoration+current.getPosX()*pieceSize, borderDecoration+current.getPosY()*pieceSize, this); // Drawing the piece image
                            }
                        }
                    }
                    white =! white; 
                }
                if(chessBoard.isGameOver()) {
                    // Drawing which color won or lose if game is over
                    if(chessBoard.getWinner() == "white") {
                        g.drawImage(gameStatus[0], borderDecoration, borderDecoration, this);
                    } else {
                        g.drawImage(gameStatus[1], borderDecoration, borderDecoration, this);
                    }
                }
            }
        };
        frame.add(panel); // Adding the panel to the frame

        // Adding keylistener for if a key is pressed(used for menu, cheats, quitting, and computer move)
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Checking for the eneter key pressed
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Adding a new frame with a textinput for typing in cheat codes
                    JFrame userInput = new JFrame();
                    userInput.setUndecorated(true);
                    JPanel userPanel = new JPanel();
                    JLabel label = new JLabel("Enter cheat code :");
                    userPanel.add(label);
                    JTextField textField = new JTextField(20);
                    final JFrame finalUserInput = userInput;
                    textField.addKeyListener(new KeyAdapter() {
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_ENTER) { // Adding another listener to hide frame when enter is pushed again
                                finalUserInput.setVisible(false);
                                String input = textField.getText();
                                cheatCodes(input, chessBoard, pieceSize);
                                finalUserInput.dispose();
                                frame.repaint();
                            }
                        }
                    });
                    userPanel.add(textField);
                    userInput.add(userPanel);
                    userInput.pack();
                    userInput.setVisible(true);
                    userInput.setLocationRelativeTo(null);
                    userInput = null;
                // Checking for the M key pressed for bringing up the board options
                } else if(e.getKeyCode() == KeyEvent.VK_M) {
                    sound("Menu", .05f); // Playing sound for opening menu
                    // Menu was created at the beginning of program
                    menu.addKeyListener(new KeyAdapter() {
                        // Adding another key listener for if M or Enter is pressed for closing the menu
                        public void keyPressed(KeyEvent e) {
                            if ((e.getKeyCode() == KeyEvent.VK_M) || (e.getKeyCode() == KeyEvent.VK_ENTER)) {
                                menu.setVisible(false);
                                boardSelection = menu.getSelectedBoard(); // Changing the selected board style
                                frame.repaint();
                            }
                        }
                    });
                    menu.setVisible(true);
                // Checking for the Space bar pressed for computer move
                } else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if(turnsOn && !chessBoard.isGameOver()){
                        selectedPiece = null;
                        chessBoard.checkGameStatus();
                        chessBoard.computerMove(chessBoard.getTurn()); // Computer makes a move if everything is good
                        chessBoard.checkGameStatus();
                        if(chessBoard.isPlayerChecked() && (play) && (!chessBoard.isGameOver())) {
                            sound("Check", .025f);
                            play = false;
                        } else if(!chessBoard.isPlayerChecked()) {
                            play = true;
                        }
                        frame.repaint();
                    }
                // Checking for if the Escape key pressed for ending the game
                } else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    sound("Close", .05f);
                    try {
                        Thread.sleep(2000); // Sleeps so the sound has time to play
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.exit(0); // Quits the game
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        // Adding a mouse listener so that the player can use the mouse to click and move pieces on the board
        frame.addMouseListener(new MouseInputListener() {
            @Override
            // Checking to see if the mouse is clicked
            public void mouseClicked(MouseEvent e) {
                if(!chessBoard.isGameOver()) {
                    frame.repaint();
                    // Getting the location on the screen where the mouse was clicked
                    int xPos = (e.getX()-(borderDecoration))/pieceSize; // Dividing by piece size to use in 8x8 grid
                    int yPos = (e.getY()-(borderDecoration))/pieceSize;
                    if(delete) {
                        chessBoard.removePiece(xPos, yPos); // Removes piece clicked on if delete is true from cheat code
                    }
                    // Moving the piece to a new spot if the there is already a piece selected
                    if(selectedPiece != null) {
                        if(selectedPiece.validMoves[xPos][yPos] == -1) {
                            chessBoard.movePiece(selectedPiece, xPos, yPos);
                        } else if(selectedPiece.validMoves[xPos][yPos] == 2) {
                            sound("Kill", .03f);
                            chessBoard.killPiece(selectedPiece, xPos, yPos);
                        }
                    } else if((chessBoard.getPiece(xPos, yPos) == null) && (godMode)) {
                        if(cheatPieceType != null) {
                            // Adding pieces if god mode is activated
                            chessBoard.addPiece(PieceFactory.createPiece(cheatPieceType, chessBoard.getTurn(), pieceSize), xPos, yPos, true);
                        }
                    }
                    // Getting a new selected piece if the spot clicked on has a piece
                    if(chessBoard.getPiece(xPos, yPos) != null) {
                        if((!turnsOn) || (chessBoard.getPiece(xPos, yPos).isWhite == chessBoard.getTurn())) {
                            if(selectedPiece != chessBoard.getPiece(xPos, yPos)) {
                                sound("Click", .03f);
                                selectedPiece = chessBoard.getPiece(xPos, yPos);
                                if(selectedPiece.getPieceType() == 5) {
                                    chessBoard.addKings(selectedPiece);
                                } else {
                                    selectedPiece.moveCheck(xPos, yPos, chessBoard);
                                }
                            } else {
                                selectedPiece = null;
                            }
                        } else {
                            selectedPiece = null;
                        }
                    } else {
                        selectedPiece = null;
                    }
                }
                chessBoard.checkGameStatus(); // Checking game status for check, gameover, and new moves after the piece is moved
                if(chessBoard.isPlayerChecked() && (play) && (!chessBoard.isGameOver())) {
                    sound("Check", .025f);
                    play = false;
                } else if(!chessBoard.isPlayerChecked()) {
                    play = true;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("mouse pressed -----------------------------------------------");
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

            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
            
        });
        frame.setDefaultCloseOperation(0);
        frame.setVisible(true); // Setting the frame to be visible so the player can see the game
        // Pausing for little bit so that the intro music has time to finish playing before playing the background music
        try {
            Thread.sleep(4000); // Sleeping for 4 seconds
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        sound("Oceans", .01f); // Playing background noise options are (Oceans, Walk, and Tempest)
    }

    // Precondition : Need a way to change some of the game settings if a cheat code is entered
    // Postcondition : Takes the string and changes the game setting if the string is valid else nothing happens
    private static void cheatCodes(String input, Board chessBoard, int pieceSize) {
        switch (input) {
            case "queen" : // Adding queen if godmode is on
                cheatPieceType = input;
                delete = false;
                break;
            case "rook" : // Adding rook if godmode is on
                cheatPieceType = input;
                delete = false;
                break;
            case "bishop" : // Adding bishop if godmode is on
                cheatPieceType = input;
                delete = false;
                break;
            case "knight" : // Adding knight if godmode is on
                cheatPieceType = input;
                delete = false;
                break;
            case "pawn" : // Adding pawn if godmode is on
                cheatPieceType = input;
                delete = false;
                break;
            case "nextturn" : // Changing to next players turn
                chessBoard.changeTurn();
                break;
            case "reset" : // Resets all the pieces to their original spots
                reset(chessBoard, pieceSize);
                chessBoard.turn = true;
                chessBoard.gameOver = false;
                cheatPieceType = null;
                delete = false;
                break;
            case "removeblacks" : // Removes all the black pieces except for the king
                if(godMode) {
                    remove(chessBoard, pieceSize, false, false);
                } else {
                    System.out.println("Godmode must be activated first");
                }
                break;
            case "removewhites" : // Removes all the white pieces except for the king
                if(godMode) {
                    remove(chessBoard, pieceSize, true, false);
                } else {
                    System.out.println("Godmode must be activated first");
                }
                break;
            case "removeall" : // Removes all the pieces except for the king
                if(godMode) {
                    remove(chessBoard, pieceSize, true, true);
                } else {
                    System.out.println("Godmode must be activated first");
                }
                break;
            case "godmodeon" : // Activates godmode for doing extra stuff
                sound("Activated", .03f);
                godMode = true;
                delete = false;
                cheatPieceType = null;
                break;
            case "godmodeoff" : // Turns of godmode so you dont accidently add or delete pieces while trying to play
                sound("Deactivated", .03f);
                turnsOn = true;
                godMode = false;
                delete = false;
                cheatPieceType = null;
                break;
            case "turnson" : // Turns on the turn based plays if off
                turnsOn = true;
                break;
            case "turnsoff" : // Turns off turn based plays
                if(godMode) {
                    turnsOn = false;
                } else {
                    System.out.println("Godmode must be activated first");
                }
                break;
            case "deleteon" : // Turns on delete mode to delete pieces
                if(godMode) {
                    delete = true;
                    cheatPieceType = null;
                } else {
                    System.out.println("Godmode must be activated first");
                }
                break;
            case "deleteoff" : // Turns off delete mode
                delete = false;
                cheatPieceType = null;
                break;
            case "please" : // THE MAGIC WORD!
                chessBoard.notSus = false;
                break;
            default :
                System.out.println("Did nothing");
                break;
        }
    }

    // Precondition : I want to be able to easily add sounds in the game anywhere
    // Postcondition : Simple function for playing a sound and adjusting the volume (I think .5 is normal volume less than that is quieter)
    public static void sound(String file, float volume_) {
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

    // Precondition : Need to ba able to reset the board when the game is over or when the player wants to
    // Postcondition : Goes throught the board and adds new pieces to their original positions
    private static void reset(Board chessBoard, int pieceSize) {
        for(int y = 2; y < 6; y++) {
            for(int x = 0; x < 8; x++) {
                if(chessBoard.getPiece(x, y) != null) {
                    chessBoard.removePiece(x, y);
                }
            }
        }
        // Same as above
        // Adding white pieces
        for(int x = 0; x < 8; x++) {
            chessBoard.addPiece(new Pawn("pawn", "Images/PawnWnew.png", true, pieceSize), x, 6, true);
        }
        chessBoard.addPiece(new Rook("rook", "Images/RookWnew.png", true, pieceSize), 0, 7, true);
        chessBoard.addPiece(new Rook("rook", "Images/RookWnew.png", true, pieceSize), 7, 7, true);
        chessBoard.addPiece(new Knight("knight", "Images/KnightWnew.png", true, pieceSize), 1, 7, true);
        chessBoard.addPiece(new Knight("knight", "Images/KnightWnew.png", true, pieceSize), 6, 7, true);
        chessBoard.addPiece(new Bishop("bishop", "Images/BishopWnew.png", true, pieceSize), 2, 7, true);
        chessBoard.addPiece(new Bishop("bishop", "Images/BishopWnew.png", true, pieceSize), 5, 7, true);
        chessBoard.addPiece(new King("king", "Images/KingWnew.png", true, pieceSize), 3, 7, true);
        chessBoard.addPiece(new Queen("queen", "Images/QueenWnew.png", true, pieceSize), 4, 7, true);

        // Adding black pieces
        for(int x = 0; x < 8; x++) {
            chessBoard.addPiece(new Pawn("pawn", "Images/PawnBnew.png", false, pieceSize), x, 1, true);
        }
        chessBoard.addPiece(new Rook("rook", "Images/RookBnew.png", false, pieceSize), 0, 0, true);
        chessBoard.addPiece(new Rook("rook", "Images/RookBnew.png", false, pieceSize), 7, 0, true);
        chessBoard.addPiece(new Knight("knight", "Images/KnightBnew.png", false, pieceSize), 1, 0, true);
        chessBoard.addPiece(new Knight("knight", "Images/KnightBnew.png", false, pieceSize), 6, 0, true);
        chessBoard.addPiece(new Bishop("bishop", "Images/BishopBnew.png", false, pieceSize), 2, 0, true);
        chessBoard.addPiece(new Bishop("bishop", "Images/BishopBnew.png", false, pieceSize), 5, 0, true);
        chessBoard.addPiece(new King("king", "Images/KingBnew.png", false, pieceSize), 3, 0, true);
        chessBoard.addPiece(new Queen("queen", "Images/QueenBnew.png", false, pieceSize), 4, 0, true);
    }

    // Precondition : Want to be able to remove certain colors or all the pieces
    // Postcondition : Removes pieces based on color or all the pieces
    private static void remove(Board chessBoard, int pieceSize, Boolean isWhite, Boolean all) {
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                if(chessBoard.getPiece(x, y) != null) {
                    if(!all) {
                        if((chessBoard.getPiece(x, y).isWhite == isWhite) && (chessBoard.getPiece(x, y).getPieceType() != 5)) {
                            chessBoard.removePiece(x, y);
                        }
                    } else {
                        if((chessBoard.getPiece(x, y).getPieceType() != 5)) {
                            chessBoard.removePiece(x, y);
                        }
                    }
                }
            }
        }
    }
}