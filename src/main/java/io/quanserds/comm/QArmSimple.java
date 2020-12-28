package io.quanserds.comm;

import io.quanserds.comm.api.Postman;
import io.quanserds.comm.math.QArmMath;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;

public class QArmSimple {

    private final QArm arm;
    private final Postman comms;

    private double b = 0;
    private double s = 0;
    private double e = 0;
    private double w = 0;
    private double g = 0;

    public QArmSimple(Postman comms) {
        this.comms = comms;
        arm = new QArm(comms, 0);
        arm.setBaseColor(0, 1, 0);
    }

    public void ping() {
        arm.ping();
    }

    public double[] effectorPosition() {
        return QArmMath.forwardKinematics(b, s, e, w);
    }

    public void home() {
        arm.qarmMove(0, 0, 0, 0, 0);
        b = 0;
        s = 0;
        e = 0;
        w = 0;
        g = 0;
        comms.sleep(0.1);
    }

    public QArm getArm() {
        return arm;
    }

    public void rotateBase(double deg) {
        double b = this.b + toRadians(deg);
        if (abs(b) > toRadians(175)) {
            System.out.println("Invalid Angle. Base does not rotate beyond +/- 175 degrees");
        } else {
            this.b = b;
            arm.qarmMoveBase(this.b);
        }
    }

    public void rotateShoulder(double deg) {
        double s = this.s + toRadians(deg);
        if (abs(s) > toRadians(90)) {
            System.out.println("Invalid Angle. Shoulder does not rotate beyond +/- 90 degrees");
        } else {
            this.s = s;
            arm.qarmMoveBase(this.s);
        }
    }

    public void rotateElbow(double deg) {
        double e = this.e + toRadians(deg);
        if (e > toRadians(90) || e < toRadians(-80)) {
            System.out.println("Invalid Angle. Elbow does not rotate beyond +90 or -80 degrees");
        } else {
            this.e = e;
            arm.qarmMoveBase(this.e);
        }
    }

    public void rotateWrist(double deg) {
        double w = this.w + toRadians(deg);
        if (abs(w) > toRadians(170)) {
            System.out.println("Invalid Angle. Wrist does not rotate beyond +/- 170 degrees");
        } else {
            this.w = w;
            arm.qarmMoveBase(this.w);
        }
    }

    public void controlGripper(double value) {
        double deg = toDegrees(g + toRadians(value));
        if (abs(value) <= 55 && deg >= 0 && deg < 56) {
            g += toRadians(value);
            arm.qarmMoveGripper(g);
        } else {
            System.out.println("Please enter a value in between +/- 55 degrees.");
        }
    }

    public void moveArm(double x, double y, double z) {
        var bse = QArmMath.inverseKinematics(x, y, z);
        b = bse[0];
        s = bse[1];
        e = bse[2];
        arm.qarmMove(b, s, e, w, g);
    }
}
