package main.distle;

import java.util.*;

public class EditDistanceUtils {

	/**
	 * Returns the completed Edit Distance memoization structure, a 2D array of ints
	 * representing the number of string manipulations required to minimally turn
	 * each subproblem's string into the other.
	 * 
	 * @param s0 String to transform into other
	 * @param s1 Target of transformation
	 * @return Completed Memoization structure for editDistance(s0, s1)
	 */
	public static int[][] getEditDistTable(String s0, String s1) {
		int len1 = s0.length();
		int len2 = s1.length();

		int[][] editDistanceTable = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++) {
			editDistanceTable[i][0] = i;
		}

		for (int j = 0; j <= len2; j++) {
			editDistanceTable[0][j] = j;
		}

		for (int i = 1; i <= len1; i++) {
			for (int j = 1; j <= len2; j++) {
				if (s0.charAt(i - 1) == (s1.charAt(j - 1))) {
					editDistanceTable[i][j] = editDistanceTable[i - 1][j - 1];
				} else {

					int insertionCost;
					int deletionCost;
					int replacementCost;
					int transpositionCost;

					insertionCost = editDistanceTable[i][j - 1] + 1;
					deletionCost = editDistanceTable[i - 1][j] + 1;
					replacementCost = editDistanceTable[i - 1][j - 1] + 1;
					if ((i >= 2 && j >= 2) && (s1.charAt(j - 1) == s0.charAt(i - 2))
							&& (s0.charAt(i - 1) == s1.charAt(j - 2))) {
						transpositionCost = editDistanceTable[i - 2][j - 2] + 1;
					} else {
						transpositionCost = Integer.MAX_VALUE;
					}

					editDistanceTable[i][j] = computeMinimum(insertionCost, deletionCost, replacementCost,
							transpositionCost);

				}
			}
		}
		return editDistanceTable;

	}

	private static int computeMinimum(int insertionCost, int deletionCost, int replacementCost, int transpositionCost) {
		double firstMin = Math.min(insertionCost, deletionCost);
		double secondMin = Math.min(replacementCost, transpositionCost);
		double min = Math.min(firstMin, secondMin);
		return (int) min;
	}

	/**
	 * Returns one possible sequence of transformations that turns String s0 into
	 * s1. The list is in top-down order (i.e., starting from the largest subproblem
	 * in the memoization structure) and consists of Strings representing the String
	 * manipulations of:
	 * <ol>
	 * <li>"R" = Replacement</li>
	 * <li>"T" = Transposition</li>
	 * <li>"I" = Insertion</li>
	 * <li>"D" = Deletion</li>
	 * </ol>
	 * In case of multiple minimal edit distance sequences, returns a list with ties
	 * in manipulations broken by the order listed above (i.e., replacements
	 * preferred over transpositions, which in turn are preferred over insertions,
	 * etc.)
	 * 
	 * @param s0    String transforming into other
	 * @param s1    Target of transformation
	 * @param table Precomputed memoization structure for edit distance between s0,
	 *              s1
	 * @return List that represents a top-down sequence of manipulations required to
	 *         turn s0 into s1, e.g., ["R", "R", "T", "I"] would be two replacements
	 *         followed by a transposition, then insertion.
	 */
	public static List<String> getTransformationList(String s0, String s1, int[][] table) {
		int len1 = s0.length();
		int len2 = s1.length();

		List<String> transformationList = new ArrayList<>();
		int row = len1;
		int col = len2;

		while(true) {
			if(row == 0 && col == 0) {
				break;
			}
			
			//same letter return nothing to the list
			if (row >= 1 && col >= 1) {
				if (s0.charAt(row-1) == s1.charAt(col-1)) {
					row = row - 1;
					col = col - 1;
					continue;
				}
			}

			//Transposition
			if (row >= 2 && col >= 2) {
				if (table[row][col] == table[row - 2][col - 2] + 1) {
					if ((s0.charAt(row-1) == s1.charAt(col - 2)) && (s1.charAt(col-1) == s0.charAt(row - 2))) {
						transformationList.add("T");
						row = row - 2;
						col = col - 2;
						continue;
					}
				}
			}

			//Replace
			if (row >= 1 && col >= 1) {
				int toAdd = 1;
				if (s0.charAt(row - 1) == s1.charAt(col - 1)) {
					toAdd = 0;
				}
				if (table[row][col] == table[row - 1][col - 1] + toAdd) {
					transformationList.add("R");
					row = row - 1;
					col = col - 1;
					continue;
				}
			}

			//insertion
			if (col >= 1) {
				if (table[row][col] == table[row][col - 1] + 1) {
					transformationList.add("I");
					col = col - 1;
					continue;
				}
			}
			
			//deletion
			if (row >= 1) { // does deletion make sense
				if (table[row][col] == table[row - 1][col] + 1) { // if so, check if actually took place
					transformationList.add("D"); // step one update transformation list
					row = row - 1; // step two update row and or col
					continue;
				}
			}
		}
		return transformationList;
	}

	/**
	 * Returns the edit distance between the two given strings: an int representing
	 * the number of String manipulations (Insertions, Deletions, Replacements, and
	 * Transpositions) minimally required to turn one into the other.
	 * 
	 * @param s0 String to transform into other
	 * @param s1 Target of transformation
	 * @return The minimal number of manipulations required to turn s0 into s1
	 */
	public static int editDistance(String s0, String s1) {
		if (s0.equals(s1)) {
			return 0;
		}
		return getEditDistTable(s0, s1)[s0.length()][s1.length()];
	}

	/**
	 * See {@link #getTransformationList(String s0, String s1, int[][] table)}.
	 */
	public static List<String> getTransformationList(String s0, String s1) {
		return getTransformationList(s0, s1, getEditDistTable(s0, s1));
	}

}
