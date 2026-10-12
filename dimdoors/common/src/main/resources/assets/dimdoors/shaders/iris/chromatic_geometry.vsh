#version 330 compatibility

in ivec2 UV1;

out vec3 vPosition;
out vec3 vNormal;
out float vSize;

void main() {
    vPosition = gl_Vertex.xyz;
    vNormal = gl_Normal;
    vSize = float(UV1.x) / 150.0;
    gl_Position = vec4(gl_Vertex.xyz, 1.0);
}
