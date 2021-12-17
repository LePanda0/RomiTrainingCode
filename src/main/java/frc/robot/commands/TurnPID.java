// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.Drivetrain;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.util.TunableNumber;

public class TurnPID extends CommandBase {
  // private static final double KP = 0.011;
  // private static final double KD = 0.0013;
  private static final double TOLERANCE = 3;
  private static final double TIMELIMIT = 0.5;
  private final TunableNumber kP = new TunableNumber("kP");
  private final TunableNumber kD = new TunableNumber("kD");
  private final double minVelocity = 0.05;
  private final Drivetrain m_drive;
  private final double m_degrees;
  private final PIDController pid = new PIDController(kP.get(), 0, kD.get());
  private final Timer timer = new Timer();
  
  

  /** Creates a new TurnPID. */
  public TurnPID(double degrees, Drivetrain drive) {
    m_degrees = degrees;
    m_drive = drive;
    kP.setDefault(0.011);
    kD.setDefault(0.0013);
    addRequirements(drive);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_drive.arcadeDrive(0, 0);
    m_drive.resetGyro();
    timer.start();
    timer.reset();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double output = pid.calculate(m_drive.getGyroAngleZ(), m_degrees);
    m_drive.arcadeDrive(0, output);
    SmartDashboard.putNumber("TurnError", pid.getPositionError());
    if (Math.abs(output) < minVelocity) {
      output = Math.copySign(minVelocity, output);
    }
    pid.setP(kP.get());
    pid.setD(kD.get());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_drive.arcadeDrive(0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    double gyroAngle = m_drive.getGyroAngleZ();
    if (Math.abs(pid.getPositionError()) > TOLERANCE) {
      timer.reset();
    }
    return timer.hasElapsed(TIMELIMIT);
  }
}
