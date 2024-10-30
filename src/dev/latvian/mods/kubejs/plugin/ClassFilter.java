package dev.latvian.mods.kubejs.plugin;

import dev.latvian.mods.kubejs.script.ScriptType;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassFilter {
    public final ScriptType scriptType;

    public ClassFilter(ScriptType scriptType) {
        this.scriptType = scriptType;
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
