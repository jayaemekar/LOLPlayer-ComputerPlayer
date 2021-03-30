package com.lol.player;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import com.lol.computer.player.ComputerPlayer;
import com.lol.computer.player.ComputerPlayerDeductionLogic;
import com.lol.computer.player.QuestionDeductionLogic;
import com.lol.constant.Constants;
import com.lol.helper.PlayerInformation;
import com.lol.helper.Utility;
import com.lol.validation.PlayerInfoValidation;

public class GameProcessing {

	private static Integer roundNumber = 0;
	private static Integer roundCount = 0;
	private static boolean treasureGuessSent = false;

	/**
	 * This method is to parse the dice information and start the Game play
	 * 04:P1,SEM,NWB,SWF
	 * This Dice Rolled message P1-Dice rolled for Player,
	 * @param messageNumber
	 * @param messageDetailsList
	 */
	public void playersTurn(String messageNumber, List<String> messageDetailsList) {
		String playerName = messageDetailsList.get(0);
		messageDetailsList.remove(0);
		if (roundNumber == 0 || roundNumber % PlayerInformation.getInstance().getNumberOfPlayers() == 0) {
			roundCount++;
			System.out.println(
					"\n====== ====== ====== ====== ROUND-" + roundCount + " ====== ====== ====== ====== ======\n");
		}
		roundNumber++;

		System.out.println("_______________________Player " + playerName + "'s turn____________________________");
		String istreasureLocFound = Constants.NO;
		if (!treasureGuessSent) {
			istreasureLocFound = ComputerPlayerDeductionLogic.checkIsTreasureLocFound();
			System.out.println("\nGuess the Treasure Locations???(YES/NO)???  :: " + istreasureLocFound);

		}

		if (Constants.YES.equalsIgnoreCase(istreasureLocFound))
			treasureGuessSent = treasureGuess();
		else {
			System.out.println(
					"Message [" + messageNumber + "] Player " + playerName + " rolled the dices " + messageDetailsList);
			if (PlayerInformation.getInstance().getPlayerName().equals(playerName) && !treasureGuessSent) {
				
				//createQuestion(messageDetailsList);
				
				QuestionDeductionLogic.createQuestion(messageDetailsList);
			} else if (treasureGuessSent) {
				System.out.println("Ignoring rolled the dices... Treasure guess sent already..!! waiting for reply...");
			}
		}
	}

	/**
	 * This method is to build the question form human user input
	 * 05:NNF,NEF,A,P3
	 * 
	 * @param messageDetailsList
	 */

	private void createQuestion(List<String> messageDetailsList) {
		System.out.println("Select Two Die Faces...... ");
		StringBuilder message = new StringBuilder(); //NN W,WW W,WW E
		message.append("05:");

		System.out.println("Enter DIE Face 1 :" + messageDetailsList);
		String dieFace1 = messageDetailsList.get(new Random().nextInt(messageDetailsList.size()));
		System.out.println("Selected DIE Face 1 :" + dieFace1);
		if (PlayerInfoValidation.getInstance().validateDiceFace(dieFace1) && messageDetailsList.contains(dieFace1)) {
			message.append(dieFace1).append(Constants.COMMA);
		} else {
			System.out.println("\n ====== Invalid DIE Face! Create question again ====== \n");
			createQuestion(messageDetailsList);
			return;
		}

		System.out.println("Enter DIE Face 2 :" + messageDetailsList);

		String dieFace2 = messageDetailsList.get(new Random().nextInt(messageDetailsList.size()));
		System.out.println("Selected DIE Face 2 :" + dieFace2);
		if (PlayerInfoValidation.getInstance().validateDiceFace(dieFace2) && messageDetailsList.contains(dieFace2)
				&& !dieFace2.equals(dieFace1)) {
			message.append(dieFace2).append(Constants.COMMA);
		} else {
			System.out.println(" ====== Invalid DIE Face! Create question again ====== \n");
			createQuestion(messageDetailsList);
			return;
		}

		String dieFace1Terrian = String.valueOf(dieFace1.charAt(2));
		String dieFace2Terrian = String.valueOf(dieFace2.charAt(2));

		if (Constants.FOREST_CHAR.equals(dieFace1Terrian) && Constants.FOREST_CHAR.equals(dieFace2Terrian)) {
			message.append(Constants.FOREST_CHAR).append(Constants.COMMA);
		} else if (Constants.BEACH_CHAR.equals(dieFace1Terrian) && Constants.BEACH_CHAR.equals(dieFace2Terrian)) {
			message.append(Constants.BEACH_CHAR).append(Constants.COMMA);
		} else if (Constants.MOUNTAINS_CHAR.equals(dieFace1Terrian)
				&& Constants.MOUNTAINS_CHAR.equals(dieFace2Terrian)) {
			message.append(Constants.MOUNTAINS_CHAR).append(Constants.COMMA);
		} else if (Constants.WILD_CHAR.equals(dieFace1Terrian) && Constants.WILD_CHAR.equals(dieFace2Terrian)) {
			System.out.println("Select Area Token: M-Mountains, F-Forest, B-Beach, A-all ");
			String[] areaTokenArr = { "A", "B", "F", "M" };
			String terrianToken = areaTokenArr[new Random().nextInt(areaTokenArr.length)];// "A";//sc.nextLine();
			System.out.println("Selected Area Terrian ::" + terrianToken);
			if (PlayerInfoValidation.getInstance().validateTerrianName(terrianToken))
				message.append(terrianToken).append(Constants.COMMA);
			else {
				System.out.println(" ====== Invalid Terrian Token! Ask Question Again!! ====== \n");
				createQuestion(messageDetailsList);
				return;
			}
		} else if (Constants.WILD_CHAR.equals(dieFace1Terrian) || Constants.WILD_CHAR.equals(dieFace2Terrian)) {

			if (Constants.FOREST_CHAR.equals(dieFace1Terrian) || Constants.FOREST_CHAR.equals(dieFace2Terrian)) {
				System.out.println("Select Area Token: A-all or " + Constants.FOREST_CHAR + "- Forest only");
				String[] areaTokenArr = { "A", "F" };
				String terrianToken = areaTokenArr[new Random().nextInt(areaTokenArr.length)];// "A";//sc.nextLine();
				System.out.println("Selected Area Terrian ::" + terrianToken);
				if (Constants.FOREST_CHAR.equals(terrianToken) || Constants.ALL_CHAR.equals(terrianToken))
					message.append(terrianToken).append(Constants.COMMA);
				else {
					System.out.println(" ====== Invalid Terrian Token! Ask Question Again!! ====== \n");
					createQuestion(messageDetailsList);
					return;
				}
			} else if (Constants.BEACH_CHAR.equals(dieFace1Terrian) || Constants.BEACH_CHAR.equals(dieFace2Terrian)) {
				System.out.println("Select Area Token: A-all or " + Constants.BEACH_CHAR + "- Beach only");
				String[] areaTokenArr = { "A", "B" };
				String terrianToken = areaTokenArr[new Random().nextInt(areaTokenArr.length)];// "A";//sc.nextLine();
				System.out.println("Selected Area Terrian ::" + terrianToken);
				if (Constants.BEACH_CHAR.equals(terrianToken) || Constants.ALL_CHAR.equals(terrianToken))
					message.append(terrianToken).append(Constants.COMMA);
				else {
					System.out.println(" ====== Invalid Terrian Token! Ask Question Again!! ====== \n");
					createQuestion(messageDetailsList);
					return;
				}

			} else if (Constants.MOUNTAINS_CHAR.equals(dieFace1Terrian)
					|| Constants.MOUNTAINS_CHAR.equals(dieFace2Terrian)) {
				System.out.println("Select Area Token: A-all or " + Constants.MOUNTAINS_CHAR + "- Mountain only");
				String[] areaTokenArr = { "A", "M" };
				String terrianToken = areaTokenArr[new Random().nextInt(areaTokenArr.length)];// "A";//sc.nextLine();
				System.out.println("Selected Area Terrian ::" + terrianToken);
				if (Constants.MOUNTAINS_CHAR.equals(terrianToken) || Constants.ALL_CHAR.equals(terrianToken))
					message.append(terrianToken).append(Constants.COMMA);
				else {
					System.out.println(" ====== Invalid Terrian Token! Ask Question Again!! ====== \n");
					createQuestion(messageDetailsList);
					return;
				}
			}

		} else {
			message.append(Constants.ALL_CHAR).append(Constants.COMMA);
		}

		System.out.println("Enter Player number whom you want to ask the question :");

		String playerAnswering = PlayerInformation.getInstance().getPlayerNameList()
				.get(new Random().nextInt(PlayerInformation.getInstance().getPlayerNameList().size()));
		System.out.println("Selected Player ::" + playerAnswering);
		if (PlayerInfoValidation.getInstance().validatePlayerName(playerAnswering)
				&& !playerAnswering.equalsIgnoreCase(PlayerInformation.getInstance().getPlayerName())) {
			message.append(playerAnswering);
		} else {
			System.out.println(" ====== Invalid Player! Create question again ====== \n");
			createQuestion(messageDetailsList);
			return;
		}

		Utility.writeFile(PlayerInformation.getInstance().getFileWritePath(), message.toString());
		Utility.parseMessage(message.toString());
	}

	/**
	 * Parsing question Information Message 05:NNF,NEF,A,P3
	 * 
	 * @param messageNumber
	 * @param messageDetailsList
	 */
	public void getQuestionInformation(String messageNumber, List<String> messageDetailsList) {

		String playerName = messageDetailsList.get(3);
		if (PlayerInformation.getInstance().getPlayerName().equals(playerName)) {
			System.out.println(" ====== Question is asked to you ====== ");
			createAnswerInformationMsg(messageDetailsList);
		} else {
			System.out.println("\nMessage [" + messageNumber + "] Question asked to player " + messageDetailsList.get(3)
					+ ", How many "
					+ PlayerInformation.getInstance().getTerrianTokenInformation(messageDetailsList.get(2))
					+ " terrains between " + messageDetailsList.get(0) + " and " + messageDetailsList.get(1));
		}
	}

	@SuppressWarnings("resource")
	private void createAnswerInformationMsg(List<String> messageDetailsList) {

		// Message [06] Player P3 has 2 token in Forest terrain between North
		// and North East
		// 05:NNF,NEF,A,P1
		// 06:NNF,NEF,F,2,P1,P3
		System.out.println(
				"How many " + PlayerInformation.getInstance().getTerrianTokenInformation(messageDetailsList.get(2))
						+ " terrain between "
						+ PlayerInformation.getInstance().getDirectionInformation(messageDetailsList.get(0)) + " "
						+ PlayerInformation.getInstance().getTerrianTokenInformation(messageDetailsList.get(0))
						+ " and " + PlayerInformation.getInstance().getDirectionInformation(messageDetailsList.get(1))
						+ " " + PlayerInformation.getInstance().getTerrianTokenInformation(messageDetailsList.get(1)));

		StringBuilder message = new StringBuilder();
		message.append("06:").append(messageDetailsList.get(0)).append(Constants.COMMA)
				.append(messageDetailsList.get(1)).append(Constants.COMMA).append(messageDetailsList.get(2))
				.append(Constants.COMMA);

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the total number of terrian you have in the selected location :");
		String terrianCount = sc.nextLine();
		message.append(terrianCount).append(Constants.COMMA).append(PlayerInformation.getInstance().getPlayerName())
				.append(Constants.COMMA).append(messageDetailsList.get(3));

		Utility.writeFile(PlayerInformation.getInstance().getFileWritePath(), message.toString());
		Utility.parseMessage(message.toString());
	}

	/**
	 * this method is to get the answer information 06:NNF,NEF,F,2,P3,P1
	 * 
	 * @param messageNumber
	 * @param messageDetailsList
	 */
	public void getAnswerInformation(String messageNumber, List<String> messageDetailsList) {
		ComputerPlayerDeductionLogic.processAnswerMessage(messageDetailsList);
		System.out.println("Message [" + messageNumber + "] Player " + messageDetailsList.get(4) + " has "
				+ messageDetailsList.get(3) + " "
				+ PlayerInformation.getInstance().getTerrianTokenInformation(messageDetailsList.get(2))
				+ " terrain between "
				+ PlayerInformation.getInstance().getDirectionInformation(messageDetailsList.get(0)) + " and "
				+ PlayerInformation.getInstance().getDirectionInformation(messageDetailsList.get(1)) + "\n");

	}

	/**
	 * This method is to send the treasure Guess message to the server
	 * 07:P1,3F,6M
	 * @param messageNumber
	 * @param messageDetailsList
	 * @return
	 */

	public boolean treasureGuess() {
		StringBuilder message = new StringBuilder("07:");
		Set<String> treasureLoc = ComputerPlayer.getInstance().getTreasureLoc();
		String[] treasureLocArr = treasureLoc.toArray(new String[treasureLoc.size()]);
		
		message.append(PlayerInformation.getInstance().getPlayerName()).append(Constants.COMMA);
		String guessOne = treasureLocArr[0];
		System.out.println("First guess identified as :: " +guessOne);
		if (PlayerInfoValidation.getInstance().validateTerrianLocation(guessOne)) {
			message.append(guessOne).append(Constants.COMMA);
		} else {
			System.out.println("\n ====== Invalid terrian token create message again ====== \n");
			treasureGuess();
			return false;
		}
		
		String guessTwo = treasureLocArr[1];
		System.out.println("Second guess identified as :: "+guessTwo);
		if (PlayerInfoValidation.getInstance().validateTerrianLocation(guessTwo)) {
			message.append(guessTwo);
		} else {
			System.out.println("\n ====== Invalid terrian token create message again ====== \n");
			treasureGuess();
			return false;
		}

		Utility.writeFile(PlayerInformation.getInstance().getFileWritePath(), message.toString());
		Utility.parseMessage(message.toString());
		System.out.println("\n ====== ====== ====== ====== Your guess sent to server ====== ====== ====== ======\n");
		return true;
	}

	public void getTreasureGuessInformation(String messageNumber, List<String> messageDetailsList) {
		System.out.println("\nMessage [" + messageNumber + "] Player " + messageDetailsList.get(0)
				+ " says treasures are located at " + messageDetailsList.get(1) + " and " + messageDetailsList.get(2));
	}

	/**
	 * This method is used to parse the winner player and token information
	 * 08:player1,wintoken1,wintoken2 eg.08:P1,3F,6M
	 * @param messageNumber
	 * @param messageDetailsList
	 */
	public void getWinnerPlayerGuessCorrect(String messageNumber, List<String> messageDetailsList) {
		System.out.println("\nMessage [" + messageNumber + "] Player " + messageDetailsList.get(0)
				+ " guessed the correct treasure locations\n");
		if (messageDetailsList.get(0).equals(PlayerInformation.getInstance().getPlayerName())) {
			System.out.println("\n\n====== ====== ====== ====== YOU ARE THE WINNER !!====== ====== ====== ======");
		} else {

			System.out.println(
					"\n======***====== PLAYER " + messageDetailsList.get(0) + " IS THE WINNER !! ======***======");
		}
		messageDetailsList.remove(0);
		System.out.println("\nThe treasure location are: " + messageDetailsList);
		System.out.println("\n======***====== GAME OVER !!======***======\n");
	}

	/**
	 * treasure guess was incorrect and disqualify 09:P2
	 * @param messageNumber
	 * @param messageDetailsList
	 * @return
	 */
	public boolean getDisqualifiedPlayerInformationOrInCorrectGuess(String messageNumber,
			List<String> messageDetailsList) {
		System.out.println("\nMessage [" + messageNumber + "] Player " + messageDetailsList.get(0)
				+ " has made incorrect guess. \n ");
		if (messageDetailsList.get(0).equals(PlayerInformation.getInstance().getPlayerName())) {
			System.out.println("_______You are disqualified from the game..Game is over for you !!________\n");

			System.out.println("_______Lets wait and see who wins the game________________________________\n");

			return false;
		} else {
			System.out.println("\n_________________" + messageDetailsList.get(0)
					+ " has been disqualified from the game._________________");
			treasureGuessSent = false;
			return treasureGuessSent;
		}

	}

}
