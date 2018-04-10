/*
 *  KEVIN THO HOANG (76963024)
 *  CS 171: INTRODUCTION TO ARTIFICIAL INTELLIGENCE
 *  PROJECT #1: FC, LCV, MRV
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.PriorityQueue;

public class BTSolver
{

	// =================================================================
	// Properties
	// =================================================================

	private ConstraintNetwork network;
	private SudokuBoard sudokuGrid;
	private Trail trail;

	private boolean hasSolution = false;

	public String varHeuristics;
	public String valHeuristics;
	public String cChecks;

	// =================================================================
	// Constructors
	// =================================================================

	public BTSolver ( SudokuBoard sboard, Trail trail, String val_sh, String var_sh, String cc )
	{
		this.network    = new ConstraintNetwork( sboard );
		this.sudokuGrid = sboard;
		this.trail      = trail;

		varHeuristics = var_sh;
		valHeuristics = val_sh;
		cChecks       = cc;
	}

	// =================================================================
	// Consistency Checks
	// =================================================================

	// Basic consistency check, no propagation done
	private boolean assignmentsCheck ( )
	{
		for ( Constraint c : network.getConstraints() )
			if ( ! c.isConsistent() )
				return false;

		return true;
	}

	/**
	 * Part 1 TODO: Implement the Forward Checking Heuristic
	 *
	 * This function will do both Constraint Propagation and check
	 * the consistency of the network
	 *
	 * (1) If a variable is assigned then eliminate that value from
	 *     the square's neighbors.
	 *
	 * Note: remember to trail.push variables before you change their domain
	1 * Return: true is assignment is consistent, false otherwise
	 */
	private boolean forwardChecking ( )
	{
            // Iterate through all modified constraints to see if they remained
            // consistent. Return false if any constraints are inconsistent and 
            // backtrack.
            for (Constraint c: network.getModifiedConstraints())
                if (!c.isConsistent())
                    return false;

            for (Variable v: getAssignedVariables())
            {
                for (Variable neighbor: network.getNeighborsOfVariable(v))
                {
                    // Having the same assigned value as one of its neighbors is
                    // inconsistent with the constraints. Thus, return false and
                    // backtrack.
                    if (neighbor.getAssignment() == v.getAssignment())
                        return false;
                    
                    // If the variable's neighbor's domain contains its assigned
                    // value, eliminate the value from its neighbor's domain.
                    if (neighbor.getDomain().contains(v.getAssignment()))
                    {
                        trail.push(neighbor);
                        neighbor.removeValueFromDomain(v.getAssignment());
                    }
                }
            }
            
            return true;
	}

	/**
	 * Part 2 TODO: Implement both of Norvig's Heuristics
	 *
	 * This function will do both Constraint Propagation and check
	 * the consistency of the network
	 *
	 * (1) If a variable is assigned then eliminate that value from
	 *     the square's neighbors.
	 *
	 * (2) If a constraint has only one possible place for a value
	 *     then put the value there.
	 *
	 * Note: remember to trail.push variables before you change their domain
	 * Return: true is assignment is consistent, false otherwise
	 */
	private boolean norvigCheck ( )
	{
            /*
             * (1) If a variable is assigned then eliminate that value from
             *     the square's neighbors.
             */

            // Iterate through all modified constraints to see if they remained
            // consistent. Return false if any constraints are inconsistent and 
            // backtrack.
            for (Constraint c: network.getModifiedConstraints())
                if (!c.isConsistent())
                    return false;

            for (Variable v: getAssignedVariables())
            {
                for (Variable neighbor: network.getNeighborsOfVariable(v))
                {
                    // Having the same assigned value as one of its neighbors is
                    // inconsistent with the constraints. Thus, return false and
                    // backtrack.
                    if (neighbor.getAssignment() == v.getAssignment())
                        return false;
                    
                    // If the variable's neighbor's domain contains its assigned
                    // value, eliminate the value from its neighbor's domain.
                    if (neighbor.getDomain().contains(v.getAssignment()))
                    {
                        trail.push(neighbor);
                        neighbor.removeValueFromDomain(v.getAssignment());
                    }
                }
            }
            
            /*
             * (2) If a constraint has only one possible place for a value
             *     then put the value there.
             */

            int[] counter = new int[sudokuGrid.getN()];
            
            for (Variable v: network.getVariables())
            {
                // Reset counter array 
                for (int i = 0; i < counter.length; ++i)
                    counter[i] = 0;
                
                // Count values in v's domain
                for (Integer value: v.getDomain())
                    ++counter[value - 1];
                
                // Count values in neighbor's domain
                for (Variable neighbor: network.getNeighborsOfVariable(v))
                    for (Integer value: neighbor.getDomain())
                        ++counter[value - 1];
                
                // Based on counter array, assign v a value if all neighbors already
                // have that value assigned to them
                for (int i = 0; i < counter.length; ++i)
                {
                    if (counter[i] == 1)
                    {
                        trail.push(v);
                        v.assignValue(i + 1);
                        break;
                    }
                }
            }

            return true;
	}

	/**
	 * Optional TODO: Implement your own advanced Constraint Propagation
	 *
	 * Completing the three tourn heuristic will automatically enter
	 * your program into a tournament.
	 */
	private boolean getTournCC ( )
	{
            /*
             * (1) If a variable is assigned then eliminate that value from
             *     the square's neighbors.
             */

            // Iterate through all modified constraints to see if they remained
            // consistent. Return false if any constraints are inconsistent and 
            // backtrack.
            for (Constraint c: network.getModifiedConstraints())
                if (!c.isConsistent())
                    return false;

            for (Variable v: getAssignedVariables())
            {
                for (Variable neighbor: network.getNeighborsOfVariable(v))
                {
                    // Having the same assigned value as one of its neighbors is
                    // inconsistent with the constraints. Thus, return false and
                    // backtrack.
                    if (neighbor.getAssignment() == v.getAssignment())
                        return false;
                    
                    // If the variable's neighbor's domain contains its assigned
                    // value, eliminate the value from its neighbor's domain.
                    if (neighbor.getDomain().contains(v.getAssignment()))
                    {
                        trail.push(neighbor);
                        neighbor.removeValueFromDomain(v.getAssignment());
                    }
                }
            }
            
            /*
             * (2) If a constraint has only one possible place for a value
             *     then put the value there.
             */

            int[] counter = new int[sudokuGrid.getN()];
            
            for (Variable v: network.getVariables())
            {
                // Reset counter array 
                for (int i = 0; i < counter.length; ++i)
                    counter[i] = 0;
                
                // Count values in v's domain
                for (Integer value: v.getDomain())
                    ++counter[value - 1];
                
                // Count values in neighbor's domain
                for (Variable neighbor: network.getNeighborsOfVariable(v))
                    for (Integer value: neighbor.getDomain())
                        ++counter[value - 1];
                
                // Based on counter array, assign v a value if all neighbors already
                // have that value assigned to them
                for (int i = 0; i < counter.length; ++i)
                {
                    if (counter[i] == 1)
                    {
                        trail.push(v);
                        v.assignValue(i + 1);
                        break;
                    }
                }
            }

            return true;
	}

	// =================================================================
	// Variable Selectors
	// =================================================================

	// Basic variable selector, returns first unassigned variable
	private Variable getfirstUnassignedVariable()
	{
		for ( Variable v : network.getVariables() )
			if ( ! v.isAssigned() )
				return v;

		// Everything is assigned
		return null;
	}
        
	/**
	 * Part 1 TODO: Implement the Minimum Remaining Value Heuristic
	 *
	 * Return: The unassigned variable with the smallest domain
	 */
	private Variable getMRV ( )
	{
            int      smallestDomainSize       = getSmallestDomainSize();
            Variable minimumRemainingVariable = null;
            
            // Search through all unassigned variables to find the one with
            // the smallest domain size.
            for (Variable v: getUnassignedVariables())
                if (v.size() == smallestDomainSize)
                    minimumRemainingVariable = v;

            return minimumRemainingVariable;
	}

	/**
	 * Part 2 TODO: Implement the Degree Heuristic
	 *
	 * Return: The unassigned variable with the most unassigned neighbors
	 */
	private Variable getDegree ( )
	{
            int      highestDegree         = getHighestDegree(getUnassignedVariables());
            Variable highestDegreeVariable = null; 
            
            // Search through all unassigned variables for the one with
            // the most unassigned neighbors.
            for (Variable v: getUnassignedVariables())
                if (getDegreeCount(v) == highestDegree)
                    highestDegreeVariable = v;

            return highestDegreeVariable;
	}

	/**
	 * Part 2 TODO: Implement the Minimum Remaining Value Heuristic
	 *                with Degree Heuristic as a Tie Breaker
	 *
	 * Return: The unassigned variable with, first, the smallest domain
	 *         and, second, the most unassigned neighbors
	 */
	private Variable MRVwithTieBreaker ( )
	{
            ArrayList<Variable> mrvValues          = new ArrayList<Variable>();
            int                 highestDegree      = 0;
            int                 smallestDomainSize = getSmallestDomainSize();
            Variable            result             = null;
            
            // Iterate through all unassigned variables and collect the ones 
            // with the smallest domain size.
            for (Variable v: getUnassignedVariables())
                if (v.size() == smallestDomainSize)
                    mrvValues.add(v);
            
            highestDegree = getHighestDegree(mrvValues);

            // Search through all variables with the smallest domain size
            // for the one with the most unassigned neighbors.
            for (Variable v: mrvValues)
                if (getDegreeCount(v) == highestDegree)
                    result = v;

            return result;
        }

	/**
	 * Optional TODO: Implement your own advanced Variable Heuristic
	 *
	 * Completing the three tourn heuristic will automatically enter
	 * your program into a tournament.
	 */
	private Variable getTournVar ( )
	{
            int      smallestDomainSize       = getSmallestDomainSize();
            Variable minimumRemainingVariable = null;
            
            // Search through all unassigned variables to find the one with
            // the smallest domain size.
            for (Variable v: getUnassignedVariables())
                if (v.size() == smallestDomainSize)
                    minimumRemainingVariable = v;

            return minimumRemainingVariable;
	}

	// =================================================================
	// Value Selectors
	// =================================================================

	// Default Value Ordering
	public List<Integer> getValuesInOrder ( Variable v )
	{
		List<Integer> values = v.getDomain().getValues();

		Comparator<Integer> valueComparator = new Comparator<Integer>(){

			@Override
			public int compare(Integer i1, Integer i2) {
				return i1.compareTo(i2);
			}
		};
		Collections.sort(values, valueComparator);
		return values;
	}

	/**
	 * Part 1 TODO: Implement the Least Constraining Value Heuristic
	 *
	 * The Least constraining value is the one that will knock the least
	 * values out of it's neighbors domain.
	 *
	 * Return: A list of v's domain sorted by the LCV heuristic
	 *         The LCV is first and the MCV is last
	 */
	public List<Integer> getValuesLCVOrder ( Variable v )
	{
            ArrayList<Integer> valuesLCVOrder    = new ArrayList<Integer>();
            Queue<Integer>     sortedOccurrences = new PriorityQueue<Integer>(v.size());
            int[]              occurrences       = new int[v.size()];
            int                index             = 0;
            
            // Initialize values in v's domain with 0 occurrences bc we haven't
            // explored any of v's neighbors yet for their occurrences.
            for (int i = 0; i < v.size(); ++i)
                occurrences[i] = 0;
            
            for (Integer value: v.getDomain())
            {
                // Iterate through v's neighbors to see if value exists in its neighbor's
                // domain.
                for (Variable neighbor: network.getNeighborsOfVariable(v))
                    if (neighbor.getDomain().contains(value))
                        occurrences[index] += 1;
                
                // Only add occurrence to sortedOccurrence if it hasn't been added
                // already. We don't want duplicates in sortedOccurrences because we
                // only need to see the occurrence once later when we build valuesLCVOrder.
                if (!sortedOccurrences.contains(occurrences[index]))
                    sortedOccurrences.add(occurrences[index]);

                ++index;
            }
            
            // Iterate through sortedOccurrences to build valuesLCVOrder. Having the least
            // amount of occurrences indicates that the value is the least constraining. 
            // Having the most amount of occurrences indicate that the value is the most
            // constraining.
            for (Integer occurrence: sortedOccurrences)
                for (int i = 0; i < v.size(); ++i)
                    if (occurrences[i] == occurrence)
                        valuesLCVOrder.add(v.getDomain().getValues().get(i));
        
            return valuesLCVOrder;
        }

	/**
	 * Optional TODO: Implement your own advanced Value Heuristic
	 *
	 * Completing the three tourn heuristic will automatically enter
	 * your program into a tournament.
	 */
	public List<Integer> getTournVal ( Variable v )
	{
            ArrayList<Integer> valuesLCVOrder    = new ArrayList<Integer>();
            Queue<Integer>     sortedOccurrences = new PriorityQueue<Integer>(v.size());
            int[]              occurrences       = new int[v.size()];
            int                index             = 0;
            
            // Initialize values in v's domain with 0 occurrences bc we haven't
            // explored any of v's neighbors yet for their occurrences.
            for (int i = 0; i < v.size(); ++i)
                occurrences[i] = 0;
            
            for (Integer value: v.getDomain())
            {
                // Iterate through v's neighbors to see if value exists in its neighbor's
                // domain.
                for (Variable neighbor: network.getNeighborsOfVariable(v))
                    if (neighbor.getDomain().contains(value))
                        occurrences[index] += 1;
                
                // Only add occurrence to sortedOccurrence if it hasn't been added
                // already. We don't want duplicates in sortedOccurrences because we
                // only need to see the occurrence once later when we build valuesLCVOrder.
                if (!sortedOccurrences.contains(occurrences[index]))
                    sortedOccurrences.add(occurrences[index]);

                ++index;
            }
            
            // Iterate through sortedOccurrences to build valuesLCVOrder. Having the least
            // amount of occurrences indicates that the value is the least constraining. 
            // Having the most amount of occurrences indicate that the value is the most
            // constraining.
            for (Integer occurrence: sortedOccurrences)
                for (int i = 0; i < v.size(); ++i)
                    if (occurrences[i] == occurrence)
                        valuesLCVOrder.add(v.getDomain().getValues().get(i));
        
            return valuesLCVOrder;
	}
	
        //==================================================================
	// Helper functions for consistency, value, and variable checks
	//==================================================================
        
        // Returns a list of all assigned variables
        public List<Variable> getAssignedVariables()
        {
            ArrayList<Variable> assignedVariables = new ArrayList<Variable>();

            for (Variable v: network.getVariables())
                if (v.isAssigned())
                    assignedVariables.add(v);

            return assignedVariables;
        }
        
        // Returns a list of all unassigned variables
        public List<Variable> getUnassignedVariables()
        {
            ArrayList<Variable> unassignedVariables = new ArrayList<Variable>();

            for (Variable v: network.getVariables())
                if (!v.isAssigned())
                    unassignedVariables.add(v);

            return unassignedVariables;
        }

        // Returns the amount of unassigned variables for a given variable
        public int getDegreeCount(Variable v)
        {
            int degreeCount = 0;

            for (Variable neighbor: network.getNeighborsOfVariable(v))
                if (!neighbor.isAssigned())
                    ++degreeCount;

            return degreeCount;
        }

        // Returns the smallest domain size between all unassigned variables
        public int getSmallestDomainSize()
        {
            int smallestDomainSize = sudokuGrid.getN();     // Smallest domain size starts
                                                            // off as N, the largest possible
                                                            // domain size.
            for (Variable v: getUnassignedVariables())
                if (v.size() < smallestDomainSize)
                    smallestDomainSize = v.size();

            return smallestDomainSize;
        }
        
        // Returns the highest degree between all of the given variables
        public int getHighestDegree(List<Variable> variables)
        {
            int highestDegree = 0;

            for (Variable v: variables)
                if (getDegreeCount(v) > highestDegree)
                    highestDegree = getDegreeCount(v);

            return highestDegree;
        }

	//==================================================================
	// Engine Functions
	//==================================================================

	public void solve ( )
	{
		if ( hasSolution )
			return;

		// Variable Selection
		Variable v = selectNextVariable();

		if ( v == null )
		{
			for ( Variable var : network.getVariables() )
			{
				// If all variables haven't been assigned
				if ( ! var.isAssigned() )
				{
					System.out.println( "Error" );
					return;
				}
			}

			// Success
			hasSolution = true;
			return;
		}

		// Attempt to assign a value
		for ( Integer i : getNextValues( v ) )
		{
			// Store place in trail and push variable's state on trail
			trail.placeTrailMarker();
			trail.push( v );

			// Assign the value
			v.assignValue( i );

			// Propagate constraints, check consistency, recurse
			if ( checkConsistency() )
				solve();

			// If this assignment succeeded, return
			if ( hasSolution )
				return;

			// Otherwise backtrack
			trail.undo();
		}
	}

	private boolean checkConsistency ( )
	{
		switch ( cChecks )
		{
			case "forwardChecking":
				return forwardChecking();

			case "norvigCheck":
				return norvigCheck();

			case "tournCC":
				return getTournCC();

			default:
				return assignmentsCheck();
		}
	}

	private Variable selectNextVariable ( )
	{
		switch ( varHeuristics )
		{
			case "MinimumRemainingValue":
				return getMRV();

			case "Degree":
				return getDegree();

			case "MRVwithTieBreaker":
				return MRVwithTieBreaker();

			case "tournVar":
				return getTournVar();

			default:
				return getfirstUnassignedVariable();
		}
	}

	public List<Integer> getNextValues ( Variable v )
	{
		switch ( valHeuristics )
		{
			case "LeastConstrainingValue":
				return getValuesLCVOrder( v );

			case "tournVal":
				return getTournVal( v );

			default:
				return getValuesInOrder( v );
		}
	}

	public boolean hasSolution ( )
	{
		return hasSolution;
	}

	public SudokuBoard getSolution ( )
	{
		return network.toSudokuBoard ( sudokuGrid.getP(), sudokuGrid.getQ() );
	}

	public ConstraintNetwork getNetwork ( )
	{
		return network;
	}
}
