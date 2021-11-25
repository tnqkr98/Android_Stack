package com.rkqnt.graphicsopengl

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    // OpenGL ES 그래픽을 그릴 수 있는 특수 뷰 (뷰 내 Renderer에서 제어)
    private lateinit var gLView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gLView = MyGLSurfaceView(this)
        setContentView(gLView)                  // GLSurfaceView로 세팅
    }
}