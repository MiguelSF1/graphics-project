#version 330

in vec4 vertexColor;
in vec2 outTexCoord;

out vec4 fragColor;

uniform sampler2D txtSampler;

void main() {
    vec4 texColor = texture(txtSampler, outTexCoord);
    fragColor = vertexColor * texColor;
}
