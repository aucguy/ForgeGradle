package com.github.aucguy.optifinegradle;

import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.DIR_PROJECT_CACHE;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.REPLACE_PROJECT_CAP_NAME;
import static net.minecraftforge.gradle.common.Constants.REPLACE_MC_VERSION;

/**
 * random constants
 */
public class OptifineConstants
{
    // normal setup
    // general
    public static final String EXTENSION              = "optifine";
    public static final String GROUP_OPTIFINE         = "Optifine";
    
    // tasks
    public static final String TASK_JOIN_JARS         = "joinJars";        
    public static final String TASK_DIFF_OPTIFINE     = "diffOptifine";
    public static final String TASK_DIFF_EXEC         = "diffExecOptifine";
    public static final String TASK_DL_PATCHES        = "dlPatches";
    public static final String TASK_EXTRACT_RENAMES   = "extractRenames";
    public static final String TASK_ASK_PERMISSION    = "askPermission";
    public static final String TASK_PREPROCESS        = "preprocess";
    
    //patching tasks
    public static final String TASK_PROJECT_DELETE_REJECTS    = "delete" + REPLACE_PROJECT_CAP_NAME + "Rejects";
    public static final String TASK_PROJECT_REMAP_REJECTS     = "remap" + REPLACE_PROJECT_CAP_NAME + "Rejects";
    public static final String TASK_PROJECT_EXTRACT_REJECTS   = "extract" + REPLACE_PROJECT_CAP_NAME + "Rejects";
    
    //files
    public static final String JAR_OPTIFINE_FRESH     = "{optifineJar}";
    public static final String JAR_CLIENT_JOINED      = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-joined.jar";
    public static final String JAR_OPTIFINE_DIFFED    = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-diffed.jar";
    public static final String OBFUSCATED_CLASSES     = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-obfuscatedClasses.txt";
    public static final String DEOBFUSCATED_CLASSES   = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-deobfuscatedClasses.txt";
    public static final String JAR_PREPROCESS       = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-preprocessed.jar";
    
    public static final String OPTIFINE_CACHE         = "{CACHE_DIR}/com/github/aucguy/optifinegradle/";
    
    public static final String PATCH_ZIP              = OPTIFINE_CACHE + "{patchArchive}";
    public static final String RENAMES_BASE           = "optifine-renames.properties";
    public static final String RENAMES_FILE           = OPTIFINE_CACHE + RENAMES_BASE;
    
    //patching files
    public static final String PATCHES_DIR            = "patches/";
    public static final String PATCH_RENAMES          = PATCHES_DIR + RENAMES_BASE;
    public static final String USER_RENAMES           = PATCH_ZIP + "/" + RENAMES_BASE;
    
    public static final String PATCH_ARCHIVE          = "{patchArchive}";
    public static final String PATCH_URL              = "{patchUrl}";
    
    public static final String PROJECT_REJECTS_ZIP            = DIR_PROJECT_CACHE + "/rejects.zip";
    public static final String PROJECT_REMAPPED_REJECTS_ZIP   = DIR_PROJECT_CACHE + "/rejects-remapped.zip";
    
    // defaults
    public static final String DEFAULT_OPTIFINE_VERSION = "1.12.2_HD_U_C7";
    public static final String DEFAULT_PATCH_URL      = "https://aucguy.github.io/downloads/optifinegradle/patches/{patchArchive}";
    public static final String DEFAULT_PATCH_ARCHIVE  = "patches-{optifineVersion}.zip";
    public static final String DEFAULT_OPTIFINE_JAR   = "{mainDir}/OptiFine_{optifineVersion}.jar";
    
    // replacements
    public static final String REPLACE_OPTIFINE_VERSION = "{optifineVersion}";
    public static final String REPLACE_PATCH_ARCHIVE  = "{patchArchive}";
    public static final String REPLACE_PATCH_URL      = "{patchUrl}";
    public static final String REPLACE_OPTIFINE_JAR   = "{optifineJar}";
    public static final String REPLACE_MAIN_DIR       = "{mainDir}";
    
    // packaging setup tasks
    public static final String TASK_OPTIFINE_PATCH    = "optifinePatch";
    public static final String TASK_GEN_PATCHES       = "genOptifinePatches";
    public static final String TASK_ZIP_PATCHES       = "zipOptifinePatches";
    
    // user visible files
    public static final String OPTIFINE_PATCH_ZIP     = "{BUILD_DIR}/optifine/{patchArchive}";
    public static final String OPTIFINE_PATCH_DIR     = "{BUILD_DIR}/optifine/patches";
}
