# flipper-plugin-back-in-time

[English | [日本語](README-ja.md)]

This plugin enables back-in-time debugging through Flipper for compiled programs
using [back-in-time-plugin](https://github.com/kitakkun/back-in-time-plugin).

![screenshot](docs/assets/main-screenshot.png)

## Demo

### Property Inspection

https://github.com/kitakkun/flipper-plugin-back-in-time/assets/48154936/0bf7159b-3aea-49d1-bfee-8dca908c1b68

### Tracking Changes

https://github.com/kitakkun/flipper-plugin-back-in-time/assets/48154936/ad57283e-dee4-4bb2-a025-e7f12f2e61d4

### Back-in-time State

https://github.com/kitakkun/flipper-plugin-back-in-time/assets/48154936/dd3bb86a-d366-44da-b25a-b323ca7dc570

## Installation

This project is still under development and has not been officially released.
Clone the repository and run the following command in the root directory.

```sh
yarn pack
```

This will generate flipper-plugin-back-in-time-vx.x.x.tgz file.
Please add it manually to Flipper (To do so, navigate to More -> Add Plugins -> Install Plugins -> Select a flipper
package).
