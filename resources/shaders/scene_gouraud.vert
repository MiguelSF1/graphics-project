#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord;

out vec4 vertexColor;
out vec2 outTexCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

const int MAX_POINT_LIGHTS = 5;
const float SPECULAR_POWER = 15;

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float reflectance;
};

struct AmbientLight {
    float factor;
    vec3 color;
};

struct DirLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight {
    vec3 position;
    vec3 color;
    float intensity;
    Attenuation att;
};

uniform Material material;
uniform AmbientLight ambientLight;
uniform DirLight dirLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];

vec4 calcLightColor(vec4 diffuse, vec4 specular, vec3 lightColor, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal) {
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    vec4 diffuseColor = diffuse * vec4(lightColor, 1.0) * light_intensity * diffuseFactor;

    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir, normal));
    float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, SPECULAR_POWER);
    vec4 specColor = specular * light_intensity * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return diffuseColor + specColor;
}

vec4 calcAmbient(AmbientLight ambientLight, vec4 ambient) {
    return vec4(ambientLight.factor * ambientLight.color, 1.0) * ambient;
}

vec4 calcDirLight(vec4 diffuse, vec4 specular, DirLight light, vec3 position, vec3 normal) {
    return calcLightColor(diffuse, specular, light.color, light.intensity, position, normalize(light.direction), normal);
}

vec4 calcPointLight(vec4 diffuse, vec4 specular, PointLight light, vec3 position, vec3 normal) {
    vec3 light_direction = light.position - position;
    vec3 to_light_dir = normalize(light_direction);
    vec4 light_color = calcLightColor(diffuse, specular, light.color, light.intensity, position, to_light_dir, normal);

    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
    return light_color / attenuationInv;
}

void main() {
    mat4 modelViewMatrix = viewMatrix * modelMatrix;
    vec4 mvPosition = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * mvPosition;

    vec3 fragPos = mvPosition.xyz;
    vec3 norm = normalize((modelViewMatrix * vec4(normal, 0.0)).xyz);

    vec4 text_color = vec4(1.0); // fallback color if texture not used
    vec4 ambient = calcAmbient(ambientLight, text_color + material.ambient);
    vec4 diffuse = text_color + material.diffuse;
    vec4 specular = text_color + material.specular;

    vec4 lighting = calcDirLight(diffuse, specular, dirLight, fragPos, norm);
    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (pointLights[i].intensity > 0) {
            lighting += calcPointLight(diffuse, specular, pointLights[i], fragPos, norm);
        }
    }

    vertexColor = ambient + lighting;
    outTexCoord = texCoord;
}
