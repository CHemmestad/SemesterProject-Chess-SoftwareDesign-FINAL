public class PieceFactory {

    public static Piece createPiece(String type, Boolean isWhite, int pieceSize) {
        
        if(isWhite) {
            switch (type) {
                case "pawn" :
                    return new Pawn(type, "Images/PawnWnew.png", true, pieceSize);
                case "rook" :
                    return new Rook(type, "Images/RookWnew.png", true, pieceSize);
                case "bishop" :
                    return new Bishop(type, "Images/BishopWnew.png", true, pieceSize);
                case "knight" :
                    return new Knight(type, "Images/KnightWnew.png", true, pieceSize);
                case "queen" :
                    return new Queen(type, "Images/QueenWnew.png", true, pieceSize);
                case "king" :
                    return new King(type, "Images/KingWnew.png", true, pieceSize);
                default :
                    return null;
            }
        } else {
            switch (type) {
                case "pawn" :
                    return new Pawn(type, "Images/PawnBnew.png", false, pieceSize);
                case "rook" :
                    return new Rook(type, "Images/RookBnew.png", false, pieceSize);
                case "bishop" :
                    return new Bishop(type, "Images/BishopBnew.png", false, pieceSize);
                case "knight" :
                    return new Knight(type, "Images/KnightBnew.png", false, pieceSize);
                case "queen" :
                    return new Queen(type, "Images/QueenBnew.png", false, pieceSize);
                case "king" :
                    return new King(type, "Images/KingBnew.png", false, pieceSize);
                default :
                    return null;
            }
        }
    }
}
