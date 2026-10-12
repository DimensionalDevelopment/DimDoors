#version 150

uniform sampler2D ChromaticSampler;
uniform sampler2D ChromaticDepthSampler;

uniform sampler2D TranslucentSampler;
uniform sampler2D TranslucentDepthSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

vec4 overTranslucent(vec4 crack, float crackDepth) {
    if (crackDepth >= texture(TranslucentDepthSampler, texCoord).r) return crack;

    vec4 water = texture(TranslucentSampler, texCoord);
    float alpha = 1.0 - (1.0 - crack.a) * (1.0 - water.a);
    return vec4((crack.rgb * crack.a + (1.0 - crack.a) * water.rgb) / alpha, alpha);
}

void main() {
    vec4 crack = texture(ChromaticSampler, texCoord);
    if (crack.a == 0.0) discard;

    crack.a = (crack.r + crack.g + crack.b) / 3.0;
    crack.rgb -= min(crack.r, min(crack.g, crack.b));

    float crackDepth = texture(ChromaticDepthSampler, texCoord).r;

    gl_FragDepth = crackDepth;
    fragColor = overTranslucent(crack, crackDepth);
}
