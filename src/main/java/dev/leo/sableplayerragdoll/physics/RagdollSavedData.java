package dev.leo.sableplayerragdoll.physics;

import dev.leo.sableplayerragdoll.SablePlayerRagdoll;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity.BodyPart;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class RagdollSavedData extends SavedData {
   private static final String FILE_ID = SablePlayerRagdoll.MOD_ID;
   private static final SavedData.Factory<RagdollSavedData> FACTORY = new SavedData.Factory<>(RagdollSavedData::new, RagdollSavedData::load);
   private final Map<UUID, Map<BodyPart, UUID>> ragdolls = new HashMap<>();

   public static RagdollSavedData get(ServerLevel level) {
      return level.getDataStorage().computeIfAbsent(FACTORY, FILE_ID);
   }

   public static RagdollSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
      RagdollSavedData data = new RagdollSavedData();
      ListTag ragdollList = tag.getList("Ragdolls", Tag.TAG_COMPOUND);
      for (int i = 0; i < ragdollList.size(); i++) {
         CompoundTag ragdollTag = ragdollList.getCompound(i);
         if (!ragdollTag.hasUUID("Head")) {
            continue;
         }

         UUID headId = ragdollTag.getUUID("Head");
         Map<BodyPart, UUID> parts = new EnumMap<>(BodyPart.class);
         ListTag partList = ragdollTag.getList("Parts", Tag.TAG_COMPOUND);
         for (int partIndex = 0; partIndex < partList.size(); partIndex++) {
            CompoundTag partTag = partList.getCompound(partIndex);
            if (!partTag.hasUUID("SubLevel")) {
               continue;
            }

            BodyPart bodyPart = BodyPart.byName(partTag.getString("BodyPart"));
            parts.put(bodyPart, partTag.getUUID("SubLevel"));
         }

         if (parts.containsKey(BodyPart.HEAD)) {
            data.ragdolls.put(headId, parts);
         }
      }

      return data;
   }

   public void saveRagdoll(UUID headSubLevelId, Map<BodyPart, UUID> partSubLevelIds) {
      this.ragdolls.put(headSubLevelId, immutableCopy(partSubLevelIds));
      this.setDirty();
   }

   public void removeRagdoll(UUID headSubLevelId) {
      if (this.ragdolls.remove(headSubLevelId) != null) {
         this.setDirty();
      }
   }

   public Map<BodyPart, UUID> ragdoll(UUID headSubLevelId) {
      Map<BodyPart, UUID> parts = this.ragdolls.get(headSubLevelId);
      return parts == null ? Map.of() : Collections.unmodifiableMap(parts);
   }

   @Override
   public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
      ListTag ragdollList = new ListTag();
      this.ragdolls.forEach((headId, parts) -> {
         CompoundTag ragdollTag = new CompoundTag();
         ragdollTag.putUUID("Head", headId);
         ListTag partList = new ListTag();
         parts.forEach((bodyPart, partId) -> {
            CompoundTag partTag = new CompoundTag();
            partTag.putString("BodyPart", bodyPart.name());
            partTag.putUUID("SubLevel", partId);
            partList.add(partTag);
         });
         ragdollTag.put("Parts", partList);
         ragdollList.add(ragdollTag);
      });
      tag.put("Ragdolls", ragdollList);
      return tag;
   }

   private static Map<BodyPart, UUID> immutableCopy(Map<BodyPart, UUID> partSubLevelIds) {
      Map<BodyPart, UUID> copy = new EnumMap<>(BodyPart.class);
      copy.putAll(partSubLevelIds);
      return copy;
   }
}
