# Refurbished Cams (Forge 1.20.1)
Addon for **MrCrayfish's Furniture Mod: Refurbished** (required, >= 1.0.12 + its Framework dependency).

- Craft/place **Security Cameras** (they look where you look).
- Open the **Computer** from Refurbished -> run the **Cameras** program -> it lists cameras within 500 blocks
  (config `detectionRadius`) -> click one to see its live view.
  - Look: drag with left mouse / arrows / WASD. Switch camera: Q / E, PageUp / PageDown or the < > buttons. Exit: ESC.
  - Gamepad (works with or without Controllable): list -> D-pad up/down select, X open, Y refresh.
    View -> right stick look, LB / RB or D-pad left/right switch camera, B exit.

## Build the .jar
- Linux/macOS/Git Bash: `./build.sh`  |  Windows: `build.bat`
- Result: `build/libs/refurbishedcams-1.20.1-1.0.0.jar`

## GitHub Actions
Every push to `main` builds the jar (Actions -> run -> Artifacts).
Publish a Release: `git tag v1.0.0 && git push origin v1.0.0`.
