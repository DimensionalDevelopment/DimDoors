#version 330 compatibility

flat in vec3 channel;
flat in float core;

layout(location = 0) out vec4 outColor0;

void main() {
    outColor0 = vec4(channel, 0.0);
}
