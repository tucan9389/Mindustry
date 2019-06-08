package io.anuke.mindustry.content;

import io.anuke.mindustry.game.ContentList;
import io.anuke.mindustry.type.Mech;
import io.anuke.mindustry.entities.mechanic.MechanicFactory;

public class Mechs implements ContentList{
    public static Mech alpha, delta, tau, omega, dart, javelin, trident, glaive;

    public static Mech starter;

    @Override
    public void load(){
        alpha = MechanicFactory.createMechanic("alpha-mech", false);
        delta = MechanicFactory.createMechanic("delta-mech", false);
        tau = MechanicFactory.createMechanic("tau-mech", false);
        omega = MechanicFactory.createMechanic("omega-mech", false);
        dart = MechanicFactory.createMechanic("dart-ship", true);
        javelin = MechanicFactory.createMechanic("javelin-ship", true);
        trident = MechanicFactory.createMechanic("trident-ship", true);
        glaive = MechanicFactory.createMechanic("glaive-ship", true);
        starter = dart;
    }
}
