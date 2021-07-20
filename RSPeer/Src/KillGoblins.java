//KillGoblins.java

package scripts;

import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.api.scene.HintArrow;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.component.tab.Spell.Modern;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.api.commons.math.Random;

import org.rspeer.script.Script;
import org.rspeer.script.task.Task;

import scripts.Debug;
import scripts.DisposableTask;
import scripts.Util;

import java.util.*;

public class KillGoblins extends DisposableTask
{
	public Area LUMBRIDGE_BANK = Area.rectangular( 3207, 3217, 3209, 3219, 2 );
	public Area LUMBRIDGE_TOLL_GATE = Area.rectangular( 3257, 3226, 3261, 3229, 0 );
	public Position dropLocation = null;
	public int minHitpoints = 6;
	//public String[] ITEMS = { "Coins" };
	public List<String> ITEMS_TO_LOOT = Arrays.asList( "Coins", "Body rune", "Water rune", "Earth rune", "Bronze bolts" );
	public List<String> FOOD = Arrays.asList( "Shrimps", "Bread", "Kebab" );

	public boolean disposable()
	{
		return false;
	}
	
	public boolean validate()
	{
		return true;
	}

	public int execute()
	{
		Debug.out( "Kill Goblins" );	
		
		if ( readyToLoot() )
		{
			Util.lootPosition( dropLocation, ITEMS_TO_LOOT );
			dropLocation = null;
		}
		else if ( readyToAttack( minHitpoints ) )
		{
			Util.attackNpc( "Goblin" );
		}	
		else if ( Players.getLocal().getTarget() != null )
		{
			Debug.out( "Player is in combat with: " + Players.getLocal().getTarget() );
			
			dropLocation = Players.getLocal().getTarget().getPosition();
			Time.sleepUntil(() -> Players.getLocal().getTarget() == null, 750, 10000);
		}
		//LOOT NEARBY
		//EAT FOOD
					
		return 0;
	}
	
	public boolean readyToAttack( int minHitpoints )
	{
		return Players.getLocal().getTarget() == null
			&& Skills.getCurrentLevel( Skill.HITPOINTS ) >= minHitpoints
			&& !Players.getLocal().isMoving();
	}
	
	public boolean readyToLoot()
	{
		return dropLocation != null 
			&& Players.getLocal().getTarget() == null
			&& !Players.getLocal().isAnimating();
	}
	
	public void lootNearby()
	{
		//TO-DO
	}

}
