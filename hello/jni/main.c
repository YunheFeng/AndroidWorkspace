#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <android/log.h>

#define TAG "SAURABH"

/* Macros for Printing to Log */
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , TAG, __VA_ARGS__)

//	int port = 1000;
JNIEXPORT void JNICALL Java_com_example_hello_MainActivity_samplefunc(JNIEnv *env, jobject obj, jobject x)
{
	jfieldID fid;
	jclass class = (*env)->GetObjectClass(env, x);

	fid = (*env)->GetFieldID(env, class, "b", "I");
	(*env)->SetIntField(env, x ,fid, 10);

	return;
}

int test_arr[] = {1, 2, 3, 4, 5}; /* Global Variable */
JNIEXPORT jintArray JNICALL Java_com_example_hello_MainActivity_getArray (JNIEnv *env, jobject obj)
{
	int i, arr_len = 5;
	jclass cls = (*env)->GetObjectClass(env, obj);
	if (!cls) {
		__android_log_print(ANDROID_LOG_DEBUG, "HelloJNI", "HelloJNI : FAILED TO GET CLASS");
		return;
	}

	jintArray result_arr = (*env)->NewIntArray(env, arr_len);
	/*jint *ptr = (jint *) malloc(sizeof(jint) * (arr_len));
	for (i = 0; i < arr_len; i++) {
		__android_log_print(ANDROID_LOG_INFO,"HelloJNI","test_arr : [%d]",test_arr[i]);
		ptr[i] =  (jint) test_arr[i];
	}*/

	(*env)->SetIntArrayRegion(env, result_arr, 0, arr_len, test_arr);
	return result_arr;
}

//char arr_char[] = {'s','a','u','r','a','b','h'};
char arr_char[] = "Saurabh";
JNIEXPORT jcharArray JNICALL Java_com_example_hello_MainActivity_getCharArray (JNIEnv *env, jobject obj)
{
	int i, arr_len = 7;
	jclass cls = (*env)->GetObjectClass(env, obj);
	if (!cls) {
		__android_log_print(ANDROID_LOG_DEBUG, "HelloJNI", "HelloJNI : FAILED TO GET CLASS");
		return;
	}

	jcharArray result_arr = (*env)->NewCharArray(env, arr_len);
	jchar *ptr = (jchar *) malloc(sizeof(jchar) * (arr_len));

	/* Required to send a char Array */
	for (i = 0; i < arr_len; i++) {
		ptr[i] =  (jchar) arr_char[i];
	}

	(*env)->SetCharArrayRegion(env, result_arr, 0, arr_len, ptr);
	return result_arr;
}


//jbyte arr_byte[] = {'p','r','a','t','i','k'};
jbyte arr_byte[] = "abcdef";
JNIEXPORT jbyteArray JNICALL Java_com_example_hello_MainActivity_getByteArray (JNIEnv *env, jobject obj)
{
	LOGD("Entered Java_com_example_hello_MainActivity_getByteArray\n");

	int i, arr_len = 6;
	jclass cls = (*env)->GetObjectClass(env, obj);
	if (!cls) {
		__android_log_print(ANDROID_LOG_DEBUG, "HelloJNI", "HelloJNI : FAILED TO GET CLASS");
		return;
	}

	jbyteArray result_arr = (*env)->NewByteArray(env, arr_len);
	jbyte *ptr = (jbyte *) malloc(sizeof(jbyte) * (arr_len));

	/* Required to send a char Array */
	for (i = 0; i < arr_len; i++) {
		ptr[i] =  (jbyte) arr_byte[i];
	}

	(*env)->SetByteArrayRegion(env, result_arr, 0, arr_len, ptr);
	return result_arr;
}


JNIEXPORT void JNICALL Java_com_example_hello_MainActivity_sendByteArray (JNIEnv *env, jobject obj, jbyteArray ptr)
{
	LOGD("[JNI] Entered Java_com_example_hello_MainActivity_sendByteArray");

	int i = 0;
	jclass cls = (*env)->GetObjectClass(env, obj);
	if (!cls) {
		LOGD("FAILED TO GET CLASS");
		return;
	}

	jsize len = (*env)->GetArrayLength(env, ptr);
	jbyte *body = (*env)->GetByteArrayElements(env, ptr, 0);

	//	LOGD("Size of recvd array [%d]\n", len	);

	char *final = malloc (sizeof(char) * (len + 1));
	memcpy(final, body, len);
	final[len] = '\0'; 			// Add null character

	char temp[10];
	memcpy(temp, body, 10);

	/* Print the Received Array */
	for (i = 0; i < len; i++)
		LOGD("[JNI] Received Array Element in JNI from Java: Element :: [%d]\n", final[i]);

	(*env)->ReleaseByteArrayElements(env, ptr, body, 0);
	return;
}
