/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Script Manager, an app to create, import, edit
 * and easily execute any properly formatted shell scripts.
 *
 */

package com.smartpack.scriptmanager.utils;

import android.content.Context;

import com.smartpack.scriptmanager.utils.root.RootFile;
import com.smartpack.scriptmanager.utils.root.RootUtils;

import java.io.File;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 12, 2020
 */

public class Scripts {

    private static final String SCRIPTS = Utils.getInternalDataStorage();
    private static final String MAGISK_SERVICED = "/data/adb/service.d";
    private static final String MAGISK_POSTFS = "/data/adb/post-fs-data.d";

    public static StringBuilder mOutput = null;

    public static boolean mApplyingScript = false;

    public static File ScriptFile() {
        return new File(SCRIPTS);
    }
    public static File MagiskServiceFile() {
        return new File(MAGISK_SERVICED);
    }
    public static File MagiskPostFSFile() {
        return new File(MAGISK_POSTFS);
    }

    public static List<String> scriptItems() {
        RootFile file = new RootFile(ScriptFile().toString());
        if (!file.exists()) {
            file.mkdir();
        }
        return file.list();
    }

    private static void makeScriptFolder() {
        if (ScriptFile().exists() && ScriptFile().isFile()) {
            ScriptFile().delete();
        }
        ScriptFile().mkdirs();
    }

    public static void importScript(String string, Context context) {
        makeScriptFolder();
        RootUtils.runCommand("cp " + string + " " + SCRIPTS);
        Utils.getInstance().showInterstitialAd(context);
    }

    public static void createScript(String file, String text, Context context) {
        makeScriptFolder();
        RootFile f = new RootFile(file);
        f.write(text, false);
        Utils.getInstance().showInterstitialAd(context);
    }

    public static void deleteScript(String path, Context context) {
        File file = new File(path);
        file.delete();
        if (Utils.existFile(MagiskServiceFile() + "/" + file.getName())) {
            RootUtils.runCommand("rm -r " + MagiskServiceFile() + "/" + file.getName());
        }
        Utils.getInstance().showInterstitialAd(context);
    }

    public static String applyScript(String file) {
        RootUtils.runCommand("sleep 1");
        mOutput.append("********************\n Checking Output!\n********************\n\n");
        return RootUtils.runCommand("sh " + file);
    }

    public static String readScript(String file) {
        return Utils.readFile(file);
    }

    public static boolean isScript(String file) {
        return Utils.getExtension(file).equals("sh") && readScript(file).startsWith("#!/system/bin/sh");
    }

    public static String scriptExistsCheck(String script) {
        return ScriptFile().toString() + "/" + script;
    }

    public static boolean isMgiskService() {
        return Utils.existFile(MAGISK_SERVICED) ||
                Utils.existFile(MAGISK_POSTFS);
    }

    public static void setScriptOnServiceD(String string, String name, Context context) {
        Utils.copy(string, MAGISK_SERVICED);
        Utils.chmod("755", MAGISK_SERVICED + "/" + name);
        Utils.getInstance().showInterstitialAd(context);
    }

    public static void setScriptOnPostFS(String string, String name, Context context) {
        Utils.copy(string, MAGISK_POSTFS);
        Utils.chmod("755", MAGISK_POSTFS + "/" + name);
        Utils.getInstance().showInterstitialAd(context);
    }

    public static boolean scriptOnPostBoot(String path) {
        return Utils.existFile(MAGISK_POSTFS + "/" + path);
    }

    public static boolean scriptOnLateBoot(String path) {
        return Utils.existFile(MAGISK_SERVICED + "/" + path);
    }

}