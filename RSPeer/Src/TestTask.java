//TestTask.java

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
import org.rspeer.runetek.api.component.tab.Spell.Modern;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
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

public class TestTask extends DisposableTask
{
	public Area LUMBRIDGE_BANK = Area.rectangular( 3207, 3217, 3209, 3219, 2 );
	public Area LUMBRIDGE_TOLL_GATE = Area.rectangular( 3257, 3226, 3261, 3229, 0 );
	public boolean reverse = false;

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
		Debug.out( "Test Task" );	
				
		if ( !reverse )
		{
			if ( !LUMBRIDGE_BANK.contains( Players.getLocal().getPosition() ) )
			{
				Debug.out( "Walking to Lumbridge bank" );
				
				List<Position> bankTiles = LUMBRIDGE_BANK.getTiles();
				int randInt = Random.nextInt( bankTiles.size() - 1 );
				
				Position randTile = bankTiles.get( randInt );
				Movement.walkTo( randTile );
			}
			else if ( LUMBRIDGE_BANK.contains( Players.getLocal().getPosition() ) )
			{
				Debug.out( "Reversing path" );
				reverse = true;
			}
		}
		else
		{
			if ( !LUMBRIDGE_TOLL_GATE.contains( Players.getLocal().getPosition() ) )
			{
				/*
				Debug.out( "Walking to Lumbridge toll gate" );
				Position randTile = LUMBRIDGE_TOLL_GATE.getTiles().get( 
					Random.nextInt( LUMBRIDGE_TOLL_GATE.getTiles().size() % LUMBRIDGE_TOLL_GATE.getTiles().size() ) );
				Movement.walkTo( randTile );
				*/
				
				Debug.out( "Walking to Lumbridge toll gate" );
				
				List<Position> gateTiles = LUMBRIDGE_TOLL_GATE.getTiles();
				int randInt = Random.nextInt( gateTiles.size() - 1 );
				
				Position randTile = gateTiles.get( randInt );
				Movement.walkTo( randTile );
			}
			else if ( LUMBRIDGE_TOLL_GATE.contains( Players.getLocal().getPosition() ) )
			{
				Debug.out( "Reversing path" );
				reverse = false;
			}
		}
				
		return 0;
	}
}
