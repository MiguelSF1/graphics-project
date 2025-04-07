#version 460 core
in vec4 fColor;

out vec4 color;

uniform float uTime;

void main()
{
    float avg = (fColor.r + fColor.g + fColor.b) / 3;
    color = vec4(avg, avg, avg, 1);
}