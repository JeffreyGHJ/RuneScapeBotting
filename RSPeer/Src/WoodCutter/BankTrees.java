//BankTrees.java
package scripts;

import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.component.InterfaceComponent;

import org.rspeer.script.Script;

import scripts.Debug;
import scripts.DisposableTask;

public class BankTrees extends DisposableTask
{
	public static Position VARROCK_EAST_BANK = 
		BankLocation.VARROCK_EAST.getPosition();
	
	public boolean disposable()
	{
		return false;
	}

	public boolean validate()
	{
		return (!Inventory.contains(n -> n.getName().contains(" axe" )) 
			&& !Equipment.contains(n -> n.getName().contains(" axe" )))
			|| Inventory.getFreeSlots() == 0;
	}
	
	public int execute()
	{	
		Debug.out( "BankTrees" );
		
		openBank();
		
		depositInventory();
		
		equipAxe();
		
		return 0;
	}
	
	private void openBank()
	{
		if( !Bank.isOpen() )
		{
			Debug.out( "Opening Bank" );
			Bank.open( BankLocation.VARROCK_EAST );
		}
		//HANDLE BANK DIALOG BOX PRESENTED TO NEW ACCOUNTS
		else if ( Interfaces.getComponent( 664, 28 ) != null 
			&& Interfaces.getComponent( 664, 28 ).isVisible() )
		{
			Debug.out( "Closing bank dialog box" );
			Interfaces.getComponent( 664, 28 ).click();
			Time.sleepUntil(() -> !Interfaces.getComponent( 664, 28 ).isVisible(), 200, 4000);
		}
	}
	
	private void depositInventory()
	{
		if( Bank.isOpen() )
		{
			Debug.out( "Depositing inventory" );
			Bank.depositAllExcept( n -> n.getName().contains( " axe" ) );
		}
	}
	
	private void equipAxe()
	{
		
		//Check for equipped axe
		if( Equipment.contains( "Bronze axe" ) )
		{
			Debug.out( "Player already has axe equipped" );
			return;
		}
		else if( Inventory.contains( "Bronze axe" ) )
		{
			Debug.out( "Player has axe in inventory, equipping" );
			Inventory.getFirst( "Bronze axe" ).interact( "Wield" );
			Time.sleepUntil(() -> Equipment.contains( "Bronze axe" ), 300, 4000);
		}
		else
		{
			Debug.out( "Checking bank for axe" );
			
			if( !Bank.isOpen() )
			{
				Bank.open( BankLocation.VARROCK_EAST );
				Time.sleepUntil(() -> Bank.isOpen(), 300, 4000);
			}
			
			if( Bank.isOpen() )
			{
				if( Bank.contains( "Bronze axe" ) )
				{
					Bank.withdraw( "Bronze axe", 1);
					Time.sleepUntil(() -> Inventory.contains( "Bronze axe" ), 300, 4000);
					Inventory.getFirst( "Bronze axe" ).interact( "Wield" );
					Time.sleepUntil(() -> Equipment.contains( "Bronze axe" ), 300, 4000);
				}
				else
				{
					Debug.out( "Player does not own any axes" );
				}
			}
		}
	}
}
