package io.anuke.mindustry.ai.control.tasks;

import com.badlogic.gdx.math.GridPoint2;
import io.anuke.mindustry.ai.control.AI;
import io.anuke.mindustry.ai.control.BfsFinder;
import io.anuke.mindustry.ai.control.WorkTask;
import io.anuke.mindustry.entities.traits.BuilderTrait.BuildRequest;
import io.anuke.mindustry.entities.units.types.WorkerDrone;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.type.Recipe;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.defense.turrets.Turret;
import io.anuke.mindustry.world.blocks.production.Drill;
import io.anuke.ucore.util.Geometry;

import static io.anuke.mindustry.Vars.world;

public class WallTask implements WorkTask{
    private Tile result;
    private Block block;
    private boolean done;

    public WallTask(Tile start, Block block){
        this.block = block;
        AI.executor.submit(() -> {
            result = BfsFinder.findGoal(start, conveyor -> conveyor.target().getTeam() == Team.red &&
                    (conveyor.target().block() instanceof Turret || conveyor.target().block() instanceof Drill), tile -> false);
            done = true;
            return result;
        });
    }

    @Override
    public void update(WorkerDrone drone){
        if(!done) return;
        drone.finishTask();
        if(result == null) return;

        for(GridPoint2 point : Geometry.d8){
            Tile other = world.tile(result.x + point.x, result.y + point.y);
            if(other != null && other.block().alwaysReplace){
                drone.beginTask(new BuildBlockTask(new BuildRequest(other.x, other.y, 0, Recipe.getByResult(block))));
            }
        }
    }
}
