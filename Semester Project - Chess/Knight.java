public class Knight extends Piece {

    public Knight(String name, String imageName, boolean isWhite, int pieceSize) {
        super(name, imageName, isWhite, pieceSize);
    }

    // Precondition : Need to define what the valid movements for this piece are
    // Postcondition : Uses functions from the parent class to define the movements for this piece
    @Override
    public int[][] moveCheck(int xPosition, int yPosition, Board board) {
        reset();
        upL(xPosition, yPosition, board);
        downL(xPosition, yPosition, board);
        rightL(xPosition, yPosition, board);
        leftL(xPosition, yPosition, board);
        return this.validMoves;
    }
}
