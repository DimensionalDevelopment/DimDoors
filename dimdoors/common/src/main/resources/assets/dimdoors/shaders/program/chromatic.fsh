#version 150 core

in vec2 texCoord;
in vec2 oneTexel;
out vec4 fragColor;

uniform sampler2D DiffuseSampler;
uniform sampler2D ChromaticSampler;
uniform vec2 RedOffset;
uniform vec2 GreenOffset;
uniform vec2 BlueOffset;

vec4 color(vec2 offset) {
    return texture(ChromaticSampler, texCoord + offset * oneTexel);
}

void main() {
    vec4 world = texture(DiffuseSampler, texCoord);
    vec4 rs = color(RedOffset);
    vec4 gs = color(GreenOffset);
    vec4 bs = color(BlueOffset);

    float r = mix(world.r, rs.r, step(0.001, rs.a));
    float g = mix(world.g, gs.g, step(0.001, gs.a));
    float b = mix(world.b, bs.b, step(0.001, bs.a));

    fragColor = vec4(r, g, b, 1.0);
}