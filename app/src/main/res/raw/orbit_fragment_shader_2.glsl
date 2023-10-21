/**
 * Fragment shader 2 of 2
 *
 * This contains the 'full logic' for drawing the full composition,
 * and uses multisampling to mitigate aliasing.
 *
 * In practice, this shader is applied only to the area of the rotating square
 * because of the extra expense of multi-sampling.
 */
precision highp float;

uniform float rectLeft;
uniform float rectRight;
uniform float rectTop;
uniform float rectBottom;

uniform vec2 dotPos;
uniform float dotRadius;
uniform vec4 dotColor;

uniform vec2 circlePos;
uniform float circleRadius;
uniform float circleGradientTheta;

uniform vec2 squarePos;
uniform float squareRadius;
uniform float squareTheta;
uniform vec4 squareColor;

uniform vec4 bgColor;
uniform vec4 dotCircleOverlapColor;
uniform vec4 dotSquareOverlapColor;
uniform vec4 squareCircleOverlapColor;
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


// arguments must be 0.0 or 1.0!
float all3(float a, float b, float c) {
  return when_gt(a + b + c, 2.99);  // unverified
}
float none3(float a, float b, float c) {
  return when_eq(a + b + c, 0.0);
}
float onlyA(float a, float b, float c) {
  return a * (1.0-b) * (1.0-c);
}
float onlyAB(float a, float b, float c) {
  return a * b * (1.0-c);
}


float map(float value, float inMin, float inMax, float outMin, float outMax) {
  return outMin + (outMax - outMin) * (value - inMin) / (inMax - inMin);
}

float norm(float value, float minimum, float maximum) {  // normalize (not in the vector sense)
  return (value - minimum) / (maximum - minimum);
}


vec4 calc(float x, float y) {

  // dot
  float dx = x - dotPos.x;
  float dy = y - dotPos.y;
  float distance = sqrt(dx * dx + dy * dy);
  float isDot = when_le(distance, dotRadius);

  // square
  float px = x - squarePos.x;
  float py = y - squarePos.y;
	dx = cos(-squareTheta) * px - sin(-squareTheta) * py;
	dy = sin(-squareTheta) * px + cos(-squareTheta) * py;
  float isSquare = when_ge(dx, -squareRadius) * when_le(dx, squareRadius) *
          when_ge(dy, -squareRadius) * when_le(dy, squareRadius);
  vec4 squareCol = squareColor * isSquare;

  // circle
  dx = x - circlePos.x;
  dy = y - circlePos.y;
  distance = sqrt(dx * dx + dy * dy);

  // calc circleColor from texture
  float rotatedx = cos(-circleGradientTheta) * dx  -  sin(-circleGradientTheta) * dy;
	float rotatedy = sin(-circleGradientTheta) * dx  +  cos(-circleGradientTheta) * dy;
  float s = map(rotatedx, -circleRadius, circleRadius, 0.0, 1.0);
  float t = map(rotatedy, -circleRadius, circleRadius, 1.0, 0.0);  // flipped
  // note how we're supplying our own texture coordinates on the texture unit
  vec4 circleColor = texture2D(textureUnit, vec2(s, t));

  float isCircle = when_le(distance, circleRadius);


  vec4 col = vec4(0.0);

  // Note how exactly 1 of these conditions will be true:

  // 0 hits
  col += none3(isDot, isSquare, isCircle) * bgColor;

  // 1 hit
  col += onlyA(isDot, isSquare, isCircle) * dotColor;  // this _does_ occur during screenoff-to-lockscreen transition
  col += onlyA(isSquare, isDot, isCircle) * squareColor;
  col += onlyA(isCircle, isDot, isSquare) * circleColor;

  // 2 hits
  col += onlyAB(isDot, isCircle, isSquare) * dotCircleOverlapColor;
  col += onlyAB(isDot, isSquare, isCircle) * dotSquareOverlapColor;  // this _does_ occur during screenoff-to-lockscreen transition
  col += onlyAB(isSquare, isCircle, isDot) * squareCircleOverlapColor;

  // 3 hits
  col += all3(isDot, isSquare, isCircle) * allOverlapColor;

  return col;
}


void main() {

  float x = mix(rectLeft, rectRight,  vTextureCoordinates.s);
  float y = mix(rectBottom, rectTop,  vTextureCoordinates.t);  // note, flipped

  // This would be the non-multi-sampled version:
  //  gl_FragColor = calc(x, y);

  // Multi-sample logic:
  // see https://i-msdn.sec.s-msft.com/dynimg/IC554624.png

  // four samples - more than 4x slower in practice (?!)
  // vec4 c1 = calc(x - 0.125, y + 0.375);  // upper-left sample
  // vec4 c2 = calc(x + 0.375, y + 0.125);  // upper-right sample
  // vec4 c3 = calc(x - 0.375, y - 0.125);  // lower-left sample
  // vec4 c4 = calc(x + 0.125, y - 0.375);  // lower-right sample
  // gl_FragColor = (c1 + c2 + c3 + c4) / 4.0;

  // two samples - exactly 2x slower; still makes important improvement
  vec4 c1 = calc(x - 0.25, y + 0.25);  // upper-left sample
  vec4 c2 = calc(x + 0.25, y - 0.25);  // lower-right sample

  // TEMP
  // vec4 c = (c1 + c2) / 2.0;
  // gl_FragColor = vec4(c.r, c.g, c.b + 0.33, 1.0);

  gl_FragColor = (c1 + c2) / 2.0;
}
