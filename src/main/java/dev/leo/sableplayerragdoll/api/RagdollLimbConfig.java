package dev.leo.sableplayerragdoll.api;

import java.util.OptionalDouble;

/**
 * Optional per-limb overrides applied when a ragdoll is assembled.
 *
 * <p>Every field is optional: anything left unset falls back to the built-in default for that
 * limb, so callers can override just a rotation, just the joint stiffness, or any subset. Rotation
 * is expressed in degrees about the limb's local axes (pitch = X, yaw = Y, roll = Z).
 *
 * <p>Build instances with {@link #builder()} and attach them to limbs via {@link RagdollLimbOptions}.
 */
public final class RagdollLimbConfig {
   private final OptionalDouble pitchDegrees;
   private final OptionalDouble yawDegrees;
   private final OptionalDouble rollDegrees;
   private final OptionalDouble angularStiffness;
   private final OptionalDouble angularDamping;

   private RagdollLimbConfig(
      OptionalDouble pitchDegrees,
      OptionalDouble yawDegrees,
      OptionalDouble rollDegrees,
      OptionalDouble angularStiffness,
      OptionalDouble angularDamping
   ) {
      this.pitchDegrees = pitchDegrees;
      this.yawDegrees = yawDegrees;
      this.rollDegrees = rollDegrees;
      this.angularStiffness = angularStiffness;
      this.angularDamping = angularDamping;
   }

   public OptionalDouble pitchDegrees() {
      return this.pitchDegrees;
   }

   public OptionalDouble yawDegrees() {
      return this.yawDegrees;
   }

   public OptionalDouble rollDegrees() {
      return this.rollDegrees;
   }

   public OptionalDouble angularStiffness() {
      return this.angularStiffness;
   }

   public OptionalDouble angularDamping() {
      return this.angularDamping;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {
      private OptionalDouble pitchDegrees = OptionalDouble.empty();
      private OptionalDouble yawDegrees = OptionalDouble.empty();
      private OptionalDouble rollDegrees = OptionalDouble.empty();
      private OptionalDouble angularStiffness = OptionalDouble.empty();
      private OptionalDouble angularDamping = OptionalDouble.empty();

      private Builder() {
      }

      public Builder rotation(double pitchDegrees, double yawDegrees, double rollDegrees) {
         this.pitchDegrees = OptionalDouble.of(pitchDegrees);
         this.yawDegrees = OptionalDouble.of(yawDegrees);
         this.rollDegrees = OptionalDouble.of(rollDegrees);
         return this;
      }

      public Builder pitch(double degrees) {
         this.pitchDegrees = OptionalDouble.of(degrees);
         return this;
      }

      public Builder yaw(double degrees) {
         this.yawDegrees = OptionalDouble.of(degrees);
         return this;
      }

      public Builder roll(double degrees) {
         this.rollDegrees = OptionalDouble.of(degrees);
         return this;
      }

      public Builder stiffness(double angularStiffness) {
         this.angularStiffness = OptionalDouble.of(angularStiffness);
         return this;
      }

      public Builder damping(double angularDamping) {
         this.angularDamping = OptionalDouble.of(angularDamping);
         return this;
      }

      public RagdollLimbConfig build() {
         return new RagdollLimbConfig(this.pitchDegrees, this.yawDegrees, this.rollDegrees, this.angularStiffness, this.angularDamping);
      }
   }
}
