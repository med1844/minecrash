# version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in vec3 vertexNormalVector;
layout(location = 3) in float inAdjacentFaceCount;

out vec3 worldCoord;
out vec2 outTextureCoord;
out vec3 vertexNormal;
out vec3 vertexPos;
out vec4 lightViewVertexPos;
out float ambientOcclusion;
out float faceOcclusion;

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 modelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;

void main() {
    worldCoord = (modelMatrix * vec4(position, 1)).xyz;
    vec4 modelViewPos = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * modelViewPos;
//    projectionMatrix;
    outTextureCoord = textureCoord;
    vertexNormal = normalize(modelViewMatrix * vec4(vertexNormalVector, 0.0)).xyz;
    vertexPos = modelViewPos.xyz;
    lightViewVertexPos = orthoProjectionMatrix * modelLightViewMatrix * vec4(position, 1.0);
    ambientOcclusion = inAdjacentFaceCount;
    faceOcclusion = 0.0f;
    if (vertexNormalVector == vec3(-1, 0, 0) || vertexNormalVector == vec3(1, 0, 0)) faceOcclusion = 0.15f;
    else if (vertexNormalVector == vec3(0, 0, -1) || vertexNormalVector == vec3(0, 0, 1)) faceOcclusion = 0.3f;
    else if (vertexNormalVector == vec3(0, -1, 0)) faceOcclusion = 0.45f;
//    gl_Position = lightViewVertexPos;
}