package com.pedroedrasousa.engine;

public class Vec3 {
		
	public float x, y, z;
	
	public Vec3() {
		x = y = z = 0.0f;
	}
	
	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3(float[] v) {
		this.x = v[0];
		this.y = v[1];
		this.z = v[2];
	}
	
	public Vec3(Vec3 a) {
		this.x = a.x;
		this.y = a.y;
		this.z = a.z;
	}
	
	public void assign(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	} 
	
	public void assign(float[] v) {
		this.x = v[0];
		this.y = v[1];
		this.z = v[2];
	}
	
	public void assign(Vec3 a) {
		this.x = a.x;
		this.y = a.y;
		this.z = a.z;
	}
	
	public void setZero() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}
	
	public void add(Vec3 a) {
		this.x += a.x;
		this.y += a.y;
		this.z += a.z;
	}
	
	public void sub(Vec3 a) {
		this.x -= a.x;
		this.y -= a.y;
		this.z -= a.z;
	}
	
	public void scale(float factor) {
		this.x *= factor;
		this.y *= factor;
		this.z *= factor;
	}
	
	public void normalize() {
		float length = (float)Math.sqrt(x * x + y * y + z * z);
		
		if (length != 0) {
		float invLength = 1.0f / length;
			x *= invLength;
			y *= invLength;
			z *= invLength;
		}
	}
	
	// Calculate unit normal vector to the triangle given by points a, b and c.
	// a->b->c goes right-handed.
	public void normal(Vec3 a, Vec3 b, Vec3 c) {
		x = (b.y - a.y) * (c.z - a.z) - (b.z - a.z) * (c.y - a.y);
		y = (b.z - a.z) * (c.x - a.x) - (b.x - a.x) * (c.z - a.z);
		z = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
	
		this.normalize();  
	}
	
	public float length() {
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
	
	@Override
	public String toString() {
	    return x + ", " + y + ", " + z;
	}
	
	@Override
	public boolean equals(Object obj) {
	   if (obj == null) {
	      return false;
	   }

	   if (this.getClass() != obj.getClass()) {
	      return false;
	   }

	   if (this.x != ((Vec3)obj).x || this.y != ((Vec3)obj).y || this.z != ((Vec3)obj).z) {
	      return false;
	   }

	   return true;
	}
	
	public static Vec3 add(Vec3 a, Vec3 b) {
		return new Vec3(a.x + b.x, a.y + b.y, a.z + b.z);
	}
	
	public static Vec3 sub(Vec3 a, Vec3 b) {
		return new Vec3(a.x - b.x, a.y - b.y, a.z - b.z);
	}
	
	public static Vec3 mul(Vec3 a, float f) {
		return new Vec3(a.x * f, a.y * f, a.z * f);
	}
	
	public static float dot(Vec3 a, Vec3 b) {
		return(a.x * b.x + a.y * b.y + a.z * b.z);
	}
	
	public static Vec3 cross(Vec3 a, Vec3 b) {
		Vec3 res = new Vec3();
		
		res.x = a.y * b.z - a.z * b.y;
		res.y = a.z * b.x - a.x * b.z;
		res.z = a.x * b.y - a.y * b.x;

		return res;
	}
}
