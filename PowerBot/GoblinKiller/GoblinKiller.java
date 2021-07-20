package Trikkstr.Scripts.GoblinKiller;

import Trikkstr.Scripts.Tasks.Bank;
import Trikkstr.Scripts.Tasks.Fight;
import org.powerbot.script.rt4.*;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;

import java.util.ArrayList;
import java.util.List;

@Script.Manifest(

        name="Goblin Killer",
        description = "Kills Goblins and Loots them",
        properties = "client=4; author=Trikkstr; topic=1343746;"
)

public class GoblinKiller extends PollingScript<ClientContext>
{
    int pollCount = 0;

    long startTime;
    long endTime;
    long runTime;

    public static List<Task> taskList = new ArrayList<Task>();


    @Override
    public void start()
    {
        startTime = System.currentTimeMillis();
        System.out.println("Starting.");

        taskList.add(new Bank(ctx));
        taskList.add(new Fight(ctx));

    }

    @Override
    public void stop()
    {
        endTime = System.currentTimeMillis();
        runTime = (endTime - startTime) / 1000;
        System.out.printf("Total Runtime: %ds\n", runTime);
        System.out.printf("Polls: %d\n", pollCount);
        System.out.printf("Polls per second: %d\n", pollCount/runTime);
        System.out.println("Stopped.");
    }

    @Override
    public void poll()
    {
        System.out.printf("Polling...\n");

        for (Task task : taskList)
        {
            if (ctx.controller.isStopping())
            {
                break;
            }

            if (task.activate())
            {
                task.execute();
                break;
            }
        }

        pollCount += 1;
    }
}
