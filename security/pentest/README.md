
# JokeApp Security

Penetration testing for [jokeap android](https://github.com/ryjen/jokeapp-droid).

See [blog post](https://ryanjennin.gs/posts/friday-frida-hack/)

## Requirements

- frida
- jadx-gui
- rooted emulator

## Release Builds

To find which methods to hook in a release build you must decompile the apk in `jadx-gui` and run targets through `frida-trace`.

#### decompile

First run a case insensitive search of particular related keywords:

*Network*: service, repository, api, client, remote

*Database*: repository, dao, local, sql

TODO: insert screenshot of decompile

#### tracing 

Once you have found target decompiled methods you can trace them to see if they are called on the actions you want to intercept.

`frida-trace -U -j 'x4.a!*' Jiver`

Which would run a trace with the Jiver application on the USB device (`-U`) for the all (regex '*') Java methods in the class package 'x4.a' (`-j x4.a!*`)

TODO: insert screenshot of output

#### scripting

Once you have correctly identified the method to intercept, you can start scripting it to log, and return spoofed data.

`frida -U -f <package> <script>`


