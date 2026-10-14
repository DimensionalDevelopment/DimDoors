#version 330 compatibility
const vec3 white = vec3(1, 1, 1);
const vec3 black = vec3(0, 0, 0);

uniform sampler2D ChromaticSampler;
uniform sampler2D ChromaticDepthSampler;

in vec2 texcoord;

/* RENDERTARGETS: 0 */
layout(location = 0) out vec4 outColor0;

void main() {
    vec3 color = texture(ChromaticSampler, texcoord).rgb;
    if (color == black) discard;

    gl_FragDepth = texture(ChromaticDepthSampler, texcoord).r;
    outColor0 = vec4(color == white ? black : color, 1.0);
}
