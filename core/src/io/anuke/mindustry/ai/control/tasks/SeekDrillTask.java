package io.anuke.mindustry.ai.control.tasks;

import com.badlogic.gdx.utils.async.AsyncResult;
import io.anuke.mindustry.ai.control.AI;
import io.anuke.mindustry.ai.control.BfsFinder;
import io.anuke.mindustry.ai.control.WorkTask;
import io.anuke.mindustry.content.blocks.ProductionBlocks;
import io.anuke.mindustry.entities.traits.BuilderTrait.BuildRequest;
import io.anuke.mindustry.entities.units.types.WorkerDrone;
import io.anuke.mindustry.type.Item;
import io.anuke.mindustry.type.Recipe;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Build;
import io.anuke.mindustry.world.Tile;

public class SeekDrillTask implements WorkTask{
    private final AsyncResult<Tile> result;
    private final Item item;
    private final Tile dest;

    public SeekDrillTask(Tile dest, Item item){
        this.item = item;
        this.dest = dest;
        Block block = ProductionBlocks.mechanicalDrill;

        result = AI.executor.submit(() -> BfsFinder.findGoal(dest,
        tile -> tile.floor().dropsItem(item) && Build.validPlace(dest.getTeam(), tile.x, tile.y, block, 0),
        other -> (other.synthetic() || other.floor().isLiquid)));
    }

    @Override
    public void begin(WorkerDrone drone){

    }

    @Override
    public void update(WorkerDrone drone){
        if(result.isDone()){
            Tile tile = result.get();
            drone.finishTask();

            if(tile != null){
                drone.beginTask(new ConveyorPathTask(tile, item, other -> other == dest));
                drone.beginTask(new BuildBlockTask(new BuildRequest(tile.x, tile.y, 0, Recipe.getByResult(ProductionBlocks.mechanicalDrill))));
            }else{
                drone.beginTask(new MineTask(item, 50));
            }
        }
    }

}
