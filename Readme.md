##Mongoose ported to Android

This is a port of [Mongoose](http://code.google.com/p/mongoose/) web server to Android. Runs only on rooted phones.

Currently it is a Cross-compiled executable with an interface to edit the command-line options for mongoose and to start/stop the web server.

The error log is stored in /data/local/downloads/ of your webserver.

To build the cross-compiled binary, refer to the [BuildOnAndroid](http://code.google.com/p/mongoose/wiki/BuildOnAndroid) page.

For this Java project, the binary is placed in the res/raw folder and is copied to /data/local folder on the device when the program runs.

TODO:
- Provide an interface to the command line options
- Currently stopping the server is a dirty hack with ps | grep mongoose and kill -9. But Java is a bitch and does not give me the process' id without delving into the command line stuff.
- Current version executes a binary. A more elegant way would be to write JNI code with which Java can interact with directly.
- Try for no-root.