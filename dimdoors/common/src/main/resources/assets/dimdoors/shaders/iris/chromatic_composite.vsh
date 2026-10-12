#version 330 compatibility

out vec2 texcoord;

void main() {
    gl_Position = vec4(gl_Vertex.xy * 2.0 - 1.0, 0.0, 1.0);
    texcoord = gl_MultiTexCoord0.xy;
}
