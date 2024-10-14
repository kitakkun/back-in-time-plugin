// See https://github.com/facebook/flipper/pull/3327 for why we need this
// @ts-ignore

global.electronRequire = require;
require("@testing-library/react");
// fix test fails with "TypeError: crypto.randomUUID is not a function"
import {randomUUID} from "node:crypto";

window.crypto.randomUUID = randomUUID;

