import java.awt.*;
import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.embed.swing.JFXPanel;


public class chessMain extends Canvas implements MouseListener{
	
	Color darkBrown = new Color(90,63,29);
	Color brown = new Color(163,119,84);
	Color offWhite = new Color(243,235,215);
	Color lighterBlack = new Color(42,41,39);
	Color transparentShade = new Color(50,50,50,69);
	Color transparentLighterBlack = new Color(42,41,39,80);
	
	Image whitePawn, whiteRook, whiteKnight, whiteBishop, whiteKing, whiteQueen;
	Image blackPawn, blackRook, blackKnight, blackBishop, blackKing, blackQueen;	
	
	ArrayList<boardInstance> boardHistory = new ArrayList<boardInstance>();
	
	private static piece[][] board;
	
	private static int moveCount = 0;
	private static int selectedPieceRow = -1;
	private static int selectedPieceCol = -1;
	private static int desiredRow = -1;
	private static int desiredCol = -1;
	
	private static String whiteKingPosition, blackKingPosition; //Strings that store the position of the two kings in the format "RC" where R is the row of the king where 0<=R<=7 and C is the column of the king where 0<=C<=7
	private static boolean whiteCheck = false; //returns true if the white king is in check
	private static boolean blackCheck = false; //returns true if the black king is in check
	
	private static boolean isWhitesTurn = true;
	
	private static boolean whiteInCheckmate = false, blackInCheckmate = false, stalemate = false;
	private static boolean gameOver = false;
	
	private static boolean canWhiteCastleKingside = true;
	private static boolean canBlackCastleKingside = true;
	private static boolean canWhiteCastleQueenside = true;
	private static boolean canBlackCastleQueenside = true;
	
	public static int blackMaterialCount, whiteMaterialCount;
	
	public chessMain() throws Exception{
		final JFXPanel fxPanel = new JFXPanel();
		addMouseListener(this);
		enableEvents(java.awt.AWTEvent.KEY_EVENT_MASK);

		whitePawn = ImageIO.read(new File("resources/whitePawn.png"));
		whiteRook = ImageIO.read(new File("resources/whiteRook.png"));
		whiteKnight = ImageIO.read(new File("resources/whiteKnight.png"));
		whiteBishop = ImageIO.read(new File("resources/whiteBishop.png"));
		whiteKing = ImageIO.read(new File("resources/whiteKing.png"));
		whiteQueen = ImageIO.read(new File("resources/whiteQueen.png"));
		
		blackPawn = ImageIO.read(new File("resources/blackPawn.png"));
		blackRook = ImageIO.read(new File("resources/blackRook.png"));
		blackKnight = ImageIO.read(new File("resources/blackKnight.png"));
		blackBishop = ImageIO.read(new File("resources/blackBishop.png"));
		blackKing = ImageIO.read(new File("resources/blackKing.png"));
		blackQueen = ImageIO.read(new File("resources/blackQueen.png"));
		
		board = new piece[8][8];
		
		for (int c=0; c<board[0].length;c++) {
			board[1][c] = new piece("Pawn","Black",1);
			board[6][c] = new piece("Pawn","White",1);
		}
		board[0][0] = new piece("Rook","Black",5);
		board[0][7] = new piece("Rook","Black",5);
		board[7][0] = new piece("Rook","White",5);
		board[7][7] = new piece("Rook","White",5);
		board[0][1] = new piece("Knight","Black",3);
		board[0][6] = new piece("Knight","Black",3);
		board[7][1] = new piece("Knight","White",3);
		board[7][6] = new piece("Knight","White",3);
		board[0][2] = new piece("Bishop","Black",3);
		board[0][5] = new piece("Bishop","Black",3);
		board[7][2] = new piece("Bishop","White",3);
		board[7][5] = new piece("Bishop","White",3);
		board[0][3] = new piece("Queen","Black",9);
		board[7][3] = new piece("Queen","White",9);
		
		board[0][4] = new piece("King","Black",0);
		blackKingPosition = "04";
		board[7][4] = new piece("King","White",0);
		whiteKingPosition = "74";
		
		piece[][] tempBoard = new piece[8][8];
		for (int i=0; i<tempBoard.length;i++) {
			System.arraycopy(board[i], 0, tempBoard[i], 0, board.length);
		}
		boardHistory.add(new boardInstance(tempBoard, isWhitesTurn, whiteKingPosition, blackKingPosition));
	}
	
	public static void main(String[] args0) throws Exception{
		JFrame window = new JFrame("Chess");
		window.setSize(645, 755);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(new chessMain());
		window.setVisible(true);
	}
	
	public void paint(Graphics g) {
		this.setBackground(offWhite);
		System.out.println(boardHistory.size());
		for (int r=0; r<board.length;r++) {
			for (int c=0; c<board[0].length;c++) {
				if ((r+c)%2==1) {
					g.setColor(brown);
					g.fillRect(14+c*75, 56+6+r*75, 75, 75);
				}
			}
		}
		g.setColor(Color.BLACK);
		g.drawRect(14, 62, 600, 600);
		g.setColor(darkBrown);
		for (int i=0; i<9;i++) {
			g.fillRect(14+i*75, 62, 2, 600);
			g.fillRect(14, 62+i*75, 600, 2);
		}
		g.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		for (int r=0; r<board.length;r++) {
			if (r%2==0) {
				g.setColor(brown);
			}
			if (r%2==1) {
				g.setColor(offWhite);
			}
			g.drawString(String.valueOf(8-r), 16, 80+r*75);
		}
		String columnLetters = "abcdefgh";
		for (int c=0; c<board[0].length;c++) {
			if (c%2==1) {
				g.setColor(brown);
			}
			if (c%2==0) {
				g.setColor(Color.WHITE);
			}
			g.drawString(columnLetters.substring(c,c+1), 78+c*75, 658);
		}
		for (int r=0; r<board.length;r++) {
			for (int c=0; c<board[0].length;c++) {
				if (board[r][c]!=null) {
					if (board[r][c].getColor().equals("White")) {
						g.setColor(Color.white);
						if (board[r][c].getType().equals("Pawn")) {
							g.drawImage(whitePawn, 17+c*75, 64+r*75, 70, 70, this);
						}
						else if (board[r][c].getType().equals("Rook")) {
							g.drawImage(whiteRook, 17+c*75, 64+r*75, 70, 70, this);
						}
						else if (board[r][c].getType().equals("Knight")) {
							g.drawImage(whiteKnight, 17+c*75, 64+r*75, 70, 70, this);
						}
						else if (board[r][c].getType().equals("Bishop")) {
							g.drawImage(whiteBishop, 17+c*75, 64+r*75, 70, 70, this);
						}
						else if (board[r][c].getType().equals("King")) {
							g.drawImage(whiteKing, 18+c*75, 64+r*75, 70, 70, this);
						}
						else if (board[r][c].getType().equals("Queen")) {
							g.drawImage(whiteQueen, 17+c*75, 64+r*75, 70, 70, this);
						}
					}
					if (board[r][c].getColor().equals("Black")) {
						g.setColor(lighterBlack);
						if (board[r][c].getType().equals("Pawn")) {
							g.drawImage(blackPawn, 17+c*75, 64+r*75, 70, 70, this);
						}
						else if (board[r][c].getType().equals("Rook")) {
							g.drawImage(blackRook, 17+c*75, 64+r*75, 70, 70, this);
						}
						else if (board[r][c].getType().equals("Knight")) {
							g.drawImage(blackKnight, 17+c*75, 64+r*75, 70, 70, this);
						}
						else if (board[r][c].getType().equals("Bishop")) {
							g.drawImage(blackBishop, 17+c*75, 64+r*75, 70, 70, this);
						}
						else if (board[r][c].getType().equals("King")) {
							g.drawImage(blackKing, 17+c*75, 64+r*75, 70, 70, this);
						}
						else if (board[r][c].getType().equals("Queen")) {
							g.drawImage(blackQueen, 17+c*75, 64+r*75, 70, 70, this);
						}
					}
				}
			}
		}
		
		blackMaterialCount = 0;
		whiteMaterialCount = 0;
		for (int r=0; r<board.length;r++) {
			for (int c=0; c<board[0].length;c++) {
				if (board[r][c]!=null) {
					if (board[r][c].getColor().equals("White")) {
						whiteMaterialCount+=board[r][c].getValue();
					}
					else {
						blackMaterialCount+=board[r][c].getValue();
					}
				}
			}
		}
		g.setColor(darkBrown);
		if (whiteMaterialCount>blackMaterialCount) {
			g.drawString("+"+String.valueOf(whiteMaterialCount-blackMaterialCount), 20, 688);
		}
		if (whiteMaterialCount<blackMaterialCount) {
			g.drawString("+"+String.valueOf(blackMaterialCount-whiteMaterialCount), 20, 56);
		}
		if (whiteMaterialCount==blackMaterialCount && blackMaterialCount==0) {
			gameOver = true;
			stalemate = true;
		}
		
		if (selectedPieceRow>=0 && selectedPieceCol>=0) {
			g.setColor(transparentLighterBlack);
			g.fillOval(17+selectedPieceCol*75, 64+selectedPieceRow*75, 70, 70);
			
			g.setColor(transparentShade);
			if (board[selectedPieceRow][selectedPieceCol].getType().equals("Pawn")) {
				for (int r=0; r<board.length;r++) {
					for (int c=0; c<board[0].length;c++) {
						if (isValidPawnMove(selectedPieceRow,selectedPieceCol,r,c,board)) {
							g.fillOval(35+c*75, 78+r*75, 36, 36);
						}
					}
				}
			}
			if (board[selectedPieceRow][selectedPieceCol].getType().equals("Knight")) {
				for (int r=0; r<board.length;r++) {
					for (int c=0; c<board[0].length;c++) {
						if (isValidKnightMove(selectedPieceRow,selectedPieceCol,r,c,board)) {
							g.fillOval(35+c*75, 78+r*75, 36, 36);
						}
					}
				}
			}
			if (board[selectedPieceRow][selectedPieceCol].getType().equals("Bishop")) {
				for (int r=0; r<board.length;r++) {
					for (int c=0; c<board[0].length;c++) {
						if (isValidBishopMove(selectedPieceRow,selectedPieceCol,r,c,board)) {
							g.fillOval(35+c*75, 78+r*75, 36, 36);
						}
					}
				}
			}
			if (board[selectedPieceRow][selectedPieceCol].getType().equals("Rook")) {
				for (int r=0; r<board.length;r++) {
					for (int c=0; c<board[0].length;c++) {
						if (isValidRookMove(selectedPieceRow,selectedPieceCol,r,c,board)) {
							g.fillOval(35+c*75, 78+r*75, 36, 36);
						}
					}
				}
			}
			if (board[selectedPieceRow][selectedPieceCol].getType().equals("Queen")) {
				for (int r=0; r<board.length;r++) {
					for (int c=0; c<board[0].length;c++) {
						if (isValidQueenMove(selectedPieceRow,selectedPieceCol,r,c,board)) {
							g.fillOval(35+c*75, 78+r*75, 36, 36);
						}
					}
				}
			}
			if (board[selectedPieceRow][selectedPieceCol].getType().equals("King")) {
				for (int r=0; r<board.length;r++) {
					for (int c=0; c<board[0].length;c++) {
						if (isValidKingMove(selectedPieceRow,selectedPieceCol,r,c,board)) {
							g.fillOval(35+c*75, 78+r*75, 36, 36);
						}
					}
				}
			}
		}
		
		g.setColor(darkBrown);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 32));
		g.drawString("CHESS", 272, 32);
		
		if ((whiteCheck || blackCheck) && !gameOver) {
			g.setFont(new Font("Times New Roman", Font.BOLD, 28));
			g.drawString("Check!", 272, 60);
		}
		g.setFont(new Font("Times New Roman", Font.BOLD, 24));
		if (gameOver) {
			if (blackCheck) {
				g.drawString("Checkmate! White Wins", 228, 60);
			}
			if (whiteCheck) {
				g.drawString("Checkmate! Black Wins", 228, 60);
			}
			if (stalemate) {
				g.drawString("Stalemate! Game drawn", 200, 60);
			}
		}
		
		if (isWhitesTurn && !gameOver) {
			g.setFont(new Font("Times New Roman", Font.PLAIN, 22));
			g.drawString("White to move", 472, 688);
		}
		
		if (!isWhitesTurn && !gameOver) {
			g.setFont(new Font("Times New Roman", Font.PLAIN, 22));
			g.drawString("Black to move", 472, 56);
		}
		
		
	}
	
	public void update(Graphics g) {
	    Graphics offgc;
	    Image offscreen = null;
	    Dimension d = size();
	    offscreen = createImage(d.width, d.height);
	    offgc = offscreen.getGraphics();
	    offgc.setColor(getBackground());
	    offgc.fillRect(0, 0, d.width, d.height);
	    offgc.setColor(getForeground());
	    paint(offgc);
	    g.drawImage(offscreen, 0, 0, this);
	    }
	
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if ((e.getY()-6-56)/75<=7 && (e.getY()-6-56)/75>=0 && (e.getX()-14)/75<=7 && (e.getX()-14)/75>=0){
				if (desiredRow < 0 && desiredCol < 0 && selectedPieceRow>=0 && selectedPieceCol>=0 && !gameOver) {
					desiredRow = (int)((e.getY()-6-56)/75);
					desiredCol = (int)((e.getX()-14)/75);
					System.out.println("("+desiredRow+", "+desiredCol+")");
				}
				
				if (selectedPieceRow<0 && selectedPieceCol<0 && !gameOver && board[(int)((e.getY()-6-56)/75)][(int)((e.getX()-14)/75)]!=null) {
					selectedPieceRow = (int)((e.getY()-6-56)/75);
					selectedPieceCol = (int)((e.getX()-14)/75);
					if ((board[selectedPieceRow][selectedPieceCol].getColor().equals("White") && !isWhitesTurn) || (board[selectedPieceRow][selectedPieceCol].getColor().equals("Black") && isWhitesTurn)) {
						selectedPieceRow=-1;
						selectedPieceCol=-1;
					}
				}
				
				if (desiredRow >= 0 && desiredCol >= 0 && selectedPieceRow>=0 && selectedPieceCol>=0 && !gameOver) {
					if (board[selectedPieceRow][selectedPieceCol]!=null) {
						if (board[selectedPieceRow][selectedPieceCol].getType().equals("Pawn")) {
							if (isValidPawnMove(selectedPieceRow,selectedPieceCol,desiredRow,desiredCol,board)) {
								System.out.println("Valid Pawn");

								board[desiredRow][desiredCol]=board[selectedPieceRow][selectedPieceCol];
								board[selectedPieceRow][selectedPieceCol]=null;
								if (board[desiredRow][desiredCol].getColor().equals("White") && desiredRow==0) {
									board[desiredRow][desiredCol]=null;
									board[desiredRow][desiredCol]=new piece("Queen","White",9);
								}
								else if (board[desiredRow][desiredCol].getColor().equals("Black") && desiredRow==7) {
									board[desiredRow][desiredCol]=null;
									board[desiredRow][desiredCol]=new piece("Queen","Black",9);
								}
								isWhitesTurn = !isWhitesTurn;
								desiredRow=-1;
								desiredCol=-1;
								selectedPieceRow=-1;
								selectedPieceCol=-1;
								moveCount++;
								piece[][] tempBoard = new piece[8][8];
								for (int i=0; i<tempBoard.length;i++) {
									System.arraycopy(board[i], 0, tempBoard[i], 0, board.length);
								}
								boardHistory.add(new boardInstance(tempBoard, isWhitesTurn, whiteKingPosition, blackKingPosition));
								repaint();
								playSound();
							}
							else {
								desiredRow=-1;
								desiredCol=-1;
							}
						}
						else if (board[selectedPieceRow][selectedPieceCol].getType().equals("Knight")) {
							if (isValidKnightMove(selectedPieceRow,selectedPieceCol,desiredRow,desiredCol,board)) {
								System.out.println("Valid Knight");

								board[desiredRow][desiredCol]=board[selectedPieceRow][selectedPieceCol];
								board[selectedPieceRow][selectedPieceCol]=null;
								isWhitesTurn = !isWhitesTurn;
								desiredRow=-1;
								desiredCol=-1;
								selectedPieceRow=-1;
								selectedPieceCol=-1;
								moveCount++;
								boardHistory.add(new boardInstance(board, isWhitesTurn, whiteKingPosition, blackKingPosition));
								repaint();
								playSound();
							}
							else {
								desiredRow=-1;
								desiredCol=-1;
							}
						}
						else if (board[selectedPieceRow][selectedPieceCol].getType().equals("Rook")) {
							if (isValidRookMove(selectedPieceRow,selectedPieceCol,desiredRow,desiredCol,board)) {
								System.out.println("Valid Rook");

								board[desiredRow][desiredCol]=board[selectedPieceRow][selectedPieceCol];
								board[selectedPieceRow][selectedPieceCol]=null;
								if (board[desiredRow][desiredCol].getColor().equals("White")) {
									if (selectedPieceCol==7) {
										canWhiteCastleKingside=false;
									}
									if (selectedPieceCol==0) {
										canWhiteCastleQueenside=false;
									}
								}
								if (board[desiredRow][desiredCol].getColor().equals("Black")) {
									if (selectedPieceCol==7) {
										canBlackCastleKingside=false;
									}
									if (selectedPieceCol==0) {
										canBlackCastleQueenside=false;
									}
								}
								isWhitesTurn = !isWhitesTurn;
								desiredRow=-1;
								desiredCol=-1;
								selectedPieceRow=-1;
								selectedPieceCol=-1;
								moveCount++;
								boardHistory.add(new boardInstance(board, isWhitesTurn, whiteKingPosition, blackKingPosition));
								repaint();
								playSound();
							}
							else {
								desiredRow=-1;
								desiredCol=-1;
							}
						}
						else if (board[selectedPieceRow][selectedPieceCol].getType().equals("Bishop")) {
							if (isValidBishopMove(selectedPieceRow,selectedPieceCol,desiredRow,desiredCol,board)) {
								System.out.println("Valid Bishop");

								board[desiredRow][desiredCol]=board[selectedPieceRow][selectedPieceCol];
								board[selectedPieceRow][selectedPieceCol]=null;
								isWhitesTurn = !isWhitesTurn;
								desiredRow=-1;
								desiredCol=-1;
								selectedPieceRow=-1;
								selectedPieceCol=-1;
								moveCount++;
								boardHistory.add(new boardInstance(board, isWhitesTurn, whiteKingPosition, blackKingPosition));
								repaint();
								playSound();
							}
							else {
								desiredRow=-1;
								desiredCol=-1;
							}
						}
						else if (board[selectedPieceRow][selectedPieceCol].getType().equals("Queen")) {
							if (isValidQueenMove(selectedPieceRow,selectedPieceCol,desiredRow,desiredCol,board)) {
								System.out.println("Valid Queen");

								board[desiredRow][desiredCol]=board[selectedPieceRow][selectedPieceCol];
								board[selectedPieceRow][selectedPieceCol]=null;
								isWhitesTurn = !isWhitesTurn;
								desiredRow=-1;
								desiredCol=-1;
								selectedPieceRow=-1;
								selectedPieceCol=-1;
								moveCount++;
								boardHistory.add(new boardInstance(board, isWhitesTurn, whiteKingPosition, blackKingPosition));
								repaint();
								playSound();
							}
							else {
								desiredRow=-1;
								desiredCol=-1;
							}
						}
						else if (board[selectedPieceRow][selectedPieceCol].getType().equals("King")) {
							if (isValidKingMove(selectedPieceRow,selectedPieceCol,desiredRow,desiredCol,board)==true) {
								System.out.println("Valid King");
								board[desiredRow][desiredCol]=board[selectedPieceRow][selectedPieceCol];
								board[selectedPieceRow][selectedPieceCol]=null;
								if (desiredRow==selectedPieceRow && desiredCol==6 && selectedPieceCol==4) {
									board[selectedPieceRow][5]=board[selectedPieceRow][7];
									board[selectedPieceRow][7]=null;
								}
								if (desiredRow==selectedPieceRow && desiredCol==2 && selectedPieceCol==4) {
									board[selectedPieceRow][3]=board[selectedPieceRow][0];
									board[selectedPieceRow][0]=null;
								}
								if (board[desiredRow][desiredCol].getColor().equals("White")) {
									whiteKingPosition = String.valueOf(desiredRow)+String.valueOf(desiredCol);
									canWhiteCastleKingside=false;
									canWhiteCastleQueenside=false;
								}
								else if (board[desiredRow][desiredCol].getColor().equals("Black")) {
									blackKingPosition = String.valueOf(desiredRow)+String.valueOf(desiredCol);
									canBlackCastleKingside=false;
									canBlackCastleQueenside=false;
								}
								isWhitesTurn = !isWhitesTurn;
								desiredRow=-1;
								desiredCol=-1;
								selectedPieceRow=-1;
								selectedPieceCol=-1;
								moveCount++;
								boardHistory.add(new boardInstance(board, isWhitesTurn, whiteKingPosition, blackKingPosition));
								repaint();
								playSound();
							}
							else {
								desiredRow=-1;
								desiredCol=-1;
							}
						}
					}
				}
				
				whiteCheck = whiteInCheck(board, whiteKingPosition);
			    blackCheck = blackInCheck(board, blackKingPosition);
			    testForGameOver(board, isWhitesTurn);
				repaint();
				
			}
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			desiredRow=-1;
			desiredCol=-1;
			selectedPieceRow=-1;
			selectedPieceCol=-1;
			repaint();
		}
	}
	
	public void mouseEntered(MouseEvent e) {
		
	}
	
	public void mousePressed(MouseEvent e) {
		
	}
	
	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
				
	}
	
	public static boolean isValidPawnMove(int pR, int pC, int dR, int dC, piece[][] usedBoard) { //pR=pieceRow, cR=pieceCol, dR=desiredRow, dC=desiredCol remember to implement en passant moves
		if (usedBoard[pR][pC].getColor().equals("White")) {
			if (dR>=pR) {
				return false;
			}
		}
		if (usedBoard[pR][pC].getColor().equals("Black")) {
			if (dR<=pR) {
				return false;
			}
		}
		if (dR==pR) {
			return false;
		}
		
		if (pC==dC && pR==dR) {
			return false;
		}
		if (dC!=pC) {
			if (Math.abs(dC-pC)!=1) {
				return false;
			}
			if (Math.abs(dR-pR)!=1) {
				return false;
			}
			if (usedBoard[dR][dC]==null) {
				return false;
			}
		}
		if (dC==pC) {
			if (usedBoard[dR][dC]!=null) {
				return false;
			}
		}
		if (Math.abs(dR-pR)!=1) {
			if (Math.abs(dR-pR)>2) {
				return false;
			}
			if (Math.abs(dR-pR)==2) {
				if (usedBoard[pR][pC].getColor().equals("White")) {
					if (usedBoard[pR-1][pC]!=null) {
						return false;
					}
					if (pR!=6) {
						return false;
					}
				}
				if (usedBoard[pR][pC].getColor().equals("Black")) {
					if (pR!=1) {
						return false;
					}
					if (usedBoard[pR+1][pC]!=null) {
						return false;
					}
				}
			}
		}
		if (usedBoard[dR][dC]!=null) {
			if (usedBoard[dR][dC].getColor().equals(usedBoard[pR][pC].getColor())) {
				return false;
			}
		}
		
		piece[][] tempBoard = new piece[usedBoard.length][usedBoard[0].length];
		for (int r=0; r<tempBoard.length;r++) {
			for (int c=0; c<tempBoard[0].length;c++) {
				tempBoard[r][c] = usedBoard[r][c];
			}
		}
		String tempWhiteKingPosition = null, tempBlackKingPosition = null;
		find_temp_king_loop:
		for (int r=0; r<tempBoard.length;r++) {
			for (int c=0; c<tempBoard[0].length;c++) {
				if (tempBoard[r][c]!=null) {
					if (tempBoard[r][c].getType().equals("King")) {
						if (tempBoard[r][c].getColor().equals("White")) {
							tempWhiteKingPosition = String.valueOf(r)+String.valueOf(c);
						}
						if (tempBoard[r][c].getColor().equals("Black")) {
							tempBlackKingPosition = String.valueOf(r)+String.valueOf(c);
						}
					}
					if (tempBlackKingPosition!=null && tempWhiteKingPosition!=null) {
						break find_temp_king_loop;
					}
				}
			}
		}
		tempBoard[dR][dC]=tempBoard[pR][pC];
		tempBoard[pR][pC]=null;
		if (tempBoard[dR][dC].getColor().equals("White")) {
			if (whiteInCheck(tempBoard,whiteKingPosition)) {
				return false;
			}
		}
		if (tempBoard[dR][dC].getColor().equals("Black")) {
			if (blackInCheck(tempBoard,blackKingPosition)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isValidKnightMove(int pR, int pC, int dR, int dC, piece[][] usedBoard) {
		if (usedBoard[dR][dC]!=null) {
			if (usedBoard[dR][dC].getColor().equals(usedBoard[pR][pC].getColor())) {
				return false;
			}
		}
		piece[][] tempBoard = new piece[usedBoard.length][usedBoard[0].length];
		for (int r=0; r<tempBoard.length;r++) {
			for (int c=0; c<tempBoard[0].length;c++) {
				tempBoard[r][c] = usedBoard[r][c];
			}
		}
		String tempWhiteKingPosition = null, tempBlackKingPosition = null;
		find_temp_king_loop:
		for (int r=0; r<tempBoard.length;r++) {
			for (int c=0; c<tempBoard[0].length;c++) {
				if (tempBoard[r][c]!=null) {
					if (tempBoard[r][c].getType().equals("King")) {
						if (tempBoard[r][c].getColor().equals("White")) {
							tempWhiteKingPosition = String.valueOf(r)+String.valueOf(c);
						}
						if (tempBoard[r][c].getColor().equals("Black")) {
							tempBlackKingPosition = String.valueOf(r)+String.valueOf(c);
						}
					}
					if (tempBlackKingPosition!=null && tempWhiteKingPosition!=null) {
						break find_temp_king_loop;
					}
				}
			}
		}
		tempBoard[dR][dC]=tempBoard[pR][pC];
		tempBoard[pR][pC]=null;
		if (tempBoard[dR][dC].getColor().equals("White")) {
			if (whiteInCheck(tempBoard,whiteKingPosition)) {
				return false;
			}
		}
		if (tempBoard[dR][dC].getColor().equals("Black")) {
			if (blackInCheck(tempBoard,blackKingPosition)) {
				return false;
			}
		}
		
		if (Math.abs(dC-pC)==1 && Math.abs(dR-pR)==2) {
			return true;
		}
		if (Math.abs(dC-pC)==2 && Math.abs(dR-pR)==1) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isValidRookMove(int pR, int pC, int dR, int dC, piece[][] usedBoard) {
		if ((pC==dC && pR==dR) || (pC!=dC && pR!=dR)) {
			return false;
		}
		if (usedBoard[dR][dC]!=null) {
			if (usedBoard[dR][dC].getColor().equals(usedBoard[pR][pC].getColor())) {
				return false;
			}
		}
		
		if (pC==dC) {
			if (dR>pR) {
				int n=1;
				while (dR!=pR+n) {
					if (usedBoard[pR+n][pC]!=null) {
						return false;
					}
					n++;
				}
			}
			else if (dR<pR) {
				int n=1;
				while (dR!=pR-n) {
					if (usedBoard[pR-n][pC]!=null) {
						return false;
					}
					n++;
				}
			}
		}
		if (pR==dR) {
			if (dC>pC) {
				int n=1;
				while (dC!=pC+n) {
					if (usedBoard[pR][pC+n]!=null) {
						return false;
					}
					n++;
				}
			}
			else if (dC<pC) {
				int n=1;
				while (dC!=pC-n) {
					if (usedBoard[pR][pC-n]!=null) {
						return false;
					}
					n++;
				}
			}
		}
		piece[][] tempBoard = new piece[usedBoard.length][usedBoard[0].length];
		for (int r=0; r<tempBoard.length;r++) {
			for (int c=0; c<tempBoard[0].length;c++) {
				tempBoard[r][c] = usedBoard[r][c];
			}
		}
		String tempWhiteKingPosition = null, tempBlackKingPosition = null;
		find_temp_king_loop:
		for (int r=0; r<tempBoard.length;r++) {
			for (int c=0; c<tempBoard[0].length;c++) {
				if (tempBoard[r][c]!=null) {
					if (tempBoard[r][c].getType().equals("King")) {
						if (tempBoard[r][c].getColor().equals("White")) {
							tempWhiteKingPosition = String.valueOf(r)+String.valueOf(c);
						}
						if (tempBoard[r][c].getColor().equals("Black")) {
							tempBlackKingPosition = String.valueOf(r)+String.valueOf(c);
						}
					}
					if (tempBlackKingPosition!=null && tempWhiteKingPosition!=null) {
						break find_temp_king_loop;
					}
				}
			}
		}
		tempBoard[dR][dC]=tempBoard[pR][pC];
		tempBoard[pR][pC]=null;
		if (tempBoard[dR][dC].getColor().equals("White")) {
			if (whiteInCheck(tempBoard,whiteKingPosition)) {
				return false;
			}
		}
		if (tempBoard[dR][dC].getColor().equals("Black")) {
			if (blackInCheck(tempBoard,blackKingPosition)) {
				return false;
			}
		}
		
		
		
		return true;
	}
	
	public static boolean isValidBishopMove(int pR, int pC, int dR, int dC, piece[][] usedBoard) {
		if (pC==dC && pR==dR) {
			return false;
		}
		
		if (Math.abs(pR-dR)!=Math.abs(pC-dC)) {
			return false;
		}
		
		if (usedBoard[dR][dC]!=null) {
			if (usedBoard[dR][dC].getColor().equals(usedBoard[pR][pC].getColor())) {
				return false;
			}
		}
		
		if (dC>pC && dR>pR) {
			int n = 1;
			while ((dC!=pC+n)) {
				if (usedBoard[pR+n][pC+n]!=null) {
					return false;
				}
				n++;
			}
		}
		
		if (dC>pC && dR<pR) {
			int n = 1;
			while ((dC!=pC+n)) {
				if (usedBoard[pR-n][pC+n]!=null) {
					return false;
				}
				n++;
			}
		}
		
		if (dC<pC && dR<pR) {
			int n = 1;
			while ((dC!=pC-n)) {
				if (usedBoard[pR-n][pC-n]!=null) {
					return false;
				}
				n++;
			}
		}
		
		if (dC<pC && dR>pR) {
			int n = 1;
			while ((dC!=pC-n)) {
				if (usedBoard[pR+n][pC-n]!=null) {
					return false;
				}
				n++;
			}
		}
		piece[][] tempBoard = new piece[usedBoard.length][usedBoard[0].length];
		for (int r=0; r<tempBoard.length;r++) {
			for (int c=0; c<tempBoard[0].length;c++) {
				tempBoard[r][c] = usedBoard[r][c];
			}
		}
		String tempWhiteKingPosition = null, tempBlackKingPosition = null;
		find_temp_king_loop:
		for (int r=0; r<tempBoard.length;r++) {
			for (int c=0; c<tempBoard[0].length;c++) {
				if (tempBoard[r][c]!=null) {
					if (tempBoard[r][c].getType().equals("King")) {
						if (tempBoard[r][c].getColor().equals("White")) {
							tempWhiteKingPosition = String.valueOf(r)+String.valueOf(c);
						}
						if (tempBoard[r][c].getColor().equals("Black")) {
							tempBlackKingPosition = String.valueOf(r)+String.valueOf(c);
						}
					}
					if (tempBlackKingPosition!=null && tempWhiteKingPosition!=null) {
						break find_temp_king_loop;
					}
				}
			}
		}
		tempBoard[dR][dC]=tempBoard[pR][pC];
		tempBoard[pR][pC]=null;
		if (tempBoard[dR][dC].getColor().equals("White")) {
			if (whiteInCheck(tempBoard,whiteKingPosition)) {
				return false;
			}
		}
		if (tempBoard[dR][dC].getColor().equals("Black")) {
			if (blackInCheck(tempBoard,blackKingPosition)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isValidQueenMove(int pR, int pC, int dR, int dC, piece[][] usedBoard) {
		if (pC==dC && pR==dR) {
			return false;
		}
		
		if (Math.abs(dC-pC)==Math.abs(pR-dR)) {
			return isValidBishopMove(pR, pC, dR, dC,usedBoard);
		}
		
		else if ((pC==dC && pR!=dR) || (pR==dR && pC!=dC)) {
			return isValidRookMove(pR, pC, dR, dC,usedBoard);
		}
		
		
		
		
		return false;
	}
	
	public static boolean isValidKingMove(int pR, int pC, int dR, int dC, piece[][] usedBoard) {
		
		if (pC==dC && pR==dR) {
			return false;
		}
		
		if (usedBoard[dR][dC]!=null) {
			if (usedBoard[dR][dC].getColor().equals(usedBoard[pR][pC].getColor())) {
				return false;
			}
		}
		
		if (pR==dR && ((usedBoard[pR][pC].getColor().equals("White") && pR==7) || (usedBoard[pR][pC].getColor().equals("Black") && pR==0)) && pC==4 && dC==6) {
			return canCastleKingside(usedBoard[pR][pC].getColor());
		}
		
		if (pR==dR && ((usedBoard[pR][pC].getColor().equals("White") && pR==7) || (usedBoard[pR][pC].getColor().equals("Black") && pR==0)) && pC==4 && dC==2) {
			return canCastleQueenside(usedBoard[pR][pC].getColor());
		}
		
		piece[][] tempBoard = new piece[usedBoard.length][usedBoard[0].length];
		for (int r=0; r<tempBoard.length;r++) {
			for (int c=0; c<tempBoard[0].length;c++) {
				tempBoard[r][c] = usedBoard[r][c];
			}
		}
		tempBoard[dR][dC]=tempBoard[pR][pC];
		tempBoard[pR][pC]=null;
		if (tempBoard[dR][dC].getColor().equals("White")) {
			if (whiteInCheck(tempBoard,String.valueOf(dR)+String.valueOf(dC))) {
				return false;
			}
		}
		if (tempBoard[dR][dC].getColor().equals("Black")) {
			if (blackInCheck(tempBoard,String.valueOf(dR)+String.valueOf(dC))) {
				return false;
			}
		}
		
		if (dR==pR && Math.abs(pC-dC)==1) {
			return true;
		}
		
		if (dC==pC && Math.abs(pR-dR)==1) {
			return true;
		}
		
		if (Math.abs(dC-pC)==1 && Math.abs(dR-pR)==1) {
			return true;
		}
		
		
		
		
		return false;
	}
	
	public static boolean canCastleKingside(String kColor) {
		int kRow = 0;
		if (kColor.equals("White") && !canWhiteCastleKingside) {
			return false;
		}
		if (kColor.equals("Black") && !canBlackCastleKingside) {
			return false;
		}
		if (kColor.equals("White")) {
			if (whiteInCheck(board,whiteKingPosition) || whiteCheck) {
				return false;
			}
			kRow=7;
		}
		if (kColor.equals("Black")) {
			if (blackInCheck(board,blackKingPosition) || blackCheck) {
				return false;
			}
			kRow=0;
		}
		if (board[kRow][5]!=null || board[kRow][6]!=null) {
			return false;
		}
		
		piece[][] tempBoard1 = new piece[8][8];
		piece[][] tempBoard2 = new piece[8][8];
		for (int r=0; r<board.length;r++) {
			for (int c=0; c<board[0].length;c++) {
				tempBoard1[r][c]=board[r][c];
				tempBoard2[r][c]=board[r][c];
			}
		}
		tempBoard1[kRow][4]=null;
		tempBoard2[kRow][4]=null;
		tempBoard1[kRow][5]= new piece("King",kColor,0);
		tempBoard2[kRow][6]= new piece("King",kColor,0);
		if (kColor.equals("White")) {
			if (whiteInCheck(tempBoard1,String.valueOf(kRow)+String.valueOf(5))) {
				return false;
			}
			if (whiteInCheck(tempBoard2,String.valueOf(kRow)+String.valueOf(6))) {
				return false;
			}
		}
		if (kColor.equals("Black")) {
			if (blackInCheck(tempBoard1,String.valueOf(kRow)+String.valueOf(5))) {
				return false;
			}
			if (blackInCheck(tempBoard2,String.valueOf(kRow)+String.valueOf(6))) {
				return false;
			}
		}
		
		if (board[kRow][7]==null) {
			return false;
		}
		if (board[kRow][7]!=null) {
			if (board[kRow][7].getColor().equals(kColor) && board[kRow][7].getType().equals("Rook")) {
				
				return true;
			}
			else {
				return false;
			}
		}
		
		return false;
	}
	
	public static boolean canCastleQueenside(String kColor) {
		int kRow = 0;
		if (kColor.equals("White") && !canWhiteCastleQueenside) {
			return false;
		}
		if (kColor.equals("Black") && !canBlackCastleQueenside) {
			return false;
		}
		if (kColor.equals("White")) {
			if (whiteInCheck(board,whiteKingPosition) || whiteCheck) {
				return false;
			}
			kRow=7;
		}
		if (kColor.equals("Black")) {
			if (blackInCheck(board,blackKingPosition) || blackCheck) {
				return false;
			}
			kRow=0;
		}
		if (board[kRow][3]!=null || board[kRow][2]!=null || board[kRow][1]!=null) {
			return false;
		}
		
		piece[][] tempBoard1 = new piece[8][8];
		piece[][] tempBoard2 = new piece[8][8];
		piece[][] tempBoard3 = new piece[8][8];
		for (int r=0; r<board.length;r++) {
			for (int c=0; c<board[0].length;c++) {
				tempBoard1[r][c]=board[r][c];
				tempBoard2[r][c]=board[r][c];
				tempBoard3[r][c]=board[r][c];
			}
		}
		tempBoard1[kRow][4]=null;
		tempBoard2[kRow][4]=null;
		tempBoard3[kRow][4]=null;
		tempBoard1[kRow][1]= new piece("King",kColor,0);
		tempBoard2[kRow][2]= new piece("King",kColor,0);
		tempBoard3[kRow][3]= new piece("King",kColor,0);
		if (kColor.equals("White")) {
			if (whiteInCheck(tempBoard1,String.valueOf(kRow)+String.valueOf(1))) {
				return false;
			}
			if (whiteInCheck(tempBoard2,String.valueOf(kRow)+String.valueOf(2))) {
				return false;
			}
			if (whiteInCheck(tempBoard3,String.valueOf(kRow)+String.valueOf(3))) {
				return false;
			}
		}
		if (kColor.equals("Black")) {
			if (blackInCheck(tempBoard1,String.valueOf(kRow)+String.valueOf(1))) {
				return false;
			}
			if (blackInCheck(tempBoard2,String.valueOf(kRow)+String.valueOf(2))) {
				return false;
			}
			if (blackInCheck(tempBoard3,String.valueOf(kRow)+String.valueOf(3))) {
				return false;
			}
		}
		
		if (board[kRow][0]==null) {
			return false;
		}
		if (board[kRow][0]!=null) {
			if (board[kRow][0].getColor().equals(kColor) && board[kRow][0].getType().equals("Rook")) {
				
				return true;
			}
			else {
				return false;
			}
		}
		
		return false;
	}
	
	public static boolean pieceCanHitKing(int pR, int pC, int dR, int dC, piece[][] usedBoard) { // used for whiteInCheck and blackInCheck methods
		if (usedBoard[pR][pC].getType().equals("Pawn")) {
			if (usedBoard[pR][pC].getColor().equals("Black")) {
				if (dR==pR+1 && Math.abs(dC-pC)==1) {
					return true;
				}
			}
			if (usedBoard[pR][pC].getColor().equals("White")) {
				if (dR==pR-1 && Math.abs(dC-pC)==1) {
					return true;
				}
			}
			return false;
		}
		else if (usedBoard[pR][pC].getType().equals("Knight")) {
			if (usedBoard[dR][dC]!=null) {
				if (usedBoard[dR][dC].getColor().equals(usedBoard[pR][pC].getColor())) {
					return false;
				}
			}
			if (Math.abs(dC-pC)==1 && Math.abs(dR-pR)==2) {
				return true;
			}
			if (Math.abs(dC-pC)==2 && Math.abs(dR-pR)==1) {
				return true;
			}
			
			return false;
		}
		else if (usedBoard[pR][pC].getType().equals("Rook")) {
			if ((pC==dC && pR==dR) || (pC!=dC && pR!=dR)) {
				return false;
			}
			if (usedBoard[dR][dC]!=null) {
				if (usedBoard[dR][dC].getColor().equals(usedBoard[pR][pC].getColor())) {
					return false;
				}
			}
			if (pC==dC) {
				if (dR>pR) {
					int n=1;
					while (dR!=pR+n) {
						if (usedBoard[pR+n][pC]!=null) {
							return false;
						}
						n++;
					}
				}
				else if (dR<pR) {
					int n=1;
					while (dR!=pR-n) {
						if (usedBoard[pR-n][pC]!=null) {
							return false;
						}
						n++;
					}
				}
			}	
			if (pR==dR) {
				if (dC>pC) {
					int n=1;
					while (dC!=pC+n) {
						if (usedBoard[pR][pC+n]!=null) {
							return false;
						}
						n++;
					}
				}
				else if (dC<pC) {
					int n=1;
					while (dC!=pC-n) {
						if (usedBoard[pR][pC-n]!=null) {
							return false;
						}
						n++;
					}
				}
			}
			return true;
		}
		else if (usedBoard[pR][pC].getType().equals("Bishop")) {
			if (pC==dC && pR==dR) {
				return false;
			}
			if (Math.abs(pR-dR)!=Math.abs(pC-dC)) {
				return false;
			}
			if (usedBoard[dR][dC]!=null) {
				if (usedBoard[dR][dC].getColor().equals(usedBoard[pR][pC].getColor())) {
					return false;
				}
			}
			if (dC>pC && dR>pR) {
				int n = 1;
				while ((dC!=pC+n)) {
					if (usedBoard[pR+n][pC+n]!=null) {
						return false;
					}
					n++;
				}
			}
			if (dC>pC && dR<pR) {
				int n = 1;
				while ((dC!=pC+n)) {
					if (usedBoard[pR-n][pC+n]!=null) {
						return false;
					}
					n++;
				}
			}
			if (dC<pC && dR<pR) {
				int n = 1;
				while ((dC!=pC-n)) {
					if (usedBoard[pR-n][pC-n]!=null) {
						return false;
					}
					n++;
				}
			}
			if (dC<pC && dR>pR) {
				int n = 1;
				while ((dC!=pC-n)) {
					if (usedBoard[pR+n][pC-n]!=null) {
						return false;
					}
					n++;
				}
			}
			return true;
		}
		else if (usedBoard[pR][pC].getType().equals("Queen")) {
			if (pC==dC && pR==dR) {
				return false;
			}
			
			if (Math.abs(dC-pC)==Math.abs(pR-dR)) {
				if (pC==dC && pR==dR) {
					return false;
				}
				if (Math.abs(pR-dR)!=Math.abs(pC-dC)) {
					return false;
				}
				if (usedBoard[dR][dC]!=null) {
					if (usedBoard[dR][dC].getColor().equals(usedBoard[pR][pC].getColor())) {
						return false;
					}
				}
				if (dC>pC && dR>pR) {
					int n = 1;
					while ((dC!=pC+n)) {
						if (usedBoard[pR+n][pC+n]!=null) {
							return false;
						}
						n++;
					}
				}
				if (dC>pC && dR<pR) {
					int n = 1;
					while ((dC!=pC+n)) {
						if (usedBoard[pR-n][pC+n]!=null) {
							return false;
						}
						n++;
					}
				}
				if (dC<pC && dR<pR) {
					int n = 1;
					while ((dC!=pC-n)) {
						if (usedBoard[pR-n][pC-n]!=null) {
							return false;
						}
						n++;
					}
				}
				if (dC<pC && dR>pR) {
					int n = 1;
					while ((dC!=pC-n)) {
						if (usedBoard[pR+n][pC-n]!=null) {
							return false;
						}
						n++;
					}
				}
				return true;
			}
			
			else if ((pC==dC && pR!=dR) || (pR==dR && pC!=dC)) {
				if ((pC==dC && pR==dR) || (pC!=dC && pR!=dR)) {
					return false;
				}
				if (usedBoard[dR][dC]!=null) {
					if (usedBoard[dR][dC].getColor().equals(usedBoard[pR][pC].getColor())) {
						return false;
					}
				}
				if (pC==dC) {
					if (dR>pR) {
						int n=1;
						while (dR!=pR+n) {
							if (usedBoard[pR+n][pC]!=null) {
								return false;
							}
							n++;
						}
					}
					else if (dR<pR) {
						int n=1;
						while (dR!=pR-n) {
							if (usedBoard[pR-n][pC]!=null) {
								return false;
							}
							n++;
						}
					}
				}	
				if (pR==dR) {
					if (dC>pC) {
						int n=1;
						while (dC!=pC+n) {
							if (usedBoard[pR][pC+n]!=null) {
								return false;
							}
							n++;
						}
					}
					else if (dC<pC) {
						int n=1;
						while (dC!=pC-n) {
							if (usedBoard[pR][pC-n]!=null) {
								return false;
							}
							n++;
						}
					}
				}
				return true;
			}
			
			
			
			
			return false;
		}
		
		return false;
	}
	
	public static boolean whiteInCheck(piece[][] usedBoard, String currentWhiteKingPosition) {
		int kR = Integer.parseInt(currentWhiteKingPosition.substring(0,1));
		int kC = Integer.parseInt(currentWhiteKingPosition.substring(1,2));
		
		for (int r=0; r<usedBoard.length;r++) {
			for (int c=0; c<usedBoard[0].length;c++) {
				if (usedBoard[r][c]!=null) {
					if (usedBoard[r][c].getColor().equals("Black")) {
						if (pieceCanHitKing(r,c,kR,kC,usedBoard)) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public static boolean blackInCheck(piece[][] usedBoard, String currentBlackKingPosition) {
		int kR = Integer.parseInt(currentBlackKingPosition.substring(0,1));
		int kC = Integer.parseInt(currentBlackKingPosition.substring(1,2));
		
		for (int r=0; r<usedBoard.length;r++) {
			for (int c=0; c<usedBoard[0].length;c++) {
				if (usedBoard[r][c]!=null) {
					if (usedBoard[r][c].getColor().equals("White")) {
						if (pieceCanHitKing(r,c,kR,kC,usedBoard)) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public static void testForGameOver(piece[][] usedBoard, boolean isWhitesTurn) {
		gameOver = true;
		boolean doesWhiteHaveValidMove = true, doesBlackHaveValidMove = true;
		if (isWhitesTurn) {
			test_if_white_has_valid_move_loop:
				for (int pR=0; pR<usedBoard.length;pR++) {
					for (int pC=0; pC<usedBoard[0].length;pC++) {
						if (usedBoard[pR][pC]!=null) {
							if (usedBoard[pR][pC].getColor().equals("White")) {
								if (usedBoard[pR][pC].getType().equals("Pawn")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidPawnMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_white_has_valid_move_loop;
											}
										}
									}
								}
								if (usedBoard[pR][pC].getType().equals("Knight")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidKnightMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_white_has_valid_move_loop;
											}
										}
									}
								}
								if (usedBoard[pR][pC].getType().equals("Bishop")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidBishopMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_white_has_valid_move_loop;
											}
										}
									}
								}
								if (usedBoard[pR][pC].getType().equals("Rook")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidRookMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_white_has_valid_move_loop;
											}
										}
									}
								}
								if (usedBoard[pR][pC].getType().equals("Queen")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidPawnMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_white_has_valid_move_loop;
											}
										}
									}
								}
								if (usedBoard[pR][pC].getType().equals("King")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidKingMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_white_has_valid_move_loop;
											}
										}
									}
								}
								doesWhiteHaveValidMove = false;
							}
						}
					}
				}
		}
		if (!isWhitesTurn) {
			test_if_black_has_valid_move_loop:
				for (int pR=0; pR<usedBoard.length;pR++) {
					for (int pC=0; pC<usedBoard[0].length;pC++) {
						if (usedBoard[pR][pC]!=null) {
							if (usedBoard[pR][pC].getColor().equals("Black")) {
								if (usedBoard[pR][pC].getType().equals("Pawn")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidPawnMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_black_has_valid_move_loop;
											}
										}
									}
								}
								if (usedBoard[pR][pC].getType().equals("Knight")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidKnightMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_black_has_valid_move_loop;
											}
										}
									}
								}
								if (usedBoard[pR][pC].getType().equals("Bishop")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidBishopMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_black_has_valid_move_loop;
											}
										}
									}
								}
								if (usedBoard[pR][pC].getType().equals("Rook")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidRookMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_black_has_valid_move_loop;
											}
										}
									}
								}
								if (usedBoard[pR][pC].getType().equals("Queen")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidPawnMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_black_has_valid_move_loop;
											}
										}
									}
								}
								if (usedBoard[pR][pC].getType().equals("King")) {
									for (int r=0; r<board.length;r++) {
										for (int c=0; c<board[0].length;c++) {
											if (isValidKingMove(pR,pC,r,c,usedBoard)) {
												gameOver = false;
												break test_if_black_has_valid_move_loop;
											}
										}
									}
								}
								doesBlackHaveValidMove = false;
							}
						}
					}
				}
		}
		
		if (gameOver) {
			if (isWhitesTurn && !doesWhiteHaveValidMove) {
				if (whiteCheck) {
					whiteInCheckmate = true;
				}
				else {
					stalemate = true;
				}
			}
			else if (!isWhitesTurn && !doesBlackHaveValidMove) {
				gameOver = true;
				if (blackCheck) {
					blackInCheckmate = true;
				}
				else {
					stalemate = true;
				}
			}
			else if ((!isWhitesTurn && !doesWhiteHaveValidMove) || (!isWhitesTurn && !doesBlackHaveValidMove)) {
				if (!whiteCheck && !blackCheck) {
					stalemate = true;
				}
			}
		}
		
		
		
	}
	
	public void processKeyEvent(KeyEvent e) {
		if ( e.getID() == KeyEvent.KEY_PRESSED ) {
			if (e.getKeyCode()==KeyEvent.VK_R) {
				for (int r=0; r<board.length;r++) {
					for (int c=0; c<board[0].length;c++) {
						board[r][c]=null;
					}
				}
				for (int c=0; c<board[0].length;c++) {
					board[1][c] = new piece("Pawn","Black",1);
					board[6][c] = new piece("Pawn","White",1);
				}
				board[0][0] = new piece("Rook","Black",5);
				board[0][7] = new piece("Rook","Black",5);
				board[7][0] = new piece("Rook","White",5);
				board[7][7] = new piece("Rook","White",5);
				board[0][1] = new piece("Knight","Black",3);
				board[0][6] = new piece("Knight","Black",3);
				board[7][1] = new piece("Knight","White",3);
				board[7][6] = new piece("Knight","White",3);
				board[0][2] = new piece("Bishop","Black",3);
				board[0][5] = new piece("Bishop","Black",3);
				board[7][2] = new piece("Bishop","White",3);
				board[7][5] = new piece("Bishop","White",3);
				board[0][3] = new piece("Queen","Black",9);
				board[7][3] = new piece("Queen","White",9);
				
				board[0][4] = new piece("King","Black",0);
				blackKingPosition = "04";
				board[7][4] = new piece("King","White",0);
				whiteKingPosition = "74";
				
				selectedPieceRow=-1;
				selectedPieceCol=-1;
				desiredRow=-1;
				desiredCol=-1;
				isWhitesTurn = true;
				gameOver = false;
				
				boardHistory.clear();
				boardHistory.add(new boardInstance(board, true, whiteKingPosition, blackKingPosition));
				moveCount=0;
				
				whiteCheck = whiteInCheck(board, whiteKingPosition);
			    blackCheck = blackInCheck(board, blackKingPosition);
			    
			    canWhiteCastleKingside = true;
				canBlackCastleKingside = true;
				canWhiteCastleQueenside = true;
				canBlackCastleQueenside = true;
			    
			    repaint();
			}
			if (e.getKeyCode()==KeyEvent.VK_Z) {
				if (moveCount>0) {
					moveCount--;
					for (int r=0; r<board.length;r++) {
						boardHistory.get(moveCount);
						System.arraycopy(boardInstance.getBoardCopy()[r], 0, board[r], 0, 8);;
					}
					isWhitesTurn = boardHistory.get(moveCount).isWhiteTurn();
					whiteKingPosition = boardHistory.get(moveCount).getWhiteKingPos();
					blackKingPosition = boardHistory.get(moveCount).getBlackKingPos();
					boardHistory.remove(boardHistory.size()-1);
					
					selectedPieceRow=-1;
					selectedPieceCol=-1;
					desiredRow=-1;
					desiredCol=-1;
					gameOver = false;
					
					whiteCheck = whiteInCheck(board, whiteKingPosition);
				    blackCheck = blackInCheck(board, blackKingPosition);
					repaint();
				}
			}
		}
	}
	
	private void playSound() {
		String capture = "resources/capture.mp3";
		Media hit = new Media(new File(capture).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(hit);
		mediaPlayer.play();
	}

}






