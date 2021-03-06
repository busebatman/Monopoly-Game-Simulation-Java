import java.util.ArrayList;

public class Player implements Comparable<Object> {

	private String name;
	private Piece piece;
	private Money money;
	private int purchasingPossibility;
	private int playerNum;
	private int playerOrder;
	private int turn;
	private int[] diceValue;
	private int currentIndex = 0;
	private int passMoneyAmount;
	private int jailCounter;
	private boolean isOnJail = false;
	private int totalDiceValue;
	private int[] colors;
	private int[] numOfColors;
	private int[] numOfUtilitiesBasedOnColors=new int[5];
	private ArrayList<House>[] houses = new ArrayList[5];
	

	private Hotel [] hotels = new Hotel[5];

	public Player() {

	}

	public Player(String name, int playerNum, int moneyAmount, int passMoneyAmount, int turn, Piece piece) {
		this.name = name;
		this.playerNum = playerNum;
		this.turn = turn;
		this.passMoneyAmount = passMoneyAmount;
		money = new Money(moneyAmount);
		piece = new Piece(this.playerNum);
		diceValue = new int[2];
		colors=new int[5];
		
		 // initializing houses
        for (int i = 0; i < 5; i++) { 
            houses[i] = new ArrayList<House>(); 
        } 
		
	}

	public boolean getJail() {
		return isOnJail;
	}

	public void setJail(boolean isOnJail) {
		this.isOnJail = isOnJail;
	}

	public int getCounter() {
		return jailCounter;
	}

	public void setCounter(int counter) {
		jailCounter = counter;
	}

	public void incrementCounter() {
		jailCounter++;
	}

	public int getTotalDice() {
		return totalDiceValue;
	}

	public void setTotalDice(int totalDice) {
		totalDiceValue = totalDice;
	}

	public void withdraw(int amount) {
		this.money.decMoney(amount);
	}

	public Piece get_piece() {
		return piece;
	}

	public String get_name() {
		return name;
	}

	public int get_turn() {
		return turn;
	}

	public void set_turn(int turn) {
		this.turn = turn;
	}

	public Money get_money() {
		return this.money;
	}

	public void set_money(int money) {
		this.money.setMoney(money);
	}

	public int get_playerOrder() {
		return playerOrder;
	}

	public void setPlayerOrder(int playerOrder) {
		this.playerOrder = playerOrder;
	}
	
	public int[] getColors() {
		return colors;
	}

	public void setColors(int[] colors) {
		this.colors = colors;
	}
	
	public int[] getNumOfColors() {
		return numOfColors;
	}

	public void setNumOfCol(Board b) {
		this.numOfColors = java.util.Arrays.copyOf(b.getColorArr(),
				b.getColorArr().length);
		for(int u=0;u<5;u++) {
			System.out.println(numOfColors[u]);
		}
		System.out.println("***");
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public int setSquareIndex(Board b) {

		if (isOnJail) {
			int jMoney = ((JailSquare) b.getASquare(currentIndex)).getJailMoney();
			System.out.println((jailCounter + 1) + ". turn in jail.");
			jailCounter++;

			if (jailCounter == 4) {
				System.out.println(this.name + " can continue to game");
				isOnJail = false;
				jailCounter = 0;

			} else if (diceValue[0] == diceValue[1]) {
				System.out.println("Dice values are equal. " + this.name + " can continue to game!");
				isOnJail = false;
				jailCounter = 0;

			} else if (this.get_money().getMoneyAmount() > jMoney) {
				System.out.println(this.name + " paid " + jMoney + "TL to get out of the jail!");
				this.get_money().decMoney(jMoney);
				isOnJail = false;
				jailCounter = 0;
			}

		}
		if (!isOnJail) {
			int newCurrentIndex = (currentIndex + totalDiceValue) % 40;

			if (newCurrentIndex < currentIndex) { // if the new index is less than the old index
													// then this means the user passed from the go square
				this.money.incMoney(passMoneyAmount);
				System.out.println(this.name + " has passed from the GO square");

			}

			currentIndex = newCurrentIndex;
			System.out.println("Current location: Square " + (currentIndex + 1));
			b.getASquare(currentIndex).landedOn(this);
			/*
			for(int y=0;y<5;y++) {
				System.out.println(numOfColors[y]+"   "+colors[y]);
			}
			*/

			if (b.getASquare(currentIndex) instanceof GoToJailSquare) {
				System.out.println("");
				currentIndex = b.getJailIndex();
				System.out.println("Current location: Square " + (currentIndex + 1));
				b.getASquare(currentIndex).landedOn(this);
			}
			if (b.getASquare(currentIndex) instanceof CardSquare) {
				System.out.println("");
				b.getACard().takenCard(this);
				if (b.getACard() instanceof GoToJailCard) {
					System.out.println("");
					currentIndex = b.getJailIndex();
					System.out.println("Current location: Square " + (currentIndex + 1));
					b.getASquare(currentIndex).landedOn(this);
				}
			}
		}
		return currentIndex;
	}

	public int[] getDiceValue() {
		return diceValue;
	}

	public void rollDice(Dice[] dice) {
		diceValue[0] = dice[0].getFaceValue();
		diceValue[1] = dice[1].getFaceValue();
		totalDiceValue = diceValue[0] + diceValue[1];
		System.out.println("Dice values are: " + diceValue[0] + " - " + diceValue[1] + ". Total : " + totalDiceValue);
	}

	@Override
	public int compareTo(Object comparedPlayer) {
		int compareOrder = ((Player) comparedPlayer).get_playerOrder();
		/* For Ascending order */
		return this.playerOrder - compareOrder;
	}

	public void printInfo() {
		System.out.println("The name of the player is: " + name);
		System.out.println("The amount of money: " + money.getMoneyAmount() + "TL.");
		System.out.println("The square index of the user: " + this.getCurrentIndex());
	}

	public int[] getNumOfUtilitiesBasedOnColors() {
		return numOfUtilitiesBasedOnColors;
	}

	public void setNumOfUtilitiesBasedOnColors(int[] numOfUtilitiesBasedOnColors) {
		this.numOfUtilitiesBasedOnColors = numOfUtilitiesBasedOnColors;
	}
	
	public void incrementNumOfAUtilityBasedOnColor(int index) {
		this.numOfUtilitiesBasedOnColors[index]++;
	}
	
	public boolean purchasedSameColorGroup(int index) {
		return numOfColors[index]==numOfUtilitiesBasedOnColors[index];
	}

	public ArrayList<House>[] getHouses() {
		return houses;
	}

	public void setHouses(ArrayList<House>[] houses) {
		this.houses = houses;
	}

	public Hotel [] getHotels() {
		return hotels;
	}

	public void setHotels(Hotel [] hotels) {
		this.hotels = hotels;
	}
	public void buildHouse(int colourIndex, int decAmount) {
		System.out.println(this.name+" builded a house with "+ decAmount+ " TLs." );
		
		houses[colourIndex].add(new House());
		money.decMoney(decAmount);
	}
	
	public void buildHotel(int colourIndex, int decAmount) {
		System.out.println(this.name+" builded a hotel with "+ decAmount+ " TLs." );
		hotels[colourIndex]=new Hotel();
		money.decMoney(decAmount);
	}

	public int getPurchasingPossibility() {
		return purchasingPossibility;
	}

	public void setPurchasingPossibility(int purchasingPossibility) {
		this.purchasingPossibility = purchasingPossibility;
	}
	
	public void setHouseArraylist(int index) {
		this.houses[index]=new ArrayList<House>();
	}

}
