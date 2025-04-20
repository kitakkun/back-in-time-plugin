# idea-plugin

This module provides an IntelliJ IDEA plugin implementation for Back In Time.

This plugin currently not available from official JetBrains plugin repository yet.
You need to manually build and install it to your IDE.

## How to install plugin manually

Run the following command in the root directory of this module:

```shell
./gradlew buildPlugin
```

Then you can find the plugin archive in `tooling/idea-plugin/build/distributions` directory.
Open the IDE settings and navigate to `Plugins` > `Install Plugin from Disk...` and select the archive.

Standalone version of the plugin built over Compose for Desktop is also available in `tooling/standalone`.
