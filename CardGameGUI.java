import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import java.util.List;
import java.util.*;

public class CardGameGUI extends JFrame implements ActionListener {

	private static final int DEFAULT_HEIGHT = 302;
	private static final int DEFAULT_WIDTH = 800;
	private static final int CARD_WIDTH = 73;
	private static final int CARD_HEIGHT = 97;
	private static final int LAYOUT_TOP = 30;
	private static final int LAYOUT_LEFT = 30;
	private static final int LAYOUT_WIDTH_INC = 100;
	private static final int LAYOUT_HEIGHT_INC = 125;
	private static final int BUTTON_TOP = 30;
	private static final int BUTTON_LEFT = 570;
	private static final int BUTTON_HEIGHT_INC = 50;
	private static final int LABEL_TOP = 160;
	private static final int LABEL_LEFT = 540;
	private static final int LABEL_HEIGHT_INC = 35;

	private PokerBoard board;

	
	private JPanel panel;
	private JButton quitButton;
	private JButton replaceButton;
	private JButton restartButton;
	private JLabel statusMsg;
	private JLabel totalsMsg;
	private JLabel[] displayCards;
	private JLabel winMsg;
	private JLabel lossMsg;
	private Point[] cardCoords;

	private boolean[] selections;
	private int totalWins;
	private int totalGames;


	public CardGameGUI(PokerBoard gameBoard){
		super("Video Poker");
		board = gameBoard;
		totalWins = 0;
		totalGames = 0;

		// Initialize cardCoords using 5 cards per row
		cardCoords = new Point[board.size()];
		int x = LAYOUT_LEFT;
		int y = LAYOUT_TOP;
		for (int i = 0; i < cardCoords.length; i++) {
			cardCoords[i] = new Point(x, y);
			if (i % 5 == 4) {
				x = LAYOUT_LEFT;
				y += LAYOUT_HEIGHT_INC;
			} else {
				x += LAYOUT_WIDTH_INC;
			}
		}
		selections = new boolean[board.size()];
		initDisplay();
		displayGame();
		int cash=0;
		boolean Negative = false;
		while(!Negative){
			cash = Integer.parseInt(JOptionPane.showInputDialog("How much money do you have?"));
			if(cash <= 0){
				JOptionPane.showMessageDialog(null, "You can't play if you got no $");
				Negative = false;
			}else{
				Negative = true;
			}
		}
		board.updateBankRoll(cash);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		repaint();

	}

	public void displayGame() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				setVisible(true);
			}
		});
	}

	public void repaint() {
		for (int k = 0; k < board.size(); k++) {
			String cardImageFileName =
				imageFileName(board.cardAt(k), selections[k]);
			URL imageURL = getClass().getResource(cardImageFileName);
			if (imageURL != null) {
				ImageIcon icon = new ImageIcon(imageURL);
				displayCards[k].setIcon(icon);
				displayCards[k].setVisible(true);
			} else {
				throw new RuntimeException(
					"Card image not found: \"" + cardImageFileName + "\"");
			}
		}
		statusMsg.setText("Cash: $" + (int)board.getBankRoll());
		statusMsg.setVisible(true);
		totalsMsg.setText("You've won " + totalWins
			 + " out of " + totalGames + " games.");
		totalsMsg.setVisible(true);
		pack();
		panel.repaint();
	}

	public void initDisplay()	{
		panel = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};
		
		panel.setLayout(new BorderLayout());
		String className = board.getClass().getSimpleName();
		int classNameLen = className.length();
		int boardLen = "Board".length();
		String boardStr = className.substring(classNameLen - boardLen);
		if (boardStr.equals("Board") || boardStr.equals("board")) {
			int titleLength = classNameLen - boardLen;
			setTitle(className.substring(0, titleLength));
		}

		int numCardRows = (board.size() + 4) / 5;
		int height = DEFAULT_HEIGHT;
		if (numCardRows > 2) {
			height += (numCardRows - 2) * LAYOUT_HEIGHT_INC;
		}

		this.setSize(new Dimension(DEFAULT_WIDTH, height));
		panel.setLayout(null);
		panel.setPreferredSize(
			new Dimension(DEFAULT_WIDTH - 20, height - 20));
		displayCards = new JLabel[board.size()];
		for (int k = 0; k < board.size(); k++) {
			displayCards[k] = new JLabel();
			panel.add(displayCards[k]);
			displayCards[k].setBounds(cardCoords[k].x, cardCoords[k].y,
										CARD_WIDTH, CARD_HEIGHT);
			displayCards[k].addMouseListener(new MyMouseListener());
			selections[k] = false;
		}
		replaceButton = new JButton();
		replaceButton.setText("Replace");
		panel.add(replaceButton);
		replaceButton.setBounds(BUTTON_LEFT, BUTTON_TOP, 100, 30);
		replaceButton.addActionListener(this);

		restartButton = new JButton();
		restartButton.setText("Continue");
		panel.add(restartButton);
		restartButton.setBounds(BUTTON_LEFT, BUTTON_TOP + BUTTON_HEIGHT_INC,
										100, 30);
		restartButton.setVisible(false);
		restartButton.addActionListener(this);

		quitButton = new JButton();
		quitButton.setText("Quit");
		panel.add(quitButton);
		quitButton.setBounds(BUTTON_LEFT, BUTTON_TOP + (BUTTON_HEIGHT_INC*2), 100, 30);
		quitButton.setBackground(Color.RED);
		quitButton.addActionListener(this);

		statusMsg = new JLabel("Cash: $" + board.getBankRoll());
		panel.add(statusMsg);
		statusMsg.setBounds(LABEL_LEFT, LABEL_TOP, 250, 30);

		winMsg = new JLabel();
		winMsg.setBounds(LABEL_LEFT, LABEL_TOP + LABEL_HEIGHT_INC, 300, 50);
		winMsg.setFont(new Font("SansSerif", Font.BOLD, 16));
		winMsg.setForeground(Color.GREEN);
		winMsg.setText("You Win");
		panel.add(winMsg);
		winMsg.setVisible(false);

		lossMsg = new JLabel();
		lossMsg.setBounds(LABEL_LEFT, LABEL_TOP + LABEL_HEIGHT_INC, 200, 30);
		lossMsg.setFont(new Font("SanSerif", Font.BOLD, 25));
		lossMsg.setForeground(Color.RED);
		lossMsg.setText("Sorry, you lose.");
		panel.add(lossMsg);
		lossMsg.setVisible(false);

		totalsMsg = new JLabel("You've won " + totalWins
			+ " out of " + totalGames + " games.");
		totalsMsg.setBounds(LABEL_LEFT, LABEL_TOP + 2 * LABEL_HEIGHT_INC,
								  250, 30);
		panel.add(totalsMsg);

		if (!board.anotherPlayIsPossible()) {
			signalLoss();
		}

		pack();
		getContentPane().add(panel);
		getRootPane().setDefaultButton(replaceButton);
		panel.setVisible(true);
	}


	private void signalError() {
		Toolkit t = panel.getToolkit();
		t.beep();
	}


	private String imageFileName(Card c, boolean isSelected) {
		String str = "cards/";
		if (c == null) {
			return "cards/back1.GIF";
		}
		str += c.rank() + c.suit();
		if (isSelected) {
			str += "S";
		}
		str += ".GIF";
		return str;
	}


	public void actionPerformed(ActionEvent e){
		winMsg.setVisible(false);
		lossMsg.setVisible(false);
		boolean lost = false;
		if (e.getSource().equals(replaceButton)){
			restartButton.setVisible(true);
			replaceButton.setVisible(false);
		
			List<Integer> selection = new ArrayList<Integer>();
			for (int k = 0; k < board.size(); k++) {
				if (selections[k]) {
					selection.add(new Integer(k));
				}
			}
			if (!board.isLegal(selection)){
				signalError();
				return;
			}
			for (int k = 0; k < board.size(); k++){
				selections[k] = false;
			}
			board.replaceSelectedCards(selection);
			if(!(board.checkHand().equalsIgnoreCase("Lose"))){
				lossMsg.setVisible(false);
				winMsg.setText(board.checkHand());
				signalWin();
			}else if(board.checkHand().equalsIgnoreCase("Lose")){
				winMsg.setVisible(false);
				signalLoss();
				if(board.getBankRoll() <= 0){
					winMsg.setVisible(false);
					lossMsg.setFont(new Font("SanSerif", Font.BOLD, 16));
					lossMsg.setText("Game Over. You lose.");
					restartButton.setVisible(false);
					replaceButton.setVisible(false);
					signalLoss();
					lost = true;
				}
				
			} else if(!board.anotherPlayIsPossible()){
				winMsg.setVisible(false);
				signalLoss();
			}
			repaint();
		}else if(e.getSource().equals(restartButton)){
			restartButton.setVisible(false);
			replaceButton.setVisible(true);
			board.newGame();
			getRootPane().setDefaultButton(replaceButton);
			winMsg.setVisible(false);
			lossMsg.setVisible(false);
			if (!board.anotherPlayIsPossible()){
				winMsg.setVisible(false);
				signalLoss();
				lossMsg.setVisible(true);
			}
			for (int i = 0; i < selections.length; i++){
				selections[i] = false;
			}
			repaint();			
		}else if(e.getSource().equals(quitButton)){
			if(!(board.getBankRoll() <= 0)){
				JOptionPane.showMessageDialog(null, "You have $" + (int)(board.getBankRoll()) + ".");

				statusMsg.setText("Cash: $" + (int)(board.getBankRoll()));
			}else{
				JOptionPane.showMessageDialog(null, "You have no more money.");

			}
			dispose();
		}else{
			signalError();
			return;
		}
	}
	
	private void signalWin() {
		getRootPane().setDefaultButton(restartButton);
		winMsg.setVisible(true);
		totalWins++;
		totalGames++;
	}

	private void signalLoss() {
		getRootPane().setDefaultButton(restartButton);
		lossMsg.setVisible(true);
		totalGames++;
	}


	private class MyMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			for (int k = 0; k < board.size(); k++) {
				if (e.getSource().equals(displayCards[k])
						&& board.cardAt(k) != null) {
					selections[k] = !selections[k];
					repaint();
					return;
				}
			}
			signalError();
		}

		//Ignore below
		public void mouseExited(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
	}
}
