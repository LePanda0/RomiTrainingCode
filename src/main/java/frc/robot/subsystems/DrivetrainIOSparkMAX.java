// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.SPI;


/** Add your docs here. */
public class DrivetrainIOSparkMAX implements DrivetrainIO {
    private final CANSparkMax m_leftLeader = new CANSparkMax(3, MotorType.kBrushless);
    private final CANSparkMax m_rightLeader = new CANSparkMax(16, MotorType.kBrushless);
    private final CANSparkMax m_leftFollower = new CANSparkMax(12, MotorType.kBrushless);
    private final CANSparkMax m_rightFollower = new CANSparkMax(15, MotorType.kBrushless);
    private CANEncoder leftEncoder = m_leftLeader.getEncoder();
    private CANEncoder rightEncoder = m_rightLeader.getEncoder();
    private AHRS gyro = new AHRS(SPI.Port.kMXP);
    private int smartCurrentLimit = 80;


    public DrivetrainIOSparkMAX(){
        m_leftLeader.restoreFactoryDefaults();
        m_leftFollower.restoreFactoryDefaults();
        m_rightLeader.restoreFactoryDefaults();
        m_rightFollower.restoreFactoryDefaults();
        m_leftFollower.follow(m_leftLeader);
        m_rightFollower.follow(m_rightLeader);
        m_rightLeader.setInverted(false);
        m_leftLeader.setInverted(true);
        m_leftLeader.setSmartCurrentLimit(smartCurrentLimit);
        m_rightLeader.setSmartCurrentLimit(smartCurrentLimit);
        m_leftFollower.setSmartCurrentLimit(smartCurrentLimit);
        m_rightFollower.setSmartCurrentLimit(smartCurrentLimit);
        m_rightLeader.enableVoltageCompensation(12);
        m_leftLeader.enableVoltageCompensation(12);
        m_rightFollower.enableVoltageCompensation(12);
        m_leftFollower.enableVoltageCompensation(12);
        m_leftLeader.burnFlash();
        m_leftFollower.burnFlash();
        m_rightLeader.burnFlash();
        m_rightFollower.burnFlash();
    }

    double afterEncoderReduction = 1.0 / ((9.0 / 62.0) * (18.0 / 30.0));
    //Represents physical gear reduction in the drivetrain

    @Override
    public void resetEncoders() {
        leftEncoder.setPosition(0);
        rightEncoder.setPosition(0);
    }

    @Override
    public void resetGyro() {
        gyro.reset();
    }

    @Override
    public void setOutputs(double leftPercent, double rightPercent) {
        m_leftLeader.set(leftPercent);
        m_rightLeader.set(rightPercent);
    }

    @Override
    public void updateInputs(DrivetrainIOInputs inputs) {
        inputs.leftPositionRadians = leftEncoder.getPosition() / afterEncoderReduction * 2 * Math.PI;
        inputs.rightPositionRadians = rightEncoder.getPosition() / afterEncoderReduction * 2 * Math.PI;
        inputs.gyroPositionRadians = Math.toRadians(gyro.getAngle());
    }
    
}
