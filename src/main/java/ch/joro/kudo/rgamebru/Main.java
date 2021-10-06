
package ch.joro.kudo.rgamebru;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.*;
import javafx.stage.Stage;

import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.Box;

import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;


import org.ode4j.ode.*;

import static org.ode4j.ode.OdeMath.*;


import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {

    private final Random rnd = new Random(System.currentTimeMillis());

    private static DSpace space;
    private static DWorld world;
    private static DJointGroup contactgroup;


    private static final double AXIS_LENGTH = 250.0;

    private final Group root = new Group();
    private final PerspectiveCamera cam = new PerspectiveCamera(true);

    private final Pivot camera = new Pivot("camera");
    private final Pivot light = new Pivot("light");
    private final Pivot redLight = new Pivot("red light");


    Material boxMat = PhongPhactory.fromImage("crate.jpg");
    Material bxmat = PhongPhactory.fromImage("floor.jpg");
    Material sphereMat = PhongPhactory.fromImage("bol.jpg");
    Material cylinderMat = PhongPhactory.fromImage("cylinder.jpg");
    private PhysObj boy;

    private double lastMouseX, lastMouseY, mouseX, mouseY;
    private double camXa = 20, camYa = 345;
    private com.sun.javafx.geom.Vec3d fwd = new com.sun.javafx.geom.Vec3d();
    private final com.sun.javafx.geom.Vec3d up = new com.sun.javafx.geom.Vec3d(0, -1, 0);

    // this list holds both PhysBox and PhysSphere instances
    ArrayList<? super PhysObj> objects = new ArrayList<PhysObj>();

    // array of key status
    public boolean[] keys;

    public static void main(String[] args) {
        launch(args);
    }

    private void buildAxes(Pivot p) {
        final Box xAxis = new Box(AXIS_LENGTH, 4, 4);
        final Box yAxis = new Box(4, AXIS_LENGTH, 4);
        final Box zAxis = new Box(4, 4, AXIS_LENGTH);

        xAxis.setMaterial(PhongPhactory.fromColour(Color.RED));
        yAxis.setMaterial(PhongPhactory.fromColour(Color.GREEN));
        zAxis.setMaterial(PhongPhactory.fromColour(Color.BLUE));

        p.getChildren().addAll(xAxis, yAxis, zAxis);
        p.setVisible(true);
        root.getChildren().addAll(p);
    }

    @Override
    public void start(Stage stage) {
        int x=10;
        int z=10;
        OdeHelper.initODE2(0);

        world = OdeHelper.createWorld();
        world.setGravity(0, 9.8, 0);
        world.setQuickStepNumIterations(24);

        space = OdeHelper.createSimpleSpace(null);


        contactgroup = OdeHelper.createJointGroup();
        DGeom ground = OdeHelper.createPlane(space, 0, -1, 0, 0);

        keys = new boolean[KeyCode.values().length];

        Scene scene = new Scene(root, 640, 480, true);
        scene.setFill(Color.SKYBLUE);
        root.getChildren().add(new AmbientLight(Color.color(0.1, 0.1, 0.1)));

        // PhysBox must be initialised so it knows where to attach the
        // physics and graphics elements
        PhysBox.initialise(world, space, scene, root);

        camera.setPosition(8, -12, -20);
        camera.getChildren().add(cam);

        cam.setNearClip(0.1);
        cam.setFarClip(2000.0);
        cam.setFieldOfView(35);
        scene.setCamera(cam);

        PointLight l = new PointLight(Color.WHITE);
        light.getChildren().add(l);
        light.setPosition(300, -200, -300);
        buildAxes(light); // adding the axis will add light pivot to scene

        PointLight rl = new PointLight(Color.LIGHTYELLOW);
        redLight.getChildren().add(rl);
        redLight.setPosition(-300, -200, 300);
        buildAxes(redLight);


        stage.setScene(scene);
        stage.show();

        // use a Timeline to give us a more traditional update/render/VBL type event
        Timeline t = new Timeline(new KeyFrame(Duration.millis(20), e -> update()));
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();

        scene.setOnKeyPressed(this::keyDown);
        scene.setOnKeyReleased(this::keyUp);
        scene.setOnMouseDragged(this::mouseDrag);
        scene.setOnMousePressed(this::mousePressed);

        PhysObj objd;
        objd = new PhysPlane();
        objd.setMaterial(bxmat);


        objd.body.setPosition(0, 1.5, 0);

        objects.add(objd);
        PhysObj[][] obj = new PhysObj[10][8];
        for (int e = 0; e <obj.length; e++) {


            for (int i = 0; i < obj[e].length; i++) {
                obj[e][i] = new physfloor(10, 0.00001, 10);
                obj[e][i].setMaterial(boxMat);

                objects.add(obj[e][i]);
            }


           obj[e][0].body.setPosition(-x, 0, 0);

           obj[e][1].body.setPosition(x, 0, 0);

           obj[e][2].body.setPosition(x, 0, z);
           obj[e][3].body.setPosition(-x, 0, z);
           obj[e][4].body.setPosition(x, 0, -z);
           obj[e][5].body.setPosition(-x, 0, -z);

           obj[e][6].body.setPosition(0, 0, z);
           obj[e][7].body.setPosition(0, 0, -z);

            x=x+10;
            z=z+10;



        }
        scene.setOnKeyPressed(this::keyDown);
        scene.setOnKeyReleased(this::keyUp);
        scene.setOnMouseDragged(this::mouseDrag);
        scene.setOnMousePressed(this::mousePressed);
        boy = new PhysBox(2,4,2);
        boy.setMaterial(sphereMat);
        boy.body.setPosition(2, 0, 2);

        objects.add(boy);
        PhysObj obj2;
        obj2 = new PhysBox(1000, 1000, 5);
        obj2.setMaterial(boxMat);


        obj2.body.setPosition(0, -1000, 0);

        objects.add(obj2);

        /*makeColumn(0, 0);
        makeColumn(-2, -2);
        makeColumn(2, -2);
        makeColumn(-2, 2);
        makeColumn(2, 2);*/

    }

    public void makeColumn(double x, double z) {

        PhysObj obj;


        for (int y = -1; y > -11; y--) {
            if (rnd.nextDouble() > 0.5) {
                obj = new PhysBox(1 + rnd.nextDouble(), 1, 1 + rnd.nextDouble());
                obj.setMaterial(boxMat);
                obj.body.setPosition(x + rnd.nextDouble(), y, z + rnd.nextDouble());
                objects.add(obj);
            } else {
                if (rnd.nextDouble() > 0.5) {
                    obj = new PhysSphere(0.5);
                    obj.setMaterial(sphereMat);
                    obj.body.setPosition(x + rnd.nextDouble(), y, z + rnd.nextDouble());
                    objects.add(obj);
                } else {
                    obj = new PhysCylinder(0.5, 2);
                    obj.setMaterial(cylinderMat);
                    obj.body.setPosition(x, y, z);
                    objects.add(obj);
                }
            }
        }
    }

    public void mousePressed(MouseEvent me) {
        lastMouseX = me.getSceneX();
        lastMouseY = me.getSceneY();
        mouseX = me.getSceneX();
        mouseY = me.getSceneY();
    }

    public void mouseDrag(MouseEvent me) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        mouseX = me.getSceneX();
        mouseY = me.getSceneY();

        camYa = camYa + ((mouseX - lastMouseX) / 4.0);
        camXa = camXa + ((mouseY - lastMouseY) / 4.0);
    }

    public void update() {

        camera.setEularRotation(camXa, -camYa, 0);
        fwd.set(0, 0, 0);
        camera.setPosition(boy.body.getPosition().get0(), -7, boy.body.getPosition().get2() - 15);

        if (keys[KeyCode.I.ordinal()]) world.setGravity(0, 9.8, 0);

        if (keys[KeyCode.U.ordinal()]) world.setGravity(0, -9.8, 0);
        if (keys[KeyCode.R.ordinal()]) boy.body.setPosition(0, -1, 0);

        if (keys[KeyCode.X.ordinal()])
            boy.body.setAngularVel(boy.body.getAngularVel().get0() - 1, boy.body.getAngularVel().get1(), boy.body.getAngularVel().get2());
        if (keys[KeyCode.C.ordinal()])
            boy.body.setAngularVel(boy.body.getAngularVel().get0(), boy.body.getAngularVel().get1() - 1, boy.body.getAngularVel().get2());
        if (keys[KeyCode.V.ordinal()])
            boy.body.setAngularVel(boy.body.getAngularVel().get0(), boy.body.getAngularVel().get1(), boy.body.getAngularVel().get2() - 1);

        if (keys[KeyCode.R.ordinal()])
            boy.body.setAngularVel(boy.body.getAngularVel().get0() + 1, boy.body.getAngularVel().get1(), boy.body.getAngularVel().get2());
        if (keys[KeyCode.T.ordinal()])
            boy.body.setAngularVel(boy.body.getAngularVel().get0(), boy.body.getAngularVel().get1() + 1, boy.body.getAngularVel().get2());
        if (keys[KeyCode.Z.ordinal()])
            boy.body.setAngularVel(boy.body.getAngularVel().get0(), boy.body.getAngularVel().get1(), boy.body.getAngularVel().get2() + 1);

        if (keys[KeyCode.Y.ordinal()])
            boy.body.setAngularVel(0, 0, 0);

        if (keys[KeyCode.Q.ordinal()])
            boy.body.setAngularVel(0, 0, 0);
        if (keys[KeyCode.E.ordinal()])
            boy.body.setAngularVel(0, 0, 0);


        if (keys[KeyCode.D.ordinal()])
            boy.body.setLinearVel(boy.body.getLinearVel().get0() + 1, boy.body.getLinearVel().get1(), boy.body.getLinearVel().get2());
        if (keys[KeyCode.J.ordinal()])
            boy.body.setLinearVel(boy.body.getLinearVel().get0(), boy.body.getLinearVel().get1() + 1, boy.body.getLinearVel().get2());
        if (keys[KeyCode.W.ordinal()])
            boy.body.setLinearVel(boy.body.getLinearVel().get0(), boy.body.getLinearVel().get1(), boy.body.getLinearVel().get2() + 1);

        if (keys[KeyCode.A.ordinal()])
            boy.body.setLinearVel(boy.body.getLinearVel().get0() - 1, boy.body.getLinearVel().get1(), boy.body.getLinearVel().get2());
        if (keys[KeyCode.N.ordinal()])
            boy.body.setLinearVel(boy.body.getLinearVel().get0(), boy.body.getLinearVel().get1() - 1, boy.body.getLinearVel().get2());
        if (keys[KeyCode.S.ordinal()])
            boy.body.setLinearVel(boy.body.getLinearVel().get0(), boy.body.getLinearVel().get1(), boy.body.getLinearVel().get2() - 1);


        if (!(fwd.x == 0 && fwd.y == 0 && fwd.z == 0)) {
            fwd = camera.rotateVector(fwd);
            com.sun.javafx.geom.Vec3d tmpV3 = camera.getPosition();
            tmpV3.add(fwd);
            camera.setPosition(tmpV3);
        }

        // Lambda makes for much nicer callback mechanism
        space.collide(null, Main::nearCallback);
        world.quickStep(1.0 / 60.0);
        contactgroup.empty();

        // sync the list of objects visuals with their physics counterparts
        for (Object obj : objects) ((PhysObj) obj).update();
    }

    private static void nearCallback(Object data, DGeom o1, DGeom o2) {
        // exit without doing anything if the two bodies are connected by a joint
        DBody b1 = o1.getBody();
        DBody b2 = o2.getBody();

        int MAX_CONTACTS = 8;
        DContactBuffer contacts = new DContactBuffer(MAX_CONTACTS);

        int numc = OdeHelper.collide(o1, o2, MAX_CONTACTS, contacts.getGeomBuffer());

        for (int i = 0; i < numc; i++) {
            contacts.get(i).surface.mode = dContactApprox1;
            contacts.get(i).surface.mu = 5;
            DJoint c = OdeHelper.createContactJoint(world, contactgroup, contacts.get(i));//contact+i);
            c.attach(b1, b2);
        }
    }


    public void keyDown(KeyEvent ke) {
        keys[ke.getCode().ordinal()] = true;
        //System.out.println("keycode="+ke.getCode().ordinal());
    }

    public void keyUp(KeyEvent ke) {
        keys[ke.getCode().ordinal()] = false;
        if (ke.getCode() == KeyCode.ESCAPE) Platform.exit();
    }
}
