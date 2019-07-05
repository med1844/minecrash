# version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in vec3 vertexNormalVector;

out vec2 outTextureCoord;
out vec3 vertexNormal;
out vec3 vertexPos;
out vec3 originVertexNormal;
out vec4 lightViewVertexPos;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 modelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;

void main() {
    vec4 modelViewPos = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * modelViewPos;
//    projectionMatrix;
    outTextureCoord = textureCoord;
    vertexNormal = normalize(modelViewMatrix * vec4(vertexNormalVector, 0.0)).xyz;
    vertexPos = modelViewPos.xyz;
    originVertexNormal = vertexNormalVector;
    lightViewVertexPos = orthoProjectionMatrix * modelLightViewMatrix * vec4(position, 1.0);
//    gl_Position = lightViewVertexPos;
}