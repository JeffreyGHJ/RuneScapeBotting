//BankTask.java
//Testing banking API

package scripts;

import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.api.scene.HintArrow;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
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
import org.rspeer.runetek.adapter.component.Item;
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

public class BankTask extends DisposableTask
{
	String[] FOOD = { "Bread", "Shrimps" };
	//List<String> FOOD = Arrays.asList( food );

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
		Debug.out( "Bank Task" );	
				
		//CALCULATE COINS
		if( Bank.contains( "Coins" ) )
		{
			Debug.out( "Coins in bank: " + Bank.getCount( "Coins" ) );
		}
		else
		{
			Debug.out( "There are no coins in the bank" );
		}
		
		//IDENTIFY FOOD IN INVENTORY
		if( Inventory.contains( FOOD ) )
		{
			Debug.out( "Player has food" );
			
			Item[] items = Inventory.getItems();
			
			for( Item i : items )
			{
			
				if( Arrays.asList(FOOD).contains( i.getName() ) )
				{
					Debug.out( i.getName() + " x " + Inventory.getCount(true, i.getName() ) );
				}
			
			}
		
		}
		else
		{
			Debug.out( "Player does not have food" );
		}
				
		return 0;
	}
	
}
