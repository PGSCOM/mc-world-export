# Neoforge 1.21.1 Migration Summary

This document summarizes the migration of mc-world-export from Minecraft 1.20.4 (Fabric) to Minecraft 1.21.1 (Neoforge).

## Overview

- **From**: Minecraft 1.20.4 with Fabric Loader
- **To**: Minecraft 1.21.1 with Neoforge 21.1.0
- **Replay Mod**: Switched from ReplayMod (Fabric) to ReforgedPlay (Neoforge port)

## Key Changes

### 1. Build System
- Gradle plugin changed from `fabric-loom` to `net.neoforged.moddev`
- Gradle version updated from 8.7 to 8.10.2
- Java version updated from 17 to 21

### 2. Dependencies
- **Mod Loader**: Fabric → Neoforge 21.1.0
- **Replay Mod**: ReplayMod (Modrinth) → ReforgedPlay (CurseForge)
  - Project ID: 1018692
  - File ID: 6942967
  - Version: 1.21.1-1.0.3
  - URL: https://www.curseforge.com/minecraft/mc-mods/reforgedplay-mod
- **MixinExtras**: 0.1.1 → 0.4.1

### 3. Mod Metadata
- Replaced `fabric.mod.json` with `META-INF/neoforge.mods.toml`
- Created `META-INF/mods.json` for mixin configuration
- Created `META-INF/accesstransformer.cfg`
- Updated mixin compatibility level: JAVA_16 → JAVA_17

### 4. Code Changes

#### Main Mod Class
- Changed from `ClientModInitializer` to `@Mod` annotation
- Entity registration now uses `DeferredRegister`
- Event system switched from Fabric to Neoforge event bus

#### Event System
- Replaced Fabric's `Event`/`EventFactory` with custom `SimpleEvent` pattern
- Updated `ClientBlockPlaceCallback` and `TimelineUpdateCallback`

#### Rendering
- `CameraPathRenderer`: Uses `RenderLevelStageEvent` instead of `WorldRenderContext`
- `CameraEntityRenderer`: Updated for 1.21.1 rendering API
- Updated all rendering class names: `MatrixStack` → `PoseStack`, etc.

#### Entity Classes
- `AnimatedCameraEntity`: Updated all entity methods for 1.21.1
  - `initDataTracker()` → `defineSynchedData()`
  - `readCustomDataFromNbt()` → `readAdditionalSaveData()`
  - `writeCustomDataToNbt()` → `addAdditionalSaveData()`
  - And many more method/field name changes

#### Mixins
- Updated all mixins for Neoforge and 1.21.1
- Changed `@Environment(EnvType.CLIENT)` → `@OnlyIn(Dist.CLIENT)`
- Updated target class names (e.g., `ClientWorld` → `ClientLevel`)

## Files Modified

### Build Configuration
- `build.gradle`
- `gradle.properties`
- `gradle/wrapper/gradle-wrapper.properties`
- `settings.gradle`

### Mod Metadata
- `src/main/resources/META-INF/neoforge.mods.toml` (new)
- `src/main/resources/META-INF/mods.json` (new)
- `src/main/resources/META-INF/accesstransformer.cfg` (new)
- `src/main/resources/worldexport.mixins.json`
- `src/main/resources/debugger.mixins.json`

### Java Source Files
- `src/main/java/org/scaffoldeditor/worldexport/ReplayExportMod.java`
- `src/main/java/org/scaffoldeditor/worldexport/ClientBlockPlaceCallback.java`
- `src/main/java/org/scaffoldeditor/worldexport/replaymod/TimelineUpdateCallback.java`
- `src/main/java/org/scaffoldeditor/worldexport/replaymod/AnimatedCameraEntity.java`
- `src/main/java/org/scaffoldeditor/worldexport/replaymod/render/CameraEntityRenderer.java`
- `src/main/java/org/scaffoldeditor/worldexport/replaymod/render/CameraPathRenderer.java`
- `src/main/java/org/scaffoldeditor/worldexport/mixins/ClientWorldMixin.java`
- `src/main/java/com/igrium/replay_debugger/ReplayDebugger.java`
- `src/main/java/com/igrium/replay_debugger/mixins/TitleScreenMixin.java`

## Building

```bash
./gradlew build
```

**Requirements:**
- Java 21
- Network access to:
  - maven.neoforged.net
  - www.cursemaven.com
  - jitpack.io

## Testing

After building, test the following:

1. **Mod Loading**: Verify mod loads in Minecraft 1.21.1 with Neoforge
2. **ReforgedPlay Integration**: Check that ReforgedPlay is detected
3. **Replay Recording**: Test recording with ReforgedPlay
4. **Camera Animations**: Create and render camera paths
5. **World Export**: Export a replay file
6. **Blender Integration**: Import exported file in Blender

## Known Limitations

- Build testing blocked by network restrictions in development environment
- Some Minecraft 1.21.1 API changes may require additional updates
- ReforgedPlay compatibility needs runtime verification
- Additional files may need updates based on compilation errors

## Next Steps

1. Build the project and fix any remaining compilation errors
2. Test all functionality with ReforgedPlay
3. Update documentation with Neoforge-specific instructions
4. Create release for Minecraft 1.21.1
5. Update mod distribution platforms

## Compatibility Notes

- **Minecraft Version**: 1.21.1 only
- **Mod Loader**: Neoforge 21.1.0+
- **Required Mods**: ReforgedPlay 1.21.1-1.0.3+
- **Java Version**: 21+
- **Breaking Changes**: This version is NOT compatible with Fabric

## Support

For issues with:
- **This mod**: Check GitHub issues
- **ReforgedPlay**: Visit https://www.curseforge.com/minecraft/mc-mods/reforgedplay-mod
- **Neoforge**: Visit https://docs.neoforged.net/

## Credits

Original migration work completed on November 3, 2024.
