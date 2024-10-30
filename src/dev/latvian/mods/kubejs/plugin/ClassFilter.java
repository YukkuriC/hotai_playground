package dev.latvian.mods.kubejs.plugin;

import dev.latvian.mods.kubejs.script.ScriptType;

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
