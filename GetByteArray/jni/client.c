#include <jni.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <android/log.h>

#define TAG "SampleApp_ByteArray"

/* Macros for Printing to Log */
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , TAG, __VA_ARGS__)

struct audio
{
        int     sample;
        char    arr[4];
};

int i;
struct audio audiocall;
audiocall.sample = 10;
for (i=0; i<4; i++)			// Fill the array
	audiocall.arr[i] = i;

JNIEXPORT void JNICALL
Java_com_ges_sip_Callbacks_nativeMethod(JNIEnv *env, jobject obj, jint get_event )
{
	jclass cls = (*env)->GetObjectClass(env, obj);
	if (!cls) {
		__android_log_print(ANDROID_LOG_DEBUG, TAG, "initClassHelper:FAILED TO GET CLASS");
		return;
	}

	jfieldID fid1 = (*env)->GetFieldID(env, cls, "sample","I");
	(*env)->SetIntField(env, obj, fid1, audio.sample);

	jbyteArray result = (*env)->NewByteArray(env, 4);
	(*env)->SetByteArrayRegion(env, result, 0, size, audiocall.arr);

	jmethodID mid = (*env)->GetMethodID(env, cls, "callback", "(I)V");  //Call Respective Function.
	if (mid == 0)
		return;
	(*env)->CallVoidMethod(env, obj, mid, get_event);

#if 0
	/*const char buf[] = { 0, 1, 2, 3, 42 };
	    const size_t buf_len = sizeof buf;

	    jboolean isCopy;
	    jbyte *jbuf = (*env)->GetByteArrayElements(env, buf, &isCopy);*/


//	jmethodID mid = (*env)->GetMethodID(env, cls, "callback", "(Ljava/lang/String;)V");

	// (*env)->ReleaseByteArrayElements(env, buf, jbuf, 0);

	char buf[4] = {0, 1, 2, 3};
	size_t len = sizeof(buf);
#endif
	return;
}
