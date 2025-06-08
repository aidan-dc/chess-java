
public class boardInstance {

	private static piece[][] boardCopy;
	private boolean isWhiteTurn;
	private String whiteKingPos, blackKingPos;
	
	public boardInstance() {
		
	}
	
	public boardInstance(piece[][] tempBoard, boolean isWhitePlayerTurn, String whiteKingCoords, String blackKingCoords) {
		boardCopy = tempBoard;
		isWhiteTurn = isWhitePlayerTurn;
		whiteKingPos = whiteKingCoords;
		blackKingPos = blackKingCoords;
	}
	
	public static piece[][] getBoardCopy(){
		return boardCopy;
	}
	
	public boolean isWhiteTurn() {
		return isWhiteTurn;
	}
	
	public String getWhiteKingPos() {
		return whiteKingPos;
	}
	
	public String getBlackKingPos() {
		return blackKingPos;
	}
}
