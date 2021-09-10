/*  
   read info for a collection of things
   from a data file and
   draw the scene, with interactively
   controlled camera
*/

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class SceneViewer extends Basic
{
  private final static int HUGE = 1000000;  

  public static void main(String[] args)
  {
    if( args.length == 1 ){
      SceneViewer app = new SceneViewer( "A Scene", 500, 500, 30, args[0] );
      app.start();
    }
    else{
      System.out.println("Usage:  j SceneViewer <data file>");
    }
  }// main

  // instance variables 

  private ArrayList<Thing> things;

  private Camera camera;

  private FloatBuffer positionBuffer, colorBuffer;
  private int positionHandle, colorHandle;

  private FloatBuffer backColor;
  private Shader v1, f1;
  private int hp1;
  private Program p1;

  private int vao;  // handle to the vertex array object
  
  private int lookAtLoc, projectLoc;
  private Mat4 lookAt, project;

  // construct basic application with given title, pixel width and height
  // of drawing area, and frames per second
  public SceneViewer( String appTitle, int pw, int ph, int fps, String fileName )
  {
    super( appTitle, pw, ph, (long) ((1.0/fps)*1000000000) );

    // read triangles data from data file
    try{
      things = new ArrayList<Thing>();
      Scanner input = new Scanner( new File( fileName ) );
      int num = input.nextInt();  input.nextLine();
      for( int k=1; k<=num; k++ ){
        Thing t = new Thing( input );
System.out.println("read thing " + k + ": " + t );
        things.add( t );
      }
    }
    catch(Exception e){
      System.out.println("error reading data file");
      e.printStackTrace();
      System.exit(1);
    }

    camera = new Camera( new Triple(50,-20,10), 90, 0, 
                         -1, 1, -1, 1, 4, 1000 );
    System.out.println("lookAt: " + camera.getLookAt() );
    System.out.println("project: " + camera.getProject() );
  }

  protected void init()
  {
    String vertexShaderCode =
"#version 330 core\n"+
"layout (location = 0 ) in vec3 vertexPosition;\n"+
"layout (location = 1 ) in vec3 vertexColor;\n"+
"uniform mat4 lookAt;\n"+
"uniform mat4 project;\n"+
"out vec3 color;\n"+
"void main(void)\n"+
"{\n"+
"  color = vertexColor;\n"+
"  vec4 point = vec4( vertexPosition, 1.0 );\n"+
"  gl_Position = project * lookAt * point;\n"+
"  // relying on fixed functionality pipeline to do perspective division\n"+
"}\n";

    System.out.println("Vertex shader:\n" + vertexShaderCode + "\n\n" );

    v1 = new Shader( "vertex", vertexShaderCode );

    String fragmentShaderCode =
"#version 330 core\n"+
"in vec3 color;\n"+
"layout (location = 0 ) out vec4 fragColor;\n"+
"void main(void)\n"+
"{\n"+
"  fragColor = vec4(color, 1.0 );\n"+
"}\n";

    System.out.println("Fragment shader:\n" + fragmentShaderCode + "\n\n" );

    f1 = new Shader( "fragment", fragmentShaderCode );

    hp1 = GL20.glCreateProgram();
         Util.error("after create program");
         System.out.println("program handle is " + hp1 );

    GL20.glAttachShader( hp1, v1.getHandle() );
         Util.error("after attach vertex shader to program");

    GL20.glAttachShader( hp1, f1.getHandle() );
         Util.error("after attach fragment shader to program");

    GL20.glLinkProgram( hp1 );
         Util.error("after link program" );

    GL20.glUseProgram( hp1 );
         Util.error("after use program");

    // locate uniform variables

    lookAtLoc = GL20.glGetUniformLocation( hp1, "lookAt" );
    System.out.println("uniform location for lookAt is " + lookAtLoc );
    projectLoc = GL20.glGetUniformLocation( hp1, "project" );
    System.out.println("uniform location for projectLoc is " + projectLoc );

    // set background color to white
    backColor = Util.makeBuffer4( 1.0f, 1.0f, 1.0f, 1.0f );

    // turn on depth buffering
    GL11.glEnable( GL11.GL_DEPTH_TEST );
    GL11.glClearDepth( 1.0f );               // had these two wrong on 10/17/16
    GL11.glDepthFunc( GL11.GL_LESS );

    // create vertex buffer objects and their handles
    positionHandle = GL15.glGenBuffers();
    colorHandle = GL15.glGenBuffers();
    System.out.println("have position handle " + positionHandle +
                       " and color handle " + colorHandle );

    positionBuffer = Util.createFloatBuffer( HUGE );
    colorBuffer = Util.createFloatBuffer( HUGE );

    // set up vertex array object

      // using convenience form that produces one vertex array handle
      vao = GL30.glGenVertexArrays();
           Util.error("after generate single vertex array");
      GL30.glBindVertexArray( vao );
           Util.error("after bind the vao");
      System.out.println("vao is " + vao );

      // enable the vertex array attributes
      GL20.glEnableVertexAttribArray(0);  // position
             Util.error("after enable attrib 0");
      GL20.glEnableVertexAttribArray(1);  // color
             Util.error("after enable attrib 1");
  
      // map index 0 to the position buffer index 1 to the color buffer
      GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, positionHandle );
             Util.error("after bind position buffer");
      GL20.glVertexAttribPointer( 0, 3, GL11.GL_FLOAT, false, 0, 0 );
             Util.error("after do position vertex attrib pointer");

      // map index 1 to the color buffer
      GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, colorHandle );
             Util.error("after bind color buffer");
      GL20.glVertexAttribPointer( 1, 3, GL11.GL_FLOAT, false, 0, 0 );
             Util.error("after do color vertex attrib pointer");

  }

  private final static double spaceAmount = 1;
  private final static double angleAmount = 3;

  protected void processInputs()
  {
    // process all waiting input events
    while( InputInfo.size() > 0 )
    {
      InputInfo info = InputInfo.get();

      if( info.kind == 'k' && (info.action == GLFW_PRESS || 
                               info.action == GLFW_REPEAT) )
      {
        int code = info.code;
        // System.out.println("code: " + code + " mods: " + info.mods );

        // interactively control the viewing situation

        if( code == GLFW_KEY_X && (info.mods & 1) == 0 ){// x for left
          camera.shift( -spaceAmount, 0, 0 );
        }
        else if( code == GLFW_KEY_X && (info.mods & 1) == 1 ){// X for right
          camera.shift( spaceAmount, 0, 0 );
        }
        else if( code == GLFW_KEY_Y && (info.mods & 1) == 0 ){// y
          camera.shift( 0, -spaceAmount, 0 );
        }
        else if( code == GLFW_KEY_Y && (info.mods & 1) == 1 ){// Y
          camera.shift( 0, spaceAmount, 0 );
        }
        else if( code == GLFW_KEY_Z && (info.mods & 1) == 0 ){// z
          camera.shift( 0, 0, -spaceAmount );
        }
        else if( code == GLFW_KEY_Z && (info.mods & 1) == 1 ){// Z
          camera.shift( 0, 0, spaceAmount );
        }
        else if( code == GLFW_KEY_R && (info.mods & 1) == 0 ){// r  rotate
          camera.turn( -angleAmount );
        }
        else if( code == GLFW_KEY_R && (info.mods & 1) == 1 ){// R
          camera.turn( angleAmount );
        }
        else if( code == GLFW_KEY_T && (info.mods & 1) == 0 ){// t tilt
          camera.tilt( -angleAmount );
        }
        else if( code == GLFW_KEY_T && (info.mods & 1) == 1 ){// T tilt
          camera.tilt( angleAmount );
        }
        else if( code == GLFW_KEY_F && (info.mods & 1) == 0 ){// f less far
          camera.zoom( 1/1.2 );
        }
        else if( code == GLFW_KEY_F && (info.mods & 1) == 1 ){// F more far
          camera.zoom( 1.2 );
        }

      }// input event is a key

      else if ( info.kind == 'm' )
      {// mouse moved
      //  System.out.println( info );
      }

      else if( info.kind == 'b' )
      {// button action
       //  System.out.println( info );
      }

    }// loop to process all input events

  }

  protected void update()
  {
  }

  protected void display()
  {
    // System.out.println("begin display on step " + getStepNumber() );

    GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
    GL30.glClearBufferfv( GL11.GL_COLOR, 0, backColor );

    // send uniform data for viewing situation:
    GL20.glUniformMatrix4fv( lookAtLoc, false, camera.getLookAt().toBuffer() );
    GL20.glUniformMatrix4fv( projectLoc, false, camera.getProject().toBuffer() );

    // send data for triangles to GPU:
    // ----------------------------------------------------------------

    // rewind before filling again
    positionBuffer.rewind();
    colorBuffer.rewind();

    // copy data from list of things to buffers
    int numTris = 0;
    for( int k=0; k<things.size(); k++ ){
      Thing t = things.get(k);
	  t.updateRotation(); // Ex10 specific.
      t.copyData( positionBuffer, colorBuffer );
      numTris += t.getNumTris();
    }

    //    Util.showBuffer( "position buffer: ", positionBuffer );
    //    Util.showBuffer( "color buffer: ", colorBuffer );
    
    // rewind again before sending to GPU
    positionBuffer.rewind();
    colorBuffer.rewind();
    
    // activate vao
    GL30.glBindVertexArray( vao );
           Util.error("after bind vao");

    // now connect the buffers
    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, positionHandle );
           Util.error("after bind positionHandle");
    GL15.glBufferData( GL15.GL_ARRAY_BUFFER,
                                   positionBuffer, GL15.GL_STATIC_DRAW );
           Util.error("after set position data");

    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, colorHandle );
           Util.error("after bind colorHandle");
    GL15.glBufferData( GL15.GL_ARRAY_BUFFER,
                                   colorBuffer, GL15.GL_STATIC_DRAW );
           Util.error("after set color data");
    // ----------------------------------------------------------------

    // draw the buffers
    GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, 3*numTris );
           Util.error("after draw arrays");
   
  }

}// SceneViewer
