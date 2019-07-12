# version 330

in vec2 outTextureCoord;
in vec3 outVertexPos;
in vec3 outVertexNormal;
out vec4 fragColor;

struct DirectionalLight {
    vec3 colour;
    vec3 direction;
    float intensity;
};

uniform sampler2D texture_sampler;
uniform DirectionalLight directionalLight;
uniform vec3 ambientLight;

vec4 ambientC;
vec4 diffuseC;

vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal) {
    vec4 diffuseColour = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColour = diffuseC * vec4(light_colour, 1.0) * light_intensity * diffuseFactor;

    return diffuseColour;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal);
}

void main() {
    fragColor = texture(texture_sampler, outTextureCoord);
    ambientC = fragColor;
    diffuseC = fragColor;
    vec4 diffuseSpecular = calcDirectionalLight(directionalLight, outVertexPos, outVertexNormal) * 0.8;
    fragColor = mix(clamp(ambientC * vec4(ambientLight, 1) + diffuseSpecular, 0, 1), vec4(0, 0, 0, 1), 0.3);
}
