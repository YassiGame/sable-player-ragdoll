package dev.leo.sableplayerragdoll.neoforge.client;

import dev.leo.sableplayerragdoll.entity.RagdollSeatEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.mixinhelpers.camera.new_camera_types.SableCameraTypes;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class RagdollCameraHelper {
   private static final int MAX_CAMERA_RETRY_TICKS = 40;
   // ShoulderSurfing crashes on unknown CameraType values in Perspective.of(), fall back to vanilla 3rd person cam.
   private static final boolean SHOULDER_SURFING = ModList.get().isLoaded("shouldersurfing");

   private static int pendingCameraTicks;
   private static boolean suppressLocalPlayerRender;
   private static boolean cameraSwitched;
   private static CameraType cameraTypeBeforeRagdoll;

   private RagdollCameraHelper() {
   }

   public static void init() {
      NeoForge.EVENT_BUS.addListener(RagdollCameraHelper::onClientTick);
      NeoForge.EVENT_BUS.addListener(RagdollCameraHelper::onRenderPlayer);
      NeoForge.EVENT_BUS.addListener(RagdollCameraHelper::onRenderHand);
   }

   public static void requestUnlockedContraptionCamera() {
      cameraTypeBeforeRagdoll = Minecraft.getInstance().options.getCameraType();
      suppressLocalPlayerRender = true;
      pendingCameraTicks = MAX_CAMERA_RETRY_TICKS;
      tryActivateUnlockedContraptionCamera();
   }

   private static void onClientTick(Post event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (suppressLocalPlayerRender && (minecraft.player == null || !minecraft.player.isPassenger())) {
         suppressLocalPlayerRender = false;
      }
      if (pendingCameraTicks > 0) {
         pendingCameraTicks--;
         if (tryActivateUnlockedContraptionCamera()) {
            pendingCameraTicks = 0;
         }
      }
   }

   private static void onRenderPlayer(RenderPlayerEvent.Pre event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (event.getEntity().getVehicle() instanceof RagdollSeatEntity) {
         event.setCanceled(true);
         return;
      }
      if (suppressLocalPlayerRender && minecraft.player != null && event.getEntity() == minecraft.player) {
         event.setCanceled(true);
      }
   }

   private static void onRenderHand(RenderHandEvent event) {
      if (suppressLocalPlayerRender) {
         event.setCanceled(true);
      }
   }

   private static boolean tryActivateUnlockedContraptionCamera() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null || minecraft.level == null || !player.isPassenger()) return false;
      if (Sable.HELPER.getVehicleSubLevel(player) == null) return false;
      minecraft.options.setCameraType(SHOULDER_SURFING ? CameraType.THIRD_PERSON_BACK : SableCameraTypes.SUB_LEVEL_VIEW_UNLOCKED);
      cameraSwitched = true;
      return true;
   }

   public static void resetFromContraptionCamera() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.options != null) {
         if (cameraSwitched) {
            minecraft.options.setCameraType(cameraTypeBeforeRagdoll != null ? cameraTypeBeforeRagdoll : CameraType.FIRST_PERSON);
            cameraTypeBeforeRagdoll = null;
            cameraSwitched = false;
         }
         pendingCameraTicks = 0;
         suppressLocalPlayerRender = false;
      }
   }

}
