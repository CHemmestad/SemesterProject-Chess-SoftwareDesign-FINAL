import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import javax.sound.sampled.*;
import javax.imageio.ImageIO;

public class Board {
    public Boolean turn = true;
    public int count = 0;
    public Boolean notSus = true;
    public boolean gameOver = false;
    public Image[] moveColors = new Image[6];
    public Image[] chessBoards = new Image[10];
    protected LinkedList<Piece> allPieces = new LinkedList<>();
    protected LinkedList<Piece> blockingPieces = new LinkedList<>();
    protected LinkedList<Piece> tempKings = new LinkedList<>();
    private Piece piece[][] = new Piece[8][8];
    private String winner;
    private boolean playerInCheck = false;

    public Board(int pieceSize, int boardSize) {
        // Adding all the images the board will need
        try {
            this.chessBoards[0] = ImageIO.read(new File("Images/DefaultChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[1] = ImageIO.read(new File("Images/BlackChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[2] = ImageIO.read(new File("Images/WoodChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[3] = ImageIO.read(new File("Images/BrushedMetalChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[4] = ImageIO.read(new File("Images/MarbleChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[5] = ImageIO.read(new File("Images/SpaceChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[6] = ImageIO.read(new File("Images/AIChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[7] = ImageIO.read(new File("Images/CodeChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[8] = ImageIO.read(new File("Images/FavoriteMovieChessBoard.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            this.chessBoards[9] = ImageIO.read(new File("Images/GodMode.png")).getScaledInstance(boardSize, boardSize, Image.SCALE_SMOOTH);
            
            this.moveColors[0] = ImageIO.read(new File("Images/HighlightedEmpty.png")).getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
            this.moveColors[1] = ImageIO.read(new File("Images/HighlightedKill.png")).getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
            this.moveColors[2] = ImageIO.read(new File("Images/Check.png")).getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
            this.moveColors[3] = ImageIO.read(new File("Images/BlackTileSpace.png")).getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
            this.moveColors[4] = ImageIO.read(new File("Images/WhiteTileSpace.png")).getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
            this.moveColors[5] = ImageIO.read(new File("Images/BlockablePieces.png")).getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean getTurn() {
        return turn;
    }

    public Piece getPiece(int xPosition, int yPosition) {
        return piece[xPosition][yPosition];
    }

    public String getWinner() {
        return winner;
    }

    public void changeTurn() {
        this.turn =! turn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isPlayerChecked() {
        return playerInCheck;
    }

    // Precondition : Need a way to move the piece to a new location
    // Postcondition : Moves the piece selected a new position on the board
    public void movePiece(Piece piece, int x, int y) { // Piece is the piece being moved and (x,y) is where it's being moved to
        piece.setFirstFalse(); // Sets the first move for the piece to false for pawn,rook,king etc.
        if(piece.getPieceType() == 5) { // Checking if the piece being moved is a king
            if(getPiece(x, y) != null) { // Checking the spot being moved to for a piece
                if((getPiece(x, y).getPieceType() == 2) && (getPiece(x, y).isWhite == piece.isWhite)) { // Checking if the piece at the spot being moved to is a rook
                    sound("Castle", .03f);
                    castleMove(piece, getPiece(x, y));
                    changeTurn();
                } else {
                    stupid(piece, x, y);
                }
            } else {
                stupid(piece, x, y);
            }
        } else {
            stupid(piece, x, y);
        }
        // Not worry about this part, it's not sus...
        count++;
        changeBoard(count, notSus);
    }

    // Precondition : Need a way to add pieces to the board in certain locations
    // Postcondition : Adds the pieces that want where you want them
    public void addPiece(Piece piece, int x, int y, boolean yes) {
        // Changing the x and y location stored in the piece
        piece.setPosX(x);
        piece.setPosY(y);
        this.piece[x][y] = piece; // Setting that spot on the board equal to the piece
        if(yes) {
            allPieces.add(piece); // Adding the piece to the LinkedList if you want it to
        }
    }

    // Precondition : Need a way to remove pieces from the board
    // Postcondition : Removes pieces from the board
    public void removePiece(int x, int y) {
        this.piece[x][y] = null; // Sets the location on the board to null
        allPieces.remove(getPiece(x, y)); // Removes the piece from the LinkedList
    }

    // Precondition : Need a way for killing pieces if possible
    // Postcondition : Kills a piece
    public void killPiece(Piece piece, int x, int y) {
        allPieces.remove(getPiece(x, y)); //Removes the piece from the board at that location
        movePiece(piece, x, y); // Then moves the selected piece to that location
    }

    // Precondition : Need a way to check the status of the game at certain points like after a player moves
    // Postcondition : Checks the status of the game looking for check, gameover, blocks, valid moves
    public void checkGameStatus() {
        Piece Wking = findWKing(); // Looking for kings to see if theyre still there
        Piece Bking = findBKing();
        if((Wking == null) || (Bking == null)) {
            gameOver(); // Game over if one of the kings are dead
            return;
        }
        switchPawn(); // Changes a pawn to queen if at the end of the board
        addKings(Bking); // Adding temp kings for checking the valid moves for the kings
        addKings(Wking);
        moveCheckAll(); // Checking all the pieces valid moves and adding available kills to a list
        kingChecked(null); // Checks the pieces kill lists for one of the kings and puts it in check if it is found
        if(((King)Wking).isChecked()) {
            checkForBlock((King)Wking); // Checking for pieces that can block an attack to king
        }
        if(((King)Bking).isChecked()) {
            checkForBlock((King)Bking);
        }
        if((((King)Wking).isChecked()) && (!((King)Wking).isMoveAvailable()) && (!((King)Wking).isBlockAvailable())) { // Checkmate
            // If the king is in check and there's nowhere the king can move to and there's no piece that can block the attack then the other player wins
            this.winner = "black";
            gameOver();
            return;
        }
        if((((King)Bking).isChecked()) && (!((King)Bking).isMoveAvailable()) && (!((King)Bking).isBlockAvailable())) {
            this.winner = "white";
            gameOver();
            return;
        }
    }

    // Precondition : Need a computer to move pieces for you
    // Postcondition : Makes a "computer" to move pieces for you using random numbers
    public void computerMove(Boolean turn) {
        Random rand  = new Random();
        Boolean go = true;
        int limit = 1000;
        while(go && (limit != 0)) {
            int x = rand.nextInt(8);
            int y = rand.nextInt(8);
            Piece piece = this.getPiece(x, y); // Finds a random piece on the board
            limit--;
            if(piece != null) {
                if(piece.isWhite == turn) { // Checks if its that pieces turn
                    for(int count = 0; count < 50; count++ ) {
                        int x2 = rand.nextInt(8); // Finds new random location on board
                        int y2 = rand.nextInt(8);
                        if((piece.validMoves[x2][y2] == -1) || (piece.validMoves[x2][y2] == 2)) { // Checks if random location is a valid move for the piece
                            go = false; // Breaks the loop if a valid move is found
                            movePiece(piece, x2, y2); // Moves the piece
                            return; // For good measure
                        }
                    }
                }
            }
        }
        if(limit == 0) { // If it can't find a valid move for any piece then it assumes the game is over
            if(turn) {
                this.winner = "black";
            } else {
                this.winner = "white";
            }
            gameOver();
        }
    }

    // Precondition : Need a way to check the available moves for the king
    // Postcondition : Places temp kings and checks to see if they are in check then adjusts the original kings moves
    public Piece addKings(Piece king) {
        king.moveCheck(king.getPosX(), king.getPosY(), this); // Checking for spots to put temp kings
        kingChecked(king);
        int[][] tempArray = new int[8][8];
        System.arraycopy(king.validMoves, 0, tempArray, 0, king.validMoves.length);
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                if(tempArray[x][y] != 0) {
                    Piece savePiece = getPiece(x, y); // Saving the piece that was there if there was one
                    Piece tempKing = new King("temp", "Images/HighlightedEmpty.png", king.isWhite, king.pieceSize); // Creating a new temp king
                    addPiece(tempKing, x, y, false); // Adding the temp king to the board
                    tempKings.add(tempKing); // Adding it to the LinkedList
                    moveCheckAll(); // Move checking all the pieces to see if the temp king is in check
                    kingChecked(tempKing); // Checking for check
                    if(((King)tempKing).isChecked()) {
                        king.validMoves[tempKing.getPosX()][tempKing.getPosY()] = 0; // Changing the available move for the original king to zero if the temp king is checked
                    }
                    piece[x][y] = savePiece; // Changing it back to the original piece at that location
                }
            }
        }
        tempKings.clear();
        return king;
    }

    // Precondition : Need to change the pawn to a queen if it gets to the other side of the board
    // Postcondition : Changes the pawn to a queen
    private void switchPawn() {
        for(Piece current : allPieces) {
            if(current.getPieceType() == 1) { // Checking if the piece is a pawn
                if((current.isWhite) && (current.getPosY() == 0)) { // Checking if its at the end of the board for the white pieces
                    // Adding the new queen and then checking if it puts the king in check
                    allPieces.remove(current);
                    Piece newPiece = new Queen("queen", "Images/QueenWnew.png", true, current.pieceSize);
                    addPiece(newPiece, current.getPosX(), current.getPosY(), true);
                    newPiece.moveCheck(newPiece.getPosX(), newPiece.getPosY(), this);
                    kingChecked(null);
                } else if((!current.isWhite) && (current.getPosY() == 7)) { // Checking if its at the end of the board for the black pieces
                    allPieces.remove(current);
                    Piece newPiece = new Queen("queen", "Images/QueenBnew.png", false, current.pieceSize);
                    addPiece(newPiece, current.getPosX(), current.getPosY(), true);
                    newPiece.moveCheck(newPiece.getPosX(), newPiece.getPosY(), this);
                    kingChecked(null);
                }
            }
        }
    }

    // Precondition : Need a way to see if their any pieces that can block an attack to the king
    // Postcondition : Looks for available pieces that can block if the king is in check
    private void checkForBlock(King king) {
        Boolean blockFound = false;
        Piece checkedByTemp = king.getCheckedBy(); // If the king is checked then it saves the piece that checked it so we only have to check the moves of one piece
        int saveX = checkedByTemp.getPosX();
        int saveY = checkedByTemp.getPosY();
        for(Piece current : allPieces) { // Searching all the pieces
            if(current.isWhite == king.isWhite) { // Looking for a piece that is the same color as the king thats checked
                for(int y = 0; y < 8; y++) {
                    for(int x = 0; x < 8; x++) {
                        if((current.validMoves[x][y] != 0) && (current != king)) {
                            // Looking at all the valid moves for that piece and then adding a pawn to see if the king is still in check by the checkedBy piece even after the pawn has been added to block it
                            Piece temp = new Pawn("temp", "Images/PawnWnew.png", king.isWhite, king.pieceSize);
                            Piece savePiece = getPiece(x, y);
                            removePiece(x, y);
                            addPiece(temp, x, y, false);
                            checkedByTemp.moveCheck(saveX, saveY, this);
                            kingChecked(null);
                            if(!king.isChecked()) {
                                blockingPieces.add(current);
                                current.validMoves[x][y] = current.validMoves[x][y];
                                blockFound = true;
                                king.setCheck(true);
                            } else {
                                current.validMoves[x][y] = 0; // Changing the valid move for that piece to 0 if the check isnt blocked at that position
                            }
                            removePiece(x, y);
                            if(savePiece != null) {
                                addPiece(savePiece, x, y, false);
                            }
                            temp = null;
                        }
                    }
                }
            }
        }
        if(blockFound) {
            king.setBlockAvailable(blockFound); // Changing bools used for checking the condition of the game
        } else {
            king.setBlockAvailable(blockFound);
        }
        checkedByTemp.moveCheck(saveX, saveY, this);
        kingChecked(null);
    }

    // Precondition : Need a way to see if the king is checked by a piece on the board
    // Postcondition : Checks if the king is checked by a piece
    private void kingChecked(Piece king) {
        boolean Wfound = false;
        boolean Bfound = false;
        Piece Wking = findWKing();
        Piece Bking = findBKing();
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                Piece current = this.piece[x][y];
                if(current != null) {
                    if(current.availableKills.contains(Wking)) { // Checking to see if the current piece has the king in their available kills list
                        Wfound = true;
                        playerInCheck = true;
                        // Changing conditions for that king and saving the checked by piece to the king
                        ((King)Wking).setCheck(true);
                        ((King)Wking).setCheckedBy(current);
                    }
                    if(current.availableKills.contains(Bking)) {
                        Bfound = true;
                        playerInCheck = true;
                        ((King)Bking).setCheck(true);
                        ((King)Bking).setCheckedBy(current);
                    }
                    // Used for checking the temp kings else if argument is null it only checks the original kings
                    if(current.availableKills.contains(king)) {
                        ((King)king).setCheck(true);
                    }
                }
            }
        }
        if((!Wfound) && (Wking != null)) {
            ((King)Wking).setCheck(false);
            ((King)Wking).setCheckedBy(null);
        }
        if((!Bfound) && (Bking != null)) {
            ((King)Bking).setCheck(false);
            ((King)Bking).setCheckedBy(null);
        }
        if(!Bfound && !Wfound) {
            playerInCheck = false;
        }
    }

    // Precondition : Need a way to move the piece in a castle fashion if a castling option is available
    // Postcondition : Switches the king and rook like you would expect when castling
    private void castleMove(Piece king, Piece rook) {
        int distance;
        if(rook.getPosX() > king.getPosX()) {
            distance = rook.getPosX() - king.getPosX() - 1;
        } else {
            distance = king.getPosX() - rook.getPosX() - 1;
        }
        int kingMove = distance - 1;
        int rookMove = distance - kingMove;
        if(king.getPosX()-rook.getPosX() > 0) {
            kingMove = kingMove*(-1);
        } else {
            rookMove = rookMove*(-1);
        }
        King temp = new King("king", king.imageName, king.isWhite, king.pieceSize);
        temp.setCastle(false);
        temp.setPosX(rook.getPosX()+rookMove);
        temp.setPosY(rook.getPosY());
        Rook temp2 = new Rook("rook", rook.imageName, rook.isWhite, rook.pieceSize);
        temp2.setPosX(king.getPosX()+kingMove);
        temp2.setPosY(king.getPosY());
        this.piece[temp.getPosX()][temp.getPosY()] = temp;
        this.piece[temp2.getPosX()][temp2.getPosY()] = temp2;
        this.piece[king.getPosX()][king.getPosY()] = null;
        this.piece[rook.getPosX()][rook.getPosY()] =  null;
        allPieces.remove(king);
        allPieces.remove(rook);
        allPieces.add(temp);
        allPieces.add(temp2);
        king = null;
        rook = null;
    }

    // Precondition : Need a fast way to check the movements of all the pieces
    // Postcondition : Goes through the list of pieces and checks each of their moves
    private void moveCheckAll() {
        for(Piece current : allPieces) {
            if(current.getPieceType() != 5) {
                current.moveCheck(current.getPosX(), current.getPosY(), this);
            }
        }
    }

    // Precondition : Need the king so it can be worked with
    // Postcondition : Finds the king and returns it to be worked with
    private Piece findWKing() {
        for(Piece current : allPieces) {
            if(current != null) {
                if(current.getPieceType() == 5) {
                    if(current.isWhite) {
                        return current;
                    }
                }
            }
        }
        this.winner = "black";
        return null;
    }

    // Precondition : Need the king so it can be worked with
    // Postcondition : Finds the king and returns it to be worked with
    private Piece findBKing() {
        for(Piece current : allPieces) {
            if(current != null) {
                if(current.getPieceType() == 5) {
                    if(!current.isWhite) {
                        return current;
                    }
                }
            }
        }
        this.winner = "white";
        return null;
    }

    // Precondition : I want to be able to easily add sounds in the game anywhere
    // Postcondition : Simple function for playing a sound and adjusting the volume (I think .5 is normal volume less than that is quieter)
    private void sound(String file, float volume_) {
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

    //Precondition : Got bored
    //Postcondition : Not bored anymore
    //Description : Just for fun because I think it'll be funny
    private void changeBoard(int count, Boolean valid) {
        if((count > 4) && (valid)) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Random rand = new Random();
            Image[] image = new Image[1];
            try {
                image[0] = ImageIO.read(new File(imageName()+".png")).getScaledInstance(500, 600, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int x;
            int y;
            File audioFile = new File(imageName()+".wav");
            AudioInputStream audioStream = null;
            try {
                audioStream = AudioSystem.getAudioInputStream(audioFile);
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
            try {
                clip.open(audioStream);
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volume = .03f;
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clip.start();
            while (clip.getMicrosecondLength() != clip.getMicrosecondPosition()) {
                JFrame something = new JFrame();
                x = rand.nextInt((int)screenSize.getWidth()-500);
                y = rand.nextInt((int)screenSize.getHeight()-600);
                something.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                something.setUndecorated(false);
                something.setBounds(x, y, 500, 600);
                JPanel somethingElse = new JPanel() {
                    @Override
                    public void paint(Graphics g) {
                        g.drawImage(image[0], 0, 0, this);
                    }
                };
                something.add(somethingElse);
                something.setVisible(true);
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            clip.close();
            try {
                audioStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            gameOver();
        }
    }

    // Precondition : I don't want you to know the name of the sound being played when you forget the magic word
    // Postcondition : "encrypts" the name of the file so it's not so easy
    private String imageName() {
        int x = 0;
        String imageName = "";
        x = (69+4);
        imageName += (char)x;
        x += (10+26);
        imageName += (char)x;
        x += (4-16);
        imageName += (char)x;
        x += (10-4);
        imageName += (char)x;
        x = (50+73-22);
        imageName += (char)x;
        x += (10+4);
        imageName += (char)x;
        x += (-15-53);
        imageName += (char)x;
        x += (31);
        imageName += (char)x;
        x += (11+22);
        imageName += (char)x;
        x = 116;
        imageName += (char)x;
        x = 83;
        imageName += (char)x;
        x = 117;
        imageName += (char)x;
        x = 115;
        imageName += (char)x;
        return imageName; // Printing this is cheating
    }

    // Precondition : Need to change the status of the game if the game is over
    // Postcondition : Changes the status of the game and also plays a game over sound
    private void gameOver() {
        sound("GameOver", .03f);
        this.gameOver = true;
    }

    // Precondition : It was getting annoying to retype all this
    // Postcondition : Makes it so I don't have to retype it all but I think it might be a dumb solution
    private void stupid(Piece piece, int x, int y) {
        removePiece(piece.getPosX(), piece.getPosY());
        piece.setPosX(x);
        piece.setPosY(y);
        this.blockingPieces.clear();
        sound("Move", .03f);
        this.piece[x][y] = piece;
        changeTurn();
    }

    // Precondition : Want to print the information about a piece for debugging
    // Postcondition : Prints info about a piece and other things
    public void printInfo(String what_is_it_doing, String in_what_class_func, String what_line, Piece the_piece) {
        System.out.print(what_is_it_doing);
        System.out.print(" - ");
        System.out.print(in_what_class_func);
        System.out.print("(line : ");
        System.out.print(what_line);
        System.out.print(")");
        if(the_piece != null) {
            System.out.print(" - Piece Type(");
            System.out.print(the_piece.getPieceType());
            System.out.print(") - Piece Loc(");
            System.out.print(the_piece.getPosX());
            System.out.print(" , ");
            System.out.print(the_piece.getPosY());
            System.out.print(")");
            if(the_piece.getPieceType() == 5) {
                System.out.print(" - Checked(");
                System.out.print(((King)the_piece).isChecked());
                System.out.print(")");
                System.out.print(" - Block Available(");
                System.out.print(((King)the_piece).isBlockAvailable());
                System.out.print(")");
            }
            System.out.println();
        } else {
            System.out.println();
        }
    }

    // Precondition : Want to print array for specific pieces for debugging
    // Postcondition : Prints the array for the piece
    public void printArray(int[][] array) {
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                System.out.print(array[x][y]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
