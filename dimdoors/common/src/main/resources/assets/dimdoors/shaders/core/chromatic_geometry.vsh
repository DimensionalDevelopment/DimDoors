#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vDistance;
out vec4 vColor;
out vec2 vTexCoord0;
out vec2 vTexCoord1;
out vec3 vPosition;
out vec3 vNormal;
out float vSize;

void main() {
    vPosition = Position;
    vNormal = Normal;

    vDistance = fog_distance(Position, FogShape);
    vColor = minecraft_mix_light(Light0_Direction, Light1_Direction, vec3(0.0), Color) * texelFetch(Sampler2, UV2 / 16, 0);
    vTexCoord0 = UV0;
    vTexCoord1 = vec2(UV1);
    vSize = float(UV1.x) / 150.0;
}
