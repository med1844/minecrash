# version 330

in vec2 outTextureCoord;
in vec3 vertexPos;
in vec3 vertexNormal;
in vec3 originVertexNormal;
in vec4 lightViewVertexPos;

out vec4 fragColor;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight {
    vec3 colour;
    vec3 direction;
    float intensity;
};

uniform sampler2D texture_sampler;
uniform sampler2D shadowMap;
uniform Material material;
uniform DirectionalLight directionalLight;
uniform float specularPower;
uniform vec3 ambientLight;
uniform mat4 orthoProjectionMatrix;
uniform mat4 lightViewMatrix;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColours(Material mat, vec2 textCoord) {
    if (material.hasTexture == 1) {
        ambientC = texture(texture_sampler, textCoord);
        diffuseC = ambientC * mat.diffuse;
        specularC = ambientC * mat.specular;
    } else {
        ambientC = mat.ambient;
        diffuseC = mat.diffuse;
        specularC = mat.specular;
    }
}

vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal) {
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColour = diffuseC * vec4(light_colour, 1.0) * light_intensity * diffuseFactor;

    // Specular Light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir, normal));
    float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColour = specularC * light_intensity * specularFactor * material.reflectance * vec4(light_colour, 1.0);

    return (diffuseColour * material.diffuse + specColour * material.specular);
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal);
}

float calcShadow(vec4 position) {
    vec3 projCoords = position.xyz;
    // Transform from screen coordinates to texture coordinates
    projCoords = projCoords * 0.5 + 0.5;
    float bias = 0.05;

    float shadowFactor = 0.0;
    vec2 inc = 1.0 / textureSize(shadowMap, 0);
    for(int row = -1; row <= 1; ++row) {
        for(int col = -1; col <= 1; ++col) {
            float textDepth = texture(shadowMap, projCoords.xy + vec2(row, col) * inc).r;
            shadowFactor += projCoords.z - bias > textDepth ? 1.0 : 0.0;
        }
    }
    shadowFactor /= 9.0;

    if(projCoords.z > 1.0) {
        shadowFactor = 1.0;
    }

    return 1 - shadowFactor;
}

void main() {
    setupColours(material, outTextureCoord);

    vec4 diffuseSpecular = calcDirectionalLight(directionalLight, vertexPos, vertexNormal) * 0.8;

    float mixRatio = 0.0f;
    if (originVertexNormal == vec3(-1, 0, 0) || originVertexNormal == vec3(1, 0, 0))
        mixRatio = 0.15f;
    else if (originVertexNormal == vec3(0, 0, -1) || originVertexNormal == vec3(0, 0, 1))
        mixRatio = 0.3f;
    else if (originVertexNormal == vec3(0, -1, 0)) mixRatio = 0.45f;

    float shadow = calcShadow(lightViewVertexPos);
    fragColor = mix(clamp(ambientC * vec4(ambientLight, 1) + diffuseSpecular * shadow, 0, 1), vec4(0, 0, 0, 1), mixRatio);
//    fragColor = vec4(vec3(texture(shadowMap, (lightViewVertexPos * 0.5 + 0.5).xy).r), 1);
//    fragColor = vec4(vec3(shadow), 1);
}
