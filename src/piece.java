//piece class for chessMain
public class piece {
	
	private String type,color;
	private int value;
	
	public piece() {
		
	}
	
	public piece(String pieceType, String pieceColor, int pieceValue) {
		type = pieceType;
		value = pieceValue;
		color = pieceColor;
	}
	
	public String getType() {
		return type;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getColor() {
		return color;
	}
	
}