package guepardoapps.mynoteencrypted.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;

public class Tools {
    private static final String TAG = Tools.class.getSimpleName();

    public static String CheckLength(@NonNull String entry) {
        while (entry.length() < 2) {
            entry = "0" + entry;
        }
        return entry;
    }

    public static Paint CreateDefaultPaint(@NonNull Canvas canvas, int textSize) {
        Paint paint = new Paint();
        paint.setColor(0x00000000);
        paint.setStyle(Style.FILL);

        canvas.drawPaint(paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);

        return paint;
    }

    public static Bitmap GetCircleBitmap(@NonNull Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.BLACK;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
}
