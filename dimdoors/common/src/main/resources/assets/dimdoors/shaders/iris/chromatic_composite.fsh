#version 330 compatibility

uniform sampler2D ChromaticSampler;
uniform sampler2D ChromaticDepthSampler;

in vec2 texcoord;

/* RENDERTARGETS: 0 */
layout(location = 0) out vec4 outColor0;

void main() {
    vec4 crack = texture(ChromaticSampler, texcoord);
    if (crack.a == 0.0) discard;

    crack.a = (crack.r + crack.g + crack.b) / 3.0;
    crack.rgb -= min(crack.r, min(crack.g, crack.b));

    gl_FragDepth = texture(ChromaticDepthSampler, texcoord).r;
    outColor0 = crack;
}
