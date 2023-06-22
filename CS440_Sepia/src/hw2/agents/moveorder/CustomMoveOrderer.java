package hw2.agents.moveorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hw2.chess.game.Board;
import hw2.chess.search.DFSTreeNode;

public class CustomMoveOrderer {

	/**
	 * TODO: implement me! This method should perform move ordering. Remember, move
	 * ordering is how alpha-beta pruning gets part of its power from. You want to
	 * see nodes which are beneficial FIRST so you can prune as much as possible
	 * during the search (i.e. be faster)
	 * 
	 * @param nodes. The nodes to order (these are children of a DFSTreeNode) that
	 *               we are about to consider in the search.
	 * @return The ordered nodes.
	 */

	static Map<Board, List<DFSTreeNode>> transpositionTable = new HashMap<>();

	public static List<DFSTreeNode> order(List<DFSTreeNode> nodes) {
		Board gameBoard = nodes.get(0).getGame().getBoard();

		if (transpositionTable.containsKey(gameBoard)) {
			return transpositionTable.get(gameBoard);
		}

		List<DFSTreeNode> captureNodes = new LinkedList<>();
		List<DFSTreeNode> otherNodes = new LinkedList<>();

		for (DFSTreeNode node : nodes) {
			if (node.getMove() != null) {
				switch (node.getMove().getType()) {
				case CAPTUREMOVE:
					captureNodes.add(node);
					break;
				default:
					otherNodes.add(node);
					break;
				}
			} else {
				otherNodes.add(node);
			}
		}

		// shuffle the non-capture nodes before combining them with capture nodes
		Collections.shuffle(otherNodes);

		captureNodes.addAll(otherNodes);
		List<DFSTreeNode> orderedNodes = captureNodes;

		// Add randomness to ordering by shuffling some nodes before others
		if (captureNodes.size() > 1) {
			int splitIndex = (int) Math.floor(Math.random() * (captureNodes.size() - 1)) + 1;
			List<DFSTreeNode> firstSplit = new ArrayList<>(captureNodes.subList(0, splitIndex));
			List<DFSTreeNode> secondSplit = new ArrayList<>(captureNodes.subList(splitIndex, captureNodes.size()));
			Collections.shuffle(firstSplit);
			Collections.shuffle(secondSplit);
			orderedNodes = new ArrayList<>(firstSplit);
			orderedNodes.addAll(secondSplit);
		}

		transpositionTable.put(gameBoard, orderedNodes);
		return orderedNodes;
	}

}
