#version 330

in vec2 textCoords;

out vec4 fragColor;


uniform sampler2D particleText;
uniform float elapsedTime;

void main(void){
    vec4 texColor = texture(particleText, textCoords);

    float darkening = 1.0 - (elapsedTime/1000)/2;

    fragColor = texColor * darkening;
}