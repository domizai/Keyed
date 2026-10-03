// Adds a soft glow around everything brighter than threshold: samples a disc
// around each pixel in a spiral, keeps only the bright part, and weights it
// by distance. Two discs, a tight one and a wide one, give a core and a halo.
#ifdef GL_ES
precision mediump float;
#endif
uniform sampler2D texture;
uniform vec2 texOffset;
uniform float threshold;
uniform float strength;
uniform float radius;
#define NUM 384
varying vec4 vertTexCoord;
vec3 disc(vec2 uv, float r) {
    vec3 sum = vec3(0.0);
    float total = 0.0;
    for (int i = 0; i < NUM; i++) {
        float d = sqrt((float(i) + 0.5) / float(NUM));
        float a = float(i) * 2.39996;
        vec3 c = texture2D(texture, uv + vec2(cos(a), sin(a)) * d * r * texOffset).rgb;
        float w = exp(-3.0 * d * d);
        sum += max(c - threshold, 0.0) / (1.0 - threshold) * w;
        total += w;
    }
    return sum / total;
}
void main() {
    vec2 uv = vertTexCoord.st;
    vec3 base = texture2D(texture, uv).rgb;
    vec3 glow = disc(uv, radius) * 0.6 + disc(uv, radius * 4.0) * 0.4;
    gl_FragColor = vec4(base + glow * strength, 1.0);
}
