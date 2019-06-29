# version 330

in vec3 bypassColor;
out vec4 fragColor;

void main() {
    fragColor = vec4(bypassColor, 1.0f);
}
