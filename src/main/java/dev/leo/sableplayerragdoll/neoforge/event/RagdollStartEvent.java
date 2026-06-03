package dev.leo.sableplayerragdoll.neoforge.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

@Deprecated(forRemoval = false)
public class RagdollStartEvent extends dev.leo.sableplayerragdoll.api.RagdollStartEvent {
   public RagdollStartEvent(ServerPlayer player, Vec3 velocity) {
      super(player, velocity);
   }
}
