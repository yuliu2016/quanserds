package io.quanserds.comm;

import io.quanserds.comm.struct.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@SuppressWarnings("unused")
public class CommAPI {
    public static final int ID_QARM = 10;
    public static final int FCN_QARM_COMMAND_AND_REQUEST_STATE = 10;
    public static final int FCN_QARM_RESPONSE_STATE = 11;
    public static final int FCN_QARM_COMMAND_BASE = 14;
    public static final int FCN_QARM_RESPONSE_BASE = 15;
    public static final int FCN_QARM_COMMAND_SHOULDER = 16;
    public static final int FCN_QARM_RESPONSE_SHOULDER = 17;
    public static final int FCN_QARM_COMMAND_ELBOW = 18;
    public static final int FCN_QARM_RESPONSE_ELBOW = 19;
    public static final int FCN_QARM_COMMAND_WRIST = 20;
    public static final int FCN_QARM_RESPONSE_WRIST = 21;
    public static final int FCN_QARM_COMMAND_GRIPPER = 22;
    public static final int FCN_QARM_RESPONSE_GRIPPER = 23;
    public static final int FCN_QARM_COMMAND_BASE_COLOR = 24;
    public static final int FCN_QARM_RESPONSE_BASE_COLOR_ACK = 25;
    public static final int FCN_QARM_COMMAND_ARM_BRIGHTNESS = 26;
    public static final int FCN_QARM_RESPONSE_ARM_BRIGHTNESS_ACK = 27;
    public static final int FCN_QARM_REQUEST_GRIPPER_OBJECT_PROPERTIES = 50;
    public static final int FCN_QARM_RESPONSE_GRIPPER_OBJECT_PROPERTIES = 51;
    public static final int FCN_QARM_REQUEST_RGB = 100;
    public static final int FCN_QARM_RESPONSE_RGB = 101;
    public static final int FCN_QARM_REQUEST_DEPTH = 110;
    public static final int FCN_QARM_RESPONSE_DEPTH = 111;

    public static final int ID_QBOT = 20;
    public static final int FCN_QBOT_COMMAND_AND_REQUEST_STATE = 10;
    public static final int FCN_QBOT_RESPONSE_STATE = 11;
    public static final int FCN_QBOT_REQUEST_RGB = 100;
    public static final int FCN_QBOT_RESPONSE_RGB = 101;
    public static final int FCN_QBOT_REQUEST_DEPTH = 110;
    public static final int FCN_QBOT_RESPONSE_DEPTH = 111;

    public static final int ID_COUPLED_TANK = 30;
    public static final int ID_SRV02_BASE = 40;
    public static final int ID_SRV02_FLEX_LINK = 41;
    public static final int ID_SRV02_BALL_AND_BEAM = 42;
    public static final int ID_SRV02_PENDULUM = 42;
    public static final int ID_QUBE2_SERVO = 60;

    public static final int ID_EMG_INTERFACE = 70;
    public static final int FCN_EMG_REQUEST_STATE = 10;
    public static final int FCN_EMG_RESPONSE_STATE = 11;


    public static final int ID_DELIVERY_TUBE = 80;
    public static final int ID_AERO = 90;

    public static final int ID_SRV02BOTTLETABLE = 100;
    public static final int FCN_SRV02BT_COMMAND_SPEED = 11;
    public static final int FCN_SRV02BT_REQUEST_ENCODER = 13;
    public static final int FCN_SRV02BT_RESPONSE_ENCODER = 14;
    public static final int FCN_SRV02BT_REQUEST_TOF = 15;
    public static final int FCN_SRV02BT_RESPONSE_TOF = 16;
    public static final int FCN_SRV02BT_REQUEST_PROXIMITY_SHORT = 17;
    public static final int FCN_SRV02BT_RESPONSE_PROXIMITY_SHORT = 18;
    public static final int FCN_SRV02BT_REQUEST_PROXIMITY_TALL = 19;
    public static final int FCN_SRV02BT_RESPONSE_PROXIMITY_TALL = 20;
    public static final int FCN_SRV02BT_SPAWN_CONTAINER = 21;
    public static final int FCN_SRV02BT_REQUEST_LOAD_MASS = 91;
    public static final int FCN_SRV02BT_RESPONSE_LOAD_MASS = 92;

    public static final int ID_QBOT_BOX = 110;
    public static final int FCN_QBOT_BOX_COMMAND = 11;
    public static final int FCN_QBOT_BOX_COMMAND_ACK = 12;

    public static final int ID_SCALE = 120;
    public static final int FCN_SCALE_REQUEST_LOAD_MASS = 91;
    public static final int FCN_SCALE_RESPONSE_LOAD_MASS = 92;

    public static final int ID_GENERIC_SPAWNER = 130;
    public static final int FCN_GENERIC_SPAWNER_SPAWN = 10;
    public static final int FCN_GENERIC_SPAWNER_SPAWN_ACK = 11;
    public static final int FCN_GENERIC_SPAWNER_SPAWN_WITH_PROPERTIES = 20;
    public static final int FCN_GENERIC_SPAWNER_SPAWN_WITH_PROPERTIES_ACK = 21;

    public static final int ID_AUTOCLAVE = 140;
    public static final int FCN_AUTOCLAVE_OPEN_DRAWER = 10;
    public static final int FCN_AUTOCLAVE_OPEN_DRAWER_ACK = 11;

    public static final int ID_UE4_SYSTEM = 1000;

    public static final int ID_SIMULATION_CODE = 1001;
    public static final int FCN_SIMULATION_CODE_RESET = 200;

    public static final int ID_UNKNOWN = 0;

    public static final int FCN_UNKNOWN = 0;
    public static final int FCN_REQUEST_PING = 1;
    public static final int FCN_RESPONSE_PING = 2;
    public static final int FCN_REQUEST_WORLD_TRANSFORM = 3;
    public static final int FCN_RESPONSE_WORLD_TRANSFORM = 4;

    private static ModularContainer container(
            int device_id, int device_num, int deviceFunction, byte[] payload) {
        return ModularContainer.of(10 + payload.length,
                device_id, device_num, deviceFunction, payload);
    }

    private static byte[] packFloat(float... floats) {
        ByteBuffer bb = ByteBuffer.allocate(floats.length * 4);
        bb.order(ByteOrder.BIG_ENDIAN);
        for (float aFloat : floats) {
            bb.putFloat(aFloat);
        }
        return bb.array();
    }

    private static float unpackFloat(byte[] payload) {
        if (payload.length == 4) {
            ByteBuffer bb = ByteBuffer.wrap(payload);
            bb.order(ByteOrder.BIG_ENDIAN);
            return bb.getFloat();
        } else {
            return 0f;
        }
    }

    private static float unpackFloat(ModularContainer container) {
        return unpackFloat(container.getPayload());
    }

    public static ModularContainer common_RequestPing(int device_id, int device_num) {
        return container(device_id, device_num, FCN_REQUEST_PING, new byte[0]);
    }

    public static ModularContainer common_RequestWorldTransform(int device_id, int device_num) {
        return container(device_id, device_num, FCN_REQUEST_WORLD_TRANSFORM, new byte[0]);
    }

    public static WorldTransform common_ResponseWorldTransform(ModularContainer container) {
        return WorldTransform.fromPayload(container.getPayload());
    }

    // ================== QARM ======================

    public static ModularContainer qarm_CommandAndRequestState(
            int device_num, float base, float shoulder, float elbow,
            float wrist, float gripper, float base_r, float base_g,
            float base_b, float arm_brightness
    ) {
        return container(ID_QARM, device_num, FCN_QARM_COMMAND_AND_REQUEST_STATE,
                packFloat(base, shoulder, elbow, wrist, gripper, base_r,
                        base_g, base_b, arm_brightness));
    }

    public static ModularContainer qarm_CommandBase(int device_num, float base) {
        return container(ID_QARM, device_num, FCN_QARM_COMMAND_BASE, packFloat(base));
    }

    public static ModularContainer qarm_CommandShoulder(int device_num, float shoulder) {
        return container(ID_QARM, device_num, FCN_QARM_COMMAND_SHOULDER, packFloat(shoulder));
    }

    public static ModularContainer qarm_CommandElbow(int device_num, float elbow) {
        return container(ID_QARM, device_num, FCN_QARM_COMMAND_ELBOW, packFloat(elbow));
    }

    public static ModularContainer qarm_CommandWrist(int device_num, float wrist) {
        return container(ID_QARM, device_num, FCN_QARM_COMMAND_WRIST, packFloat(wrist));
    }

    public static ModularContainer qarm_CommandGripper(int device_num, float gripper) {
        return container(ID_QARM, device_num, FCN_QARM_COMMAND_GRIPPER, packFloat(gripper));
    }

    public static ModularContainer qarm_CommandBaseColor(
            int device_num, float base_r, float base_g, float base_b) {
        return container(ID_QARM, device_num, FCN_QARM_COMMAND_BASE_COLOR,
                packFloat(base_r, base_g, base_b));
    }

    public static ModularContainer qarm_CommandArmBrightness(int device_num, float arm_brightness) {
        return container(ID_QARM, device_num, FCN_QARM_COMMAND_ARM_BRIGHTNESS, packFloat(arm_brightness));
    }

    public static ModularContainer qarm_RequestGripperObjectProperties(int device_num) {
        return container(ID_QARM, device_num, FCN_QARM_REQUEST_GRIPPER_OBJECT_PROPERTIES, new byte[0]);
    }

    public QArmState qarm_ResponseState(ModularContainer container) {
        return QArmState.fromPayload(container.getPayload());
    }

    public float qarm_ResponseBase(ModularContainer container) {
        return unpackFloat(container);
    }

    public float qarm_ResponseShoulder(ModularContainer container) {
        return unpackFloat(container);
    }

    public float qarm_ResponseElbow(ModularContainer container) {
        return unpackFloat(container);
    }

    public float qarm_ResponseWrist(ModularContainer container) {
        return unpackFloat(container);
    }

    public QArmGripperState qarm_ResponseGripper(ModularContainer container) {
        return QArmGripperState.fromPayload(container.getPayload());
    }

    public QArmGripperObject qarm_ResponseGripperObjectProperties(ModularContainer container) {
        return QArmGripperObject.fromPayload(container.getPayload());
    }


    // ================== QBOT2E ======================

    private static final float QBOT_RADIUS = 0.235f / 2.0f;

    public ModularContainer qbot2e_CommandAndRequestState(int device_num, float forward, float turn) {
        float right_wheel_speed = forward + turn * QBOT_RADIUS;
        float left_wheel_speed = forward - turn * QBOT_RADIUS;
        return container(ID_QBOT, device_num, FCN_QBOT_COMMAND_AND_REQUEST_STATE,
                packFloat(right_wheel_speed, left_wheel_speed));
    }

    public ModularContainer qbot2e_CommandAndRequestStateTank(
            int device_num, float right_wheel_speed, float left_wheel_speed) {
        return container(ID_QBOT, device_num, FCN_QBOT_COMMAND_AND_REQUEST_STATE,
                packFloat(right_wheel_speed, left_wheel_speed));
    }

    public ModularContainer qbot2e_RequestRGB(int device_num) {
        return container(ID_QBOT, device_num, FCN_QARM_REQUEST_RGB, new byte[0]);
    }

    public ModularContainer qbot2e_RequestDepth(int device_num) {
        return container(ID_QBOT, device_num, FCN_QBOT_REQUEST_DEPTH, new byte[0]);
    }

    public QBot2eState qbot2e_ResponseState(ModularContainer container) {
        return QBot2eState.fromPayload(container.getPayload());
    }

    public byte[] qbot2e_ResponseRGB(ModularContainer container) {
        // just assume this is a valid payload for now.
        byte[] payload = container.getPayload();
        return Arrays.copyOfRange(payload, 4, payload.length);
    }

    public byte[] qbot2e_ResponseDepth(ModularContainer container) {
        // just assume this is a valid payload for now.
        byte[] payload = container.getPayload();
        return Arrays.copyOfRange(payload, 4, payload.length);
    }
}
