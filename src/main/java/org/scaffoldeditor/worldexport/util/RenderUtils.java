package org.scaffoldeditor.worldexport.util;

public final class RenderUtils {
    private RenderUtils() {};

    /**
     * Takes an integer-based color value and makes the alpha channel {@code 255},
     * essentially stripping it of all transparency.
     * 
     * @param color The input color.
     * @return The color without any transparency.
     */
    public static int stripAlpha(int color) {
        // Thanks to ChatGPT for this snippet!
        return (color & 0x00FFFFFF) | 0xFF000000;
    }

    /**
     * Convert hue/saturation/value to an ARGB color. Hue is in degrees 0..360, saturation/value 0..1.
     */
    public static int hsvToARGB(float hueDeg, float saturation, float value) {
        float chroma = value * saturation;
        float huePrime = (hueDeg % 360f) / 60.0f;
        float intermediate = chroma * (1 - Math.abs(huePrime % 2 - 1));

        float r1, g1, b1;
        if (huePrime >= 0 && huePrime < 1) { r1 = chroma; g1 = intermediate; b1 = 0; }
        else if (huePrime < 2) { r1 = intermediate; g1 = chroma; b1 = 0; }
        else if (huePrime < 3) { r1 = 0; g1 = chroma; b1 = intermediate; }
        else if (huePrime < 4) { r1 = 0; g1 = intermediate; b1 = chroma; }
        else if (huePrime < 5) { r1 = intermediate; g1 = 0; b1 = chroma; }
        else { r1 = chroma; g1 = 0; b1 = intermediate; }

        float m = value - chroma;
        int r = Math.round((r1 + m) * 255);
        int g = Math.round((g1 + m) * 255);
        int b = Math.round((b1 + m) * 255);
        int a = 0xFF;
        return (a << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}
