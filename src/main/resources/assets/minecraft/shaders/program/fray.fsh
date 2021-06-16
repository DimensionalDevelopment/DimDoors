#version 150

//make this a uniform when implementing in practice.
//ranges from 0 to 1.
//note: the shader should not be used when the effect strength is 0,
//because this will cause division-by-zero issues inside the shader.
uniform float FrayIntensity;
uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;

in vec2 texcoord;

out vec4 fragColor;

float square(float f) { return f * f; }
vec2  square(vec2  v) { return v * v; }

float smoothify(float f) { return f * f * (f * -2.0 + 3.0); }
vec2  smoothify(vec2  v) { return v * v * (v * -2.0 + 3.0); }

float unmix(float low, float high, float frac) { return (frac - low) / (high - low); }
vec2  unmix(vec2  low, vec2  high, vec2  frac) { return (frac - low) / (high - low); }

float hash11(float p) { //from https://www.shadertoy.com/view/4djSRW
	p = fract(p * 10.31);
	p *= p + 33.33;
	p *= p + p;
	return fract(p);
}

float noise11(float coord, float wrap) {
	float fracCoord = fract(coord);
	vec2 corners;
	corners.x = mod(coord - fracCoord, wrap);
	corners.y = mod(corners.x + 1.0, wrap);
	fracCoord = smoothify(fracCoord);
	return mix(hash11(corners.x), hash11(corners.y), fracCoord);
}

vec2 hash22(vec2 p) { //from https://www.shadertoy.com/view/4djSRW
	vec3 p3 = fract(vec3(p.xyx) * vec3(10.31, 10.30, 9.73));
	p3 += dot(p3, p3.yzx + 33.33);
	return fract((p3.xx + p3.yz) * p3.zy);
}

vec2 noise22(vec2 coord) {
	vec2 fracCoord = fract(coord);
	vec4 corners;
	corners.xy = coord - fracCoord;
	corners.zw = corners.xy + vec2(1.0);
	fracCoord = smoothify(fracCoord);
	return mix(
		mix(
			hash22(corners.xy),
			hash22(corners.xw),
			fracCoord.y
		),
		mix(
			hash22(corners.zy),
			hash22(corners.zw),
			fracCoord.y
		),
		fracCoord.x
	);
}

void main(){
	//-1 to +1 instead of 0 to 1.
	vec2 signedTexcoord = texcoord * 2.0 - 1.0;
	//0 in the center, 1 on the edges.
	//this is not a euclidean distance.
	float centerDistance = sqrt(1.0 - (1.0 - signedTexcoord.x * signedTexcoord.x) * (1.0 - signedTexcoord.y * signedTexcoord.y));

	//offset the texcoord a bit so the lines are more wiggley.
	vec2 offset = noise22(texcoord * 4.0);
	offset += noise22(texcoord * 8.0) * 0.5;
	offset += noise22(texcoord * 16.0) * 0.25;
	offset /= 1.0 + 0.5 + 0.25; //0 to 1
	offset = offset * 2.0 - 1.0; //-1 to +1
	//don't offset the texcoord in the exact middle of the screen,
	//because we want the lines to converge on the center.
	offset *= centerDistance;
	//0.03125 is the amount of wiggleyness to apply.
	vec2 tc = texcoord + offset * 0.03125;

	//texcoord, in polar coordinates, ranging from 0 to 1 instead of -pi to +pi.
	float angle = atan(tc.y - 0.5, tc.x - 0.5) * (0.5 / 3.14159265359) + 0.5;

	//noise used for the lines.
	//this is one-dimensional noise, where the angle is the 
	float noise = noise11(angle * 64.0, 64.0);
	noise += noise11(angle * 128.0, 128.0) * 0.5;
	noise += noise11(angle * 256.0, 256.0) * 0.25;
	noise /= 1.0 + 0.5 + 0.25; //0 to 1
	//4.0 controls the "initial" thickness of the lines.
	//higher numbers will be thinner, and lower numbers will be thicker.
	noise = abs(noise * 2.0 - 1.0) * 4.0;

	//0 at the edges, and... more than 0 at the center.
	float distanceFactor = unmix(1.0, 1.0 - FrayIntensity, centerDistance);
	float finalMultiplier = (noise + distanceFactor) * (1.0 - FrayIntensity);

	fragColor = texture(DiffuseSampler, texcoord);
	fragColor.rgb *= min(finalMultiplier, 1.0);
}