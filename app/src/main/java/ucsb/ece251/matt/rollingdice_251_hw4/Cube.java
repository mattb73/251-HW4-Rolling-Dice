package ucsb.ece251.matt.rollingdice_251_hw4;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {

//    private final String vertexShaderCode =
//            // This matrix member variable provides a hook to manipulate
//            // the coordinates of the objects that use this vertex shader
//            "uniform mat4 uMVPMatrix;" +
//                    "attribute vec4 vPosition;" +
//                    "void main() {" +
//                    // The matrix must be included as a modifier of gl_Position.
//                    // Note that the uMVPMatrix factor *must be first* in order
//                    // for the matrix multiplication product to be correct.
//                    "  gl_Position = uMVPMatrix * vPosition;" +
//                    "}";
//
//    private final String fragmentShaderCode =
//            "precision mediump float;" +
//                    "uniform vec4 vColor;" +
//                    "void main() {" +
//                    "  gl_FragColor = vColor;" +
//                    "}";
private final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +

                "attribute vec4 vPosition;" +
                "attribute vec4 a_color;" +
                "attribute vec2 tCoordinate;" +
                "varying vec2 v_tCoordinate;" +
                "varying vec4 v_Color;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                "  gl_Position = uMVPMatrix*vPosition;" +
                "	v_tCoordinate = tCoordinate;" +
                "	v_Color = a_color;" +
                "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 v_Color;" +
                    "varying vec2 v_tCoordinate;" +
                    "uniform sampler2D s_texture;" +
                    "void main() {" +
                    // texture2D() is a build-in function to fetch from the texture map
                    "	vec4 texColor = texture2D(s_texture, v_tCoordinate); " +
                    "  gl_FragColor = v_Color*0.5 + texColor*0.5;" +
                    "}";

    private final FloatBuffer vertexBuffer, texCoordBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle, mTexCoordHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mTextureDataHandle, mTextureUniformHandle;

    //=========== Vertex =================================
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float cubeCoords[] = {
            // Back (0,1,2,3)
            -0.5f, -0.5f, -0.5f, //0
            0.5f, -0.5f, -0.5f,  //1
            0.5f, 0.5f, -0.5f,   //2
            -0.5f, 0.5f, -0.5f,  //3
            // Front (4,5,6,7)
            -0.5f, -0.5f, 0.5f,  //4
            0.5f, -0.5f, 0.5f,   //5
            0.5f, 0.5f, 0.5f,    //6
            -0.5f, 0.5f, 0.5f,   //7
            // Top (7,6,2,3)
            -0.5f, 0.5f, 0.5f,   //8
            0.5f, 0.5f, 0.5f,    //9
            0.5f, 0.5f, -0.5f,   //10
            -0.5f, 0.5f, -0.5f,  //11
            // Bottom (4,5,1,0)
            -0.5f, -0.5f, 0.5f,  //12
            0.5f, -0.5f, 0.5f,   //13
            0.5f, -0.5f, -0.5f,  //14
            -0.5f, -0.5f, -0.5f, //15
            // Left (4,0,3,7)
            -0.5f, -0.5f, 0.5f,  //16
            -0.5f, -0.5f, -0.5f, //17
            -0.5f, 0.5f, -0.5f,  //18
            -0.5f, 0.5f, 0.5f,   //19
            // Right (5,1,2,6)
            0.5f, -0.5f, 0.5f,   //20
            0.5f, -0.5f, -0.5f,  //21
            0.5f, 0.5f, -0.5f,   //22
            0.5f, 0.5f, 0.5f     //23


    };
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per float


    //==== Draw Order =====================
    private final short drawOrder[] = {
            // Back (2,1,0,2,0,3)
            2, 1, 0, 2, 0, 3,
            // Front (7,4,5,7,5,6)
            7, 4, 5, 7, 5, 6,
            // Top (3,7,6,3,6,2)
            11, 8, 9, 11, 9, 10,
            // Bottom (4,0,1,4,1,5)
            12, 15, 14, 12, 14, 13,
            // Left (3,0,4,3,4,7)
            18, 17, 16, 18, 16, 19,
            // Right (6,5,1,6,1,2)
            23, 20, 21, 23, 21, 22


//            // Back (2,1,0,2,0,3)
//            2, 1, 0, 2, 0, 3,
//            // Front (7,4,5,7,5,6)
//            7, 4, 6, 4, 5, 6,
//            // Top (3,7,6,3,6,2)
//            11, 8, 9, 11, 9, 10,
//            // Bottom (4,0,1,4,1,5)
//            12, 15, 14, 12, 14, 13,
//            //12, 13, 14, 12, 14, 15,
//            // Left (3,0,4,3,4,7)
//            18, 17, 16, 18, 16, 19,
//            //16, 17, 18, 16, 18, 19,
//            // Right (6,5,1,6,1,2)
//            23, 20, 21, 23, 21, 22


    };

    //============ Texture =======================
    static final int COORDS_PER_TEX = 2;
    static float texCoords[] = {
            // Back (1, CDBA)
            0f, 0.167f,
            1f, 0.167f,
            1f, 0f,
            0f, 0f,
            // Front (3, GHFE)
            0f, 0.5f,
            1f, 0.5f,
            1f, 0.334f,
            0f, 0.334f,
            // Top (5, KLJI)
            0f, 0.834f,
            1f, 0.834f,
            1f, 0.667f,
            0f, 0.667f,
            // Bottom (6, MNLK)
            0f, 1f,
            1f, 1f,
            1f, 0.834f,
            0f, 0.834f,
            // Left (4, IJHG)
            0f, 0.667f,
            1f, 0.667f,
            1f, 0.5f,
            0f,0.5f,
            // Right (2, EFDC)
            1f, 0.167f,
            0f, 0.167f,
            0f, 0.334f,
            1f, 0.334f

    };

    private final int texCoordStride = COORDS_PER_TEX * 4; // 4 bytes per float





    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Cube(Context context) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                cubeCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // initialize texture coord byte buffer for texture coordinates
        ByteBuffer texbb = ByteBuffer.allocateDirect(
                texCoords.length * 4);
        // use the device hardware's native byte order
        texbb.order(ByteOrder.nativeOrder());
        texCoordBuffer = texbb.asFloatBuffer();
        texCoordBuffer.put(texCoords);
        texCoordBuffer.position(0);

        //===================================
        // loading an image into texture
        //===================================
        mTextureDataHandle = loadTexture(context, R.drawable.dice1);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    public static int loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        //=============== VERTEX =========================================
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        //========== COLOR: NOT USED ===============================
//        // setting vertex color
//        mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_color");
//        Log.i("chuu", "Error: mColorHandle = " + mColorHandle);
//        GLES20.glEnableVertexAttribArray(mColorHandle);
//        GLES20.glVertexAttribPointer(mColorHandle, COLORB_PER_VER,
//                                     GLES20.GL_FLOAT, false,
//                                     colorBlendStride, colorBuffer);
//        MyGLRenderer.checkGlError("glVertexAttribPointer...color");

        //========== TEXTURE ======================================
        // setting texture coordinate to vertex shader
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "tCoordinate");
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, COORDS_PER_TEX,
                                     GLES20.GL_FLOAT, false,
                                     texCoordStride, texCoordBuffer);
        MyGLRenderer.checkGlError("glVertexAttribPointer...texCoord");

        // get handle to fragment shader's vColor member
        //mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // texture
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Draw the Cube
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }































//        private FloatBuffer mVertexBuffer;
//        private FloatBuffer mColorBuffer;
//        private ByteBuffer mIndexBuffer;
//
//        private float vertices[] = {
//                -1.0f, -1.0f, -1.0f,
//                1.0f, -1.0f, -1.0f,
//                1.0f,  1.0f, -1.0f,
//                -1.0f, 1.0f, -1.0f,
//                -1.0f, -1.0f,  1.0f,
//                1.0f, -1.0f,  1.0f,
//                1.0f,  1.0f,  1.0f,
//                -1.0f,  1.0f,  1.0f
//        };
//        private float colors[] = {
//                0.0f,  1.0f,  0.0f,  1.0f,
//                0.0f,  1.0f,  0.0f,  1.0f,
//                1.0f,  0.5f,  0.0f,  1.0f,
//                1.0f,  0.5f,  0.0f,  1.0f,
//                1.0f,  0.0f,  0.0f,  1.0f,
//                1.0f,  0.0f,  0.0f,  1.0f,
//                0.0f,  0.0f,  1.0f,  1.0f,
//                1.0f,  0.0f,  1.0f,  1.0f
//        };
//
//        private byte indices[] = {
//                0, 4, 5, 0, 5, 1,
//                1, 5, 6, 1, 6, 2,
//                2, 6, 7, 2, 7, 3,
//                3, 7, 4, 3, 4, 0,
//                4, 7, 6, 4, 6, 5,
//                3, 0, 1, 3, 1, 2
//        };
//
//        public Cube() {
//            ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
//            byteBuf.order(ByteOrder.nativeOrder());
//            mVertexBuffer = byteBuf.asFloatBuffer();
//            mVertexBuffer.put(vertices);
//            mVertexBuffer.position(0);
//
//            byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
//            byteBuf.order(ByteOrder.nativeOrder());
//            mColorBuffer = byteBuf.asFloatBuffer();
//            mColorBuffer.put(colors);
//            mColorBuffer.position(0);
//
//            mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
//            mIndexBuffer.put(indices);
//            mIndexBuffer.position(0);
//        }
//
//        public void draw(GL10 gl) {
//            gl.glFrontFace(GL10.GL_CW);
//
//            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
//            gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
//
//            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//
//            gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE,
//                              mIndexBuffer);
//
//            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
//        }

}
