#version 120

uniform sampler2D colortex0;

void main() {
    gl_FragColor = vec4(texture2D(colortex0, gl_TexCoord[0].st).rgb, 1.0);
}
