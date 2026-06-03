package dev.leo.sableplayerragdoll.neoforge.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

@Deprecated(forRemoval = false)
public class RagdollEndEvent extends dev.leo.sableplayerragdoll.api.RagdollEndEvent {
   public RagdollEndEvent(ServerPlayer player, Vec3 exitVelocity, Reason reason) {
      super(player, exitVelocity, reason);
   }
}
