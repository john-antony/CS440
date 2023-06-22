package hw2.agents.heuristics;

import java.util.Set;

import edu.cwru.sepia.util.Direction;
import hw2.chess.game.Board;
import hw2.chess.game.move.PromotePawnMove;
import hw2.chess.game.piece.Piece;
import hw2.chess.game.piece.PieceType;
import hw2.chess.game.player.Player;
import hw2.chess.search.DFSTreeNode;
import hw2.chess.utils.Coordinate;

public class CustomHeuristics {

	/**
	 * TODO: implement me! The heuristics that I wrote are useful, but not very good
	 * for a good chessbot. Please use this class to add your heuristics here! I
	 * recommend taking a look at the ones I provided for you in
	 * CustomHeuristics.java (which is in the same directory as this file)
	 */

	public static Player getMaxPlayer(DFSTreeNode node) {
		return node.getMaxPlayer();
	}

	/**
	 * Get the min player from a node
	 * 
	 * @param node
	 * @return
	 */
	public static Player getMinPlayer(DFSTreeNode node) {
		return CustomHeuristics.getMaxPlayer(node).equals(node.getGame().getCurrentPlayer())
				? node.getGame().getOtherPlayer()
				: node.getGame().getCurrentPlayer();
	}

	public static class PieceFormationHeuristics extends Object {
		// formation scores based on number of adjacent allies
		private static final int[] FORMATION_SCORES = { 0, 10, 30, 50, 70, 90, 100 };

		// Returns formation score for a given node
		public static int getFormationScore(DFSTreeNode node) {
			int totalFormationScore = 0;
			// Get the current board state
			Board board = node.getGame().getBoard();

			// Iterate over all the piece types
			for (PieceType type : PieceType.values()) {
				int playerFormationScore = getPlayerFormationScore(board, node.getGame().getCurrentPlayer(), type);
				int opponentFormationScore = getPlayerFormationScore(board, node.getGame().getOtherPlayer(), type);
				// Add difference in formation score to total
				totalFormationScore += (playerFormationScore - opponentFormationScore);
			}
			// Return total formation score
			return totalFormationScore;
		}

		// Returns formation score for a player's pieces of a given type
		private static int getPlayerFormationScore(Board board, Player player, PieceType type) {
			int playerFormationScore = 0;
			Set<Piece> playerPieces = board.getPieces(player, type);
			// Iterate over each of the player's pieces
			for (Piece piece : playerPieces) {
				// Get the number of adjacent allies for the current piece
				int numAdjacentAllies = countAdjacentAllies(board, playerPieces, piece);
				// Add formation score based on the number of adjacent allies to
				// players formation score
				playerFormationScore += FORMATION_SCORES[numAdjacentAllies];
			}
			// Return the players formation score for this type
			return playerFormationScore;
		}

		// Counts number of adjacent allies for given piece
		private static int countAdjacentAllies(Board board, Set<Piece> allies, Piece piece) {
			int numAdjacentAllies = 0;
			// Get coordinate of current piece
			Coordinate pieceCoord = board.getPiecePosition(piece);
			// loop through directions
			for (Direction direction : Direction.values()) {
				Coordinate neighborposition = pieceCoord.getNeighbor(direction);
				Piece neighborPiece = board.getPieceAtPosition(neighborposition);
				// If the neighbor is an ally and of the same type as the current piece,
				// increment
				if (neighborPiece != null && neighborPiece.getPlayer().equals(piece.getPlayer())
						&& allies.contains(neighborPiece)) {
					numAdjacentAllies++;
				}
			}
			// Return the number of adjacent allies
			return numAdjacentAllies;
		}
	}

	public static class OffensiveHeuristics extends Object {
		public static final int THREATENING_WEIGHT = 1;

		public static int getNumberOfPiecesMaxPlayerIsThreatening(DFSTreeNode node) {
			int numPiecesMaxPlayerIsThreatening = 0;
			for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node))) {
				numPiecesMaxPlayerIsThreatening += piece.getAllCaptureMoves(node.getGame()).size();
			}
			return numPiecesMaxPlayerIsThreatening * THREATENING_WEIGHT;
		}
	}

	public static class DefensiveHeuristics extends Object {
		public static final int MAX_ALIVE_WEIGHT = 3;
		public static final int MIN_ALIVE_WEIGHT = 2;
		public static final int KING_SURROUNDING_WEIGHT = 4;
		public static final int THREATENED_WEIGHT = -1;

		public static int getNumberOfMaxPlayersAlivePieces(DFSTreeNode node) {
			int numMaxPlayersPiecesAlive = 0;
			for (PieceType pieceType : PieceType.values()) {
				numMaxPlayersPiecesAlive += node.getGame().getNumberOfAlivePieces(CustomHeuristics.getMaxPlayer(node),
						pieceType);
			}
			return numMaxPlayersPiecesAlive * MAX_ALIVE_WEIGHT;
		}

		public static int getNumberOfMinPlayersAlivePieces(DFSTreeNode node) {
			int numMaxPlayersPiecesAlive = 0;
			for (PieceType pieceType : PieceType.values()) {
				numMaxPlayersPiecesAlive += node.getGame().getNumberOfAlivePieces(CustomHeuristics.getMinPlayer(node),
						pieceType);
			}
			return numMaxPlayersPiecesAlive * MIN_ALIVE_WEIGHT;
		}

		public static int getClampedPieceValueTotalSurroundingMaxPlayersKing(DFSTreeNode node) {
			// what is the state of the pieces next to the king? add up the values of the
			// neighboring pieces
			// positive value for friendly pieces and negative value for enemy pieces (will
			// clamp at 0)
			int maxPlayerKingSurroundingPiecesValueTotal = 0;

			Piece kingPiece = node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.KING)
					.iterator().next();
			Coordinate kingPosition = node.getGame().getCurrentPosition(kingPiece);
			for (Direction direction : Direction.values()) {
				Coordinate neightborPosition = kingPosition.getNeighbor(direction);
				if (node.getGame().getBoard().isInbounds(neightborPosition)
						&& node.getGame().getBoard().isPositionOccupied(neightborPosition)) {
					Piece piece = node.getGame().getBoard().getPieceAtPosition(neightborPosition);
					int pieceValue = Piece.getPointValue(piece.getType());
					if (piece != null && kingPiece.isEnemyPiece(piece)) {
						maxPlayerKingSurroundingPiecesValueTotal -= pieceValue;
					} else if (piece != null && !kingPiece.isEnemyPiece(piece)) {
						maxPlayerKingSurroundingPiecesValueTotal += pieceValue;
					}
				}
			}
			// kingSurroundingPiecesValueTotal cannot be < 0 b/c the utility of losing a
			// game is 0, so all of our utility values should be at least 0
			maxPlayerKingSurroundingPiecesValueTotal = Math.max(maxPlayerKingSurroundingPiecesValueTotal, 0);
			return maxPlayerKingSurroundingPiecesValueTotal * KING_SURROUNDING_WEIGHT;
		}

		public static int getNumberOfPiecesThreateningMaxPlayer(DFSTreeNode node) {
			// how many pieces are threatening us?
			int numPiecesThreateningMaxPlayer = 0;
			for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMinPlayer(node))) {
				numPiecesThreateningMaxPlayer += piece.getAllCaptureMoves(node.getGame()).size();
			}
			return numPiecesThreateningMaxPlayer * THREATENED_WEIGHT;
		}
	}

	public static double getOffensiveMaxPlayerHeuristicValue(DFSTreeNode node) {
		// remember the action has already taken affect at this point, so capture moves
		// have already resolved
		// and the targeted piece will not exist inside the game anymore.
		// however this value was recorded in the amount of points that the player has
		// earned in this node
		double damageDealtInThisNode = node.getGame().getBoard().getPointsEarned(CustomHeuristics.getMaxPlayer(node));

		switch (node.getMove().getType()) {
		case PROMOTEPAWNMOVE:
			PromotePawnMove promoteMove = (PromotePawnMove) node.getMove();
			damageDealtInThisNode += Piece.getPointValue(promoteMove.getPromotedPieceType());
			break;
		default:
			break;
		}
		// offense can typically include the number of pieces that our pieces are
		// currently threatening
		int numPiecesWeAreThreatening = OffensiveHeuristics.getNumberOfPiecesMaxPlayerIsThreatening(node);

		return damageDealtInThisNode + numPiecesWeAreThreatening;
	}

	public static double getDefensiveMaxPlayerHeuristicValue(DFSTreeNode node) {
		// how many pieces exist on our team?
		int numPiecesAlive = DefensiveHeuristics.getNumberOfMaxPlayersAlivePieces(node);

		// what is the state of the pieces next to the king? add up the values of the
		// neighboring pieces
		// positive value for friendly pieces and negative value for enemy pieces (will
		// clamp at 0)
		int kingSurroundingPiecesValueTotal = DefensiveHeuristics
				.getClampedPieceValueTotalSurroundingMaxPlayersKing(node);

		// how many pieces are threatening us?
		int numPiecesThreateningUs = DefensiveHeuristics.getNumberOfPiecesThreateningMaxPlayer(node);

		return numPiecesAlive + kingSurroundingPiecesValueTotal + numPiecesThreateningUs;
	}

	public static double getNonlinearPieceCombinationMaxPlayerHeuristicValue(DFSTreeNode node) {
		// both bishops are worth more together than a single bishop alone
		// same with knights...we want to encourage keeping pairs of elements
		double multiPieceValueTotal = 0.0;

		double exponent = 1.5; // f(numberOfKnights) = (numberOfKnights)^exponent

		// go over all the piece types that have more than one copy in the game
		// (including pawn promotion)
		for (PieceType pieceType : new PieceType[] { PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK,
				PieceType.QUEEN }) {
			multiPieceValueTotal += Math.pow(
					node.getGame().getNumberOfAlivePieces(CustomHeuristics.getMaxPlayer(node), pieceType), exponent);
		}

		return multiPieceValueTotal;
	}

	public static double getMaxPlayerHeuristicValue(DFSTreeNode node) {
		double offenseHeuristicValue = CustomHeuristics.getOffensiveMaxPlayerHeuristicValue(node);
		double defenseHeuristicValue = CustomHeuristics.getDefensiveMaxPlayerHeuristicValue(node);
		double nonlinearHeuristicValue = CustomHeuristics.getNonlinearPieceCombinationMaxPlayerHeuristicValue(node);
		double formationHeuristicValue = PieceFormationHeuristics.getFormationScore(node);

		return offenseHeuristicValue + defenseHeuristicValue + nonlinearHeuristicValue + formationHeuristicValue;
	}

	public static double getHeuristicValue(DFSTreeNode node) {
		// please replace this!
		return CustomHeuristics.getMaxPlayerHeuristicValue(node);
	}

}
