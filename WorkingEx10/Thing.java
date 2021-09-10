/*
  a thing is a triangle mesh
  that can be translated, rotated,
  and scaled
*/

import java.util.Scanner;
import java.nio.FloatBuffer;
import java.io.File;

public class Thing{

  private TriMesh mesh;
  private Mat4 translate, rotate, scale;
  private Mat4 transform;  // derived data = T R S
  private double currentAngle;
  private Triple rotationAxis = new Triple(0,0,1); // Default, extensions would need to change in their code.
  private boolean isRotatable = false; // Default
  private double rotationRate = 0.0; // Default
  
  public Thing( Scanner input ){

    String kind = input.next();   input.nextLine();
	System.out.println("kind = " + kind );

    Triple s = new Triple( input );
    scale = Mat4.scale( s.x, s.y, s.z );

    double angle = input.nextDouble();
    currentAngle = angle;
	Triple axis = new Triple( input );
    rotate = Mat4.rotate( angle, axis.x, axis.y, axis.z );
	
    Triple shift = new Triple( input );
    translate = Mat4.translate( shift.x, shift.y, shift.z );
    
    // update derived data "transform" whenver translate, rotate, scale change
    updateTransform();

	// Get the mesh data for this thing
    if( kind.equals( "cyl" ) ){
		isRotatable = true;
		int numSides = input.nextInt();
		double radius = input.nextDouble();
		double height = input.nextDouble();
		rotationRate = input.nextDouble(); input.nextLine();
		String fileName = input.next(); input.nextLine();
		System.out.println("Getting colors from: " + fileName + ".txt");
		try{
			Scanner colorFile = new Scanner( new File(fileName + ".txt"));
			mesh = new TriMesh("cyl", numSides, radius, height, colorFile);
			System.out.print("mesh is " + mesh );
		}
		catch( Exception e ){
			System.out.println("There is no file named [" + fileName + "] You need a file with colors for a cylinder." );
			e.printStackTrace();
			System.exit(1);
		}
    }
	else if (kind.equals("wall")) {
		int nw = input.nextInt();
		int nh = input.nextInt();
		input.nextLine();
		Triple color1 = new Triple(input);
		Triple color2 = new Triple(input);
		//Triple color1 = new Triple(input.nextDouble(), input.nextDouble(), input.nextDouble());
		//input.nextLine();
		//Triple color2 = new Triple(input.nextDouble(), input.nextDouble(), input.nextInt());
		//input.nextLine();
		mesh = new TriMesh("wall", nw, nh, color1, color2);
	}
    else if( kind.equals( "arb" ) ){
      mesh = new TriMesh( input );
      System.out.print("mesh is " + mesh );
    }

    else if( kind.equals( "file" ) ){
      String fileName = input.next();
      try{
        Scanner meshInput = new Scanner( new File( fileName + ".txt") );
        mesh = new TriMesh( meshInput );
        meshInput.close();
      }
      catch( Exception e ){
        System.out.println("There is no file named [" + fileName + "]" );
        e.printStackTrace();
        System.exit(1);
      }
    }

  }// constructor

  private void updateTransform(){
    transform = translate.mult( rotate.mult( scale ) );
  }
  
  public void updateRotation() {
	if (isRotatable) {
		currentAngle = currentAngle + rotationRate;
		if (currentAngle > 360) {
			currentAngle = currentAngle - 360;
		} 
		else if (currentAngle < 0) {
			currentAngle = currentAngle + 360;
		}
		rotate = Mat4.rotate( currentAngle, rotationAxis.x, rotationAxis.y, rotationAxis.z );  
		updateTransform();
	}
  }

  public int getNumTris(){
    return mesh.getNumTris();    
  }
  
  public void copyData( FloatBuffer posBuffer, FloatBuffer colorBuffer ){
    mesh.copyTransformedData( transform,
                              posBuffer, colorBuffer );    
  }

  public String toString(){
    return transform + " " + mesh;
  }

}
