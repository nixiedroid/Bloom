precision highp float;

uniform float width;  // dimension of quad in pixels
uniform float height;

varying vec2 vTextureCoordinates;

uniform vec2 mainCenter[4];
uniform float mainRadius[4];
uniform vec4 mainColor[4];

uniform vec2 shadowCenter[4];
uniform float shadowOuterRadius[4];
uniform float shadowInnerRadius[4];
uniform vec4 shadowColor[4];

uniform vec2 highlightCenter[4];
uniform float highlightOuterRadius[4];
uniform float highlightInnerRadius[4];
uniform vec4 highlightColor[4];

uniform vec4 outerColor[4];


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
float onlyA(float a, float b) {
  return a * (1.0-b);
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


void main() {

  // current position in pixel units
  float x = vTextureCoordinates.s * width;
  float y = vTextureCoordinates.t * height;


  float dx, dy, distanceMainCenter, distanceShadowCenter, distanceHighlightCenter;
  bool isMain, isHighlight;


  dx = x - mainCenter[3].x;
  dy = y - mainCenter[3].y;
  distanceMainCenter = sqrt(dx * dx + dy * dy);
  isMain = distanceMainCenter < mainRadius[3];

  if (isMain) {

    dx = x - shadowCenter[3].x;
    dy = y - shadowCenter[3].y;
    distanceShadowCenter = sqrt(dx * dx + dy * dy);

    float val = norm(distanceShadowCenter,  shadowInnerRadius[3], shadowOuterRadius[3]);
    val = clamp(val, 0.0, 1.0);
    gl_FragColor = mix(mainColor[3], shadowColor[3], val);
    return;

  } else {

    dx = x - highlightCenter[3].x;
    dy = y - highlightCenter[3].y;
    distanceHighlightCenter = sqrt(dx * dx + dy * dy);
    isHighlight = distanceHighlightCenter < highlightOuterRadius[3];

    if (isHighlight) {
      float val = norm(distanceHighlightCenter,  highlightInnerRadius[3], highlightOuterRadius[3]);
      val = clamp(val, 0.0, 1.0);
      gl_FragColor = mix(highlightColor[3], outerColor[3], val);
      return;
    }
  }


  dx = x - mainCenter[2].x;
  dy = y - mainCenter[2].y;
  distanceMainCenter = sqrt(dx * dx + dy * dy);
  isMain = distanceMainCenter < mainRadius[2];

  if (isMain) {

    dx = x - shadowCenter[2].x;
    dy = y - shadowCenter[2].y;
    distanceShadowCenter = sqrt(dx * dx + dy * dy);

    float val = norm(distanceShadowCenter,  shadowInnerRadius[2], shadowOuterRadius[2]);
    val = clamp(val, 0.0, 1.0);
    gl_FragColor = mix(mainColor[2], shadowColor[2], val);
    return;

  } else {

    dx = x - highlightCenter[2].x;
    dy = y - highlightCenter[2].y;
    distanceHighlightCenter = sqrt(dx * dx + dy * dy);
    isHighlight = distanceHighlightCenter < highlightOuterRadius[2];

    if (isHighlight) {
      float val = norm(distanceHighlightCenter,  highlightInnerRadius[2], highlightOuterRadius[2]);
      val = clamp(val, 0.0, 1.0);
      gl_FragColor = mix(highlightColor[2], outerColor[2], val);
      return;
    }
  }


  dx = x - mainCenter[1].x;
  dy = y - mainCenter[1].y;
  distanceMainCenter = sqrt(dx * dx + dy * dy);
  isMain = distanceMainCenter < mainRadius[1];

  if (isMain) {

    dx = x - shadowCenter[1].x;
    dy = y - shadowCenter[1].y;
    distanceShadowCenter = sqrt(dx * dx + dy * dy);

    float val = norm(distanceShadowCenter,  shadowInnerRadius[1], shadowOuterRadius[1]);
    val = clamp(val, 0.0, 1.0);
    gl_FragColor = mix(mainColor[1], shadowColor[1], val);
    return;

  } else {

    dx = x - highlightCenter[1].x;
    dy = y - highlightCenter[1].y;
    distanceHighlightCenter = sqrt(dx * dx + dy * dy);
    isHighlight = distanceHighlightCenter < highlightOuterRadius[1];

    if (isHighlight) {
      float val = norm(distanceHighlightCenter,  highlightInnerRadius[1], highlightOuterRadius[1]);
      val = clamp(val, 0.0, 1.0);
      gl_FragColor = mix(highlightColor[1], outerColor[1], val);
      return;
    }
  }


  dx = x - mainCenter[0].x;
  dy = y - mainCenter[0].y;
  distanceMainCenter = sqrt(dx * dx + dy * dy);
  isMain = distanceMainCenter < mainRadius[0];

  if (isMain) {

    dx = x - shadowCenter[0].x;
    dy = y - shadowCenter[0].y;
    distanceShadowCenter = sqrt(dx * dx + dy * dy);

    float val = norm(distanceShadowCenter,  shadowInnerRadius[0], shadowOuterRadius[0]);
    val = clamp(val, 0.0, 1.0);
    gl_FragColor = mix(mainColor[0], shadowColor[0], val);
    return;

  } else {

    // this part loses the last conditional

    dx = x - highlightCenter[0].x;
    dy = y - highlightCenter[0].y;
    distanceHighlightCenter = sqrt(dx * dx + dy * dy);
    isHighlight = distanceHighlightCenter < highlightOuterRadius[0];

    float val = norm(distanceHighlightCenter,  highlightInnerRadius[0], highlightOuterRadius[0]);
    val = clamp(val, 0.0, 1.0);
    gl_FragColor = mix(highlightColor[0], outerColor[0], val);
  }
}
