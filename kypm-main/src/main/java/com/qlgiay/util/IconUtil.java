package com.qlgiay.util;

import com.formdev.flatlaf.util.UIScale;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class IconUtil {
    private static final Map<String, Icon> CACHE = new HashMap<>();

    private IconUtil() {}

    public static Icon loadPng(String basePath, int baseSize) {
        int size = UIScale.scale(baseSize);
        String key = basePath + "@" + size;

        Icon cached = CACHE.get(key);
        if (cached != null) return cached;

        String path2x = basePath.replace(".png", "@2x.png");
        String chosen = resourceExists(path2x) && size > baseSize ? path2x : basePath;

        ImageIcon icon = readAndScale(chosen, size);
        if (icon != null) CACHE.put(key, icon);
        return icon;
    }

    private static boolean resourceExists(String path) {
        return IconUtil.class.getResource(path) != null;
    }

    private static ImageIcon readAndScale(String path, int size) {
        try (InputStream is = IconUtil.class.getResourceAsStream(path)) {
            if (is == null) return null;

            BufferedImage img = ImageIO.read(is);
            if (img == null) return null;

            Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }
}