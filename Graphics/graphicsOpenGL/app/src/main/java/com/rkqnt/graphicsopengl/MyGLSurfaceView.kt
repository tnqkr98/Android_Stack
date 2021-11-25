package com.rkqnt.graphicsopengl

import android.content.Context
import android.opengl.GLSurfaceView

class MyGLSurfaceView(context: Context): GLSurfaceView(context) {
    private val renderer : MyGLRenderer
    init{
        setEGLContextClientVersion(2)   // OpenGL ES 2.0 버전 Context
        renderer = MyGLRenderer()
        setRenderer(renderer)           // GLSurfaceVeiw 를 그리기 위한 Renderer 설정
    }

    // 이벤트 수신을 위해 쓰임

}