package dev.leo.sableplayerragdoll;

import dev.leo.sableplayerragdoll.api.RagdollAPI;
import dev.leo.sableplayerragdoll.api.RagdollLaunchOptions;
import dev.leo.sableplayerragdoll.api.RagdollLimbConfig;
import dev.leo.sableplayerragdoll.api.RagdollLimbOptions;
import dev.leo.sableplayerragdoll.api.RagdollSession;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity.BodyPart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class RagdollKeybindExample {

   private static final double UPWARD_KICK = 5.0;

   private RagdollKeybindExample() {
   }

   @Nullable
   public static RagdollSession launch(ServerPlayer player, Vec3 velocity) {
      Vec3 launchVelocity = player.isFallFlying() ? velocity : velocity.add(0, UPWARD_KICK, 0);
      RagdollLimbOptions pose = player.isFallFlying() ? elytraPose() : onFootPose();
      RagdollLaunchOptions options = RagdollLaunchOptions.builder().limbs(pose).build();
      return RagdollAPI.launch(player, launchVelocity, options);
   }

   private static RagdollLimbOptions onFootPose() {
      return RagdollLimbOptions.builder()
         .limb(BodyPart.LEFT_ARM, RagdollLimbConfig.builder().roll(20))
         .limb(BodyPart.RIGHT_ARM, RagdollLimbConfig.builder().roll(-20))
         .limb(BodyPart.RIGHT_LEG, RagdollLimbConfig.builder().roll(-20))
         .limb(BodyPart.LEFT_LEG, RagdollLimbConfig.builder().roll(20))
         .build();
   }

   private static RagdollLimbOptions elytraPose() {
      return RagdollLimbOptions.builder()
         .limb(BodyPart.LEFT_ARM, RagdollLimbConfig.builder().stiffness(60).damping(8))
         .limb(BodyPart.RIGHT_ARM, RagdollLimbConfig.builder().stiffness(60).damping(8))
         .build();
   }
}
