package io.anuke.mindustry.ai.control;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import io.anuke.mindustry.ai.control.tasks.*;
import io.anuke.mindustry.content.Items;
import io.anuke.mindustry.content.blocks.CraftingBlocks;
import io.anuke.mindustry.content.blocks.ProductionBlocks;
import io.anuke.mindustry.content.blocks.TurretBlocks;
import io.anuke.mindustry.entities.TileEntity;
import io.anuke.mindustry.entities.traits.BuilderTrait.BuildRequest;
import io.anuke.mindustry.entities.units.types.WorkerDrone;
import io.anuke.mindustry.game.EventType.TileChangeEvent;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.type.Item;
import io.anuke.mindustry.type.Recipe;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import io.anuke.ucore.core.Events;
import io.anuke.ucore.entities.EntityGroup;
import io.anuke.ucore.entities.impl.BaseEntity;
import io.anuke.ucore.util.Tmp;

import static io.anuke.mindustry.Vars.unitGroups;
import static io.anuke.mindustry.Vars.world;

public class AI{
    public static final AsyncExecutor executor = new AsyncExecutor(6);

    private final Team team;
    private ObjectMap<Block, ObjectSet<Tile>> blocks = new ObjectMap<>();
    private IntMap<Item> tags = new IntMap<>();

    public AI(Team team) {
        this.team = team;

        Events.on(TileChangeEvent.class, event -> {
            for(ObjectSet<Tile> set : blocks.values()){
                set.remove(event.tile);
            }

            if(event.tile.getTeam() == team){
                if(!blocks.containsKey(event.tile.block())) blocks.put(event.tile.block(), new ObjectSet<>());
                blocks.get(event.tile.block()).add(event.tile);
            }
        });
    }

    public void update(){
        EntityGroup<? extends BaseEntity> group = unitGroups[team.ordinal()];
        for(BaseEntity entity : group.all()){
            if(!(entity instanceof WorkerDrone)) continue;

            WorkerDrone drone = (WorkerDrone)entity;
            if(drone.getTask() == null){
                assignTask(drone);
            }
        }
    }

    public void tagTile(Tile tile, Item item){
        tags.put(tile.id(), item);
    }

    public Item tag(Tile tile){
        return tags.get(tile.id());
    }

    ObjectSet<Tile> getBlock(Block block){
        if(!blocks.containsKey(block)) blocks.put(block, new ObjectSet<>());
        return blocks.get(block);
    }

    void assignTask(WorkerDrone drone){
        TileEntity core = drone.getClosestCore();

        Item toMine = Items.copper;
        int amount = core.items.get(toMine);

        if(amount >= 300){
            createTurret(drone);
        }else if(amount >= 100 && core.items.get(Items.lead) < 50){
            createDrill(drone, Items.lead);
        }else if(amount >= 50 && getBlock(ProductionBlocks.mechanicalDrill).size < 12){
            createDrill(drone, toMine);
        }else{
            mineItem(drone, toMine, 50);
        }
    }

    void createTurret(WorkerDrone drone){
        drone.beginTask(new TurretTask(drone.getClosestCore().tile, TurretBlocks.duo, Items.copper));
    }

    void createSmelter(WorkerDrone drone){
        TileEntity core = drone.getClosestCore();

        Tile tile;
        do{
            Tmp.v1.setToRandomDirection().scl(100f);
            tile = world.tileWorld(core.x + Tmp.v1.x, core.y + Tmp.v1.y);
        }while(tile == null || !tile.block().alwaysReplace);

        drone.beginTask(new PathfindTask(tile, Items.densealloy));
        leadDrill(drone, Items.coal, tile);
        leadDrill(drone, Items.lead, tile);
        leadDrill(drone, Items.copper, tile);
        drone.beginTask(new BuildBlockTask(new BuildRequest(tile.x, tile.y, 0, Recipe.getByResult(CraftingBlocks.smelter))));
    }

    void mineItem(WorkerDrone drone, Item item, int amount){
        drone.beginTask(new MineTask(item, amount));
    }

    void createDrill(WorkerDrone drone, Item item){
        drone.beginTask(new DrillTask(drone.getClosestCore().tile, item));
    }

    void leadDrill(WorkerDrone drone, Item item, Tile tile){
        drone.beginTask(new SeekDrillTask(tile, item));
    }

}
