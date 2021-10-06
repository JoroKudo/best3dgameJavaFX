package ch.joro.kudo.rgamebru;

public class Quat {

    public double x,y,z,w;

    Quat() {
        identity();
    }
    
    public void identity() {
		x=0;y=0;z=0;w=1;
	}

    public void rotationXYZ(double xi, double yi, double zi) {

        final double hx = xi / 2f;
        final double hy = yi / 2f;
        final double hz = zi / 2f;

        final double shx = Math.sin(hx);
        final double chx = Math.cos(hx);

        final double shy = Math.sin(hy);
        final double chy = Math.cos(hy);
        final double shz = Math.sin(hz);
        final double chz = Math.cos(hz);

        final double chy_shx = chy * shx;
        final double shy_chx = shy * chx;
        final double chy_chx = chy * chx;
        final double shy_shx = shy * shx;

        x = (chy_shx * chz) + (shy_chx * shz); // cos(yaw/2) * sin(pitch/2) * cos(roll/2) + sin(yaw/2) * cos(pitch/2) * sin(roll/2)
        y = (shy_chx * chz) - (chy_shx * shz); // sin(yaw/2) * cos(pitch/2) * cos(roll/2) - cos(yaw/2) * sin(pitch/2) * sin(roll/2)
        z = (chy_chx * shz) - (shy_shx * chz); // cos(yaw/2) * cos(pitch/2) * sin(roll/2) - sin(yaw/2) * sin(pitch/2) * cos(roll/2)
        w = (chy_chx * chz) + (shy_shx * shz); // cos(yaw/2) * cos(pitch/2) * cos(roll/2) + sin(yaw/2) * sin(pitch/2) * sin(roll/2)

        normalise(); // needed???
    }




    private final double PI2 = (Math.PI * 2f);
    
    public void axisAngle(double xa, double ya, double za, double a) {

        double d = Math.sqrt(xa * xa + ya * ya + za * za);
        if (d==0) {
            x=0;y=0;z=0;w=1;
            return;
        }
        d=1f/d;
        double la = a < 0 ? PI2 - (-a % PI2) : a % PI2;
        double ls = Math.sin(la/2);
        double lc = Math.cos(la/2);
        x = d * xa * ls;
        y = d * ya * ls;
        z = d * za * ls;
        w = lc;
        normalise();
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public void normalise() {
        double il = 1f/length();
        x=x * il;
        y=y * il;
        z=z * il;
        w=w * il;
    }

    public void multiply(Quat q2) {

        double tx = x * q2.w + w * q2.x + y * q2.z - z * q2.y;
        double ty = y * q2.w + w * q2.y + z * q2.x - x * q2.z;
        double tz = z * q2.w + w * q2.z + x * q2.y - y * q2.x;
        double tw = w * q2.w - x * q2.x - y * q2.y - z * q2.z;

        x = tx; y = ty; z = tz; w = tw; 
    }
}
