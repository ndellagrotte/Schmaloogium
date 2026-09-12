#version 120

uniform sampler2D colortex0;

void main() {
    vec3 c = texture2D(colortex0, gl_TexCoord[0].st).rgb;
    gl_FragData[0] = vec4(1.0 - c, 1.0);
}
