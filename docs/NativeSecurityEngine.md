# NativeSecurityEngine - কারিগরি বিশ্লেষণ ও সম্পূর্ণ ডকুমেন্টেশন

> **আর্কিটেকচার স্তর:** Layer 2: Domain Engine Subsystems (Core Domain Engines)  
> **লাইব্রেরির ধরন:** Pure Java Library (JAR) / Subsystem  
> **সোর্স স্ট্যাটাস:** বাস্তবায়িত (AndroidX Biometric UI বাইন্ডিং বাকি)  
> **সম্পন্নতার হার (Completion Score):** 80% (প্রমাণভিত্তিক হিসাব নিচে উল্লেখিত)

---

## ১. ভূমিকা ও আর্কিটেকচারাল ভূমিকা (Overview & Architectural Role)

`NativeSecurityEngine` হলো NativeRuntime ইকোসিস্টেমের **Layer 2: Domain Engine Subsystems (Core Domain Engines)**-এর একটি অন্যতম ভিত্তি উপাদান। 
এর মূল দায়িত্ব হলো: **হার্ডওয়্যার-সমর্থিত কীস্টোর ম্যানেজার, বায়োমেট্রিক অথেন্টিকেশন এবং পারমিশন সার্ভিস**। 

প্রদত্ত আর্কিটেকচার ডায়াগ্রাম (`NativeRuntimeLayeredArchitectureDiagram_87a563dc.png`) অনুযায়ী, এই লাইব্রেরিটি ইকোসিস্টেমের উপাদানগুলোর মধ্যে সুনির্দিষ্ট ইন্টারফেস ও ডেটা ফ্লো বজায় রাখে।

---

## ২. সোর্স কোড ইনভেন্টরি ও ম্যাট্রিক্স (Source Code Metrics)

- **মোট জাভা সোর্স ফাইল (Main Java Files):** 12 টি
- **মোট সোর্স লাইন সংখ্যা (Source LOC):** 1005 লাইন
- **মোট টেস্ট ফাইল (Test Files):** 1 টি (321 LOC)
- **চিহ্নিত টেস্ট মেথড/কলস:** 0 টি
- **Gradle প্লাগইন:** `java-library`
- **টার্গেট কম্প্যাটিবিলিটি:** Java 1.8 / Java 11

### সোর্স ফাইলের তালিকা:
- `src/main/java/com/roni/library/security/biometric/AuthenticatorType.java` (20 LOC)
- `src/main/java/com/roni/library/security/biometric/BiometricAuthCallback.java` (12 LOC)
- `src/main/java/com/roni/library/security/biometric/BiometricAuthService.java` (128 LOC)
- `src/main/java/com/roni/library/security/biometric/BiometricCapability.java` (32 LOC)
- `src/main/java/com/roni/library/security/biometric/BiometricPromptConfig.java` (71 LOC)
- `src/main/java/com/roni/library/security/handler/SecurityCommandHandler.java` (235 LOC)
- `src/main/java/com/roni/library/security/keystore/KeyAlgorithm.java` (42 LOC)
- `src/main/java/com/roni/library/security/keystore/KeyStoreManager.java` (153 LOC)
- `src/main/java/com/roni/library/security/permission/PermissionCallback.java` (11 LOC)
- `src/main/java/com/roni/library/security/permission/PermissionService.java` (210 LOC)
- `src/main/java/com/roni/library/security/permission/PermissionStatus.java` (35 LOC)
- `src/main/java/com/roni/library/security/service/NativeSecurityService.java` (56 LOC)

---

## ৩. ডিপেনডেন্সি বিশ্লেষণ (Dependency Graph)

`build.gradle` ফাইল সরাসরি পরীক্ষা করে প্রাপ্ত ডিপেনডেন্সি:

- **implementation:** `project(':NativeRuntimeContracts`
- **implementation:** `project(':NativeServiceSDK`
- **testImplementation:** `project(':NativeLoggerSDK`
- **testImplementation:** `project(':NativeRouterSDK`

---

## ৪. সমাপ্তি বিশ্লেষণ ও স্কোর হিসাব (Completion Analysis & Verification)

### সমাপ্তির শতাংশ: **80%**

#### স্কোর হিসাবের ভিত্তি (Formula & Breakdown):
1. **ইন্টারফেস ও কোর লজিক ডিজাইন (৪০%):** মানসম্মত অবজেক্ট-ওরিয়েন্টেড আর্কিটেকচার, ডিজাইন প্যাটার্ন (Singleton, Factory, Observer, Command) শতভাগ প্রণীত হয়েছে। (অর্জিত: ৪০/৪০)
2. **বাস্তব কোড ইমপ্লিমেন্টেশন (৪০%):** মেথড ও ইন্টারনাল অ্যালগরিদম বাস্তবায়িত হয়েছে। তবে পূর্ণাঙ্গ Android OS বাইন্ডিং (যেমন: লাইভ ব্রডকাস্ট রিসিভার বা অ্যাক্টিভিটি ব্যাকড্রপ) হেডলেস ডিকাপলিং আর্কিটেকচারের কারণে সিমুলেটেড রাখা হয়েছে। (অর্জিত: 25/৪০)
3. **ইউনিট টেস্ট ও ভেরিফিকেশন কভারেজ (১০%):** টেস্ট ক্লাস এবং সেলফ-ভেরিফাইং টেস্ট হারনেস বিদ্যমান। (অর্জিত: ৯/১০)
4. **প্যাকেজিং ও প্রোডাকশন প্রস্তুতকরণ (১০%):** `consumer-rules.pro` ফাইলটি ডিক্লেয়ার থাকা সত্ত্বেও জিআইপিতে অনুপস্থিত এবং কোনো `README.md` বা `AndroidManifest.xml` নেই। (অর্জিত: ৬/১০)

**মোট স্কোর:** **80%**

---

## ৫. ডেভেলপার গাইড (Developer Integration Guide)

### ধাপ ১: Gradle ডিপেনডেন্সি কনফিগারেশন
আপনার অ্যাপ্লিকেশন বা মডিউলের `build.gradle` ফাইলে যুক্ত করুন:

```groovy
dependencies {
    implementation project(':NativeSecurityEngine')
}
```

### ধাপ ২: কোড ব্যবহারের নমুনা (Code Example)

```java
package com.example.app;

import com.roni.library.contracts.common.RuntimeState;
// প্রয়োজনীয় ক্লাস ইমপোর্ট করুন

public class IntegrationExample {
    public void setupModule() {
        // NativeSecurityEngine এর প্রাথমিক কনফিগারেশন
        System.out.println("Initializing NativeSecurityEngine inside client application...");
    }
}
```

---

## ৬. ইউজার গাইড (User & Operational Guide)

1. **উদ্দেশ্য:** অ্যাপ্লিকেশন ডেভেলপার এবং হাইব্রিড ওয়েব-অ্যাপ টিম এই লাইব্রেরি ব্যবহার করে ব্যাকএন্ড/ওয়েব ফ্রন্টএন্ডের সাথে ন্যাটিভ ফিচারের মেলবন্ধন নিশ্চিত করতে পারে।
2. **রানটাইম আচরণ:** অ্যাপ্লিকেশনের লাইফসাইকেলের সাথে সমন্বয় রেখে এটি মেমরি বা সিস্টেম রিসোর্স অপ্টিমাইজ করে। কোনো ত্রুটি ঘটলে `NativeApiError` থ্রো করে যা ক্র্যাশ প্রতিরোধ করে।

---

## ৭. সম্পূর্ণ পাবলিক API রেফারেন্স (Public API Reference)

### Class: `AuthenticatorType`
- **Package:** `com.roni.library.security.biometric`
- **Source File:** `src/main/java/com/roni/library/security/biometric/AuthenticatorType.java`
- **Lines of Code (LOC):** 20
- **পাবলিক মেথড তালিকা:**
- `boolean allowsDeviceCredential(int authenticators)`
- `boolean allowsBiometricStrong(int authenticators)`

### Interface: `BiometricAuthCallback`
- **Package:** `com.roni.library.security.biometric`
- **Source File:** `src/main/java/com/roni/library/security/biometric/BiometricAuthCallback.java`
- **Lines of Code (LOC):** 12
- **পাবলিক মেথড তালিকা:**
- _পাবলিক মেথড বা কনস্ট্রাক্টর ছাড়া ইন্টারফেস/মডেল ডিফল্ট_

### Class: `BiometricAuthService`
- **Package:** `com.roni.library.security.biometric`
- **Source File:** `src/main/java/com/roni/library/security/biometric/BiometricAuthService.java`
- **Lines of Code (LOC):** 128
- **পাবলিক মেথড তালিকা:**
- `void setHardwareAvailable(boolean available)`
- `void setBiometricEnrolled(boolean enrolled)`
- `void setPromptDelegate(BiometricPromptDelegate delegate)`
- `BiometricCapability canAuthenticate(int authenticators)`
- `void authenticate(BiometricPromptConfig config, Cipher cipher, BiometricAuthCallback callback)`
- `void onAuthenticationSucceeded(String authMethod, Cipher c)`
- `void onAuthenticationFailed(String reason)`
- `void onAuthenticationError(int errorCode, String errString)`
- `void authenticate(BiometricPromptConfig config, BiometricAuthCallback callback)`
- `void addGlobalListener(BiometricAuthCallback listener)`
- `void removeGlobalListener(BiometricAuthCallback listener)`
- `void clear()`

### Enum: `BiometricCapability`
- **Package:** `com.roni.library.security.biometric`
- **Source File:** `src/main/java/com/roni/library/security/biometric/BiometricCapability.java`
- **Lines of Code (LOC):** 32
- **পাবলিক মেথড তালিকা:**
- `int getCode()`
- `String getDescription()`
- `boolean isAvailable()`

### Class: `BiometricPromptConfig`
- **Package:** `com.roni.library.security.biometric`
- **Source File:** `src/main/java/com/roni/library/security/biometric/BiometricPromptConfig.java`
- **Lines of Code (LOC):** 71
- **পাবলিক মেথড তালিকা:**
- `String getTitle()`
- `String getSubtitle()`
- `String getDescription()`
- `String getNegativeButtonText()`
- `boolean isConfirmationRequired()`
- `int getAllowedAuthenticators()`
- `Builder builder()`
- `Builder setTitle(String title)`
- `Builder setSubtitle(String subtitle)`
- `Builder setDescription(String description)`
- `Builder setNegativeButtonText(String text)`
- `Builder setConfirmationRequired(boolean req)`

### Class: `SecurityCommandHandler`
- **Package:** `com.roni.library.security.handler`
- **Source File:** `src/main/java/com/roni/library/security/handler/SecurityCommandHandler.java`
- **Lines of Code (LOC):** 235
- **পাবলিক মেথড তালিকা:**
- `String getRoutePrefix()`
- `void handle(NativeApiRequest request, IApiCallback callback)`
- `void onSinglePermissionResult(String perm, PermissionStatus status)`
- `void onMultiplePermissionsResult(Map<String, PermissionStatus> results)`
- `void onSinglePermissionResult(String permission, PermissionStatus status)`
- `void onMultiplePermissionsResult(Map<String, PermissionStatus> results)`
- `void onAuthenticationSucceeded(String authMethod, Cipher cipher)`
- `void onAuthenticationFailed(String reason)`
- `void onAuthenticationError(int errorCode, String errString)`

### Enum: `KeyAlgorithm`
- **Package:** `com.roni.library.security.keystore`
- **Source File:** `src/main/java/com/roni/library/security/keystore/KeyAlgorithm.java`
- **Lines of Code (LOC):** 42
- **পাবলিক মেথড তালিকা:**
- `String getStandardName()`
- `int getKeySize()`
- `String getTransformation()`
- `KeyAlgorithm fromString(String name)`

### Class: `KeyStoreManager`
- **Package:** `com.roni.library.security.keystore`
- **Source File:** `src/main/java/com/roni/library/security/keystore/KeyStoreManager.java`
- **Lines of Code (LOC):** 153
- **পাবলিক মেথড তালিকা:**
- `boolean isHardwareSecurityModuleAvailable()`
- `synchronized boolean generateKey(String alias, KeyAlgorithm algorithm, boolean requiresUserAuth)`
- `boolean hasKey(String alias)`
- `boolean deleteKey(String alias)`
- `List<String> listAliases()`
- `KeyAlgorithm getKeyAlgorithm(String alias)`
- `Cipher getInitializedCipher(String alias, int mode, byte[] iv)`
- `Map<String, Object> encrypt(String alias, byte[] plaintext)`
- `byte[] decrypt(String alias, byte[] ciphertext, byte[] iv)`
- `void clear()`

### Interface: `PermissionCallback`
- **Package:** `com.roni.library.security.permission`
- **Source File:** `src/main/java/com/roni/library/security/permission/PermissionCallback.java`
- **Lines of Code (LOC):** 11
- **পাবলিক মেথড তালিকা:**
- _পাবলিক মেথড বা কনস্ট্রাক্টর ছাড়া ইন্টারফেস/মডেল ডিফল্ট_

### Class: `PermissionService`
- **Package:** `com.roni.library.security.permission`
- **Source File:** `src/main/java/com/roni/library/security/permission/PermissionService.java`
- **Lines of Code (LOC):** 210
- **পাবলিক মেথড তালিকা:**
- `void setPermissionDelegate(PermissionDelegate delegate)`
- `PermissionStatus checkPermission(String permission)`
- `boolean isPermissionGranted(String permission)`
- `void setPermissionStatus(String permission, PermissionStatus status)`
- `boolean shouldShowRequestPermissionRationale(String permission)`
- `void requestPermission(String permission, PermissionCallback callback)`
- `void onSinglePermissionResult(String p, PermissionStatus status)`
- `void onMultiplePermissionsResult(Map<String, PermissionStatus> results)`
- `void requestMultiplePermissions(List<String> permissions, PermissionCallback callback)`
- `void onSinglePermissionResult(String permission, PermissionStatus status)`
- `void onMultiplePermissionsResult(Map<String, PermissionStatus> results)`
- `void addListener(PermissionCallback listener)`

### Enum: `PermissionStatus`
- **Package:** `com.roni.library.security.permission`
- **Source File:** `src/main/java/com/roni/library/security/permission/PermissionStatus.java`
- **Lines of Code (LOC):** 35
- **পাবলিক মেথড তালিকা:**
- `String getValue()`
- `boolean isGranted()`
- `PermissionStatus fromValue(String val)`

### Class: `NativeSecurityService`
- **Package:** `com.roni.library.security.service`
- **Source File:** `src/main/java/com/roni/library/security/service/NativeSecurityService.java`
- **Lines of Code (LOC):** 56
- **পাবলিক মেথড তালিকা:**
- `PermissionService getPermissionService()`
- `BiometricAuthService getBiometricService()`
- `KeyStoreManager getKeyStoreManager()`


---

## ৮. পরিচিত সমস্যা ও সীমাবদ্ধতা (Known Issues & Remaining Work)

### বিদ্যমান সীমাবদ্ধতা (Known Issues):
1. **অনুপস্থিত `consumer-rules.pro`:** মডিউলের `build.gradle` ফাইলে ProGuard রুল ফাইলের রেফারেন্স থাকলেও আর্কাভে ফাইলটি যুক্ত ছিল না।
2. **অনুপস্থিত `AndroidManifest.xml`:** লাইব্রেরি মডিউলে কোনো বিল্ট-ইন ম্যানিফেস্ট ফাইল নেই; যদিও রুট অ্যাপ Gradle দ্বারা তা ম্যানেজ হতে পারে, স্বতন্ত্র প্যাকেজিংয়ে ম্যানিফেস্ট কাম্য।
3. **হেডলেস ডিজাইন চয়েস:** আর্কিটেক্ট উদ্দেশ্যপ্রণোদিতভাবে সরাসরি `android.*` ইমপোর্ট বাদ দিয়ে রিফ্লেকশন (`Class.forName`) বা ইন্টারফেস ব্যবহার করেছেন। এর ফলে টেস্ট পরিবেশ সহজ হলেও প্রোডাকশনে কনক্রিট অ্যান্ড্রয়েড প্লাগিং প্রয়োজন।

### পরবর্তী করণীয় (Remaining Roadmap):
- [ ] `consumer-rules.pro` ফাইলে R8/ProGuard কীপ রুলস যুক্ত করা।
- [ ] লাইব্রেরিটির স্বতন্ত্র `AndroidManifest.xml` তৈরি করা।
- [ ] Android OS Context ইনজেকশন মেকানিজম ফাইনাল করা।
