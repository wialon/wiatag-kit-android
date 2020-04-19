# WiaTagKit
WiaTagKit is a library that will help developers to build communication with Wialon platform using WiaTag binary protocol.

## Installation

Declare the following permission in your Android Manifest file:
```
<!-- Required permissions -->
<uses-permission android:name="android.permission.INTERNET" />
```

Get latest aar:
- Download from [Maven Central][maven]  
- grab via Gradle: ``` implementation 'com.gurtam:wiatag-kit:1.0.3' ```
- or Maven:
```
<dependency>
  <groupId>com.gurtam</groupId>
  <artifactId>wiatag-kit</artifactId>
  <version>1.0.3</version>
</dependency>
```
## Example
First you need to initialize MessageManager with Context, Host, Port, DeviceID and Password.
If you are not using Local, then use 193.193.165.165 for host and 20963 for port. Else use your Local settings.
```
MessageManager.initWithHost(context,"193.193.165.165",20963,"123456","unitpassword");
```
After initialization is done you may prepare message you want to send:
```
Message message = new Message().time(new Date().getTime()).Sos();
```
and send it:
```
MessageManager.sendMessage(message, new MessageSenderListener() {
        @Override
        protected void onSuccess() {
            super.onSuccess();
        }
        @Override
        protected void onFailure(final byte errorCode) {
            super.onFailure(errorCode);
        }
    });
```


## Author

Gurtam, development@gurtam.com

## License

WiaTagKit is available under the CC BY-ND 4.0 license. See the LICENSE file for more info.


[maven]: <https://search.maven.org/search?q=g:com.gurtam%20AND%20a:wiatag-kit>