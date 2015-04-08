#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <errno.h>
#include <jni.h>
#include <android/log.h>

#define TAG					"[JNI_GSIP_Client] "
#define SOCK_EVA_GUI_PATH	"/dev/socket/test_socket"
#define BUFFER_SIZE			320

/* Macros for Printing to Log */
#define LOGV(...)               __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGD(...)               __android_log_print(ANDROID_LOG_DEBUG  , TAG, __VA_ARGS__)
#define LOGI(...)               __android_log_print(ANDROID_LOG_INFO   , TAG, __VA_ARGS__)
#define LOGW(...)               __android_log_print(ANDROID_LOG_WARN   , TAG, __VA_ARGS__)
#define LOGE(...)               __android_log_print(ANDROID_LOG_ERROR  , TAG, __VA_ARGS__)

/* Function Protoypes */
void connect_client_eva_gui (void);
void error(char *msg);

int client_sock_eva_fd;
char audio_buffer [BUFFER_SIZE];


JNIEXPORT jbyteArray JNICALL Java_com_example_recplaywithsocket_MainActivity_getArrayElements (JNIEnv *env, jobject obj)
{
	int i, nbytes = -1;

	nbytes = read(client_sock_eva_fd, audio_buffer, sizeof(audio_buffer));
	if (-1 == nbytes)
		error("Read");

	int arr_len = BUFFER_SIZE;
	jclass cls = (*env)->GetObjectClass(env, obj);
	if (!cls) {
		LOGI("JNISIPCLIENT:FAILED TO GET CLASS");
		return NULL;
	}
	jbyteArray result_arr = (*env)->NewByteArray(env, arr_len);
	jbyte *ptr = (jbyte *) malloc(sizeof(jbyte) * (arr_len));

	for (i = 0; i < arr_len; i++)
	{
		ptr[i] =  (jbyte) audio_buffer[i];
	}

	(*env)->SetByteArrayRegion(env, result_arr, 0, arr_len, ptr);
	return result_arr;
}

JNIEXPORT void Java_com_example_recplaywithsocket_MainActivity_sendArrayElements(JNIEnv *env, jobject object, jbyteArray ptr)
{
	jclass cls = (*env)->GetObjectClass(env, object);
	if (!cls) {
		LOGI("JNISIPCLIENT:FAILED TO GET CLASS");
		return;
	}

	jsize len = (*env)->GetArrayLength(env, ptr);
	jbyte *body = (*env)->GetByteArrayElements(env, ptr, 0);

	memcpy(audio_buffer, body, BUFFER_SIZE);


	if (-1 == write (client_sock_eva_fd, (void *) &audio_buffer, sizeof(audio_buffer)))
		error("Write");

	(*env)->ReleaseByteArrayElements(env, ptr, body, 0);
	return;
}


/* This function connects to server over EVA and GUI Socket */
JNIEXPORT void JNICALL Java_com_example_recplaywithsocket_MainActivity_ConnectClient(JNIEnv *env, jobject object)
{
	connect_client_eva_gui();
	return;
}

void connect_client_eva_gui (void)
{
	int len;
	struct sockaddr_un remote;

	if ((client_sock_eva_fd = socket(AF_UNIX, SOCK_STREAM, 0)) == -1) {
		error("===== EVA-GUI Socket ======");
	}
	LOGI("===== EVA-GUI Socket Created ======");
	remote.sun_family = AF_UNIX;
	strcpy(remote.sun_path, SOCK_EVA_GUI_PATH);
	len = strlen(remote.sun_path) + sizeof(remote.sun_family);

	LOGI("===== EVA-GUI Connecting to Socket =====");
	if (connect(client_sock_eva_fd, (struct sockaddr *) &remote, len) == -1) {
		LOGI("===== EVA-GUI Error Connecting to Socket! =====");
		error("EVA-GUI Connect");
	}
	LOGI("===== EVA-GUI Successfully Connected to Socket =====");
	return;
}

void error(char *msg)
{
	LOGI("===== Error in:[%s]\tError No:[%d]\tError Message:[%s] =====", msg, errno, strerror(errno));
	exit(EXIT_FAILURE);
}
