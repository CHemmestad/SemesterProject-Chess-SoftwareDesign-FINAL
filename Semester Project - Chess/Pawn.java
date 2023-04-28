public class Pawn extends Piece {
    private int limit;

    public Pawn(String name, String imageName, boolean isWhite, int pieceSize) {
        super(name, imageName, isWhite, pieceSize);
    }

    // Precondition : Need to define what the valid movements for this piece are
    // Postcondition : Uses functions from the parent class to define the movements for this piece
    @Override
    public int[][] moveCheck(int xPosition, int yPosition, Board board) {
        reset();
        if(this.firstMove) {
            this.limit = 2;
        } else {
            this.limit = 1; // Sets the limit to 1 if this piece has already made a first move
        }
        if(this.isWhite) {
            diagonalUpLeft(xPosition, yPosition, 1, board);
            diagonalUpRight(xPosition, yPosition, 1, board);
            up(xPosition, yPosition, limit, board);
        } else {
            diagonalDownLeft(xPosition, yPosition, 1, board);
            diagonalDownRight(xPosition, yPosition, 1, board);
            down(xPosition, yPosition, limit, board);
        }
        return this.validMoves;
    }
}
