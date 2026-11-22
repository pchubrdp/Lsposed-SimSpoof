package com.example.simspoof;

import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SimSpoofModule implements IXposedHookLoadPackage {

    private static final String TAG = "SimSpoof";

    // Set your desired spoofed number here (include country code if the target app expects +91...)
    private static final String SPOOFED_NUMBER = "+919876543210";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Do NOT restrict to "android" - hook whatever target apps you enable in LSPosed Scope
        if (lpparam.packageName.equals("android")) {
            return;
        }

        XposedBridge.log(TAG + ": Active in " + lpparam.packageName);
        hookPhoneNumberOnly();
    }

    private void hookPhoneNumberOnly() {
        // 1. TelephonyManager.getLine1Number()
        try {
            XposedHelpers.findAndHookMethod(
                TelephonyManager.class,
                "getLine1Number",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        XposedBridge.log(TAG + ": Spoofed getLine1Number()");
                        return SPOOFED_NUMBER;
                    }
                }
            );
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": getLine1Number() hook skipped: " + t.getMessage());
        }

        // 2. Hidden overload TelephonyManager.getLine1Number(int subId) used on Android 12 / MIUI 14
        try {
            XposedHelpers.findAndHookMethod(
                TelephonyManager.class,
                "getLine1Number",
                int.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        XposedBridge.log(TAG + ": Spoofed getLine1Number(subId=" + param.args[0] + ")");
                        return SPOOFED_NUMBER;
                    }
                }
            );
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": getLine1Number(int) hook skipped: " + t.getMessage());
        }

        // 3. SubscriptionInfo.getNumber() (works cleanly because getActiveSubscriptionInfoList is NOT nulled)
        try {
            XposedHelpers.findAndHookMethod(
                SubscriptionInfo.class,
                "getNumber",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        XposedBridge.log(TAG + ": Spoofed SubscriptionInfo.getNumber()");
                        return SPOOFED_NUMBER;
                    }
                }
            );
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": SubscriptionInfo.getNumber() hook skipped: " + t.getMessage());
        }

        // 4. SubscriptionManager.getPhoneNumber(int) for Android 13+ MIUI ports (skipped safely on stock Android 12)
        if (Build.VERSION.SDK_INT >= 33) {
            try {
                XposedHelpers.findAndHookMethod(
                    SubscriptionManager.class,
                    "getPhoneNumber",
                    int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            XposedBridge.log(TAG + ": Spoofed SubscriptionManager.getPhoneNumber(int)");
                            return SPOOFED_NUMBER;
                        }
                    }
                );
            } catch (Throwable t) {
                XposedBridge.log(TAG + ": getPhoneNumber(int) hook skipped: " + t.getMessage());
            }
        }
    }
}
) {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing getSimOperator");
                        return SPOOFED_SIM_OPERATOR;
                    }
                });
                
            XposedHelpers.findAndHookMethod(TelephonyManager.class, "getSimOperatorName",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing getSimOperatorName");
                        return SPOOFED_SIM_OPERATOR_NAME;
                    }
                });
            
            // === Device Information ===
            XposedHelpers.findAndHookMethod(TelephonyManager.class, "getDeviceId",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing getDeviceId (IMEI)");
                        return SPOOFED_DEVICE_ID;
                    }
                });
                
            XposedHelpers.findAndHookMethod(TelephonyManager.class, "getDeviceSoftwareVersion",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing getDeviceSoftwareVersion");
                        return SPOOFED_DEVICE_SOFTWARE_VERSION;
                    }
                });
                
            XposedHelpers.findAndHookMethod(TelephonyManager.class, "getGroupIdLevel1",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing getGroupIdLevel1");
                        return SPOOFED_GROUP_ID_LEVEL1;
                    }
                });
                
            // === Multi-SIM Support (Android 5.1+) ===
            try {
                // Hook methods with subscription ID parameter
                XposedHelpers.findAndHookMethod(TelephonyManager.class, "getSimSerialNumber", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log(TAG + ": Spoofing getSimSerialNumber(int) for subId: " + param.args[0]);
                            return SPOOFED_ICCID;
                        }
                    });
                    
                XposedHelpers.findAndHookMethod(TelephonyManager.class, "getSubscriberId", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log(TAG + ": Spoofing getSubscriberId(int) for subId: " + param.args[0]);
                            return SPOOFED_IMSI;
                        }
                    });
                    
                XposedHelpers.findAndHookMethod(TelephonyManager.class, "getLine1Number", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log(TAG + ": Spoofing getLine1Number(int) for subId: " + param.args[0]);
                            return SPOOFED_LINE1_NUMBER;
                        }
                    });
                    
                XposedHelpers.findAndHookMethod(TelephonyManager.class, "getNetworkOperator", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log(TAG + ": Spoofing getNetworkOperator(int) for subId: " + param.args[0]);
                            return SPOOFED_NETWORK_OPERATOR;
                        }
                    });
                    
                XposedHelpers.findAndHookMethod(TelephonyManager.class, "getSimOperator", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log(TAG + ": Spoofing getSimOperator(int) for subId: " + param.args[0]);
                            return SPOOFED_SIM_OPERATOR;
                        }
                    });
                    
                XposedHelpers.findAndHookMethod(TelephonyManager.class, "getSimCountryIso", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log(TAG + ": Spoofing getSimCountryIso(int) for subId: " + param.args[0]);
                            return SPOOFED_SIM_COUNTRY_ISO;
                        }
                    });
                    
            } catch (Exception e) {
                XposedBridge.log(TAG + ": Multi-SIM methods not available (older Android): " + e.getMessage());
            }
            
            // === Alternative methods (different Android versions) ===
            try {
                // Some OEMs or Android versions might use different method names
                XposedHelpers.findAndHookMethod(TelephonyManager.class, "getSubscriberIdForSubscription", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log(TAG + ": Spoofing getSubscriberIdForSubscription");
                            return SPOOFED_IMSI;
                        }
                    });
                    
                XposedHelpers.findAndHookMethod(TelephonyManager.class, "getLine1NumberForSubscriber", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log(TAG + ": Spoofing getLine1NumberForSubscriber");
                            return SPOOFED_LINE1_NUMBER;
                        }
                    });
                    
            } catch (Exception e) {
                // These methods might not exist on all Android versions
                XposedBridge.log(TAG + ": Alternative methods not available: " + e.getMessage());
            }
                
        } catch (Exception e) {
            XposedBridge.log(TAG + ": Error hooking TelephonyManager: " + e.getMessage());
        }
    }
    
    private void hookSubscriptionManager(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // Hook SubscriptionManager - return null to hide subscription info
            XposedHelpers.findAndHookMethod(SubscriptionManager.class, "getActiveSubscriptionInfoList",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Intercepting getActiveSubscriptionInfoList - returning null");
                        return null; // Hide all subscription info
                    }
                });
                
            // Hook SubscriptionInfo methods for when apps do get subscription objects
            XposedHelpers.findAndHookMethod(SubscriptionInfo.class, "getIccId",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing SubscriptionInfo.getIccId");
                        return SPOOFED_ICCID;
                    }
                });
                
            XposedHelpers.findAndHookMethod(SubscriptionInfo.class, "getNumber",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing SubscriptionInfo.getNumber");
                        return SPOOFED_PHONE_NUMBER;
                    }
                });
                
            XposedHelpers.findAndHookMethod(SubscriptionInfo.class, "getCountryIso",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing SubscriptionInfo.getCountryIso");
                        return SPOOFED_COUNTRY_ISO;
                    }
                });
                
            XposedHelpers.findAndHookMethod(SubscriptionInfo.class, "getMcc",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing SubscriptionInfo.getMcc");
                        return Integer.parseInt(SPOOFED_NETWORK_OPERATOR.substring(0, 3)); // Extract MCC
                    }
                });
                
            XposedHelpers.findAndHookMethod(SubscriptionInfo.class, "getMnc",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing SubscriptionInfo.getMnc");
                        return Integer.parseInt(SPOOFED_NETWORK_OPERATOR.substring(3)); // Extract MNC
                    }
                });
                
            XposedHelpers.findAndHookMethod(SubscriptionInfo.class, "getCarrierName",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing SubscriptionInfo.getCarrierName");
                        return SPOOFED_NETWORK_OPERATOR_NAME;
                    }
                });
                
            XposedHelpers.findAndHookMethod(SubscriptionInfo.class, "getDisplayName",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Spoofing SubscriptionInfo.getDisplayName");
                        return SPOOFED_NETWORK_OPERATOR_NAME;
                    }
                });
                
        } catch (Exception e) {
            XposedBridge.log(TAG + ": Error hooking SubscriptionManager: " + e.getMessage());
        }
    }
    
    private void hookStringComparisons(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // Hook String.equals
            XposedHelpers.findAndHookMethod(String.class, "equals", Object.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String thisStr = (String) param.thisObject;
                        Object otherObj = param.args[0];
                        
                        if (otherObj instanceof String) {
                            String otherStr = (String) otherObj;
                            
                            for (String detectionStr : DETECTION_STRINGS) {
                                if (detectionStr.equals(thisStr) || detectionStr.equals(otherStr)) {
                                    XposedBridge.log(TAG + ": Blocking string comparison: " + thisStr + " vs " + otherStr);
                                    param.setResult(false);
                                    return;
                                }
                            }
                        }
                    }
                });
                
            // Hook String.contains
            XposedHelpers.findAndHookMethod(String.class, "contains", CharSequence.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String thisStr = (String) param.thisObject;
                        CharSequence sequence = (CharSequence) param.args[0];
                        
                        if (sequence != null) {
                            String seqStr = sequence.toString();
                            
                            for (String detectionStr : DETECTION_STRINGS) {
                                if (thisStr.contains(detectionStr) || seqStr.contains(detectionStr)) {
                                    XposedBridge.log(TAG + ": Blocking string contains: " + thisStr + " contains " + seqStr);
                                    param.setResult(false);
                                    return;
                                }
                            }
                        }
                    }
                });
                
            // Hook TextUtils.equals for Android-specific comparisons
            XposedHelpers.findAndHookMethod(TextUtils.class, "equals", CharSequence.class, CharSequence.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        CharSequence a = (CharSequence) param.args[0];
                        CharSequence b = (CharSequence) param.args[1];
                        
                        if (a != null && b != null) {
                            String strA = a.toString();
                            String strB = b.toString();
                            
                            for (String detectionStr : DETECTION_STRINGS) {
                                if (detectionStr.equals(strA) || detectionStr.equals(strB)) {
                                    XposedBridge.log(TAG + ": Blocking TextUtils comparison: " + strA + " vs " + strB);
                                    param.setResult(false);
                                    return;
                                }
                            }
                        }
                    }
                });
                
        } catch (Exception e) {
            XposedBridge.log(TAG + ": Error hooking string comparisons: " + e.getMessage());
        }
    }
    
    private void hookSystemCalls(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // Hook Thread.getName to hide detection threads
            XposedHelpers.findAndHookMethod(Thread.class, "getName",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String name = (String) param.getResult();
                        
                        if (name != null) {
                            for (String detectionStr : DETECTION_STRINGS) {
                                if (name.toLowerCase().contains(detectionStr)) {
                                    XposedBridge.log(TAG + ": Hiding thread name: " + name);
                                    param.setResult("app_worker_" + new Random().nextInt(1000));
                                    return;
                                }
                            }
                        }
                    }
                });
                
            // Hook Runtime.exec
            XposedHelpers.findAndHookMethod(Runtime.class, "exec", String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String command = (String) param.args[0];
                        
                        if (command != null) {
                            for (String rootBinary : ROOT_BINARIES) {
                                if (command.contains(rootBinary)) {
                                    XposedBridge.log(TAG + ": Blocking root check command: " + command);
                                    param.setThrowable(new IOException("Cannot run program \"" + command + "\": error=2, No such file or directory"));
                                    return;
                                }
                            }
                        }
                    }
                });
                
        } catch (Exception e) {
            XposedBridge.log(TAG + ": Error hooking system calls: " + e.getMessage());
        }
    }
    
    private void hookFileOperations(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // Hook File.exists with more comprehensive path filtering
            XposedHelpers.findAndHookMethod(File.class, "exists",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        File file = (File) param.thisObject;
                        String path = file.getAbsolutePath();
                        
                        // Hide sensitive system paths
                        for (String sensitivePath : SENSITIVE_PATHS) {
                            if (path.contains(sensitivePath)) {
                                XposedBridge.log(TAG + ": Hiding sensitive path: " + path);
                                param.setResult(false);
                                return;
                            }
                        }
                        
                        // Hide detection strings in paths
                        for (String detectionStr : DETECTION_STRINGS) {
                            if (path.toLowerCase().contains(detectionStr.toLowerCase())) {
                                XposedBridge.log(TAG + ": Hiding detection path: " + path);
                                param.setResult(false);
                                return;
                            }
                        }
                        
                        // Hide root-related files
                        for (String rootBinary : ROOT_BINARIES) {
                            if (path.contains(rootBinary)) {
                                XposedBridge.log(TAG + ": Hiding root file: " + path);
                                param.setResult(false);
                                return;
                            }
                        }
                    }
                });
                
            // Hook BufferedReader.readLine for /proc/maps filtering (like Hide-My-Applist)
            XposedHelpers.findAndHookMethod(BufferedReader.class, "readLine",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String line = (String) param.getResult();
                        
                        if (line != null) {
                            // Filter detection strings from file contents
                            for (String detectionStr : DETECTION_STRINGS) {
                                if (line.toLowerCase().contains(detectionStr.toLowerCase())) {
                                    XposedBridge.log(TAG + ": Filtering line containing: " + detectionStr);
                                    // Skip this line and try to read next one
                                    try {
                                        BufferedReader reader = (BufferedReader) param.thisObject;
                                        String nextLine = reader.readLine();
                                        param.setResult(nextLine);
                                    } catch (Exception e) {
                                        param.setResult(null);
                                    }
                                    return;
                                }
                            }
                            
                            // Filter sensitive paths from maps files
                            for (String sensitivePath : SENSITIVE_PATHS) {
                                if (line.contains(sensitivePath)) {
                                    XposedBridge.log(TAG + ": Filtering maps line: " + sensitivePath);
                                    try {
                                        BufferedReader reader = (BufferedReader) param.thisObject;
                                        String nextLine = reader.readLine();
                                        param.setResult(nextLine);
                                    } catch (Exception e) {
                                        param.setResult(null);
                                    }
                                    return;
                                }
                            }
                        }
                    }
                });
            
            // Hook File.listFiles to hide directories
            XposedHelpers.findAndHookMethod(File.class, "listFiles",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        File[] files = (File[]) param.getResult();
                        if (files != null) {
                            List<File> filteredFiles = new ArrayList<>();
                            for (File file : files) {
                                boolean shouldHide = false;
                                String fileName = file.getName().toLowerCase();
                                
                                for (String detectionStr : DETECTION_STRINGS) {
                                    if (fileName.contains(detectionStr.toLowerCase())) {
                                        shouldHide = true;
                                        break;
                                    }
                                }
                                
                                if (!shouldHide) {
                                    filteredFiles.add(file);
                                }
                            }
                            param.setResult(filteredFiles.toArray(new File[0]));
                        }
                    }
                });
                
        } catch (Exception e) {
            XposedBridge.log(TAG + ": Error hooking file operations: " + e.getMessage());
        }
    }
    
    private void hookProcessOperations(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // Hook Process-related operations
            Class<?> processClass = XposedHelpers.findClass("java.lang.Process", lpparam.classLoader);
            
            // Hook InputStream reading to filter process output
            XposedHelpers.findAndHookMethod("java.io.InputStream", lpparam.classLoader, "read", byte[].class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        byte[] buffer = (byte[]) param.args[0];
                        int bytesRead = (Integer) param.getResult();
                        
                        if (bytesRead > 0 && buffer != null) {
                            String content = new String(buffer, 0, bytesRead);
                            
                            for (String detectionStr : DETECTION_STRINGS) {
                                if (content.toLowerCase().contains(detectionStr.toLowerCase())) {
                                    XposedBridge.log(TAG + ": Filtering process output containing: " + detectionStr);
                                    // Replace with dummy content
                                    byte[] dummyContent = "normal_process_output\n".getBytes();
                                    System.arraycopy(dummyContent, 0, buffer, 0, 
                                        Math.min(dummyContent.length, buffer.length));
                                    param.setResult(dummyContent.length);
                                    return;
                                }
                            }
                        }
                    }
                });
                
        } catch (Exception e) {
            XposedBridge.log(TAG + ": Error hooking process operations: " + e.getMessage());
        }
    }
    
    private void hookRootDetection(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // Hook ProcessBuilder.start
            XposedHelpers.findAndHookMethod(ProcessBuilder.class, "start",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        ProcessBuilder pb = (ProcessBuilder) param.thisObject;
                        List<String> command = pb.command();
                        
                        if (command != null && !command.isEmpty()) {
                            String cmd = command.toString();
                            
                            for (String rootBinary : ROOT_BINARIES) {
                                if (cmd.contains(rootBinary)) {
                                    XposedBridge.log(TAG + ": Blocking ProcessBuilder command: " + cmd);
                                    param.setThrowable(new IOException("Cannot run program: error=2, No such file or directory"));
                                    return;
                                }
                            }
                        }
                    }
                });
                
        } catch (Exception e) {
            XposedBridge.log(TAG + ": Error hooking root detection: " + e.getMessage());
        }
    }
    
    private void hookDebuggerDetection(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // Hook Debug.isDebuggerConnected
            XposedHelpers.findAndHookMethod(Debug.class, "isDebuggerConnected",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": Hiding debugger connection");
                        return false;
                    }
                });
                
        } catch (Exception e) {
            XposedBridge.log(TAG + ": Error hooking debugger detection: " + e.getMessage());
        }
    }
}
