package io.quanserds.comm.math;

import static java.lang.Math.*;

public class QArmMath {

    // Manipulator parameters in meters:
    private static final double _L1 = 0.127;
    private static final double _L2 = 0.3556;
    private static final double _L3 = 0.4064;

    // Define joint angle (in rads) and gripper limits
    private static final double _base_upper_lim = 3.05;
    private static final double _base_lower_lim = -3.05;
    private static final double _shoulder_upper_limit = 1.57;
    private static final double _shoulder_lower_limit = -1.57;
    private static final double _elbow_upper_limit = 1.57;
    private static final double _elbow_lower_limit = -1.39;
    private static final double _wrist_upper_limit = 2.96;
    private static final double _wrist_lower_limit = -2.96;
    private static final double _gripper_upper_limit = 1;
    private static final double _gripper_lower_limit = 0;

    public static boolean anglesWithinBound(
            double base, double shoulder, double elbow, double wrist, double gripper) {
        // Check if given joint angles and gripper value are within acceptable limit
        return !(base > _base_upper_lim || base < _base_lower_lim ||
                shoulder > _shoulder_upper_limit || shoulder < _shoulder_lower_limit ||
                elbow > _elbow_upper_limit || elbow < _elbow_lower_limit ||
                wrist > _wrist_upper_limit || wrist < _wrist_lower_limit ||
                gripper > _gripper_upper_limit || gripper < _gripper_lower_limit);
    }

    public static boolean coordinatesWithinBound(double x, double y, double z) {
        double R = Math.hypot(x, y);

        // Vertical offset within the verical plane from Frame 1 to End-Effector
        // Note: Frame 1 y-axis points downward (negative global Z-axis direction)
        double Z = _L1 - z;

        // Distance from Frame 1 to End-Effector Frame
        double Lambda = Math.hypot(R, Z);

        return !(Lambda > (_L2 + _L3) || z < 0);
    }

    /**
     * Calculate standard DH parameters
     * Inputs:
     * a       :   translation  : along : x_{i}   : from : z_{i-1} : to : z_{i}
     * alpha   :      rotation  : about : x_{i}   : from : z_{i-1} : to : z_{i}
     * d       :   translation  : along : z_{i-1} : from : x_{i-1} : to : x_{i}
     * theta   :      rotation  : about : z_{i-1} : from : x_{i-1} : to : x_{i}
     * Outputs:
     * transformed       : transformation                   : from :     {i} : to : {i-1}
     */
    public static Matrix44 dh(double theta, double d, double a, double alpha) {
        // Rotation Transformation about z axis by theta
        var a_r_z = new Matrix44(
                cos(theta), -sin(theta), 0, 0,
                sin(theta), cos(theta), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);

        // Translation Transformation along z axis by d
        var a_t_z = new Matrix44(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, d,
                0, 0, 0, 1);

        // Translation Transformation along x axis by a
        var a_t_x = new Matrix44(
                1, 0, 0, a,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);

        // Rotation Transformation about x axis by alpha
        var a_r_x = new Matrix44(
                1, 0, 0, 0,
                0, cos(alpha), -sin(alpha), 0,
                0, sin(alpha), cos(alpha), 0,
                0, 0, 0, 1
        );

        return a_r_z.mult(a_t_z).mult(a_r_x).mult(a_t_x);
    }


    /**
     * Calculate end-effector position (x, y, z) using forward kinematics
     * Input:    joint angles in rads
     * Output:   end-effector position (x, y, z) expressed in base frame {0}
     */
    public static double[] forwardKinematics(
            double joint1, double joint2, double joint3, double joint4) {
        // Transformation matrices for all frames:
        // A{i-1}{i} = quanser_arm_dh(theta, d, a, alpha)
        var A01 = dh(joint1, _L1, 0, -PI / 2);
        var A12 = dh(joint2 - PI / 2, 0, _L2, 0);
        var A23 = dh(joint3, 0, 0, -PI / 2);
        var A34 = dh(joint4, _L3, 0, 0);

        var A04 = A01.mult(A12).mult(A23).mult(A34);

        double x = A04.get(0, 3);
        double y = A04.get(1, 3);
        double z = A04.get(2, 3);

        return new double[]{x, y, z};
    }

    /**
     * Compute the position of the end-effector using inverse kinematics
     *
     * The solution is based on the geometric configuration of the QArm
     * where the upper links are contained within the vertical plane rotating
     * with the based joint angle q1.
     * The frame definition is consistent with the S&V DH convention.
     * Inputs: end-effector position, p_x, p_y, p_z
     * Outputs: joint angles in rads (base, shoulder, elbow) based on inverse kinematics
     */
    public static double[] inverseKinematics(double x, double y, double z) {
        // Base angle:
        double q_base = atan2(y, x);

        // Geometric definitions
        // Radial distance (R) projection onto the horizontal plane
        double R = hypot(x, y);

        // Vertical offset within the verical plane from Frame 1 to End-Effector
        // Note: Frame 1 y-axis points downward (negative global Z-axis direction)
        double Z = _L1 - z;

        // Distance from Frame 1 to End-Effector Frame
        double Lambda = hypot(R, Z);

        // Angle of Lambda vector from horizontal plane (Frame 1)
        // Note: theta is measured about z-axis of Frame 1 so positive theta
        // rotates Lambda "down".
        double theta = atan2(Z, R);

        // Based angle of the triangle formed by L2, L3 and Lambda
        // Computed using cosine law
        // Note: The sign of alpha determines whether it is elbow up (alpha < 0) or
        // elbow down (alpha > 0) configuration (i.e., consistent with Frame 1)
        double alpha = acos(-(_L3 * _L3 - _L2 * _L2 - Lambda * Lambda) / (2 * _L2 * Lambda));

        // Solve for q_shoulder; elbow up solution
        double q_shoulder = PI / 2 + (theta - alpha);

        // Solve for q_elbow, elbow up solution
        double q_elbow = atan2(
                _L2 - R * sin(q_shoulder) + Z * cos(q_shoulder),
                R * cos(q_shoulder) + Z * sin(q_shoulder)
        );

        return new double[]{q_base, q_shoulder, q_elbow};
    }
}
