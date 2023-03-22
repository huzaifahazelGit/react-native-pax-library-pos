import { NativeModules } from "react-native";

const { Pax } = NativeModules;

export default {
  FULL_CUT: 0,
  PARTIAL_CUT: 1,

  printStr(type, url, text, cutMode, text2) {
    Pax.printStr(type, url, text, cutMode === undefined ? 0 : cutMode, text2);
  },
  openDrawer() {
    return Pax.openDrawer();
  },
};
