
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class PrisonersDilemma {
	/*
	 * This Java program models the two-player Prisoner's Dilemma game. We use the
	 * integer "0" to represent cooperation, and "1" to represent defection.
	 * 
	 * Recall that in the 2-players dilemma, U(DC) > U(CC) > U(DD) > U(CD), where we
	 * give the payoff for the first player in the list. We want the three-player
	 * game to resemble the 2-player game whenever one player's response is fixed,
	 * and we also want symmetry, so U(CCD) = U(CDC) etc. This gives the unique
	 * ordering
	 * 
	 * U(DCC) > U(CCC) > U(DDC) > U(CDC) > U(DDD) > U(CDD)
	 * 
	 * The payoffs for player 1 are given by the following matrix:
	 */

    static int[][][] payoff = {
            {{6, 3}, // payoffs when first and second players cooperate 
            {3, 0}}, // payoffs when first player coops, second defects
            {{8, 5}, // payoffs when first player defects, second coops
            {5, 2}}};	// payoffs when first and second players defect

        private static void showPayoff() {

            for (int i = 0; i < payoff.length; i++) {
                for (int j = 0; j < payoff[0].length; j++) {
                    for (int k = 0; k < payoff[0][0].length; k++) {

                        System.out.printf(
                                "Player 1 %-10s Player 2 %-10s Player 3 %-10s --> "
                                + " Player 1's payoff: %d%n",
                                i == 0 ? "Cooperate," : "Defect,",
                                j == 0 ? "Cooperate," : "Defect,",
                                k == 0 ? "Cooperate" : "Defect",
                                payoff[i][j][k]);
                    }
                }
            }
            System.out.println();
        }
        
    	/*
    	 * So payoff[i][j][k] represents the payoff to player 1 when the first player's
    	 * action is i, the second player's action is j, and the third player's action
    	 * is k.
    	 * 
    	 * In this simulation, triples of players will play each other repeatedly in a
    	 * 'match'. A match consists of about 100 rounds, and your score from that match
    	 * is the average of the payoffs from each round of that match. For each round,
    	 * your strategy is given a list of the previous plays (so you can remember what
    	 * your opponent did) and must compute the next action.
    	 */

    	abstract class Player {
    		// This procedure takes in the number of rounds elapsed so far (n), and
    		// the previous plays in the match, and returns the appropriate action.
    		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
    			throw new RuntimeException("You need to override the selectAction method.");
    		}

    		// Used to extract the name of this player class.
    		final String name() {
    			String result = getClass().getName();
    			return result.substring(result.indexOf('$') + 1);
    		}
    	}
        
    	///////////////////////// Start of Players Declaration /////////////////////////
        // Player #1: Nice Player - always cooperates 
    	
    	class NicePlayer extends Player {
    		// NicePlayer always cooperates
    		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
    			return 0;
    		}
    	}

        // Player #2: Nasty Player - always defects 
    	
    	class NastyPlayer extends Player {
    		// NastyPlayer always defects
    		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
    			return 1;
    		}
    	}
        
       // Player #3: Random Player - picks his actions randomly  
       
    	class RandomPlayer extends Player {
    		// RandomPlayer randomly picks his action each time
    		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
    			if (Math.random() < 0.5)
    				return 0; // cooperates half the time
    			else
    				return 1; // defects half the time
    		}
    	}
    	
        // Player #4: Tit for Tat Player - plays nice first, but uses T4T strategy after 
    	
    	class T4TPlayer extends Player {
    		// Picks a random opponent at each play,
    		// and uses the 'tit-for-tat' strategy against them
    		int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
    			if (n == 0)
    				return 0; // cooperate by default
    			if (Math.random() < 0.5)
    				return oppHistory1[n - 1];
    			else
    				return oppHistory2[n - 1];
    		}
    	}

    
    	///////////////////////// End of Players Declaration /////////////////////////
    	
    	
    	/*
    	 * In our tournament, each pair of strategies will play one match against each
    	 * other. This procedure simulates a single match and returns the scores.
    	 */
    	float[] scoresOfMatch(Player A, Player B, Player C, int rounds) {

            int[] HistoryA = new int[0], HistoryB = new int[0], HistoryC = new int[0];
            float ScoreA = 0, ScoreB = 0, ScoreC = 0;

            for (int i = 0; i < rounds; i++) {

                int PlayA = A.selectAction(i, HistoryA, HistoryB, HistoryC);
                int PlayB = B.selectAction(i, HistoryB, HistoryC, HistoryA);
                int PlayC = C.selectAction(i, HistoryC, HistoryA, HistoryB);

                ScoreA = ScoreA + payoff[PlayA][PlayB][PlayC];
                ScoreB = ScoreB + payoff[PlayB][PlayC][PlayA];
                ScoreC = ScoreC + payoff[PlayC][PlayA][PlayB];

                HistoryA = extendIntArray(HistoryA, PlayA);
                HistoryB = extendIntArray(HistoryB, PlayB);
                HistoryC = extendIntArray(HistoryC, PlayC);
            }

            float[] result = {ScoreA / rounds, ScoreB / rounds, ScoreC / rounds};
            return result;
        }

        // This is a helper function needed by scoresOfMatch.
        int[] extendIntArray(int[] arr, int next) {

            int[] result = new int[arr.length + 1];
            for (int i = 0; i < arr.length; i++) {
                result[i] = arr[i];
            }

            result[result.length - 1] = next;
            return result;
        }

    	/*
    	 * The procedure makePlayer is used to reset each of the Players (strategies) in
    	 * between matches. When you add your own strategy, you will need to add a new
    	 * entry to makePlayer, and change numPlayers.
    	 */

    	int numPlayers = 4; // includes custom Players

    	Player makePlayer(int which) {
    		switch (which) {
    			case 0:
    				return new NicePlayer();
    			case 1:
    				return new NastyPlayer();
    			case 2:
    				return new RandomPlayer();
    			case 3:
    				return new T4TPlayer();
    		}
    		throw new RuntimeException("Bad argument passed to makePlayer");
    	}
    	
    	/* Finally, the remaining code actually runs the tournament. */
        private static Map<String, Integer> map1stPlace = new HashMap<>();
        private static Map<String, Integer> mapTop3 = new HashMap<>();
    	private static Map<String, Double> playerScores = new HashMap<>();

        public static Map<String, Double> sortByDoubleValuesDesc(Map<String, Double> map) {

            if (map == null) {
                return map;
            }

            List<Map.Entry<String, Double>> list = new LinkedList<>(map.entrySet());

            // Sort based on values, in descending order
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> mapEntry1, Map.Entry<String, Double> mapEntry2) {
                    return mapEntry2.getValue().compareTo(mapEntry1.getValue());
                }
            });

            // Using LinkedHashMap to preserve order
            Map<String, Double> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Double> mapEntry : list) {
                sortedMap.put(mapEntry.getKey(), mapEntry.getValue());
            }

            return sortedMap;
        }

        public static Map<String, Integer> sortByIntValuesDesc(Map<String, Integer> map) {

            if (map == null) {
                return map;
            }

            List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());

            // Sort based on values, in descending order
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> mapEntry1, Map.Entry<String, Integer> mapEntry2) {
                    return mapEntry2.getValue().compareTo(mapEntry1.getValue());
                }
            });

            // Using LinkedHashMap to preserve order
            Map<String, Integer> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> mapEntry : list) {
                sortedMap.put(mapEntry.getKey(), mapEntry.getValue());
            }

            return sortedMap;
        }
        
    	boolean verbose = false; // set verbose = false if you get too much text output
    	
    	void runTournament(int tournament_num) {
            float[] totalScore = new float[numPlayers];

            // This loop plays each triple of players against each other.
            // Note that we include duplicates: two copies of your strategy will play once
            // against each other strategy, and three copies of your strategy will play once.
            for (int i = 0; i < numPlayers; i++) {
                for (int j = i; j < numPlayers; j++) {
                    for (int k = j; k < numPlayers; k++) {

                        // Create a fresh copy of each player
                        Player A = makePlayer(i);
                        Player B = makePlayer(j);
                        Player C = makePlayer(k);

                        // Between 90 and 110 rounds
                        int rounds = 90 + (int) Math.rint(20 * Math.random());
                        // Run match
                        float[] matchResults = scoresOfMatch(A, B, C, rounds);
                        totalScore[i] = totalScore[i] + matchResults[0];
                        totalScore[j] = totalScore[j] + matchResults[1];
                        totalScore[k] = totalScore[k] + matchResults[2];

                        if (verbose) {
                            System.out.printf("%-16s scored %.2f points, "
                                    + "%-16s scored %.2f points, "
                                    + "%-16s scored %.2f points.%n", A.name(),
                                    matchResults[0], B.name(), matchResults[1],
                                    C.name(), matchResults[2]);
                        }
                    }
                }
            }

            int[] sortedOrder = new int[numPlayers];
            // This loop sorts the players by their score.
            for (int i = 0; i < numPlayers; i++) {
                int j = i - 1;
                for (; j >= 0; j--) {
                    if (totalScore[i] > totalScore[sortedOrder[j]]) {
                        sortedOrder[j + 1] = sortedOrder[j];
                    } else {
                        break;
                    }
                }
                sortedOrder[j + 1] = i;
            }

            // Finally, print out the sorted results.
            if (verbose) {
                System.out.println();
            }

            System.out.println("***************************************");
            System.out.println("Results of Tournament "+tournament_num);
            System.out.println("***************************************");
            for (int i = 0; i < numPlayers; i++) {

                String playerName = makePlayer(sortedOrder[i]).name();
                System.out.printf("%-20s %.3f points.%n",
                        playerName, totalScore[sortedOrder[i]]);

                // Average score in this tournament
                Double currentScore = playerScores.get(playerName);
                if (currentScore != null) {
                    currentScore += totalScore[sortedOrder[i]];
                } else {
                    currentScore = Double.valueOf(totalScore[sortedOrder[i]]);
                }
                playerScores.put(playerName, currentScore);

                if (i < 1) {
                    // 1st place finishes
                    Integer numTop1 = map1stPlace.get(playerName);
                    map1stPlace.put(playerName, (numTop1 == null) ? 1 : numTop1 + 1);
                }

                if (i < 3) {
                    // Top 3 finishes
                    Integer numTop3 = mapTop3.get(playerName);
                    mapTop3.put(playerName, (numTop3 == null) ? 1 : numTop3 + 1);
                }
            }
            
    		//System.out.println("Tournament Results");
    		//for (int i = 0; i < numPlayers; i++) {
    		//	System.out.println("Position " +(i+1)+ ": "+ makePlayer(sortedOrder[i]).name() + ": " + totalScore[sortedOrder[i]] + " points.");

    	} // end of runTournament()

       
        
	public static void main (String args[]) { 
		System.out.println("Start of Main");
		PrisonersDilemma instance = new PrisonersDilemma();
        showPayoff();

        for (int i = 0; i < instance.numPlayers; i++) {
            String playerName = instance.makePlayer(i).name();
            map1stPlace.put(playerName, 0);
            mapTop3.put(playerName, 0);
        }

        ////// Number of tournaments to be executed!
        int totalTour = 5000; 
        for (int numTour = 0; numTour < totalTour; numTour++) {
            instance.runTournament(numTour+1);
            System.out.println();
        }

        playerScores = sortByDoubleValuesDesc(playerScores);
        System.out.println("Average score after " + totalTour + " tournaments:");
        for (Map.Entry<String, Double> playerScore : playerScores.entrySet()) {

            System.out.printf("%-20s %.3f points.%n",
                    playerScore.getKey(),
                    playerScore.getValue() / totalTour);
        }

        map1stPlace = sortByIntValuesDesc(map1stPlace);

        System.out.println("\n==================================="
                + " Rankings! ===================================");
        System.out.printf("%-25s %-30s %-20s%n",
                "Player Name", "1st Place", "Top 3");
        for (Map.Entry<String, Integer> mEntry : map1stPlace.entrySet()) {

            System.out.printf("%-25s ", mEntry.getKey());
            int numTop1 = mEntry.getValue();
            int numTop3 = mapTop3.get(mEntry.getKey());
            System.out.printf("%-30s ", String.format("%-12s (%2.2f%%) ",
                    (numTop1 + "/" + totalTour), ((double) numTop1 / totalTour) * 100));
            System.out.printf("%-12s (%2.2f%%)%n",
                    (numTop3 + "/" + totalTour), ((double) numTop3 / totalTour) * 100);
        }
        System.out.println("==================================="
                + " Rankings! ===================================");
	}
}