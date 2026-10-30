package dev.jason.gboardpatches.extension.customtheme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Describes an Rboard "pack" archive that bundles light and dark variants.
 *
 * A pack contains nested theme zips (e.g. KOKE_Dark.zip, KOKE_Light.zip) plus a
 * pack.meta file. Each inner zip is itself a normal Gboard theme with a
 * metadata.json carrying {@code is_light_theme}.
 */
public final class GboardCustomThemePack {
    public static final String PACK_META = "pack.meta";

    private final List<String> innerZipNames;

    private GboardCustomThemePack(List<String> innerZipNames) {
        this.innerZipNames = Collections.unmodifiableList(new ArrayList<>(innerZipNames));
    }

    public List<String> getInnerZipNames() { return innerZipNames; }

    public static boolean isPack(ZipFile zip) {
        boolean hasPackMeta = false;
        boolean hasInnerZip = false;
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) continue;
            String name = entry.getName();
            if (PACK_META.equals(name)) hasPackMeta = true;
            if (name.toLowerCase().endsWith(".zip")) hasInnerZip = true;
        }
        return hasPackMeta && hasInnerZip;
    }

    public static GboardCustomThemePack inspect(ZipFile zip) throws IOException {
        if (!isPack(zip)) throw new IOException("Not an Rboard pack archive");
        List<String> inner = new ArrayList<>();
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) continue;
            if (entry.getName().toLowerCase().endsWith(".zip")) inner.add(entry.getName());
        }
        if (inner.isEmpty()) throw new IOException("Pack contains no inner theme zips");
        return new GboardCustomThemePack(inner);
    }

    private GboardCustomThemePack() { this.innerZipNames = Collections.emptyList(); }
}