
package com.reactpaxlibrary;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.pax.dal.ICashDrawer;
import com.pax.dal.IDAL;
import com.pax.dal.IPrinter;
import com.pax.neptunelite.api.NeptuneLiteUser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.net.*;

public class RNPaxLibraryModule extends ReactContextBaseJavaModule {

    private static final String NAME = "Pax";
    private final ReactApplicationContext reactContext;

    private IDAL dal;
    private IPrinter printer;
    private ICashDrawer cashDrawer;

    public RNPaxLibraryModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        try {
            dal = NeptuneLiteUser.getInstance().getDal(reactContext);
            printer = dal.getPrinter();
            cashDrawer = dal.getCashDrawer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void printStr(String type,String picUrl,String text, Double cutMode, String text2) {
    
        try {

            if(!text2.equals("") && !picUrl.equals("")){
                final String encodedString = picUrl;
                final String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",")  + 1);
                final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);


                Bitmap bitmapWithoutAlpha = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),Bitmap.Config.RGB_565); 
                Canvas canvas = new Canvas(bitmapWithoutAlpha); 
                canvas.drawColor(Color.WHITE); 
                Paint paint = new Paint(); 
                paint.setAntiAlias(true); 
                canvas.drawBitmap(bitmap, 0f, 0f, paint); 
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                bitmapWithoutAlpha.compress(CompressFormat.PNG, 100, baos);
                byte[] bmpImageData = baos.toByteArray(); 

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;


                printer.init();
                printer.setGray(6);

                printer.printStr(text, null);
                printer.printBitmap(BitmapFactory.decodeByteArray(bmpImageData, 0, bmpImageData.length, options));
                printer.printStr(text2, null);
            }else {

                printer.init();
                printer.setGray(6);
                printer.printStr(text, null);

            }

            this.leftIndents(Short.parseShort("55"));
           if(!type.equals("pre")){
            printer.printStr("\n\n\n\n\n\n", null);
           }
              
            printer.start();
            printer.cutPaper(cutMode.intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    @ReactMethod
    public void leftIndents(short indent) {
    try {
        printer.leftIndent(indent);
        //logTrue("leftIndent");
    } catch (Exception e) {
        // e.printStackTrace();
        //logErr("leftIndent", e.toString());
    }
    }

    @ReactMethod
    public void fontSet(EFontTypeAscii asciiFontType, EFontTypeExtCode cFontType) {
    try {
        printer.fontSet(asciiFontType, cFontType);
        //logTrue("fontSet");
    } catch (Exception e) {
        e.printStackTrace();
        //logErr("fontSet", e.toString());
    }

    }

    @ReactMethod
    public void openDrawer(Promise promise) {
        final int result = cashDrawer.open();

        if (result == 0) {
            promise.resolve(result);
        } else {
            promise.reject("Error "+ result, "The cash drawer cannot be opened.");
        }
    }
}