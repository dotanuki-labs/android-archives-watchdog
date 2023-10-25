# Android Archives Watchdog 🐶

## Overview
`aaw` is command-line tool and a cross-over between functionalities from
[apkanalyzer](https://developer.android.com/tools/apkanalyzer) and 
[bundletool](https://developer.android.com/tools/bundletool). 


This utility has as goal helping with detection of newly introduced Android frameworks components 
in your release archives (`.apk` or `.aab`), especially transitive ones brought by 3rd party 
project dependencies, following a shift-left approach.

`aaw` is distributed as a 
[truly-executable](https://skife.org/java/unix/2011/06/20/really_executable_jars.html) 
[fatjar](https://imperceptiblethoughts.com/shadow/), and 
it's tested against `jdk11`, `jdk17` and `jdk21`. In addition, this project has end-to-end tests
targeting the following Android products with public open-source releases on Github:

- [DuckDuckGo](https://github.com/duckduckgo/Android)
- [ProntonMail](https://github.com/ProtonMail/proton-mail-android)
- [WooCommerce](https://github.com/woocommerce/woocommerce-android)
- [Mozilla Firefox](https://github.com/mozilla-mobile/firefox-android)

## Installing

> Github releases and installing instruction to come, stay tuned!

## Using

The following snippets use 
[ProntonMail](https://github.com/ProtonMail/proton-mail-android) 
releases as examples, in particular versions 
[3.0.7](https://github.com/ProtonMail/proton-mail-android/releases/tag/3.0.7) (November/2022) and
[3.0.17](https://github.com/ProtonMail/proton-mail-android/releases/tag/3.0.17) (October/2023)


Every command supports archives in `.apk` and `.aab` formats.

### Getting an overview from an Android archive

```bash
$> aaw overview -a tmp/ProtonMail-3.0.5.apk

┌────────────────────────────┬───────────────────────┐
│ Attribute                  │ Evaluation            │
├────────────────────────────┼───────────────────────┤
│ Application Id             │ ch.protonmail.android │
├────────────────────────────┼───────────────────────┤
│ Minimum SDK                │ 23                    │
├────────────────────────────┼───────────────────────┤
│ Target SDK                 │ 31                    │
├────────────────────────────┼───────────────────────┤
│ Total Used Features        │ 2                     │
├────────────────────────────┼───────────────────────┤
│ Total Manifest permissions │ 14                    │
├────────────────────────────┼───────────────────────┤
│ Dangerous permissions      │ Yes                   │
├────────────────────────────┼───────────────────────┤
│ Activities                 │ 54                    │
├────────────────────────────┼───────────────────────┤
│ Services                   │ 14                    │
├────────────────────────────┼───────────────────────┤
│ Broadcast Receivers        │ 15                    │
├────────────────────────────┼───────────────────────┤
│ Content Providers          │ 4                     │
└────────────────────────────┴───────────────────────┘
```
This mimics functionally from `apkanalyser` and supports a `--json` switch for automation purposes.

### Generating a baseline from an Android archive

```bash
$> aaw generate --archive=tmp/ProtonMail-3.0.7.apk

Baseline available at : ch.protonmail.android.toml

```

This command will produce a `<applicationId>.toml` file in the current directory, which is intended to be available in 
your VCS. This `toml` tracks a subset of information from the related merged `AndroidManifest.xml`, namely:

- Application Permissions
- Android Features
- Activities
- Services
- Content Providers
- Broadcast Receivers

Optionally, you can generate a compact version of a baseline by passing "trusted" packages, usually the ones
related to your project structure. Those must be passed in a single argument, comma (`,`) separated

```bash
$> aaw generate --archive=tmp/ProtonMail-3.0.7.apk --trusted='ch.protonmail,me.proton.core'

Baseline available at : ch.protonmail.android.toml

$> more ch.protonmail.android.toml

applicationId = "ch.protonmail.android"
permissions = [
    "android.permission.ACCESS_NETWORK_STATE",
    "android.permission.FOREGROUND_SERVICE",
    "android.permission.GET_ACCOUNTS",
    "android.permission.INTERNET",
    "android.permission.READ_CONTACTS",
    "android.permission.READ_EXTERNAL_STORAGE",
    "android.permission.RECEIVE_BOOT_COMPLETED",
    "android.permission.SCHEDULE_EXACT_ALARM",
    "android.permission.USE_BIOMETRIC",
    "android.permission.USE_FINGERPRINT",
    "android.permission.VIBRATE",
    "android.permission.WAKE_LOCK",
    "android.permission.WRITE_EXTERNAL_STORAGE",
    "com.google.android.c2dm.permission.RECEIVE"
]
features = [
    "android.hardware.faketouch",
    "android.hardware.screen.portrait"
]
trustedPackages = [
    "ch.protonmail",
    "me.proton.core"
]
activities = [
    "androidx.biometric.DeviceCredentialHandlerActivity",
    "com.google.android.gms.common.api.GoogleApiActivity"
]
 .
 .
 .

```

### Comparing an archive against a baseline

```bash
# Considering the baseline file generated in the previous example
$> aaw compare -a tmp/ProtonMail-3.0.17.apk -b ch.protonmail.android.toml

Your baseline file does not match the supplied artifact.

┌─────────────┬───────────────────────────────────────────────────────────────────┬──────────────────────┐
│ Category    │ Finding                                                           │ Description          │
├─────────────┼───────────────────────────────────────────────────────────────────┼──────────────────────┤
│ Permissions │ android.permission.POST_NOTIFICATIONS                             │ Not on your baseline │
├─────────────┼───────────────────────────────────────────────────────────────────┼──────────────────────┤
│ Permissions │ android.permission.READ_MEDIA_AUDIO                               │ Not on your baseline │
├─────────────┼───────────────────────────────────────────────────────────────────┼──────────────────────┤
│ Permissions │ android.permission.READ_MEDIA_IMAGES                              │ Not on your baseline │
├─────────────┼───────────────────────────────────────────────────────────────────┼──────────────────────┤
│ Permissions │ android.permission.READ_MEDIA_VIDEO                               │ Not on your baseline │
├─────────────┼───────────────────────────────────────────────────────────────────┼──────────────────────┤
│ Components  │ com.google.android.play.core.common.PlayCoreDialogWrapperActivity │ Not on your baseline │
├─────────────┼───────────────────────────────────────────────────────────────────┼──────────────────────┤
│ Components  │ androidx.profileinstaller.ProfileInstallReceiver                  │ Not on your baseline │
└─────────────┴───────────────────────────────────────────────────────────────────┴──────────────────────┘
```

This example illustrates how to track sensitive changes as part of your Continuous Integration, assuming that you have
a snapshot of your releasable archive produced at CI runtime. `compare` will exit with a failure status if a fresh 
archive does not match an existing baseline, forcing a baseline update as part of pull/merge request.

## Credits

> TODO

## License

Copyright (c) 2023 - Dotanuki Labs - [The MIT license](https://choosealicense.com/licenses/mit/)

