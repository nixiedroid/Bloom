uniform mat4 uMatrix;
attribute vec4 aPosition;
attribute vec4 aColor;
attribute vec2 aTextureCoordinates;
varying vec2 vTextureCoordinates;
varying vec4 vColor;
varying vec2 vDitherCoords;

void main() {
  gl_Position = uMatrix * aPosition;
  vTextureCoordinates = aTextureCoordinates;
  vColor = aColor;
  vDitherCoords = aPosition.xy / 4.0;
}
