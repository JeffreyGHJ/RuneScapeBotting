package Trikkstr.Scripts.Tasks;

import Trikkstr.Scripts.GoblinKiller.CONSTANTS;
import Trikkstr.Scripts.GoblinKiller.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.*;

import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class Fight extends Task
{

    final static int GOBLIN[] = { 3029, 3030, 3031, 3032, 3034, 3035 };
    final static int FOOD[] = { 315, 1971, 2309 };

    final static int HUT_VALUES[] = {3243, 3244, 3245, 3246, 3247, 3248};

    private final Walker walker = new Walker(ctx);

    Component inventory = ctx.widgets.widget(161).component(61);

    public Fight(ClientContext ctx)
    {
        super(ctx);
    }

    @Override
    public boolean activate()
    {
        return true;
    }

    @Override
    public void execute()
    {
        System.out.printf("Executing Fight.\n");
        System.out.printf("Health: %d\n", ctx.combat.health());

        //IF YOU ARE NOT PAST THE GATE THEN WALK THE PATH, THEN OPEN THE GATE

        while(ctx.players.local().tile().x() > 3267)
        {
            walker.walkPath(CONSTANTS.AK_BANK_TO_GOBLINS);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().inCombat();
                }
            }, 1000, 1);

            //if close to the gate and not already on the other side, then pay the toll
            if(ctx.objects.select().id(2882).poll().tile().distanceTo(ctx.players.local()) < 8
                    && ctx.players.local().tile().x() > 3267)
            {
                System.out.printf("Opening Al-Kharid Gate\n");
                if (!ctx.objects.select().id(2882).poll().inViewport())
                    ctx.camera.turnTo(ctx.objects.select().id(2882).poll());

                ctx.objects.select().id(2882).poll().interact("Pay-toll(10gp)", "Gate");

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().tile().x() < 3268;
                    }
                }, 500, 6);
            }
        }

        if(bonesInInventory() && !ctx.players.local().inCombat())
            dropBones();


        if(lootNearby() && !ctx.players.local().inCombat())
        {
            System.out.printf("Looting.\n");
            pickup();
        }

        if(hasFood() || ctx.combat.health() >= 10)
        {
            System.out.printf(("Has Food: True\n"));

            if(needsHeal())
            {
                System.out.printf("Needs Heal: True\n");
                heal();
            }
            else if(shouldAttack())
            {
                System.out.printf("Should Attack: True\n");
                attack();
            }
        }
    }

    public boolean bonesInInventory()
    {
        return ctx.inventory.select().id(526).count() > 0;
    }

    public boolean needsHeal()
    {
        return ctx.combat.health() < 6;
    }

    public boolean shouldAttack()
    {
        return !ctx.players.local().inCombat();
    }

    public boolean hasFood()
    {
        return ctx.inventory.select().id(FOOD).count() > 0;
    }

    public boolean lootNearby()
    {
        GroundItem loot = ctx.groundItems.select().name(Pattern.compile("(.*rune)|(Coins)|(.*bolts)")).nearest().poll();

        if(ctx.inventory.select().id(FOOD).count() > 0)
        {
            return(loot.tile().distanceTo(ctx.players.local().tile()) <= 4);
        }
        else
        {
            return(loot.tile().distanceTo(ctx.players.local().tile()) <= 18);
        }

    }

    public void pickup()
    {
        GroundItem loot = getLoot();

        final int startingWealth = ctx.inventory.select().name(Pattern.compile("(.*rune)|(Coins)|(.*bolts)")).count(true);

        if(ctx.inventory.select().id(loot).count() > 0 || ctx.inventory.count() < 28)
        {
            ctx.camera.turnTo(loot);

            if(!loot.inViewport())
                ctx.movement.step(loot);

            loot.interact("Take");
        }


        Condition.wait(new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                final int currentWealth = ctx.inventory.select().name(Pattern.compile("(.*rune)|(Coins)|(.*bolts)")).count(true);
                return currentWealth != startingWealth;
            }
        }, 400, 7);
    }

    public void attack()
    {
        System.out.printf("Selecting A Goblin To Attack\n");

        final Npc goblin = getGoblin();

        if(!goblin.inViewport())
        {
            ctx.camera.turnTo(goblin);
            ctx.movement.step(goblin);
        }


        goblin.interact("Attack", "Goblin");

        //after initiating an attack, wait until you or the target are in combat
        Condition.wait(new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {

                return ctx.players.local().inCombat() || goblin.inCombat();
            }
        }, 500, 10);

        //if one of the the two was in combat, and it was not you then you can continue
        Condition.wait(new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {

                return !ctx.players.local().inCombat();
            }
        }, 500, 6);
    }

    public void heal()
    {
        inventory.click();

        Item food = ctx.inventory.select().id(FOOD).poll();

        final int startingHealth = ctx.combat.health();

        food.interact("Eat");

        Condition.wait(new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                final int currentHealth = ctx.combat.health();
                return currentHealth != startingHealth;
            }
        }, 150, 20);
    }

    public void dropBones()
    {
        for(Item bones : ctx.inventory.select().id(526))
        {
            if(ctx.controller.isStopping())
            {
                break;
            }

            final int startAmountInventory = ctx.inventory.select().count();
            bones.interact("Bury", "Bones");

            Condition.wait(new Callable<Boolean>()
            {
                @Override
                public Boolean call() throws Exception
                {
                    return ctx.inventory.select().count() != startAmountInventory;
                }
            }, 75, 20);
        }
    }

    public Npc getGoblin()
    {
        return ctx.npcs.select().id(GOBLIN).select(new Filter<Npc>()
        {
            @Override
            public boolean accept(Npc npc)
            {
                return !npc.inCombat();
            }
        }).select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc)
            {
                boolean hit = false;
                int x = npc.tile().x();
                int y = npc.tile().y();
                for(int i = 0;  i < HUT_VALUES.length; i++ )
                {
                    if(x == HUT_VALUES[i])
                        for(int j = 0; j < HUT_VALUES.length; j++)
                            if(y == HUT_VALUES[j])
                                hit = true;
                }
                return hit == false;
            }
        }).select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc)
            {
                int count = 0;
                int y = npc.tile().y();
                for(int i = 0;  i < HUT_VALUES.length; i++ )
                {
                    if(y == HUT_VALUES[i])
                        count += 1;
                }
                return count == 0;
            }
        }).nearest().poll();
    }

    public GroundItem getLoot()
    {
        return ctx.groundItems.select().name(Pattern.compile("(.*rune)|(Coins)|(.*bolts)")).select(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem item)
            {
                boolean hit = false;
                int x = item.tile().x();
                int y = item.tile().y();
                for(int i = 0;  i < HUT_VALUES.length; i++ )
                {
                    if(x == HUT_VALUES[i])
                        for(int j = 0; j < HUT_VALUES.length; j++)
                            if(y == HUT_VALUES[j])
                                hit = true;
                }
                return hit == false;
            }
        }).select(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem item)
            {
                int count = 0;
                int y = item.tile().y();
                for(int i = 0;  i < HUT_VALUES.length; i++ )
                {
                    if(y == HUT_VALUES[i])
                        count += 1;
                }
                return count == 0;
            }
        }).nearest().poll();
    }
}
