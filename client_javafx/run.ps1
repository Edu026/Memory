# run.ps1

# Change to the directory where the script is located
Set-Location $PSScriptRoot

# Set MAVEN_OPTS environment variable
$env:MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"

# Check for the first argument and set it as the main class
$mainClass = $args[0]

Write-Host "Setting MAVEN_OPTS to: $env:MAVEN_OPTS"
Write-Host "Main Class: $mainClass"

# Construct Maven argument for the main class
$mavenMainClassArg = "-Dexec.mainClass=$mainClass"

# Get the rest of the arguments (excluding the first one for mainClass) and join them into a single string
$javaArgsArray = $args[1..($args.Length - 1)]
$javaArgsStr = $javaArgsArray -join ' '


# Create a separate argument for exec.args
$execArgsForJava = "-Dexec.args='$javaArgsStr'"

Write-Host "Maven Main Class Argument: $mavenMainClassArg"
Write-Host "Java Program Arguments: $javaArgsStr"

# Execute the Maven commands
mvn clean test-compile
mvn exec:java -PrunMain $mavenMainClassArg $execArgsForJava

# Execute mvn command
mvn clean test-compile exec:java $execArgs