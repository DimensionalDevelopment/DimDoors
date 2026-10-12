#version 150

layout(triangles) in;
layout(triangle_strip, max_vertices = 9) out;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float ChromaticAngle;
uniform float ChromaticPhase;
uniform float ChromaticSeparation;

in float vDistance[];
in vec4 vColor[];
in vec2 vTexCoord0[];
in vec2 vTexCoord1[];
in vec3 vPosition[];
in vec3 vNormal[];
in float vSize[];

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out vec2 texCoord1;
flat out vec3 channel;

void main() {
    vec3 n = cross(vPosition[1] - vPosition[0], vPosition[2] - vPosition[0]);
    if (dot(n, n) == 0.0) return;
    n = normalize(n) * sign(dot(n, vNormal[0]));

    vec3 tangent = normalize(cross(vec3(0.0, 1.0, 0.0), n));
    vec3 bitangent = cross(n, tangent);

    for (int c = 0; c < 3; c++) {
        float offset = ChromaticSeparation * vSize[0] * sin(ChromaticPhase + float(c) * 2.0943951);
        vec3 shift = offset * (cos(ChromaticAngle) * tangent + sin(ChromaticAngle) * bitangent);

        for (int i = 0; i < 3; i++) {
            gl_Position = ProjMat * ModelViewMat * vec4(vPosition[i] + shift, 1.0);
            vertexDistance = vDistance[i];
            vertexColor = vColor[i];
            texCoord0 = vTexCoord0[i];
            texCoord1 = vTexCoord1[i];
            channel = vec3(c == 0, c == 1, c == 2);
            EmitVertex();
        }
        EndPrimitive();
    }
}
