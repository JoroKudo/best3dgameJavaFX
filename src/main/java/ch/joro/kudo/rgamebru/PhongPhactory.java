package ch.joro.kudo.rgamebru;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.image.Image;

// TODO relace this with something more interesting...

public class PhongPhactory {
	
	public static Material fromColour( Color c) {
        final PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseColor(c);
        mat.setSpecularColor(c.brighter());
        return mat;		
	}
	
	public static Material fromImage( String fileName ) {
		Image img = new Image(fileName);

		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseMap(img);

		return mat;		
	}
}
