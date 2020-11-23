set TARGET_DIR=%1
set OUT_FILE=%2

java -classpath .\;SimpleStepCounter.jar util.stepcounter.StepCounter %TARGET_DIR% %OUT_FILE% > %OUT_FILE%.log 2>&1
