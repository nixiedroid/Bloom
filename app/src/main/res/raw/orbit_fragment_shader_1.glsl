/**
 * Fragment shader 1 of 2
 *
 * Simplified version with no square-drawing logic
 * (Square is drawn on top by second shader)
 *
 */
precision highp float;

uniform float width;  // dimension of quad in pixels
uniform float height;

uniform vec2 dotPos;
uniform float dotRadius;
uniform vec4 dotColor;

uniform vec2 circlePos;
uniform float circleRadius;
uniform float circleGradientTheta;

uniform vec4 bgColor;
uniform vec4 dotCircleOverlapColor;
uniform vec4 allOverlapColor;

uniform sampler2D textureUnit;

varying vec2 vTextureCoordinates;


// See http://theorangeduck.com/page/avoiding-shader-conditionals
float when_gt(float x, float y) {
  return max(sign(x - y), 0.0);
}
float when_le(float x, float y) {
  return 1.0 - when_gt(x, y);
}
float when_lt(float x, float y) {
  return max(sign(y - x), 0.0);
}
float when_ge(float x, float y) {
  return 1.0 - when_lt(x, y);
}
float when_eq(float x, float y) {
  return 1.0 - abs(sign(x - y));
}

float all2(float a, float b) {
  return when_gt(a + b, 1.99);  // unverified
}
float none2(float a, float b) {
  return when_eq(a + b, 0.0);
}
float onlyA2(float a, float b) {
  return a * (1.0-b);
}


float map(float value, float inMin, float inMax, float outMin, float outMax) {
  return outMin + (outMax - outMin) * (value - inMin) / (inMax - inMin);
}

float norm(float value, float minimum, float maximum) {  // normalize (not in the vector sense)
  return (value - minimum) / (maximum - minimum);
}


// find closest point to P on line segment AB
// http://stackoverflow.com/a/30448467
vec2 closest_point_on_line_segment(vec2 P, vec2 A, vec2 B) {
    vec2 AP = P - A;
    vec2 AB = B - A;
    float f = dot(AP, AB) / dot(AB, AB);
    f = clamp( f, 0.0, 1.0);
    return AP  -  AB * f;
}


vec4 calc(float x, float y) {

  // dot
  float dx = x - dotPos.x;
  float dy = y - dotPos.y;
  float distance = sqrt(dx * dx + dy * dy);
  float isDot = when_le(distance, dotRadius);

  // circle
  dx = x - circlePos.x;
  dy = y - circlePos.y;
  distance = sqrt(dx * dx + dy * dy);

  // calc circleColor from texture
  float rotatedx = cos(-circleGradientTheta) * dx  -  sin(-circleGradientTheta) * dy;
	float rotatedy = sin(-circleGradientTheta) * dx  +  cos(-circleGradientTheta) * dy;
  float s = map(rotatedx, -circleRadius, circleRadius, 0.0, 1.0);
  float t = map(rotatedy, -circleRadius, circleRadius, 1.0, 0.0);  // flipped
  // note how we're supplying our own texture coordinates for the texture lookup
  vec4 circleColor = texture2D(textureUnit, vec2(s, t));

  float isCircle = when_le(distance, circleRadius);

  vec4 col = vec4(0.0);

  // Note how exactly 1 of these conditions will be true:
  col += none2(isDot, isCircle) * bgColor;
  col += onlyA2(isDot, isCircle) * dotColor;  // this _does_ occur during screenoff-to-lockscreen transition
  col += onlyA2(isCircle, isDot) * circleColor;
  col += all2(isDot, isCircle) * dotCircleOverlapColor;

  return col;
}


void main() {

  float x = vTextureCoordinates.s * width;
  float y = (1. - vTextureCoordinates.t) * height;

  gl_FragColor = calc(x, y);
}
