package dev.jason.gboardpatches.extension.customtheme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Extracts the inner theme zips of an Rboard pack into separate files.
 */
public final class GboardCustomThemePackVariants {
    public static final class Variant {
        public final File file;
        public final boolean light;
        public Variant(File file, boolean light) { this.file = file; this.light = light; }
    }

    private GboardCustomThemePackVariants() { }

    public static List<Variant> extract(File pack, File outputDir) throws Exception {
        List<Variant> result = new ArrayList<>();
        try (ZipFile zip = new ZipFile(pack)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().toLowerCase().endsWith(".zip")) continue;
                File out = new File(outputDir, new File(entry.getName()).getName());
                try (InputStream in = zip.getInputStream(entry);
                     FileOutputStream fos = new FileOutputStream(out)) {
                    byte[] buf = new byte[32 * 1024];
                    int r;
                    while ((r = in.read(buf)) >= 0) if (r > 0) fos.write(buf, 0, r);
                }
                boolean light;
                try (ZipFile inner = new ZipFile(out)) {
                    light = GboardCustomThemeMetadata.isLightTheme(inner);
                }
                result.add(new Variant(out, light));
            }
        }
        return result;
    }
}