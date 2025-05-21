#version 330

layout (location=0) in vec2 position;

out vec2 textCoords;
out vec2 textCoords1;
out vec2 textCoords2;
out float blendF;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

uniform vec2 texOffset1;
uniform vec2 texOffset2;
uniform vec2 texCoordInfo;

void main(void) {

    textCoords = position + vec2(0.5, 0.5);
    textCoords.y = 1 - textCoords.y;
	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);

}