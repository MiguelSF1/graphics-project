#version 330

in vec2 textCoords;

out vec4 fragColor;


uniform sampler2D particleText;

void main(void){


	fragColor = texture(particleText, textCoords);

}