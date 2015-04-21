package ucsb.ece251.matt.rollingdice_251_hw4;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MyGLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "MyGLRenderer";
    private Cube mCube;
    private Context mContext;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    // To control camera position and up vector, which decides what number is on top.
    private float cameraX = 3f, cameraY = 3f, cameraZ = -3f, upVectorX = 0f, upVectorY = 0f,
            upVectorZ = -1f;

    public MyGLRenderer(Context context) {
        mContext = context;
    }

    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;


//    public volatile float[] rotateAxis = {1f, -1f, -1f};


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mCube = new Cube(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_CULL_FACE); //!!!IMPORTANT!!!!!!!!!!
        GLES20.glCullFace(GLES20.GL_BACK);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, cameraX, cameraY, cameraZ, 0f, 0f, 0f, upVectorX,
                          upVectorY, upVectorZ);


        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Create a rotation for the triangle
//        long time = SystemClock.uptimeMillis() % 4000L;
//        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 1f, -1f, -1f);
//        Matrix.setRotateM(mRotationMatrix, 0, mAngle, rotateAxis[0], rotateAxis[1], rotateAxis[2]);

        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw triangle
        mCube.draw(scratch);
    }

    /**
     * Utility method for compiling a OpenGL shader.
     * <p/>
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     * <p/>
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }


    // Set the camera position and up vector, which determines which number appears on top
    public void setCameraRender(int i) {
        switch (i) {
            case 1:
                cameraX = 3f;
                cameraY = 3f;
                cameraZ = -3f;
                upVectorX = 0f;
                upVectorY = 0f;
                upVectorZ = -1f;
                mAngle = 0f;
                break;
            case 2:
                cameraX = 3f;
                cameraY = -3f;
                cameraZ = 3f;
                upVectorX = 1f;
                upVectorY = 0f;
                upVectorZ = 0f;
                mAngle = 0f;
                break;
            case 3:
                cameraX = -3f;
                cameraY = -3f;
                cameraZ = 3f;
                upVectorX = 0f;
                upVectorY = 0f;
                upVectorZ = 1f;
                mAngle = 0f;
                break;
            case 4:
                cameraX = -3f;
                cameraY = 3f;
                cameraZ = 3f;
                upVectorX = -1f;
                upVectorY = 0f;
                upVectorZ = 0f;
                mAngle = 0f;
                break;
            case 5:
                cameraX = 3f;
                cameraY = 3f;
                cameraZ = 3f;
                upVectorX = 0f;
                upVectorY = 1f;
                upVectorZ = 0f;
                mAngle = 0f;
                break;
            case 6:
                cameraX = 3f;
                cameraY = -3f;
                cameraZ = 3f;
                upVectorX = 0f;
                upVectorY = -1f;
                upVectorZ = 0f;
                mAngle = 0f;
                break;
        }
    }
}
