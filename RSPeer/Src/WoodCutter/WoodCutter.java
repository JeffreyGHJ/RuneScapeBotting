
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.runetek.event.listeners.RenderListener;

import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.task.Task;

import scripts.Debug;
import scripts.DisposableTask;
import scripts.BankTrees;
import scripts.ChopTrees;
import java.util.*;

@ScriptMeta(
	name = "Wood Cutter",  
	desc = "Chop down trees and store in bank", 
	developer = "N/A", 
	category = ScriptCategory.TOOL
)

public class WoodCutter extends Script implements RenderListener
{
	public List<DisposableTask> taskList = new LinkedList<DisposableTask>();

    @Override
    public void onStart() 
	{
		System.out.println("onStart()");
		Debug.out("onStart()");
		
		//Start by banking
		taskList.add( new BankTrees() );
		taskList.add( new ChopTrees() );
		//Walk to trees
		//Chop trees
    }

    @Override
    public int loop() 
	{
        if ( !taskList.isEmpty() )
        {
			for( int task = 0; task < taskList.size(); task++ )
			{
				if ( taskList.get( task ).disposable() )
				{
					Debug.out( "Removing completed task:  " + taskList.get( task ) );
					taskList.remove( task );
				}
				else if ( taskList.get( task ).validate() )
				{
					taskList.get( task ).execute();
				}
			}
        }
        else
        {
        	Debug.out( "TaskList is empty" );
        	//Add new task
        }
        
        //Iterating like this casuses a concurrent modification exception btw
        /*
        for( DisposableTask task : taskList )
        {		
        	if( task.validate() )
			{
				task.execute();
			}
        		
			if( task.disposable() )
			{
				Debug.out( "Removing completed task:  " + task );
				taskList.remove( taskList.indexOf( task ) );
			}
        }
        */

		Debug.out("Loop");

        return Random.nextInt( 200, 700 ); 
    }

    @Override
    public void onStop() 
	{
		Debug.out("Stopped");
    }

	@Override
	public void notify( RenderEvent e )
	{
        Debug.Update(e);
	}
}

