#version 150 core

in vec2 texCoord;
in vec2 oneTexel;
out vec4 fragColor;

uniform sampler2D DiffuseSampler;
uniform sampler2D ChromaticSampler;
uniform float Seperation;

void main() {
    vec2 direction = normalize(texCoord - 0.5);
    vec2 offset = vec2(1.0, 0.0) * (Seperation * oneTexel);

    vec4 world = texture(DiffuseSampler, texCoord);
    vec4 rs = texture(ChromaticSampler, texCoord + offset);
    vec4 gs = texture(ChromaticSampler, texCoord);
    vec4 bs = texture(ChromaticSampler, texCoord - offset);

    float r = mix(world.r, rs.r, step(0.001, rs.a));
    float g = mix(world.g, gs.g, step(0.001, gs.a));
    float b = mix(world.b, bs.b, step(0.001, bs.a));

    fragColor = vec4(r, g, b, 1.0);
}