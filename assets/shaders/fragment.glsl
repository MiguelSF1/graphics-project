#version 460 core
in vec4 fColor;
in vec2 fTexCoords;

out vec4 color;

uniform float uTime;
uniform sampler2D TEX_SAMPLER;

void main()
{
    color = texture(TEX_SAMPLER, fTexCoords);
}