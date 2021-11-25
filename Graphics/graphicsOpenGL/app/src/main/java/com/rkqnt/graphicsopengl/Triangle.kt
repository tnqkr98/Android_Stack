package com.rkqnt.graphicsopengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


/**
 *  기본적으로 OpenGL ES 좌표계는 [x,y,z] 기준 [0,0,0] 이 중심이고,
 *  [1,1,0] 이 오른쪽 상단
 *  [-1,-1,0] 이 왼쪽 하단
 *
**/

const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(     // in counterclockwise order:
    0.0f, 0.622008459f, 0.0f,      // top
    -0.5f, -0.311004243f, 0.0f,    // bottom left
    0.5f, -0.311004243f, 0.0f      // bottom right
)

/**
 *  도형을 랜더링 해보자.
 *
 *  OpenGL ES 2.0 이 도형을 그리기 위해 그래픽 렌더링 파이프라인에 전달할 정보
 *  1. 꼭지점 셰이더 (VertexShader) : 최소 하나 이상 정의해야하는 OpenGL ES 그래픽 코드
 *  2. 조각(Fragment) 셰이더 : 도형의 면에 색감과 질감을 랜더링하는 OpenGL ES 코드
 *  3. 프로그램(Program) : 하나 이상의 셰이더가 포함된 OpenGL ES 객체
 * */

class Triangle {
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)       // RGBA

    private var vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {                 // 각 좌표마다 4바이트씩 할당해야함.
            order(ByteOrder.nativeOrder())                      // 디바이스 하드웨어의 네이티브 바이트 순서로 설정

            // 부동소수점 버퍼 생성
            asFloatBuffer().apply {
                put(triangleCoords)                 // 생성한 버퍼에 좌표 추가
                position(0)
            }
        }

    // 기본 셰이더 정의 : GLSL(OpenGL Shading Language) 으로 정의하고, 이를 컴파일 하기위한 유틸리티 메소드(loadShader) 필요
    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private var mProgram: Int

    init{
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)               // (셰이더 타입, 셰이더 코드) 전달하여 컴파일
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {

            GLES20.glAttachShader(it, vertexShader)     // 꼭지점 셰이더를 프로그램에 추가

            GLES20.glAttachShader(it, fragmentShader)   // 조각(Fragment) 셰이더를 프로그램에 추가

            GLES20.glLinkProgram(it)                    //  OpenGL ES program 실행가능하게 연결
        }
    }

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    fun draw() {
        // 초기화한 프로그램(Program)을 OpenGL ES 환경에 추가
        GLES20.glUseProgram(mProgram)

        // 꼭지점 셰이더의 vPosition 멤버로부터 제어(Handle)를 가져옴
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {

            // 삼각형 꼭지점의 제어 허용
            GLES20.glEnableVertexAttribArray(it)

            // 삼각형 꼭지점 데이터 준비
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // 조각(Fragment) 셰이더의 vColor 멤버로부터  제어를 가져옴
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // 삼각형에 색상을 설정
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            // 삼각형 그리기
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

            // 꼭지점 배열 비활성화 (메모리 해제인지?)
            GLES20.glDisableVertexAttribArray(it)
        }
    }


    // GLSL(OpenGL Shading Language) 컴파일러
    private fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)

        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}