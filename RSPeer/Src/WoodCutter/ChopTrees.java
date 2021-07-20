//BankTrees.java
package scripts;

import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.adapter.component.InterfaceComponent;

import org.rspeer.script.Script;

import scripts.Debug;
import scripts.DisposableTask;

public class ChopTrees extends DisposableTask
{
	public static Position TREE_POSITION = new Position(3276, 3450);
	public static int CHOPPING_TREE = 879;
		
	private boolean startedChopping = false;
	public int treesCut = 0;
	public int failedAttempts = 0;
	public long woodCuttingXp = Skills.getExperience( Skill.WOODCUTTING );

	
	public boolean disposable()
	{
		return false;
	}

	public boolean validate()
	{
		return (Inventory.contains(n -> n.getName().contains(" axe" )) 
			|| Equipment.contains(n -> n.getName().contains(" axe" )))
			&& Inventory.getFreeSlots() > 0;
		//return true;
	}
	
	public int execute()
	{	
		Debug.out( "ChopTrees" );
		
		walkToTrees();
		
		chop();
		
		return 0;
	}
	
	private void walkToTrees()
	{
		if( Players.getLocal().getPosition().distance( TREE_POSITION ) > 10
			&& !Players.getLocal().isAnimating() )
		{
			Debug.out( "Walking to trees" );
			Movement.walkTo( TREE_POSITION );
			Time.sleep( Random.low(250, 600), Random.high(601, 1300) );
		}
	}
	
	private void chop()
	{		
		if( startedChopping )
		{
			if( Players.getLocal().getAnimation() != CHOPPING_TREE )
			{
				Debug.out( "Player attempted to cut a tree" );
				updateTreesCut();
				startedChopping = false;
				antiban();
			}
			else
			{
				Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 850, Random.high(8000, 20000) );
			}
			
		}
		else if( !Players.getLocal().isAnimating()
			&& Players.getLocal().getPosition().distance( TREE_POSITION ) <= 10 )
		{
			//TREE MUST ALSO BE WITHIN RANGE?
			SceneObject tree = SceneObjects.getNearest( "Tree" );
		
			if( tree != null )
			{				
				if( tree.interact( "Chop down" ) )
				{
					Debug.out( "Attempting to interact with SceneObject: " + tree.getId() );
					
					if( Time.sleepUntil(() -> Players.getLocal().getAnimation() == CHOPPING_TREE, 800, Random.high(6718, 11455) ) )
					{
						Debug.out( "Successful interaction with SceneObject: " + tree.getId() );
						startedChopping = true;
						Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 850, Random.high(8000, 20000) );
					}
					else
					{
						Debug.out( "Timeout interaction with SceneObject: " + tree.getId() );
					}
				}
				else
				{
					Debug.out( "Filed interaction with SceneObject: " + tree.getId() );
				}
			}
			else
			{
				Debug.out( "Selected SceneObject is null" );
			}
		}
	}
	
	private void updateTreesCut()
	{
		long currentWoodCuttingXp = Skills.getExperience( Skill.WOODCUTTING );
		
		if( currentWoodCuttingXp > woodCuttingXp )
		{
			failedAttempts = 0;
			treesCut++;
			woodCuttingXp = currentWoodCuttingXp;
			Debug.out( "Trees cut: " + treesCut );
		}
		else
		{
			failedAttempts++;
			Debug.out( "Failed attempts: " + failedAttempts );
		}
	}
	
	private void antiban()
	{
		if( Random.low( 1, 100 ) > 55 )
		{
			Debug.out( "Long Antiban..." );
			Time.sleep( Random.low(2500, 5000), Random.high(5001, 43478) );
		}
		else
		{
			Debug.out( "Short Antiban..." );
			Time.sleep( Random.low( 333, 1312), Random.low( 1313, 12710) ); 
		}
	}
}
