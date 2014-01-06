uniform mat4	uMVPMatrix;
attribute vec3	aVertPos;

void main()
{
	gl_PointSize = 10.0;
	gl_Position = uMVPMatrix * vec4(aVertPos, 1.0);
}
