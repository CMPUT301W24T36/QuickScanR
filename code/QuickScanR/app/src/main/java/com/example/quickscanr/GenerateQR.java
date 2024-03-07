package com.example.quickscanr;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GenerateQR {
    /**
     * Generates a QR code bitmap from given text.
     *
     * @param text The content to encode in the QR code.
     * @param width The width of the QR code in pixels.
     * @param height The height of the QR code in pixels.
     * @return A bitmap containing the generated QR code.
     * @throws WriterException If an error occurs during QR code generation.
     */
    public static Bitmap generateQRCode(String text, int width, int height) throws WriterException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        return barcodeEncoder.createBitmap(bitMatrix);
    }
}