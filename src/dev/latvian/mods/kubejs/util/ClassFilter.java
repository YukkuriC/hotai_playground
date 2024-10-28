package dev.latvian.mods.kubejs.util;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassFilter {
    public ClassFilter() {
    }

    public void deny(String s) {
    }

    public void deny(Class<?> c) {
    }

    public void allow(String s) {
    }

    public void allow(Class<?> c) {
    }

    public boolean isAllowed(String s) {
        return true;
    }
}
