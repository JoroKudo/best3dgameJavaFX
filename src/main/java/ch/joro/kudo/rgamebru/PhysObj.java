package ch.joro.kudo.rgamebru;

import org.ode4j.ode.*;
import org.ode4j.math.DQuaternionC;
import org.ode4j.math.DVector3C;

import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.shape.Shape3D;
import javafx.scene.paint.Material;

public class PhysObj {
	
	// this base class ties all the physics and visual stuff together
	// before anything that extends this can be used initialise must be
	// called

	public DBody body;
	DGeom geom;
	Pivot pivot;
	
	Shape3D shape;
	
	private static DWorld world;
	protected static DSpace space;
	private static Scene scene;
	private static Group root;
	private static boolean initialised = false;
	
	private final Quat tmpQuat = new Quat();
	
	public static void initialise(DWorld wld, DSpace spc, Scene scn, Group rt) {
		world=wld;
		space=spc;
		scene=scn;
		root=rt;
		initialised = true;
	}
	
	PhysObj() {
		if (!initialised) throw new RuntimeException("PhysObj uninitialised"); 
		
		body = OdeHelper.createBody(world);
		pivot = new Pivot();
		root.getChildren().addAll(pivot);
	}
		
	public void update() {
		final DQuaternionC rot = body.getQuaternion();
		final DVector3C pos = body.getPosition();
		tmpQuat.x = rot.get1();
		tmpQuat.y = rot.get2();
		tmpQuat.z = rot.get3();
		tmpQuat.w = -rot.get0();
		
		pivot.set(tmpQuat);
		pivot.setPosition(pos.get0(),pos.get1(),pos.get2());
	}
	
	public void setMaterial(Material mat) {
		shape.setMaterial(mat);
	}
}
