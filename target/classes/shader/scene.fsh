# version 330

const int CASCADE_NUM = 3;

in vec3 worldCoord;
in vec2 outTextureCoord;
in vec3 vertexPos;
in vec3 vertexNormal;
in vec4 lightViewVertexPos[CASCADE_NUM];
in float ambientOcclusion;
in float faceOcclusion;

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

uniform int selected;
uniform vec3 selectedBlock;
uniform sampler2D texture_sampler;
uniform sampler2DShadow shadowMap_0;
uniform sampler2DShadow shadowMap_1;
uniform sampler2DShadow shadowMap_2;
uniform Material material;
uniform DirectionalLight directionalLight;
uniform float specularPower;
uniform vec3 ambientLight;
uniform float cascadeFarPlanes[CASCADE_NUM];
uniform float fogDensity;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

vec2 poissonDisk[16] = vec2[](
    vec2( -0.94201624, -0.39906216 ),
    vec2( 0.94558609, -0.76890725 ),
    vec2( -0.094184101, -0.92938870 ),
    vec2( 0.34495938, 0.29387760 ),
    vec2( -0.91588581, 0.45771432 ),
    vec2( -0.81544232, -0.87912464 ),
    vec2( -0.38277543, 0.27676845 ),
    vec2( 0.97484398, 0.75648379 ),
    vec2( 0.44323325, -0.97511554 ),
    vec2( 0.53742981, -0.47373420 ),
    vec2( -0.26496911, -0.41893023 ),
    vec2( 0.79197514, 0.19090188 ),
    vec2( -0.24188840, 0.99706507 ),
    vec2( -0.81409955, 0.91437590 ),
    vec2( 0.19984126, 0.78641367 ),
    vec2( 0.14383161, -0.14100790 )
);

float random(vec3 seed, int i){
    vec4 seed4 = vec4(seed,i);
    float dot_product = dot(seed4, vec4(12.9898, 78.233, 45.164, 94.673));
    return fract(sin(dot_product) * 43758.5453);
}

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

float calcShadow(vec4 position, int j) {
    if (directionalLight.intensity < 1e-4) return 0;
    vec4 shadowCoord = position * 0.5 + 0.5;
    float result = 0.0;
    const float SAMPLE = 8;
    for (int i = 0; i < SAMPLE; ++i) {
        int index = int(16.0 * random(floor(position.xyz * 1000.0), i)) % 16;
        if (j == 0)
            result += (1.0 / SAMPLE) * texture(shadowMap_0, vec3(shadowCoord.xy + poissonDisk[index] / 5000.0, shadowCoord.z - 1e-3));
        else if (j == 1)
            result += (1.0 / SAMPLE) * texture(shadowMap_1, vec3(shadowCoord.xy + poissonDisk[index] / 5000.0, shadowCoord.z - 1e-3));
        else if (j == 2)
            result += (1.0 / SAMPLE) * texture(shadowMap_2, vec3(shadowCoord.xy + poissonDisk[index] / 5000.0, shadowCoord.z - 1e-3));
    }
    return result;
}

vec4 fog(vec4 color, vec4 fogColor, float depth, float density) {
    const float e = 2.71828182845904523536028747135266249;
    float f = pow(e, -pow(depth * density, 2));
    return mix(fogColor, color, f);
}

bool check(vec3 sourcePos, vec3 targetPos) {
    // this checks whether source postion is the edge of targetPos
    float eps = 0.0075;
    return
    (abs(sourcePos.x - targetPos.x    ) < eps && abs(sourcePos.y - targetPos.y    ) < eps && targetPos.z <= sourcePos.z && sourcePos.z <= targetPos.z + 1) ||
    (abs(sourcePos.x - targetPos.x    ) < eps && abs(sourcePos.y - targetPos.y - 1) < eps && targetPos.z <= sourcePos.z && sourcePos.z <= targetPos.z + 1) ||
    (abs(sourcePos.x - targetPos.x - 1) < eps && abs(sourcePos.y - targetPos.y    ) < eps && targetPos.z <= sourcePos.z && sourcePos.z <= targetPos.z + 1) ||
    (abs(sourcePos.x - targetPos.x - 1) < eps && abs(sourcePos.y - targetPos.y - 1) < eps && targetPos.z <= sourcePos.z && sourcePos.z <= targetPos.z + 1) ||

    (abs(sourcePos.x - targetPos.x    ) < eps && abs(sourcePos.z - targetPos.z    ) < eps && targetPos.y <= sourcePos.y && sourcePos.y <= targetPos.y + 1) ||
    (abs(sourcePos.x - targetPos.x    ) < eps && abs(sourcePos.z - targetPos.z - 1) < eps && targetPos.y <= sourcePos.y && sourcePos.y <= targetPos.y + 1) ||
    (abs(sourcePos.x - targetPos.x - 1) < eps && abs(sourcePos.z - targetPos.z    ) < eps && targetPos.y <= sourcePos.y && sourcePos.y <= targetPos.y + 1) ||
    (abs(sourcePos.x - targetPos.x - 1) < eps && abs(sourcePos.z - targetPos.z - 1) < eps && targetPos.y <= sourcePos.y && sourcePos.y <= targetPos.y + 1) ||

    (abs(sourcePos.y - targetPos.y    ) < eps && abs(sourcePos.z - targetPos.z    ) < eps && targetPos.x <= sourcePos.x && sourcePos.x <= targetPos.x + 1) ||
    (abs(sourcePos.y - targetPos.y    ) < eps && abs(sourcePos.z - targetPos.z - 1) < eps && targetPos.x <= sourcePos.x && sourcePos.x <= targetPos.x + 1) ||
    (abs(sourcePos.y - targetPos.y - 1) < eps && abs(sourcePos.z - targetPos.z    ) < eps && targetPos.x <= sourcePos.x && sourcePos.x <= targetPos.x + 1) ||
    (abs(sourcePos.y - targetPos.y - 1) < eps && abs(sourcePos.z - targetPos.z - 1) < eps && targetPos.x <= sourcePos.x && sourcePos.x <= targetPos.x + 1);
}

void main() {
    setupColours(material, outTextureCoord);
    float alpha = ambientC.a;

    vec4 diffuseSpecular = calcDirectionalLight(directionalLight, vertexPos, vertexNormal) * 0.8;

    int i;
    for (i = 0; i < CASCADE_NUM && abs(vertexPos.z) >= cascadeFarPlanes[i]; ++i);
    float shadow = calcShadow(lightViewVertexPos[i], i);

    fragColor = mix(clamp(ambientC * vec4(vec3(ambientOcclusion), 1) * vec4(ambientLight, 1) + diffuseSpecular * shadow, 0, 1), vec4(0, 0, 0, 1), faceOcclusion);
    fragColor = fog(fragColor, vec4(directionalLight.colour * 0.8, 1), length(vertexPos), fogDensity);
    if (selected == 1 && check(worldCoord, selectedBlock)) {
        fragColor = vec4(1, 1, 1, 2) - fragColor;
    }
    fragColor.a = alpha;
}
