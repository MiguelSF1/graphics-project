#version 330

const int MAX_POINT_LIGHTS = 5;
const float SPECULAR_POWER = 15; // shininess dependent on viewing angles

in vec3 outPosition;
in vec3 outNormal;
in vec2 outTexCoord;

out vec4 fragColor;

uniform sampler2D txtSampler;

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float reflectance; // shine intensity
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
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform DirLight dirLight;

vec4 calcLightColor(vec4 diffuse, vec4 specular, vec3 lightColor, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal) {
    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0); // how directly light hits surface
    vec4 diffuseColor = diffuse * vec4(lightColor, 1.0) * light_intensity * diffuseFactor;

    // Specular Light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir, normal)); // reflection of light on normal
    float specularFactor = max(dot(camera_direction, reflected_light), 0.0); // how close the view direction matches the light reflection direction
    specularFactor = pow(specularFactor, SPECULAR_POWER); // more intense if camera is pointing to light cone
    vec4 specColor = specular * light_intensity  * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return (diffuseColor + specColor);
}

vec4 calcAmbient(AmbientLight ambientLight, vec4 ambient) {
    return vec4(ambientLight.factor * ambientLight.color, 1) * ambient;
}

vec4 calcDirLight(vec4 diffuse, vec4 specular, DirLight light, vec3 position, vec3 normal) {
    return calcLightColor(diffuse, specular, light.color, light.intensity, position, normalize(light.direction), normal);
}

vec4 calcPointLight(vec4 diffuse, vec4 specular, PointLight light, vec3 position, vec3 normal) {
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 light_color = calcLightColor(diffuse, specular, light.color, light.intensity, position, to_light_dir, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = (light.att.constant) + (light.att.linear * distance) + (light.att.exponent * distance * distance);
    return light_color / attenuationInv;
}

void main() {
    vec4 text_color = texture(txtSampler, outTexCoord);
    vec4 ambient = calcAmbient(ambientLight, text_color + material.ambient); // light everywhere like background light
    vec4 diffuse = text_color + material.diffuse;                            // when facing light source its brighter
    vec4 specular = text_color + material.specular;                          // how light reflects on surface

    vec4 diffuseSpecularComp = calcDirLight(diffuse, specular, dirLight, outPosition, outNormal);

    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (pointLights[i].intensity > 0) {
            diffuseSpecularComp += calcPointLight(diffuse, specular, pointLights[i], outPosition, outNormal);
        }
    }

    fragColor = ambient + diffuseSpecularComp;
}

