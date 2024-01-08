package org.swezyn.utilities;

public class Vector3 {
    public double x, y, z;

    public Vector3(){
        x = 0;
        y = 0;
        z = 0;
    }
    public Vector3(double _x, double _y, double _z){
        x = _x;
        y = _y;
        z = _y;
    }

    public Vector3(Quaternion quaternion){
        double pitch = 2 * (quaternion.x * quaternion.z + quaternion.w * quaternion.y);
        double yaw = 2 * (quaternion.y * quaternion.z - quaternion.w * quaternion.x);
        double roll = 1 - 2 * (quaternion.x * quaternion.x + quaternion.y * quaternion.y);

        x = Math.toDegrees(pitch);
        y = Math.toDegrees(yaw);
        z = Math.toDegrees(roll);
    }

    public Vector3 add(Vector3 other){
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 multiply(double scalar){
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }

    public Vector3 multiply(Vector3 other){
        return new Vector3(x * other.x, y * other.y, z * other.z);
    }

    public Vector3 normalize(){
        double magnitude = Math.max(Math.sqrt(x * x + y * y + z * z), 0.000001);
        return new Vector3(x / magnitude, y / magnitude, z / magnitude);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
