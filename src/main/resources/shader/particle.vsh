# version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in vec3 vertexNormal;

out vec2 outTextureCoord;
out vec3 outVertexNormal;
out vec3 outVertexPos;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main() {
    vec4 modelViewPos = modelViewMatrix * vec4(position, 1);
    gl_Position = projectionMatrix * modelViewPos;
    outTextureCoord = textureCoord;
    outVertexNormal = normalize(modelViewMatrix * vec4(vertexNormal, 0.0)).xyz;
    outVertexPos = modelViewPos.xyz;
}
