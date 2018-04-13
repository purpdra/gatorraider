package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

import java.util.List;

public final class StudentAttackerController implements AttackerController
{
	public void init(Game game) {}

	public void shutdown(Game game) { }


	public boolean isDefenderClose(List<Defender> defenders, Node attackerLocation){
		for(int i = 0; i < defenders.size(); i++){
			if( attackerLocation.getPathDistance(defenders.get(i).getLocation())<5){
				return true;
			}
		}
		return false;
	}

	public Node closestVulnDefender(List<Defender> defenders, Node attackerLocation){//returns Node of closest vulnerable attacker
		Node temp = null;
		for(int i = 0; i < defenders.size(); i++){
			int distance = attackerLocation.getPathDistance(defenders.get(i).getLocation());
			if(defenders.get(i).isVulnerable()){
				if(temp == null && distance>-1) {
					temp = defenders.get(i).getLocation();
				}else
				if (distance < attackerLocation.getPathDistance(temp) && distance != -1) {
					temp = defenders.get(i).getLocation();
				}

			}
		}
		return temp;
	}

	public Defender nearestDefender(List<Defender> defenders, Node attackerLocation){//returns Node of closest vulnerable attacker
		Defender temp = null;
		for(int i = 0; i < defenders.size(); i++){
			int distance = attackerLocation.getPathDistance(defenders.get(i).getLocation());
			if(!defenders.get(i).isVulnerable()){
				if(temp == null && distance>-1) {
					temp = defenders.get(i);
				}else
				if (temp != null && distance < attackerLocation.getPathDistance(temp.getLocation()) && distance != -1) {
					temp = defenders.get(i);
				}

			}
		}
		return temp;
	}


	public boolean defenderVuln(List<Defender> defenders){
		for(int i = 0; i < defenders.size(); i++){
			if(defenders.get(i).isVulnerable()){
				return true;
			}
		}
		return false;
	}

	public int update(Game game,long timeDue)
	{
		int distanceToPowerPill = 0;
		List<Integer> possibleDirs = game.getAttacker().getPossibleDirs(true);
		List<Node> possibleLocations = game.getAttacker().getPossibleLocations(true);

		List<Node> Pills = game.getPillList();
		List<Node> powerPills= game.getPowerPillList();
		Node closestPill = game.getAttacker().getTargetNode(Pills,true);
		Node closestPower = game.getAttacker().getTargetNode(powerPills,true);

		Node attackerLocation = game.getAttacker().getLocation();
		List<Defender> defenders = game.getDefenders();

		int distanceToPill = attackerLocation.getPathDistance(closestPill);
		if(closestPower != null) {
			distanceToPowerPill = attackerLocation.getPathDistance(closestPower);
		}
		int PowerpillMove = -1;
		if(powerPills.size()>0) {
			PowerpillMove = game.getAttacker().getNextDir(closestPower, true);
		}
		int pillMove = game.getAttacker().getNextDir(closestPill,true);

		if(powerPills.size()>0 && defenderVuln(defenders)==false) {//if there are powerpills and no defenders are vulnerable
			if (isDefenderClose(defenders, attackerLocation)) {
				//if defender gets within 5 spaces of you, get the powerpill

				return PowerpillMove;
			}
			if (distanceToPowerPill > 3 && isDefenderClose(defenders,attackerLocation)) { //else if you are not near a power pill, go near one
				return PowerpillMove;
			} if(distanceToPowerPill<3 && !isDefenderClose(defenders,attackerLocation)){    //if you are close to a powerpill and no one is near you, hold tight.
				return game.getAttacker().getReverse();
			}
			else{
				return pillMove;
			}

		}
//When you wanna just get pills
		if(powerPills.size()==0 && !defenderVuln(defenders)){// no powerpills and no defenders are vulnerable
			//no defenders nearby, keep goin for pills
			return pillMove;

		}

		if(closestVulnDefender(defenders,attackerLocation)!=null){//if defenders are vulnerable
			int defenderMove = game.getAttacker().getNextDir(closestVulnDefender(defenders,attackerLocation),true);
			return defenderMove;//move towards defenders
		}
		return -1;

	}
}


