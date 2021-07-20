package Trikkstr.Scripts.Tasks;

import Trikkstr.Scripts.GoblinKiller.CONSTANTS;
import Trikkstr.Scripts.GoblinKiller.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

public class Bank extends Task
{

    final static int FOOD[] = { 315, 1971, 2309 };

    Component clickToContinue = ctx.widgets.widget(231).component(2);
    Component clickToContinue2 = ctx.widgets.widget(217).component(2);
    Component yes = ctx.widgets.widget(219).component(0).component(2);

    private final Walker walker = new Walker(ctx);

    public Bank(ClientContext ctx)
    {
        super(ctx);
    }

    @Override
    public boolean activate()
    {
        //will not go to the bank to get food unless at least 30 coins are in the inventory while they are near goblins,
        //and if the player has no food, and  if the player is not in combat
        //this allows the 'Fight' task to execute instead so that the bot can loot nearby coins until it has enough
        return ctx.inventory.select().id(FOOD).count() < 1 && !ctx.players.local().inCombat()
                && ((ctx.inventory.select().id(995).count(true) > 29 && ctx.players.local().tile().x() < 3268)
                || ctx.inventory.select().id(995).count(true) < 10 && ctx.players.local().tile().x() > 3267);
    }

    @Override
    public void execute()
    {
        System.out.printf("Executing Bank.\n");

        //Walk to the bank
        while(ctx.players.local().tile().distanceTo(ctx.bank.nearest()) > 5)
        {
            walker.walkPathReverse(CONSTANTS.AK_BANK_TO_GOBLINS);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().inCombat();
                }
            }, 1000, 1);

            //if close to the gate and not already on the other side, then pay the toll
            if(ctx.objects.select().id(2882).poll().tile().distanceTo(ctx.players.local()) < 4
                    && ctx.players.local().tile().x() < 3268)
            {
                System.out.printf("Opening Al-Kharid Gate\n");
                if(!ctx.objects.select().id(2882).poll().inViewport())
                    ctx.camera.turnTo(ctx.objects.select().id(2882).poll());

                ctx.objects.select().id(2882).poll().interact("Pay-toll(10gp)", "Gate");

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().tile().x() > 3267;
                    }
                }, 500, 6);
            }
        }

        Condition.wait(new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                return !ctx.players.local().inMotion();
            }
        }, 1000, 5);

        ctx.camera.turnTo(ctx.bank.nearest());

        //open the bank
        while(!ctx.bank.opened() && ctx.players.local().tile().distanceTo(ctx.bank.nearest().tile()) <= 5)
            ctx.bank.open();

        ctx.bank.depositInventory();

        //wait a moment for items to deposit
        Condition.sleep(Random.nextInt(1500, 2666));

        if(ctx.bank.opened())
        {
            //check the balance
            int bankBalance = ctx.bank.select().id(995).count(true);
            System.out.printf("Balance: %d\n", bankBalance);

            //subtract 10 from the balance (10 coins saved to get back to the goblins
            int spendable = bankBalance - 10;
            System.out.printf("Spendable: %d\n", spendable);

            //check to see if there are kebabs in the bank
            int foodAvailable = ctx.bank.select().id(FOOD[1]).count(true);
            System.out.printf("Food Available In Bank: %d\n", foodAvailable);

            //if there are 10 coins or less and no food then exit (10 to cross and at least one coin or food to eat)
            if(bankBalance < 10)
            {
                System.out.printf("Not enough coins: 10 are needed to pay the gate toll.\n");
                ctx.controller.stop();
            }
            else if(bankBalance < 11 && foodAvailable < 1)
            {
                System.out.printf("Not enough supplies: Need at least 10 coins and 1 kebab OR just 11 coins.\n");
                ctx.controller.stop();
            }
            else
                ;

            ctx.bank.withdraw(995, 10);

            //if there is no food
            if(foodAvailable < 1)
            {
                //withdraw up to 10 additional coins
                if(spendable < 10)
                    ctx.bank.withdraw(995, spendable);
                else
                    ctx.bank.withdraw(995, 10);

                ctx.bank.close();
                //buy kebabs until coins = 10
                buyKebabs();
            }
            //if there are less than 10 kebabs then just withdraw what is available
            //otherwise withdraw 10 kebabs
            else if(foodAvailable < 10)
            {
                ctx.bank.withdraw(FOOD[1], foodAvailable);
                ctx.bank.close();
            }
            else
            {
                ctx.bank.withdraw(FOOD[1], 10);
                ctx.bank.close();
            }
        }
    }

    public void buyKebabs()
    {
        final Npc Karim = ctx.npcs.select().id(529).poll();

        while(ctx.players.local().tile().distanceTo(Karim) > 3)
        {
            ctx.movement.step(Karim);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.players.local().inMotion();
                }
            }, 1000, 1);
        }

        ctx.camera.turnTo(Karim);

        while(ctx.inventory.select().id(995).count(true) > 10)
        {
            if(ctx.controller.isStopping())
            {
                ctx.controller.stop();
            }

            Karim.interact("Talk-to");

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return clickToContinue.visible();
                }
            }, 1000, 6);

            clickToContinue.click();

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return yes.visible();
                }
            }, 1000, 6);

            final int trigger = ctx.inventory.select().count();

            yes.click();

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return clickToContinue2.visible();
                }
            }, 1000, 6);

            clickToContinue2.click();

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().count() != trigger;
                }
            }, 1000, 6);
        }
    }
}
