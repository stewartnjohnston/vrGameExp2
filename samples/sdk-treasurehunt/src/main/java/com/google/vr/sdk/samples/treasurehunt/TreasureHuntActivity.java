/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.vr.sdk.samples.treasurehunt;


import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
//-------------------------------------------------------------------------
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.InputDevice;
//-------------------------------------------------------------------------

import com.google.vr.sdk.audio.GvrAudioEngine;
import com.google.vr.sdk.base.AndroidCompat;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import net.pogo.game.engine.DisplayComponent;
import net.pogo.game.engine.Engine;
import net.pogo.game.engine.Entity;
import net.pogo.game.engine.PositionComponent;
import net.pogo.game.engine.VelocityComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.microedition.khronos.egl.EGLConfig;

/**
 * A Google VR sample application.
 *
 * <p>This app presents a scene consisting of a room and a floating object. When the user finds the
 * object, they can invoke the trigger action, and a new object will be randomly spawned. When in
 * Cardboard mode, the user must gaze at the object and use the Cardboard trigger button. When in
 * Daydream mode, the user can use the controller to position the cursor, and use the controller
 * buttons to invoke the trigger action.
 */
public class TreasureHuntActivity extends GvrActivity implements GvrView.StereoRenderer {

  //-------------------------------------------------------------------------
  static Engine engine;
  float EulerXvelocity = 0;
  float EulerYvelocity = 0;
  float EulerX = 0;
  float EulerY = 0;
  private static float velocitySpaceship = 0.0f;
  private static float xPosition = 1.0f;
  private static float yPosition = 0.0f;
  private static float zPosition = 1.0f;

  float headEuler[] = new float[3];
  float[] cameraTranslat = new float[16];
  float[] cameraRotate = new float[16];
  float[] copym = new float[16];
  float[] rotateTmp = new float[16];

  private  static float[] forwardVector = new float[3];
  private static  float[] headViewx = new float[16];
  private static  float[][] actors = new float[9][3];

  private static final float inputVelocityVector[] = new float[] {0.0f,0.0f,-1.0f,0.0f};
  private static float outputVelocityVector[] = new float[4];

  private static final float CAMERA_Z = 0.01f;

  private static float velocityX = 0.0f;
  private static float velocityY = 0.0f;
  private static float velocityZ = 0.0f;

  // We keep the light always position just above the user.
  private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] {0.0f, 2.0f, 0.0f, 1.0f};
  private static final float[] LIGHT_POS_IN_WORLD_SPACE_ORIGINAL = new float[] {0.0f, 2.0f, 0.0f, 1.0f};
  //-------------------------------------------------------------------------



  private static final String TAG = "HelloVrActivity";

  private static final int TARGET_MESH_COUNT = 3;

  private static final float Z_NEAR = 0.01f;
  private static final float Z_FAR = 10.0f;

  // Convenience vector for extracting the position from a matrix via multiplication.
  private static final float[] POS_MATRIX_MULTIPLY_VEC = {0.0f, 0.0f, 0.0f, 1.0f};
  private static final float[] FORWARD_VEC = {0.0f, 0.0f, -1.0f, 1.f};

  private static final float MIN_TARGET_DISTANCE = 3.0f;
  private static final float MAX_TARGET_DISTANCE = 3.5f;

  private static final String OBJECT_SOUND_FILE = "audio/HelloVR_Loop.ogg";
  private static final String SUCCESS_SOUND_FILE = "audio/HelloVR_Activation.ogg";

  private static final float FLOOR_HEIGHT = -2.0f;

  private static final float ANGLE_LIMIT = 0.2f;

  // The maximum yaw and pitch of the target object, in degrees. After hiding the target, its
  // yaw will be within [-MAX_YAW, MAX_YAW] and pitch will be within [-MAX_PITCH, MAX_PITCH].
  private static final float MAX_YAW = 100.0f;
  private static final float MAX_PITCH = 25.0f;

  private static final String[] OBJECT_VERTEX_SHADER_CODE =
      new String[] {
        "uniform mat4 u_MVP;",
        "attribute vec4 a_Position;",
        "attribute vec2 a_UV;",
        "varying vec2 v_UV;",
        "",
        "void main() {",
        "  v_UV = a_UV;",
        "  gl_Position = u_MVP * a_Position;",
        "}",
      };
  private static final String[] OBJECT_FRAGMENT_SHADER_CODE =
      new String[] {
        "precision mediump float;",
        "varying vec2 v_UV;",
        "uniform sampler2D u_Texture;",
        "",
        "void main() {",
        "  // The y coordinate of this sample's textures is reversed compared to",
        "  // what OpenGL expects, so we invert the y coordinate.",
        "  gl_FragColor = texture2D(u_Texture, vec2(v_UV.x, 1.0 - v_UV.y));",
        "}",
      };

  private int objectProgram;

  private int objectPositionParam;
  private int objectUvParam;
  private int objectModelViewProjectionParam;

  private float targetDistance = MAX_TARGET_DISTANCE;

  private TexturedMesh room;
  private Texture roomTex;
  private ArrayList<TexturedMesh> targetObjectMeshes;
  private ArrayList<Texture> targetObjectNotSelectedTextures;
  private ArrayList<Texture> targetObjectSelectedTextures;
  private ArrayList<Texture> sterioTextures;
  private int curTargetObject;

  private Random random;

  private float[] targetPosition;
  private float[] camera;
  private float[] view;
  private float[] headView;
  private float[] modelViewProjection;
  private float[] modelView;

  private float[] modelTarget;
  private float[] modelRoom;

  private float[] tempPosition;
  private float[] headRotation;

  private GvrAudioEngine gvrAudioEngine;
  private volatile int sourceId = GvrAudioEngine.INVALID_ID;
  private volatile int successSourceId = GvrAudioEngine.INVALID_ID;

    public void createSpaceship(int _objectPositionParam, int _objectUvParam, float x, float y, float z,float vx, float vy, float vz) throws IOException
    {
        Entity spaceship = new Entity();

        PositionComponent position = new PositionComponent();
        position.position.SetUnity();

        position.position.Translate(0.0f, 0.0f, targetDistance);
        position.position.Translate(x,y,z);

        spaceship.add( position );

        DisplayComponent displayComponent = new DisplayComponent();

        displayComponent.textureMesh = new String("cube.obj");
        if(!engine.GetTexturedMeshContainer().containsKey("cube.obj"))
        {
            TexturedMesh texturedMesh =  new TexturedMesh(this, "cube.obj", _objectPositionParam, _objectUvParam);
            displayComponent.textureMeshObject = texturedMesh;
            engine.GetTexturedMeshContainer().loadWaveFrontObject("cube.obj", texturedMesh);
        }
        else
        {
            displayComponent.textureMeshObject = engine.GetTexturedMeshContainer().getTexturedMesh("cube.obj");
        }

        if(!engine.GetTextureContainer().containsKey("bumpy_bricks_public_domain_l.png")) {
          Texture texture = new Texture(this, "bumpy_bricks_public_domain_l.png");
          displayComponent.textureObject = texture;
          engine.GetTextureContainer().loadTexture("bumpy_bricks_public_domain_l.png", texture);
        }
        else
        {
          displayComponent.textureObject = engine.GetTextureContainer().getTexture("bumpy_bricks_public_domain_l.png");
        }

        if(!engine.GetTextureContainer().containsKey("bumpy_bricks_public_domain_r.png")) {
          Texture texture = new Texture(this, "bumpy_bricks_public_domain_r.png");
          engine.GetTextureContainer().loadTexture("bumpy_bricks_public_domain_r.png", texture);
        }
        else
        {
          displayComponent.textureObject = engine.GetTextureContainer().getTexture("bumpy_bricks_public_domain_r.png");
        }

        displayComponent.texture = new String("bumpy_bricks_public_domain_r.png");

        spaceship.add( displayComponent );



        VelocityComponent velocity = new VelocityComponent();
        velocity.velocity.data[0]=vx;
        velocity.velocity.data[1]=vy;
        velocity.velocity.data[2]=vz;
        spaceship.add( velocity );

        engine.addEntity( spaceship );
    }
  /**
   * Sets the view to our GvrView and initializes the transformation matrices we will use
   * to render our scene.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initializeGvrView();
      engine = new Engine();


    camera = new float[16];
    view = new float[16];
    modelViewProjection = new float[16];
    modelView = new float[16];
    // Target object first appears directly in front of user.
    targetPosition = new float[] {0.0f, 0.0f, -MIN_TARGET_DISTANCE};
    tempPosition = new float[4];
    headRotation = new float[4];
    modelTarget = new float[16];
    modelRoom = new float[16];
    headView = new float[16];

    // Initialize 3D audio engine.
    gvrAudioEngine = new GvrAudioEngine(this, GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);

    random = new Random();
  }

  public void initializeGvrView() {
    setContentView(R.layout.common_ui);

    GvrView gvrView = (GvrView) findViewById(R.id.gvr_view);
    gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

    gvrView.setRenderer(this);
    gvrView.setTransitionViewEnabled(true);

    // Enable Cardboard-trigger feedback with Daydream headsets. This is a simple way of supporting
    // Daydream controller input for basic interactions using the existing Cardboard trigger API.
    gvrView.enableCardboardTriggerEmulation();

    if (gvrView.setAsyncReprojectionEnabled(true)) {
      // Async reprojection decouples the app framerate from the display framerate,
      // allowing immersive interaction even at the throttled clockrates set by
      // sustained performance mode.
      AndroidCompat.setSustainedPerformanceMode(this, true);
    }

    setGvrView(gvrView);
  }

  @Override
  public void onPause() {
    gvrAudioEngine.pause();
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    gvrAudioEngine.resume();
  }

  @Override
  public void onRendererShutdown() {
    Log.i(TAG, "onRendererShutdown");
  }

  @Override
  public void onSurfaceChanged(int width, int height) {
    Log.i(TAG, "onSurfaceChanged");
  }

  /**
   * Creates the buffers we use to store information about the 3D world.
   *
   * <p>OpenGL doesn't use Java arrays, but rather needs data in a format it can understand.
   * Hence we use ByteBuffers.
   *
   * @param config The EGL configuration used when creating the surface.
   */
  @Override
  public void onSurfaceCreated(EGLConfig config) {
    Log.i(TAG, "onSurfaceCreated");
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    objectProgram = Util.compileProgram(OBJECT_VERTEX_SHADER_CODE, OBJECT_FRAGMENT_SHADER_CODE);

    objectPositionParam = GLES20.glGetAttribLocation(objectProgram, "a_Position");
    objectUvParam = GLES20.glGetAttribLocation(objectProgram, "a_UV");
    objectModelViewProjectionParam = GLES20.glGetUniformLocation(objectProgram, "u_MVP");

    Util.checkGlError("Object program params");


    AndroidVRGraphicsDispay display = new AndroidVRGraphicsDispay();
    display.objectModelViewProjectionParam = objectModelViewProjectionParam;
    display.objectProgram = objectProgram;
    engine.GetrenderSystem().display = display;

    Matrix.setIdentityM(modelRoom, 0);
    Matrix.translateM(modelRoom, 0, 0, FLOOR_HEIGHT, 0);

    // Avoid any delays during start-up due to decoding of sound files.
    new Thread(
            new Runnable() {
              @Override
              public void run() {
                // Start spatial audio playback of OBJECT_SOUND_FILE at the model position. The
                // returned sourceId handle is stored and allows for repositioning the sound object
                // whenever the target position changes.
                gvrAudioEngine.preloadSoundFile(OBJECT_SOUND_FILE);
                sourceId = gvrAudioEngine.createSoundObject(OBJECT_SOUND_FILE);
                gvrAudioEngine.setSoundObjectPosition(
                    sourceId, targetPosition[0], targetPosition[1], targetPosition[2]);
                gvrAudioEngine.playSound(sourceId, true /* looped playback */);
                // Preload an unspatialized sound to be played on a successful trigger on the
                // target.
                gvrAudioEngine.preloadSoundFile(SUCCESS_SOUND_FILE);
              }
            })
        .start();

    updateTargetPosition();

    Util.checkGlError("onSurfaceCreated");

    try {

      createSpaceship(objectPositionParam, objectUvParam, 0, 0, targetDistance, 0, 0, 0);
      createSpaceship(objectPositionParam, objectUvParam, 0, 0, 2.5f, 0, 0, 0);
      createSpaceship(objectPositionParam, objectUvParam, 2.0f, 2.0f, 2.5f, 0, 0, 0.01f);
      createSpaceship(objectPositionParam, objectUvParam, 1.0f, 1.0f, 2.5f, 0, 0.01f, 0);
      createSpaceship(objectPositionParam, objectUvParam, 3.0f, 0.0f, 2.5f, 0.01f, 0, 0);


      room = new TexturedMesh(this, "CubeRoom.obj", objectPositionParam, objectUvParam);
      roomTex = new Texture(this, "CubeRoom_BakedDiffuse.png");
      targetObjectMeshes = new ArrayList<>();
      targetObjectNotSelectedTextures = new ArrayList<>();
      targetObjectSelectedTextures = new ArrayList<>();
      sterioTextures = new ArrayList<>();

      sterioTextures.add(new Texture(this, "bumpy_bricks_public_domain_l.png"));
      sterioTextures.add(new Texture(this, "bumpy_bricks_public_domain_r.png"));


      targetObjectMeshes.add(new TexturedMesh(this, "cube.obj", objectPositionParam, objectUvParam));
      targetObjectMeshes.add(new TexturedMesh(this, "cube.obj", objectPositionParam, objectUvParam));
      targetObjectMeshes.add(new TexturedMesh(this, "cube.obj", objectPositionParam, objectUvParam));



      targetObjectMeshes.add(new TexturedMesh(this, "Icosahedron.obj", objectPositionParam, objectUvParam));
      //targetObjectMeshes.add(new TexturedMesh(this, "BaseMeshsculpt2.obj", objectPositionParam, objectUvParam));

      targetObjectNotSelectedTextures.add(new Texture(this, "Icosahedron_Blue_BakedDiffuse.png"));
      targetObjectSelectedTextures.add(new Texture(this, "Icosahedron_Pink_BakedDiffuse.png"));

      targetObjectMeshes.add(new TexturedMesh(this, "QuadSphere.obj", objectPositionParam, objectUvParam));
      //targetObjectMeshes.add(new TexturedMesh(this, "BaseMeshsculpt2.obj", objectPositionParam, objectUvParam));

      targetObjectNotSelectedTextures.add(new Texture(this, "QuadSphere_Blue_BakedDiffuse.png"));
      targetObjectSelectedTextures.add(new Texture(this, "QuadSphere_Pink_BakedDiffuse.png"));

      //targetObjectMeshes.add(new TexturedMesh(this, "TriSphere.obj", objectPositionParam, objectUvParam));
      targetObjectMeshes.add(new TexturedMesh(this, "BaseMeshsculpt2.obj", objectPositionParam, objectUvParam));

      targetObjectNotSelectedTextures.add(new Texture(this, "TriSphere_Blue_BakedDiffuse.png"));
      targetObjectSelectedTextures.add(new Texture(this, "TriSphere_Pink_BakedDiffuse.png"));
    } catch (IOException e) {
      Log.e(TAG, "Unable to initialize objects", e);
    }
    curTargetObject = random.nextInt(TARGET_MESH_COUNT);
  }

  /** Updates the target object position. */
  private void updateTargetPosition() {
    Matrix.setIdentityM(modelTarget, 0);
    Matrix.translateM(modelTarget, 0, targetPosition[0], targetPosition[1], targetPosition[2]);

    // Update the sound location to match it with the new target position.
    if (sourceId != GvrAudioEngine.INVALID_ID) {
      gvrAudioEngine.setSoundObjectPosition(
          sourceId, targetPosition[0], targetPosition[1], targetPosition[2]);
    }
    Util.checkGlError("updateTargetPosition");
  }

  //-----------------------------------------------------------------------------------------------

  @Override
  public boolean dispatchGenericMotionEvent(MotionEvent event) {


    // Check that the event came from a joystick or gamepad since a generic
    // motion event could be almost anything. API level 18 adds the useful
    // event.isFromSource() helper function.
    boolean rt = false;
    int eventSource = event.getSource();
    if (  ((eventSource & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
            ((eventSource & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) )
    {
      if (event.getAction() == android.view.MotionEvent.ACTION_MOVE  )
      {
        int id = event.getDeviceId();
        if (-1 != id) {

          float X = event.getX();

          float Y = event.getY();

          if (X == 0 && Y == 1) {
            // yaw right
            EulerYvelocity = EulerYvelocity +1;
            Log.i(TAG, "dispatchGenericMotionEvent()yaw right");
            rt = true;

          } else if (X == 0 && Y == -1) {
            // yaw left
            EulerYvelocity = EulerYvelocity -1;
            Log.i(TAG, "dispatchGenericMotionEvent()yaw left");

            rt = true;
          } else if (X == 1 && Y == 0) {
            // pitch up
            EulerXvelocity = EulerXvelocity -1;
            Log.i(TAG, "dispatchGenericMotionEvent()pitch up");

            rt = true;
          } else if (X == -1 && Y == 0) {
            // pitch down
            EulerXvelocity = EulerXvelocity +1;
            Log.i(TAG, "dispatchGenericMotionEvent()pitch down");

            rt = true;
          } else if (X == -1 && Y == -1) {
            // yaw and pich -1
            EulerXvelocity = EulerXvelocity +1;
            EulerYvelocity = EulerYvelocity +1;
            Log.i(TAG, "dispatchGenericMotionEvent()pitch down yaw left");

            rt = true;
          } else if (X == 1 && Y == 1) {
            // yaw and pitch +1
            EulerXvelocity = EulerXvelocity -1;
            EulerYvelocity = EulerYvelocity -1;
            Log.i(TAG, "dispatchGenericMotionEvent()pitch up yaw right");


            rt = true;
          } else {
            rt = true;
            EulerXvelocity = 0;
            EulerYvelocity = 0;

          }
        }

      }
    }
    else
    {
      //overlayView.show3DToast("dispatchGenericMotionEvent()\n2\nsuper.onGenericMotionEvent(event) ");
      rt = super.onGenericMotionEvent(event);
    }

    return rt;
  }

  @Override
  public boolean dispatchKeyEvent (KeyEvent event) {
    boolean rt = false;


//              Joystick buttons
//
//           u           D
//                       |
//        j          B---+---A
//           @           |
//                       C


    if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
    {
      if (event.getRepeatCount() == 0)
      {

        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_B) {
          // *****  Buttot "A" on joystick
          //walk = walk - 5.0f;
          Log.i(TAG, "dispatchKeyEvent()  Create Wall");
          rt = true;
        } else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_X) {
          // *****  Buttot "B" on joystick
//          Log.i(TAG, "dispatchKeyEvent()B-B Nothing");
          //R0 = R0 +5;
          //updateModelPosition(walk, R0, R1, R2);
          //Log.i(TAG, "dispatchKeyEvent()R0");
          rt = true;
        } else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_Y) {
          // *****  Button "D" on joystick
          velocitySpaceship++;
          Log.i(TAG, "dispatchKeyEvent()  B-D Faster");
          //R1 = R1 + 5;
          //updateModelPosition(walk, R0, R1, R2);
          //Log.i(TAG, "dispatchKeyEvent()R1");
          rt = true;
        } else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_A) {
          // *****  Button "C" on joystick
          velocitySpaceship = 0;
          Log.i(TAG, "dispatchKeyEvent()  B-C Stop");
          //R2 = R2 + 5;
          //updateModelPosition(walk, R0, R1, R2);
          //Log.i(TAG, "dispatchKeyEvent()R2");

          rt = true;
        } else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_R1) {
          // *****  Top Button on joystick
          Log.i(TAG, "dispatchKeyEvent()  B-Top Nothing");
          rt = true;
        } else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_L1) {
          // *****  Bottom Button on joystick
          // Go back to the starting position and set velocity to zero
          xPosition = 1.0f;
          yPosition = 0.0f;
          zPosition = 1.0f;
          velocitySpaceship = 0;
          //walk = R0 = R1= R2 = 0.0f;
          EulerXvelocity = EulerYvelocity = EulerY = EulerX = 0;

          Log.i(TAG, "dispatchKeyEvent()  B-bottom Reset");
          rt = true;
        } else {
          rt = true;
        }
      }
      else
      {
        rt = true;
      }
    }
    else
    {
      rt = super.dispatchKeyEvent(event);

    }
    return rt;
  }
//-------------------------------------------------------------------------------------------------


  /**
   * Prepares OpenGL ES before we draw a frame.
   *
   * @param headTransform The head transformation in the new frame.
   */
  @Override
  public void onNewFrame(HeadTransform headTransform) {
    // Build the camera matrix and apply it to the ModelView.
    Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f);

    engine.GetSystemManager().update(1);

    Matrix.setIdentityM(headView, 0);
    headTransform.getHeadView(headView, 0);

    //-------------------------------------------------------------------------------------------------

    Matrix.setIdentityM(copym, 0);
    Matrix.setRotateEulerM(copym, 0, 0, 0, 90);


    EulerY = EulerY + EulerYvelocity;
    EulerX = EulerX + EulerXvelocity;

    headTransform.getForwardVector(forwardVector, 0);
    headTransform.getEulerAngles(headEuler,0);

    forwardVector[1] = -headView[6];
    forwardVector[0] =  headView[2];

    velocityX = forwardVector[0];
    velocityY = -forwardVector[1];
    velocityZ = -forwardVector[2];


    Matrix.setIdentityM(rotateTmp, 0);
    Matrix.rotateM(rotateTmp,0,-EulerY, 0.0f, 1.0f, 0.0f);
    Matrix.rotateM(rotateTmp,0,-EulerX, 1.0f, 0.0f, 0.0f);
    Matrix.multiplyMV(outputVelocityVector, 0, rotateTmp, 0, inputVelocityVector, 0);

    if (velocitySpaceship > 0)
    {

      //xPosition = xPosition + velocitySpaceship * (velocityX/100.0f);
      //yPosition = yPosition + velocitySpaceship * (velocityY/100.0f);
      //zPosition = zPosition + velocitySpaceship * (velocityZ/100.0f);

      xPosition = xPosition - (outputVelocityVector[0] * velocitySpaceship /100.0f);
      yPosition = yPosition - (outputVelocityVector[1] * velocitySpaceship /100.0f);
      zPosition = zPosition - (outputVelocityVector[2] * velocitySpaceship /100.0f);
    }

    LIGHT_POS_IN_WORLD_SPACE[0] = xPosition + LIGHT_POS_IN_WORLD_SPACE_ORIGINAL[0];
    LIGHT_POS_IN_WORLD_SPACE[1] = yPosition + LIGHT_POS_IN_WORLD_SPACE_ORIGINAL[1];
    LIGHT_POS_IN_WORLD_SPACE[2] = yPosition + LIGHT_POS_IN_WORLD_SPACE_ORIGINAL[2];


    //-------------------------------------------------------------------------------------------------


    // Update the 3d audio engine with the most recent head rotation.
    headTransform.getQuaternion(headRotation, 0);
    gvrAudioEngine.setHeadRotation(
        headRotation[0], headRotation[1], headRotation[2], headRotation[3]);
    // Regular update call to GVR audio engine.
    gvrAudioEngine.update();

    Util.checkGlError("onNewFrame");
  }

  /**
   * Draws a frame for an eye.
   *
   * @param eye The eye to render. Includes all required transformations.
   */
  @Override
  public void onDrawEye(Eye eye) {
    GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    // The clear color doesn't matter here because it's completely obscured by
    // the room. However, the color buffer is still cleared because it may
    // improve performance.
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    //-------------------------------------------------------------------------------------------------
    Matrix.setIdentityM(cameraTranslat, 0);
    Matrix.translateM(cameraTranslat, 0, xPosition, yPosition, zPosition);

    Matrix.setIdentityM(copym, 0);
    Matrix.setIdentityM(rotateTmp, 0);


    Matrix.rotateM(copym, 0, eye.getEyeView(), 0, EulerX, 1, 0, 0);
    Matrix.rotateM(rotateTmp, 0, copym, 0, EulerY, 0, 1, 0);

    Matrix.setIdentityM(copym, 0);


    // Apply the eye transformation to the camera.
    Matrix.multiplyMM(copym, 0, cameraTranslat, 0, camera, 0);
    //Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, copym, 0);
    Matrix.multiplyMM(view, 0, rotateTmp, 0, copym, 0);

    Matrix.setIdentityM(copym, 0);
    //Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);


    //-------------------------------------------------------------------------------------------------


    // Apply the eye transformation to the camera.
    //Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

    // Build the ModelView and ModelViewProjection matrices
    // for calculating the position of the target object.
    float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

    engine.GetrenderSystem().view = view;
    engine.GetrenderSystem().perspective = perspective;

    engine.GetrenderSystem().draw();

    Matrix.multiplyMM(modelView, 0, view, 0, modelTarget, 0);
    Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
    drawTarget(eye.getType() == Eye.Type.LEFT);

/*
    // Set modelView for the room, so it's drawn in the correct location
    Matrix.multiplyMM(modelView, 0, view, 0, modelRoom, 0);
    Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
    drawRoom();
    */
  }

  @Override
  public void onFinishFrame(Viewport viewport) {}

  /** Draw the target object. */
  public void drawTarget(boolean leftEye) {
    GLES20.glUseProgram(objectProgram);
    GLES20.glUniformMatrix4fv(objectModelViewProjectionParam, 1, false, modelViewProjection, 0);
/*
    if (isLookingAtTarget()) {
      targetObjectSelectedTextures.get(curTargetObject).bind();
    } else {
      targetObjectNotSelectedTextures.get(curTargetObject).bind();
    }
  */

    if (leftEye) {
      sterioTextures.get(0).bind();
    } else {
      sterioTextures.get(1).bind();
    }

    targetObjectMeshes.get(curTargetObject).draw();
    Util.checkGlError("drawTarget");
  }

  /** Draw the room. */
  public void drawRoom() {
    GLES20.glUseProgram(objectProgram);
    GLES20.glUniformMatrix4fv(objectModelViewProjectionParam, 1, false, modelViewProjection, 0);
    roomTex.bind();
    room.draw();
    Util.checkGlError("drawRoom");
  }

  /**
   * Called when the Cardboard trigger is pulled.
   */
  @Override
  public void onCardboardTrigger() {
    Log.i(TAG, "onCardboardTrigger");

    if (isLookingAtTarget()) {
      successSourceId = gvrAudioEngine.createStereoSound(SUCCESS_SOUND_FILE);
      gvrAudioEngine.playSound(successSourceId, false /* looping disabled */);
      hideTarget();
    }
  }

  /** Find a new random position for the target object. */
  private void hideTarget() {
    float[] rotationMatrix = new float[16];
    float[] posVec = new float[4];

    // Matrix.setRotateM takes the angle in degrees, but Math.tan takes the angle in radians, so
    // yaw is in degrees and pitch is in radians.
    float yawDegrees = (random.nextFloat() - 0.5f) * 2.0f * MAX_YAW;
    float pitchRadians = (float) Math.toRadians((random.nextFloat() - 0.5f) * 2.0f * MAX_PITCH);

    Matrix.setRotateM(rotationMatrix, 0, yawDegrees, 0.0f, 1.0f, 0.0f);
    targetDistance =
        random.nextFloat() * (MAX_TARGET_DISTANCE - MIN_TARGET_DISTANCE) + MIN_TARGET_DISTANCE;
    targetPosition = new float[] {0.0f, 0.0f, -targetDistance};
    Matrix.setIdentityM(modelTarget, 0);
    Matrix.translateM(modelTarget, 0, targetPosition[0], targetPosition[1], targetPosition[2]);
    Matrix.multiplyMV(posVec, 0, rotationMatrix, 0, modelTarget, 12);

    targetPosition[0] = posVec[0];
    targetPosition[1] = (float) Math.tan(pitchRadians) * targetDistance;
    targetPosition[2] = posVec[2];

    updateTargetPosition();
    curTargetObject = random.nextInt(TARGET_MESH_COUNT);
  }

  /**
   * Check if user is looking at the target object by calculating where the object is in eye-space.
   *
   * @return true if the user is looking at the target object.
   */
  private boolean isLookingAtTarget() {
    // Convert object space to camera space. Use the headView from onNewFrame.
    Matrix.multiplyMM(modelView, 0, headView, 0, modelTarget, 0);
    Matrix.multiplyMV(tempPosition, 0, modelView, 0, POS_MATRIX_MULTIPLY_VEC, 0);

    float angle = Util.angleBetweenVectors(tempPosition, FORWARD_VEC);
    return angle < ANGLE_LIMIT;
  }
}
