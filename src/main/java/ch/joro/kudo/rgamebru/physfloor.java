package ch.joro.kudo.rgamebru;

import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DMass;
import org.ode4j.ode.OdeHelper;


public class physfloor extends PhysObj {

	// each extended shape only need provide a constructor with
	// the shape specific stuff all the generic stuff is looked
	// after in the base class

	physfloor() {
		this( 1, 1, 1);
	}

	physfloor(double sx, double sy, double sz) {
		
		super();

		geom = OdeHelper.createBox(space,sx,sy,sz);
		geom.setBody(body);

		DMass mass = OdeHelper.createMass();
		mass.setBox(20, sx, sy, sz);
		body.setMass(mass);

		
		shape = new Box (sx,sy,sz);

		pivot.getChildren().addAll(shape);
		shape.setMaterial(PhongPhactory.fromColour(Color.RED)); // a default
	}
	@Override
	public void update() {

		final DVector3C pos = body.getPosition();


		pivot.setPosition(pos.get0(),pos.get1(),pos.get2());
	}


}
