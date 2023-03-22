declare var Pax: {
  FULL_CUT: number;
  PARTIAL_CUT: number;

  printStr: (type: string,url: string,text: string, cutMode?: number,text2:string) => void;
  openDrawer: () => Promise<any>;
};

export default Pax;
