package chessBoardPackage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class ChessBoard extends JComponent
{
	private static final long serialVersionUID=1L;
	private int white=1;
	private int black=-white;
	private int rectangleSelectionWidth=3;
	
	// the images for each kind of piece
	public BufferedImage whiteRookImage;
	public BufferedImage whiteKnightImage;
	public BufferedImage whiteBishopImage;
	public BufferedImage whiteQueenImage;
	public BufferedImage whiteKingImage;
	public BufferedImage whitePawnImage;
	public BufferedImage blackRookImage;
	public BufferedImage blackKnightImage;
	public BufferedImage blackBishopImage;
	public BufferedImage blackQueenImage;
	public BufferedImage blackKingImage;
	public BufferedImage blackPawnImage;
	
	public String whiteKnightImageFile="knight_white.png";
	public String whiteBishopImageFile="bishop_white.png";
	public String whiteRookImageFile="rook_white.png";
	public String whiteQueenImageFile="queen_white.png";
	public String whitePawnImageFile="pawn_white.png";
	public String whiteKingImageFile="king_white.png";
	public String blackQueenImageFile="queen_black.png";
	public String blackKingImageFile="king_black.png";
	public String blackPawnImageFile="pawn_black.png";
	public String blackRookImageFile="rook_black.png";
	public String blackKnightImageFile="knight_black.png";
	public String blackBishopImageFile="bishop_black.png";
	
	private int squareSize=60;
	private int numberOfSquarePerLine=8;
	String piecesMatrix[][];
	
	public ChessBoard(String piecesMatrixParameter[][])
	{
		piecesMatrix=piecesMatrixParameter;
		setPreferredSize(getDimension());
		try
		{
			whiteRookImage=ImageIO.read(getClass().getResourceAsStream(whiteRookImageFile));
			whiteKnightImage=ImageIO.read(getClass().getResourceAsStream(whiteKnightImageFile));
			whiteBishopImage=ImageIO.read(getClass().getResourceAsStream(whiteBishopImageFile));
			whiteQueenImage=ImageIO.read(getClass().getResourceAsStream(whiteQueenImageFile));
			whiteKingImage=ImageIO.read(getClass().getResourceAsStream(whiteKingImageFile));
			whitePawnImage=ImageIO.read(getClass().getResourceAsStream(whitePawnImageFile));
			blackRookImage=ImageIO.read(getClass().getResourceAsStream(blackRookImageFile));
			blackKnightImage=ImageIO.read(getClass().getResourceAsStream(blackKnightImageFile));
			blackBishopImage=ImageIO.read(getClass().getResourceAsStream(blackBishopImageFile));
			blackQueenImage=ImageIO.read(getClass().getResourceAsStream(blackQueenImageFile));
			blackKingImage=ImageIO.read(getClass().getResourceAsStream(blackKingImageFile));
			blackPawnImage=ImageIO.read(getClass().getResourceAsStream(blackPawnImageFile));
		}
		catch(IOException imageException)
		{
			imageException.printStackTrace();
		}
	}
	
	public Dimension getDimension()
	{
		return new Dimension(numberOfSquarePerLine*squareSize,numberOfSquarePerLine*squareSize);
	}
	
	// allow to put an image under another one considering a transparent color 
	public BufferedImage makeColorTransparent(BufferedImage imageParameter,Color color)
	{
		BufferedImage resultImage=new BufferedImage(imageParameter.getWidth(),imageParameter.getHeight(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics=resultImage.createGraphics();
		graphics.setComposite(AlphaComposite.Src);
		graphics.drawImage(imageParameter,null,0,0);
		graphics.dispose();
		for(int counterVertical=0;counterVertical<resultImage.getHeight();counterVertical++)
			for(int counterHorizontal=0;counterHorizontal<resultImage.getWidth();counterHorizontal++)
				if(resultImage.getRGB(counterHorizontal,counterVertical)==color.getRGB())
					resultImage.setRGB(counterHorizontal,counterVertical,0x8F1C1C);
		return resultImage;
	}
	
	public BufferedImage getImageAccordingToString(String pieceId)
	{
		switch(pieceId)
		{
		case "wr":
			return whiteRookImage;
		case "wk":
			return whiteKnightImage;
		case "wb":
			return whiteBishopImage;
		case "wq":
			return whiteQueenImage;
		case "wK":
			return whiteKingImage;
		case "wp":
			return whitePawnImage;
		case "br":
			return blackRookImage;
		case "bk":
			return blackKnightImage;
		case "bb":
			return blackBishopImage;
		case "bq":
			return blackQueenImage;
		case "bK":
			return blackKingImage;
		case "bp":
			return blackPawnImage;
		default:
			;
		}
		return null;
	}
	
	public void drawASquare(Point insertionPoint,Graphics graphics)
	{
		// first of all we repaint the square itself
		if(insertionPoint.y%2==0)
		{
			if(insertionPoint.x%2==0)
				graphics.setColor(Color.white);
			else
				graphics.setColor(Color.black);
		}
		else
		{
			if(insertionPoint.x%2==0)
				graphics.setColor(Color.black);
			else
				graphics.setColor(Color.white);
		}
		graphics.fillRect(insertionPoint.x*squareSize,insertionPoint.y*squareSize,squareSize-1,squareSize-1);
		graphics.drawRect(insertionPoint.x*squareSize,insertionPoint.y*squareSize,squareSize-1,squareSize-1);
		if(getImageAccordingToString(piecesMatrix[insertionPoint.y][insertionPoint.x])!=null)
			graphics.drawImage(makeColorTransparent(getImageAccordingToString(piecesMatrix[insertionPoint.y][insertionPoint.x]),Color.green),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
	}
	
	// repaint the whole chess board, each square actually
	@Override
	public void paintComponent(Graphics graphics)
	{
		for(int CounterVertical=0;CounterVertical<numberOfSquarePerLine;CounterVertical++)
			for(int CounterHorizontal=0;CounterHorizontal<numberOfSquarePerLine;CounterHorizontal++)
				drawASquare(new Point(CounterHorizontal,CounterVertical),graphics);
	}
	
	// retrieve a point in ChessBoard coordinates with pixels coordinates as input parameter
	public Point getCorrespondingSquare(Point pointCoordinates)
	{
		int horizontalSquareSelected=pointCoordinates.x/squareSize;
		int verticalSquareSelected=pointCoordinates.y/squareSize;
		if(horizontalSquareSelected<0||horizontalSquareSelected>=numberOfSquarePerLine||verticalSquareSelected<0||verticalSquareSelected>=numberOfSquarePerLine)
			return null;
		return new Point(horizontalSquareSelected,verticalSquareSelected);
	}
	
	public int giveMeThePieceColorOnThisSquare(Point pointCoordinates)
	{
		if(piecesMatrix[pointCoordinates.y][pointCoordinates.x].contains("w"))
			return white;
		if(piecesMatrix[pointCoordinates.y][pointCoordinates.x].contains("b"))
			return black;
		return 0;
	}
	
	// draw a square according to a specific color
	public void drawRectableToASquare(Point pointParameter,Color colorParameter,Graphics graphics)
	{
		int HorizontalSquareSelected=pointParameter.x;
		int VerticalSquareSelected=pointParameter.y;
		graphics.setColor(colorParameter);
		for(int RectangleWidth=0;RectangleWidth<rectangleSelectionWidth;RectangleWidth++)
		{
			// left
			graphics.drawLine(HorizontalSquareSelected*squareSize+RectangleWidth,VerticalSquareSelected*squareSize,HorizontalSquareSelected*squareSize+RectangleWidth,VerticalSquareSelected*squareSize+squareSize-1);
			
			// top
			graphics.drawLine(HorizontalSquareSelected*squareSize,VerticalSquareSelected*squareSize+RectangleWidth,HorizontalSquareSelected*squareSize+squareSize-1,VerticalSquareSelected*squareSize+RectangleWidth);
			
			// right
			graphics.drawLine(HorizontalSquareSelected*squareSize+squareSize-RectangleWidth-1,VerticalSquareSelected*squareSize,HorizontalSquareSelected*squareSize+squareSize-RectangleWidth-1,VerticalSquareSelected*squareSize+squareSize-1);
			
			// bottom
			graphics.drawLine(HorizontalSquareSelected*squareSize,VerticalSquareSelected*squareSize+squareSize-RectangleWidth-1,HorizontalSquareSelected*squareSize+squareSize-1,VerticalSquareSelected*squareSize+squareSize-RectangleWidth-1);
		}
	}
	
	// draw several squares in blue given in a list 
	public void drawSeveralSquares(ArrayList<Point> possibleMoves)
	{
		for(int counterMoves=0;counterMoves<possibleMoves.size();counterMoves++)
			drawASquare(possibleMoves.get(counterMoves),getGraphics());
	}
	
	// draw several squares in blue given in a list 
	public void drawSeveralSquares(ArrayList<Point> possibleMoves,Color colorParameter)
	{
		for(int counterMoves=0;counterMoves<possibleMoves.size();counterMoves++)
			drawRectableToASquare(possibleMoves.get(counterMoves),colorParameter,getGraphics());
	}
	
	public void doMove(Point pointSource,Point pointDestination,int pixelIncrement,int timeToWait) throws InterruptedException
	{
		Graphics graphics=getGraphics();
		BufferedImage imageSource=getImageAccordingToString(piecesMatrix[pointSource.y][pointSource.x]);
		String stringSource=piecesMatrix[pointSource.y][pointSource.x];
		Point insertionPoint=new Point(pointSource);
		insertionPoint.y=insertionPoint.y*squareSize;
		insertionPoint.x=insertionPoint.x*squareSize;
		piecesMatrix[pointSource.y][pointSource.x]="";
		if(pixelIncrement==-1) // it means that it's instantaneous move
			pixelIncrement=Math.max(Math.abs(pointSource.x-pointDestination.x),Math.abs(pointSource.y-pointDestination.y))*squareSize;
		if(stringSource.indexOf("k")!=-1)
		{
			// we take the smallest increment
			double horizontalDifference=pointSource.x*squareSize-pointDestination.x*squareSize;
			double verticalDifference=pointSource.y*squareSize-pointDestination.y*squareSize;
			double maxDifference=Math.max(Math.abs(horizontalDifference),Math.abs(verticalDifference));
			double incrementHorizontal=Math.abs(horizontalDifference/maxDifference);
			double incrementVertical=Math.abs(verticalDifference/maxDifference);
			double incrementRatio=incrementHorizontal/incrementVertical;
			if(incrementHorizontal==1)
			{
				incrementHorizontal*=pixelIncrement;
				incrementVertical=incrementHorizontal/incrementRatio;
			}
			else
			{
				incrementVertical*=pixelIncrement;
				incrementHorizontal=incrementRatio*incrementVertical;
			}
			if(pointSource.x<pointDestination.x)
			{
				if(pointSource.y<pointDestination.y) // bottom right
				{
					for(double counterVertical=pointSource.y*squareSize,counterHorizontal=pointSource.x*squareSize;counterVertical<pointDestination.y*squareSize;counterVertical+=incrementVertical,counterHorizontal+=incrementHorizontal)
					{
						graphics.drawImage(makeColorTransparent(imageSource,Color.green),(int)counterHorizontal,(int)counterVertical,null);
						Thread.sleep(timeToWait);
						drawASquare(new Point((int)counterHorizontal/squareSize,(int)counterVertical/squareSize),graphics);
						drawASquare(new Point((int)counterHorizontal/squareSize+1,(int)counterVertical/squareSize),graphics);
						drawASquare(new Point((int)counterHorizontal/squareSize,(int)counterVertical/squareSize+1),graphics);
						drawASquare(new Point((int)counterHorizontal/squareSize+1,(int)counterVertical/squareSize+1),graphics);
					}
				}
				if(pointSource.y>pointDestination.y) // top right
				{
					for(double counterVertical=pointSource.y*squareSize,counterHorizontal=pointSource.x*squareSize;counterVertical>pointDestination.y*squareSize;counterVertical-=incrementVertical,counterHorizontal+=incrementHorizontal)
					{
						graphics.drawImage(makeColorTransparent(imageSource,Color.green),(int)counterHorizontal,(int)counterVertical,null);
						Thread.sleep(timeToWait);
						drawASquare(new Point((int)counterHorizontal/squareSize,(int)counterVertical/squareSize),graphics);
						if((int)counterHorizontal/squareSize<numberOfSquarePerLine-1)
							drawASquare(new Point((int)counterHorizontal/squareSize+1,(int)counterVertical/squareSize),graphics);
						if((int)counterVertical/squareSize<numberOfSquarePerLine-1)
						{
							drawASquare(new Point((int)counterHorizontal/squareSize,(int)counterVertical/squareSize+1),graphics);
							if(counterHorizontal/squareSize<numberOfSquarePerLine-1)
								drawASquare(new Point((int)counterHorizontal/squareSize+1,(int)counterVertical/squareSize+1),graphics);
						}
					}
				}
			}
			if(pointSource.x>pointDestination.x)
			{
				if(pointSource.y<pointDestination.y) // bottom left
				{
					for(double counterVertical=pointSource.y*squareSize,counterHorizontal=pointSource.x*squareSize;counterVertical<pointDestination.y*squareSize;counterVertical+=incrementVertical,counterHorizontal-=incrementHorizontal)
					{
						graphics.drawImage(makeColorTransparent(imageSource,Color.green),(int)counterHorizontal,(int)counterVertical,null);
						Thread.sleep(timeToWait);
						drawASquare(new Point((int)counterHorizontal/squareSize,(int)counterVertical/squareSize),graphics);
						if(counterVertical/squareSize<numberOfSquarePerLine-1)
						{
							drawASquare(new Point((int)counterHorizontal/squareSize,(int)counterVertical/squareSize+1),graphics);
							if((int)counterHorizontal/squareSize<numberOfSquarePerLine-1)
								drawASquare(new Point((int)counterHorizontal/squareSize+1,(int)counterVertical/squareSize+1),graphics);
						}
						if((int)counterHorizontal/squareSize<numberOfSquarePerLine-1)
							drawASquare(new Point((int)counterHorizontal/squareSize+1,(int)counterVertical/squareSize),graphics);
					}
				}
				else // top left
				{
					for(double counterVertical=pointSource.y*squareSize,counterHorizontal=pointSource.x*squareSize;counterVertical>pointDestination.y*squareSize;counterVertical-=incrementVertical,counterHorizontal-=incrementHorizontal)
					{
						graphics.drawImage(makeColorTransparent(imageSource,Color.green),(int)counterHorizontal,(int)counterVertical,null);
						Thread.sleep(timeToWait);
						drawASquare(new Point((int)counterHorizontal/squareSize,(int)counterVertical/squareSize),graphics);
						if((int)counterHorizontal/squareSize<numberOfSquarePerLine-1)
							drawASquare(new Point((int)counterHorizontal/squareSize+1,(int)counterVertical/squareSize),graphics);
						if((int)counterVertical/squareSize<numberOfSquarePerLine-1)
						{
							drawASquare(new Point((int)counterHorizontal/squareSize,(int)counterVertical/squareSize+1),graphics);
							if((int)counterHorizontal/squareSize<numberOfSquarePerLine-1)
								drawASquare(new Point((int)counterHorizontal/squareSize+1,(int)counterVertical/squareSize+1),graphics);
						}
					}
				}
			}
			piecesMatrix[pointDestination.y][pointDestination.x]=stringSource;
			return;
		}
		
		// queen or bishop moves
		if(pointSource.y<pointDestination.y&&pointSource.x<pointDestination.x) // to bottom right
			for(double counterPixel=pointSource.x*squareSize;counterPixel<pointDestination.x*squareSize;counterPixel+=pixelIncrement/Math.sqrt(2))
			{
				graphics.drawImage(makeColorTransparent(imageSource,Color.green),(int)counterPixel,squareSize*pointSource.y+((int)counterPixel-pointSource.x*squareSize),null);
				Thread.sleep(timeToWait);
				drawASquare(new Point((int)counterPixel/squareSize,((int)counterPixel-pointSource.x*squareSize+pointSource.y*squareSize)/squareSize),graphics);
				drawASquare(new Point((int)counterPixel/squareSize+1,(((int)counterPixel-pointSource.x*squareSize+pointSource.y*squareSize))/squareSize),graphics);
				drawASquare(new Point((int)counterPixel/squareSize,((int)counterPixel-pointSource.x*squareSize+pointSource.y*squareSize)/squareSize+1),graphics);
				drawASquare(new Point((int)counterPixel/squareSize+1,(((int)counterPixel-pointSource.x*squareSize+pointSource.y*squareSize))/squareSize+1),graphics);
			}
		if(pointSource.y>pointDestination.y&&pointSource.x>pointDestination.x) // to top left
			for(double counterPixel=pointSource.x*squareSize;(int)counterPixel>pointDestination.x*squareSize;counterPixel-=pixelIncrement/Math.sqrt(2))
			{
				graphics.drawImage(makeColorTransparent(imageSource,Color.green),(int)counterPixel,squareSize*pointSource.y+((int)counterPixel-pointSource.x*squareSize),null);
				Thread.sleep(timeToWait);
				drawASquare(new Point((int)counterPixel/squareSize,((int)counterPixel-pointSource.x*squareSize+squareSize*pointSource.y)/squareSize),graphics);
				if((int)counterPixel/squareSize<numberOfSquarePerLine-1)
					drawASquare(new Point((int)counterPixel/squareSize+1,((int)counterPixel-pointSource.x*squareSize+squareSize*pointSource.y)/squareSize),graphics);
				if(((int)counterPixel-pointSource.x*squareSize+squareSize*pointSource.y)/squareSize<numberOfSquarePerLine-1)
				{
					drawASquare(new Point((int)counterPixel/squareSize,((int)counterPixel-pointSource.x*squareSize+squareSize*pointSource.y)/squareSize+1),graphics);
					if((int)counterPixel/squareSize<numberOfSquarePerLine-1)
						drawASquare(new Point((int)counterPixel/squareSize+1,((int)counterPixel-pointSource.x*squareSize+squareSize*pointSource.y)/squareSize+1),graphics);
				}
			}
		if(pointSource.y<pointDestination.y&&pointSource.x>pointDestination.x) // to bottom left
			for(double counterPixel=pointSource.x*squareSize;(int)counterPixel>pointDestination.x*squareSize;counterPixel-=pixelIncrement/Math.sqrt(2))
			{
				graphics.drawImage(makeColorTransparent(imageSource,Color.green),(int)counterPixel,squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize),null);
				Thread.sleep(timeToWait);
				drawASquare(new Point((int)counterPixel/squareSize,(squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize))/squareSize),graphics);
				if((squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize))/squareSize<numberOfSquarePerLine-1)
				{
					drawASquare(new Point((int)counterPixel/squareSize,(squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize))/squareSize+1),graphics);
					if((int)counterPixel/squareSize<numberOfSquarePerLine-1)
						drawASquare(new Point((int)counterPixel/squareSize+1,(squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize))/squareSize+1),graphics);
				}
				if((int)counterPixel/squareSize<numberOfSquarePerLine-1)
					drawASquare(new Point((int)counterPixel/squareSize+1,(squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize))/squareSize),graphics);
			}
		
		if(pointSource.y>pointDestination.y&&pointSource.x<pointDestination.x) // to top right
			for(double counterPixel=pointSource.x*squareSize;(int)counterPixel<pointDestination.x*squareSize;counterPixel+=pixelIncrement/Math.sqrt(2))
			{
				graphics.drawImage(makeColorTransparent(imageSource,Color.green),(int)counterPixel,squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize),null);
				Thread.sleep(timeToWait);
				drawASquare(new Point((int)counterPixel/squareSize,(squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize))/squareSize),graphics);
				if((int)counterPixel/squareSize<numberOfSquarePerLine-1)
					drawASquare(new Point((int)counterPixel/squareSize+1,(squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize))/squareSize),graphics);
				if((squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize))/squareSize<numberOfSquarePerLine-1)
				{
					drawASquare(new Point((int)counterPixel/squareSize,(squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize))/squareSize+1),graphics);
					if((int)counterPixel/squareSize<numberOfSquarePerLine-1)
						drawASquare(new Point((int)counterPixel/squareSize+1,(squareSize*pointSource.y-((int)counterPixel-pointSource.x*squareSize))/squareSize+1),graphics);
				}
			}
		
		// rook or queen moves
		if(pointSource.y==pointDestination.y&&pointSource.x<pointDestination.x)
			for(int counterPixel=pointSource.x*squareSize;counterPixel<pointDestination.x*squareSize;counterPixel+=pixelIncrement) // right
			{
				graphics.drawImage(makeColorTransparent(imageSource,Color.green),counterPixel,insertionPoint.y,null);
				Thread.sleep(timeToWait);
				drawASquare(new Point(counterPixel/squareSize,pointSource.y),graphics);
				drawASquare(new Point(counterPixel/squareSize+1,pointSource.y),graphics);
			}
		if(pointSource.y==pointDestination.y&&pointSource.x>pointDestination.x)
			for(int counterPixel=pointSource.x*squareSize-1;counterPixel>pointDestination.x*squareSize;counterPixel-=pixelIncrement) // left
			{
				graphics.drawImage(makeColorTransparent(imageSource,Color.green),counterPixel,insertionPoint.y,null);
				Thread.sleep(timeToWait);
				drawASquare(new Point(counterPixel/squareSize,pointSource.y),graphics);
				drawASquare(new Point(counterPixel/squareSize+1,pointSource.y),graphics);
			}
		if(pointSource.y<pointDestination.y&&pointSource.x==pointDestination.x)
			for(int counterPixel=pointSource.y*squareSize;counterPixel<pointDestination.y*squareSize;counterPixel+=pixelIncrement) // bottom
			{
				graphics.drawImage(makeColorTransparent(imageSource,Color.green),insertionPoint.x,counterPixel,null);
				Thread.sleep(timeToWait);
				drawASquare(new Point(pointSource.x,counterPixel/squareSize),graphics);
				drawASquare(new Point(pointSource.x,counterPixel/squareSize+1),graphics);
			}
		if(pointSource.y>pointDestination.y&&pointSource.x==pointDestination.x)
		{
			int counterPixel;
			for(counterPixel=pointSource.y*squareSize-1;counterPixel>pointDestination.y*squareSize;counterPixel-=pixelIncrement) // top
			{
				graphics.drawImage(makeColorTransparent(imageSource,Color.green),insertionPoint.x,counterPixel,null);
				Thread.sleep(timeToWait);
				drawASquare(new Point(pointSource.x,counterPixel/squareSize),graphics);
				drawASquare(new Point(pointSource.x,counterPixel/squareSize+1),graphics);
			}
			drawASquare(new Point(pointSource.x,counterPixel/squareSize),graphics);
			drawASquare(new Point(pointSource.x,counterPixel/squareSize+1),graphics);
		}
		piecesMatrix[pointDestination.y][pointDestination.x]=stringSource;
		drawASquare(pointDestination,graphics); // for deleting the target
	}
}