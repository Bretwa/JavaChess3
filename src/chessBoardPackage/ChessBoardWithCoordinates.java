/*
This is the chessboard with coordinates panels close to it
*/

package chessBoardPackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JPanel;

public class ChessBoardWithCoordinates extends JPanel
{
	private static final long serialVersionUID=1L;
	private Box verticalBox;
	private Box horizontalBox;
	private ChessBoard chessBoard;
	private int squareSize=60;
	private int numberOfSquarePerLine=8;
	private TopAndBottomCoordinates topCoordinates;
	private TopAndBottomCoordinates bottomCoordinates;
	private LeftAndRightCoordinates leftCoordinates;
	private LeftAndRightCoordinates rightCoordinates;
	
	// constructor, put the four coordinates components
	public ChessBoardWithCoordinates(Graphics graphics,String piecesMatrixParameter[][])
	{
		setLayout(null);
		chessBoard=new ChessBoard(piecesMatrixParameter);
		bottomCoordinates=new TopAndBottomCoordinates(graphics);
		topCoordinates=new TopAndBottomCoordinates(graphics);
		leftCoordinates=new LeftAndRightCoordinates(graphics);
		rightCoordinates=new LeftAndRightCoordinates(graphics);
		horizontalBox=Box.createHorizontalBox();
		verticalBox=Box.createVerticalBox();
		verticalBox.add(topCoordinates);
		horizontalBox.add(leftCoordinates);
		horizontalBox.add(chessBoard);
		horizontalBox.add(rightCoordinates);
		verticalBox.add(horizontalBox);
		verticalBox.add(bottomCoordinates);
		verticalBox.setBounds(0,0,getDimension().width,getDimension().height);
		add(verticalBox);
		setPreferredSize(getDimension());
		setMaximumSize(getDimension());
	}
	
	public int giveMeThePieceColorOnThisSquare(Point pointCoordinates)
	{
		return chessBoard.giveMeThePieceColorOnThisSquare(pointCoordinates);
	}
	
	public Point getCorrespondingSquare(Point pointCoordinates)
	{
		return chessBoard.getCorrespondingSquare(new Point(pointCoordinates.x-leftCoordinates.getWidth(),pointCoordinates.y-topCoordinates.getHeight()));
	}
	
	// the dimension is calculated according to the coordinates sizes
	public Dimension getDimension()
	{
		return new Dimension(numberOfSquarePerLine*squareSize+leftCoordinates.getWidth()+rightCoordinates.getWidth(),numberOfSquarePerLine*squareSize+topCoordinates.getHeight()+bottomCoordinates.getHeight());
	}
	
	public void drawSeveralSquares(ArrayList<Point> possibleMoves)
	{
		chessBoard.drawSeveralSquares(possibleMoves);
	}
	
	public void drawSeveralSquares(ArrayList<Point> possibleMoves,Color colorParameter)
	{
		chessBoard.drawSeveralSquares(possibleMoves,colorParameter);
	}
	
	// draw a square according to a specific color
	public void drawASquare(Point PointParameter,Color colorParameter,Graphics graphics)
	{
		chessBoard.drawRectableToASquare(PointParameter,colorParameter,chessBoard.getGraphics());
	}
	
	// paint a square, used to refresh
	public void drawASquare(Point PointParameter,Graphics graphics)
	{
		chessBoard.drawASquare(PointParameter,chessBoard.getGraphics());
	}
	
	public void doMove(Point pointSource,Point pointDestination,int pixelIncrement,int timeToWait) throws InterruptedException
	{
		chessBoard.doMove(pointSource,pointDestination,pixelIncrement,timeToWait);
	}
	
	public BufferedImage getImageAccordingToString(String pieceId)
	{
		return chessBoard.getImageAccordingToString(pieceId);
	}
	
	public void turn180Degrees()
	{
		leftCoordinates.turn180Degrees();
		topCoordinates.turn180Degrees();
		rightCoordinates.turn180Degrees();
		bottomCoordinates.turn180Degrees();
	}
}
