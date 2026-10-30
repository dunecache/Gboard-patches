package dev.jason.gboardpatches.extension.customtheme;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Reads a Gboard/Rboard theme's metadata.json. */
public final class GboardCustomThemeMetadata {
    private GboardCustomThemeMetadata() { }

    public static boolean isLightTheme(ZipFile zip) {
        ZipEntry entry = zip.getEntry("metadata.json");
        if (entry == null) return true; // unknown -> assume light
        try (InputStream in = zip.getInputStream(entry)) {
            byte[] buf = new byte[(int) entry.getSize()];
            int read = 0;
            while (read < buf.length) {
                int r = in.read(buf, read, buf.length - read);
                if (r < 0) break;
                read += r;
            }
            JSONObject json = new JSONObject(new String(buf, 0, read, "UTF-8"));
            if (json.has("is_light_theme")) return json.optBoolean("is_light_theme", true);
            if (json.has("isDark")) return !json.optBoolean("isDark", false);
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}