#version 150
const vec3 white = vec3(1, 1, 1);
const vec3 black = vec3(0, 0, 0);

uniform sampler2D ChromaticSampler;
uniform sampler2D ChromaticDepthSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec3 color = texture(ChromaticSampler, texCoord).rgb;
    if (color == black) discard;

    gl_FragDepth = texture(ChromaticDepthSampler, texCoord).r;
    fragColor = vec4(color == white ? black : color, 1.0);
}
