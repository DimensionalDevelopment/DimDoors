#version 150

uniform sampler2D ChromaticSampler;
uniform sampler2D ChromaticDepthSampler;
uniform vec2 RedOffset;
uniform vec2 GreenOffset;
uniform vec2 BlueOffset;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

vec4 color(vec2 offset) {
    return texture(ChromaticSampler, texCoord + offset * oneTexel);
}

float depth(vec2 offset, float alpha) {
    return alpha > 0.0 ? texture(ChromaticDepthSampler, texCoord + offset * oneTexel).r : 1.0;
}

void main() {
    vec4 rs = color(RedOffset);
    vec4 gs = color(GreenOffset);
    vec4 bs = color(BlueOffset);

    float alpha = max(rs.a, max(gs.a, bs.a));
    if (alpha == 0.0) discard;

    gl_FragDepth = min(depth(RedOffset, rs.a), min(depth(GreenOffset, gs.a), depth(BlueOffset, bs.a)));
    fragColor = vec4(rs.r, gs.g, bs.b, alpha);
}
