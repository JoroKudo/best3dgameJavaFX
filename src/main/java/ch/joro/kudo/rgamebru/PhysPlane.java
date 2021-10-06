package ch.joro.kudo.rgamebru;

import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import org.ode4j.ode.DMass;
import org.ode4j.ode.OdeHelper;


public class PhysPlane extends PhysObj {

	// each extended shape only need provide a constructor with
	// the shape specific stuff all the generic stuff is looked
	// after in the base class



	PhysPlane() {
		
		super();

		geom= OdeHelper.createPlane(space, 0, -1, 0, 0);
		geom.setBody(body);
		
		DMass mass = OdeHelper.createMass();
		mass.setBox(1, 10000, 0.1, 10000);
		body.setMass(mass);
		
		shape = new Box(10000,1,10000);
		pivot.getChildren().addAll(shape);
		shape.setMaterial(PhongPhactory.fromColour(Color.RED)); // a default
		pivot.setPosition(0,1.1,0);
	}
	@Override
	public void update() {

	}


}
