/* BaseballElimination.java
   CSC 226 - Summer 2018
   Assignment 4 - Baseball Elimination Program
   
   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java BaseballElimination
	
   To conveniently test the algorithm with a large input, create a text file
   containing one or more test divisions (in the format described below) and run
   the program with
	java -cp .;algs4.jar BaseballElimination file.txt (Windows)
   or
    java -cp .:algs4.jar BaseballElimination file.txt (Linux or Mac)
   where file.txt is replaced by the name of the text file.
   
   The input consists of an integer representing the number of teams in the division and then
   for each team, the team name (no whitespace), number of wins, number of losses, and a list
   of integers represnting the number of games remaining against each team (in order from the first
   team to the last). That is, the text file looks like:
   
	<number of teams in division>
	<team1_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...
	<teamn_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>

	
   An input file can contain an unlimited number of divisions but all team names are unique, i.e.
   no team can be in more than one division.


   R. Little - 07/13/2018


	compile with
 
		javac -cp ;algs4.jar BaseballElimination.java
		java -cp ;algs4.jar BaseballElimination
*/

import edu.princeton.cs.algs4.*;
import java.util.*;
import java.io.File;

//Do not change the name of the BaseballElimination class
public class BaseballElimination{
	
	// We use an ArrayList to keep track of the eliminated teams.
	public ArrayList<String> eliminated = new ArrayList<String>();

	/* BaseballElimination(s)
		Given an input stream connected to a collection of baseball division
		standings we determine for each division which teams have been eliminated 
		from the playoffs. For each team in each division we create a flow network
		and determine the maxflow in that network. If the maxflow exceeds the number
		of inter-divisional games between all other teams in the division, the current
		team is eliminated.
	*/
	public BaseballElimination(Scanner s){
		int numTeams = s.nextInt();
		
		/* ... Your code here ... */	
		//teams
		String[] teams = new String[numTeams];
		//current wins indexed in team order
		int[] w = new int[numTeams];
		//remaining games indexed in team order
		int[] r = new int[numTeams];
		//matrix for remaining games with each team
		int[][] l = new int[numTeams][numTeams];
		//current maximum number of wins
		int cw = 0;
		//total number of L nodes
		int totalLNodes=0;
		//number of L nodes to be excluded based off of team indexes
		int[] lNodes = new int[numTeams];
		//loop to collect data from scanner
		for(int i=0; i<numTeams; i++){
			teams[i] = s.next();
			w[i] = s.nextInt();
			if(w[i]>cw){cw=w[i];}
			r[i] = s.nextInt();
			for(int k=0; k<numTeams; k++){
				l[k][i] = s.nextInt();
				if(l[k][i]!=0){
					lNodes[i]++;
					totalLNodes++;
				}
			}
		}


		///////////////////////////////////////////////////////
						//	data print test   //
		System.out.println("teams :");
		for(int i=0; i<numTeams; i++){
			System.out.print(teams[i]+",   ");
		}
		System.out.println();
		System.out.println();
		System.out.println("w array:");
		for(int i=0; i<numTeams; i++){
			System.out.print(w[i]+",   ");
		}
		System.out.println();
		System.out.println();
		System.out.println("r array:");
		for(int i=0; i<numTeams; i++){
			System.out.print(r[i]+",   ");
		}
		System.out.println();
		System.out.println();
		System.out.println("l array:");
		for(int i=0; i<numTeams; i++){
			for(int k=0; k<numTeams; k++){
				System.out.print(l[k][i]+"  ");
			}
		System.out.println();
		}
		//////////////////////////////////////////////////////

		//main loop for elimination testing
		for(int i=0; i<numTeams; i++){
			if(w[i]+r[i]>=cw){
				int W = w[i] + r[i];


				int curNumNodes = 2+numTeams+totalLNodes-lNodes[i];
				FlowNetwork G = new FlowNetwork(curNumNodes);
				System.out.println();
				System.out.println("the flownet for team "+teams[i]+" has "+curNumNodes+" nodes");
				System.out.println();


				//verteces for teams are labled 0, 1,...., numTeams-1
				for(int k=0; k<numTeams; k++){
					if(k!=i){
						//sink = curnumnodes-1
						G.addEdge(new FlowEdge(k, curNumNodes-1,  W-w[k]));
					}

				}
				int games=0;
				int curGameNode = numTeams;
				//voerteces for eatch team match up are labled numTeams,.....curNumNodes-3
				for(int x=0; x<numTeams; x++){
					if(x!=i){
						for(int y=0; y<x; y++){
							if(y!=i && l[x][y]!=0){
								G.addEdge(new FlowEdge(curGameNode, x, Double.POSITIVE_INFINITY));
								G.addEdge(new FlowEdge(curGameNode, y, Double.POSITIVE_INFINITY));
								//source = curnumnodes-2
								G.addEdge(new FlowEdge(curNumNodes-2, curGameNode, l[x][y]));
								curGameNode++;
								games = games+l[x][y];
							}
						}
					}
				}

				FordFulkerson F = new FordFulkerson(G, curNumNodes-2, curNumNodes-1);


				System.out.println(G.toString());

				System.out.println("max flow = "+F.value());
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();
				if(F.value()<games){
					eliminated.add(teams[i]);
				}

			}else{
				eliminated.add(teams[i]);
				System.out.println();
			}
		}
		


	}
		
	/* main()
	   Contains code to test the BaseballElimantion function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below.
	*/
	public static void main(String[] args){
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}
		
		BaseballElimination be = new BaseballElimination(s);		
		
		if (be.eliminated.size() == 0)
			System.out.println("No teams have been eliminated.");
		else
			System.out.println("Teams eliminated: " + be.eliminated);
	}
}
