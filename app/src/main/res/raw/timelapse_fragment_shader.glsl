precision mediump float;

uniform sampler2D uTextureUnit;
uniform float uAlpha;
uniform float uTime;
varying vec2 vTextureCoordinates;
varying vec4 vColor;
varying vec2 vDitherCoords;

float triangleNoise(highp vec2 n) {
    // triangle noise, in [-1.0..1.0[ range
    n += vec2(0.07 * fract(uTime));
    n  = fract(n * vec2(5.3987, 5.4421));
    n += dot(n.yx, n.xy + vec2(21.5351, 14.3137));

    highp float xy = n.x * n.y;
    // compute in [0..2[ and remap to [-1.0..1.0[
    return fract(xy * 95.4307) + fract(xy * 75.04961) - 1.0;
}

vec4 Dither_TriangleNoise(vec4 rgba) {
    return rgba + triangleNoise(gl_FragCoord.xy * vDitherCoords.xy) / 255.0;
}

void main() {

  // workaround to avoid shimmering on leading edge during unlock anim
  vec2 texCoords = vec2(vTextureCoordinates.x, clamp(vTextureCoordinates.y, 0.008, 0.992));

  // Scale the gradient colors by the alpha value on uTextureUnit.  This provides the rounded
  // blending effect.
  vec4 color = texture2D(uTextureUnit, texCoords).a * vColor * uAlpha;
  // Also, dither to prevent banding.
  gl_FragColor = Dither_TriangleNoise(color);
}
