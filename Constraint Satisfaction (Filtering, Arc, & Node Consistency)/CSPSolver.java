package main.csp;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.LinkedList;

/**
 * CSP: Calendar Satisfaction Problem Solver Provides a solution for scheduling
 * some n meetings in a given period of time and according to some unary and
 * binary constraints on the dates of each meeting.
 */
public class CSPSolver {

	// Backtracking CSP Solver
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Public interface for the CSP solver in which the number of meetings, range of
	 * allowable dates for each meeting, and constraints on meeting times are
	 * specified.
	 * 
	 * @param nMeetings   The number of meetings that must be scheduled, indexed
	 *                    from 0 to n-1
	 * @param rangeStart  The start date (inclusive) of the domains of each of the n
	 *                    meeting-variables
	 * @param rangeEnd    The end date (inclusive) of the domains of each of the n
	 *                    meeting-variables
	 * @param constraints Date constraints on the meeting times (unary and binary
	 *                    for this assignment)
	 * @return A list of dates that satisfies each of the constraints for each of
	 *         the n meetings, indexed by the variable they satisfy, or null if no
	 *         solution exists.
	 */

	public static List<LocalDate> solve(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd,
			Set<DateConstraint> constraints) {
		ArrayList<LocalDate> assignments = new ArrayList<LocalDate>();
		ArrayList<MeetingDomain> meetingDomains = new ArrayList<MeetingDomain>(nMeetings);
		for (int i = 0; i < nMeetings; i++) {
			meetingDomains.add(new MeetingDomain(rangeStart, rangeEnd));
		}
		nodeConsistency(meetingDomains, constraints);
		arcConsistency(meetingDomains, constraints);
		return (backTrack(nMeetings, meetingDomains, constraints, assignments, 0));
	}

	private static List<LocalDate> backTrack(int nMeetings, List<MeetingDomain> meetingDomains,
			Set<DateConstraint> constraints, List<LocalDate> assignments, int index) {
		if (assignments.size() == nMeetings) {
			return assignments;
		}
		for (MeetingDomain meeting : meetingDomains) {
			if (meeting.domainValues.isEmpty()) {
				return null;
			}
		}

		MeetingDomain dates = meetingDomains.get(index);
		for (LocalDate date : dates.domainValues) {
			assignments.add(date);
			if (constraintCheck(constraints, assignments)) {
				List<LocalDate> results = backTrack(nMeetings, meetingDomains, constraints, assignments, index + 1);
				if (results != null) {
					return results;
				}
			}
			assignments.remove(assignments.size() - 1);
		}
		return null;
	}

	private static boolean constraintCheck(Set<DateConstraint> constraints, List<LocalDate> assignments) {
		int length = assignments.size() - 1;
		for (DateConstraint constraint : constraints) {
			if (constraint.arity() == 1 && length >= constraint.L_VAL) {
				LocalDate rightDate = ((UnaryDateConstraint) constraint).R_VAL;
				LocalDate leftDate = assignments.get(constraint.L_VAL);
				if (!constraint.isSatisfiedBy(leftDate, rightDate)) {
					return false;
				}
			}

			if (constraint.arity() == 2) {
				if (length >= ((BinaryDateConstraint) constraint).L_VAL
						&& length >= ((BinaryDateConstraint) constraint).R_VAL) {
					LocalDate rightDate = assignments.get(((BinaryDateConstraint) constraint).R_VAL);
					LocalDate leftDate = assignments.get(constraint.L_VAL);
					if (!constraint.isSatisfiedBy(leftDate, rightDate)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// Filtering Operations
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Enforces node consistency for all variables' domains given in varDomains
	 * based on the given constraints. Meetings' domains correspond to their index
	 * in the varDomains List.
	 * 
	 * @param varDomains  List of MeetingDomains in which index i corresponds to D_i
	 * @param constraints Set of DateConstraints specifying how the domains should
	 *                    be constrained. [!] Note, these may be either unary or
	 *                    binary constraints, but this method should only process
	 *                    the *unary* constraints!
	 */
	public static void nodeConsistency(List<MeetingDomain> varDomains, Set<DateConstraint> constraints) {
		for (DateConstraint constraint : constraints) {
			if (constraint.arity() == 2) {
				continue;
			}
			MeetingDomain newDomains = varDomains.get(constraint.L_VAL);
			// Set<LocalDate> theDomVals = updateDom.domainValues;
			Set<LocalDate> dates = new HashSet<>(varDomains.get(constraint.L_VAL).domainValues);
			for (LocalDate date : dates) {
				if (!constraint.isSatisfiedBy(date, ((UnaryDateConstraint) constraint).R_VAL)) {
					newDomains.domainValues.remove(date);
				}
			}
			varDomains.set(constraint.L_VAL, newDomains);
		}
	}

	/*
	 * Enforces arc consistency for all variables' domains given in varDomains based
	 * on the given constraints. Meetings' domains correspond to their index in the
	 * varDomains List.
	 * 
	 * @param varDomains List of MeetingDomains in which index i corresponds to D_i
	 * 
	 * @param constraints Set of DateConstraints specifying how the domains should
	 * be constrained. [!] Note, these may be either unary or binary constraints,
	 * but this method should only process the *binary* constraints using the AC-3
	 * algorithm!
	 */
	public static void arcConsistency(List<MeetingDomain> varDomains, Set<DateConstraint> constraints) {
		Set<Arc> arcs = new HashSet<Arc>();
		Set<Arc> newArcs = new HashSet<Arc>();
		for (DateConstraint constraint : constraints) {
			if (constraint.arity() == 1) {
				continue;
			}
			Arc inverseArc = new Arc(((BinaryDateConstraint) constraint).R_VAL, constraint.L_VAL,
					((BinaryDateConstraint) constraint).getReverse());
			arcs.add(new Arc(constraint.L_VAL, ((BinaryDateConstraint) constraint).R_VAL, constraint));
			arcs.add(inverseArc);
		}
		Set<Arc> allPossibleArcs = new HashSet<Arc>(arcs);
		while (!arcs.isEmpty()) {
			for (Arc polledArc : arcs) {
				if (Remove(varDomains, polledArc)) {
					for (Arc copiedArc : allPossibleArcs) {
						if (polledArc.TAIL == copiedArc.HEAD) {
							Arc neighbor = new Arc(copiedArc.TAIL, copiedArc.HEAD, copiedArc.CONSTRAINT);
							newArcs.add(neighbor);
						}
					}
				}
			}
			arcs = new HashSet<Arc>(newArcs);
			newArcs.clear();
		}
	}

	private static boolean Remove(List<MeetingDomain> varDomains, Arc removedArc) {
		boolean removed = false;
		MeetingDomain domainT = varDomains.get(removedArc.TAIL);
		MeetingDomain domainH = varDomains.get(removedArc.HEAD);
		Set<LocalDate> valuesT = new HashSet<>(domainT.domainValues);
		Set<LocalDate> valuesH = new HashSet<>(domainH.domainValues);
		for (LocalDate dateT : valuesT) {
			boolean containVal = false;
			for (LocalDate dateH : valuesH) {
				if (removedArc.CONSTRAINT.isSatisfiedBy(dateT, dateH)) {
					containVal = true;
				}
			}
			if (!containVal) {
				domainT.domainValues.remove(dateT);
				removed = true;
			}
		}
		varDomains.set(removedArc.TAIL, domainT);
		return removed;
	}

	/**
	 * Private helper class organizing Arcs as defined by the AC-3 algorithm, useful
	 * for implementing the arcConsistency method. [!] You may modify this class
	 * however you'd like, its basis is just a suggestion that will indeed work.
	 */
	private static class Arc {

		public final DateConstraint CONSTRAINT;
		public final int TAIL, HEAD;

		/**
		 * Constructs a new Arc (tail -> head) where head and tail are the meeting
		 * indexes corresponding with Meeting variables and their associated domains.
		 * 
		 * @param tail Meeting index of the tail
		 * @param head Meeting index of the head
		 * @param c    Constraint represented by this Arc. [!] WARNING: A
		 *             DateConstraint's isSatisfiedBy method is parameterized as:
		 *             isSatisfiedBy (LocalDate leftDate, LocalDate rightDate), meaning
		 *             L_VAL for the first parameter and R_VAL for the second. Be
		 *             careful with this when creating Arcs that reverse direction. You
		 *             may find the BinaryDateConstraint's getReverse method useful
		 *             here.
		 */
		public Arc(int tail, int head, DateConstraint c) {
			this.TAIL = tail;
			this.HEAD = head;
			this.CONSTRAINT = c;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (this.getClass() != other.getClass()) {
				return false;
			}
			Arc otherArc = (Arc) other;
			return this.TAIL == otherArc.TAIL && this.HEAD == otherArc.HEAD
					&& this.CONSTRAINT.equals(otherArc.CONSTRAINT);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.TAIL, this.HEAD, this.CONSTRAINT);
		}

		@Override
		public String toString() {
			return "(" + this.TAIL + " -> " + this.HEAD + ")";
		}

	}

}
