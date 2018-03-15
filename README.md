# usb4java.example
usb4java open source usages focus on differences between windows and linux

usb4java는 libusb를 java 언어로 사용할 수 있도록 고안한 라이브러리입니다.
이 곳에서는 Windows 환경과 Linux 환경에서 사용상에 어떤차이가 있는지 확인하기 위한 목적으로 만들어졌습니다.
gradle을 통해서 빌드하고 테스트를 통해 usb4java의 usage를 확인할 수 있도록 하였습니다.

## Build
`./gradlew clean test`  

## Remote Copy
저는 Windows 에서 Intellij를 통해서 개발하고 있습니다. Linux에서 빌드하기 위해서 편의를 위해 빌드 후 Linux machine으로 소스를 복사하는
task를 만들어서 사용하고 있습니다. 보통은 Windows에서 빌드되는지 먼저 확인하고 task를 통해 리눅스로 소스를 넘겨 다시 빌드하여 test를 돌려봅니다.
 
*Windows*  
`./gradlew clean build scp -x test`  
  
*linux*  
`./gradlew clean test`  
 
