public class Bishop extends Piece {

    public Bishop(String name, String imageName, boolean isWhite, int pieceSize) {
        super(name, imageName, isWhite, pieceSize);
    }

    // Precondition : Need to define what the valid movements for this piece are
    // Postcondition : Uses functions from the parent class to define the movements for this piece
    @Override
    public int[][] moveCheck(int xPosition, int yPosition, Board board) {
        reset();
        diagonalUpRight(xPosition, yPosition, 10, board);
        diagonalUpLeft(xPosition, yPosition, 10, board);
        diagonalDownRight(xPosition, yPosition, 10, board);
        diagonalDownLeft(xPosition, yPosition, 10, board);
        return this.validMoves;
    }
}
