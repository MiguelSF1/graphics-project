#version 330 core

in vec2 fragTexCoord;
uniform sampler2D texSampler;
out vec4 fragColor;

void main() {
    vec4 tex = texture(texSampler, fragTexCoord);
    if (tex.a < 0.1)
        discard;
    fragColor = tex;
}
