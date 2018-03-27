/*
ChessRuler knows every rule of the chess game
*/

package chessApplicationPackage;

import java.awt.Point;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ChessRuler extends Thread
{
	int alphaTemp,betaTemp; // for optimization
	private static final int maximumPossibleMoves=200;
	private int counterMultithreading;
	public int totalCounter;
	private int currentTurnMultithreading;
	private int beginIndexMultithreading;
	private int endIndexMultithreading;
	private int depthGapForQuiescence=3;
	private int depthForThreadComputing;
	private boolean useQuiescenceMultithreading;
	static ArrayList<Integer> listSourceForMultithreading=new ArrayList<Integer>();
	static int listValuesForMultithreading[];
	static ArrayList<Integer> listDestinationForMultithreading=new ArrayList<Integer>();
	private static final int maximumOccurrenceForASituation=3;
	private Date gameBeginningDate;
	private ArrayList<PiecesSituation> listPiecesSituation;
	private ArrayList<Integer> listPiecesSituationOccurrences;
	int counterMoveFinished;
	private static final int numberOfSquarePerLine=8;
	private int currentTurn;
	private static final int white=1;
	private static final int black=-white;
	private static final int whiteIsPat=2*white;
	private static final int blackIsPat=2*black;
	private static final int noCurrentGame=0;
	private static final int noPieceId=0;
	private static final int pawnId=1000;
	private static final int knightId=2400;
	private static final int bishopId=4000;
	private static final int rookId=6400;
	private static final int queenId=10400;
	private static final int kingId=10000000;
	private static final int kingIdValue=4000;
	private static final int infinite=1000000000;
	
	// pawn promotion
	public static final String promotionExplicit="promotion";
	public static final String promotionStandard="=Q";
	private static final String checkDescription="check";
	
	// for castlings
	public long whiteKingCastlingMask;
	public long whiteQueenCastlingMask;
	public long blackKingCastlingMask;
	public long blackQueenCastlingMask;
	public static final int beginBlackQueenCastling=1;
	public static final int endBlackQueenCastling=3;
	public static final int beginBlackKingCastling=5;
	public static final int endBlackKingCastling=6;
	public static final int beginWhiteQueenCastling=57;
	public static final int endWhiteQueenCastling=59;
	public static final int beginWhiteKingCastling=61;
	public static final int endWhiteKingCastling=62;
	public static final int blackKingQueenCastlingDestination=2;
	public static final int blackRookQueenCastlingDestination=3;
	public static final int blackKingKingCastlingDestination=6;
	public static final int blackRookKingCastlingDestination=5;
	public static final int whiteKingQueenCastlingDestination=58;
	public static final int whiteRookQueenCastlingDestination=59;
	public static final int whiteKingKingCastlingDestination=62;
	public static final int whiteRookKingCastlingDestination=61;
	public static final String kingSideCastlingStandard="0-0";
	public static final String kingSideCastlingExplicit="kingside castling";
	public static final String queenSideCastlingStandard="0-0-0";
	public static final String queenSideCastlingExplicit="queenside castling";
	public boolean isWhiteKingHasMoved;
	public boolean isBlackKingHasMoved;
	public boolean isBlackLeftRookHasMoved;
	public boolean isBlackRightRookHasMoved;
	public boolean isWhiteLeftRookHasMoved;
	public boolean isWhiteRightRookHasMoved;
	
	// en passant
	public String enPassantExplicit="en passant";
	public String enPassantStandard="e.p";
	public String enPassantReducedForAnalysis="ep";
	
	// all the longs for each type of piece
	public long whiteRooks;
	public long whiteKnights;
	public long whiteBishops;
	public long whiteQueens;
	public long whiteKing;
	public long whitePawns;
	public long blackRooks;
	public long blackKnights;
	public long blackBishops;
	public long blackQueens;
	public long blackKing;
	public long blackPawns;
	
	private static long arrayLinesMask[];
	private static long arrayDiagonalsMask[];
	private static long knightMoves[];
	private static long kingMoves[];
	private static long diagonalsExtrems[];
	private static long linesExtrems[];
	private static HashMap<Long,Long> hashMapLinesPossibilities;
	private static HashMap<Long,Long> hashMapDiagonalsPossibilities;
	
	// there are all the coordinates for a standard game with the white at bottom 
	public static final Point leftWhiteBishopInitialPosition=new Point(2,7);
	public static final Point rightWhiteBishopInitialPosition=new Point(5,7);
	public static final Point leftWhiteKnightInitialPosition=new Point(1,7);
	public static final Point rightWhiteKnightInitialPosition=new Point(6,7);
	public static final Point leftWhiteRookInitialPosition=new Point(0,7);
	public static final Point rightWhiteRookInitialPosition=new Point(7,7);
	public static final Point whiteQueenInitialPosition=new Point(3,7);
	public static final Point whiteKingInitialPosition=new Point(4,7);
	public static final Point firstLeftWhitePawnsInitialPosition=new Point(0,6);
	public static final Point leftBlackRookInitialPosition=new Point(0,0);
	public static final Point rightBlackRookInitialPosition=new Point(7,0);
	public static final Point leftBlackKnightInitialPosition=new Point(1,0);
	public static final Point rightBlackKnightInitialPosition=new Point(6,0);
	public static final Point leftBlackBishopInitialPosition=new Point(2,0);
	public static final Point rightBlackBishopInitialPosition=new Point(5,0);
	public static final Point blackQueenInitialPosition=new Point(3,0);
	public static final Point blackKingInitialPosition=new Point(4,0);
	public static final Point firstLeftBlackPawnsInitialPosition=new Point(0,1);
	
	public void displayLong(long longParameter)
	{
		System.out.println("-------------- longParameter : "+longParameter+" --------------");
		for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
		{
			for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
			{
				if((longParameter&(1L<<(counterVertical*numberOfSquarePerLine+counterHorizontal)))!=0)
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.println("");
		}
	}
	
	public void displayLong(long longParameter,String description)
	{
		System.out.println("-------------- longParameter : "+longParameter+" --------------  description : "+description+" --------------");
		for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
		{
			for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
			{
				if((longParameter&(1L<<(counterVertical*numberOfSquarePerLine+counterHorizontal)))!=0)
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.println("");
		}
	}
	
	public long rotateHeightBitHorizontalToVertical(long longParameter,long index)
	{
		long longReturn=0;
		for(int counterBits=0;counterBits<6;counterBits++)
			if((longParameter&(1L<<counterBits))!=0)
				longReturn=longReturn|1L<<(index+counterBits*numberOfSquarePerLine);
		return longReturn;
	}
	
	public long getDiagonalsExtremes(long pieceIndex)
	{
		long extremes=0;
		extremes|=1L<<pieceIndex;
		// Extreme values for diagonal mask
		if(pieceIndex%numberOfSquarePerLine+pieceIndex/numberOfSquarePerLine<numberOfSquarePerLine) // left top triangle of the chess board
		{
			extremes|=1L<<pieceIndex%numberOfSquarePerLine+pieceIndex/numberOfSquarePerLine; // top right on the top border
			extremes|=1L<<(pieceIndex%numberOfSquarePerLine+pieceIndex/numberOfSquarePerLine)*numberOfSquarePerLine; // bottom left on left border
		}
		if((numberOfSquarePerLine-pieceIndex%numberOfSquarePerLine)+pieceIndex/numberOfSquarePerLine<=numberOfSquarePerLine) //  right top triangle on the top border
		{
			extremes|=1L<<pieceIndex%numberOfSquarePerLine-pieceIndex/numberOfSquarePerLine; // top left on the top border
			extremes|=1L<<(pieceIndex/numberOfSquarePerLine+(numberOfSquarePerLine-pieceIndex%numberOfSquarePerLine-1))*numberOfSquarePerLine+numberOfSquarePerLine-1; // right bottom on the right border
		}
		if((numberOfSquarePerLine-pieceIndex/numberOfSquarePerLine-1)+(numberOfSquarePerLine-pieceIndex%numberOfSquarePerLine-1)<numberOfSquarePerLine) // right bottom triangle of the chess board
		{
			extremes|=1L<<(pieceIndex/numberOfSquarePerLine-(numberOfSquarePerLine-pieceIndex%numberOfSquarePerLine-1))*numberOfSquarePerLine+numberOfSquarePerLine-1; // top right on right border
			extremes|=1L<<(pieceIndex%numberOfSquarePerLine)+(numberOfSquarePerLine*(numberOfSquarePerLine-1))-(numberOfSquarePerLine-pieceIndex/numberOfSquarePerLine)+1; // bottom left on bottom border
		}
		
		if(pieceIndex%numberOfSquarePerLine+(numberOfSquarePerLine-pieceIndex/numberOfSquarePerLine)<numberOfSquarePerLine) // left bottom triangle of the chess board
		{
			extremes|=1L<<pieceIndex%numberOfSquarePerLine+(numberOfSquarePerLine-pieceIndex/numberOfSquarePerLine)+(numberOfSquarePerLine*(numberOfSquarePerLine-1))-1; // bottom right on bottom border
			extremes|=1L<<(pieceIndex/numberOfSquarePerLine-(pieceIndex%numberOfSquarePerLine))*numberOfSquarePerLine; // left tom on right border
		}
		return extremes;
	}
	
	public ChessRuler()
	{
		// initiate castling bitboards
		blackQueenCastlingMask=0;
		for(int counterIndexCastling=beginBlackQueenCastling;counterIndexCastling<=endBlackQueenCastling;counterIndexCastling++)
			blackQueenCastlingMask|=1L<<counterIndexCastling;
		blackKingCastlingMask=0;
		for(int counterIndexCastling=beginBlackKingCastling;counterIndexCastling<=endBlackKingCastling;counterIndexCastling++)
			blackKingCastlingMask|=1L<<counterIndexCastling;
		whiteQueenCastlingMask=0;
		for(int counterIndexCastling=beginWhiteQueenCastling;counterIndexCastling<=endWhiteQueenCastling;counterIndexCastling++)
			whiteQueenCastlingMask|=1L<<counterIndexCastling;
		whiteKingCastlingMask=0;
		for(int counterIndexCastling=beginWhiteKingCastling;counterIndexCastling<=endWhiteKingCastling;counterIndexCastling++)
			whiteKingCastlingMask|=1L<<counterIndexCastling;
		
		linesExtrems=new long[numberOfSquarePerLine*numberOfSquarePerLine];
		diagonalsExtrems=new long[numberOfSquarePerLine*numberOfSquarePerLine];
		arrayLinesMask=new long[numberOfSquarePerLine*numberOfSquarePerLine];
		arrayDiagonalsMask=new long[numberOfSquarePerLine*numberOfSquarePerLine];
		knightMoves=new long[numberOfSquarePerLine*numberOfSquarePerLine];
		kingMoves=new long[numberOfSquarePerLine*numberOfSquarePerLine];
		hashMapLinesPossibilities=new HashMap<Long,Long>();
		hashMapDiagonalsPossibilities=new HashMap<Long,Long>();
		for(int counterBits=0;counterBits<numberOfSquarePerLine*numberOfSquarePerLine;counterBits++)
		{
			int horizontalPosition=counterBits%numberOfSquarePerLine;
			int verticalPosition=counterBits/numberOfSquarePerLine;
			
			long boardWithFilter=0;
			if(counterBits%numberOfSquarePerLine>1) // first left-top and second bottom-right
			{
				if(counterBits/numberOfSquarePerLine>0)
					boardWithFilter|=1L<<counterBits-numberOfSquarePerLine-2;
				if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-1)
					boardWithFilter|=1L<<counterBits+numberOfSquarePerLine-2;
			}
			if(counterBits/numberOfSquarePerLine>1) // second left-bottom and first top-right
			{
				if(counterBits%numberOfSquarePerLine>0)
					boardWithFilter|=1L<<counterBits-2*numberOfSquarePerLine-1;
				if(counterBits%numberOfSquarePerLine<numberOfSquarePerLine-1)
					boardWithFilter|=1L<<counterBits-2*numberOfSquarePerLine+1;
			}
			if(counterBits%numberOfSquarePerLine<numberOfSquarePerLine-2) // second right-top and first bottom-right
			{
				if(counterBits/numberOfSquarePerLine>0)
					boardWithFilter|=1L<<counterBits-numberOfSquarePerLine+2;
				if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-1)
					boardWithFilter|=1L<<counterBits+numberOfSquarePerLine+2;
			}
			if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-2) // first left-bottom and second bottom-right
			{
				if(counterBits%numberOfSquarePerLine>0)
					boardWithFilter|=1L<<counterBits+2*numberOfSquarePerLine-1;
				if(counterBits%numberOfSquarePerLine<numberOfSquarePerLine-1)
					boardWithFilter|=1L<<counterBits+2*numberOfSquarePerLine+1;
			}
			knightMoves[counterBits]=boardWithFilter;
			
			// we have to compute king moves now
			boardWithFilter=0;
			
			// top-middle
			if(counterBits/numberOfSquarePerLine>0)
				boardWithFilter|=1L<<counterBits-numberOfSquarePerLine;
			
			// bottom-middle
			if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-1)
				boardWithFilter|=1L<<counterBits+numberOfSquarePerLine;
			
			// left : top, middle and bottom
			if(counterBits%numberOfSquarePerLine>0)
			{
				boardWithFilter|=1L<<counterBits-1;
				if(counterBits/numberOfSquarePerLine>0)
					boardWithFilter|=1L<<counterBits-numberOfSquarePerLine-1;
				if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-1)
					boardWithFilter|=1L<<counterBits+numberOfSquarePerLine-1;
			}
			
			// right : top, middle and bottom
			if((counterBits+1)%numberOfSquarePerLine!=0)
			{
				boardWithFilter|=1L<<counterBits+1;
				if(counterBits/numberOfSquarePerLine>0)
					boardWithFilter|=1L<<counterBits-numberOfSquarePerLine+1;
				if(counterBits/numberOfSquarePerLine<numberOfSquarePerLine-1)
					boardWithFilter|=1L<<counterBits+numberOfSquarePerLine+1;
			}
			kingMoves[counterBits]=boardWithFilter;
			
			// lines for rooks and queens
			for(int counterHorizontal=1;counterHorizontal<numberOfSquarePerLine-1;counterHorizontal++)
				arrayLinesMask[counterBits]=arrayLinesMask[counterBits]|(1L<<(verticalPosition*numberOfSquarePerLine)+counterHorizontal);
			for(int counterVertical=1;counterVertical<numberOfSquarePerLine-1;counterVertical++)
				arrayLinesMask[counterBits]=arrayLinesMask[counterBits]|(1L<<(counterVertical*numberOfSquarePerLine)+horizontalPosition);
			
			// diagonals for bishops and queens
			long lineMask=0;
			lineMask|=1L<<counterBits;
			// from top-left to bottom-right 
			int leftTopHorizontalInsertion=horizontalPosition;
			int leftTopVerticalInsertion=verticalPosition;
			for(;leftTopHorizontalInsertion>0&&leftTopVerticalInsertion>0;leftTopHorizontalInsertion--,leftTopVerticalInsertion--)
				;
			int counterVertical=leftTopVerticalInsertion;
			for(int counterHorizontal=leftTopHorizontalInsertion;counterHorizontal<numberOfSquarePerLine&&counterVertical<numberOfSquarePerLine;counterHorizontal++,counterVertical++)
				lineMask|=1L<<counterHorizontal+counterVertical*numberOfSquarePerLine;
			
			// from bottom-left to top-right
			int leftBottomHorizontalInsertion=horizontalPosition;
			int leftBottomVerticalInsertion=verticalPosition;
			for(;leftBottomHorizontalInsertion>0&&leftBottomVerticalInsertion<numberOfSquarePerLine-1;leftBottomHorizontalInsertion--,leftBottomVerticalInsertion++)
				;
			counterVertical=leftBottomVerticalInsertion;
			for(int counterHorizontal=leftBottomHorizontalInsertion;counterHorizontal<numberOfSquarePerLine&&counterVertical>=0;counterHorizontal++,counterVertical--)
				lineMask|=1L<<counterHorizontal+counterVertical*numberOfSquarePerLine;
			arrayDiagonalsMask[counterBits]=lineMask;
			
			for(long counterHorizontalPossibilities=0;counterHorizontalPossibilities<Math.pow(2,6);counterHorizontalPossibilities++)
			{
				for(long counterVerticalPossibilities=0;counterVerticalPossibilities<Math.pow(2,6);counterVerticalPossibilities++)
				{
					// we put the four boardWithFilters to identify clearly where the rook/queen is
					boardWithFilter=0;
					boardWithFilter|=1L<<horizontalPosition;
					boardWithFilter|=1L<<(horizontalPosition+(numberOfSquarePerLine*(numberOfSquarePerLine-1)));
					boardWithFilter|=1L<<verticalPosition*numberOfSquarePerLine;
					boardWithFilter|=1L<<(verticalPosition*numberOfSquarePerLine+numberOfSquarePerLine-1);
					linesExtrems[counterBits]=boardWithFilter;
					boardWithFilter|=counterHorizontalPossibilities<<(verticalPosition*numberOfSquarePerLine)+1;
					long verticalCounter=rotateHeightBitHorizontalToVertical(counterVerticalPossibilities,0); // the vertical counter with the right pattern
					verticalCounter=verticalCounter<<numberOfSquarePerLine;
					verticalCounter=verticalCounter<<horizontalPosition;
					boardWithFilter=boardWithFilter|verticalCounter;
					if(hashMapLinesPossibilities.get(boardWithFilter)==null)
					{
						long movesResult=0;
						for(long counterRight=counterBits+1;counterRight%numberOfSquarePerLine!=0;counterRight++) // right
							if(((counterHorizontalPossibilities<<1)&(1L<<counterRight%numberOfSquarePerLine))!=0)
							{
								movesResult=movesResult|1L<<counterRight;
								break;
							}
							else
								movesResult=movesResult|1L<<counterRight;
						for(long counterBottom=counterBits+numberOfSquarePerLine;counterBottom<numberOfSquarePerLine*numberOfSquarePerLine;counterBottom+=numberOfSquarePerLine) // bottom
							if(((verticalCounter)&(1L<<counterBottom))!=0)
							{
								movesResult=movesResult|1L<<counterBottom;
								break;
							}
							else
								movesResult=movesResult|1L<<counterBottom;
						for(long counterLeft=counterBits%numberOfSquarePerLine-1;counterLeft>=0;counterLeft--) // left
							if(((counterHorizontalPossibilities<<1)&(1L<<counterLeft))!=0)
							{
								movesResult=movesResult|1L<<verticalPosition*numberOfSquarePerLine+counterLeft;
								break;
							}
							else
								movesResult=movesResult|1L<<verticalPosition*numberOfSquarePerLine+counterLeft;
						for(long counterTop=counterBits-numberOfSquarePerLine;counterTop>=0;counterTop-=numberOfSquarePerLine) // top
							if(((verticalCounter)&(1L<<counterTop))!=0)
							{
								movesResult=movesResult|1L<<counterTop;
								break;
							}
							else
								movesResult=movesResult|1L<<counterTop;
						hashMapLinesPossibilities.put(boardWithFilter,movesResult);
					}
					
					// now we compute diagonals
					boardWithFilter=0;
					boardWithFilter|=1L<<counterBits;
					
					// before all we have to go to the most top left
					int counterBackTrackTopLeft=0;
					for(counterBackTrackTopLeft=counterBits;;counterBackTrackTopLeft-=(numberOfSquarePerLine+1))
						if(counterBackTrackTopLeft<numberOfSquarePerLine||counterBackTrackTopLeft%numberOfSquarePerLine==0)
							break;
						
					// we put the bit in top-left bottom-right
					for(int counterBitsTopLeftBottomRight=1;counterBitsTopLeftBottomRight<=6;counterBitsTopLeftBottomRight++)
					{
						if((counterBackTrackTopLeft+counterBitsTopLeftBottomRight+counterBitsTopLeftBottomRight*numberOfSquarePerLine)%numberOfSquarePerLine==0)
							break;
						if((counterBackTrackTopLeft+counterBitsTopLeftBottomRight+counterBitsTopLeftBottomRight*numberOfSquarePerLine)/numberOfSquarePerLine==numberOfSquarePerLine)
							break;
						if((counterHorizontalPossibilities&1L<<(counterBitsTopLeftBottomRight-1))!=0)
							boardWithFilter|=1L<<counterBackTrackTopLeft+counterBitsTopLeftBottomRight+counterBitsTopLeftBottomRight*numberOfSquarePerLine;
						else
							boardWithFilter|=0L<<counterBackTrackTopLeft+counterBitsTopLeftBottomRight+counterBitsTopLeftBottomRight*numberOfSquarePerLine;
					}
					
					// before all we have to go to the most top right
					int counterBackTrackTopRight=0;
					for(counterBackTrackTopRight=counterBits;;counterBackTrackTopRight-=(numberOfSquarePerLine-1))
						if(counterBackTrackTopRight<numberOfSquarePerLine||counterBackTrackTopRight%numberOfSquarePerLine==(numberOfSquarePerLine-1))
							break;
						
					// we put the bit in top-right bottom-left
					for(int counterBitsRightTopBottomLeft=1;counterBitsRightTopBottomLeft<=6;counterBitsRightTopBottomLeft++)
					{
						if((counterBackTrackTopRight-counterBitsRightTopBottomLeft+counterBitsRightTopBottomLeft*numberOfSquarePerLine)%numberOfSquarePerLine==0)
							break;
						if((counterBackTrackTopRight-counterBitsRightTopBottomLeft+counterBitsRightTopBottomLeft*numberOfSquarePerLine)/numberOfSquarePerLine==numberOfSquarePerLine)
							break;
						if((counterVerticalPossibilities&1L<<(counterBitsRightTopBottomLeft-1))!=0)
							boardWithFilter|=1L<<counterBackTrackTopRight-counterBitsRightTopBottomLeft+counterBitsRightTopBottomLeft*numberOfSquarePerLine;
						else
							boardWithFilter|=0L<<counterBackTrackTopRight-counterBitsRightTopBottomLeft+counterBitsRightTopBottomLeft*numberOfSquarePerLine;
					}
					
					// now we compute the available moves for diagonals
					// top left
					long movesResult=0;
					counterVertical=verticalPosition-1;
					int counterHorizontal=horizontalPosition-1;
					for(;counterVertical>=0&&counterHorizontal>=0;counterHorizontal--,counterVertical--)
					{
						movesResult|=1L<<(counterHorizontal+counterVertical*numberOfSquarePerLine);
						if((boardWithFilter&1L<<(counterHorizontal+counterVertical*numberOfSquarePerLine))!=0)
							break;
					}
					
					// bottom-right
					counterVertical=verticalPosition+1;
					counterHorizontal=horizontalPosition+1;
					for(;counterVertical<numberOfSquarePerLine&&counterHorizontal<numberOfSquarePerLine;counterHorizontal++,counterVertical++)
					{
						movesResult|=1L<<(counterHorizontal+counterVertical*numberOfSquarePerLine);
						if((boardWithFilter&1L<<(counterHorizontal+counterVertical*numberOfSquarePerLine))!=0)
							break;
					}
					
					// top-right
					counterVertical=verticalPosition-1;
					counterHorizontal=horizontalPosition+1;
					for(;counterVertical>=0&&counterHorizontal<numberOfSquarePerLine;counterHorizontal++,counterVertical--)
					{
						movesResult|=1L<<(counterHorizontal+counterVertical*numberOfSquarePerLine);
						if((boardWithFilter&1L<<(counterHorizontal+counterVertical*numberOfSquarePerLine))!=0)
							break;
					}
					
					// bottom-left
					counterVertical=verticalPosition+1;
					counterHorizontal=horizontalPosition-1;
					for(;counterVertical<numberOfSquarePerLine&&counterHorizontal>=0;counterHorizontal--,counterVertical++)
					{
						movesResult|=1L<<(counterHorizontal+counterVertical*numberOfSquarePerLine);
						if((boardWithFilter&1L<<(counterHorizontal+counterVertical*numberOfSquarePerLine))!=0)
							break;
					}
					
					diagonalsExtrems[counterBits]=getDiagonalsExtremes(counterBits); // speed improvement with direct correspondance
					boardWithFilter|=diagonalsExtrems[counterBits];
					if(counterBits==numberOfSquarePerLine-1||counterBits==0)
						boardWithFilter&=~(1L<<counterBits);
					hashMapDiagonalsPossibilities.put(boardWithFilter,movesResult);
				}
			}
		}
		initializeNewGame();
	}
	
	// we set all the pieces with their right position
	public void initializeNewGame()
	{
		listPiecesSituation=new ArrayList<PiecesSituation>();
		listPiecesSituationOccurrences=new ArrayList<Integer>();
		counterMoveFinished=0;
		currentTurn=white;
		blackBishops=0;
		blackRooks=0;
		
		/*	to simulate blind effect
			blackRooks=blackRooks|(1L<<16);
			blackRooks=blackRooks|(1L<<23);		
			blackRooks=blackRooks|(1L<<32);
			blackRooks=blackRooks|(1L<<39);		
			blackKing=0;
			blackKing=blackKing|(1L<<blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine);		
			whiteKing=0;
			whiteKing=whiteKing|(1L<<28);		
			blackPawns=0;
			blackPawns|=1L<<44;		
			*/
		
		blackRooks=blackRooks|(1L<<leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*numberOfSquarePerLine);
		blackRooks=blackRooks|(1L<<rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*numberOfSquarePerLine);
		blackBishops=blackBishops|(1L<<leftBlackBishopInitialPosition.x+leftBlackBishopInitialPosition.y*numberOfSquarePerLine);
		blackBishops=blackBishops|(1L<<rightBlackBishopInitialPosition.x+rightBlackBishopInitialPosition.y*numberOfSquarePerLine);
		blackKnights=0;
		blackKnights=blackKnights|(1L<<leftBlackKnightInitialPosition.x+leftBlackKnightInitialPosition.y*numberOfSquarePerLine);
		blackKnights=blackKnights|(1L<<rightBlackKnightInitialPosition.x+rightBlackKnightInitialPosition.y*numberOfSquarePerLine);
		blackQueens=0;
		blackQueens=blackQueens|(1L<<blackQueenInitialPosition.x+blackQueenInitialPosition.y*numberOfSquarePerLine);
		blackKing=0;
		blackKing=blackKing|(1L<<blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine);
		blackPawns=0;
		for(int counterHorizontal=firstLeftBlackPawnsInitialPosition.x;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
			blackPawns|=1L<<(counterHorizontal+firstLeftBlackPawnsInitialPosition.y*numberOfSquarePerLine);
		whiteRooks=0;
		whiteRooks=whiteRooks|(1L<<leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*numberOfSquarePerLine);
		whiteRooks=whiteRooks|(1L<<rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*numberOfSquarePerLine);
		whiteBishops=0;
		whiteBishops=whiteBishops|(1L<<leftWhiteBishopInitialPosition.x+leftWhiteBishopInitialPosition.y*numberOfSquarePerLine);
		whiteBishops=whiteBishops|(1L<<rightWhiteBishopInitialPosition.x+rightWhiteBishopInitialPosition.y*numberOfSquarePerLine);
		whiteKnights=0;
		whiteKnights=whiteKnights|(1L<<leftWhiteKnightInitialPosition.x+leftWhiteKnightInitialPosition.y*numberOfSquarePerLine);
		whiteKnights=whiteKnights|(1L<<rightWhiteKnightInitialPosition.x+rightWhiteKnightInitialPosition.y*numberOfSquarePerLine);
		whiteQueens=0;
		whiteQueens=whiteQueens|(1L<<whiteQueenInitialPosition.x+whiteQueenInitialPosition.y*numberOfSquarePerLine);
		whiteKing=0;
		whiteKing=whiteKing|(1L<<whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine);
		whitePawns=0;
		for(int counterHorizontal=firstLeftWhitePawnsInitialPosition.x;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
			whitePawns|=1L<<(counterHorizontal+firstLeftWhitePawnsInitialPosition.y*numberOfSquarePerLine);
		
		// for castling
		isWhiteKingHasMoved=false;
		isBlackKingHasMoved=false;
		isBlackLeftRookHasMoved=false;
		isBlackRightRookHasMoved=false;
		isWhiteLeftRookHasMoved=false;
		isWhiteRightRookHasMoved=false;
		
		// because it's a new game, we have to initialize begining date
		Calendar calendar=Calendar.getInstance();
		long beginningTime=calendar.getTimeInMillis();
		gameBeginningDate=new Date(beginningTime);
	}
	
	public int getPieceTypeAtThisIndexWithCurrentColor(int indexPiece)
	{
		switch(currentTurn)
		{
		case black:
			return getPieceTypeAtThisIndexWithBlackColor(indexPiece);
		case white:
			return getPieceTypeAtThisIndexWithWhiteColor(indexPiece);
		default:
			return noPieceId;
		}
	}
	
	public int getPieceTypeAtThisIndexWithWhiteColor(int indexPiece)
	{
		if((whitePawns&(1L<<indexPiece))!=0)
			return pawnId;
		else if((whiteKnights&(1L<<indexPiece))!=0)
			return knightId;
		else if((whiteBishops&(1L<<indexPiece))!=0)
			return bishopId;
		else if((whiteRooks&(1L<<indexPiece))!=0)
			return rookId;
		else if((whiteQueens&(1L<<indexPiece))!=0)
			return queenId;
		else if((whiteKing&(1L<<indexPiece))!=0)
			return kingId;
		return noPieceId;
	}
	
	public int getPieceTypeAtThisIndexWithBlackColor(int indexPiece)
	{
		if((blackPawns&(1L<<indexPiece))!=0)
			return pawnId;
		else if((blackKnights&(1L<<indexPiece))!=0)
			return knightId;
		else if((blackBishops&(1L<<indexPiece))!=0)
			return bishopId;
		else if((blackRooks&(1L<<indexPiece))!=0)
			return rookId;
		else if((blackQueens&(1L<<indexPiece))!=0)
			return queenId;
		else if((blackKing&(1L<<indexPiece))!=0)
			return kingId;
		return noPieceId;
	}
	
	public long getCurrentPieces()
	{
		switch(currentTurn)
		{
		case black:
			return getBlackPieces();
		case white:
			return getWhitePieces();
		default:
			return noPieceId;
		}
	}
	
	public long getWhitePieces()
	{
		long whitePieces=whitePawns;
		whitePieces|=whiteKnights;
		whitePieces|=whiteBishops;
		whitePieces|=whiteRooks;
		whitePieces|=whiteQueens;
		whitePieces|=whiteKing;
		return whitePieces;
	}
	
	public long getBlackPieces()
	{
		
		long blackPieces=blackPawns;
		blackPieces|=blackKnights;
		blackPieces|=blackBishops;
		blackPieces|=blackRooks;
		blackPieces|=blackQueens;
		blackPieces|=blackKing;
		return blackPieces;
	}
	
	public long getAllPieces()
	{
		return getBlackPieces()|getWhitePieces();
	}
	
	public long getLinesMoves(int indexSource)
	{
		long movePossibilities=0;
		long maskResult=0;
		maskResult=getAllPieces()&arrayLinesMask[indexSource];
		maskResult|=1L<<indexSource%numberOfSquarePerLine;
		maskResult|=1L<<(indexSource/numberOfSquarePerLine)*numberOfSquarePerLine;
		maskResult|=1L<<((indexSource/numberOfSquarePerLine+1)*numberOfSquarePerLine)-1;
		maskResult|=1L<<numberOfSquarePerLine*(numberOfSquarePerLine-1)+indexSource%numberOfSquarePerLine;
		movePossibilities=hashMapLinesPossibilities.get(maskResult);
		movePossibilities&=~getCurrentPieces();
		return movePossibilities;
	}
	
	public long getLinesMoves(int indexSource,long ownPieces,long allPieces)
	{
		long maskResult=allPieces&arrayLinesMask[indexSource];
		maskResult|=linesExtrems[indexSource];
		long movePossibilities=hashMapLinesPossibilities.get(maskResult);
		movePossibilities&=~ownPieces;
		return movePossibilities;
	}
	
	public long getDiagonalsMoves(int indexSource)
	{
		long movePossibilities=0;
		long maskResult=0;
		maskResult=getAllPieces()&arrayDiagonalsMask[indexSource];
		maskResult|=diagonalsExtrems[indexSource];
		if(indexSource==numberOfSquarePerLine-1||indexSource==0)
			maskResult&=~(1L<<indexSource);
		movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
		movePossibilities&=~getCurrentPieces();
		return movePossibilities;
	}
	
	public long getDiagonalsMoves(int indexSource,long ownPieces,long allPieces)
	{
		long maskResult=allPieces&arrayDiagonalsMask[indexSource];
		maskResult|=diagonalsExtrems[indexSource];
		if(indexSource==numberOfSquarePerLine-1||indexSource==0)
			maskResult&=~(1L<<indexSource);
		long movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
		movePossibilities&=~ownPieces;
		return movePossibilities;
	}
	
	public long getKingMoves(int indexSource,long ownPiece)
	{
		long movePossibilities=kingMoves[indexSource];
		movePossibilities&=~ownPiece;
		return movePossibilities;
	}
	
	public long getKingMoves(int indexSource)
	{
		long movePossibilities=kingMoves[indexSource];
		movePossibilities&=~getCurrentPieces();
		return movePossibilities;
	}
	
	public long getQueensMoves(int indexSource)
	{
		long movePossibilities=getLinesMoves(indexSource);
		movePossibilities|=getDiagonalsMoves(indexSource);
		return movePossibilities;
	}
	
	public long getQueensMoves(int indexSource,long ownPieces,long allPieces)
	{
		long maskResult=allPieces&arrayDiagonalsMask[indexSource];
		maskResult|=diagonalsExtrems[indexSource];
		if(indexSource==numberOfSquarePerLine-1||indexSource==0)
			maskResult&=~(1L<<indexSource);
		long movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
		maskResult=allPieces&arrayLinesMask[indexSource];
		maskResult|=linesExtrems[indexSource];
		movePossibilities|=hashMapLinesPossibilities.get(maskResult);
		movePossibilities&=~ownPieces;
		return movePossibilities;
	}
	
	public long getWhitePawnMoves(int indexSource,long blackPieces,long allPieces)
	{
		long movesPossibilites=0;
		if((allPieces&1L<<indexSource-numberOfSquarePerLine)==0)
		{
			movesPossibilites|=1L<<indexSource-numberOfSquarePerLine;
			if(indexSource/numberOfSquarePerLine==firstLeftWhitePawnsInitialPosition.y&&(allPieces&1L<<indexSource-2*numberOfSquarePerLine)==0)
				movesPossibilites|=1L<<indexSource-2*numberOfSquarePerLine;
		}
		if((indexSource%numberOfSquarePerLine>0)&&(blackPieces&(1L<<(indexSource-numberOfSquarePerLine-1)))!=0)
			movesPossibilites|=1L<<indexSource-numberOfSquarePerLine-1;
		if((indexSource%numberOfSquarePerLine<numberOfSquarePerLine-1)&&(blackPieces&(1L<<(indexSource-numberOfSquarePerLine+1)))!=0)
			movesPossibilites|=1L<<indexSource-numberOfSquarePerLine+1;
		return movesPossibilites;
	}
	
	public long getBlackPawnMoves(int indexSource,long whitePieces,long allPieces)
	{
		long movesPossibilites=0;
		if((allPieces&1L<<indexSource+numberOfSquarePerLine)==0)
		{
			movesPossibilites|=1L<<indexSource+numberOfSquarePerLine;
			if(indexSource/numberOfSquarePerLine==firstLeftBlackPawnsInitialPosition.y&&(allPieces&1L<<indexSource+2*numberOfSquarePerLine)==0)
				movesPossibilites|=1L<<indexSource+2*numberOfSquarePerLine;
		}
		if((indexSource%numberOfSquarePerLine>0)&&(whitePieces&(1L<<(indexSource+numberOfSquarePerLine-1)))!=0)
			movesPossibilites|=1L<<indexSource+numberOfSquarePerLine-1;
		if((indexSource%numberOfSquarePerLine<numberOfSquarePerLine-1)&&(whitePieces&(1L<<(indexSource+numberOfSquarePerLine+1)))!=0)
			movesPossibilites|=1L<<indexSource+numberOfSquarePerLine+1;
		return movesPossibilites;
	}
	
	public long getKnightMoves(int indexSource,long ownPieces)
	{
		long movesPossibilites=knightMoves[indexSource];
		movesPossibilites&=~ownPieces;
		return movesPossibilites;
	}
	
	// we get all the moves for a piece at a specific square, useful for human player
	public ArrayList<Point> getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(int indexSource,int isLastMoveEnableEnPassant)
	{
		ArrayList<Integer> listIndex=getListOfPossibleMovesForAPieceWithCheckChecking(indexSource,isLastMoveEnableEnPassant,true);
		ArrayList<Point> listPoint=new ArrayList<Point>();
		for(int counterIndex=0;counterIndex<listIndex.size();counterIndex++)
			listPoint.add(new Point(listIndex.get(counterIndex)%numberOfSquarePerLine,listIndex.get(counterIndex)/numberOfSquarePerLine));
		return listPoint;
	}
	
	public ArrayList<Integer> getListOfPossibleMovesForAPieceWithCheckChecking(int indexSource,int isLastMoveEnableEnPassant,boolean withCastling)
	{
		boolean[] isSpecial=new boolean[1];
		long allPieces=getAllPieces();
		long whitePieces=getWhitePieces();
		long blackPieces=getBlackPieces();
		
		// we put each move possible into an arrayList and return it
		long movePossibilities=0;
		long currentKing=blackKing;
		if(currentTurn==white)
			currentKing=whiteKing;
		int currentPieceType=getPieceTypeAtThisIndexWithCurrentColor(indexSource);
		switch(currentPieceType) // get moves and delete moves that put or let king in check
		{
		case pawnId:
			if(currentTurn==white)
			{
				movePossibilities=getWhitePawnMoves(indexSource,blackPieces,allPieces);
				if(isLastMoveEnableEnPassant!=-1&&indexSource/numberOfSquarePerLine==firstLeftBlackPawnsInitialPosition.y+2&&(isLastMoveEnableEnPassant==indexSource+1||isLastMoveEnableEnPassant==indexSource-1))
					movePossibilities|=1L<<isLastMoveEnableEnPassant-numberOfSquarePerLine;
			}
			if(currentTurn==black)
			{
				movePossibilities=getBlackPawnMoves(indexSource,whitePieces,allPieces);
				if(isLastMoveEnableEnPassant!=-1&&indexSource/numberOfSquarePerLine==firstLeftWhitePawnsInitialPosition.y-2&&(isLastMoveEnableEnPassant==indexSource+1||isLastMoveEnableEnPassant==indexSource-1))
					movePossibilities|=1L<<isLastMoveEnableEnPassant+numberOfSquarePerLine;
			}
			for(int counterBits=0;counterBits<numberOfSquarePerLine*numberOfSquarePerLine;counterBits++)
				if((movePossibilities&1L<<counterBits)!=0)
				{
					int pieceEventuallydeleted=MakeMoveWithTwoIndexForCurrentTurnWithPieceId(pawnId,indexSource,counterBits,isSpecial);
					if(isThisSquareAttacked(Long.numberOfTrailingZeros(currentKing),whitePieces,blackPieces,allPieces)==true)
						movePossibilities&=~(1L<<counterBits);
					undoMoveWithTwoIndexForCurrentTurnWithPieceId(pawnId,counterBits,indexSource,pieceEventuallydeleted,isSpecial[0]);
				}
			break;
		case kingId:
			if(withCastling==true)
				movePossibilities=getMovesForKingWithCheckChecking(indexSource,whitePieces,blackPieces,allPieces);
			else
				movePossibilities=getMovesForKingWithCheckCheckingWithoutCastling(indexSource,whitePieces,blackPieces,allPieces);
			break;
		case rookId:
			movePossibilities=getLinesMoves(indexSource);
			for(int counterBits=0;counterBits<numberOfSquarePerLine*numberOfSquarePerLine;counterBits++)
				if((movePossibilities&1L<<counterBits)!=0)
				{
					int pieceEventuallydeleted=MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,indexSource,counterBits,isSpecial);
					if(isThisSquareAttacked(Long.numberOfTrailingZeros(currentKing),whitePieces,blackPieces,allPieces)==true)
						movePossibilities&=~(1L<<counterBits);
					undoMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,counterBits,indexSource,pieceEventuallydeleted,isSpecial[0]);
				}
			break;
		case bishopId:
			movePossibilities=getDiagonalsMoves(indexSource);
			for(int counterBits=0;counterBits<numberOfSquarePerLine*numberOfSquarePerLine;counterBits++)
				if((movePossibilities&1L<<counterBits)!=0)
				{
					int pieceEventuallydeleted=MakeMoveWithTwoIndexForCurrentTurnWithPieceId(bishopId,indexSource,counterBits,isSpecial);
					if(isThisSquareAttacked(Long.numberOfTrailingZeros(currentKing),whitePieces,blackPieces,allPieces)==true)
						movePossibilities&=~(1L<<counterBits);
					undoMoveWithTwoIndexForCurrentTurnWithPieceId(bishopId,counterBits,indexSource,pieceEventuallydeleted,isSpecial[0]);
				}
			break;
		case queenId:
			movePossibilities=getDiagonalsMoves(indexSource);
			movePossibilities|=getLinesMoves(indexSource);
			for(int counterBits=0;counterBits<numberOfSquarePerLine*numberOfSquarePerLine;counterBits++)
				if((movePossibilities&1L<<counterBits)!=0)
				{
					int pieceEventuallydeleted=MakeMoveWithTwoIndexForCurrentTurnWithPieceId(queenId,indexSource,counterBits,isSpecial);
					if(isThisSquareAttacked(Long.numberOfTrailingZeros(currentKing),whitePieces,blackPieces,allPieces)==true)
						movePossibilities&=~(1L<<counterBits);
					undoMoveWithTwoIndexForCurrentTurnWithPieceId(queenId,counterBits,indexSource,pieceEventuallydeleted,isSpecial[0]);
				}
			break;
		case knightId:
			movePossibilities=knightMoves[indexSource];
			movePossibilities&=~getCurrentPieces();
			for(int counterBits=0;counterBits<numberOfSquarePerLine*numberOfSquarePerLine;counterBits++)
				if((movePossibilities&1L<<counterBits)!=0)
				{
					int pieceEventuallydeleted=MakeMoveWithTwoIndexForCurrentTurnWithPieceId(knightId,indexSource,counterBits,isSpecial);
					if(isThisSquareAttacked(Long.numberOfTrailingZeros(currentKing),whitePieces,blackPieces,allPieces)==true)
						movePossibilities&=~(1L<<counterBits);
					undoMoveWithTwoIndexForCurrentTurnWithPieceId(knightId,counterBits,indexSource,pieceEventuallydeleted,isSpecial[0]);
				}
			break;
		default:
		}
		ArrayList<Integer> arrayListPoint=new ArrayList<Integer>();
		for(int counterBits=0;counterBits<numberOfSquarePerLine*numberOfSquarePerLine;counterBits++)
			if((movePossibilities&1L<<counterBits)!=0)
				if(isThisMoveHasToBeRemovedDueToThreeRepetitionsLaw(currentTurn,currentPieceType,indexSource,counterBits)==false)
					arrayListPoint.add(counterBits);
		return arrayListPoint;
	}
	
	public boolean isThisMovePossible(Point sourceCoordinates,Point destinationCoordinates,int isLastMoveEnableEnPassant)
	{
		ArrayList<Integer> arrayListPossibleMoves=getListOfPossibleMovesForAPieceWithCheckChecking(sourceCoordinates.x+sourceCoordinates.y*numberOfSquarePerLine,isLastMoveEnableEnPassant,true);
		for(int indexCounter=0;indexCounter<arrayListPossibleMoves.size();indexCounter++)
			if(destinationCoordinates.x==arrayListPossibleMoves.get(indexCounter)%numberOfSquarePerLine&&destinationCoordinates.y==arrayListPossibleMoves.get(indexCounter)/numberOfSquarePerLine)
				return true;
		return false;
	}
	
	public int undoMoveWithTwoIndexForCurrentTurnWithPieceId(int pieceId,int sourceIndex,int destinationIndex,int pieceEventuallydeleted,boolean isSpecial)
	{
		boolean[] isSpecialUnmake=new boolean[1];
		MakeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceId,sourceIndex,destinationIndex,isSpecialUnmake);
		switch(currentTurn)
		{
		case white:
			switch(pieceEventuallydeleted)
			{
			case kingId:
				blackKing|=1L<<sourceIndex;
				break;
			case pawnId:
				blackPawns|=1L<<sourceIndex;
				break;
			case knightId:
				blackKnights|=1L<<sourceIndex;
				break;
			case bishopId:
				blackBishops|=1L<<sourceIndex;
				break;
			case rookId:
				blackRooks|=1L<<sourceIndex;
				break;
			case queenId:
				blackQueens|=1L<<sourceIndex;
				break;
			default:
				;
			}
			if(isSpecial==true&&pieceId!=rookId&&pieceId!=kingId)
				whiteQueens&=~(1L<<sourceIndex);
			if(isSpecial==true&&destinationIndex==whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine&&pieceId==kingId)
				isWhiteKingHasMoved=false;
			if(isSpecial==true&&destinationIndex==rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*numberOfSquarePerLine&&pieceId==rookId)
				isWhiteRightRookHasMoved=false;
			if(isSpecial==true&&destinationIndex==leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*numberOfSquarePerLine&&pieceId==rookId)
				isWhiteLeftRookHasMoved=false;
			break;
		case black:
			switch(pieceEventuallydeleted)
			{
			case kingId:
				whiteKing|=1L<<sourceIndex;
				break;
			case pawnId:
				whitePawns|=1L<<sourceIndex;
				break;
			case knightId:
				whiteKnights|=1L<<sourceIndex;
				break;
			case bishopId:
				whiteBishops|=1L<<sourceIndex;
				break;
			case rookId:
				whiteRooks|=1L<<sourceIndex;
				break;
			case queenId:
				whiteQueens|=1L<<sourceIndex;
				break;
			default:
				;
			}
			if(isSpecial==true&&pieceId!=rookId&&pieceId!=kingId)
				blackQueens&=~(1L<<sourceIndex);
			if(isSpecial==true&&destinationIndex==blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine&&pieceId==kingId)
				isBlackKingHasMoved=false;
			if(isSpecial==true&&destinationIndex==rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*numberOfSquarePerLine&&pieceId==rookId)
				isBlackRightRookHasMoved=false;
			if(isSpecial==true&&destinationIndex==leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*numberOfSquarePerLine&&pieceId==rookId)
				isBlackLeftRookHasMoved=false;
			break;
		default:
			;
		}
		return noPieceId;
	}
	
	public int MakeMoveWithTwoIndexForCurrentTurnWithPieceId(int pieceId,int sourceIndex,int destinationIndex,boolean[] isSpecial)
	{
		isSpecial[0]=false;
		switch(currentTurn)
		{
		case white:
			switch(pieceId)
			{
			case kingId:
				whiteKing&=~(1L<<sourceIndex);
				whiteKing|=1L<<destinationIndex;
				if(isWhiteKingHasMoved==false&&sourceIndex==whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine)
				{
					isWhiteKingHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case pawnId:
				whitePawns&=~(1L<<sourceIndex);
				if(destinationIndex<numberOfSquarePerLine)
				{
					whiteQueens|=1L<<destinationIndex;
					isSpecial[0]=true;
				}
				else
					whitePawns|=1L<<destinationIndex;
				break;
			case knightId:
				whiteKnights&=~(1L<<sourceIndex);
				whiteKnights|=1L<<destinationIndex;
				break;
			case rookId:
				whiteRooks&=~(1L<<sourceIndex);
				whiteRooks|=1L<<destinationIndex;
				if(isWhiteRightRookHasMoved==false&&sourceIndex==rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*numberOfSquarePerLine)
				{
					isWhiteRightRookHasMoved=true;
					isSpecial[0]=true;
				}
				if(isWhiteLeftRookHasMoved==false&&sourceIndex==leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*numberOfSquarePerLine)
				{
					isWhiteLeftRookHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case bishopId:
				whiteBishops&=~(1L<<sourceIndex);
				whiteBishops|=1L<<destinationIndex;
				break;
			case queenId:
				whiteQueens&=~(1L<<sourceIndex);
				whiteQueens|=1L<<destinationIndex;
				break;
			default:
				;
			}
			if((blackKing&(1L<<destinationIndex))!=0)
			{
				blackKing&=~(1L<<destinationIndex);
				return kingId;
			}
			if((blackPawns&(1L<<destinationIndex))!=0)
			{
				blackPawns&=~(1L<<destinationIndex);
				return pawnId;
			}
			if((blackKnights&(1L<<destinationIndex))!=0)
			{
				blackKnights&=~(1L<<destinationIndex);
				return knightId;
			}
			if((blackBishops&(1L<<destinationIndex))!=0)
			{
				blackBishops&=~(1L<<destinationIndex);
				return bishopId;
			}
			if((blackRooks&(1L<<destinationIndex))!=0)
			{
				blackRooks&=~(1L<<destinationIndex);
				return rookId;
			}
			if((blackQueens&(1L<<destinationIndex))!=0)
			{
				blackQueens&=~(1L<<destinationIndex);
				return queenId;
			}
			break;
		case black:
			switch(pieceId)
			{
			case kingId:
				blackKing&=~(1L<<sourceIndex);
				blackKing|=1L<<destinationIndex;
				if(isBlackKingHasMoved==false&&sourceIndex==blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine)
				{
					isBlackKingHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case pawnId:
				blackPawns&=~(1L<<sourceIndex);
				if(destinationIndex>=numberOfSquarePerLine*(numberOfSquarePerLine-1))
				{
					blackQueens|=1L<<destinationIndex;
					isSpecial[0]=true;
				}
				else
					blackPawns|=1L<<destinationIndex;
				break;
			case knightId:
				blackKnights&=~(1L<<sourceIndex);
				blackKnights|=1L<<destinationIndex;
				break;
			case rookId:
				blackRooks&=~(1L<<sourceIndex);
				blackRooks|=1L<<destinationIndex;
				if(isBlackRightRookHasMoved==false&&sourceIndex==rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*numberOfSquarePerLine)
				{
					isBlackRightRookHasMoved=true;
					isSpecial[0]=true;
				}
				if(isBlackLeftRookHasMoved==false&&sourceIndex==leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*numberOfSquarePerLine)
				{
					isBlackLeftRookHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case bishopId:
				blackBishops&=~(1L<<sourceIndex);
				blackBishops|=1L<<destinationIndex;
				break;
			case queenId:
				blackQueens&=~(1L<<sourceIndex);
				blackQueens|=1L<<destinationIndex;
				break;
			default:
				;
			}
			if((whiteKing&(1L<<destinationIndex))!=0)
			{
				whiteKing&=~(1L<<destinationIndex);
				return kingId;
			}
			if((whitePawns&(1L<<destinationIndex))!=0)
			{
				whitePawns&=~(1L<<destinationIndex);
				return pawnId;
			}
			if((whiteKnights&(1L<<destinationIndex))!=0)
			{
				whiteKnights&=~(1L<<destinationIndex);
				return knightId;
			}
			if((whiteBishops&(1L<<destinationIndex))!=0)
			{
				whiteBishops&=~(1L<<destinationIndex);
				return bishopId;
			}
			if((whiteRooks&(1L<<destinationIndex))!=0)
			{
				whiteRooks&=~(1L<<destinationIndex);
				return rookId;
			}
			if((whiteQueens&(1L<<destinationIndex))!=0)
			{
				whiteQueens&=~(1L<<destinationIndex);
				return queenId;
			}
			break;
		default:
			;
		}
		return noPieceId;
	}
	
	// get the name of a piece, useful to understand what happens
	private String getNamePieceAtThisSquare(Point squareCoordinates)
	{
		if((whiteKing&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)|blackKing&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))!=0)
			return "king";
		else if((whiteKnights&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)|blackKnights&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))!=0)
			return "knight";
		else if((whitePawns&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)|blackPawns&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))!=0)
			return "pawn";
		else if((whiteRooks&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)|blackRooks&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))!=0)
			return "rook";
		else if((whiteBishops&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)|blackBishops&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))!=0)
			return "bishop";
		else if((whiteQueens&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine)|blackQueens&1L<<(squareCoordinates.x+squareCoordinates.y*numberOfSquarePerLine))!=0)
			return "queen";
		return "";
	}
	
	// transform a coordinate square into a string with algebraic notation 
	private String getSquareInString(Point coordinates)
	{
		Character[] heightFirstLettersOfTheAlphabet=
		{'a','b','c','d','e','f','g','h'};
		String result="";
		result+=heightFirstLettersOfTheAlphabet[coordinates.x];
		result+=numberOfSquarePerLine-coordinates.y;
		return result;
	}
	
	public int GiveMeThePieceColorOnThisSquare(Point pointCoordinates)
	{
		if((getWhitePieces()&1L<<(pointCoordinates.x+pointCoordinates.y*numberOfSquarePerLine))!=0)
			return white;
		if((getBlackPieces()&1L<<(pointCoordinates.x+pointCoordinates.y*numberOfSquarePerLine))!=0)
			return black;
		return 0;
	}
	
	// check is multiple piece with the same color and same type can go to the same square
	public Boolean IsItAmbiguous(Point oldSelectedSquare,Point newSelectedSquare,long currentPieces,int isLastMoveEnableEnPassant)
	{
		if((currentPieces&1L<<(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine))!=0)
			for(int counterPieces=0;counterPieces<numberOfSquarePerLine*numberOfSquarePerLine;counterPieces++)
				if(((currentPieces&1L<<counterPieces)!=0)&&counterPieces!=(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine))
				{
					ArrayList<Integer> arrayListPossibleMoves=getListOfPossibleMovesForAPieceWithCheckChecking(counterPieces,isLastMoveEnableEnPassant,true);
					for(int counterMoves=0;counterMoves<arrayListPossibleMoves.size();counterMoves++)
						if(newSelectedSquare.x==arrayListPossibleMoves.get(counterMoves)%numberOfSquarePerLine&&newSelectedSquare.y==arrayListPossibleMoves.get(counterMoves)/numberOfSquarePerLine)
							return true;
				}
		return false;
	}
	
	public int doThisMoveAndGetDescription(Point oldSelectedSquare,Point newSelectedSquare,ArrayList<String> arrayMoveDescription,boolean[] isSpecial,int isLastMoveEnableEnPassant)
	{
		counterMoveFinished++;
		int returnValue=doThisMoveAndGetDescriptionWithoutIncrement(oldSelectedSquare,newSelectedSquare,arrayMoveDescription,isSpecial,isLastMoveEnableEnPassant);
		// we save position into the array and set the good number of occurrence
		PiecesSituation piecesSituation=new PiecesSituation(whiteKnights,whiteBishops,whiteQueens,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueens,blackKing,blackPawns,blackRooks);
		for(int counterSituations=0;counterSituations<listPiecesSituation.size();counterSituations++)
			if(listPiecesSituation.get(counterSituations).equal(piecesSituation)==true)
			{
				listPiecesSituationOccurrences.set(counterSituations,listPiecesSituationOccurrences.get(counterSituations)+1);
				return returnValue;
			}
		listPiecesSituation.add(piecesSituation);
		listPiecesSituationOccurrences.add(1);
		return returnValue;
	}
	
	// we make the move, with a description, this is useful for human play
	public int doThisMoveAndGetDescriptionWithoutIncrement(Point oldSelectedSquare,Point newSelectedSquare,ArrayList<String> arrayMoveDescription,boolean[] isSpecial,int isLastMoveEnableEnPassant)
	{
		isSpecial[0]=false;
		int returnValue=0;
		
		// before all we have to know if several pieces can move to destination or only one
		boolean isItAmbiguousMove=false;
		if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==white)
		{
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,whitePawns,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteKnights,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteQueens,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteBishops,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteRooks,isLastMoveEnableEnPassant);
		}
		if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==black)
		{
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,blackPawns,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,blackKnights,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,blackQueens,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,blackBishops,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||IsItAmbiguous(oldSelectedSquare,newSelectedSquare,blackRooks,isLastMoveEnableEnPassant);
		}
		
		// we create explicit description of the move 
		String moveDescription="";
		moveDescription+=counterMoveFinished+". ";
		
		// we check if it's a castling
		if(getPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*numberOfSquarePerLine+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==whiteKingInitialPosition.y&&oldSelectedSquare.x==whiteKingInitialPosition.x&&newSelectedSquare.y==whiteKingKingCastlingDestination/numberOfSquarePerLine&&newSelectedSquare.x==whiteKingKingCastlingDestination%numberOfSquarePerLine)
		{
			moveDescription+=kingSideCastlingExplicit;
			MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*numberOfSquarePerLine,whiteRookKingCastlingDestination,isSpecial);
			returnValue=whiteRookKingCastlingDestination;
		}
		else if(getPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*numberOfSquarePerLine+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==whiteKingInitialPosition.y&&oldSelectedSquare.x==whiteKingInitialPosition.x&&newSelectedSquare.y==whiteKingQueenCastlingDestination/numberOfSquarePerLine&&newSelectedSquare.x==whiteKingQueenCastlingDestination%numberOfSquarePerLine)
		{
			moveDescription+=queenSideCastlingExplicit;
			MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*numberOfSquarePerLine,whiteRookQueenCastlingDestination,isSpecial);
			returnValue=whiteRookQueenCastlingDestination;
		}
		
		else if(getPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*numberOfSquarePerLine+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==blackKingInitialPosition.y&&oldSelectedSquare.x==blackKingInitialPosition.x&&newSelectedSquare.y==blackKingKingCastlingDestination/numberOfSquarePerLine&&newSelectedSquare.x==blackKingKingCastlingDestination%numberOfSquarePerLine)
		{
			moveDescription+=kingSideCastlingExplicit;
			MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*numberOfSquarePerLine,blackRookKingCastlingDestination,isSpecial);
			returnValue=blackRookKingCastlingDestination;
		}
		else if(getPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*numberOfSquarePerLine+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==blackKingInitialPosition.y&&oldSelectedSquare.x==blackKingInitialPosition.x&&newSelectedSquare.y==blackKingQueenCastlingDestination/numberOfSquarePerLine&&newSelectedSquare.x==blackKingQueenCastlingDestination%numberOfSquarePerLine)
		{
			moveDescription+=queenSideCastlingExplicit;
			MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*numberOfSquarePerLine,blackRookQueenCastlingDestination,isSpecial);
			returnValue=blackRookQueenCastlingDestination;
		}
		else
		{
			moveDescription+=getNamePieceAtThisSquare(oldSelectedSquare)+" "+getSquareInString(oldSelectedSquare)+"-"+getSquareInString(newSelectedSquare);
			if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==white)
				if((getBlackPieces()&1L<<(newSelectedSquare.x+newSelectedSquare.y*numberOfSquarePerLine))!=0)
					moveDescription+=" captures "+getNamePieceAtThisSquare(newSelectedSquare);
			if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==black)
				if((getWhitePieces()&1L<<(newSelectedSquare.x+newSelectedSquare.y*numberOfSquarePerLine))!=0)
					moveDescription+=" captures "+getNamePieceAtThisSquare(newSelectedSquare);
		}
		
		// promotion
		if((newSelectedSquare.y==0&&(whitePawns&1L<<(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine))!=0)||(newSelectedSquare.y==numberOfSquarePerLine-1&&((blackPawns&1L<<(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine))!=0)))
			moveDescription+=" - "+promotionExplicit;
		
		// make the move itself
		int pieceType=getPieceTypeAtThisIndexWithCurrentColor(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine);
		MakeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine,newSelectedSquare.x+newSelectedSquare.y*numberOfSquarePerLine,isSpecial);
		
		// we have to know if it's an en passant move, if this is case, we write it
		if(currentTurn==white&&isLastMoveEnableEnPassant!=-1&&firstLeftBlackPawnsInitialPosition.y+2==oldSelectedSquare.y&&newSelectedSquare.x==isLastMoveEnableEnPassant%numberOfSquarePerLine&&getPieceTypeAtThisIndexWithCurrentColor(newSelectedSquare.x+newSelectedSquare.y*numberOfSquarePerLine)==pawnId)
		{
			returnValue=oldSelectedSquare.y*numberOfSquarePerLine+newSelectedSquare.x;
			blackPawns&=~(1L<<returnValue);
			moveDescription+=" captures pawn "+enPassantExplicit;
			isSpecial[0]=true;
		}
		if(currentTurn==black&&isLastMoveEnableEnPassant!=-1&&firstLeftWhitePawnsInitialPosition.y-2==oldSelectedSquare.y&&newSelectedSquare.x==isLastMoveEnableEnPassant%numberOfSquarePerLine&&getPieceTypeAtThisIndexWithCurrentColor(newSelectedSquare.x+newSelectedSquare.y*numberOfSquarePerLine)==pawnId)
		{
			returnValue=oldSelectedSquare.y*numberOfSquarePerLine+newSelectedSquare.x;
			whitePawns&=~(1L<<returnValue);
			moveDescription+=" captures pawn "+enPassantExplicit;
			isSpecial[0]=true;
		}
		
		// we look if the opponent's king is under check 
		ChangePlayerTurn();
		if(currentTurn==black)
		{
			if(isThisSquareAttacked(Long.numberOfTrailingZeros(blackKing),getWhitePieces(),getBlackPieces(),getAllPieces())==true)
				moveDescription+=" - "+checkDescription;
		}
		else
		{
			if(isThisSquareAttacked(Long.numberOfTrailingZeros(whiteKing),getWhitePieces(),getBlackPieces(),getAllPieces())==true)
				moveDescription+=" - "+checkDescription;
		}
		ChangePlayerTurn();
		arrayMoveDescription.add(moveDescription);
		arrayMoveDescription.add(TransformExplicitMoveDescriptionIntoStandardMoveDescription(moveDescription,isItAmbiguousMove));
		return returnValue;
	}
	
	// determine if the move is ambiguous or if a piece has been eaten and give the standard description of the move
	private String TransformExplicitMoveDescriptionIntoStandardMoveDescription(String explicitMoveDescription,boolean isItAmbiguousMove)
	{
		String standardMoveDescription=explicitMoveDescription;
		int indexDot=standardMoveDescription.indexOf(".");
		int indexPawn=explicitMoveDescription.indexOf("pawn");
		if(indexPawn>indexDot+3) // this is useful to know if its a pawn who moved or if it's a pawn eaten
			indexPawn=-1;
		standardMoveDescription=standardMoveDescription.replaceAll("pawn ","");
		standardMoveDescription=standardMoveDescription.replaceAll("knight ","N");
		standardMoveDescription=standardMoveDescription.replaceAll("queen ","Q");
		standardMoveDescription=standardMoveDescription.replaceAll("king ","K");
		standardMoveDescription=standardMoveDescription.replaceAll("bishop ","B");
		standardMoveDescription=standardMoveDescription.replaceAll("rook ","R");
		standardMoveDescription=standardMoveDescription.replaceAll(enPassantExplicit," "+enPassantStandard);
		int indexHyphen=standardMoveDescription.indexOf("-");
		if(indexPawn==-1)
		{
			if(isItAmbiguousMove==false)
			{
				if(standardMoveDescription.indexOf(kingSideCastlingExplicit)!=-1) // castling management
					standardMoveDescription=kingSideCastlingStandard;
				else if(standardMoveDescription.indexOf(queenSideCastlingExplicit)!=-1)
					standardMoveDescription=queenSideCastlingStandard;
				else
				{
					standardMoveDescription=standardMoveDescription.substring(indexDot+2,indexDot+3)+standardMoveDescription.substring(indexHyphen+1,indexHyphen+3);
				}
			}
			else
			{
				standardMoveDescription=standardMoveDescription.substring(indexDot+2,indexDot+5)+standardMoveDescription.substring(indexHyphen+1,indexHyphen+3);
			}
		}
		else
		{
			if(isItAmbiguousMove==false)
			{
				standardMoveDescription=standardMoveDescription.substring(indexHyphen+1,indexHyphen+3); // we do not set any letter for the pawn case
				if(explicitMoveDescription.indexOf(promotionExplicit)!=-1)
					standardMoveDescription+=promotionStandard;
			}
			else
			{
				standardMoveDescription=standardMoveDescription.substring(indexHyphen-2,indexHyphen-0)+standardMoveDescription.substring(indexHyphen+1,indexHyphen+3);
				if(explicitMoveDescription.indexOf(promotionExplicit)!=-1)
					standardMoveDescription+=promotionStandard;
			}
		}
		int indexEat=explicitMoveDescription.indexOf("captures ");
		if(indexEat!=-1)
		{
			if(indexPawn==-1)
				standardMoveDescription=standardMoveDescription.substring(0,1)+"x"+standardMoveDescription.substring(1,standardMoveDescription.length());
			else
				standardMoveDescription="x"+standardMoveDescription; // we are in pawn case			
		}
		int indexCheck=explicitMoveDescription.indexOf(checkDescription);
		if(indexCheck!=-1)
			standardMoveDescription+="+";
		if(explicitMoveDescription.indexOf(enPassantExplicit)!=-1)
			standardMoveDescription+=" "+enPassantStandard;
		return standardMoveDescription;
	}
	
	// check if at least one move of the current player is possible
	public int IfGameHasEndedGiveMeTheWinner(int isLastMoveEnableEnPassant)
	{
		long blackAllPieces=getBlackPieces();
		long whiteAllPieces=getWhitePieces();
		long allPieces=blackAllPieces|whiteAllPieces;
		switch(currentTurn)
		{
		case white:
			int indexWhitePiece;
			int totalMovesForWhitePlayer=0;
			for(;;)
			{
				indexWhitePiece=Long.numberOfTrailingZeros(whiteAllPieces);
				if(indexWhitePiece!=Long.SIZE)
				{
					whiteAllPieces&=~(1L<<indexWhitePiece);
					ArrayList<Integer> listPoint=getListOfPossibleMovesForAPieceWithCheckChecking(indexWhitePiece,isLastMoveEnableEnPassant,true);
					totalMovesForWhitePlayer+=listPoint.size();
				}
				else
					break;
			}
			if(totalMovesForWhitePlayer==0)
			{
				if(isThisSquareAttacked(Long.numberOfTrailingZeros(whiteKing),whiteAllPieces,blackAllPieces,allPieces)==false)
					return whiteIsPat;
				return black;
			}
			break;
		case black:
			int indexBlackPiece;
			int totalMovesForBlackPlayer=0;
			for(;;)
			{
				indexBlackPiece=Long.numberOfTrailingZeros(blackAllPieces);
				if(indexBlackPiece!=Long.SIZE)
				{
					blackAllPieces&=~(1L<<indexBlackPiece);
					ArrayList<Integer> listPoint=getListOfPossibleMovesForAPieceWithCheckChecking(indexBlackPiece,isLastMoveEnableEnPassant,true);
					totalMovesForBlackPlayer+=listPoint.size();
				}
				else
					break;
			}
			if(totalMovesForBlackPlayer==0)
			{
				if(isThisSquareAttacked(Long.numberOfTrailingZeros(blackKing),whiteAllPieces,blackAllPieces,allPieces)==false)
					return blackIsPat;
				return white;
			}
			break;
		}
		return 0;
	}
	
	// change the player turn, because we use opposed values, we don't have to know what is the current turn, and the turn we have to switch on
	public void ChangePlayerTurn()
	{
		currentTurn=-currentTurn;
	}
	
	public int getCurrentTurn()
	{
		return currentTurn;
	}
	
	public void EndTheGame()
	{
		currentTurn=noCurrentGame;
	}
	
	public void SetToLastTurnBeforeCheckAndMate(boolean isItPairMovement)
	{
		if(isItPairMovement==true)
			currentTurn=white;
		else
			currentTurn=black;
	}
	
	// calculate a point coordinate with the string coordinates given in parameter
	Point getCorrespondingSquare(String squareCoordinates)
	{
		Point pointCoordinate=new Point(-1,-1);
		Character[] heightFirstLettersOfTheAlphabet=
		{'a','b','c','d','e','f','g','h'};
		for(int counterLetter=0;counterLetter<heightFirstLettersOfTheAlphabet.length;counterLetter++)
			if(squareCoordinates.charAt(0)==heightFirstLettersOfTheAlphabet[counterLetter])
				pointCoordinate.x=counterLetter;
		pointCoordinate.y=numberOfSquarePerLine-Integer.decode(squareCoordinates.substring(1,2));
		return pointCoordinate;
	}
	
	public int getBlackPieceType(int indexPiece)
	{
		if((blackQueens&1L<<indexPiece)!=0)
			return queenId;
		else if((blackPawns&1L<<indexPiece)!=0)
			return pawnId;
		else if((blackRooks&1L<<indexPiece)!=0)
			return rookId;
		else if((blackBishops&1L<<indexPiece)!=0)
			return bishopId;
		else if((blackKnights&1L<<indexPiece)!=0)
			return knightId;
		else if((blackKing&1L<<indexPiece)!=0)
			return kingId;
		return noPieceId;
	}
	
	public int getWhitePieceType(int indexPiece)
	{
		if((whiteQueens&1L<<indexPiece)!=0)
			return queenId;
		else if((whitePawns&1L<<indexPiece)!=0)
			return pawnId;
		else if((whiteRooks&1L<<indexPiece)!=0)
			return rookId;
		else if((whiteBishops&1L<<indexPiece)!=0)
			return bishopId;
		else if((whiteKnights&1L<<indexPiece)!=0)
			return knightId;
		else if((whiteKing&1L<<indexPiece)!=0)
			return kingId;
		return noPieceId;
	}
	
	public int getPieceTypeAtThisIndexAndWithThisColor(int pieceColor,int pieceIndex)
	{
		switch(pieceColor)
		{
		case white:
			return getWhitePieceType(pieceIndex);
		case black:
			return getBlackPieceType(pieceIndex);
		default:
			;
		}
		return noPieceId;
	}
	
	public int getThePieceColorAtThisIndex(int pieceIndex)
	{
		if((getWhitePieces()&1L<<pieceIndex)!=0)
			return white;
		if((getBlackPieces()&1L<<pieceIndex)!=0)
			return black;
		return noPieceId;
	}
	
	int getPieceIdWithString(String pieceTypeEventuallydeletedString)
	{
		if(pieceTypeEventuallydeletedString.equals(new String("pawn"))==true)
			return pawnId;
		else if(pieceTypeEventuallydeletedString.equals(new String("knight"))==true)
			return knightId;
		else if(pieceTypeEventuallydeletedString.equals(new String("king"))==true)
			return kingId;
		else if(pieceTypeEventuallydeletedString.equals(new String("rook"))==true)
			return rookId;
		else if(pieceTypeEventuallydeletedString.equals(new String("bishop"))==true)
			return bishopId;
		else if(pieceTypeEventuallydeletedString.equals(new String("queen"))==true)
			return queenId;
		return noPieceId;
	}
	
	// we unmake a move and restore the piece which has eventually been deleted
	public void undoMoveForWithoutRefreshRehearsalHistoric(Point sourceSquare,Point destinationSquare,int piecedeleted,boolean isSpecial)
	{
		int currentColor=getThePieceColorAtThisIndex(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
		int pieceId=getPieceTypeAtThisIndexAndWithThisColor(currentColor,destinationSquare.x+destinationSquare.y*numberOfSquarePerLine);
		boolean[] arrayIsSpecial=new boolean[1];
		MakeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceId,destinationSquare.x+destinationSquare.y*numberOfSquarePerLine,sourceSquare.x+sourceSquare.y*numberOfSquarePerLine,arrayIsSpecial);
		if(isSpecial==true)
		{
			switch(pieceId)
			{
			case pawnId:
				if(currentColor==white)
				{
					blackPawns|=1L<<(destinationSquare.x+(destinationSquare.y+1)*numberOfSquarePerLine);
					blackPawns&=~(1L<<(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine));
					piecedeleted=noPieceId;
				}
				if(currentColor==black)
				{
					whitePawns|=1L<<(destinationSquare.x+(destinationSquare.y-1)*numberOfSquarePerLine);
					whitePawns&=~(1L<<(destinationSquare.x+destinationSquare.y*numberOfSquarePerLine));
					piecedeleted=noPieceId;
				}
				break;
			case queenId:
				if(currentColor==white)
				{
					whiteQueens&=~(1L<<sourceSquare.x+sourceSquare.y*numberOfSquarePerLine);
					whitePawns|=1L<<sourceSquare.x+sourceSquare.y*numberOfSquarePerLine;
				}
				else if(currentColor==black)
				{
					blackQueens&=~(1L<<sourceSquare.x+sourceSquare.y*numberOfSquarePerLine);
					blackPawns|=1L<<sourceSquare.x+sourceSquare.y*numberOfSquarePerLine;
				}
				break;
			case rookId:
				if(currentColor==white)
				{
					if(sourceSquare.x==rightWhiteRookInitialPosition.x&&sourceSquare.y==rightWhiteRookInitialPosition.y)
						isWhiteRightRookHasMoved=false;
					else if(sourceSquare.x==leftWhiteRookInitialPosition.x&&sourceSquare.y==leftWhiteRookInitialPosition.y)
						isWhiteLeftRookHasMoved=false;
				}
				else if(currentColor==black)
				{
					if(sourceSquare.x==rightBlackRookInitialPosition.x&&sourceSquare.y==rightBlackRookInitialPosition.y)
						isBlackRightRookHasMoved=false;
					else if(sourceSquare.x==leftBlackRookInitialPosition.x&&sourceSquare.y==leftBlackRookInitialPosition.y)
						isBlackLeftRookHasMoved=false;
				}
				break;
			case kingId:
				if(currentColor==white&&sourceSquare.x==whiteKingInitialPosition.x&&sourceSquare.y==whiteKingInitialPosition.y)
					isWhiteKingHasMoved=false;
				else if(currentColor==black&&sourceSquare.x==blackKingInitialPosition.x&&sourceSquare.y==blackKingInitialPosition.y)
					isBlackKingHasMoved=false;
			default:
				;
			}
		}
		if(piecedeleted!=noPieceId)
		{
			if(GiveMeThePieceColorOnThisSquare(sourceSquare)==black)
			{
				
				if(piecedeleted==pawnId)
					whitePawns|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
				else if(piecedeleted==knightId)
					whiteKnights|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
				else if(piecedeleted==kingId)
					whiteKing|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
				else if(piecedeleted==rookId)
					whiteRooks|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
				else if(piecedeleted==bishopId)
					whiteBishops|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
				else if(piecedeleted==queenId)
					whiteQueens|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
			}
			if(GiveMeThePieceColorOnThisSquare(sourceSquare)==white)
			{
				if(piecedeleted==pawnId)
					blackPawns|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
				else if(piecedeleted==knightId)
					blackKnights|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
				else if(piecedeleted==kingId)
					blackKing|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
				else if(piecedeleted==rookId)
					blackRooks|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
				else if(piecedeleted==bishopId)
					blackBishops|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
				else if(piecedeleted==queenId)
					blackQueens|=1L<<destinationSquare.x+destinationSquare.y*numberOfSquarePerLine;
			}
		}
	}
	
	public void undoMove(Point sourceSquare,Point destinationSquare,int piecedeleted,boolean isSpecial)
	{
		PiecesSituation piecesSituation=new PiecesSituation(whiteKnights,whiteBishops,whiteQueens,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueens,blackKing,blackPawns,blackRooks);
		for(int counterSituations=0;counterSituations<listPiecesSituation.size();counterSituations++)
			if(listPiecesSituation.get(counterSituations).equal(piecesSituation)==true)
			{
				listPiecesSituationOccurrences.set(counterSituations,listPiecesSituationOccurrences.get(counterSituations)-1);
				if(listPiecesSituationOccurrences.get(counterSituations)==0)
				{
					listPiecesSituationOccurrences.remove(counterSituations);
					listPiecesSituation.remove(counterSituations);
				}
				break;
			}
		undoMoveForWithoutRefreshRehearsalHistoric(sourceSquare,destinationSquare,piecedeleted,isSpecial);
	}
	
	public void SetCounterOfMoves(int counterOfMovesParameter)
	{
		counterMoveFinished=counterOfMovesParameter;
	}
	
	public Date getBeginningDate()
	{
		return gameBeginningDate;
	}
	
	public int getCounterOfMoves()
	{
		return counterMoveFinished;
	}
	
	public int doThisMoveAndGetDescriptionWithMoveDescription(long currentPieces,String moveDescription,ArrayList<String> arrayMoveDescription,boolean[] arrayIsSpecial,int isLastMoveEnableEnPassant)
	{
		// first of all we get destination
		String moveDescriptionWithoutPieceIdentifier=moveDescription.replaceAll("N","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll(promotionStandard,"");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("R","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("B","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("Q","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("K","");
		String stringDestination=moveDescriptionWithoutPieceIdentifier.substring(moveDescriptionWithoutPieceIdentifier.length()-2,moveDescriptionWithoutPieceIdentifier.length());
		Point pointDestination=null;
		if(stringDestination.equals(enPassantReducedForAnalysis)==true)
		{
			moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll(enPassantReducedForAnalysis,"");
			stringDestination=moveDescriptionWithoutPieceIdentifier.substring(moveDescriptionWithoutPieceIdentifier.length()-2,moveDescriptionWithoutPieceIdentifier.length());
			pointDestination=getCorrespondingSquare(stringDestination);
			if(currentTurn==white)
				isLastMoveEnableEnPassant=pointDestination.x+(pointDestination.y+1)*numberOfSquarePerLine;
			if(currentTurn==black)
				isLastMoveEnableEnPassant=pointDestination.x+(pointDestination.y-1)*numberOfSquarePerLine;
		}
		else
			pointDestination=getCorrespondingSquare(stringDestination);
		ArrayList<Integer> arrayListPossibleMoves=new ArrayList<Integer>();
		int numberOfPossiblePieces=0;
		ArrayList<Integer> arrayListPossibleSourcePieces=new ArrayList<Integer>();
		for(int counterBits=0;counterBits<numberOfSquarePerLine*numberOfSquarePerLine;counterBits++)
		{
			arrayListPossibleMoves.clear();
			if((currentPieces&1L<<counterBits)!=0)
			{
				arrayListPossibleMoves=getListOfPossibleMovesForAPieceWithCheckChecking(counterBits,isLastMoveEnableEnPassant,true);
				int destinationOfCurrentMove=0;
				for(int counterMovesFirstLevel=0;counterMovesFirstLevel<arrayListPossibleMoves.size();counterMovesFirstLevel++)
				{
					destinationOfCurrentMove=arrayListPossibleMoves.get(counterMovesFirstLevel);
					if(destinationOfCurrentMove%numberOfSquarePerLine==pointDestination.x&&destinationOfCurrentMove/numberOfSquarePerLine==pointDestination.y)
					{
						numberOfPossiblePieces++;
						arrayListPossibleSourcePieces.add(counterBits);
					}
				}
			}
		}
		
		// here multiple piece can go do the destination, we have to delete the wrong pieces
		if(numberOfPossiblePieces>1)
		{
			// we have to know what piece is concerned
			ArrayList<Point> ArrayListPossibleSourceWithColumnFilter=new ArrayList<Point>();
			for(int counterPossiblePieceForColumnFilter=0;counterPossiblePieceForColumnFilter<arrayListPossibleSourcePieces.size();counterPossiblePieceForColumnFilter++)
			{
				Point currentPieceSourceForColumnFilter=new Point(arrayListPossibleSourcePieces.get(counterPossiblePieceForColumnFilter)%numberOfSquarePerLine,arrayListPossibleSourcePieces.get(counterPossiblePieceForColumnFilter)/numberOfSquarePerLine);
				if(moveDescriptionWithoutPieceIdentifier.charAt(0)<'a'||moveDescriptionWithoutPieceIdentifier.charAt(0)>'h') // we do coherence check on the file content
				{
					javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"A bad character has been found : ["+moveDescriptionWithoutPieceIdentifier.charAt(0)+"].\n"+"It should be between a and h.\n"+"Move description : "+moveDescription);
					return -1;
				}
				if(currentPieceSourceForColumnFilter.x==moveDescriptionWithoutPieceIdentifier.charAt(0)-'a')
					ArrayListPossibleSourceWithColumnFilter.add((Point)currentPieceSourceForColumnFilter.clone());
			}
			if(ArrayListPossibleSourceWithColumnFilter.size()>1) // column filter is not enough, we use line filter
			{
				ArrayList<Point> ArrayListPossibleSourceWithColumnAndLineFilter=new ArrayList<Point>();
				for(int counterPossiblePieceForLineFilter=0;counterPossiblePieceForLineFilter<ArrayListPossibleSourceWithColumnFilter.size();counterPossiblePieceForLineFilter++)
				{
					Point currentPieceSourceForColumnAndLineFilter=ArrayListPossibleSourceWithColumnFilter.get(counterPossiblePieceForLineFilter);
					if(moveDescriptionWithoutPieceIdentifier.charAt(1)<'1'||moveDescriptionWithoutPieceIdentifier.charAt(1)>'8') // we do coherence check on the file content for line filter
					{
						javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"A bad character has been found : "+moveDescriptionWithoutPieceIdentifier.charAt(1)+".\n"+"It should be between 1 and 8.\n"+"Move description : "+moveDescription);
						return -1;
					}
					if(numberOfSquarePerLine-currentPieceSourceForColumnAndLineFilter.y-1==moveDescriptionWithoutPieceIdentifier.charAt(1)-'1')
						ArrayListPossibleSourceWithColumnAndLineFilter.add((Point)currentPieceSourceForColumnAndLineFilter.clone()); // we have found the piece according to line filter
				}
				if(ArrayListPossibleSourceWithColumnAndLineFilter.size()==0) // error case we should have one and only one piece
				{
					javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify line of a begining move in an ambiguous case, no piece found.\n"+"Move description : "+moveDescription);
					return -1;
				}
				else if(ArrayListPossibleSourceWithColumnAndLineFilter.size()>1) // error case we should have one and only one piece
				{
					javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify line of a begining move in an ambiguous case, too many pieces found.\n"+"Move description : "+moveDescription);
					return -1;
				}
				else if(ArrayListPossibleSourceWithColumnAndLineFilter.size()==1)
				{
					doThisMoveAndGetDescription(ArrayListPossibleSourceWithColumnAndLineFilter.get(0),pointDestination,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant); // we have the right piece according to column and line filter
					return IsItDoublePawnMoveForEnPassant(ArrayListPossibleSourceWithColumnAndLineFilter.get(0),pointDestination);
				}
			}
			else if(ArrayListPossibleSourceWithColumnFilter.size()==0)
			{
				javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify column of a begining move in an ambiguous case, no piece found.\n"+"Move description : "+moveDescription);
				return -1;
			}
			else if(ArrayListPossibleSourceWithColumnFilter.size()==1)
			{
				doThisMoveAndGetDescription(ArrayListPossibleSourceWithColumnFilter.get(0),pointDestination,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
				return IsItDoublePawnMoveForEnPassant(ArrayListPossibleSourceWithColumnFilter.get(0),pointDestination);
			}
		}
		else if(numberOfPossiblePieces==0) // case which non piece can go to the destination
		{
			javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify piece at the begining move.\n"+"Move description : "+moveDescription);
			return -1;
		}
		else
		{
			doThisMoveAndGetDescription(new Point(arrayListPossibleSourcePieces.get(0)%numberOfSquarePerLine,arrayListPossibleSourcePieces.get(0)/numberOfSquarePerLine),pointDestination,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant); // we have directly the good piece, it's too easy
			return IsItDoublePawnMoveForEnPassant(new Point(arrayListPossibleSourcePieces.get(0)%numberOfSquarePerLine,arrayListPossibleSourcePieces.get(0)/numberOfSquarePerLine),pointDestination);
		}
		return -1;
	}
	
	// use for redo feature
	public int doThisMoveAndGetDescriptionFromAWord(String moveDescription,ArrayList<String> arrayMoveDescription,boolean[] arrayIsSpecial,int isLastMoveEnableEnPassant)
	{
		if(getCurrentTurn()==white)
		{
			// now we have to find the source of the moves
			long currentPieces=0;
			int indexPiece=moveDescription.indexOf("N");
			if(indexPiece!=-1)
				currentPieces=whiteKnights;
			indexPiece=moveDescription.indexOf("R");
			if(indexPiece!=-1)
				currentPieces=whiteRooks;
			indexPiece=moveDescription.indexOf("B");
			if(indexPiece!=-1)
				currentPieces=whiteBishops;
			indexPiece=moveDescription.indexOf("Q");
			if(indexPiece!=-1)
				currentPieces=whiteQueens;
			indexPiece=moveDescription.indexOf("K");
			if(indexPiece!=-1)
				currentPieces=whiteKing;
			if(currentPieces==0)
				currentPieces=whitePawns;
			if(moveDescription.equals(kingSideCastlingStandard)==true) //	castling management
			{
				doCastling(moveDescription);
				counterMoveFinished++;
				arrayMoveDescription.add(counterMoveFinished+". "+kingSideCastlingExplicit);
				arrayMoveDescription.add(counterMoveFinished+". "+kingSideCastlingStandard);
				arrayIsSpecial[0]=true;
			}
			else if(moveDescription.equals(queenSideCastlingStandard)==true) //	castling management
			{
				doCastling(moveDescription);
				counterMoveFinished++;
				arrayMoveDescription.add(counterMoveFinished+". "+queenSideCastlingExplicit);
				arrayMoveDescription.add(counterMoveFinished+". "+queenSideCastlingStandard);
				arrayIsSpecial[0]=true;
			}
			else
				return doThisMoveAndGetDescriptionWithMoveDescription(currentPieces,moveDescription,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
		}
		else if(getCurrentTurn()==black)
		{
			// now we have to find the source of the moves
			long currentPieces=0;
			int indexPiece=moveDescription.indexOf("N");
			if(indexPiece!=-1)
				currentPieces=blackKnights;
			indexPiece=moveDescription.indexOf("R");
			if(indexPiece!=-1)
				currentPieces=blackRooks;
			indexPiece=moveDescription.indexOf("B");
			if(indexPiece!=-1)
				currentPieces=blackBishops;
			indexPiece=moveDescription.indexOf("Q");
			if(indexPiece!=-1)
				currentPieces=blackQueens;
			indexPiece=moveDescription.indexOf("K");
			if(indexPiece!=-1)
				currentPieces=blackKing;
			if(currentPieces==0)
				currentPieces=blackPawns;
			if(moveDescription.equals(kingSideCastlingStandard)==true) //	castling management
			{
				doCastling(moveDescription);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+kingSideCastlingExplicit);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+kingSideCastlingStandard);
				arrayIsSpecial[0]=true;
			}
			else if(moveDescription.equals(queenSideCastlingStandard)==true) //	castling management
			{
				doCastling(moveDescription);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+queenSideCastlingExplicit);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+queenSideCastlingStandard);
				arrayIsSpecial[0]=true;
			}
			else
				return doThisMoveAndGetDescriptionWithMoveDescription(currentPieces,moveDescription,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
		}
		return -1;
	}
	
	// used for king's moves, to check if other king doesn't attack this square, the method without check checking is used
	public boolean isThisSquareAttacked(int squareIndex,long whitePieces,long blackPieces,long allPieces)
	{
		switch(currentTurn)
		{
		case white:
			if((kingMoves[squareIndex]&blackKing)!=0)
				return true;
			if((knightMoves[squareIndex]&blackKnights)!=0)
				return true;
			if((getLinesMoves(squareIndex)&blackRooks)!=0)
				return true;
			if((getDiagonalsMoves(squareIndex)&blackBishops)!=0)
				return true;
			if((getLinesMoves(squareIndex)&blackQueens)!=0)
				return true;
			if((getDiagonalsMoves(squareIndex)&blackQueens)!=0)
				return true;
			if((getWhitePawnMoves(squareIndex,blackPieces,allPieces)&blackPawns)!=0)
				return true;
			break;
		case black:
			if((kingMoves[squareIndex]&whiteKing)!=0)
				return true;
			if((knightMoves[squareIndex]&whiteKnights)!=0)
				return true;
			if((getLinesMoves(squareIndex)&whiteRooks)!=0)
				return true;
			if((getDiagonalsMoves(squareIndex)&whiteBishops)!=0)
				return true;
			if((getLinesMoves(squareIndex)&whiteQueens)!=0)
				return true;
			if((getDiagonalsMoves(squareIndex)&whiteQueens)!=0)
				return true;
			if((getBlackPawnMoves(squareIndex,whitePieces,allPieces)&whitePawns)!=0)
				return true;
			break;
		default:
			;
		}
		return false;
	}
	
	private long getMovesForKingWithCheckCheckingWithoutCastling(int pieceIndex,long whitePieces,long blackPieces,long allPieces)
	{
		long movePossibilities=kingMoves[pieceIndex];
		movePossibilities&=~getCurrentPieces();
		
		// we delete moves that put king in check
		long currentPossibleMoves=movePossibilities;
		int leadingZeros=Long.numberOfTrailingZeros(currentPossibleMoves);
		long saveKing;
		if(currentTurn==white)
		{
			saveKing=whiteKing;
			whiteKing=0;
		}
		else
		{
			saveKing=blackKing;
			blackKing=0;
		}
		while(true)
		{
			if(leadingZeros==numberOfSquarePerLine*numberOfSquarePerLine)
				break;
			if(isThisSquareAttacked(leadingZeros,whitePieces,blackPieces,allPieces)==true)
				movePossibilities&=~(1L<<leadingZeros);
			currentPossibleMoves&=~(1L<<leadingZeros);
			leadingZeros=Long.numberOfTrailingZeros(currentPossibleMoves);
		}
		if(currentTurn==white)
			whiteKing=saveKing;
		else
			blackKing=saveKing;
		return movePossibilities;
	}
	
	private long getMovesForKingWithCheckChecking(int pieceIndex,long whitePieces,long blackPieces,long allPieces)
	{
		long movePossibilities=kingMoves[pieceIndex];
		movePossibilities&=~getCurrentPieces();
		
		// we delete moves that put king in check
		long currentPossibleMoves=movePossibilities;
		int leadingZeros=Long.numberOfTrailingZeros(currentPossibleMoves);
		long saveKing;
		if(currentTurn==white)
		{
			saveKing=whiteKing;
			whiteKing=0;
		}
		else
		{
			saveKing=blackKing;
			blackKing=0;
		}
		while(true)
		{
			if(leadingZeros==numberOfSquarePerLine*numberOfSquarePerLine)
				break;
			if(isThisSquareAttacked(leadingZeros,whitePieces,blackPieces,allPieces)==true)
				movePossibilities&=~(1L<<leadingZeros);
			currentPossibleMoves&=~(1L<<leadingZeros);
			leadingZeros=Long.numberOfTrailingZeros(currentPossibleMoves);
		}
		if(currentTurn==white)
			whiteKing=saveKing;
		else
			blackKing=saveKing;
		
		// castling management
		if(currentTurn==white)
		{
			if(((whiteKing&1L<<(whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine))!=0)&&((whiteRooks&1L<<(rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*numberOfSquarePerLine))!=0)&&isWhiteKingHasMoved==false&&isWhiteRightRookHasMoved==false)
			{
				long whiteKingCastlingMaskTemp=whiteKingCastlingMask;
				long allPiecesForCastling=(allPieces&whiteKingCastlingMaskTemp);
				if(allPiecesForCastling==0)
				{
					int indexSquare=Long.numberOfTrailingZeros(whiteKingCastlingMaskTemp);
					while(indexSquare!=numberOfSquarePerLine*numberOfSquarePerLine)
					{
						if(isThisSquareAttacked(indexSquare,blackPieces,whitePieces,allPieces)==true)
							break;
						whiteKingCastlingMaskTemp&=~(1L<<indexSquare);
						indexSquare=Long.numberOfTrailingZeros(whiteKingCastlingMaskTemp);
					}
					if(indexSquare==numberOfSquarePerLine*numberOfSquarePerLine&&isThisSquareAttacked(pieceIndex,blackPieces,whitePieces,allPieces)==false)
						movePossibilities|=1L<<(whiteKingKingCastlingDestination);
				}
			}
			if(((whiteKing&1L<<(whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine))!=0)&&((whiteRooks&1L<<(leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*numberOfSquarePerLine))!=0)&&isWhiteKingHasMoved==false&&isWhiteLeftRookHasMoved==false)
			{
				long whiteQueenCastlingMaskTemp=whiteQueenCastlingMask;
				long allPiecesForCastling=(allPieces&whiteQueenCastlingMaskTemp);
				if(allPiecesForCastling==0)
				{
					int indexSquare=Long.numberOfTrailingZeros(whiteQueenCastlingMaskTemp);
					while(indexSquare!=numberOfSquarePerLine*numberOfSquarePerLine)
					{
						if(isThisSquareAttacked(indexSquare,blackPieces,whitePieces,allPieces)==true)
							break;
						whiteQueenCastlingMaskTemp&=~(1L<<indexSquare);
						indexSquare=Long.numberOfTrailingZeros(whiteQueenCastlingMaskTemp);
					}
					if(indexSquare==numberOfSquarePerLine*numberOfSquarePerLine&&isThisSquareAttacked(pieceIndex,blackPieces,whitePieces,allPieces)==false)
						movePossibilities|=1L<<(whiteKingQueenCastlingDestination);
				}
			}
		}
		else
		{
			if(((blackKing&1L<<(blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine))!=0)&&((blackRooks&1L<<(rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*numberOfSquarePerLine))!=0)&&isBlackKingHasMoved==false&&isBlackRightRookHasMoved==false)
			{
				long blackKingCastlingMaskTemp=blackKingCastlingMask;
				long allPiecesForCastling=(allPieces&blackKingCastlingMaskTemp);
				if(allPiecesForCastling==0)
				{
					int indexSquare=Long.numberOfTrailingZeros(blackKingCastlingMaskTemp);
					while(indexSquare!=numberOfSquarePerLine*numberOfSquarePerLine)
					{
						if(isThisSquareAttacked(indexSquare,blackPieces,blackPieces,allPieces)==true)
							break;
						blackKingCastlingMaskTemp&=~(1L<<indexSquare);
						indexSquare=Long.numberOfTrailingZeros(blackKingCastlingMaskTemp);
					}
					if(indexSquare==numberOfSquarePerLine*numberOfSquarePerLine&&isThisSquareAttacked(pieceIndex,blackPieces,blackPieces,allPieces)==false)
						movePossibilities|=1L<<(blackKingKingCastlingDestination);
				}
			}
			if(((blackKing&1L<<(blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine))!=0)&&((blackRooks&1L<<(leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*numberOfSquarePerLine))!=0)&&isBlackKingHasMoved==false&&isBlackLeftRookHasMoved==false)
			{
				long blackQueenCastlingMaskTemp=blackQueenCastlingMask;
				long allPiecesForCastling=(allPieces&blackQueenCastlingMaskTemp);
				if(allPiecesForCastling==0)
				{
					int indexSquare=Long.numberOfTrailingZeros(blackQueenCastlingMaskTemp);
					while(indexSquare!=numberOfSquarePerLine*numberOfSquarePerLine)
					{
						if(isThisSquareAttacked(indexSquare,blackPieces,blackPieces,allPieces)==true)
							break;
						blackQueenCastlingMaskTemp&=~(1L<<indexSquare);
						indexSquare=Long.numberOfTrailingZeros(blackQueenCastlingMaskTemp);
					}
					if(indexSquare==numberOfSquarePerLine*numberOfSquarePerLine&&isThisSquareAttacked(pieceIndex,blackPieces,blackPieces,allPieces)==false)
						movePossibilities|=1L<<(blackKingQueenCastlingDestination);
				}
			}
		}
		return movePossibilities;
	}
	
	public ArrayList<Point> undoCastling(String castlingDescription)
	{
		boolean[] isSpecial=new boolean[1];
		ArrayList<Point> arrayConcernedSquares=new ArrayList<Point>();
		if(currentTurn==white)
		{
			if(castlingDescription.equals(kingSideCastlingExplicit)==true||castlingDescription.equals(kingSideCastlingStandard)==true) // king side
			{
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,whiteRookKingCastlingDestination,rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*numberOfSquarePerLine,isSpecial);
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,whiteKingKingCastlingDestination,whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine,isSpecial);
				arrayConcernedSquares.add(new Point(whiteKingKingCastlingDestination%numberOfSquarePerLine,whiteKingKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(whiteRookKingCastlingDestination%numberOfSquarePerLine,whiteRookKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)rightWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteRightRookHasMoved=false;
				isWhiteKingHasMoved=false;
			}
			else // queen side
			{
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,whiteRookQueenCastlingDestination,leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*numberOfSquarePerLine,isSpecial);
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,whiteKingQueenCastlingDestination,whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine,isSpecial);
				arrayConcernedSquares.add(new Point(whiteKingQueenCastlingDestination%numberOfSquarePerLine,whiteKingQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(whiteRookQueenCastlingDestination%numberOfSquarePerLine,whiteRookQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)leftWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteLeftRookHasMoved=false;
				isWhiteKingHasMoved=false;
			}
		}
		else
		{
			if(castlingDescription.equals(kingSideCastlingExplicit)==true||castlingDescription.equals(kingSideCastlingStandard)==true)
			{
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,blackRookKingCastlingDestination,rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*numberOfSquarePerLine,isSpecial);
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,blackKingKingCastlingDestination,blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine,isSpecial);
				arrayConcernedSquares.add(new Point(blackKingKingCastlingDestination%numberOfSquarePerLine,blackKingKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(blackRookKingCastlingDestination%numberOfSquarePerLine,blackRookKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)rightBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackRightRookHasMoved=false;
				isBlackKingHasMoved=false;
			}
			else
			{
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,blackRookQueenCastlingDestination,leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*numberOfSquarePerLine,isSpecial);
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,blackKingQueenCastlingDestination,blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine,isSpecial);
				arrayConcernedSquares.add(new Point(blackKingQueenCastlingDestination%numberOfSquarePerLine,blackKingQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(blackRookQueenCastlingDestination%numberOfSquarePerLine,blackRookQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)leftBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackLeftRookHasMoved=false;
				isBlackKingHasMoved=false;
			}
		}
		return arrayConcernedSquares;
	}
	
	public ArrayList<Point> doCastling(String castlingDescription)
	{
		boolean[] isSpecial=new boolean[1];
		ArrayList<Point> arrayConcernedSquares=new ArrayList<Point>();
		if(currentTurn==white)
		{
			if(castlingDescription.equals(kingSideCastlingExplicit)==true||castlingDescription.equals(kingSideCastlingStandard)==true)
			{
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*numberOfSquarePerLine,whiteRookKingCastlingDestination,isSpecial);
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine,whiteKingKingCastlingDestination,isSpecial);
				arrayConcernedSquares.add(new Point(whiteKingKingCastlingDestination%numberOfSquarePerLine,whiteKingKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(whiteRookKingCastlingDestination%numberOfSquarePerLine,whiteRookKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)rightWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteRightRookHasMoved=true;
				isWhiteKingHasMoved=true;
			}
			else
			{
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*numberOfSquarePerLine,whiteRookQueenCastlingDestination,isSpecial);
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,whiteKingInitialPosition.x+whiteKingInitialPosition.y*numberOfSquarePerLine,whiteKingQueenCastlingDestination,isSpecial);
				arrayConcernedSquares.add(new Point(whiteKingQueenCastlingDestination%numberOfSquarePerLine,whiteKingQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(whiteRookQueenCastlingDestination%numberOfSquarePerLine,whiteRookQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)leftWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteLeftRookHasMoved=true;
				isWhiteKingHasMoved=true;
			}
		}
		else
		{
			if(castlingDescription.equals(kingSideCastlingExplicit)==true||castlingDescription.equals(kingSideCastlingStandard)==true)
			{
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*numberOfSquarePerLine,blackRookKingCastlingDestination,isSpecial);
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine,blackKingKingCastlingDestination,isSpecial);
				arrayConcernedSquares.add(new Point(blackKingKingCastlingDestination%numberOfSquarePerLine,blackKingKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(blackRookKingCastlingDestination%numberOfSquarePerLine,blackRookKingCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)rightBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackRightRookHasMoved=true;
				isBlackKingHasMoved=true;
			}
			else
			{
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*numberOfSquarePerLine,blackRookQueenCastlingDestination,isSpecial);
				MakeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,blackKingInitialPosition.x+blackKingInitialPosition.y*numberOfSquarePerLine,blackKingQueenCastlingDestination,isSpecial);
				arrayConcernedSquares.add(new Point(blackKingQueenCastlingDestination%numberOfSquarePerLine,blackKingQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add(new Point(blackRookQueenCastlingDestination%numberOfSquarePerLine,blackRookQueenCastlingDestination/numberOfSquarePerLine));
				arrayConcernedSquares.add((Point)leftBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackLeftRookHasMoved=true;
				isBlackKingHasMoved=true;
			}
		}
		return arrayConcernedSquares;
	}
	
	// we investigate to know is three moves repetition occurs
	public boolean isThisMoveHasToBeRemovedDueToThreeRepetitionsLaw(int color,int pieceType,int indexSource,int indexDestination)
	{
		boolean[] isSpecial=new boolean[1];
		int pieceEventuallydeleted=MakeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,indexSource,indexDestination,isSpecial);
		PiecesSituation piecesSituation=new PiecesSituation(whiteKnights,whiteBishops,whiteQueens,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueens,blackKing,blackPawns,blackRooks);
		for(int counterSituations=0;counterSituations<listPiecesSituation.size();counterSituations++)
			if(listPiecesSituation.get(counterSituations).equal(piecesSituation)==true&&listPiecesSituationOccurrences.get(counterSituations)+1>=maximumOccurrenceForASituation)
			{
				undoMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,indexDestination,indexSource,pieceEventuallydeleted,isSpecial[0]);
				return true;
			}
		undoMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,indexDestination,indexSource,pieceEventuallydeleted,isSpecial[0]);
		return false;
	}
	
	public int IsItDoublePawnMoveForEnPassant(Point oldSelectedSquare,Point newSelectedSquare)
	{
		switch(currentTurn)
		{
		case white:
			if(oldSelectedSquare.y==firstLeftWhitePawnsInitialPosition.y&&newSelectedSquare.y==firstLeftWhitePawnsInitialPosition.y-2&&getPieceTypeAtThisIndexWithCurrentColor(newSelectedSquare.x+newSelectedSquare.y*numberOfSquarePerLine)==pawnId)
				return newSelectedSquare.x+newSelectedSquare.y*numberOfSquarePerLine;
			break;
		case black:
			if(oldSelectedSquare.y==firstLeftBlackPawnsInitialPosition.y&&newSelectedSquare.y==firstLeftBlackPawnsInitialPosition.y+2&&getPieceTypeAtThisIndexWithCurrentColor(newSelectedSquare.x+newSelectedSquare.y*numberOfSquarePerLine)==pawnId) // ici
				return newSelectedSquare.x+newSelectedSquare.y*numberOfSquarePerLine;
			break;
		default:
			;
		}
		return -1;
	}
	
	public int getBlackPieceTypeWithTarget(long target)
	{
		if((blackPawns&target)!=0)
			return pawnId;
		else if((blackRooks&target)!=0)
			return rookId;
		else if((blackBishops&target)!=0)
			return bishopId;
		else if((blackKnights&target)!=0)
			return knightId;
		else if((blackQueens&target)!=0)
			return queenId;
		return kingId;
	}
	
	public int deleteAndgetWhitePieceType(int indexPiece,long whitePiece)
	{
		if((whitePiece&1L<<indexPiece)==0)
			return noPieceId;
		else if((whitePawns&1L<<indexPiece)!=0)
		{
			whitePawns&=~(1L<<indexPiece);
			return pawnId;
		}
		else if((whiteRooks&1L<<indexPiece)!=0)
		{
			whiteRooks&=~(1L<<indexPiece);
			return rookId;
		}
		else if((whiteBishops&1L<<indexPiece)!=0)
		{
			whiteBishops&=~(1L<<indexPiece);
			return bishopId;
		}
		else if((whiteKnights&1L<<indexPiece)!=0)
		{
			whiteKnights&=~(1L<<indexPiece);
			return knightId;
		}
		else if((whiteQueens&1L<<indexPiece)!=0)
		{
			whiteQueens&=~(1L<<indexPiece);
			return queenId;
		}
		else
		{
			whiteKing&=~(1L<<indexPiece);
			return kingId;
		}
	}
	
	public int deleteEventualBlackPieceAndGetAlpha(int currentTurnParameter,int currentDepth,int currentEvaluation,int alpha,int beta,long target)
	{
		if((blackPawns&target)!=0)
		{
			blackPawns&=~target;
			alphaTemp=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation+pawnId,alpha,beta));
			blackPawns|=target;
			return alphaTemp;
		}
		else if((blackRooks&target)!=0)
		{
			blackRooks&=~target;
			alphaTemp=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation+rookId,alpha,beta));
			blackRooks|=target;
			return alphaTemp;
		}
		else if((blackBishops&target)!=0)
		{
			blackBishops&=~target;
			alphaTemp=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation+bishopId,alpha,beta));
			blackBishops|=target;
			return alphaTemp;
		}
		else if((blackKnights&target)!=0)
		{
			blackKnights&=~target;
			alphaTemp=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation+knightId,alpha,beta));
			blackKnights|=target;
			return alphaTemp;
		}
		else if((blackQueens&target)!=0)
		{
			blackQueens&=~target;
			alphaTemp=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation+queenId,alpha,beta));
			blackQueens|=target;
			return alphaTemp;
		}
		return kingId; // no need to do more
	}
	
	public int deleteEventualWhitePieceAndGetBeta(int currentTurnParameter,int currentDepth,int currentEvaluation,int alpha,int beta,long target)
	{
		if((whitePawns&target)!=0)
		{
			whitePawns&=~target;
			betaTemp=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation-pawnId,alpha,beta));
			whitePawns|=target;
			return betaTemp;
		}
		else if((whiteRooks&target)!=0)
		{
			whiteRooks&=~target;
			betaTemp=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation-rookId,alpha,beta));
			whiteRooks|=target;
			return betaTemp;
		}
		else if((whiteBishops&target)!=0)
		{
			whiteBishops&=~target;
			betaTemp=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation-bishopId,alpha,beta));
			whiteBishops|=target;
			return betaTemp;
		}
		else if((whiteKnights&target)!=0)
		{
			whiteKnights&=~target;
			betaTemp=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation-knightId,alpha,beta));
			whiteKnights|=target;
			return betaTemp;
		}
		else if((whiteQueens&target)!=0)
		{
			whiteQueens&=~target;
			betaTemp=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation-queenId,alpha,beta));
			whiteQueens|=target;
			return betaTemp;
		}
		return -kingId;
	}
	
	public int deleteAndgetBlackPieceType(int indexPiece,long blackPiece)
	{
		if((blackPiece&1L<<indexPiece)==0)
			return noPieceId;
		else if((blackPawns&1L<<indexPiece)!=0)
		{
			blackPawns&=~(1L<<indexPiece);
			return pawnId;
		}
		else if((blackRooks&1L<<indexPiece)!=0)
		{
			blackRooks&=~(1L<<indexPiece);
			return rookId;
		}
		else if((blackBishops&1L<<indexPiece)!=0)
		{
			blackBishops&=~(1L<<indexPiece);
			return bishopId;
		}
		else if((blackKnights&1L<<indexPiece)!=0)
		{
			blackKnights&=~(1L<<indexPiece);
			return knightId;
		}
		else if((blackQueens&1L<<indexPiece)!=0)
		{
			blackQueens&=~(1L<<indexPiece);
			return queenId;
		}
		else
		{
			blackKing&=~(1L<<indexPiece);
			return kingId;
		}
	}
	
	public int getWhitePieceTypeWithTarget(long target)
	{
		if((whitePawns&target)!=0)
			return pawnId;
		else if((whiteRooks&target)!=0)
			return rookId;
		else if((whiteBishops&target)!=0)
			return bishopId;
		else if((whiteKnights&target)!=0)
			return knightId;
		else if((whiteQueens&target)!=0)
			return queenId;
		return kingId;
	}
	
	private int minMaxForZeroDepth(int currentTurnParameter,int currentEvaluation,int alpha,int beta)
	{
		//		counterMultithreading++;
		currentTurnParameter=-currentTurnParameter;
		long piecesTemp;
		long maskResult;
		long target;
		long movePossibilities;
		if(currentTurnParameter==white)
		{
			long opponentPieces=getBlackPieces();
			long ownPieces=getWhitePieces();
			long allPieces=ownPieces|opponentPieces;
			long reverseAllPieces=~allPieces;
			piecesTemp=whiteQueens;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities|=hashMapLinesPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					alpha=Math.max(alpha,currentEvaluation);
					if(beta<=alpha)
						return alpha;
				}
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(1L<<indexMoves));
					if(beta<=alpha)
						return alpha;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteRooks;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities=hashMapLinesPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					alpha=Math.max(alpha,currentEvaluation);
					if(beta<=alpha)
						return alpha;
				}
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(1L<<indexMoves));
					if(beta<=alpha)
						return alpha;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteBishops;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					alpha=Math.max(alpha,currentEvaluation);
					if(beta<=alpha)
						return alpha;
				}
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(1L<<indexMoves));
					if(beta<=alpha)
						return alpha;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteKnights;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				movePossibilities=knightMoves[indexPiece];
				if((movePossibilities&reverseAllPieces)!=0)
				{
					alpha=Math.max(alpha,currentEvaluation);
					if(beta<=alpha)
						return alpha;
				}
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(1L<<indexMoves));
					if(beta<=alpha)
						return alpha;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			int indexWhiteKing=Long.numberOfTrailingZeros(whiteKing);
			movePossibilities=kingMoves[indexWhiteKing];
			if((movePossibilities&reverseAllPieces)!=0)
			{
				alpha=Math.max(alpha,currentEvaluation);
				if(beta<=alpha)
					return alpha;
			}
			movePossibilities&=opponentPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
			{
				alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(1L<<indexMoves));
				if(beta<=alpha)
					return alpha;
				movePossibilities&=movePossibilities-1;
			}
			
			piecesTemp=whitePawns;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				if((allPieces&1L<<indexPiece-numberOfSquarePerLine)==0)
				{
					// move one square forward
					alpha=Math.max(alpha,currentEvaluation);
					if(beta<=alpha)
						return alpha;
				}
				
				// captures left
				target=1L<<(indexPiece-numberOfSquarePerLine-1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(target));
					if(beta<=alpha)
						return alpha;
				}
				
				// captures right
				target=1L<<(indexPiece-numberOfSquarePerLine+1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(target));
					if(beta<=alpha)
						return alpha;
				}
				
				// pawn promotion
				if(indexPiece/numberOfSquarePerLine==1)
				{
					if((allPieces&1L<<indexPiece-numberOfSquarePerLine)==0)
					{
						// move one square forward
						alpha=Math.max(alpha,currentEvaluation-pawnId+queenId);
						if(beta<=alpha)
							return alpha;
					}
					
					// captures left
					target=1L<<(indexPiece-numberOfSquarePerLine-1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
					{
						alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(target)-pawnId+queenId);
						if(beta<=alpha)
							return alpha;
					}
					
					// captures right
					target=1L<<(indexPiece-numberOfSquarePerLine+1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
					{
						alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(target)-pawnId+queenId);
						if(beta<=alpha)
							return alpha;
					}
				}
				piecesTemp&=piecesTemp-1;
			}
			return alpha;
		}
		else
		{
			long opponentPieces=getWhitePieces();
			long ownPieces=getBlackPieces();
			long allPieces=ownPieces|opponentPieces;
			long reverseAllPieces=~allPieces;
			piecesTemp=blackQueens;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities|=hashMapLinesPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					beta=Math.min(beta,currentEvaluation);
					if(beta<=alpha)
						return beta;
				}
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(1L<<indexMoves));
					if(beta<=alpha)
						return beta;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=blackRooks;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities=hashMapLinesPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					beta=Math.min(beta,currentEvaluation);
					if(beta<=alpha)
						return beta;
				}
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(1L<<indexMoves));
					if(beta<=alpha)
						return beta;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=blackBishops;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					beta=Math.min(beta,currentEvaluation);
					if(beta<=alpha)
						return beta;
				}
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(1L<<indexMoves));
					if(beta<=alpha)
						return beta;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=blackKnights;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				movePossibilities=knightMoves[indexPiece];
				if((movePossibilities&reverseAllPieces)!=0)
				{
					beta=Math.min(beta,currentEvaluation);
					if(beta<=alpha)
						return beta;
				}
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(1L<<indexMoves));
					if(beta<=alpha)
						return beta;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			int indexBlackKing=Long.numberOfTrailingZeros(blackKing);
			movePossibilities=kingMoves[indexBlackKing];
			if((movePossibilities&reverseAllPieces)!=0)
			{
				beta=Math.min(beta,currentEvaluation);
				if(beta<=alpha)
					return beta;
			}
			movePossibilities&=opponentPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
			{
				beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(1L<<indexMoves));
				if(beta<=alpha)
					return beta;
				movePossibilities&=movePossibilities-1;
			}
			
			piecesTemp=blackPawns;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				// move on square forward
				if((allPieces&1L<<indexPiece+numberOfSquarePerLine)==0)
				{
					beta=Math.min(beta,currentEvaluation);
					if(beta<=alpha)
						return beta;
				}
				
				// captures left
				target=1L<<(indexPiece+numberOfSquarePerLine-1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(target));
					if(beta<=alpha)
						return beta;
				}
				
				// captures right
				target=1L<<(indexPiece+numberOfSquarePerLine+1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(target));
					if(beta<=alpha)
						return beta;
				}
				
				// queen promotion
				if(indexPiece/numberOfSquarePerLine==numberOfSquarePerLine-2)
				{
					// move on square forward
					if((allPieces&1L<<indexPiece+numberOfSquarePerLine)==0)
					{
						beta=Math.min(beta,currentEvaluation+pawnId-queenId);
						if(beta<=alpha)
							return beta;
					}
					
					// captures left
					target=1L<<(indexPiece+numberOfSquarePerLine-1);
					if((opponentPieces&target)!=0&&indexPiece%numberOfSquarePerLine>0)
					{
						beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(target)+pawnId-queenId);
						if(beta<=alpha)
							return beta;
					}
					
					// captures right
					target=1L<<(indexPiece+numberOfSquarePerLine+1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
					{
						beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(target)+pawnId-queenId);
						if(beta<=alpha)
							return beta;
					}
				}
				piecesTemp&=piecesTemp-1;
			}
			return beta;
		}
	}
	
	private int minMax(int currentTurnParameter,int currentDepth,int currentEvaluation,int alpha,int beta)
	{
		currentDepth--;
		if(currentDepth==0)
			return minMaxForZeroDepth(currentTurnParameter,currentEvaluation,alpha,beta);
		currentTurnParameter=-currentTurnParameter;
		long saveBeforeWithoutPiece;
		long piecesTemp;
		long saveBeforeMove;
		long target;
		long captureMoves;
		long movePossibilities;
		long maskResult;
		if(currentTurnParameter==white)
		{
			long opponentPieces=getBlackPieces();
			long ownPieces=getWhitePieces();
			long allPieces=ownPieces|opponentPieces;
			long reverseAllPieces=~allPieces;
			piecesTemp=whiteQueens;
			saveBeforeMove=whiteQueens;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				whiteQueens&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteQueens;
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities|=hashMapLinesPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					whiteQueens|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteQueens=saveBeforeMove;
						return alpha;
					}
					whiteQueens=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					whiteQueens|=1L<<indexMoves;
					alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteQueens=saveBeforeMove;
						return alpha;
					}
					whiteQueens=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteQueens=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteRooks;
			saveBeforeMove=whiteRooks;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				whiteRooks&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteRooks;
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities=hashMapLinesPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					whiteRooks|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteRooks=saveBeforeMove;
						return alpha;
					}
					whiteRooks=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					whiteRooks|=target;
					alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteRooks=saveBeforeMove;
						return alpha;
					}
					whiteRooks=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteRooks=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteBishops;
			saveBeforeMove=whiteBishops;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				whiteBishops&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteBishops;
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					whiteBishops|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteBishops=saveBeforeMove;
						return alpha;
					}
					whiteBishops=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					whiteBishops|=target;
					alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteBishops=saveBeforeMove;
						return alpha;
					}
					whiteBishops=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteBishops=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteKnights;
			saveBeforeMove=whiteKnights;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				whiteKnights&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteKnights;
				movePossibilities=knightMoves[indexPiece];
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					whiteKnights|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteKnights=saveBeforeMove;
						return alpha;
					}
					whiteKnights=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					whiteKnights|=target;
					alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteKnights=saveBeforeMove;
						return alpha;
					}
					whiteKnights=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteKnights=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			saveBeforeMove=whiteKing;
			movePossibilities=kingMoves[Long.numberOfTrailingZeros(whiteKing)];
			captureMoves=movePossibilities&opponentPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
			{
				whiteKing=1L<<indexMoves;
				alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,whiteKing);
				if(beta<=alpha)
				{
					whiteKing=saveBeforeMove;
					return alpha;
				}
				captureMoves&=captureMoves-1;
			}
			movePossibilities&=reverseAllPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
			{
				whiteKing=1L<<indexMoves;
				alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
				if(beta<=alpha)
				{
					whiteKing=saveBeforeMove;
					return alpha;
				}
				movePossibilities&=movePossibilities-1;
			}
			whiteKing=saveBeforeMove;
			
			piecesTemp=whitePawns;
			saveBeforeMove=whitePawns;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				whitePawns&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whitePawns;
				
				// captures left
				target=1L<<(indexPiece-numberOfSquarePerLine-1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
				{
					whitePawns|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whitePawns=saveBeforeMove;
						return alpha;
					}
					whitePawns=saveBeforeWithoutPiece;
				}
				
				// captures right
				target=1L<<(indexPiece-numberOfSquarePerLine+1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
				{
					whitePawns|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whitePawns=saveBeforeMove;
						return alpha;
					}
					whitePawns=saveBeforeWithoutPiece;
				}
				
				// move forward
				if((allPieces&1L<<indexPiece-numberOfSquarePerLine)==0)
				{
					// move one square forward
					whitePawns|=1L<<indexPiece-numberOfSquarePerLine;
					alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whitePawns=saveBeforeMove;
						return alpha;
					}
					whitePawns=saveBeforeWithoutPiece;
					
					// move two square forward
					if(indexPiece/numberOfSquarePerLine==firstLeftWhitePawnsInitialPosition.y&&(allPieces&1L<<indexPiece-2*numberOfSquarePerLine)==0)
					{
						whitePawns|=1L<<indexPiece-2*numberOfSquarePerLine;
						alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
						whitePawns=saveBeforeWithoutPiece;
					}
				}
				
				// pawn promotion
				if(indexPiece/numberOfSquarePerLine==1)
				{
					long saveQueens=whiteQueens;
					if((allPieces&1L<<indexPiece-numberOfSquarePerLine)==0)
					{
						// move one square forward
						whiteQueens|=1L<<indexPiece-numberOfSquarePerLine;
						alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation-pawnId+queenId,alpha,beta));
						whiteQueens=saveQueens;
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
					
					// captures left
					target=1L<<(indexPiece-numberOfSquarePerLine-1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
					{
						whiteQueens|=target;
						alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation-pawnId+queenId,alpha,beta,target);
						whiteQueens=saveQueens;
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
					
					// captures right
					target=1L<<(indexPiece-numberOfSquarePerLine+1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
					{
						whiteQueens|=target;
						alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation-pawnId+queenId,alpha,beta,target);
						whiteQueens=saveQueens;
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
				}
				whitePawns=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			return alpha;
		}
		else
		{
			long opponentPieces=getWhitePieces();
			long ownPieces=getBlackPieces();
			long allPieces=ownPieces|opponentPieces;
			long reverseAllPieces=~allPieces;
			piecesTemp=blackQueens;
			saveBeforeMove=blackQueens;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				blackQueens&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackQueens;
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities|=hashMapLinesPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					blackQueens|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackQueens=saveBeforeMove;
						return beta;
					}
					blackQueens=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					blackQueens|=1L<<indexMoves;
					beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackQueens=saveBeforeMove;
						return beta;
					}
					blackQueens=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackQueens=saveBeforeMove;
			}
			
			saveBeforeMove=blackRooks;
			piecesTemp=blackRooks;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				blackRooks&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackRooks;
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities=hashMapLinesPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					blackRooks|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackRooks=saveBeforeMove;
						return beta;
					}
					blackRooks=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					blackRooks|=target;
					beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackRooks=saveBeforeMove;
						return beta;
					}
					blackRooks=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackRooks=saveBeforeMove;
			}
			
			piecesTemp=blackBishops;
			saveBeforeMove=blackBishops;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				blackBishops&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackBishops;
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					blackBishops|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackBishops=saveBeforeMove;
						return beta;
					}
					blackBishops=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					blackBishops|=target;
					beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackBishops=saveBeforeMove;
						return beta;
					}
					blackBishops=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackBishops=saveBeforeMove;
			}
			
			piecesTemp=blackKnights;
			saveBeforeMove=blackKnights;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				blackKnights&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackKnights;
				movePossibilities=knightMoves[indexPiece];
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					blackKnights|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackKnights=saveBeforeMove;
						return beta;
					}
					blackKnights=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					blackKnights|=target;
					beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackKnights=saveBeforeMove;
						return beta;
					}
					blackKnights=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackKnights=saveBeforeMove;
			}
			
			saveBeforeMove=blackKing;
			movePossibilities=kingMoves[Long.numberOfTrailingZeros(blackKing)];
			captureMoves=movePossibilities&opponentPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
			{
				blackKing=1L<<indexMoves;
				beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,blackKing);
				if(beta<=alpha)
				{
					blackKing=saveBeforeMove;
					return beta;
				}
				captureMoves&=captureMoves-1;
			}
			movePossibilities&=reverseAllPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
			{
				blackKing=1L<<indexMoves;
				beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
				if(beta<=alpha)
				{
					blackKing=saveBeforeMove;
					return beta;
				}
				movePossibilities&=movePossibilities-1;
			}
			blackKing=saveBeforeMove;
			
			piecesTemp=blackPawns;
			saveBeforeMove=blackPawns;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				blackPawns&=~(1L<<indexPiece); // in all the case the pawn source is no more
				saveBeforeWithoutPiece=blackPawns;
				
				// captures left
				target=1L<<(indexPiece+numberOfSquarePerLine-1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
				{
					blackPawns|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackPawns=saveBeforeMove;
						return beta;
					}
					blackPawns=saveBeforeWithoutPiece;
				}
				
				// captures right
				target=1L<<(indexPiece+numberOfSquarePerLine+1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
				{
					blackPawns|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackPawns=saveBeforeMove;
						return beta;
					}
					blackPawns=saveBeforeWithoutPiece;
				}
				
				if((allPieces&1L<<indexPiece+numberOfSquarePerLine)==0)
				{
					// move on square forward
					blackPawns|=1L<<indexPiece+numberOfSquarePerLine;
					beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackPawns=saveBeforeMove;
						return beta;
					}
					blackPawns=saveBeforeWithoutPiece;
					
					// move two square forward
					if(indexPiece/numberOfSquarePerLine==firstLeftBlackPawnsInitialPosition.y&&(allPieces&1L<<indexPiece+2*numberOfSquarePerLine)==0)
					{
						blackPawns|=1L<<indexPiece+2*numberOfSquarePerLine;
						beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
						blackPawns=saveBeforeWithoutPiece;
					}
				}
				
				// queen promotion
				if(indexPiece/numberOfSquarePerLine==numberOfSquarePerLine-2)
				{
					long saveQueens=blackQueens;
					// move on square forward
					if((allPieces&1L<<indexPiece+numberOfSquarePerLine)==0)
					{
						blackQueens|=1L<<indexPiece-numberOfSquarePerLine;
						beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation+pawnId-queenId,alpha,beta));
						blackQueens=saveQueens;
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
					
					// captures left
					target=1L<<(indexPiece+numberOfSquarePerLine-1);
					if((opponentPieces&target)!=0&&indexPiece%numberOfSquarePerLine>0)
					{
						blackQueens|=target;
						beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation+pawnId-queenId,alpha,beta,target);
						blackQueens=saveQueens;
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
					
					// captures right
					target=1L<<(indexPiece+numberOfSquarePerLine+1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
					{
						blackQueens|=target;
						beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation+pawnId-queenId,alpha,beta,target);
						blackQueens=saveQueens;
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
				}
				blackPawns=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			return beta;
		}
	}
	
	int evaluateCurrentSituation()
	{
		return (Long.bitCount(whitePawns)-Long.bitCount(blackPawns))*pawnId+(Long.bitCount(whiteQueens)-Long.bitCount(blackQueens))*queenId+(Long.bitCount(whiteKing)-Long.bitCount(blackKing))*kingId+(Long.bitCount(whiteBishops)-Long.bitCount(blackBishops))*bishopId+(Long.bitCount(whiteKnights)-Long.bitCount(blackKnights))*knightId+(Long.bitCount(whiteRooks)-Long.bitCount(blackRooks))*rookId;
	}
	
	int evaluatePositioningSituation()
	{
		int saveTurn=currentTurn;
		long whitePieces=getWhitePieces();
		long blackPieces=getBlackPieces();
		long allPieces=getAllPieces();
		currentTurn=white;
		int whiteQueenCount=0;
		long piecesTemp=whiteQueens;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whiteQueenCount+=Long.bitCount(getQueensMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int whiteRookCount=0;
		piecesTemp=whiteRooks;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whiteRookCount+=Long.bitCount(getLinesMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int whiteBishopCount=0;
		piecesTemp=whiteBishops;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whiteBishopCount+=Long.bitCount(getDiagonalsMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int whiteKnightCount=0;
		piecesTemp=whiteKnights;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whiteKnightCount+=Long.bitCount(getKnightMoves(indexPiece,whitePieces));
			piecesTemp&=piecesTemp-1;
		}
		int whitePawnCount=0;
		piecesTemp=whitePawns;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whitePawnCount+=Long.bitCount(getWhitePawnMoves(indexPiece,blackPieces,allPieces));
			piecesTemp&=piecesTemp-1;
		}
		int whiteKingCount=0;
		piecesTemp=whiteKing;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whiteKingCount+=Long.bitCount(getKingMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		currentTurn=black;
		int blackQueenCount=0;
		piecesTemp=blackQueens;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackQueenCount+=Long.bitCount(getQueensMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int blackRookCount=0;
		piecesTemp=blackRooks;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackRookCount+=Long.bitCount(getLinesMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int blackBishopCount=0;
		piecesTemp=blackBishops;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackBishopCount+=Long.bitCount(getDiagonalsMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int blackKnightCount=0;
		piecesTemp=blackKnights;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackKnightCount+=Long.bitCount(getKnightMoves(indexPiece,blackPieces));
			piecesTemp&=piecesTemp-1;
		}
		int blackPawnCount=0;
		piecesTemp=blackPawns;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackPawnCount+=Long.bitCount(getBlackPawnMoves(indexPiece,whitePieces,allPieces));
			piecesTemp&=piecesTemp-1;
		}
		int blackKingCount=0;
		piecesTemp=blackKing;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackKingCount+=Long.bitCount(getKingMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		currentTurn=saveTurn;
		return (whiteQueenCount-blackQueenCount)*queenId+((whiteRookCount-blackRookCount)*rookId)+((whiteBishopCount-blackBishopCount)*bishopId)+((whiteKnightCount-blackKnightCount)*knightId)+((whitePawnCount-blackPawnCount)*pawnId)+((whiteKingCount-blackKingCount)*kingIdValue);
	}
	
	public int deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(int currentTurnParameter,int currentDepth,int currentEvaluation,int alpha,int beta,long target)
	{
		if((blackPawns&target)!=0)
		{
			blackPawns&=~target;
			alphaTemp=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation+pawnId,alpha,beta));
			blackPawns|=target;
			return alphaTemp;
		}
		else if((blackRooks&target)!=0)
		{
			blackRooks&=~target;
			alphaTemp=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation+rookId,alpha,beta));
			blackRooks|=target;
			return alphaTemp;
		}
		else if((blackBishops&target)!=0)
		{
			blackBishops&=~target;
			alphaTemp=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation+bishopId,alpha,beta));
			blackBishops|=target;
			return alphaTemp;
		}
		else if((blackKnights&target)!=0)
		{
			blackKnights&=~target;
			alphaTemp=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation+knightId,alpha,beta));
			blackKnights|=target;
			return alphaTemp;
		}
		else if((blackQueens&target)!=0)
		{
			blackQueens&=~target;
			alphaTemp=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation+queenId,alpha,beta));
			blackQueens|=target;
			return alphaTemp;
		}
		return kingId;
	}
	
	public int deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(int currentTurnParameter,int currentDepth,int currentEvaluation,int alpha,int beta,long target)
	{
		if((whitePawns&target)!=0)
		{
			whitePawns&=~target;
			betaTemp=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation-pawnId,alpha,beta));
			whitePawns|=target;
			return betaTemp;
		}
		else if((whiteRooks&target)!=0)
		{
			whiteRooks&=~target;
			betaTemp=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation-rookId,alpha,beta));
			whiteRooks|=target;
			return betaTemp;
		}
		else if((whiteBishops&target)!=0)
		{
			whiteBishops&=~target;
			betaTemp=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation-bishopId,alpha,beta));
			whiteBishops|=target;
			return betaTemp;
		}
		else if((whiteKnights&target)!=0)
		{
			whiteKnights&=~target;
			betaTemp=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation-knightId,alpha,beta));
			whiteKnights|=target;
			return betaTemp;
		}
		else if((whiteQueens&target)!=0)
		{
			whiteQueens&=~target;
			betaTemp=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation-queenId,alpha,beta));
			whiteQueens|=target;
			return betaTemp;
		}
		return -kingId;
	}
	
	private int minMaxWithBeyondTheDepth(int currentTurnParameter,int currentDepth,int currentEvaluation,int alpha,int beta)
	{
		//		counterMultithreading++;
		currentTurnParameter=-currentTurnParameter;
		long saveBeforeWithoutPiece;
		long piecesTemp;
		long saveBeforeMove;
		long target;
		long maskResult;
		long movePossibilities;
		if(currentTurnParameter==white)
		{
			long opponentPieces=getBlackPieces();
			long ownPieces=getWhitePieces();
			long allPieces=ownPieces|opponentPieces;
			long reverseAllPieces=~allPieces;
			piecesTemp=whiteQueens;
			saveBeforeMove=whiteQueens;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities|=hashMapLinesPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					alpha=Math.max(alpha,currentEvaluation);
					if(beta<=alpha)
						return alpha;
				}
				movePossibilities&=opponentPieces;
				whiteQueens&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteQueens;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					whiteQueens|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteQueens=saveBeforeMove;
						return alpha;
					}
					whiteQueens=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteQueens=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			piecesTemp=whiteRooks;
			saveBeforeMove=whiteRooks;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities=hashMapLinesPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					alpha=Math.max(alpha,currentEvaluation);
					if(beta<=alpha)
						return alpha;
				}
				whiteRooks&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteRooks;
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					whiteRooks|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteRooks=saveBeforeMove;
						return alpha;
					}
					whiteRooks=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteRooks=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteBishops;
			saveBeforeMove=whiteBishops;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					alpha=Math.max(alpha,currentEvaluation);
					if(beta<=alpha)
						return alpha;
				}
				movePossibilities&=opponentPieces;
				whiteBishops&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteBishops;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					whiteBishops|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteBishops=saveBeforeMove;
						return alpha;
					}
					whiteBishops=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteBishops=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteKnights;
			saveBeforeMove=whiteKnights;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				movePossibilities=knightMoves[indexPiece];
				if((movePossibilities&reverseAllPieces)!=0)
				{
					alpha=Math.max(alpha,currentEvaluation);
					if(beta<=alpha)
						return alpha;
				}
				movePossibilities&=opponentPieces;
				whiteKnights&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteKnights;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					whiteKnights|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteKnights=saveBeforeMove;
						return alpha;
					}
					whiteKnights=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteKnights=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			movePossibilities=kingMoves[Long.numberOfTrailingZeros(whiteKing)];
			if((movePossibilities&reverseAllPieces)!=0)
			{
				alpha=Math.max(alpha,currentEvaluation);
				if(beta<=alpha)
					return alpha;
			}
			saveBeforeMove=whiteKing;
			movePossibilities&=opponentPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
			{
				whiteKing=1L<<indexMoves;
				alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,whiteKing);
				if(beta<=alpha)
				{
					whiteKing=saveBeforeMove;
					return alpha;
				}
				movePossibilities&=movePossibilities-1;
			}
			whiteKing=saveBeforeMove;
			
			piecesTemp=whitePawns;
			saveBeforeMove=whitePawns;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				// move forward, no need to test two square, because it require at least one square
				if((allPieces&1L<<indexPiece-numberOfSquarePerLine)==0)
				{
					// move one square forward
					alpha=Math.max(alpha,currentEvaluation);
					if(beta<=alpha)
						return alpha;
				}
				whitePawns&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whitePawns;
				
				// captures left
				target=1L<<(indexPiece-numberOfSquarePerLine-1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
				{
					whitePawns|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whitePawns=saveBeforeMove;
						return alpha;
					}
					whitePawns=saveBeforeWithoutPiece;
				}
				
				// captures right
				target=1L<<(indexPiece-numberOfSquarePerLine+1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
				{
					whitePawns|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whitePawns=saveBeforeMove;
						return alpha;
					}
					whitePawns=saveBeforeWithoutPiece;
				}
				
				// pawn promotion
				if(indexPiece/numberOfSquarePerLine==1)
				{
					if((allPieces&1L<<indexPiece-numberOfSquarePerLine)==0)
					{
						// move one square forward
						alpha=Math.max(alpha,currentEvaluation-pawnId+queenId);
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
					
					long saveQueens=whiteQueens;
					// captures left
					target=1L<<(indexPiece-numberOfSquarePerLine-1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
					{
						whiteQueens|=target;
						alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation-pawnId+queenId,alpha,beta,target);
						whiteQueens=saveQueens;
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
					
					// captures right
					target=1L<<(indexPiece-numberOfSquarePerLine+1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
					{
						whiteQueens|=target;
						alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation-pawnId+queenId,alpha,beta,target);
						whiteQueens=saveQueens;
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
				}
				whitePawns=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			return alpha;
		}
		else
		{
			long opponentPieces=getWhitePieces();
			long ownPieces=getBlackPieces();
			long allPieces=ownPieces|opponentPieces;
			long reverseAllPieces=~allPieces;
			piecesTemp=blackQueens;
			saveBeforeMove=blackQueens;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities|=hashMapLinesPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					beta=Math.min(beta,currentEvaluation);
					if(beta<=alpha)
						return beta;
				}
				blackQueens&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackQueens;
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					blackQueens|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackQueens=saveBeforeMove;
						return beta;
					}
					blackQueens=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				
				piecesTemp&=piecesTemp-1;
				blackQueens=saveBeforeMove;
			}
			
			saveBeforeMove=blackRooks;
			piecesTemp=blackRooks;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities=hashMapLinesPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					beta=Math.min(beta,currentEvaluation);
					if(beta<=alpha)
						return beta;
				}
				movePossibilities&=opponentPieces;
				blackRooks&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackRooks;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					blackRooks|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackRooks=saveBeforeMove;
						return beta;
					}
					blackRooks=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackRooks=saveBeforeMove;
			}
			
			piecesTemp=blackBishops;
			saveBeforeMove=blackBishops;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				if((movePossibilities&reverseAllPieces)!=0)
				{
					beta=Math.min(beta,currentEvaluation);
					if(beta<=alpha)
						return beta;
				}
				blackBishops&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackBishops;
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					blackBishops|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackBishops=saveBeforeMove;
						return beta;
					}
					blackBishops=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackBishops=saveBeforeMove;
			}
			
			piecesTemp=blackKnights;
			saveBeforeMove=blackKnights;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				movePossibilities=knightMoves[indexPiece];
				if((movePossibilities&reverseAllPieces)!=0)
				{
					beta=Math.min(beta,currentEvaluation);
					if(beta<=alpha)
						return beta;
				}
				blackKnights&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackKnights;
				movePossibilities&=opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					blackKnights|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackKnights=saveBeforeMove;
						return beta;
					}
					blackKnights=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackKnights=saveBeforeMove;
			}
			
			movePossibilities=kingMoves[Long.numberOfTrailingZeros(blackKing)];
			if((movePossibilities&reverseAllPieces)!=0)
			{
				beta=Math.min(beta,currentEvaluation);
				if(beta<=alpha)
					return beta;
			}
			saveBeforeMove=blackKing;
			movePossibilities&=opponentPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
			{
				blackKing=1L<<indexMoves;
				beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,blackKing);
				if(beta<=alpha)
				{
					blackKing=saveBeforeMove;
					return beta;
				}
				movePossibilities&=movePossibilities-1;
			}
			blackKing=saveBeforeMove;
			
			piecesTemp=blackPawns;
			saveBeforeMove=blackPawns;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				if((allPieces&1L<<indexPiece+numberOfSquarePerLine)==0)
				{
					// move one square forward
					beta=Math.min(beta,currentEvaluation);
					if(beta<=alpha)
						return beta;
				}
				
				blackPawns&=~(1L<<indexPiece); // in all the case the pawn source is no more
				saveBeforeWithoutPiece=blackPawns;
				
				// captures left
				target=1L<<(indexPiece+numberOfSquarePerLine-1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
				{
					blackPawns|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackPawns=saveBeforeMove;
						return beta;
					}
					blackPawns=saveBeforeWithoutPiece;
				}
				
				// captures right
				target=1L<<(indexPiece+numberOfSquarePerLine+1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
				{
					blackPawns|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackPawns=saveBeforeMove;
						return beta;
					}
					blackPawns=saveBeforeWithoutPiece;
				}
				
				// queen promotion
				if(indexPiece/numberOfSquarePerLine==numberOfSquarePerLine-2)
				{
					if((allPieces&1L<<indexPiece+numberOfSquarePerLine)==0)
					{
						// move one square forward
						beta=Math.min(beta,currentEvaluation+pawnId-queenId);
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
					
					long saveQueens=blackQueens;
					// captures left
					target=1L<<(indexPiece+numberOfSquarePerLine-1);
					if((opponentPieces&target)!=0&&indexPiece%numberOfSquarePerLine>0)
					{
						blackQueens|=target;
						beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation+pawnId-queenId,alpha,beta,target);
						blackQueens=saveQueens;
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
					
					// captures right
					target=1L<<(indexPiece+numberOfSquarePerLine+1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
					{
						blackQueens|=target;
						beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation+pawnId-queenId,alpha,beta,target);
						blackQueens=saveQueens;
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
				}
				blackPawns=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			return beta;
		}
	}
	
	private int minMaxWithPotentialQuiescence(int currentTurnParameter,int currentDepth,int currentEvaluation,int alpha,int beta)
	{
		currentDepth--;
		//		counterMultithreading++;		
		if(currentDepth==-depthGapForQuiescence)
			return minMaxForZeroDepth(currentTurnParameter,currentEvaluation,alpha,beta);
		if(currentDepth<=0)
			return minMaxWithBeyondTheDepth(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta);
		currentTurnParameter=-currentTurnParameter;
		long saveBeforeWithoutPiece;
		long piecesTemp;
		long saveBeforeMove;
		long target;
		long maskResult;
		long movePossibilities;
		long captureMoves;
		if(currentTurnParameter==white)
		{
			long opponentPieces=getBlackPieces();
			long ownPieces=getWhitePieces();
			long allPieces=ownPieces|opponentPieces;
			long reverseAllPieces=~allPieces;
			piecesTemp=whiteQueens;
			saveBeforeMove=whiteQueens;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				whiteQueens&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteQueens;
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities|=hashMapLinesPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					whiteQueens|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteQueens=saveBeforeMove;
						return alpha;
					}
					whiteQueens=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					whiteQueens|=1L<<indexMoves;
					alpha=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteQueens=saveBeforeMove;
						return alpha;
					}
					whiteQueens=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteQueens=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteRooks;
			saveBeforeMove=whiteRooks;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				whiteRooks&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteRooks;
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities=hashMapLinesPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					whiteRooks|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteRooks=saveBeforeMove;
						return alpha;
					}
					whiteRooks=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					whiteRooks|=target;
					alpha=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteRooks=saveBeforeMove;
						return alpha;
					}
					whiteRooks=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteRooks=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteBishops;
			saveBeforeMove=whiteBishops;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				whiteBishops&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteBishops;
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					whiteBishops|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteBishops=saveBeforeMove;
						return alpha;
					}
					whiteBishops=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					whiteBishops|=target;
					alpha=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteBishops=saveBeforeMove;
						return alpha;
					}
					whiteBishops=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteBishops=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteKnights;
			saveBeforeMove=whiteKnights;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				whiteKnights&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteKnights;
				movePossibilities=knightMoves[indexPiece];
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					whiteKnights|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteKnights=saveBeforeMove;
						return alpha;
					}
					whiteKnights=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					whiteKnights|=target;
					alpha=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteKnights=saveBeforeMove;
						return alpha;
					}
					whiteKnights=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteKnights=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			saveBeforeMove=whiteKing;
			movePossibilities=kingMoves[Long.numberOfTrailingZeros(whiteKing)];
			captureMoves=movePossibilities&opponentPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
			{
				whiteKing=1L<<indexMoves;
				alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,whiteKing);
				if(beta<=alpha)
				{
					whiteKing=saveBeforeMove;
					return alpha;
				}
				captureMoves&=captureMoves-1;
			}
			movePossibilities&=reverseAllPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
			{
				whiteKing=1L<<indexMoves;
				alpha=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
				if(beta<=alpha)
				{
					whiteKing=saveBeforeMove;
					return alpha;
				}
				movePossibilities&=movePossibilities-1;
			}
			whiteKing=saveBeforeMove;
			
			long saveQueens=whiteQueens;
			piecesTemp=whitePawns;
			saveBeforeMove=whitePawns;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				whitePawns&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whitePawns;
				
				// captures left
				target=1L<<(indexPiece-numberOfSquarePerLine-1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
				{
					whitePawns|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whitePawns=saveBeforeMove;
						return alpha;
					}
					whitePawns=saveBeforeWithoutPiece;
				}
				
				// captures right
				target=1L<<(indexPiece-numberOfSquarePerLine+1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
				{
					whitePawns|=target;
					alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whitePawns=saveBeforeMove;
						return alpha;
					}
					whitePawns=saveBeforeWithoutPiece;
				}
				
				// move forward
				if((allPieces&1L<<indexPiece-numberOfSquarePerLine)==0)
				{
					// move one square forward
					whitePawns|=1L<<indexPiece-numberOfSquarePerLine;
					alpha=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whitePawns=saveBeforeMove;
						return alpha;
					}
					whitePawns=saveBeforeWithoutPiece;
					
					// move two square forward
					if(indexPiece/numberOfSquarePerLine==firstLeftWhitePawnsInitialPosition.y&&(allPieces&1L<<indexPiece-2*numberOfSquarePerLine)==0)
					{
						whitePawns|=1L<<indexPiece-2*numberOfSquarePerLine;
						alpha=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
						whitePawns=saveBeforeWithoutPiece;
					}
				}
				
				// pawn promotion
				if(indexPiece/numberOfSquarePerLine==1)
				{
					if((allPieces&1L<<indexPiece-numberOfSquarePerLine)==0)
					{
						// move one square forward
						whiteQueens|=1L<<indexPiece-numberOfSquarePerLine;
						alpha=Math.max(alpha,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation-pawnId+queenId,alpha,beta));
						whiteQueens=saveQueens;
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
					
					// captures left
					target=1L<<(indexPiece-numberOfSquarePerLine-1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
					{
						whiteQueens|=target;
						alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation-pawnId+queenId,alpha,beta,target);
						whiteQueens=saveQueens;
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
					
					// captures right
					target=1L<<(indexPiece-numberOfSquarePerLine+1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
					{
						whiteQueens|=target;
						alpha=deleteEventualBlackPieceAndGetAlphaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation-pawnId+queenId,alpha,beta,target);
						whiteQueens=saveQueens;
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
				}
				whitePawns=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			return alpha;
		}
		else
		{
			long opponentPieces=getWhitePieces();
			long ownPieces=getBlackPieces();
			long allPieces=ownPieces|opponentPieces;
			long reverseAllPieces=~allPieces;
			piecesTemp=blackQueens;
			saveBeforeMove=blackQueens;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				blackQueens&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackQueens;
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities|=hashMapLinesPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					blackQueens|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackQueens=saveBeforeMove;
						return beta;
					}
					blackQueens=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					blackQueens|=1L<<indexMoves;
					beta=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackQueens=saveBeforeMove;
						return beta;
					}
					blackQueens=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackQueens=saveBeforeMove;
			}
			
			saveBeforeMove=blackRooks;
			piecesTemp=blackRooks;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				blackRooks&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackRooks;
				maskResult=allPieces&arrayLinesMask[indexPiece];
				maskResult|=linesExtrems[indexPiece];
				movePossibilities=hashMapLinesPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					blackRooks|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackRooks=saveBeforeMove;
						return beta;
					}
					blackRooks=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					blackRooks|=target;
					beta=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackRooks=saveBeforeMove;
						return beta;
					}
					blackRooks=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackRooks=saveBeforeMove;
			}
			
			piecesTemp=blackBishops;
			saveBeforeMove=blackBishops;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				blackBishops&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackBishops;
				maskResult=allPieces&arrayDiagonalsMask[indexPiece];
				maskResult|=diagonalsExtrems[indexPiece];
				if(indexPiece==numberOfSquarePerLine-1||indexPiece==0)
					maskResult&=~(1L<<indexPiece);
				movePossibilities=hashMapDiagonalsPossibilities.get(maskResult);
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					blackBishops|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackBishops=saveBeforeMove;
						return beta;
					}
					blackBishops=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					blackBishops|=target;
					beta=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackBishops=saveBeforeMove;
						return beta;
					}
					blackBishops=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackBishops=saveBeforeMove;
			}
			
			piecesTemp=blackKnights;
			saveBeforeMove=blackKnights;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				blackKnights&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackKnights;
				movePossibilities=knightMoves[indexPiece];
				captureMoves=movePossibilities&opponentPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
				{
					target=1L<<indexMoves;
					blackKnights|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackKnights=saveBeforeMove;
						return beta;
					}
					blackKnights=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
				{
					target=1L<<indexMoves;
					blackKnights|=target;
					beta=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackKnights=saveBeforeMove;
						return beta;
					}
					blackKnights=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackKnights=saveBeforeMove;
			}
			
			saveBeforeMove=blackKing;
			movePossibilities=kingMoves[Long.numberOfTrailingZeros(blackKing)];
			captureMoves=movePossibilities&opponentPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(captureMoves);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(captureMoves))
			{
				blackKing=1L<<indexMoves;
				beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,blackKing);
				if(beta<=alpha)
				{
					blackKing=saveBeforeMove;
					return beta;
				}
				captureMoves&=captureMoves-1;
			}
			movePossibilities&=reverseAllPieces;
			for(int indexMoves=Long.numberOfTrailingZeros(movePossibilities);indexMoves!=Long.SIZE;indexMoves=Long.numberOfTrailingZeros(movePossibilities))
			{
				blackKing=1L<<indexMoves;
				beta=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
				if(beta<=alpha)
				{
					blackKing=saveBeforeMove;
					return beta;
				}
				movePossibilities&=movePossibilities-1;
			}
			blackKing=saveBeforeMove;
			
			piecesTemp=blackPawns;
			saveBeforeMove=blackPawns;
			long saveQueens=blackQueens;
			for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
			{
				blackPawns&=~(1L<<indexPiece); // in all the case the pawn source is no more
				saveBeforeWithoutPiece=blackPawns;
				
				// captures left
				target=1L<<(indexPiece+numberOfSquarePerLine-1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine>0))
				{
					blackPawns|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackPawns=saveBeforeMove;
						return beta;
					}
					blackPawns=saveBeforeWithoutPiece;
				}
				
				// captures right
				target=1L<<(indexPiece+numberOfSquarePerLine+1);
				if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
				{
					blackPawns|=target;
					beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackPawns=saveBeforeMove;
						return beta;
					}
					blackPawns=saveBeforeWithoutPiece;
				}
				
				if((allPieces&1L<<indexPiece+numberOfSquarePerLine)==0)
				{
					// move on square forward
					blackPawns|=1L<<indexPiece+numberOfSquarePerLine;
					beta=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackPawns=saveBeforeMove;
						return beta;
					}
					blackPawns=saveBeforeWithoutPiece;
					
					// move two square forward
					if(indexPiece/numberOfSquarePerLine==firstLeftBlackPawnsInitialPosition.y&&(allPieces&1L<<indexPiece+2*numberOfSquarePerLine)==0)
					{
						blackPawns|=1L<<indexPiece+2*numberOfSquarePerLine;
						beta=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
						blackPawns=saveBeforeWithoutPiece;
					}
				}
				
				// queen promotion
				if(indexPiece/numberOfSquarePerLine==numberOfSquarePerLine-2)
				{
					if((allPieces&1L<<indexPiece+numberOfSquarePerLine)==0)
					{
						// move on square forward
						blackQueens|=1L<<numberOfSquarePerLine-1;
						beta=Math.min(beta,minMaxWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation+pawnId-queenId,alpha,beta));
						blackQueens=saveQueens;
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
					
					// captures left
					target=1L<<(indexPiece+numberOfSquarePerLine-1);
					if((opponentPieces&target)!=0&&indexPiece%numberOfSquarePerLine>0)
					{
						blackQueens|=target;
						beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation+pawnId-queenId,alpha,beta,target);
						blackQueens=saveQueens;
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
					
					// captures right
					target=1L<<(indexPiece+numberOfSquarePerLine+1);
					if((opponentPieces&target)!=0&&(indexPiece%numberOfSquarePerLine<numberOfSquarePerLine-1))
					{
						blackQueens|=target;
						beta=deleteEventualWhitePieceAndGetBetaWithPotentialQuiescence(currentTurnParameter,currentDepth,currentEvaluation+pawnId-queenId,alpha,beta,target);
						blackQueens=saveQueens;
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
				}
				blackPawns=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			return beta;
		}
	}
	
	@Override
	public void run()
	{
		currentTurn=currentTurnMultithreading;
		counterMultithreading=0;
		for(int counterMoves=beginIndexMultithreading;counterMoves<endIndexMultithreading;counterMoves++)
		{
			boolean isSpecial[]=new boolean[1];
			int pieceIdSource=getPieceTypeAtThisIndexAndWithThisColor(currentTurnMultithreading,listSourceForMultithreading.get(counterMoves));
			int pieceEventualydeleted=MakeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceIdSource,listSourceForMultithreading.get(counterMoves),listDestinationForMultithreading.get(counterMoves),isSpecial);
			int alpha=-infinite;
			int beta=-alpha;
			currentTurn=-currentTurn;
			int returnValue;
			if(useQuiescenceMultithreading==true)
			{
				if(depthForThreadComputing!=0)
					returnValue=minMaxWithPotentialQuiescence(-currentTurn,depthForThreadComputing,evaluateCurrentSituation(),alpha,beta);
				else
					returnValue=evaluateCurrentSituation();
			}
			else
			{
				if(depthForThreadComputing!=0)
					returnValue=minMax(-currentTurn,depthForThreadComputing,evaluateCurrentSituation(),alpha,beta);
				else
					returnValue=evaluateCurrentSituation();
			}
			currentTurn=-currentTurn;
			listValuesForMultithreading[counterMoves]=returnValue;
			undoMoveWithTwoIndexForCurrentTurnWithPieceId(pieceIdSource,listDestinationForMultithreading.get(counterMoves),listSourceForMultithreading.get(counterMoves),pieceEventualydeleted,isSpecial[0]);
		}
	}
	
	public ChessRuler(int currentTurnParameter,int beginIndexParameter,int endIndexParameter,int depthParameter,long whiteKnightsParameter,long whiteBishopsParameter,long whiteQueensParameter,long whiteKingParameter,long whitePawnsParameter,long whiteRooksParameter,long blackKnightsParameter,long blackBishopsParameter,long blackQueensParameter,long blackKingParameter,long blackPawnsParameter,long blackRooksParameter,int isLastMoveEnableEnPassantParameter,boolean useQuiescenceParameter)
	{
		currentTurnMultithreading=currentTurnParameter;
		depthForThreadComputing=depthParameter;
		beginIndexMultithreading=beginIndexParameter;
		endIndexMultithreading=endIndexParameter;
		whiteKnights=whiteKnightsParameter;
		whiteBishops=whiteBishopsParameter;
		whiteQueens=whiteQueensParameter;
		whiteKing=whiteKingParameter;
		whitePawns=whitePawnsParameter;
		whiteRooks=whiteRooksParameter;
		blackKnights=blackKnightsParameter;
		blackBishops=blackBishopsParameter;
		blackQueens=blackQueensParameter;
		blackKing=blackKingParameter;
		blackPawns=blackPawnsParameter;
		blackRooks=blackRooksParameter;
		isWhiteKingHasMoved=true;
		isBlackKingHasMoved=true;
		isBlackLeftRookHasMoved=true;
		isBlackRightRookHasMoved=true;
		isWhiteLeftRookHasMoved=true;
		isWhiteRightRookHasMoved=true;
		useQuiescenceMultithreading=useQuiescenceParameter;
	}
	
	private int playComputerAtOnlyASpecificLevel(int maximumDepth,ArrayList<Integer> listPointSource,ArrayList<Integer> listPointDestination,int isLastMoveEnableEnPassant,boolean useQuiescence) throws InterruptedException
	{
		listPointSource.clear();
		listPointDestination.clear();
		listValuesForMultithreading=new int[200];
		int numberOfCores=Runtime.getRuntime().availableProcessors();
		if(listSourceForMultithreading.size()<numberOfCores)
			numberOfCores=listSourceForMultithreading.size();
		int remainPossibleMoves=listSourceForMultithreading.size();
		ArrayList<ChessRuler> listChessRuler=new ArrayList<ChessRuler>();
		ArrayList<Thread> listThread=new ArrayList<Thread>();
		for(int counterCore=numberOfCores;counterCore>0;counterCore--)
		{
			float currentMovesFloat=remainPossibleMoves/counterCore;
			int currentMoves=(int)currentMovesFloat;
			if(currentMovesFloat>0)
			{
				if(currentMoves==0)
					currentMoves=remainPossibleMoves;
			}
			else
				break;
			remainPossibleMoves=remainPossibleMoves-currentMoves;
			ChessRuler instanceChessRulesMan=new ChessRuler(currentTurn,remainPossibleMoves,remainPossibleMoves+currentMoves,maximumDepth-1,whiteKnights,whiteBishops,whiteQueens,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueens,blackKing,blackPawns,blackRooks,isLastMoveEnableEnPassant,useQuiescence);
			listChessRuler.add(instanceChessRulesMan);
			Thread thread=new Thread(instanceChessRulesMan);
			listThread.add(thread);
			thread.start();
		}
		
		// we wait all threads done their work and count the number of evaluations
		int currentTotalCounter=0;
		for(int counterThread=0;counterThread<numberOfCores;counterThread++)
		{
			listThread.get(counterThread).join();
			currentTotalCounter+=listChessRuler.get(counterThread).counterMultithreading;
		}
		
		// we find the best value
		int bestEvaluation;
		if(currentTurn==white)
		{
			bestEvaluation=-infinite;
			for(int counterSource=0;counterSource<listSourceForMultithreading.size();counterSource++)
				if(bestEvaluation<listValuesForMultithreading[counterSource])
					bestEvaluation=listValuesForMultithreading[counterSource];
		}
		else
		{
			bestEvaluation=infinite;
			for(int counterSource=0;counterSource<listSourceForMultithreading.size();counterSource++)
				if(bestEvaluation>listValuesForMultithreading[counterSource])
					bestEvaluation=listValuesForMultithreading[counterSource];
		}
		for(int counterSource=0;counterSource<listSourceForMultithreading.size();counterSource++)
			if(listValuesForMultithreading[counterSource]==bestEvaluation)
			{
				listPointSource.add(listSourceForMultithreading.get(counterSource));
				listPointDestination.add(listDestinationForMultithreading.get(counterSource));
			}
		return currentTotalCounter;
	}
	
	void getMovesAtFirstLevelWithCheckChecking(int isLastMoveEnableEnPassant)
	{
		listSourceForMultithreading.clear();
		listDestinationForMultithreading.clear();
		if(currentTurn==black)
		{
			long blackPieces=getBlackPieces();
			for(int indexPiece=Long.numberOfTrailingZeros(blackPieces);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(blackPieces))
			{
				ArrayList<Integer> listDestination=getListOfPossibleMovesForAPieceWithCheckChecking(indexPiece,isLastMoveEnableEnPassant,false);
				listDestinationForMultithreading.addAll(listDestination);
				for(int counterDestination=0;counterDestination<listDestination.size();counterDestination++)
					listSourceForMultithreading.add(indexPiece);
				blackPieces&=blackPieces-1;
			}
		}
		else
		{
			long whitePieces=getWhitePieces();
			for(int indexPiece=Long.numberOfTrailingZeros(whitePieces);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(whitePieces))
			{
				ArrayList<Integer> listDestination=getListOfPossibleMovesForAPieceWithCheckChecking(indexPiece,isLastMoveEnableEnPassant,false);
				listDestinationForMultithreading.addAll(listDestination);
				for(int counterDestination=0;counterDestination<listDestination.size();counterDestination++)
					listSourceForMultithreading.add(indexPiece);
				whitePieces&=whitePieces-1;
			}
		}
	}
	
	public int playComputer(int maximumDepth,ArrayList<String> listMoveDescription,ArrayList<Point> listPointSourceFinal,ArrayList<Point> listPointDestinationFinal,boolean[] arrayIsSpecial,int isLastMoveEnableEnPassant,boolean useQuiescenceSearch) throws InterruptedException
	{
		isLastMoveEnableEnPassant=-1;
		getMovesAtFirstLevelWithCheckChecking(isLastMoveEnableEnPassant);
		ArrayList<Integer> listPointSource=new ArrayList<Integer>();
		ArrayList<Integer> listPointDestination=new ArrayList<Integer>();
		totalCounter=playComputerAtOnlyASpecificLevel(maximumDepth,listPointSource,listPointDestination,isLastMoveEnableEnPassant,useQuiescenceSearch);
		for(int counterDepth=1;counterDepth<maximumDepth;counterDepth++) // we filter lowest depths for blind problematic
		{
			if(listPointSource.size()==1)
				break;
			ArrayList<Integer> listPointSourceFilter=new ArrayList<Integer>();
			ArrayList<Integer> listPointDestinationFilter=new ArrayList<Integer>();
			playComputerAtOnlyASpecificLevel(counterDepth,listPointSourceFilter,listPointDestinationFilter,isLastMoveEnableEnPassant,false);
			ArrayList<Integer> listPointSourceTemp=new ArrayList<Integer>();
			ArrayList<Integer> listPointDestinationTemp=new ArrayList<Integer>();
			for(int counterMovesFilter=0;counterMovesFilter<listPointSourceFilter.size();counterMovesFilter++)
				for(int counterMovesOriginal=0;counterMovesOriginal<listPointSource.size();counterMovesOriginal++)
					if(listPointSource.get(counterMovesOriginal)==listPointSourceFilter.get(counterMovesFilter)&&listPointDestination.get(counterMovesOriginal)==listPointDestinationFilter.get(counterMovesFilter)&&listPointSource.size()>1)
					{
						listPointSourceTemp.add(listPointSource.get(counterMovesOriginal));
						listPointDestinationTemp.add(listPointDestination.get(counterMovesOriginal));
					}
			if(listPointSourceTemp.size()>0)
			{
				listPointSource.clear();
				listPointDestination.clear();
				for(int counterLowerFilter=0;counterLowerFilter<listPointSourceTemp.size();counterLowerFilter++)
				{
					listPointSource.add(listPointSourceTemp.get(counterLowerFilter));
					listPointDestination.add(listPointDestinationTemp.get(counterLowerFilter));
				}
			}
		}
		
		// now we have to delete move that will put in pat 
		int counterBestMoves=0;
		boolean arrayIndexToBedeleted[]=new boolean[maximumPossibleMoves];
		for(int counter=0;counter<maximumPossibleMoves;counter++)
			arrayIndexToBedeleted[counter]=false;
		int counterMovesdeleted=0;
		boolean isSpecial[]=new boolean[1];
		ArrayList<String> listeMoveDescriptionTemp=new ArrayList<String>();
		for(;counterBestMoves<listPointSource.size();counterBestMoves++)
		{
			int typeOfEventualydeletedPiece=getPieceTypeAtThisIndexAndWithThisColor(-currentTurn,listPointDestination.get(counterBestMoves));
			doThisMoveAndGetDescriptionWithoutIncrement(new Point(listPointSource.get(counterBestMoves)%numberOfSquarePerLine,listPointSource.get(counterBestMoves)/numberOfSquarePerLine),new Point(listPointDestination.get(counterBestMoves)%numberOfSquarePerLine,listPointDestination.get(counterBestMoves)/numberOfSquarePerLine),listeMoveDescriptionTemp,isSpecial,isLastMoveEnableEnPassant);
			ChangePlayerTurn();
			int winner=IfGameHasEndedGiveMeTheWinner(IsItDoublePawnMoveForEnPassant(new Point(listPointSource.get(counterBestMoves)%numberOfSquarePerLine,listPointSource.get(counterBestMoves)/numberOfSquarePerLine),new Point(listPointDestination.get(counterBestMoves)%numberOfSquarePerLine,listPointDestination.get(counterBestMoves)/numberOfSquarePerLine)));
			ChangePlayerTurn();
			if(winner!=0)
			{
				switch(winner)
				{
				case whiteIsPat:
				case blackIsPat:
					if(evaluateCurrentSituation()*currentTurn>0)
					{
						counterMovesdeleted++;
						arrayIndexToBedeleted[counterBestMoves]=true;
					}
				default:
					;
				}
			}
			undoMoveForWithoutRefreshRehearsalHistoric(new Point(listPointSource.get(counterBestMoves)%numberOfSquarePerLine,listPointSource.get(counterBestMoves)/numberOfSquarePerLine),new Point(listPointDestination.get(counterBestMoves)%numberOfSquarePerLine,listPointDestination.get(counterBestMoves)/numberOfSquarePerLine),typeOfEventualydeletedPiece,isSpecial[0]);
		}
		if(listSourceForMultithreading.size()>1&&counterMovesdeleted>0) // we delete only if there a least one issue, if not it's a desperate situation
		{
			ArrayList<Integer> listSourceTemp=new ArrayList<Integer>();
			ArrayList<Integer> listDestinationTemp=new ArrayList<Integer>();
			for(int counterMoves=0;counterMoves<listSourceForMultithreading.size();counterMoves++)
				if(arrayIndexToBedeleted[counterMoves]==false)
				{
					listSourceTemp.add(listSourceForMultithreading.get(counterMoves));
					listDestinationTemp.add(listDestinationForMultithreading.get(counterMoves));
				}
			listPointSource=listSourceTemp;
			listPointDestination=listDestinationTemp;
		}
		
		// we filter to get pawns move if there are
		ArrayList<Integer> listSourceTemp=new ArrayList<Integer>();
		ArrayList<Integer> listDestinationTemp=new ArrayList<Integer>();
		if(currentTurn==black)
		{
			int lowPawnVertical=-infinite;
			for(int counter=0;counter<listPointSource.size();counter++)
				if(getPieceTypeAtThisIndexWithCurrentColor(listPointSource.get(counter))==pawnId&&(listPointDestination.get(counter)>=lowPawnVertical))
				{
					if(listPointDestination.get(counter)>lowPawnVertical)
					{
						listSourceTemp.clear();
						listDestinationTemp.clear();
					}
					listSourceTemp.add(listPointSource.get(counter));
					listDestinationTemp.add(listPointDestination.get(counter));
					lowPawnVertical=listPointDestination.get(counter);
				}
		}
		else
		{
			int lowPawnVertical=infinite;
			for(int counter=0;counter<listPointSource.size();counter++)
				if(getPieceTypeAtThisIndexWithCurrentColor(listPointSource.get(counter))==pawnId&&(listPointDestination.get(counter)<=lowPawnVertical))
				{
					if(listPointDestination.get(counter)<lowPawnVertical)
					{
						listSourceTemp.clear();
						listDestinationTemp.clear();
					}
					listSourceTemp.add(listPointSource.get(counter));
					listDestinationTemp.add(listPointDestination.get(counter));
					lowPawnVertical=listPointDestination.get(counter);
				}
		}
		if(listSourceTemp.size()>0&&listPointSource.size()>1)
		{
			listPointSource=listSourceTemp;
			listPointDestination=listDestinationTemp;
		}
		
		// we get the best values according to moves possibilities
		isSpecial=new boolean[1];
		int bestPossibilities=infinite;
		if(currentTurn==white)
			bestPossibilities=-infinite;
		for(int counter=0;counter<listPointSource.size();counter++)
		{
			int pieceType=getPieceTypeAtThisIndexAndWithThisColor(currentTurn,listPointSource.get(counter));
			int pieceEventuallydeleted=MakeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,listPointSource.get(counter),listPointDestination.get(counter),isSpecial);
			int currentEvaluation=evaluatePositioningSituation();
			if(currentTurn==white)
			{
				if(currentEvaluation>=bestPossibilities)
				{
					if(currentEvaluation>bestPossibilities)
					{
						listPointSourceFinal.clear();
						listPointDestinationFinal.clear();
					}
					listPointSourceFinal.add(new Point(listPointSource.get(counter)%numberOfSquarePerLine,listPointSource.get(counter)/numberOfSquarePerLine));
					listPointDestinationFinal.add(new Point(listPointDestination.get(counter)%numberOfSquarePerLine,listPointDestination.get(counter)/numberOfSquarePerLine));
					bestPossibilities=currentEvaluation;
				}
			}
			if(currentTurn==black)
			{
				if(currentEvaluation<=bestPossibilities)
				{
					if(currentEvaluation<bestPossibilities)
					{
						listPointSourceFinal.clear();
						listPointDestinationFinal.clear();
					}
					listPointSourceFinal.add(new Point(listPointSource.get(counter)%numberOfSquarePerLine,listPointSource.get(counter)/numberOfSquarePerLine));
					listPointDestinationFinal.add(new Point(listPointDestination.get(counter)%numberOfSquarePerLine,listPointDestination.get(counter)/numberOfSquarePerLine));
					bestPossibilities=currentEvaluation;
				}
			}
			undoMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,listPointDestination.get(counter),listPointSource.get(counter),pieceEventuallydeleted,isSpecial[0]);
		}
		
		// otherwise make the first move
		listPointSourceFinal.get(0).x=listPointSourceFinal.get(0).x;
		listPointSourceFinal.get(0).y=listPointSourceFinal.get(0).y;
		listPointDestinationFinal.get(0).x=listPointDestinationFinal.get(0).x;
		listPointDestinationFinal.get(0).y=listPointDestinationFinal.get(0).y;
		return doThisMoveAndGetDescription(listPointSourceFinal.get(0),listPointDestinationFinal.get(0),listMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
	}
}
