#version 150

in vec3 Position;
in vec2 UV0;

uniform vec2 ScreenSize;

out vec2 texCoord;

void main() {
    gl_Position = vec4(Position.xy * 2.0 - 1.0, 0.0, 1.0);
    texCoord = UV0;
}
