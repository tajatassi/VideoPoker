public class PokerRunnerGUI{
	public static void main(String[] args){
		new CardGameGUI(new PokerBoard(5, PokerBoard.ranks, PokerBoard.suits, PokerBoard.POINT_VALUES, 0));
		
	}
}