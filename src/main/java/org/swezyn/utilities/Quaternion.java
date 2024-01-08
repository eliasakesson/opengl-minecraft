package org.swezyn.utilities;

public class Quaternion {
    public double x, y, z, w;

    public Quaternion(){
        x = 0;
        y = 0;
        z = 0;
        w = 1;
    }

    public Quaternion(double _x, double _y, double _z, double _w){
        x = _x;
        y = _y;
        z = _z;
        w = _w;
    }

    public Quaternion(Vector3 axis) {
        double xRad = Math.toRadians(axis.x);
        double yRad = Math.toRadians(axis.y);
        double zRad = Math.toRadians(axis.z);

        double cy = Math.cos(yRad * 0.5);
        double sy = Math.sin(yRad * 0.5);
        double cp = Math.cos(zRad * 0.5);
        double sp = Math.sin(zRad * 0.5);
        double cr = Math.cos(xRad * 0.5);
        double sr = Math.sin(xRad * 0.5);

        x = cy * cp * sr - sy * sp * cr;
        y = sy * cp * sr + cy * sp * cr;
        z = sy * cp * cr - cy * sp * sr;
        w = cy * cp * cr + sy * sp * sr;
    }

    public Vector3 toForwardVector() {
        double x = 2 * (this.x * this.z + this.w * this.y);
        double y = 2 * (this.y * this.z - this.w * this.x);
        double z = 1 - 2 * (this.x * this.x + this.y * this.y);

        double pitch = Math.toDegrees(Math.atan2(y, Math.sqrt(x * x + z * z)));
        double yaw = Math.toDegrees(Math.atan2(x, z));

        return new Vector3(pitch, yaw, 0).normalize();
    }

    public Vector3 toRightVector() {
        double x = 1 - 2 * (this.y * this.y + this.z * this.z);
        double y = 2 * (this.x * this.y + this.w * this.z);
        double z = 2 * (this.x * this.z - this.w * this.y);

        double roll = Math.toDegrees(Math.atan2(this.y, this.x));
        double pitch = Math.toDegrees(Math.atan2(-z, Math.sqrt(x * x + y * y)));

        return new Vector3(pitch, 0, roll).normalize();
    }

    public Quaternion multiply(Quaternion other){
        double _w = w * other.w - x * other.x - y * other.y - z * other.z;
        double _x = x * other.w + w * other.x + y * other.z - z * other.y;
        double _y = y * other.w + w * other.y + z * other.x - x * other.z;
        double _z = z * other.w + w * other.z + x * other.y - y * other.x;

        return new Quaternion(_x, _y, _z, _w);
    }

    public Quaternion conjugate() {
    	return new Quaternion(-x, -y, -z, w);
    }

    public float[] getMatrix(){
        double[] matrix = new double[16];

        matrix[0] = 1.0 - 2.0 * (y * y + z * z);
        matrix[1] = 2.0 * (x * y - z * w);
        matrix[2] = 2.0 * (x * z + y * w);
        matrix[3] = 0.0;

        matrix[4] = 2.0 * (x * y + z * w);
        matrix[5] = 1.0 - 2.0 * (x * x + z * z);
        matrix[6] = 2.0 * (y * z - x * w);
        matrix[7] = 0.0;

        matrix[8] = 2.0 * (x * z - y * w);
        matrix[9] = 2.0 * (y * z + x * w);
        matrix[10] = 1.0 - 2.0 * (x * x + y * y);
        matrix[11] = 0.0;

        matrix[12] = 0.0;
        matrix[13] = 0.0;
        matrix[14] = 0.0;
        matrix[15] = 1.0;

        float[] floatMatrix = new float[16];
        for (int i = 0; i < 16; i++) {
            floatMatrix[i] = (float) matrix[i];
        }

        return floatMatrix;
    }

    @Override
    public String toString() {
        return "(" + w + ", " + x + ", " + y + ", " + z + ")";
    }
}
