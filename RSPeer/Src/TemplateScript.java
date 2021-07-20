//TemplateScript

import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.adapter.scene.SceneObject;

import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.ScriptCategory;

@ScriptMeta(
	name = "Template Script",  
	desc = "An empty template for an RSPeer script", 
	developer = "N/A", 
	category = ScriptCategory.TOOL
)

public class TemplateScript extends Script 
{
    @Override
    public void onStart() 
	{
		System.out.println("Hello World!");
    }

    @Override
    public int loop() 
	{
		/*
		*	Code in here starting from the top-down will be ran and repeated.
		*
		*	The rate of repeat is defined by the returning int, 
		*	this number represents milliseconds. 1000ms = 1 second.
        */

		System.out.println("Loop");

		final SceneObject tree = SceneObjects.getNearest("Tree");

		if ( tree != null )
		{
			tree.interact("Chop down");
		}
		else
		{
			System.out.println("Tree is null :(");
		}

		System.out.println("Loop");

        return 1000; 
    }

    @Override
    public void onStop() 
	{
		System.out.println("Stopped");
    }
}

