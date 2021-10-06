package ch.joro.kudo.rgamebru;

import org.ode4j.ode.*;

import javafx.scene.shape.Sphere;
import javafx.scene.paint.Color;


public class PhysSphere extends PhysObj {
	
	// each extended shape only need provide a constructor with
	// the shape specific stuff all the generic stuff is looked
	// after in the base class
		
	PhysSphere() {
		this( 1 );
	}
	
	PhysSphere(double r) {
		
		super();
		
		geom = OdeHelper.createSphere(space,r);
		geom.setBody(body);
		
		DMass mass = OdeHelper.createMass();
		mass.setSphere(1, r);
		body.setMass(mass);
		
		shape = new Sphere(r);
		pivot.getChildren().addAll(shape);
		shape.setMaterial(PhongPhactory.fromColour(Color.RED)); // a default
	}
		
}
