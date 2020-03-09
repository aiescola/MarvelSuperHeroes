package com.aescolar.apiclient

import okhttp3.Interceptor

/**
 * The main reason of this class is for dependency injection purposes.
 */
class InterceptorProvider(val interceptors: List<Interceptor>)
