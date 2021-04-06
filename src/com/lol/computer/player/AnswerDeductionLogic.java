package com.lol.computer.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lol.constant.Constants;
import com.lol.helper.PlayerInformation;

public class AnswerDeductionLogic {

	static Boolean flag = false;

	public static void getAnswerInformation(String messageNumber, List<String> messageDetailsList) {
		String locFound = checkIsTreasureLocFound();
		System.out.println("Message [" + messageNumber + "] Player " + messageDetailsList.get(4) + " has "
				+ messageDetailsList.get(3) + " "
				+ PlayerInformation.getInstance().getTerrianTokenInformation(messageDetailsList.get(2))
				+ " terrain between "
				+ PlayerInformation.getInstance().getDirectionInformation(messageDetailsList.get(0)) + " and "
				+ PlayerInformation.getInstance().getDirectionInformation(messageDetailsList.get(1)) + "\n");
		if (!PlayerInformation.getInstance().getPlayerName().equals(messageDetailsList.get(4))
				&& !Constants.YES.equals(locFound)) {
			processAnswerMessage(messageDetailsList);

			 AnswerDeductionHelper.checkAllToken();
		}
		System.out.println(ComputerPlayer.getInstance().getNotTreasureLoc());
	}

	public static void processAnswerMessage(List<String> messageDetailsList) {

		String playerName = messageDetailsList.get(4);
		String Diretion1 = messageDetailsList.get(0).substring(0, 2);
		String Diretion2 = messageDetailsList.get(1).substring(0, 2);

		Map<Integer, Set<String>> areaTokenSet = new HashMap<>();
		areaTokenSet.put(Constants.NOT_WITH_PLAYER_TERRAIN, new HashSet<>());
		areaTokenSet.put(Constants.CONFIRM_TERRAIN, new HashSet<>());
		areaTokenSet.put(Constants.TENTATIVE_TERRAIN, new HashSet<>());

		Node directionHead = ComputerPlayer.getInstance().createNode(Diretion1);
		Node dirtectionTail = ComputerPlayer.getInstance().createNode(Diretion2);

		if (directionHead.direction.equals(dirtectionTail.direction))
			AnswerDeductionHelper.getTerrainStatus(ComputerPlayer.getInstance().getAllPlayerTrrianMap(), areaTokenSet,
					playerName);
		else {
			while (directionHead != dirtectionTail) {
				AnswerDeductionHelper.getTerrainStatus(directionHead.terrainList, areaTokenSet, playerName);
				directionHead = directionHead.next;
			}
		}

		CheckTerrainStatus(areaTokenSet, messageDetailsList);
		checkIsTreasureLocFound();
	}

	private static void CheckTerrainStatus(Map<Integer, Set<String>> areaTokenSet, List<String> messageDetailsList) {

		String areaToken = messageDetailsList.get(2);
		String noOfTokens = messageDetailsList.get(3);
		int tokenCount = Integer.valueOf(noOfTokens);
		String Diretion1 = messageDetailsList.get(0).substring(0, 2);
		String Diretion2 = messageDetailsList.get(1).substring(0, 2);
		String playerName = messageDetailsList.get(4);

		if (Constants.BEACH_CHAR.equals(areaToken))
			updateAreaTokenSet(areaToken, areaTokenSet);

		else if (Constants.FOREST_CHAR.equals(areaToken))
			updateAreaTokenSet(areaToken, areaTokenSet);

		else if (Constants.MOUNTAINS_CHAR.equals(areaToken))
			updateAreaTokenSet(areaToken, areaTokenSet);

		List<List<String>> value = updateStatus(areaTokenSet, areaToken, tokenCount, Diretion1, Diretion2, playerName);
		if (ComputerPlayer.getInstance().getTentativeToken() != null) {

			if (ComputerPlayer.getInstance().getTentativeToken().get(playerName).get(areaToken) != null
					&& !(value.isEmpty())) {
				ComputerPlayer.getInstance().getTentativeToken().get(playerName).remove(areaToken);
				ComputerPlayer.getInstance().getTentativeToken().get(playerName).put(areaToken, value);
			}
		}
	}

	public static List<List<String>> updateStatus(Map<Integer, Set<String>> areaTokenSet, String areaToken,
			int tokenCount, String Diretion1, String Diretion2, String playerName) {

		List<List<String>> updatedList = new ArrayList<>();

		if (tokenCount == 0 && !areaTokenSet.get(-1).isEmpty())
			updateZeroterrainTokenInformation(areaTokenSet.get(-1), playerName);
		else if (areaTokenSet.get(-1).size() > 0)
			tokenCount = tokenCount - areaTokenSet.get(1).size();

		if (tokenCount > 0 && !(areaTokenSet.get(-1).isEmpty())) {

			if (areaTokenSet.get(-1).size() == tokenCount)
				AnswerDeductionHelper.updateTerrainTokenMap(areaTokenSet.get(-1), playerName);

			if (areaTokenSet.get(-1).size() > tokenCount) {
				List<String> value = new ArrayList<>();
				value.add(Diretion1);
				value.add(Diretion2);
				value.add(Integer.toString(tokenCount));
				if (ComputerPlayer.getInstance().getTentativeToken() == null) {
					Map<String, Map<String, List<List<String>>>> tentativeToken = new HashMap<>();
					List<String> PlayerList1 = PlayerInformation.getInstance().getPlayerNameList();
					for (String player : PlayerList1) {
						if (!player.equals(PlayerInformation.getInstance().getPlayerName()))
							tentativeToken.put(player, new HashMap<>());
					}
					ComputerPlayer.getInstance().setTentativeToken(tentativeToken);
				} else if (ComputerPlayer.getInstance().getTentativeToken().get(playerName).get(areaToken) == null) {
					List<List<String>> contain = new ArrayList<>();
					contain.add(value);
					ComputerPlayer.getInstance().getTentativeToken().get(playerName).put(areaToken, contain);

				} else {
					ComputerPlayer.getInstance().getTentativeToken().get(playerName).get(areaToken).forEach(loca -> {
						updatedList.add(loca);
						if (!(loca.get(0).equals(Diretion1)) && !(loca.get(1).equals(Diretion2))) {
							flag = true;
						}
					});
					if (flag.equals(true)) {
						updatedList.add(value);
						flag = false;
					}
				}
			}
		}
		return updatedList;
	}

	public static Map<Integer, Set<String>> updateAreaTokenSet(String areaToken,
			Map<Integer, Set<String>> areaTokenSet) {

		areaTokenSet.entrySet().stream().forEach(value -> {
			value.getValue().removeIf((terrain) -> (!Character.toString(terrain.charAt(1)).equals(areaToken)));
		});
		return areaTokenSet;
	}

	public static String checkIsTreasureLocFound() {
		for (String terrain : ComputerPlayer.getInstance().getAllPlayerTrrianMap().keySet()) {

			Map<String, Integer> terrainMap = ComputerPlayer.getInstance().getAllPlayerTrrianMap().get(terrain);
			int sum = terrainMap.values().stream().reduce(0, Integer::sum);
			if (sum == 1) {
				Set<String> treasureNotLocSet = ComputerPlayer.getInstance().getNotTreasureLoc();
				treasureNotLocSet.add(terrain);
				ComputerPlayer.getInstance().setNotTreasureLoc(treasureNotLocSet);
			} else if (sum == 0) {
				Set<String> treasureLocSet = ComputerPlayer.getInstance().getTreasureLoc();
				treasureLocSet.add(terrain);
				ComputerPlayer.getInstance().setTreasureLoc(treasureLocSet);
			}
		}
		System.out.println(
				"Number of non Treasure location count ::  " + ComputerPlayer.getInstance().getNotTreasureLoc().size());
		System.out.println("Treasure Location identified  ::  " + ComputerPlayer.getInstance().getTreasureLoc());
		if (ComputerPlayer.getInstance().getTreasureLoc() != null
				&& ComputerPlayer.getInstance().getTreasureLoc().size() == 2)
			return Constants.YES;
		else if (ComputerPlayer.getInstance().getNotTreasureLoc() != null
				&& ComputerPlayer.getInstance().getNotTreasureLoc().size() == 22) {
			Set<String> allLocation = ComputerPlayer.getInstance().getAllPlayerTrrianMap().keySet();
			allLocation.removeAll(ComputerPlayer.getInstance().getNotTreasureLoc());

			Set<String> treasureLocSet = new HashSet<>();
			treasureLocSet.addAll(allLocation);
			ComputerPlayer.getInstance().setTreasureLoc(treasureLocSet);
			return Constants.YES;
		}
		return Constants.NO;

	}

	private static void updateZeroterrainTokenInformation(Set<String> terrainToken, String playerName) {
		List<String> PlayerList = PlayerInformation.getInstance().getPlayerNameList();
		for (String player1 : PlayerList) {
			for (String terrain : terrainToken) {
				Map<String, Integer> terrainMap = ComputerPlayer.getInstance().getAllPlayerTrrianMap().get(terrain);
				if (terrainMap != null) {
					if (playerName.equals(player1))
						terrainMap.put(player1, 0);
					else
						terrainMap.put(player1,
								ComputerPlayer.getInstance().getAllPlayerTrrianMap().get(terrain).get(player1));

					ComputerPlayer.getInstance().getAllPlayerTrrianMap().put(terrain, terrainMap);
				}
			}
		}
		AnswerDeductionHelper.checkTentativeTerrain();
	}

	public static void updateterrainMapForNotTreasureLoc(List<String> updatedterrainList, String playerName) {
		for (String terrainToken : updatedterrainList) {
			Map<String, Integer> terrainMap = ComputerPlayer.getInstance().getAllPlayerTrrianMap().get(terrainToken);
			terrainMap.put(playerName, 0);
			ComputerPlayer.getInstance().getAllPlayerTrrianMap().put(terrainToken, terrainMap);
		}
	}
}