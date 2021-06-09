#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float GameTime;
uniform int EndPortalLayers;

in vec2 UV0;
in vec2 UV2;

out vec4 fragColor;

float Noise21 (vec2 p, float ta, float tb) {
    return fract(sin(p.x*ta+p.y*tb)*5678.);
}

void main() {
    vec2 uv = vec2(UV0) / vec2(1920, 1080);

    float t = GameTime + 123.;
    float ta = t * .654321;
    float tb = t * (ta * .123456);

    float c = Noise21(uv, ta, tb);
    vec3 col = vec3(c);

    fragColor = vec4(col, 1.0);
}