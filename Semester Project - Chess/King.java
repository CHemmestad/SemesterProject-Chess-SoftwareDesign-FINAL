import java.util.ArrayList;

public class King extends Piece {
    protected ArrayList<Piece> castlePiece = new ArrayList<>();
    private boolean inCheck;
    private boolean castle = true;
    private boolean blockAvailable;
    private Piece checkedBy;
    

    public King(String name, String imageName, boolean isWhite, int pieceSize) {
        super(name, imageName, isWhite, pieceSize);
    }

    public boolean isChecked() {
        return inCheck;
    }

    public boolean isCastle() {
        return castle;
    }

    public boolean isBlockAvailable() {
        return blockAvailable;
    }

    public void setCheckedBy(Piece checker) {
        checkedBy = checker;
    }

    public void setCheck(boolean checked) {
        this.inCheck = checked;
    }

    public void setCastle(boolean castle) {
        this.castle = castle;
    }

    public void setBlockAvailable(boolean blockAvailable) {
        this.blockAvailable = blockAvailable;
    }

    public Piece getCheckedBy() {
        return checkedBy;
    }

    // Precondition : Need to define what the valid movements for this piece are
    // Postcondition : Uses functions from the parent class to define the movements for this piece
    @Override
    public int[][] moveCheck(int xPosition, int yPosition, Board board) {
        reset();
        up(xPosition, yPosition, 1, board);
        down(xPosition, yPosition, 1, board);
        right(xPosition, yPosition, 1, board, true);
        left(xPosition, yPosition, 1, board, true);
        diagonalUpRight(xPosition, yPosition, 1, board);
        diagonalUpLeft(xPosition, yPosition, 1, board);
        diagonalDownRight(xPosition, yPosition, 1, board);
        diagonalDownLeft(xPosition, yPosition, 1, board);
        if(castle && firstMove) {
            isCastleAvailable(xPosition, yPosition, board);
        }
        return this.validMoves;
    }

    // Precondition : Need a way to see if the king has a move available for checking the conditions of the game
    // Postcondition : Checks to see if the king has a move it can make
    protected boolean isMoveAvailable() {
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                if((this.validMoves[x][y] == -1) || (this.validMoves[x][y] == 2)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Precondition : Need to check if the king has a castle to the right and left that it can switch with
    // Postcondition : Checks the right and left for a castle
    private void isCastleAvailable(int xPosition, int yPosition, Board board) {
        Piece temp;
        temp = right(xPosition, yPosition, 5, board, false);
        if(temp != null) {
            this.castlePiece.add(temp);
        }
        temp = left(xPosition, yPosition, 5, board, false);
        if(temp != null) {
            this.castlePiece.add(temp);
        }
    }
}
