import java.util.*;
import javax.swing.*;

public class PokerTester{
	public static void main(String[] args){
		final int size = 5;
		boolean Negative = false;
		int cash = 0;
		boolean playAgain = false;
		do{
			
			while(!Negative){
				cash = Integer.parseInt(JOptionPane.showInputDialog("How much money do you have?"));
				if(cash < 0){
					JOptionPane.showMessageDialog(null, "You can't play if you got no $");
					Negative = false;
				}else{
					Negative = true;
				}
			}

			Board board = new PokerBoard(size, PokerBoard.ranks, PokerBoard.suits, PokerBoard.POINT_VALUES, cash);
			List<Integer> selectedCards = new ArrayList<Integer>();
			System.out.println(board);
			boolean isMaxxed = false;
			int numReplaced = 0;
			while(!isMaxxed){
				numReplaced = Integer.parseInt(JOptionPane.showInputDialog(board+"How many cards would you like to replace?"));
				if(numReplaced <= 5){
						isMaxxed = true;
					}else{
						isMaxxed = false;
						JOptionPane.showMessageDialog(null, "You can only replace 5 cards");
					}
			}


			//still need to add something to check that you dont put same index position twice
			while(numReplaced > 0){
				boolean isValidReplaceInput = true, x, canProceed = true;
				int posToReplace = 0;
				while(isValidReplaceInput && canProceed){
						posToReplace = Integer.parseInt(JOptionPane.showInputDialog(board+ "\n Type the 1, 2, 3, 4, 5 that corresponds with the card you want to replace."));
					
					x = posToReplace > 5 || posToReplace < 1;
					if(x){
						JOptionPane.showMessageDialog(null, "You must enter a valid index position");
						isValidReplaceInput = false;
						canProceed = true;
					}else if(!x){
						isValidReplaceInput = true;
						canProceed = false;
					}
				}
				if(isValidReplaceInput){
					selectedCards.add(posToReplace - 1);
					numReplaced--;
				}
			}
			board.replaceSelectedCards(selectedCards);
			JOptionPane.showMessageDialog(null, ("Your new cards are \n" + board));
			System.out.println("New Cards: \n" + board);
			
			PokerBoard b = (PokerBoard) board;
			JOptionPane.showMessageDialog(null, b.checkHand());
			System.out.println(b.checkHand());
			Object[] YN = {"Yes", "No"};
			Object message = "You have $" + b.getBankRoll() + ". Would you like to continue?";
			
			int Play =JOptionPane.showOptionDialog(null, message, null ,JOptionPane.YES_NO_OPTION, 
			JOptionPane.PLAIN_MESSAGE, null, YN, 0);
			
			switch(Play){
				case 0:
						playAgain = true;
						Negative = true;
				break;				
				case 1:
						JOptionPane.showMessageDialog(null, "You ended with $" + b.getBankRoll() + ".");
						playAgain = false;
						System.exit(0);
				break;
				
				default: 
					System.exit(0);
			}
		
		}while(playAgain);
	}
}