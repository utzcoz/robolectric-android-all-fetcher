# robolectric-android-all-fetcher

## shell

It contains some scripts to fetch robolectric android-all dependencies in China with speed up maven repository

```shell
cd shell
source fetch.sh
```

## gradle

It contains Gradle projects to extract android-all jars metadata. There are two supported commands:

### `./gradlew :extractor:updateAndroidAllJarsMetadata`

Extract all android-all dependencies from the list defined in `build.gradle.kts` and update them
to scripts in shell.

### `./gradlew :extractor:retrieveRobolectricVersionForBazel`

Extract sha256 for all supported android-all-instrumented jars and print the format with these data
to the console.

See https://github.com/robolectric/robolectric-bazel/blob/master/bazel/robolectric.bzl for the reason
that this task is added.

### How to update supported android-all data?

When there is a new Robolectric release, see its `AndroidSdk.kt` in source tree to get all used android-all
jars of this release, and sync it to related `build.gradle.kts`.
