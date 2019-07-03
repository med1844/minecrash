# version 330

in vec2 outTextureCoord;
in vec3 vertexPos;
in vec3 vertexNormal;
in vec3 originVertexNormal;
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
uniform Material material;
uniform DirectionalLight directionalLight;
uniform float specularPower;
uniform vec3 ambientLight;

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

void main() {
    setupColours(material, outTextureCoord);

    vec4 diffuseSpecular = calcDirectionalLight(directionalLight, vertexPos, vertexNormal) * 0.8;

    float mixRatio = 0.0f;
    if (originVertexNormal == vec3(-1, 0, 0) || originVertexNormal == vec3(1, 0, 0))
        mixRatio = 0.15f;
    else if (originVertexNormal == vec3(0, 0, -1) || originVertexNormal == vec3(0, 0, 1))
        mixRatio = 0.3f;

    fragColor = mix(ambientC * vec4(ambientLight, 1) + diffuseSpecular, vec4(0, 0, 0, 1), mixRatio);
}
