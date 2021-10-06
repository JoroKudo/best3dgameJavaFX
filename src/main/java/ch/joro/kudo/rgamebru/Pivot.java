
package ch.joro.kudo.rgamebru;

import com.sun.javafx.geom.Vec3d;
import javafx.scene.Group;
import javafx.scene.transform.Affine;
import javafx.scene.transform.MatrixType;
import javafx.geometry.Point3D; // horrid immutable yucky blob

public class Pivot extends Group {
	
	// TODO port other math routines from my code base

	private final double[] idt={1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1};
    private final Vec3d tmpV3 = new Vec3d(); // holds temp value for return

	protected Affine matrix = new Affine(idt,MatrixType.MT_3D_4x4,0);

	
	public Pivot() {
		super();
		getTransforms().setAll(matrix);
	}
	
	public Pivot(String id) {
		this();
		setId(id);
	}
	
	public Pivot( Pivot orig) {
		this();
		copy(orig);
	}
	
	public Pivot(double x, double y, double z) {
		this();
		matrix.setTx(x);
		matrix.setTy(y);
		matrix.setTz(z);
	}
	
	public Pivot(String id, double x, double y, double z) {
		this(x,y,z);
		setId(id);
	}
	
	public void copy(Pivot orig) {
		matrix.setMxx(orig.matrix.getMxx()); matrix.setMxy(orig.matrix.getMxy()); matrix.setMxz(orig.matrix.getMxz()); matrix.setTx(orig.matrix.getTx());
		matrix.setMyx(orig.matrix.getMyx()); matrix.setMyy(orig.matrix.getMyy()); matrix.setMyz(orig.matrix.getMyz()); matrix.setTy(orig.matrix.getTy());		
		matrix.setMzx(orig.matrix.getMzx()); matrix.setMzy(orig.matrix.getMzy()); matrix.setMzz(orig.matrix.getMzz()); matrix.setTz(orig.matrix.getTz());
	}
	
	public void setPosition(double x, double y, double z) {
		matrix.setTx(x);
		matrix.setTy(y);
		matrix.setTz(z);
	}

	
	public void setPosition(Vec3d v) {
		matrix.setTx(v.x);
		matrix.setTy(v.y);
		matrix.setTz(v.z);
	}
	
	public Vec3d getPosition() {
		return new Vec3d(matrix.getTx(),matrix.getTy(),matrix.getTz());
	}

	// set to eular rotation retaining translation
	public void setEularRotation(double rx, double ry, double rz) {
		rx=Math.toRadians(rx);
		ry=Math.toRadians(ry);
		rz=Math.toRadians(rz);
		double cx = Math.cos(rx);
        double cy = Math.cos(ry);
        double cz = Math.cos(rz);
        double sx = Math.sin(rx);
        double sy = Math.sin(ry);
        double sz = Math.sin(rz);
		matrix.setMxx(cy*cz);	matrix.setMxy((sx * sy * cz) + (cx * sz));	matrix.setMxz(-(cx * sy * cz) + (sx * sz));
		matrix.setMyx(-cy*sz);	matrix.setMyy(-(sx * sy * sz) + (cx * cz));	matrix.setMyz((cx * sy * sz) + (sx * cz));
		matrix.setMzx(sy);		matrix.setMzy(-sx*cy);						matrix.setMzz(cx*cy);
	}


    public void lookAt(Vec3d centre, Vec3d up) {

        final Vec3d f = new Vec3d(), s = new Vec3d(), u = new Vec3d();
        final Vec3d t = new Vec3d(), eye = new Vec3d();
        
        eye.set( matrix.getTx(), matrix.getTy(), matrix.getTz());

        f.set(eye);
        f.sub(centre);
        f.normalize();

        up.normalize();

        t.set(f);
        s.cross(t,up);
        s.normalize();

        t.set(s);
        u.cross(f,t);
        u.normalize();
      
        matrix.setMxx( -s.x);	matrix.setMxy( u.x);	matrix.setMxz( -f.x);
        matrix.setMyx( -s.y);    matrix.setMyy( u.y);    matrix.setMyz( -f.y);
        matrix.setMzx( -s.z);    matrix.setMzy( u.z);    matrix.setMzz( -f.z);
    }
        
    public Vec3d rotateVector(Vec3d vec) { // TODO benchmark doing math here v's deltaTransform
		Point3D tmpP3 = matrix.deltaTransform(vec.x, vec.y, vec.z);
		tmpV3.x = tmpP3.getX();
		tmpV3.y = tmpP3.getY();
		tmpV3.z = tmpP3.getZ();
		return tmpV3;
	}
	
	public void identity() {
		matrix.setToTransform(idt,MatrixType.MT_3D_4x4,0);
	}
	
	// use a quaternion to turn the matrix into a rotational matrix
    // TODO test after port! (could have col/row transposed!)
    public void set(Quat q) {
        final double xx = q.x * q.x;
        final double xy = q.x * q.y;
        final double xz = q.x * q.z;
        final double xw = q.x * q.w;
        final double yy = q.y * q.y;
        final double yz = q.y * q.z;
        final double yw = q.y * q.w;
        final double zz = q.z * q.z;
        final double zw = q.z * q.w;

        matrix.setMxx( 1 - 2 * (yy + zz));
        matrix.setMxy( 2 * (xy + zw));
        matrix.setMxz( 2 * (xz - yw));


        matrix.setMyx( 2 * (xy - zw));
        matrix.setMyy( 1 - 2 * (xx + zz));
        matrix.setMyz( 2 * (yz + xw));


        matrix.setMzx( 2 * (xz + yw));
        matrix.setMzy( 2 * (yz - xw));
        matrix.setMzz( 1 - 2 * (xx + yy));

    }
    
    public void rotateX(double angle) {
		matrix.appendRotation(angle, 0,0,0, 1,0,0);		
	}
    
    public void rotateY(double angle) {
		matrix.appendRotation(angle, 0,0,0, 0,1,0);		
	}
    
    public void rotateZ(double angle) {
		matrix.appendRotation(angle, 0,0,0, 0,0,1);		
	}
	
	/** this conversion uses conventions as described on page:
	*   http://www.euclideanspace.com/maths/geometry/rotations/euler/index.htm
	*   Coordinate System: right hand
	*   Positive angle: right hand
	*   Order of euler angles: heading first, then attitude, then bank
	*   matrix row column ordering:
	*   [m00 m01 m02] xx xy xz
	*   [m10 m11 m12] yx yy yz
	*   [m20 m21 m22] zx zy zz */
	public Vec3d getEular() {
		if (matrix.getMyx() > Double.MIN_VALUE) { // north pole
			tmpV3.x = Math.toDegrees(Math.atan2(matrix.getMxz(),matrix.getMzz())); // m02,m22
			tmpV3.y = Math.toDegrees(Math.PI/2); 
			tmpV3.z = 0;
			return tmpV3;
		}

		if (matrix.getMyx() < -Double.MIN_VALUE) { // south pole
			tmpV3.x = Math.toDegrees(Math.atan2(matrix.getMxz(),matrix.getMzz())); // m02,m22
			tmpV3.y = Math.toDegrees(-Math.PI/2);
			tmpV3.z = 0;
			return tmpV3;
		}

		tmpV3.x = Math.toDegrees(Math.atan2(-matrix.getMzx(),matrix.getMxx())); // -m20,m00
		tmpV3.y = Math.toDegrees(Math.atan2(-matrix.getMyz(),matrix.getMyy())); // -m12,m11
		tmpV3.z = Math.toDegrees(Math.asin(matrix.getMyx())); // m10
		
		return tmpV3;
	}
	
}
