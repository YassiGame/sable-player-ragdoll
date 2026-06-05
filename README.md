# Sable Player Ragdoll

Sable Player Ragdoll is a NeoForge mod that adds player ragdolls powered by
Sable's physics system. It can ragdoll players, seat the real player onto the
simulated body, spawn playerless dummy ragdolls, and expose a small public API
for addon mods.

This project is still early and experimental. The goal is to keep the core mod
small while giving addon developers enough hooks to trigger ragdolls from their
own gameplay.

## Features

- Manual player ragdoll trigger through a client keybind.
- Physics-backed ragdoll body parts using Sable sublevels.
- Automatic player seating so the camera follows the ragdoll.
- Playerless dummy ragdolls with position, heading, skin profile, velocity, and
  despawn options.
- Per-limb spawn pose and joint stiffness/damping control through the public API.
- Simple API for addon mods to launch ragdolls or query active sessions.
- Datapack item tag support for weapons that ragdoll players on hit.
- Test commands for spawning dummies and giving a ragdoll test stick.

## Requirements

- Minecraft `1.21.1`
- NeoForge `21.1.219` or newer
- Sable `1.1.0` or newer
- Java `21`

## Commands

The command root is:

```mcfunction
/sable_player_ragdoll
```

Useful test commands:

```mcfunction
/sable_player_ragdoll dummy
/sable_player_ragdoll dummy <pos> <heading>
/sable_player_ragdoll dummy profile <profile>
/sable_player_ragdoll dummy profile <profile> <pos> <heading>
/sable_player_ragdoll test_stick
```

Dummy despawn options can be appended where supported:

```mcfunction
despawn default
despawn never
despawn after_ticks <ticks>
despawn below_speed <meters_per_second>
```

`heading` is in degrees, using Minecraft's yaw-style direction.

## Datapack Item Tag

Items in this tag ragdoll players when used to hit them:

```text
sable_player_ragdoll:ragdoll_on_hit
```

Example datapack file:

```json
{
  "replace": false,
  "values": [
    "minecraft:stick"
  ]
}
```

Path:

```text
data/sable_player_ragdoll/tags/item/ragdoll_on_hit.json
```

## Public API

Addon mods can depend on this mod jar and call the public methods in
`dev.leo.sableplayerragdoll.api.RagdollAPI`.

Main API entry points:

```java
RagdollAPI.launch(player, velocity);
RagdollAPI.launch(player, velocity, despawnConditions);
RagdollAPI.launch(player, velocity, launchOptions);
RagdollAPI.spawnPlayerless(level, position, headingDegrees);
RagdollAPI.spawnPlayerless(level, position, headingDegrees, velocity);
RagdollAPI.spawnPlayerless(level, position, headingDegrees, profile, velocity);
RagdollAPI.activeSession(player);
RagdollAPI.isRagdolled(player);
RagdollAPI.isRagdollSubLevel(subLevelId);
RagdollAPI.isRagdollSubLevel(subLevel);
```

Despawning helpers are available through:

```java
DespawnCondition.afterTicks(ticks);
DespawnCondition.belowSpeed(metersPerSecond);
DespawnCondition.belowSpeedAfterTicks(metersPerSecond, minTicks);
DespawnCondition.never();
DespawnCondition.all(...);
DespawnCondition.any(...);
```

Player launches can be customized per call with `RagdollLaunchOptions`:

```java
RagdollLaunchOptions options = RagdollLaunchOptions.builder()
   .autoSeat(false)
   .despawnConditions(List.of(DespawnCondition.afterTicks(80)))
   .build();

RagdollAPI.launch(player, velocityMetersPerSecond, options);
```

### Per-limb pose and joint control

`RagdollLimbOptions` lets you set the spawn pose and joint stiffness/damping for
individual limbs. All fields are optional, unset limbs use the built-in defaults,
so you only need to specify what you want to override.

```java
RagdollLimbOptions limbs = RagdollLimbOptions.builder()
   .limb(BodyPart.LEFT_ARM,  RagdollLimbConfig.builder().rotation(0, 0, 90))
   .limb(BodyPart.RIGHT_ARM, RagdollLimbConfig.builder().rotation(0, 0, -90))
   .limb(BodyPart.HEAD,      RagdollLimbConfig.builder().stiffness(120).damping(10))
   .build();

RagdollAPI.launch(player, velocity,
   RagdollLaunchOptions.builder().limbs(limbs).build());
```

`rotation(pitchDegrees, yawDegrees, rollDegrees)` sets the limb's rest angle. Individual axes can be set separately with
`.pitch(d)`, `.yaw(d)`, and `.roll(d)`.

The same API is available for dummies:

```java
RagdollAPI.spawnPlayerless(level, position, headingDegrees, profile, velocity,
   despawnRule, limbs);
```

`RagdollKeybindExample` in the source is a worked end-to-end example showing an
on-foot pose and an elytra pose built with this API.

Playerless ragdolls use `PlayerlessDespawnRule`:

```java
PlayerlessDespawnRule.defaultRule();
PlayerlessDespawnRule.never();
PlayerlessDespawnRule.afterTicks(ticks);
PlayerlessDespawnRule.belowSpeed(metersPerSecond);
```

Addon hooks are posted on the NeoForge game event bus:

```java
RagdollStartEvent
RagdollEndEvent
```

`RagdollStartEvent` fires before a player ragdoll is assembled. It is
cancellable, and listeners can replace the launch velocity.

`RagdollEndEvent` fires after a player exits a ragdoll. It exposes the player,
the exit velocity inherited from the ragdoll, and a reason.

`isRagdollSubLevel` lets other mods check whether a given sub-level (or its UUID)
belongs to a ragdoll.

The API covers spawning, despawning, per-limb pose/joint control, basic session
queries, and sub-level identification. It does not currently expose direct force
application to an already active ragdoll or per-body-part inventory injection.

## License

All rights reserved. Do not redistribute.

See [LICENSE](LICENSE) for the full license text.
