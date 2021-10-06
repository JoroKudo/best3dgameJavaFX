package ch.joro.kudo.rgamebru;

import org.ode4j.ode.*;

import javafx.scene.shape.Cylinder;
import javafx.scene.paint.Color;
import javafx.geometry.Point3D;

public class PhysCylinder extends PhysObj {
	
	// each extended shape only need provide a constructor with
	// the shape specific stuff all the generic stuff is looked
	// after in the base class
	
	private final Point3D odeCorrection = new Point3D(1,0,0);
	
	PhysCylinder() {
		this( 0.5, 1);
	}
	
	PhysCylinder(double r, double h) {
		
		super();
		
		geom = OdeHelper.createCylinder(space,r,h);
		geom.setBody(body);
		
		DMass mass = OdeHelper.createMass();
		mass.setCylinder (1, 2, r, h); // 2 is Y axis ( TODO why is this changable when you cant define a geom with different length axis
		body.setMass(mass);
		
		shape = new Cylinder(r, h);
		shape.setRotationAxis(odeCorrection);
		shape.setRotate(90);
		
		pivot.getChildren().addAll(shape);
		shape.setMaterial(PhongPhactory.fromColour(Color.RED)); // a default
	}
	


}
