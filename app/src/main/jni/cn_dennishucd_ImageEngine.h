
#include <jni.h>
/* Header for class cn_dennishucd_ImageEngine */

#ifndef _Included_cn_dennishucd_ImageEngine
#define _Included_cn_dennishucd_ImageEngine
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_cn_dennishucd_ImageEngine_getResultFromJni
  (JNIEnv *, jobject);

JNIEXPORT jintArray JNICALL Java_cn_dennishucd_ImageEngine_toGray
  (JNIEnv *, jobject, jintArray, jint, jint);

JNIEXPORT jintArray JNICALL Java_cn_dennishucd_ImageEngine_toFudiao
  (JNIEnv *, jobject, jintArray, jint, jint);

JNIEXPORT jintArray JNICALL Java_cn_dennishucd_ImageEngine_toHeibai
  (JNIEnv *, jobject, jintArray, jint, jint);

JNIEXPORT jintArray JNICALL Java_cn_dennishucd_ImageEngine_toMohu
  (JNIEnv *, jobject, jintArray, jint, jint, jint);

JNIEXPORT jintArray JNICALL Java_cn_dennishucd_ImageEngine_toDipian
  (JNIEnv *, jobject, jintArray, jint, jint);

JNIEXPORT jintArray JNICALL Java_cn_dennishucd_ImageEngine_toSunshine
  (JNIEnv *, jobject, jintArray, jint, jint, jint, jint, jint, jint);

JNIEXPORT jintArray JNICALL Java_cn_dennishucd_ImageEngine_toFangdajing
  (JNIEnv *, jobject, jintArray, jint, jint, jint, jint, jint, jfloat);

JNIEXPORT jintArray JNICALL Java_cn_dennishucd_ImageEngine_toHahajing
  (JNIEnv *, jobject, jintArray, jint, jint, jint, jint, jint, jfloat);

#ifdef __cplusplus
}
#endif
#endif
