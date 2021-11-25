package com.rkqnt.graphicsopengl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer


var squareCoords = floatArrayOf(
    -0.5f,  0.5f, 0.0f,      // top left
    -0.5f, -0.5f, 0.0f,      // bottom left
    0.5f, -0.5f, 0.0f,      // bottom right
    0.5f,  0.5f, 0.0f       // top right
)

class Square {

    // 사각형은 삼각형 두개로 그린다. 두 삼각형을 시계반대방향으로 그림
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)          // 정점을 그리는 순서

    // 정점 좌표 (형태를 결정하는) Float 바이트 버퍼 할당
    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    // 순서 리스트에 대한 Short 바이트버퍼 할당 (그래픽 랜더링 파이프라인에 정보 전달)
    private val drawListBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

}