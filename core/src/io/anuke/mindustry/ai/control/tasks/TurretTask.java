package io.anuke.mindustry.ai.control.tasks;

import com.badlogic.gdx.math.GridPoint2;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.ai.control.AI;
import io.anuke.mindustry.ai.control.BfsFinder;
import io.anuke.mindustry.ai.control.WorkTask;
import io.anuke.mindustry.content.blocks.DistributionBlocks;
import io.anuke.mindustry.entities.traits.BuilderTrait.BuildRequest;
import io.anuke.mindustry.entities.units.types.WorkerDrone;
import io.anuke.mindustry.type.Item;
import io.anuke.mindustry.type.Recipe;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.distribution.Conveyor;
import io.anuke.ucore.util.Geometry;

import static io.anuke.mindustry.Vars.world;

public class TurretTask implements WorkTask{
    private Tile result;
    private Block block;

    public TurretTask(Tile core, Block block, Item item){
        this.block = block;
        AI.executor.submit(() -> {
            result = BfsFinder.findGoal(core, conveyor -> conveyor.block() instanceof Conveyor && Vars.state.teams.get(core.getTeam()).ai.tag(conveyor) == item, tile -> false);
            return result;
        });
    }

    @Override
    public void update(WorkerDrone drone){
        if(result == null) return;

        drone.finishTask();
        drone.beginTask(new BuildBlockTask(new BuildRequest(result.x, result.y, 0, Recipe.getByResult(DistributionBlocks.router))));
        for(GridPoint2 point : Geometry.d4){
            Tile other = world.tile(result.x + point.x, result.y + point.y);
            if(other != null && other.block().alwaysReplace){
                drone.beginTask(new BuildBlockTask(new BuildRequest(other.x, other.y, 0, Recipe.getByResult(block))));
            }
        }
    }
}
