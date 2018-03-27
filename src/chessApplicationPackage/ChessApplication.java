package chessApplicationPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;

import chessBoardPackage.ChessBoardMouseListener;
import chessBoardPackage.ChessBoardWithCoordinates;
import informationPanelPackage.InformationPanel;

public class ChessApplication extends JFrame implements ActionListener
{
	private int maximumGameDescriptionLength=80;
	private int indexMoves=-1;
	private Box mainHorizontalBox;
	private Box mainVerticalBox;
	public static Point oldSelectedSquare;
	private static final int white=1;
	private static final int black=-white;
	private static final int whiteIsPat=2*white;
	private static final int blackIsPat=2*black;
	private ChessRuler chessRuler;
	public boolean whitesAtBottom=true;
	private static final int noCurrentGame=0;
	private static final int numberOfSquarePerLine=8;
	
	// for multithreading
	ArrayList<Point> listSourceForMultithreading=new ArrayList<Point>();
	ArrayList<Point> listDestinationForMultithreading=new ArrayList<Point>();
	
	// for the menu bar 
	JMenuBar menuBar;
	public static int maximumDepth=8;
	public static int defaultDepth=3;
	private static final String game="Game";
	private static final String quit="Quit";
	private static final String newGame="New game";
	private static final String saveGame="Save game";
	private static final String loadGame="Load game";
	public static String computerLevel="Computer level";
	public static String computerPlaysBlack="Computer plays black";
	public static String computerPlaysWhite="Computer plays white";
	private static final String moves="Moves";
	private static final String cancelMove="Cancel move";
	private static final String replayMove="Replay move";
	private static final String options="Options";
	private static final String turnChessboard="Turn chessboard";
	private static final String slidingMoves="Sliding moves";
	private static final String standardNotation="Standard notation";
	private static final String explicitNotation="Explicit notation";
	private static final String quiescenceSearch="Use quiescence search";
	private JMenuItem itemComputerPlaysBlack;
	private JMenuItem itemComputerPlaysWhite;
	private JCheckBoxMenuItem itemQuiescenceSearch;
	public static ArrayList<JRadioButtonMenuItem> arrayListBlackLevel;
	public static ArrayList<JRadioButtonMenuItem> arrayListWhiteLevel;
	public static String blackPlayerLevel="Black player level";
	public static String whitePlayerLevel="White player level";
	public static String help="Help";
	public static String howToPlay="How to play";
	
	// tips window
	private static final int tipsFrameWidth=600;
	private static final int tipsFrameHeight=230;
	public static String tips="Tips";
	public static final int tipsBorderSize=5;
	
	// the spaces between component
	private static final int spaceAtRightOfTheInformationPanel=10;
	private static final int spaceAtBottomOfTheChessboard=10;
	private static final int spaceAtTopOfTheChessboard=10;
	private static final int spaceAtLeftOfTheChessboard=10;
	private static final int spaceAtRightOfTheChessboard=10;
	
	// milliseconds for movements
	private static int waitingMillisecondsPeriodForScrolling;
	private static int pixelsSpaceForScrolling;
	private static final int waitingMillisecondsPeriodForScrollingOnWindows7=4;
	private static final int pixelsSpaceForScrollingOnWindows7=4;
	private static final int waitingMillisecondsPeriodForScrollingOnOthersOSThanWindows7=4;
	private static final int pixelsSpaceForScrollingOnOthersOSThanWindows7=6;
	
	private static ChessBoardMouseListener chessBoardMouseListener;
	ChessBoardWithCoordinates chessBoardWithCoordinates=null; // the chessboard, the main component of the game	
	private InformationPanel informationPanel; // the information panel, used to options information	
	public BufferedImage whitePawnImage;
	private static final long serialVersionUID=1L;
	private String piecesMatrix[][]; // used for piece representation	
	int isLastMoveEnableEnPassant; // for en passant memory
	
	public void invertSquare(Point squareToReverse)
	{
		squareToReverse.x=Math.abs(squareToReverse.x-numberOfSquarePerLine+1);
		squareToReverse.y=Math.abs(squareToReverse.y-numberOfSquarePerLine+1);
	}
	
	public void invertMultipleSquares(ArrayList<Point> arrayListSquaresToReverse)
	{
		for(int counterIndex=0;counterIndex<arrayListSquaresToReverse.size();counterIndex++)
			invertSquare(arrayListSquaresToReverse.get(counterIndex));
	}
	
	// useful when an action has been done and selection doesn't have anymore sense
	public void unselectSquareIfSelected()
	{
		if(oldSelectedSquare.x>=0)
		{
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			ArrayList<Point> arrayListPossibleMoves=chessRuler.getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine,isLastMoveEnableEnPassant);
			if(whitesAtBottom==false)
				invertMultipleSquares(arrayListPossibleMoves);
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			chessBoardWithCoordinates.drawASquare(oldSelectedSquare,mainVerticalBox.getGraphics());
			chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves);
			oldSelectedSquare=new Point(-1,1);
		}
	}
	
	public String getNextWord(String stringParameter,int wordCounterParameter)
	{
		int indexBeginingWord=0;
		int wordCounter=0;
		for(int charCounter=1;charCounter<stringParameter.length();charCounter++)
		{
			if((stringParameter.charAt(charCounter)!=' '&&stringParameter.charAt(charCounter)!='\n')&&stringParameter.charAt(charCounter-1)==' '||stringParameter.charAt(charCounter-1)=='\n')
				indexBeginingWord=charCounter;
			
			if((stringParameter.charAt(charCounter)==' '||stringParameter.charAt(charCounter)=='\n')&&stringParameter.charAt(charCounter-1)!=' '&&stringParameter.charAt(charCounter-1)!='\n')
			{
				if(wordCounter==wordCounterParameter)
					return stringParameter.substring(indexBeginingWord,charCounter);
				wordCounter++;
			}
		}
		if(wordCounter==wordCounterParameter)
			return stringParameter.substring(indexBeginingWord,stringParameter.length());
		return "";
	}
	
	private void playComputerVsComputerGame() throws InterruptedException
	{
		paint(getGraphics());
		long beginingTimeForComputerVsComputerGame=System.currentTimeMillis();
		long totalMovesForTheEntireGame=0;
		long incrementTime=0;
		for(int counterMoves=0;counterMoves<1000;counterMoves++)
		{
			ArrayList<Point> listPointSource=new ArrayList<Point>();
			ArrayList<Point> listPointDestination=new ArrayList<Point>();
			ArrayList<String> listMoveDescription=new ArrayList<String>();
			boolean[] arrayIsSpecial=new boolean[1];
			long beginingTimeForComputerVsComputerGameOnePly=System.currentTimeMillis();
			if(chessRuler.getCurrentTurn()==black&&itemComputerPlaysBlack.isSelected()==true)
			{
				chessRuler.playComputer(getBlackLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial,isLastMoveEnableEnPassant,itemQuiescenceSearch.isSelected());
				if(whitesAtBottom==false)
				{
					invertSquare(listPointSource.get(0));
					invertSquare(listPointDestination.get(0));
				}
				chessBoardWithCoordinates.doMove(listPointSource.get(0),listPointDestination.get(0),pixelsSpaceForScrolling,waitingMillisecondsPeriodForScrolling);
			}
			if(chessRuler.getCurrentTurn()==white&&itemComputerPlaysWhite.isSelected()==true)
			{
				chessRuler.playComputer(getWhiteLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial,isLastMoveEnableEnPassant,itemQuiescenceSearch.isSelected());
				if(whitesAtBottom==false)
				{
					invertSquare(listPointSource.get(0));
					invertSquare(listPointDestination.get(0));
				}
				chessBoardWithCoordinates.doMove(listPointSource.get(0),listPointDestination.get(0),pixelsSpaceForScrolling,waitingMillisecondsPeriodForScrolling);
			}
			long endTimeForComputerVsComputerGameOnePly=System.currentTimeMillis();
			long timeForOneComputerPly=endTimeForComputerVsComputerGameOnePly-beginingTimeForComputerVsComputerGameOnePly;
			incrementTime+=timeForOneComputerPly;
			totalMovesForTheEntireGame+=chessRuler.totalCounter;
			
			if(chessRuler.counterMoveFinished%5==0)
				System.out.println(chessRuler.counterMoveFinished+" Total time : "+incrementTime/(double)1000+" evaluations : "+totalMovesForTheEntireGame+" speed : "+(long)(totalMovesForTheEntireGame/(incrementTime/(double)1000))+" moves/sec "+((double)incrementTime/(double)1000)/chessRuler.counterMoveFinished+" seconds/move");
			
			transformBitSetsIntoReadableMatrix();
			if(whitesAtBottom==false)
			{
				invertSquare(listPointSource.get(0));
				invertSquare(listPointDestination.get(0));
			}
			chessBoardWithCoordinates.drawASquare(listPointSource.get(0),mainVerticalBox.getGraphics());
			chessBoardWithCoordinates.drawASquare(listPointDestination.get(0),mainVerticalBox.getGraphics());
			goToNextTurn(listMoveDescription,arrayIsSpecial[0],isLastMoveEnableEnPassant);
			if(chessRuler.IfGameHasEndedGiveMeTheWinner(isLastMoveEnableEnPassant)!=0)
			{
				
				long endTimeForComputerVsComputerGame=System.currentTimeMillis();
				long totalTimeForComputerVsComputerGame=endTimeForComputerVsComputerGame-beginingTimeForComputerVsComputerGame;
				System.out.println("Total time : "+totalTimeForComputerVsComputerGame+" total evaluations : "+totalMovesForTheEntireGame+" ratio : "+(long)(totalMovesForTheEntireGame/(totalTimeForComputerVsComputerGame/(double)1000))+" moves by second "+((double)totalTimeForComputerVsComputerGame/(double)1000)/chessRuler.counterMoveFinished+" seconds by moves "+chessRuler.counterMoveFinished+" moves");
				
				finishTheGameIfItsTheCase();
				break;
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent actionEvent)
	{
		if(actionEvent.getActionCommand().equals(tips))
		{
			JFrame tipsFrame=new JFrame();
			tipsFrame.setTitle(tips);
			Container tipsContainer=tipsFrame.getContentPane();
			tipsFrame.setLayout(new BorderLayout());
			JLabel compte=new JLabel("<html><br> - If you are playing against the computer and if you cancel a move, the computer will stop playing. You have to manually reset the computer player.<br><br>"+" - Shortcuts : use 'c' to cancel a move, 'r' to replay a move, 's' to use sliding moves, 'q' to use quiescence search, 't' to turn the chessboard.<br><br>"+" - Quiescence search : in complex situations, the computer player faces its limited projection and may do errors. The quiescence search is a remedy for this and "+" allows the computer to react better. Select this option if you want more challenge.</html>");
			tipsContainer.add(compte,BorderLayout.NORTH);
			tipsFrame.setSize(tipsFrameWidth,tipsFrameHeight);
			tipsFrame.setLocationRelativeTo(null);
			tipsFrame.setVisible(true);
			Border raisedBorder=BorderFactory.createEmptyBorder(tipsBorderSize,tipsBorderSize,tipsBorderSize,tipsBorderSize);
			((JComponent)(tipsFrame.getContentPane())).setBorder(raisedBorder);
		}
		
		if(actionEvent.getActionCommand().equals(howToPlay))
		{
			String url="http://en.wikibooks.org/wiki/Chess/Playing_The_Game";
			String os=System.getProperty("os.name").toLowerCase();
			Runtime rt=Runtime.getRuntime();
			try
			{
				if(os.indexOf("win")>=0)
					rt.exec("rundll32 url.dll,FileProtocolHandler "+url);
				else if(os.indexOf("mac")>=0)
					rt.exec("open "+url);
				else if(os.indexOf("nix")>=0||os.indexOf("nux")>=0)
				{
					String[] browsers=
					{"epiphany","firefox","mozilla","konqueror","netscape","opera","links","lynx"};
					
					StringBuffer cmd=new StringBuffer();
					for(int i=0;i<browsers.length;i++)
						cmd.append((i==0?"":" || ")+browsers[i]+" \""+url+"\" ");
					rt.exec(new String[]
					{"sh","-c",cmd.toString()});
				}
			}
			catch(Exception e)
			{
				return;
			}
		}
		if(actionEvent.getActionCommand().equals(computerPlaysBlack))
		{
			if(itemComputerPlaysBlack.isSelected()==true)
			{
				if(indexMoves!=-1)
					chessRuler.SetCounterOfMoves(indexMoves);
				informationPanel.deleteHistoricUntil(indexMoves);
				indexMoves=-1;
				informationPanel.undrawLines();
				if(chessRuler.getCurrentTurn()==noCurrentGame)
				{
					itemComputerPlaysBlack.setSelected(false);
					return;
				}
				if(itemComputerPlaysWhite.isSelected()==true)
					try
					{
						playComputerVsComputerGame();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
				else if(chessRuler.getCurrentTurn()==black)
				{
					try
					{
						playComputer();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
					finishTheGameIfItsTheCase();
				}
			}
		}
		
		if(actionEvent.getActionCommand().equals(computerPlaysWhite))
		{
			if(itemComputerPlaysWhite.isSelected()==true)
			{
				if(indexMoves!=-1)
					chessRuler.SetCounterOfMoves(indexMoves);
				informationPanel.deleteHistoricUntil(indexMoves);
				indexMoves=-1;
				informationPanel.undrawLines();
				if(chessRuler.getCurrentTurn()==noCurrentGame)
				{
					itemComputerPlaysWhite.setSelected(false);
					return;
				}
				if(itemComputerPlaysBlack.isSelected()==true)
					try
					{
						playComputerVsComputerGame();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
				else if(chessRuler.getCurrentTurn()==white)
				{
					try
					{
						playComputer();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
					finishTheGameIfItsTheCase();
				}
			}
		}
		
		// we save the game into a local file
		if(actionEvent.getActionCommand().equals(saveGame))
		{
			// we have to build the file name, before all we retrieve the current date
			Calendar currentCalendar=Calendar.getInstance();
			long beginingTime=currentCalendar.getTimeInMillis();
			Date currentDate=new Date(beginingTime);
			
			// now we retrieve the beginning date
			DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd kk-mm-ss");
			Date beginningDate=chessRuler.getBeginningDate();
			String stringBeginningDate=dateFormat.format(beginningDate);
			GregorianCalendar difference=new GregorianCalendar();
			
			// we calculate the difference 
			difference.setTimeInMillis(currentDate.getTime()-beginningDate.getTime());
			int year=difference.get(Calendar.YEAR)-1970;
			int month=difference.get(Calendar.MONTH);
			int day=difference.get(Calendar.DAY_OF_MONTH)-1;
			int hour=difference.get(Calendar.HOUR_OF_DAY)-1;
			int minute=difference.get(Calendar.MINUTE);
			int seconds=difference.get(Calendar.SECOND);
			
			// now we have all the items to create the entire file name
			String fileName="c:\\"+stringBeginningDate+" - ";
			if(year>0)
				fileName+=year+"-";
			if(month>0||year>0)
			{
				if(month<10)
					fileName+="0";
				fileName+=month+"-";
			}
			if(day>0||month>0||year>0)
			{
				if(day<10)
					fileName+="0";
				fileName+=day+" ";
			}
			if(hour>0||day>0||month>0||year>0)
			{
				if(hour<10)
					fileName+="0";
				fileName+=hour+"-";
			}
			if(minute<10)
				fileName+="0";
			fileName+=""+minute+"-";
			if(seconds<10)
				fileName+="0";
			fileName+=seconds+" - "+chessRuler.getCounterOfMoves()+".pgn"; // here we have the full file name
			
			// we put the good look and feel 
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch(Exception exception)
			{
				exception.printStackTrace();
			}
			Locale.setDefault(java.util.Locale.ENGLISH);
			
			//we create the file chooser
			final JFileChooser fileChooser=new JFileChooser();
			fileChooser.setLocale(Locale.ENGLISH);
			fileChooser.updateUI();
			fileChooser.setSelectedFile(new File(fileName));
			fileChooser.setDialogTitle("Select directory to save current game");
			int returnOpenDialog=fileChooser.showOpenDialog(this);
			if(returnOpenDialog!=0)
				return;
			File file=fileChooser.getSelectedFile();
			String fileNameSelected=file.getPath();
			if(fileNameSelected==null)
				return;
			
			// we have to get all the standard moves descriptions and concatenate all of it
			ArrayList<String> listMovesDescription=informationPanel.getStandardArrayMovesDescription();
			String concatenationMovesDescription="";
			int currentLineLength=0;
			for(int counterMoves=0;counterMoves<listMovesDescription.size();counterMoves++)
			{
				currentLineLength+=listMovesDescription.get(counterMoves).length();
				if(currentLineLength>maximumGameDescriptionLength)
				{
					concatenationMovesDescription+="\n";
					currentLineLength=listMovesDescription.get(counterMoves).length();
				}
				concatenationMovesDescription+=listMovesDescription.get(counterMoves);
				if(counterMoves<listMovesDescription.size()-1)
				{
					concatenationMovesDescription+=" ";
					currentLineLength++;
				}
			}
			
			// now we can write everything on the file
			try
			{
				FileWriter fileWriter=new FileWriter(fileNameSelected,true);
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
				bufferedWriter.write("[Event \"Local game\"]\n");
				bufferedWriter.write("[Site \"?\"]\n");
				bufferedWriter.write("[Date \""+new SimpleDateFormat("yyyy").format(beginningDate)+"."+new SimpleDateFormat("MM").format(beginningDate)+"."+new SimpleDateFormat("dd").format(beginningDate)+"\"]\n");
				bufferedWriter.write("[Round \""+chessRuler.getCounterOfMoves()+"\"]\n");
				bufferedWriter.write("[White \"?\"]\n");
				bufferedWriter.write("[Black \"?\"]\n");
				bufferedWriter.write("[Result \"*\"]\n\n");
				
				// the game itself
				bufferedWriter.write(concatenationMovesDescription);
				bufferedWriter.flush();
				bufferedWriter.close();
			}
			catch(IOException exception)
			{
				exception.printStackTrace();
			}
		}
		
		// we load a game
		if(actionEvent.getActionCommand().equals(loadGame))
		{
			// we open the file dialog box to select the right file
			String fileName="";
			FileDialog fileDialog=new FileDialog(this);
			fileDialog.setTitle("Select directory to save current game");
			fileDialog.setMode(FileDialog.LOAD);
			fileDialog.setVisible(true);
			fileName=fileDialog.getDirectory()+fileDialog.getFile();
			if(fileName==""||fileName==null||fileDialog.getFile()==null)
				return;
			
			// we open the file chosen and read its entire content
			File file=new File(fileName);
			StringBuilder stringBuilder=null;
			Charset charset=Charset.defaultCharset();
			Reader reader=null;
			try
			{
				reader=new InputStreamReader(new FileInputStream(file),charset);
			}
			catch(FileNotFoundException fileNotFoundException)
			{
				fileNotFoundException.printStackTrace();
			}
			stringBuilder=new StringBuilder((int)file.length());
			char[] arrayChar=new char[(int)file.length()];
			int sizeRead=0;
			try
			{
				sizeRead=reader.read(arrayChar);
			}
			catch(IOException inputOutputException)
			{
				inputOutputException.printStackTrace();
			}
			stringBuilder.append(arrayChar,0,sizeRead);
			try
			{
				reader.close();
			}
			catch(IOException ioException)
			{
				ioException.printStackTrace();
			}
			
			// we reset the entire current game
			unselectSquareIfSelected();
			chessRuler.initializeNewGame();
			informationPanel.undrawLines();
			indexMoves=-1;
			informationPanel.clearList();
			informationPanel.SetPlayerTurn("White turn");
			informationPanel.undrawLines();
			
			// now we have to analyze the content of the file
			String movesDescription=new String(arrayChar); // we get the entire content of the file into a string
			movesDescription=movesDescription.substring(movesDescription.indexOf("\n1."));
			movesDescription=movesDescription.replaceAll(ChessRuler.promotionStandard,"");
			movesDescription=movesDescription.replaceAll("\\+","");
			movesDescription=movesDescription.replaceAll(" "+chessRuler.enPassantStandard,chessRuler.enPassantReducedForAnalysis);
			movesDescription=movesDescription.replaceAll("x",""); // the fact piece are eaten doesn't bring any useful information
			movesDescription=movesDescription.replaceAll("\n"," "); // carriage return are more problem than other thing, we replace by space to because it has same meaning
			informationPanel.setToExplicitNotation(indexMoves);
			
			// we look at all the words in the string
			boolean[] arrayIsSpecial=new boolean[1];
			for(int wordCounter=0;;wordCounter++)
			{
				String currentWord=getNextWord(movesDescription,wordCounter);
				if(currentWord.equals(""))
					break;
				if(currentWord.indexOf(".")==-1)
				{
					ArrayList<String> arrayMoveDescription=new ArrayList<String>();
					isLastMoveEnableEnPassant=chessRuler.doThisMoveAndGetDescriptionFromAWord(currentWord,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
					goToNextTurn(arrayMoveDescription,arrayIsSpecial[0],isLastMoveEnableEnPassant);
				}
			}
			
			// loaded of the file is done, we can now update graphics
			paint(getGraphics());
		}
		
		// we initialize a new game
		if(actionEvent.getActionCommand().equals(newGame))
		{
			indexMoves=-1;
			itemComputerPlaysBlack.setSelected(false);
			itemComputerPlaysWhite.setSelected(false);
			chessRuler.initializeNewGame();
			transformBitSetsIntoReadableMatrix();
			informationPanel.clearList();
			informationPanel.SetPlayerTurn("White turn");
			paint(getGraphics());
			isLastMoveEnableEnPassant=-1;
		}
		
		// we turn the chessboard of 180 degrees
		if(actionEvent.getActionCommand().equals(turnChessboard))
		{
			oldSelectedSquare=new Point(-1,-1); // we unselect the last piece, which is quite natural
			whitesAtBottom=!whitesAtBottom;
			chessBoardWithCoordinates.turn180Degrees();
			paint(getGraphics());
		}
		
		// we turn the chessboard of 180 degrees
		if(actionEvent.getActionCommand().equals(slidingMoves))
		{
			if(waitingMillisecondsPeriodForScrolling==0)
			{
				// set pixels values according to the operating system
				if(System.getProperty("os.name").indexOf("Windows 7")!=-1)
				{
					waitingMillisecondsPeriodForScrolling=waitingMillisecondsPeriodForScrollingOnWindows7;
					pixelsSpaceForScrolling=pixelsSpaceForScrollingOnWindows7;
				}
				else
				{
					waitingMillisecondsPeriodForScrolling=waitingMillisecondsPeriodForScrollingOnOthersOSThanWindows7;
					pixelsSpaceForScrolling=pixelsSpaceForScrollingOnOthersOSThanWindows7;
				}
			}
			else
			{
				waitingMillisecondsPeriodForScrolling=0;
				pixelsSpaceForScrolling=-1;
			}
		}
		
		if(actionEvent.getActionCommand().equals(standardNotation))
		{
			informationPanel.setToStandardNotation();
			unselectSquareIfSelected();
		}
		
		if(actionEvent.getActionCommand().equals(explicitNotation))
		{
			informationPanel.setToExplicitNotation(indexMoves);
			unselectSquareIfSelected();
		}
		
		// we initialize a new game
		if(actionEvent.getActionCommand().equals(newGame))
		{
			indexMoves=-1;
			itemComputerPlaysBlack.setSelected(false);
			itemComputerPlaysWhite.setSelected(false);
			chessRuler.initializeNewGame();
			transformBitSetsIntoReadableMatrix();
			informationPanel.clearList();
			informationPanel.SetPlayerTurn("White turn");
			paint(getGraphics());
		}
		
		// we leave the application
		if(actionEvent.getActionCommand().equals(quit))
		{
			System.exit(0);
		}
		
		// we replay the move
		if(actionEvent.getActionCommand().equals(replayMove))
		{
			if(indexMoves==informationPanel.getNumberOfMoves()||indexMoves==-1)
				return; // there is no remake to do so we leave
			unselectSquareIfSelected();
			indexMoves++;
			informationPanel.drawLine(indexMoves);
			String sourceStringSquare=informationPanel.getSourceSquare(indexMoves-1);
			if(sourceStringSquare==null) // castling management
			{
				String castling2=getNextWord(informationPanel.getStringAt(indexMoves-1),1);
				String castling3=getNextWord(informationPanel.getStringAt(indexMoves-1),2);
				ArrayList<Point> arrayConcernedSquares=chessRuler.doCastling(castling2+" "+castling3);
				transformBitSetsIntoReadableMatrix();
				for(int counterSquares=0;counterSquares<arrayConcernedSquares.size();counterSquares++)
				{
					if(whitesAtBottom==false)
						invertSquare(arrayConcernedSquares.get(counterSquares));
					chessBoardWithCoordinates.drawASquare(arrayConcernedSquares.get(counterSquares),mainVerticalBox.getGraphics());
				}
			}
			else
			{
				Point sourceSquare=chessRuler.getCorrespondingSquare(sourceStringSquare);
				String destinationStringSquare=informationPanel.getDestinationSquare(indexMoves-1);
				Point destinationSquare=chessRuler.getCorrespondingSquare(destinationStringSquare);
				ArrayList<String> arrayMoveDescription=new ArrayList<String>();
				boolean[] arrayIsSpecial=new boolean[1];
				chessRuler.doThisMoveAndGetDescription(sourceSquare,destinationSquare,arrayMoveDescription,arrayIsSpecial,informationPanel.getEnPassantIndex(indexMoves-2));
				isLastMoveEnableEnPassant=informationPanel.getEnPassantIndex(indexMoves-1);
				chessRuler.SetCounterOfMoves(indexMoves);
				
				// we paint the transformation
				transformBitSetsIntoReadableMatrix();
				if(whitesAtBottom==false)
				{
					invertSquare(destinationSquare);
					invertSquare(sourceSquare);
				}
				chessBoardWithCoordinates.drawASquare(sourceSquare,mainVerticalBox.getGraphics());
				chessBoardWithCoordinates.drawASquare(destinationSquare,mainVerticalBox.getGraphics());
				
				if(informationPanel.getEnPassantIndex(indexMoves-2)!=-1)
				{
					Point enPassantPoint=null;
					if(chessRuler.getCurrentTurn()==white)
						enPassantPoint=new Point(informationPanel.getEnPassantIndex(indexMoves-2)%numberOfSquarePerLine,informationPanel.getEnPassantIndex(indexMoves-2)/numberOfSquarePerLine);
					else
						enPassantPoint=new Point(informationPanel.getEnPassantIndex(indexMoves-2)%numberOfSquarePerLine,informationPanel.getEnPassantIndex(indexMoves-2)/numberOfSquarePerLine);
					if(whitesAtBottom==false)
						invertSquare(enPassantPoint);
					chessBoardWithCoordinates.drawASquare(enPassantPoint,mainVerticalBox.getGraphics());
				}
				
			}
			unselectSquareIfSelected();
			chessRuler.ChangePlayerTurn();
			
			if(chessRuler.getCurrentTurn()==white)
				informationPanel.SetPlayerTurn("White turn");
			if(chessRuler.getCurrentTurn()==black)
				informationPanel.SetPlayerTurn("Black turn");
			
			finishTheGameIfItsTheCase(); // check if the game is over
		}
		
		// we cancel the move 
		if(actionEvent.getActionCommand().equals(cancelMove))
		{
			itemComputerPlaysBlack.setSelected(false);
			itemComputerPlaysWhite.setSelected(false);
			if(chessRuler.getCurrentTurn()==noCurrentGame)
				chessRuler.SetToLastTurnBeforeCheckAndMate(informationPanel.isPairNumberOfMoves());
			unselectSquareIfSelected();
			if(indexMoves==0)
				return; // we have unmake everything so we leave
			if(indexMoves==-1)
				indexMoves=informationPanel.getNumberOfMoves(); // this is the beginning of a new cycle
			indexMoves--;
			informationPanel.drawLine(indexMoves);
			chessRuler.ChangePlayerTurn();
			if(chessRuler.getCurrentTurn()==white)
				informationPanel.SetPlayerTurn("White turn");
			if(chessRuler.getCurrentTurn()==black)
				informationPanel.SetPlayerTurn("Black turn");
			String sourceStringSquare=informationPanel.getSourceSquare(indexMoves);
			if(sourceStringSquare==null)
			{
				String castling2=getNextWord(informationPanel.getStringAt(indexMoves),1);
				String castling3=getNextWord(informationPanel.getStringAt(indexMoves),2);
				ArrayList<Point> arrayConcernedSquares=chessRuler.undoCastling(castling2+" "+castling3); // castling management, we get the right castling, queen or king side
				transformBitSetsIntoReadableMatrix();
				for(int counterSquares=0;counterSquares<arrayConcernedSquares.size();counterSquares++)
				{
					if(whitesAtBottom==false)
						invertSquare(arrayConcernedSquares.get(counterSquares));
					chessBoardWithCoordinates.drawASquare(arrayConcernedSquares.get(counterSquares),mainVerticalBox.getGraphics());
				}
			}
			else
			{
				Point sourceSquare=chessRuler.getCorrespondingSquare(sourceStringSquare);
				String destinationStringSquare=informationPanel.getDestinationSquare(indexMoves);
				String pieceTypeEventuallyDeletedString=informationPanel.getPieceTypeEventuallyDeleted(indexMoves);
				pieceTypeEventuallyDeletedString=getNextWord(pieceTypeEventuallyDeletedString,0); // for promotion	
				int pieceTypeEventuallyDeleted=chessRuler.getPieceIdWithString(pieceTypeEventuallyDeletedString);
				Point destinationSquare=chessRuler.getCorrespondingSquare(destinationStringSquare);
				chessRuler.undoMove(sourceSquare,destinationSquare,pieceTypeEventuallyDeleted,informationPanel.isThisMoveSpecial(indexMoves));
				isLastMoveEnableEnPassant=informationPanel.getEnPassantIndex(indexMoves-1);
				
				// we paint the transformation
				transformBitSetsIntoReadableMatrix();
				if(whitesAtBottom==false)
				{
					invertSquare(destinationSquare);
					invertSquare(sourceSquare);
				}
				chessBoardWithCoordinates.drawASquare(sourceSquare,mainVerticalBox.getGraphics());
				chessBoardWithCoordinates.drawASquare(destinationSquare,mainVerticalBox.getGraphics());
				if(informationPanel.getStringAt(indexMoves).indexOf(chessRuler.enPassantExplicit)!=-1)
				{
					transformBitSetsIntoReadableMatrix();
					if(whitesAtBottom==false)
					{
						invertSquare(destinationSquare);
						invertSquare(sourceSquare);
					}
					Point enPassantPoint=null;
					if(chessRuler.getCurrentTurn()==white)
						enPassantPoint=new Point(destinationSquare.x,destinationSquare.y+1);
					else
						enPassantPoint=new Point(destinationSquare.x,destinationSquare.y-1);
					if(whitesAtBottom==false)
						invertSquare(enPassantPoint);
					chessBoardWithCoordinates.drawASquare(enPassantPoint,mainVerticalBox.getGraphics());
				}
			}
		}
	}
	
	public static void main(String stringArray[])
	{
		new ChessApplication();
	}
	
	// a click has been done on the board, it has to be analyzed to know what happened
	public void onMousePressedOnTheChessBoard(MouseEvent mouseEvent) throws InterruptedException
	{
		// no need to do anything if there is no game
		if(chessRuler.getCurrentTurn()==noCurrentGame)
			return;
		
		// we get the right point for the chessboard with coordinates
		Point pointThatFitWithTheChessBoardWithCoordinates=new Point(mouseEvent.getPoint().x-spaceAtLeftOfTheChessboard-getInsets().left,mouseEvent.getPoint().y-spaceAtTopOfTheChessboard-menuBar.getHeight()-getInsets().top);
		Point newSeletectedSquare=chessBoardWithCoordinates.getCorrespondingSquare(pointThatFitWithTheChessBoardWithCoordinates);
		if(newSeletectedSquare==null)
			return;
		
		// we check if the current square is a piece the play can move 
		if(chessBoardWithCoordinates.giveMeThePieceColorOnThisSquare(newSeletectedSquare)==chessRuler.getCurrentTurn())
		{
			// we paint the old square, if it has been selected, it is repaint 
			if(oldSelectedSquare.x>=0)
			{
				if(whitesAtBottom==false)
					invertSquare(oldSelectedSquare);
				ArrayList<Point> arrayListPossibleMoves=chessRuler.getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine,isLastMoveEnableEnPassant);
				if(whitesAtBottom==false)
					invertMultipleSquares(arrayListPossibleMoves);
				chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves); // repaint target if a new piece has been selected
				if(whitesAtBottom==false)
					invertSquare(oldSelectedSquare);
				chessBoardWithCoordinates.drawASquare(oldSelectedSquare,mainVerticalBox.getGraphics());
			}
			
			// the old square is the same that the new, we have to unselect it and possible moves
			if(oldSelectedSquare.x==newSeletectedSquare.x&&oldSelectedSquare.y==newSeletectedSquare.y)
			{
				if(whitesAtBottom==false)
					invertSquare(oldSelectedSquare);
				ArrayList<Point> arrayListPossibleMoves=chessRuler.getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine,isLastMoveEnableEnPassant);
				if(whitesAtBottom==false)
					invertMultipleSquares(arrayListPossibleMoves);
				oldSelectedSquare=new Point(-1,-1);
				return;
			}
			chessBoardWithCoordinates.drawASquare(newSeletectedSquare,Color.green,mainVerticalBox.getGraphics());
			oldSelectedSquare=new Point(newSeletectedSquare.x,newSeletectedSquare.y);
			
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			ArrayList<Point> arrayListPossibleMoves=chessRuler.getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine,isLastMoveEnableEnPassant);
			if(whitesAtBottom==false)
				invertMultipleSquares(arrayListPossibleMoves);
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves,Color.blue);
		}
		else
		{
			// maybe a square target has been selected
			if(oldSelectedSquare.x>=0)
			{
				// we invert the old and new square in order to make the good move
				if(whitesAtBottom==false)
				{
					invertSquare(oldSelectedSquare);
					invertSquare(newSeletectedSquare);
				}
				
				if(chessRuler.isThisMovePossible(oldSelectedSquare,newSeletectedSquare,isLastMoveEnableEnPassant)==true)
				{
					// we erase the old possible moves            
					ArrayList<Point> arrayListPossibleMoves=chessRuler.getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine,isLastMoveEnableEnPassant);
					if(whitesAtBottom==false)
						invertMultipleSquares(arrayListPossibleMoves);
					chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves);
					
					// we make the move and update move description
					ArrayList<String> arrayMoveDescription=new ArrayList<String>();
					if(indexMoves!=-1)
						chessRuler.counterMoveFinished=indexMoves;
					informationPanel.deleteHistoricUntil(indexMoves);
					indexMoves=-1;
					informationPanel.undrawLines();
					
					if(whitesAtBottom==false)
					{
						invertSquare(oldSelectedSquare);
						invertSquare(newSeletectedSquare);
					}
					chessBoardWithCoordinates.doMove(oldSelectedSquare,newSeletectedSquare,pixelsSpaceForScrolling,waitingMillisecondsPeriodForScrolling);
					if(whitesAtBottom==false)
					{
						invertSquare(oldSelectedSquare);
						invertSquare(newSeletectedSquare);
					}
					
					boolean[] arrayIsSpecial=new boolean[1];
					int eventuallyCastlingOrEnPassant=chessRuler.doThisMoveAndGetDescription(oldSelectedSquare,newSeletectedSquare,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
					Point sourceCastling=null;
					Point destinationCastling=null;
					switch(eventuallyCastlingOrEnPassant)
					{
					case ChessRuler.whiteRookKingCastlingDestination:
						sourceCastling=new Point(ChessRuler.rightWhiteRookInitialPosition);
						destinationCastling=new Point(ChessRuler.whiteRookKingCastlingDestination%numberOfSquarePerLine,ChessRuler.whiteRookKingCastlingDestination/numberOfSquarePerLine);
						break;
					case ChessRuler.whiteRookQueenCastlingDestination:
						sourceCastling=new Point(ChessRuler.leftWhiteRookInitialPosition);
						destinationCastling=new Point(ChessRuler.whiteRookQueenCastlingDestination%numberOfSquarePerLine,ChessRuler.whiteRookQueenCastlingDestination/numberOfSquarePerLine);
						break;
					case ChessRuler.blackRookKingCastlingDestination:
						sourceCastling=new Point(ChessRuler.rightBlackRookInitialPosition);
						destinationCastling=new Point(ChessRuler.blackRookKingCastlingDestination%numberOfSquarePerLine,ChessRuler.blackRookKingCastlingDestination/numberOfSquarePerLine);
						break;
					case ChessRuler.blackRookQueenCastlingDestination:
						sourceCastling=new Point(ChessRuler.leftBlackRookInitialPosition);
						destinationCastling=new Point(ChessRuler.blackRookQueenCastlingDestination%numberOfSquarePerLine,ChessRuler.blackRookQueenCastlingDestination/numberOfSquarePerLine);
						break;
					default:
						// it means that is a en passant move refresh the right square
						if(eventuallyCastlingOrEnPassant!=0)
						{
							transformBitSetsIntoReadableMatrix();
							Point pointForEnPassant=new Point(newSeletectedSquare.x,oldSelectedSquare.y);
							if(whitesAtBottom==false)
								invertSquare(pointForEnPassant);
							chessBoardWithCoordinates.drawASquare(pointForEnPassant,mainVerticalBox.getGraphics());
						}
					}
					isLastMoveEnableEnPassant=chessRuler.IsItDoublePawnMoveForEnPassant(oldSelectedSquare,newSeletectedSquare);
					if(sourceCastling!=null)
					{
						if(whitesAtBottom==false)
						{
							invertSquare(sourceCastling);
							invertSquare(destinationCastling);
						}
						chessBoardWithCoordinates.doMove(sourceCastling,destinationCastling,pixelsSpaceForScrolling,waitingMillisecondsPeriodForScrolling);
					}
					
					// paint the target for pawn promotion	 
					transformBitSetsIntoReadableMatrix();
					if(whitesAtBottom==false)
						invertSquare(newSeletectedSquare);
					chessBoardWithCoordinates.drawASquare(newSeletectedSquare,mainVerticalBox.getGraphics());
					oldSelectedSquare=new Point(-1,-1);
					goToNextTurn(arrayMoveDescription,arrayIsSpecial[0],isLastMoveEnableEnPassant);
					
					// check if the game is over
					if(finishTheGameIfItsTheCase()==true)
						return;
					
					// play computer if necessary
					if((chessRuler.getCurrentTurn()==black&&itemComputerPlaysBlack.isSelected()==true)||(chessRuler.getCurrentTurn()==white&&itemComputerPlaysWhite.isSelected()==true))
					{
						playComputer();
						// check if the game is over
						if(finishTheGameIfItsTheCase()==true)
							return;
					}
				}
				else
				{
					if(whitesAtBottom==false)
					{
						invertSquare(oldSelectedSquare);
						invertSquare(newSeletectedSquare);
					}
				}
			}
		}
	}
	
	public ChessApplication()
	{
		chessRuler=new ChessRuler();
		piecesMatrix=new String[numberOfSquarePerLine][numberOfSquarePerLine];
		for(int CounterVertical=0;CounterVertical<numberOfSquarePerLine;CounterVertical++)
			for(int CounterHorizontal=0;CounterHorizontal<numberOfSquarePerLine;CounterHorizontal++)
				piecesMatrix[CounterVertical][CounterHorizontal]=new String("");
		oldSelectedSquare=new Point(-1,-1);
		isLastMoveEnableEnPassant=-1;
		
		// first of all we create the menu bar and add items on it
		menuBar=new JMenuBar();
		JMenu menuGame=new JMenu(game);
		JMenuItem itemNewGame=new JMenuItem(newGame);
		menuGame.add(itemNewGame);
		itemNewGame.addActionListener(this);
		JMenuItem itemSaveGame=new JMenuItem(saveGame);
		menuGame.add(itemSaveGame);
		itemSaveGame.addActionListener(this);
		JMenuItem itemLoadGame=new JMenuItem(loadGame);
		menuGame.add(itemLoadGame);
		itemLoadGame.addActionListener(this);
		menuGame.addSeparator();
		itemComputerPlaysBlack=new JCheckBoxMenuItem(computerPlaysBlack);
		itemComputerPlaysBlack.addActionListener(this);
		itemComputerPlaysBlack.setSelected(true); // computer play blacks by default
		menuGame.add(itemComputerPlaysBlack);
		itemComputerPlaysWhite=new JCheckBoxMenuItem(computerPlaysWhite);
		itemComputerPlaysWhite.addActionListener(this);
		menuGame.add(itemComputerPlaysWhite);
		menuGame.addSeparator();
		JMenuItem itemQuit=new JMenuItem(quit);
		menuGame.add(itemQuit);
		itemQuit.addActionListener(this);
		menuBar.add(menuGame);
		JMenu menuMoves=new JMenu(moves);
		JMenuItem itemCancelMove=new JMenuItem(cancelMove);
		itemCancelMove.setAccelerator(KeyStroke.getKeyStroke('c'));
		menuMoves.add(itemCancelMove);
		itemCancelMove.addActionListener(this);
		JMenuItem itemReplayMove=new JMenuItem(replayMove);
		itemReplayMove.setAccelerator(KeyStroke.getKeyStroke('r'));
		menuMoves.add(itemReplayMove);
		itemReplayMove.addActionListener(this);
		menuBar.add(menuMoves);
		JMenu menuOptions=new JMenu(options);
		ButtonGroup group=new ButtonGroup();
		JRadioButtonMenuItem itemStandardNotation=new JRadioButtonMenuItem(standardNotation);
		menuOptions.add(itemStandardNotation);
		group.add(itemStandardNotation);
		itemStandardNotation.addActionListener(this);
		JRadioButtonMenuItem itemExplicitNotation=new JRadioButtonMenuItem(explicitNotation);
		itemExplicitNotation.setSelected(true);
		menuOptions.add(itemExplicitNotation);
		group.add(itemExplicitNotation);
		itemExplicitNotation.addActionListener(this);
		menuOptions.addSeparator();
		JCheckBoxMenuItem itemSlidingMoves=new JCheckBoxMenuItem(slidingMoves);
		itemSlidingMoves.setAccelerator(KeyStroke.getKeyStroke('s'));
		menuOptions.add(itemSlidingMoves);
		itemSlidingMoves.addActionListener(this);
		itemSlidingMoves.setSelected(true); // by default we have slide moves
		menuOptions.addSeparator();
		itemQuiescenceSearch=new JCheckBoxMenuItem(quiescenceSearch);
		itemQuiescenceSearch.setAccelerator(KeyStroke.getKeyStroke('q'));
		// 	itemQuiescenceSearch.setSelected(true);  // by default we have a pure computer levels
		menuOptions.add(itemQuiescenceSearch);
		itemQuiescenceSearch.addActionListener(this);
		menuOptions.addSeparator();
		JMenuItem itemSwitchSides=new JMenuItem(turnChessboard);
		itemSwitchSides.setAccelerator(KeyStroke.getKeyStroke('t'));
		menuOptions.add(itemSwitchSides);
		itemSwitchSides.addActionListener(this);
		menuBar.add(menuOptions);
		
		// now a create the menu for the computer level
		JMenu menuComputerConfiguration=new JMenu(computerLevel);
		
		// add black computer levels
		ButtonGroup groupBlack=new ButtonGroup();
		arrayListBlackLevel=new ArrayList<JRadioButtonMenuItem>();
		for(int counterLevel=1;counterLevel<=maximumDepth;counterLevel++)
		{
			String blackPlayerLevelCounter=blackPlayerLevel+" "+counterLevel;
			JRadioButtonMenuItem menuItemBlackPlayerLevel=new JRadioButtonMenuItem(blackPlayerLevelCounter);
			if(counterLevel==defaultDepth)
				menuItemBlackPlayerLevel.setSelected(true);
			arrayListBlackLevel.add(menuItemBlackPlayerLevel);
			menuItemBlackPlayerLevel.addActionListener(this);
			groupBlack.add(menuItemBlackPlayerLevel);
			menuComputerConfiguration.add(menuItemBlackPlayerLevel);
		}
		
		// put a separator between black level and white level
		menuComputerConfiguration.addSeparator();
		
		// add white computer levels
		ButtonGroup groupWhite=new ButtonGroup();
		arrayListWhiteLevel=new ArrayList<JRadioButtonMenuItem>();
		for(int counterLevel=1;counterLevel<=maximumDepth;counterLevel++)
		{
			String whitePlayerLevelCounter=whitePlayerLevel+" "+counterLevel;
			JRadioButtonMenuItem menuItemWhitePlayerLevel=new JRadioButtonMenuItem(whitePlayerLevelCounter);
			if(counterLevel==defaultDepth)
				menuItemWhitePlayerLevel.setSelected(true);
			arrayListWhiteLevel.add(menuItemWhitePlayerLevel);
			menuItemWhitePlayerLevel.addActionListener(this);
			groupWhite.add(menuItemWhitePlayerLevel);
			menuComputerConfiguration.add(menuItemWhitePlayerLevel);
		}
		menuComputerConfiguration.addActionListener(this);
		menuBar.add(menuComputerConfiguration);
		JMenu menuHelp=new JMenu(help);
		JMenuItem itemHowToPlay=new JMenuItem(howToPlay);
		itemHowToPlay.addActionListener(this);
		menuHelp.add(itemHowToPlay);
		JMenuItem itemTips=new JMenuItem(tips);
		itemTips.addActionListener(this);
		menuHelp.add(itemTips);
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);
		
		// create and add boxes for graphics
		mainHorizontalBox=Box.createHorizontalBox();
		mainVerticalBox=Box.createVerticalBox();
		setVisible(true);
		chessBoardWithCoordinates=new ChessBoardWithCoordinates(getGraphics(),piecesMatrix);
		Dimension chessBoardDimension=chessBoardWithCoordinates.getDimension();
		mainHorizontalBox.add(Box.createRigidArea(new Dimension(spaceAtLeftOfTheChessboard,chessBoardDimension.height)));
		mainHorizontalBox.add(chessBoardWithCoordinates);
		mainHorizontalBox.add(Box.createRigidArea(new Dimension(spaceAtRightOfTheChessboard,chessBoardDimension.height)));
		mainVerticalBox.add(Box.createRigidArea(new Dimension(chessBoardDimension.width,spaceAtTopOfTheChessboard)));
		mainVerticalBox.add(mainHorizontalBox);
		mainVerticalBox.add(Box.createRigidArea(new Dimension(chessBoardDimension.width,spaceAtBottomOfTheChessboard)));
		getContentPane().add(mainVerticalBox);
		setResizable(false);
		informationPanel=new InformationPanel(chessBoardDimension.height,getGraphics());
		mainHorizontalBox.add(informationPanel); // we add the information panel
		mainHorizontalBox.add(Box.createRigidArea(new Dimension(spaceAtRightOfTheInformationPanel,chessBoardDimension.height)));
		
		if(itemSlidingMoves.isSelected()==true)
		{
			// set pixels values according to the operating system
			if(System.getProperty("os.name").indexOf("Windows 7")!=-1)
			{
				waitingMillisecondsPeriodForScrolling=waitingMillisecondsPeriodForScrollingOnWindows7;
				pixelsSpaceForScrolling=pixelsSpaceForScrollingOnWindows7;
			}
			else
			{
				waitingMillisecondsPeriodForScrolling=waitingMillisecondsPeriodForScrollingOnOthersOSThanWindows7;
				pixelsSpaceForScrolling=pixelsSpaceForScrollingOnOthersOSThanWindows7;
			}
		}
		else
		{
			waitingMillisecondsPeriodForScrolling=0;
			pixelsSpaceForScrolling=-1;
		}
		
		// set right dimensions
		pack();
		Dimension dimensionThatFit=new Dimension();
		dimensionThatFit.height=chessBoardDimension.height+menuBar.getHeight()+getInsets().top+getInsets().bottom+spaceAtTopOfTheChessboard+spaceAtBottomOfTheChessboard;
		dimensionThatFit.width=chessBoardDimension.width+getInsets().left+getInsets().right+spaceAtLeftOfTheChessboard+spaceAtRightOfTheChessboard+spaceAtRightOfTheInformationPanel+informationPanel.getWidth();
		setSize(dimensionThatFit);
		setLocationRelativeTo(getParent());
		chessBoardMouseListener=new ChessBoardMouseListener(this);
		addMouseListener(chessBoardMouseListener);
		informationPanel.SetPlayerTurn("White turn");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("JavaChess 3");
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics graphics)
	{
		menuBar.paint(menuBar.getGraphics());
		transformBitSetsIntoReadableMatrix();
		if(getContentPane()!=null&&mainVerticalBox.getGraphics()!=null)
			getContentPane().paint(mainVerticalBox.getGraphics());
		mainVerticalBox.paint(mainVerticalBox.getGraphics());
		
		// this is when the user move the window outside the current screen, and keep the selection squares displayed
		if(oldSelectedSquare.x>=0)
		{
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			ArrayList<Point> arrayListPossibleMoves=chessRuler.getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(oldSelectedSquare.x+oldSelectedSquare.y*numberOfSquarePerLine,isLastMoveEnableEnPassant);
			if(whitesAtBottom==false)
				invertMultipleSquares(arrayListPossibleMoves);
			if(whitesAtBottom==false)
				invertSquare(oldSelectedSquare);
			chessBoardWithCoordinates.drawASquare(oldSelectedSquare,Color.green,mainVerticalBox.getGraphics());
			chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves,Color.blue);
		}
	}
	
	private boolean finishTheGameIfItsTheCase()
	{
		int winner=chessRuler.IfGameHasEndedGiveMeTheWinner(isLastMoveEnableEnPassant);
		if(winner!=0)
		{
			switch(winner)
			{
			case blackIsPat:
				informationPanel.SetPlayerTurn("Game is drawn");
				javax.swing.JOptionPane.showMessageDialog(null,"Black player is pat! Game is drawn.");
				break;
			case whiteIsPat:
				informationPanel.SetPlayerTurn("Game is drawn");
				javax.swing.JOptionPane.showMessageDialog(null,"White player is pat! Game is drawn.");
				break;
			case white:
				informationPanel.SetPlayerTurn("White is the winner");
				javax.swing.JOptionPane.showMessageDialog(null,"White player wins!");
				break;
			case black:
				informationPanel.SetPlayerTurn("Black is the winner");
				javax.swing.JOptionPane.showMessageDialog(null,"Black player wins!");
				break;
			default:
				;
			}
			chessRuler.EndTheGame();
			return true;
		}
		return false;
	}
	
	public void goToNextTurn(ArrayList<String> arrayListMovesDescriptions,Boolean isSpecialMove,int enPassantIndex)
	{
		chessRuler.ChangePlayerTurn();
		if(chessRuler.getCurrentTurn()==white)
			informationPanel.SetPlayerTurn("White turn");
		if(chessRuler.getCurrentTurn()==black)
			informationPanel.SetPlayerTurn("Black turn");
		informationPanel.addNewMoveDescription(arrayListMovesDescriptions.get(0),arrayListMovesDescriptions.get(1),isSpecialMove,enPassantIndex);
	}
	
	public void transformBitSetsIntoReadableMatrix()
	{
		// first of all we erase all the matrix in order to recreate a clean one
		for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
			for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
				piecesMatrix[counterVertical][counterHorizontal]=new String("");
			
		// now we have to fill the string matrix according to the chessboard orientation
		if(whitesAtBottom==true)
		{
			for(int counterBit=0;counterBit<numberOfSquarePerLine*numberOfSquarePerLine;counterBit++)
			{
				if((chessRuler.whiteRooks&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("wr");
				if((chessRuler.whiteBishops&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("wb");
				if((chessRuler.whiteQueens&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("wq");
				if((chessRuler.whiteKnights&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("wk");
				if((chessRuler.whitePawns&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("wp");
				if((chessRuler.whiteKing&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("wK");
				if((chessRuler.blackRooks&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("br");
				if((chessRuler.blackBishops&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("bb");
				if((chessRuler.blackQueens&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("bq");
				if((chessRuler.blackKnights&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("bk");
				if((chessRuler.blackPawns&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("bp");
				if((chessRuler.blackKing&(1L<<counterBit))!=0)
					piecesMatrix[counterBit/numberOfSquarePerLine][counterBit%numberOfSquarePerLine]=new String("bK");
			}
		}
		else
		{
			for(int counterBit=0;counterBit<numberOfSquarePerLine*numberOfSquarePerLine;counterBit++)
			{
				if((chessRuler.whiteRooks&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("wr");
				if((chessRuler.whiteBishops&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("wb");
				if((chessRuler.whiteQueens&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("wq");
				if((chessRuler.whiteKnights&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("wk");
				if((chessRuler.whitePawns&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("wp");
				if((chessRuler.whiteKing&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("wK");
				if((chessRuler.blackRooks&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("br");
				if((chessRuler.blackBishops&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("bb");
				if((chessRuler.blackQueens&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("bq");
				if((chessRuler.blackKnights&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("bk");
				if((chessRuler.blackPawns&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("bp");
				if((chessRuler.blackKing&(1L<<counterBit))!=0)
					piecesMatrix[(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)/numberOfSquarePerLine][(numberOfSquarePerLine*numberOfSquarePerLine-counterBit-1)%numberOfSquarePerLine]=new String("bK");
			}
		}
	}
	
	// get the white computer level into the menu
	public int getWhiteLevel()
	{
		Iterator<JRadioButtonMenuItem> iteratorWhiteLevel=arrayListWhiteLevel.iterator();
		while(iteratorWhiteLevel.hasNext())
		{
			JRadioButtonMenuItem currentRadioButtonMenuItem=iteratorWhiteLevel.next();
			if(currentRadioButtonMenuItem.isSelected()==true)
			{
				String stringLevel=currentRadioButtonMenuItem.getText().substring(currentRadioButtonMenuItem.getText().length()-1,currentRadioButtonMenuItem.getText().length());
				return Integer.parseInt(stringLevel);
			}
		}
		System.out.println("Error while getting white level, no level selected");
		return -1;
	}
	
	// get the black computer level into the menu
	public int getBlackLevel()
	{
		Iterator<JRadioButtonMenuItem> iteratorBlackLevel=arrayListBlackLevel.iterator();
		while(iteratorBlackLevel.hasNext())
		{
			JRadioButtonMenuItem currentRadioButtonMenuItem=iteratorBlackLevel.next();
			if(currentRadioButtonMenuItem.isSelected()==true)
			{
				String stringLevel=currentRadioButtonMenuItem.getText().substring(currentRadioButtonMenuItem.getText().length()-1,currentRadioButtonMenuItem.getText().length());
				return Integer.parseInt(stringLevel);
			}
		}
		System.out.println("Error while getting black level, no level selected");
		return -1;
	}
	
	public void playComputer() throws InterruptedException
	{
		ArrayList<Point> listPointSource=new ArrayList<Point>();
		ArrayList<Point> listPointDestination=new ArrayList<Point>();
		ArrayList<String> listMoveDescription=new ArrayList<String>();
		boolean[] arrayIsSpecial=new boolean[1];
		if(chessRuler.getCurrentTurn()==black&&itemComputerPlaysBlack.isSelected()==true)
			chessRuler.playComputer(getBlackLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial,isLastMoveEnableEnPassant,itemQuiescenceSearch.isSelected());
		if(chessRuler.getCurrentTurn()==white&&itemComputerPlaysWhite.isSelected()==true)
			chessRuler.playComputer(getWhiteLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial,isLastMoveEnableEnPassant,itemQuiescenceSearch.isSelected());
		isLastMoveEnableEnPassant=chessRuler.IsItDoublePawnMoveForEnPassant(listPointSource.get(0),listPointDestination.get(0));
		if(whitesAtBottom==false)
		{
			invertSquare(listPointSource.get(0));
			invertSquare(listPointDestination.get(0));
		}
		chessBoardWithCoordinates.doMove(listPointSource.get(0),listPointDestination.get(0),pixelsSpaceForScrolling,waitingMillisecondsPeriodForScrolling);
		transformBitSetsIntoReadableMatrix();
		goToNextTurn(listMoveDescription,arrayIsSpecial[0],isLastMoveEnableEnPassant);
		chessBoardWithCoordinates.drawASquare(listPointSource.get(0),mainVerticalBox.getGraphics());
		chessBoardWithCoordinates.drawASquare(listPointDestination.get(0),mainVerticalBox.getGraphics());
	}
}
