import java.util.*;

public class PokerBoard extends Board{
	private double bankRoll;
	public static final int[] POINT_VALUES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
	public static final String[] ranks = {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};
	public static final String[] suits = {"spades", "hearts", "diamonds", "clubs"};


	public PokerBoard(int size, String[] ranks, String[] suits, int[] pointValues, int cash){
		super(size, ranks, suits, pointValues);
		bankRoll = cash;
	}

	public String checkHand(){
		Card[] hand=new Card[5];
		updateBankRoll(-0.5);
		for(int i=0;i<hand.length;i++){
			hand[i]=super.cardAt(i);
		}

		int[]ranks=new int[13];
		for(int x=0; x<=12;x++){
			ranks[x]=0;
		}
		for(int x=0;x<=4;x++){
			ranks[hand[x].pointValue()-1]++;
		}

		//pairs
		boolean onePair=false,twoPair=false,threePair=false,fullHouse=false,fourOfAKind=false;

		int pairs1=1,pairs2=1;
		for(int x=12; x>=0; x--){
			if(ranks[x]>pairs1){
				if(pairs1!=1){
					pairs2=ranks[x];
				}
				else{
					pairs2=pairs1;
					pairs1=ranks[x];
				}
				pairs1=ranks[x];
			}else if(ranks[x]>pairs2){
				pairs2=ranks[x];
			}
		}
		if((pairs1==3 && pairs2==2) || (pairs1==2 && pairs2==3)){
			fullHouse=true;
		}
		if(pairs1==4){
			fourOfAKind=true;
		}
		if(pairs1==3){
			threePair=true;
		}
		if(pairs1==2&&pairs2==2){
			twoPair=true;
		}
		if(pairs1==2){
			onePair=true;
		}

		//flush
		boolean flush=true;
		for (int x=0; x<4; x++) {
			if (!hand[x].suit().equalsIgnoreCase(hand[x+1].suit()))
					flush=false;
		}
		//straight
		int highestValue=0;
		boolean straight=false;
		for(int x=1; x<=9;x++){
			if(ranks[x]==1&&ranks[x+1]==1&&ranks[x+2]==1&&ranks[x+3]==1&&ranks[x+4]==1){
				straight=true;
			break;
			}
		}
		if(straight && (ranks[10]==1&&ranks[11]==1&&ranks[12]==1&&ranks[1]==1)){
			highestValue=14; /*this is here to check if royal flush */
		}
		if(highestValue == 14 && flush){
			updateBankRoll(125);
			return "Royal Flush: You win $250";
		}else if(straight && flush){
			updateBankRoll(25);
			return "Straight Flush: You win $50";
		}else if(fourOfAKind){
			updateBankRoll(12.5);
			return "Four of a Kind: You earn $25";
		}else if(fullHouse){
			updateBankRoll(3);
			return "Full House: You earn $6";
		}else if(flush){
			updateBankRoll(2.5);
			return "Flush: You win $6";
		}else if(straight){
			updateBankRoll(2);
			return "Straight: You win $4";
		}else if(threePair){
			updateBankRoll(1.5);
			return "Three of a Kind: You earn $3";
		}else if(twoPair){
			updateBankRoll(1);
			return "Two Pair: You earn $2";
		}else if(onePair){
			updateBankRoll(.5);
			return "One Pair: You earn $1.";
		}
			return "Lose";
	}
	public void updateBankRoll(double amountToAdd){
		setBankRoll(getBankRoll() + amountToAdd);
	}
	public void replaceHand(List<Integer> indexValues){
		replaceSelectedCards(indexValues);
	}

	public boolean anotherPlayIsPossible(){
		if(getBankRoll() < 0)
			return false;
		return true;
	}

	public boolean isLegal(List<Integer> selectedCards){
		return true;
	}

	public double getBankRoll(){
		return bankRoll;
	}
	public void setBankRoll(double newRoll){
		bankRoll = newRoll;
	}
	public String endGame(){
		return "You finished with $" + getBankRoll() + ".";
	}
}