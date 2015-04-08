## Android Makefile to build library in order to run the Apk ##

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= main.c
LOCAL_SHARED_LIBRARIES := libcutils

LOCAL_CFLAGS += -g -Wall -Wno-multichar
#LOCAL_CFLAGS += -fpermissive 

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE_TAGS := debug

LOCAL_MODULE:= libsocket_test			# Name of the Library to build.
include $(BUILD_SHARED_LIBRARY)
