# Shaderpack programs

Some Dimensional Doors effects can be replaced by a shaderpack. Each one looks for programs named `<namespace>_<node>_<kind>` in the pack's shader folder (per-dimension folders work too). If a program is missing, our own version is used.

Rift cracks are the node `dimdoors:chromatic`, drawn at the translucent stage (after deferred, before translucent geometry):

- `dimdoors_chromatic_geometry`: draws the crack into its own buffer. Write it like a `gbuffers_*` program.
- `dimdoors_chromatic_composite`: draws that buffer into the scene. Write it like a `composite*` program and output with `RENDERTARGETS`. Scene depth is attached and depth-tested, so set `gl_FragDepth` from `ChromaticDepthSampler`. `blend.<program>` is honored, otherwise it alpha-blends.

Extra inputs for these programs, on top of the usual Iris uniforms and samplers:

- `ChromaticSampler`: crack color, alpha > 0 where the crack is
- `ChromaticDepthSampler`: crack depth
- `ChromaticAngle` (`float`): radians, rotation of the axis the channels oscillate along
- `ChromaticPhase` (`float`): radians, oscillation phase; channels are offset by 120° from it
- `ChromaticSeparation` (`float`): oscillation amplitude as a fraction of the rift's size
- `UV1.x` (geometry only): rift size × 150

Our fallbacks are in `assets/dimdoors/shaders/iris/chromatic_geometry.*` and `assets/dimdoors/shaders/iris/chromatic_composite.*` if you want a starting point.

Shadow-pass programs aren't supported yet.
