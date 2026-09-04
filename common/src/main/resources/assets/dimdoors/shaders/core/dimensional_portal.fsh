// Copied from the end portal shader
// h e l p
#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;

uniform float GameTime;
uniform int EndPortalLayers;

uniform int[16] Colors;

in vec4 texProj0;

const vec3 BASE_COLOR = vec3(0.4627, 0.3569, 0.6196);
const int layers = 16;

const mat4 SCALE_TRANSLATE = mat4(
    0.5, 0.0, 0.0, 0.25,
    0.0, 0.5, 0.0, 0.25,
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0
);

mat4 end_portal_layer(float layer) {
    mat4 translate = mat4(
        1.0, 0.0, 0.0, 17.0 / layer,
        0.0, 1.0, 0.0, (2.0 + layer / 1.5) * (GameTime * 1.5),
        0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 1.0
    );

    mat2 rotate = mat2_rotate_z(radians((layer * layer * 4321.0 + layer * 9.0) * 2.0));

    mat2 scale = mat2(4.5 - layer / 4.0);

    return mat4(scale * rotate) * translate * SCALE_TRANSLATE;
}

vec3 getColor(int layer) {
    int color = Colors[layer];

    return vec3(
            ((color >> 16) & 0xff) * 0.003921569f,
            ((color >> 8) & 0xff) * 0.003921569f,
            (color & 0xff) * 0.003921569f
    );
}

out vec4 fragColor;

void main() {
    vec3 color = vec3(0, 0, 0);
    for (int i = 0; i < layers; i++) {
        color += textureProj(Sampler0, texProj0 * end_portal_layer(float(i + 1))).rgb * getColor(i);
    }
    fragColor = vec4(color, 1.0);
}
