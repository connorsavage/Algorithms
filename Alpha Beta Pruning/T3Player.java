package main.t3;

import java.util.*;

/**
 * Artificial Intelligence responsible for playing the game of T3! Implements
 * the alpha-beta-pruning mini-max search algorithm
 */
public class T3Player {

	public T3Action choose(T3State state) {
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;

		int minOption;
		T3Action optimalChoice = null;
		Map<T3Action, T3State> mapTransitions = state.getTransitions();

		for (Map.Entry<T3Action, T3State> item : mapTransitions.entrySet()) {

			if (item.getValue().isWin()) {
				return item.getKey();
			}

			minOption = minFunction(item.getValue(), alpha, beta);

			if (minOption > alpha) {
				alpha = minOption;
				optimalChoice = item.getKey();
			} else if (minOption == alpha) {
				if (optimalChoice.compareTo(item.getKey()) > 0) {
					optimalChoice = item.getKey();
				}
			}

			if (alpha >= beta) {
				break;
			}

		}
		System.out.println(optimalChoice);
		return optimalChoice;

	}

	private int minFunction(T3State state, int alpha, int beta) {
		if (state.isTie()) {
			return 0;
		}

		if (state.isWin()) {
			return 1;
		}
		int maxOption;
		T3Action optimalChoice = null;
		Map<T3Action, T3State> mapTransitions = state.getTransitions();

		for (Map.Entry<T3Action, T3State> item : mapTransitions.entrySet()) {
			if (item.getValue().isWin()) {
				return -1;
			}

			maxOption = maxFunction(item.getValue(), alpha, beta);
			if (optimalChoice == null) {
				optimalChoice = item.getKey();
			}

			if (maxOption < beta) {
				beta = maxOption;
				optimalChoice = item.getKey();
			} else if (maxOption == beta) {
				if (optimalChoice.compareTo(item.getKey()) > 0) {
					optimalChoice = item.getKey();
				}
			}

			if (alpha >= beta) {
				break;
			}
		}

		return beta;
	}

	private int maxFunction(T3State state, int alpha, int beta) {
		if (state.isTie()) {
			return 0;
		}

		if (state.isWin()) {
			return -1;
		}
		int minOption;
		T3Action optimalChoice = null;
		Map<T3Action, T3State> mapTransitions = state.getTransitions();

		for (Map.Entry<T3Action, T3State> item : mapTransitions.entrySet()) {
			if (item.getValue().isWin()) {
				return 1;
			}

			minOption = minFunction(item.getValue(), alpha, beta);

			if (optimalChoice == null) {
				optimalChoice = item.getKey();
			}

			if (minOption > alpha) {
				alpha = minOption;
				optimalChoice = item.getKey();
			} else if (minOption == alpha) {
				if (optimalChoice.compareTo(item.getKey()) > 0) {
					optimalChoice = item.getKey();
				}
			}

			if (alpha >= beta) {
				break;
			}
		}

		return alpha;
	}

}
