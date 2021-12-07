// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.SPI;

/** Add your docs here. */
public class DrivetrainIOSparkMAX implements DrivetrainIO {
    private static final int configTimeoutRuntime = 0;

    private final AHRS gyro = new AHRS(SPI.Port.kMXP);
    private final CANSparkMax m_leftLeaderMotor = new CANSparkMax(12, MotorType.kBrushless);
    private final CANSparkMax m_rightLeaderMotor = new CANSparkMax(15, MotorType.kBrushless);
    private final CANSparkMax m_leftFollowerMotor = new CANSparkMax(3, MotorType.kBrushless);
    private final CANSparkMax m_rightFollowerMotor = new CANSparkMax(16, MotorType.kBrushless);
    private double afterEncoderReduction = 1.0 / ((9.0 / 62.0) * (18.0 / 30.0));
    private final CANEncoder leftEncoder = m_leftLeaderMotor.getEncoder();
    private final CANEncoder rightEncoder = m_rightLeaderMotor.getEncoder();
    private static final int smartCurrentLimit = 80;
    private static final boolean reverseOutputLeft = true;
    private static final boolean reverseOutputRight = false;

    public DrivetrainIOSparkMAX() {
        m_leftLeaderMotor.restoreFactoryDefaults();
        m_leftFollowerMotor.restoreFactoryDefaults();
        m_rightLeaderMotor.restoreFactoryDefaults();
        m_rightFollowerMotor.restoreFactoryDefaults();
        m_rightFollowerMotor.follow(m_rightLeaderMotor);
        m_leftFollowerMotor.follow(m_leftLeaderMotor);
        m_rightLeaderMotor.setSmartCurrentLimit(smartCurrentLimit);
        m_leftLeaderMotor.setSmartCurrentLimit(smartCurrentLimit);
        m_rightFollowerMotor.setSmartCurrentLimit(smartCurrentLimit);
        m_leftFollowerMotor.setSmartCurrentLimit(smartCurrentLimit);
        m_rightLeaderMotor.setInverted(reverseOutputRight);
        m_leftLeaderMotor.setInverted(reverseOutputLeft);
        setCANTimeout(configTimeoutRuntime);
        m_leftLeaderMotor.enableVoltageCompensation(12);
        m_rightLeaderMotor.enableVoltageCompensation(12);
        m_leftLeaderMotor.setIdleMode(IdleMode.kBrake);
        m_leftFollowerMotor.setIdleMode(IdleMode.kBrake);
        m_rightLeaderMotor.setIdleMode(IdleMode.kBrake);
        m_rightFollowerMotor.setIdleMode(IdleMode.kBrake);
        m_leftFollowerMotor.burnFlash();
        m_leftLeaderMotor.burnFlash();
        m_rightFollowerMotor.burnFlash();
        m_rightLeaderMotor.burnFlash();
        gyro.zeroYaw();
    }

    public void updateInputs(DrivetrainIOInputs inputs) {
        inputs.leftPositionRadians = ((leftEncoder.getPosition() / afterEncoderReduction) * 2 * Math.PI);
        inputs.rightPositionRadians = ((rightEncoder.getPosition() / afterEncoderReduction) * 2 * Math.PI);
        inputs.gyroPositionRadians = Math.toRadians(gyro.getAngle());
    }

    /** Drives the robot at the specified percentages (from -1 to 1). */
    public void setOutputs(double leftPercent, double rightPercent) {
        m_leftLeaderMotor.setVoltage(leftPercent * 12);
        m_rightLeaderMotor.setVoltage(rightPercent * 12);
    }

    /** Resets the encoder values to 0. */
    public void resetEncoders() {
        leftEncoder.setPosition(0);
        rightEncoder.setPosition(0);
    }

    /** Resets the gyro angle to 0. */
    public void resetGyro() {
        gyro.zeroYaw();
    }

    private void setCANTimeout(int ms) {
        m_leftLeaderMotor.setCANTimeout(ms);
        m_leftFollowerMotor.setCANTimeout(ms);
        m_rightLeaderMotor.setCANTimeout(ms);
        m_rightFollowerMotor.setCANTimeout(ms);
    }
}